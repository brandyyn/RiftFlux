package com.voidsrift.riftflux.avatar.glider;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class GliderClientEvents {
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre evt) {
        if (!evt.isCancelable() || evt.entityLiving == null) {
            return;
        }

        EntityPlayer player = evt.entityPlayer;
        ItemStack gliderStack = GliderItemHelper.getGliderStack(player);
        boolean holdingGlider = gliderStack != null;
        boolean isLocal = player == Minecraft.getMinecraft().thePlayer;
        String playerName = player.getDisplayName();
        if (playerName == null) {
            return;
        }

        if (holdingGlider) {
            boolean shouldRenderGlider = GliderState.isPlayerGliding(playerName) && !player.isInWater();
            if (shouldRenderGlider) {
                ensureGlider(player, gliderStack);
                return;
            }

            if (player.riddenByEntity instanceof EntityGlider) {
                detachGlider(player);
            }
            return;
        }

        if (player.riddenByEntity instanceof EntityGlider) {
            detachGlider(player);
        }
        if (isLocal && GliderState.isPlayerGliding(playerName)) {
            GliderState.removeGlidingPlayerName(playerName);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) {
            return;
        }
        EntityPlayer player = mc.thePlayer;
        ItemStack gliderStack = GliderItemHelper.getGliderStack(player);
        boolean holdingGlider = gliderStack != null;
        String playerName = player.getDisplayName();
        if (playerName == null) {
            return;
        }
        boolean shouldRenderGlider = holdingGlider && !player.isInWater()
                && GliderState.isPlayerGliding(playerName);

        if (shouldRenderGlider) {
            ensureGlider(player, gliderStack);
        } else if (player.riddenByEntity instanceof EntityGlider) {
            detachGlider(player);
        }
    }

    private void ensureGlider(EntityPlayer player, ItemStack held) {
        if (held == null || !(held.getItem() instanceof ItemGlider)) {
            return;
        }
        int desiredColor = ((ItemGlider) held.getItem()).getColor();
        if (player.riddenByEntity instanceof EntityGlider) {
            EntityGlider existing = (EntityGlider) player.riddenByEntity;
            if (existing.getColor() != desiredColor) {
                existing.setColor(desiredColor);
            }
            return;
        }
        EntityGlider entity = new EntityGlider(player.worldObj, desiredColor);
        entity.setLocationAndAngles(player.posX, player.posY, player.posZ, 0.0f, 0.0f);
        entity.prevPosX = entity.posX;
        entity.prevPosY = entity.posY;
        entity.prevPosZ = entity.posZ;
        entity.lastTickPosX = entity.posX;
        entity.lastTickPosY = entity.posY;
        entity.lastTickPosZ = entity.posZ;
        entity.ridingEntity = player;
        entity.initOnSpawn();
        entity.prevRotationYAW = player.rotationYaw;
        entity.rotationYAW = player.rotationYaw;
        entity.rotationRoll = 0.01;
        entity.prevRotationRoll = 0.01;
        player.riddenByEntity = entity;
        player.worldObj.spawnEntityInWorld(entity);
    }

    private void detachGlider(EntityPlayer player) {
        Entity rider = player.riddenByEntity;
        if (rider != null) {
            rider.ridingEntity = null;
            rider.setDead();
        }
        player.riddenByEntity = null;
    }
}
