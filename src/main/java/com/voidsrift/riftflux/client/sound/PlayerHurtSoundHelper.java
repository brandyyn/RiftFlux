package com.voidsrift.riftflux.client.sound;

import com.voidsrift.riftflux.sound.PlayerHurtSoundResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public final class PlayerHurtSoundHelper {
    private PlayerHurtSoundHelper() {
    }

    public static void playClientPlayerHurtSound(EntityPlayer player, float volume, float pitch) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.getSoundHandler() == null) {
            return;
        }
        if (player == null || player != mc.thePlayer) {
            return;
        }

        ResourceLocation sound = new ResourceLocation(PlayerHurtSoundResolver.resolvePlayerHurtSoundKey());
        mc.getSoundHandler().playSound(new PositionedSoundRecord(
                sound,
                volume,
                pitch,
                (float) player.posX,
                (float) (player.posY - player.yOffset),
                (float) player.posZ
        ));
    }
}
