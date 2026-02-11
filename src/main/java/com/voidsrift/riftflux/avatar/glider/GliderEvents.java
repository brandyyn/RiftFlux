package com.voidsrift.riftflux.avatar.glider;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class GliderEvents {
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {
        if (!(evt.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) evt.entityLiving;
        ItemStack held = player.getHeldItem();
        boolean holdingGlider = held != null && held.getItem() instanceof ItemGlider;
        String playerName = player.getDisplayName();

        if (!holdingGlider) {
            if (GliderState.isPlayerGliding(playerName)) {
                GliderState.removeGlidingPlayerName(playerName);
            }
            return;
        }

        if (!GliderState.isPlayerGliding(playerName)) {
            return;
        }

        if (player.isInWater()) {
            GliderState.removeGlidingPlayerName(playerName);
            return;
        }

        if (player.onGround) {
            return;
        }

        if (player.isSneaking()) {
            player.motionY = -0.2;
            player.motionX *= 1.05;
            player.motionZ *= 1.05;
        } else {
            if (player.motionY < -0.07) {
                player.motionY = -0.07;
            }
        }

        if (Math.abs(player.motionX) + Math.abs(player.motionZ) < 2.0) {
            player.motionX *= 1.05;
            player.motionZ *= 1.05;
        }

        player.fallDistance = 0.0f;
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent evt) {
        ItemStack held = evt.entityLiving.getHeldItem();
        if (held != null && held.getItem() instanceof ItemGlider) {
            evt.distance = 1.1f;
        }
    }
}
