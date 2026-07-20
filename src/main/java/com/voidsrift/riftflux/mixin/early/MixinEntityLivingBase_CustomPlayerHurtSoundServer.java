package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Suppresses vanilla's server sound packet for players. The accepted-hit entity status packet
 * remains authoritative and the client mixin replaces its hurt sound with RiftFlux's sound.
 */
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_CustomPlayerHurtSoundServer {
    @Shadow
    protected abstract String getHurtSound();

    @Redirect(
            method = "attackEntityFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;getHurtSound()Ljava/lang/String;"
            )
    )
    private String riftflux$suppressServerPlayerHurtSound(EntityLivingBase self, DamageSource source, float amount) {
        if (ModConfig.playerOnlyHurtSound && self instanceof EntityPlayer) {
            return null;
        }
        return this.getHurtSound();
    }
}
