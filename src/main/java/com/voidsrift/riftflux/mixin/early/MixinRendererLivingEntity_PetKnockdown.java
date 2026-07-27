package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.pets.PetKnockdown;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity_PetKnockdown {
    @Unique
    private static final int riftflux$NOT_KNOCKED_DOWN = Integer.MIN_VALUE;

    @Shadow
    protected abstract float getDeathMaxRotation(EntityLivingBase entity);

    @Unique
    private final Deque<Integer> riftflux$petDeathTimes = new ArrayDeque<Integer>();

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
        if (this.riftflux$petDeathTimes.isEmpty()) {
            return;
        }
        int previousDeathTime = this.riftflux$petDeathTimes.pop().intValue();
        if (previousDeathTime != riftflux$NOT_KNOCKED_DOWN) {
            entity.deathTime = previousDeathTime;
        }
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
        double bodyYawRadians = Math.toRadians((double) bodyYaw);
        double hitboxCenterOffsetX =
                (entity.boundingBox.minX + entity.boundingBox.maxX) * 0.5D - entity.posX;
        double hitboxCenterOffsetZ =
                (entity.boundingBox.minZ + entity.boundingBox.maxZ) * 0.5D - entity.posZ;
        GL11.glTranslated(
                hitboxCenterOffsetX - Math.cos(bodyYawRadians) * corpseCenterDistance,
                0.0D,
                hitboxCenterOffsetZ - Math.sin(bodyYawRadians) * corpseCenterDistance
        );
        GL11.glRotatef(180.0F - bodyYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(deathRotation, 0.0F, 0.0F, 1.0F);
        ci.cancel();
    }
}
