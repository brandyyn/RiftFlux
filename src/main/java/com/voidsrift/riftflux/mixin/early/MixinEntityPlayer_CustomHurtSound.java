package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Resource packs commonly replace the vanilla player hurt sound ("game.player.hurt").
 * Unfortunately, many mods reuse that same sound for their mobs, so a resource pack ends up
 * changing modded mob hurt sounds too.
 *
 * This mixin gives the *player* a dedicated sound key in our domain, leaving "game.player.hurt"
 * untouched so mobs can keep using vanilla.
 */
@Mixin(EntityPlayer.class)
public class MixinEntityPlayer_CustomHurtSound {

    @Inject(method = "getHurtSound()Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void riftflux$useCustomPlayerHurtSound(CallbackInfoReturnable<String> cir) {
        if (ModConfig.playerOnlyHurtSound) {
            cir.setReturnValue(Constants.MODID + ":player_hurt");
        }
    }
}