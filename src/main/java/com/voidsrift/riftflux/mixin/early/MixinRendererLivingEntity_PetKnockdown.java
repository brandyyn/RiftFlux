package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.pets.PetKnockdown;
import com.voidsrift.riftflux.pets.PetKnockdownCarry;
import com.voidsrift.riftflux.pets.PetKnockdownRotationAccess;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity_PetKnockdown {
    @Unique
    private static final int riftflux$NOT_KNOCKED_DOWN = Integer.MIN_VALUE;
    @Unique
    private static final Object riftflux$NO_MOUNT = new Object();

    @Shadow
    protected abstract float getDeathMaxRotation(EntityLivingBase entity);

    @Unique
    private final Deque<Integer> riftflux$petDeathTimes = new ArrayDeque<Integer>();
    @Unique
    private final Deque<Object> riftflux$petRenderMounts = new ArrayDeque<Object>();
    @Unique
    private final Deque<Boolean> riftflux$knockdownRenderStates = new ArrayDeque<Boolean>();

    @Inject(
            method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = @At("HEAD")
    )
    private void riftflux$startKnockdownTint(
            EntityLivingBase entity,
            double x,
            double y,
            double z,
            float yaw,
            float partialTicks,
            CallbackInfo ci
    ) {
        this.riftflux$knockdownRenderStates.push(Boolean.valueOf(PetKnockdown.isKnockedDown(entity)));
        Entity mount = entity.ridingEntity;
        this.riftflux$petRenderMounts.push(mount == null ? riftflux$NO_MOUNT : mount);
        if (PetKnockdown.isKnockedDown(entity)) {
            entity.ridingEntity = null;
        }

        if (!PetKnockdown.isKnockedDown(entity) || !ModConfig.showPetKnockdownRedTint) {
            this.riftflux$petDeathTimes.push(Integer.valueOf(riftflux$NOT_KNOCKED_DOWN));
            return;
        }
        this.riftflux$petDeathTimes.push(Integer.valueOf(entity.deathTime));
        entity.deathTime = 1;
    }

    @Inject(
            method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = @At("RETURN")
    )
    private void riftflux$finishKnockdownTint(
            EntityLivingBase entity,
            double x,
            double y,
            double z,
            float yaw,
            float partialTicks,
            CallbackInfo ci
    ) {
        if (!this.riftflux$knockdownRenderStates.isEmpty()) {
            this.riftflux$knockdownRenderStates.pop();
        }
        if (!this.riftflux$petRenderMounts.isEmpty()) {
            Object previousMount = this.riftflux$petRenderMounts.pop();
            entity.ridingEntity = previousMount == riftflux$NO_MOUNT ? null : (Entity) previousMount;
        }

        if (this.riftflux$petDeathTimes.isEmpty()) {
            return;
        }
        int previousDeathTime = this.riftflux$petDeathTimes.pop().intValue();
        if (previousDeathTime != riftflux$NOT_KNOCKED_DOWN) {
            entity.deathTime = previousDeathTime;
        }
    }

    @ModifyArgs(
            method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;renderModel(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V"
            )
    )
    private void riftflux$freezeKnockedDownMainModelHead(Args args) {
        if (riftflux$isRenderingKnockedDownPet()) {
            args.set(4, Float.valueOf(0.0F));
            args.set(5, Float.valueOf(0.0F));
        }
    }

    @ModifyArgs(
            method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"
            )
    )
    private void riftflux$freezeKnockedDownRenderPassHead(Args args) {
        if (riftflux$isRenderingKnockedDownPet()) {
            args.set(4, Float.valueOf(0.0F));
            args.set(5, Float.valueOf(0.0F));
        }
    }

    @Unique
    private boolean riftflux$isRenderingKnockedDownPet() {
        return !this.riftflux$knockdownRenderStates.isEmpty()
                && this.riftflux$knockdownRenderStates.peek().booleanValue();
    }

    @Inject(
            method = "rotateCorpse(Lnet/minecraft/entity/EntityLivingBase;FFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$renderKnockedDownPose(
            EntityLivingBase entity,
            float age,
            float bodyYaw,
            float partialTicks,
            CallbackInfo ci
    ) {
        if (!PetKnockdown.isKnockedDown(entity)) {
            return;
        }

        float deathRotation = this.getDeathMaxRotation(entity);
        double corpseCenterDistance =
                Math.sin(Math.toRadians((double) deathRotation)) * (double) entity.height * 0.5D;
        double hitboxCenterOffsetX =
                (entity.boundingBox.minX + entity.boundingBox.maxX) * 0.5D - entity.posX;
        double hitboxCenterOffsetZ =
                (entity.boundingBox.minZ + entity.boundingBox.maxZ) * 0.5D - entity.posZ;
        GL11.glTranslated(hitboxCenterOffsetX, 0.0D, hitboxCenterOffsetZ);
        float thrownSpin = ModConfig.spinThrownKnockedDownPets
                && PetKnockdownCarry.isThrown(entity)
                && !entity.onGround
                && !entity.isCollidedVertically
                ? ((float) entity.ticksExisted + partialTicks)
                        * ModConfig.thrownKnockedDownPetSpinSpeed
                : 0.0F;
        float lockedBodyYaw = entity instanceof PetKnockdownRotationAccess
                ? ((PetKnockdownRotationAccess) entity).riftflux$getKnockdownBodyYaw()
                : bodyYaw;
        GL11.glRotatef(180.0F - lockedBodyYaw + thrownSpin, 0.0F, 1.0F, 0.0F);
        GL11.glTranslated(corpseCenterDistance, 0.0D, 0.0D);
        GL11.glRotatef(deathRotation, 0.0F, 0.0F, 1.0F);
        ci.cancel();
    }
}
