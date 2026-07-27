package com.voidsrift.riftflux.entity;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class PrimedTntCarryEvents {
    private static final int THROW_GRACE_TICKS = 2;

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (event == null || event.entityPlayer == null || event.entityPlayer.worldObj.isRemote) {
            return;
        }

        EntityPlayer player = event.entityPlayer;
        EntityTNTPrimed carried = getCarriedTnt(player);
        if (carried != null) {
            if (getCarryTicks(player) > THROW_GRACE_TICKS) {
                throwCarriedTnt(player, carried);
                event.setCanceled(true);
            }
            return;
        }

        if (!ModConfig.enablePrimedTntPickupAndThrow
                || player.riddenByEntity != null
                || player.getCurrentEquippedItem() != null
                || !(event.target instanceof EntityTNTPrimed)) {
            return;
        }

        EntityTNTPrimed tnt = (EntityTNTPrimed) event.target;
        if (tnt.isDead || tnt.ridingEntity != null) {
            return;
        }

        PrimedTntCarry.markCarried(tnt);
        tnt.mountEntity(player);
        PrimedTntCarry.updateCarriedPosition(tnt, player);
        player.getEntityData().setInteger(PrimedTntCarry.PLAYER_CARRY_TICKS_TAG, 0);
        player.worldObj.playSoundAtEntity(player, "legendgear:lift", 0.2F, 1.2F);
        player.swingItem();
        syncAttachment(player, tnt, player);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event == null
                || event.entityPlayer == null
                || event.entityPlayer.worldObj.isRemote
                || (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR
                        && event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        EntityPlayer player = event.entityPlayer;
        EntityTNTPrimed carried = getCarriedTnt(player);
        if (carried == null || getCarryTicks(player) <= THROW_GRACE_TICKS) {
            return;
        }

        throwCarriedTnt(player, carried);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (event == null || !(event.entityLiving instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (!(player.riddenByEntity instanceof EntityTNTPrimed)
                || !PrimedTntCarry.isCarried((EntityTNTPrimed) player.riddenByEntity)) {
            if (!player.worldObj.isRemote) {
                player.getEntityData().setInteger(PrimedTntCarry.PLAYER_CARRY_TICKS_TAG, 0);
            }
            return;
        }

        EntityTNTPrimed tnt = (EntityTNTPrimed) player.riddenByEntity;
        if (!ModConfig.enablePrimedTntPickupAndThrow || tnt.isDead) {
            PrimedTntCarry.clearState(tnt);
            syncAttachment(player, tnt, null);
            return;
        }

        if (!player.worldObj.isRemote) {
            player.getEntityData().setInteger(
                    PrimedTntCarry.PLAYER_CARRY_TICKS_TAG,
                    getCarryTicks(player) + 1
            );
        }
    }

    private static EntityTNTPrimed getCarriedTnt(EntityPlayer player) {
        if (!ModConfig.enablePrimedTntPickupAndThrow
                || !(player.riddenByEntity instanceof EntityTNTPrimed)) {
            return null;
        }
        EntityTNTPrimed tnt = (EntityTNTPrimed) player.riddenByEntity;
        return PrimedTntCarry.isCarried(tnt) && !tnt.isDead ? tnt : null;
    }

    private static int getCarryTicks(EntityPlayer player) {
        return player.getEntityData().getInteger(PrimedTntCarry.PLAYER_CARRY_TICKS_TAG);
    }

    public static void throwCarriedTnt(EntityPlayer player, EntityTNTPrimed tnt) {
        PrimedTntCarry.updateCarriedPosition(tnt, player);
        tnt.mountEntity(null);
        PrimedTntCarry.markThrown(tnt);

        float speed = 0.8F;
        float yawRadians = player.rotationYaw / 180.0F * (float) Math.PI;
        float pitchRadians = player.rotationPitch / 180.0F * (float) Math.PI;
        tnt.motionX = -MathHelper.sin(yawRadians) * MathHelper.cos(pitchRadians) * speed;
        tnt.motionZ = MathHelper.cos(yawRadians) * MathHelper.cos(pitchRadians) * speed;
        tnt.motionY = -MathHelper.sin(pitchRadians) * speed + 0.2D;
        tnt.fallDistance = 0.0F;
        tnt.onGround = false;
        tnt.isAirBorne = true;
        tnt.velocityChanged = true;

        player.getEntityData().setInteger(PrimedTntCarry.PLAYER_CARRY_TICKS_TAG, 0);
        player.worldObj.playSoundAtEntity(
                player,
                "random.bow",
                0.5F,
                0.4F / (player.getRNG().nextFloat() * 0.4F + 0.8F)
        );
        player.swingItem();
        syncAttachment(player, tnt, null);
        syncThrownMotion(player, tnt);
    }

    private static void syncAttachment(EntityPlayer player, EntityTNTPrimed tnt, Entity mount) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(
                    new S1BPacketEntityAttach(0, tnt, mount)
            );
        }
    }

    private static void syncThrownMotion(EntityPlayer player, EntityTNTPrimed tnt) {
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
            serverPlayer.playerNetServerHandler.sendPacket(new S18PacketEntityTeleport(tnt));
            serverPlayer.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(tnt));
        }
    }
}
