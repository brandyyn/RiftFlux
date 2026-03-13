package com.voidsrift.riftflux.mixin.early.torohealth;

import com.voidsrift.riftflux.combat.torohealth.mixins.EntityLivingBaseExt;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_ToroHealth implements EntityLivingBaseExt {

    @Unique
    private int riftflux$toroHealthPrevHealth;

    @Unique
    private int riftflux$toroHealthLastDamageParticleTick;

    @Override
    @Unique
    public int riftflux$getToroHealthPrevHealth() {
        return this.riftflux$toroHealthPrevHealth;
    }

    @Override
    @Unique
    public void riftflux$setToroHealthPrevHealth(int value) {
        this.riftflux$toroHealthPrevHealth = value;
    }

    @Override
    @Unique
    public int riftflux$getToroHealthLastDamageParticleTick() {
        return this.riftflux$toroHealthLastDamageParticleTick;
    }

    @Override
    @Unique
    public void riftflux$setToroHealthLastDamageParticleTick(int value) {
        this.riftflux$toroHealthLastDamageParticleTick = value;
    }

    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;setHealth(F)V")
    )
    private float riftflux$initToroHealth(float health) {
        this.riftflux$toroHealthPrevHealth = MathHelper.floor_float(health);
        this.riftflux$toroHealthLastDamageParticleTick = 0;
        return health;
    }
}
