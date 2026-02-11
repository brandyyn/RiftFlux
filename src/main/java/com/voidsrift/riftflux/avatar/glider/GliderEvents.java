package com.voidsrift.riftflux.avatar.glider;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import com.voidsrift.riftflux.compat.BackhandCompat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GliderEvents {
    private static final Map<String, Boolean> OFFHAND_USE = new ConcurrentHashMap<String, Boolean>();

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {
        if (!(evt.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) evt.entityLiving;
        ItemStack gliderStack = GliderItemHelper.getGliderStack(player);
        boolean holdingGlider = gliderStack != null;
        String playerName = player.getDisplayName();

        handleOffhandToggle(player, playerName);

        if (!holdingGlider) {
            if (GliderState.isPlayerGliding(playerName)) {
                GliderState.removeGlidingPlayerName(playerName);
            }
            OFFHAND_USE.remove(playerName);
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
        if (evt.entityLiving instanceof EntityPlayer) {
            ItemStack gliderStack = GliderItemHelper.getGliderStack((EntityPlayer) evt.entityLiving);
            if (gliderStack != null) {
                evt.distance = 1.1f;
            }
        }
    }

    private static void handleOffhandToggle(EntityPlayer player, String playerName) {
        if (!player.worldObj.isRemote || !BackhandCompat.isAvailable()) {
            return;
        }
        ItemStack offhand = BackhandCompat.getOffhandItem(player);
        if (!GliderItemHelper.isGlider(offhand)) {
            OFFHAND_USE.remove(playerName);
            return;
        }
        boolean usingOffhand = BackhandCompat.isUsingOffhand(player);
        boolean wasUsing = OFFHAND_USE.containsKey(playerName) && OFFHAND_USE.get(playerName);
        if (usingOffhand && !wasUsing) {
            if (GliderState.isPlayerGliding(playerName)) {
                GliderState.removeGlidingPlayerName(playerName);
            } else {
                GliderState.addGlidingPlayerName(playerName);
            }
        }
        OFFHAND_USE.put(playerName, usingOffhand);
    }
}
