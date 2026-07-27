package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.pets.PetKnockdown;
import com.voidsrift.riftflux.pets.PetKnockdownCarry;
import com.voidsrift.riftflux.pets.PetKnockdownRotationAccess;
import com.voidsrift.riftflux.pets.PetKnockdownTimeout;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_PetKnockdown implements PetKnockdownRotationAccess {

    @Shadow
    protected boolean isJumping;

    @Unique
    private boolean riftflux$knockdownRotationLocked;
    @Unique
    private float riftflux$knockdownYaw;
    @Unique
    private float riftflux$knockdownPitch;
    @Unique
    private float riftflux$knockdownHeadYaw;
    @Unique
    private float riftflux$knockdownBodyYaw;
    @Unique
    private double riftflux$knockdownX;
    @Unique
    private double riftflux$knockdownY;
    @Unique
    private double riftflux$knockdownZ;

    @Inject(method = "canBeCollidedWith()Z", at = @At("HEAD"), cancellable = true)
    private void riftflux$ignoreCarriedPetHitbox(CallbackInfoReturnable<Boolean> cir) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (PetKnockdownCarry.isCarried(self)) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    @ModifyVariable(
            method = "setHealth(F)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float riftflux$clampProtectedPetHealth(float health) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!PetKnockdown.isForcingDeath(self)
                && health <= PetKnockdown.KNOCKDOWN_HEALTH
                && PetKnockdown.isProtectedPet(self)) {
            return PetKnockdown.KNOCKDOWN_HEALTH;
        }
        return health;
    }

    @ModifyVariable(
            method = "setRevengeTarget(Lnet/minecraft/entity/EntityLivingBase;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private EntityLivingBase riftflux$rejectKnockedDownRevengeTarget(EntityLivingBase target) {
        return target != null && PetKnockdown.isKnockedDown(target) ? null : target;
    }

    @Inject(
            method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$blockKnockdownDamageAndAttacks(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> cir
    ) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!PetKnockdown.isForcingDeath(self)
                && amount > 0.0F
                && (PetKnockdown.isKnockedDown(self) || PetKnockdown.isDamageFromKnockedDownPet(source))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onLivingUpdate()V", at = @At("HEAD"))
    private void riftflux$beginKnockdownTick(CallbackInfo ci) {
        PetKnockdownTimeout.tick((EntityLivingBase) (Object) this);
        riftflux$holdKnockedDownPet();
    }

    @Inject(method = "onLivingUpdate()V", at = @At("RETURN"))
    private void riftflux$endKnockdownTick(CallbackInfo ci) {
        riftflux$holdKnockedDownPet();
    }

    @Inject(method = "updateRidden()V", at = @At("RETURN"))
    private void riftflux$lockCarriedKnockdownRotation(CallbackInfo ci) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (PetKnockdown.isKnockedDown(self) && PetKnockdownCarry.isCarried(self)) {
            this.riftflux$lockKnockdownRotation(self);
        }
    }

    private void riftflux$holdKnockedDownPet() {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!PetKnockdown.isKnockedDown(self)) {
            if (PetKnockdownCarry.hasState(self)) {
                PetKnockdownCarry.clearState(self);
            }
            this.riftflux$knockdownRotationLocked = false;
            return;
        }

        if (!ModConfig.enablePetKnockdownPickupAndThrow
                && PetKnockdownCarry.hasState(self)) {
            PetKnockdownCarry.clearState(self);
        }
        if (PetKnockdownCarry.isThrown(self)
                && (self.onGround || (self.isCollidedVertically && self.motionY <= 0.0D))) {
            this.riftflux$keepThrowLandingRotation(self);
            PetKnockdownCarry.settleThrown(self);
        }

        this.riftflux$lockKnockdownRotation(self);
        self.moveForward = 0.0F;
        self.moveStrafing = 0.0F;
        self.limbSwing = 0.0F;
        self.limbSwingAmount = 0.0F;
        self.prevLimbSwingAmount = 0.0F;
        this.isJumping = false;

        boolean carried = PetKnockdownCarry.isCarried(self);
        boolean thrown = PetKnockdownCarry.isThrown(self);
        if (carried) {
            self.motionX = 0.0D;
            self.motionY = 0.0D;
            self.motionZ = 0.0D;
        } else if (!thrown) {
            self.motionX = 0.0D;
            self.motionY = 0.0D;
            self.motionZ = 0.0D;
            self.setPosition(
                    this.riftflux$knockdownX,
                    this.riftflux$knockdownY,
                    this.riftflux$knockdownZ
            );
            self.prevPosX = this.riftflux$knockdownX;
            self.prevPosY = this.riftflux$knockdownY;
            self.prevPosZ = this.riftflux$knockdownZ;
            self.lastTickPosX = this.riftflux$knockdownX;
            self.lastTickPosY = this.riftflux$knockdownY;
            self.lastTickPosZ = this.riftflux$knockdownZ;
        }

        if (self instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) self;
            living.getNavigator().clearPathEntity();
            living.setAttackTarget(null);
            living.setRevengeTarget(null);
        }

        self.extinguish();

        if (ModConfig.showPetKnockdownParticles
                && self.worldObj.isRemote
                && self.getRNG().nextInt(5) == 0) {
            int color = Potion.moveSlowdown.getLiquidColor();
            double red = (double) (color >> 16 & 255) / 255.0D;
            double green = (double) (color >> 8 & 255) / 255.0D;
            double blue = (double) (color & 255) / 255.0D;
            self.worldObj.spawnParticle(
                    "mobSpell",
                    self.posX + (self.getRNG().nextDouble() - 0.5D) * (double) self.width,
                    self.posY
                            + self.getRNG().nextDouble() * (double) self.height
                            + (double) ModConfig.petKnockdownParticleHeightOffset,
                    self.posZ + (self.getRNG().nextDouble() - 0.5D) * (double) self.width,
                    red,
                    green,
                    blue
            );
        }
    }

    @Unique
    private void riftflux$lockKnockdownRotation(EntityLivingBase self) {
        if (!this.riftflux$knockdownRotationLocked) {
            this.riftflux$knockdownRotationLocked = true;
            this.riftflux$knockdownYaw = self.rotationYaw;
            this.riftflux$knockdownPitch = self.rotationPitch;
            this.riftflux$knockdownHeadYaw = self.rotationYawHead;
            this.riftflux$knockdownBodyYaw = self.renderYawOffset;
            this.riftflux$knockdownX = self.posX;
            this.riftflux$knockdownY = self.posY;
            this.riftflux$knockdownZ = self.posZ;
        }

        self.rotationYaw = this.riftflux$knockdownYaw;
        self.prevRotationYaw = this.riftflux$knockdownYaw;
        self.rotationPitch = this.riftflux$knockdownPitch;
        self.prevRotationPitch = this.riftflux$knockdownPitch;
        self.rotationYawHead = this.riftflux$knockdownHeadYaw;
        self.prevRotationYawHead = this.riftflux$knockdownHeadYaw;
        self.renderYawOffset = this.riftflux$knockdownBodyYaw;
        self.prevRenderYawOffset = this.riftflux$knockdownBodyYaw;
    }

    @Unique
    private void riftflux$keepThrowLandingRotation(EntityLivingBase self) {
        if (ModConfig.spinThrownKnockedDownPets) {
            float completedSpin =
                    (float) self.ticksExisted * ModConfig.thrownKnockedDownPetSpinSpeed;
            this.riftflux$knockdownBodyYaw = MathHelper.wrapAngleTo180_float(
                    this.riftflux$knockdownBodyYaw - completedSpin
            );
            this.riftflux$knockdownYaw = this.riftflux$knockdownBodyYaw;
            this.riftflux$knockdownHeadYaw = this.riftflux$knockdownBodyYaw;
        }
        this.riftflux$knockdownX = self.posX;
        this.riftflux$knockdownY = self.posY;
        this.riftflux$knockdownZ = self.posZ;
        this.riftflux$knockdownRotationLocked = true;
        this.riftflux$lockKnockdownRotation(self);
    }

    @Override
    public float riftflux$getKnockdownBodyYaw() {
        return this.riftflux$knockdownBodyYaw;
    }

    @Inject(method = "writeEntityToNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"))
    private void riftflux$writeKnockdownAnchor(NBTTagCompound tag, CallbackInfo ci) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!PetKnockdown.isKnockedDown(self)) {
            return;
        }
        if (!this.riftflux$knockdownRotationLocked) {
            this.riftflux$lockKnockdownRotation(self);
        }

        tag.setDouble("RiftFluxKnockdownX", this.riftflux$knockdownX);
        tag.setDouble("RiftFluxKnockdownY", this.riftflux$knockdownY);
        tag.setDouble("RiftFluxKnockdownZ", this.riftflux$knockdownZ);
        tag.setFloat("RiftFluxKnockdownYaw", this.riftflux$knockdownYaw);
        tag.setFloat("RiftFluxKnockdownPitch", this.riftflux$knockdownPitch);
        tag.setFloat("RiftFluxKnockdownHeadYaw", this.riftflux$knockdownHeadYaw);
        tag.setFloat("RiftFluxKnockdownBodyYaw", this.riftflux$knockdownBodyYaw);
    }

    @Inject(method = "readEntityFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"))
    private void riftflux$readKnockdownAnchor(NBTTagCompound tag, CallbackInfo ci) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (self.getHealth() <= 0.0F
                || self.getHealth() > PetKnockdown.KNOCKDOWN_HEALTH
                || !tag.hasKey("RiftFluxKnockdownX")) {
            return;
        }

        this.riftflux$knockdownRotationLocked = true;
        this.riftflux$knockdownX = tag.getDouble("RiftFluxKnockdownX");
        this.riftflux$knockdownY = tag.getDouble("RiftFluxKnockdownY");
        this.riftflux$knockdownZ = tag.getDouble("RiftFluxKnockdownZ");
        this.riftflux$knockdownYaw = tag.getFloat("RiftFluxKnockdownYaw");
        this.riftflux$knockdownPitch = tag.getFloat("RiftFluxKnockdownPitch");
        this.riftflux$knockdownHeadYaw = tag.getFloat("RiftFluxKnockdownHeadYaw");
        this.riftflux$knockdownBodyYaw = tag.getFloat("RiftFluxKnockdownBodyYaw");
        self.setPosition(
                this.riftflux$knockdownX,
                this.riftflux$knockdownY,
                this.riftflux$knockdownZ
        );
    }
}
