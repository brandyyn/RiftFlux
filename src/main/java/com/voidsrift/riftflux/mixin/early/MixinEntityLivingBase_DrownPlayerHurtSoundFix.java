package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_DrownPlayerHurtSoundFix {
    @Shadow
    protected abstract String getHurtSound();

    @Redirect(
            method = "attackEntityFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;getHurtSound()Ljava/lang/String;"
            )
    )
    private String riftflux$suppressVanillaDrownHurtSound(EntityLivingBase self, DamageSource source, float amount) {
        if (ModConfig.playerOnlyHurtSound && self instanceof EntityPlayer && source == DamageSource.drown) {
            return null;
        }
        return this.getHurtSound();
    }
}
