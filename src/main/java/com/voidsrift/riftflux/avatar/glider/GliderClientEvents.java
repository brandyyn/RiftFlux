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
        ItemStack held = player.getHeldItem();
        boolean holdingGlider = held != null && held.getItem() instanceof ItemGlider;
        boolean isLocal = player == Minecraft.getMinecraft().thePlayer;
        String playerName = player.getDisplayName();

        if (holdingGlider) {
            boolean shouldGlide = !player.onGround && !player.isInWater()
                    && GliderState.isPlayerGliding(playerName);
            if (shouldGlide) {
                ensureGlider(player, held);
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
        ItemStack held = player.getHeldItem();
        boolean holdingGlider = held != null && held.getItem() instanceof ItemGlider;
        String playerName = player.getDisplayName();
        boolean shouldGlide = holdingGlider && !player.onGround && !player.isInWater()
                && GliderState.isPlayerGliding(playerName);

        if (shouldGlide) {
            ensureGlider(player, held);
        } else if (player.riddenByEntity instanceof EntityGlider) {
            detachGlider(player);
        }
    }

    private void ensureGlider(EntityPlayer player, ItemStack held) {
        if (player.riddenByEntity instanceof EntityGlider) {
            return;
        }
        EntityGlider entity = new EntityGlider(player.worldObj, ((ItemGlider) held.getItem()).getColor());
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
