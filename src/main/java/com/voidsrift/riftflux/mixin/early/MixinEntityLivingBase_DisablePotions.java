package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Blocks specific potion IDs from ever being applied to players.
 *
 * If the potion's ID is in ModConfig.disabledPotionIds, addPotionEffect is cancelled
 * and the effect is never added to the player.
 */
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_DisablePotions {

    @Inject(
            method = "addPotionEffect(Lnet/minecraft/potion/PotionEffect;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rf$blockConfiguredPotions(PotionEffect effect, CallbackInfo ci) {
        if (effect == null) {
            return;
        }

        EntityLivingBase self = (EntityLivingBase) (Object) this;

        // Only block effects on players (not mobs)
        if (!(self instanceof EntityPlayer)) {
            return;
        }

        // Feature master toggle
        if (!ModConfig.disableSpecificPotions) {
            return;
        }

        int id = effect.getPotionID();
        if (ModConfig.isPotionIdDisabled(id)) {
            // Cancel: this potion effect will never be applied to the player
            ci.cancel();
        }
    }
}
