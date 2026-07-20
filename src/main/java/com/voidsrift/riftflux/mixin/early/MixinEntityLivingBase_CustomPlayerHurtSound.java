package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.sound.PlayerHurtSoundResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_CustomPlayerHurtSound {
    @Redirect(
            method = "handleHealthUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;playSound(Ljava/lang/String;FF)V",
                    ordinal = 0
            )
    )
    private void riftflux$redirectPlayerHurtSound(EntityLivingBase self, String sound, float volume, float pitch) {
        if (ModConfig.playerOnlyHurtSound && self instanceof EntityPlayer && self.worldObj != null && self.worldObj.isRemote) {
            Minecraft minecraft = Minecraft.getMinecraft();
            boolean localPlayer = minecraft != null && self == minecraft.thePlayer;
            String playerHurtSound = PlayerHurtSoundResolver.resolvePlayerHurtSoundKey(
                    localPlayer || ModConfig.otherPlayersOOF
            );
            self.playSound(playerHurtSound, volume, pitch);
            return;
        }

        if (sound != null) {
            self.playSound(sound, volume, pitch);
        }
    }
}
