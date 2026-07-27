package com.voidsrift.riftflux.pets;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class PetKnockdownCarryEvents {
    private static final int THROW_GRACE_TICKS = 2;

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (event == null || event.entityPlayer == null || event.entityPlayer.worldObj.isRemote) {
            return;
        }

        EntityPlayer player = event.entityPlayer;
        EntityLivingBase carried = getCarriedKnockedDownMob(player);
        if (carried != null) {
            if (getCarryTicks(player) > THROW_GRACE_TICKS) {
                throwCarriedMob(player, carried);
                event.setCanceled(true);
            }
            return;
        }

        if (!ModConfig.enablePetKnockdownPickupAndThrow
                || player.riddenByEntity != null
                || player.getCurrentEquippedItem() != null
                || !(event.target instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase target = (EntityLivingBase) event.target;
        if (!PetKnockdown.isKnockedDown(target) || target.ridingEntity != null || target.isDead) {
            return;
        }

        PetKnockdownCarry.markCarried(target);
        target.mountEntity(player);
        PetKnockdownCarry.updateCarriedPosition(target, player);
        player.getEntityData().setInteger(PetKnockdownCarry.PLAYER_CARRY_TICKS_TAG, 0);
        player.worldObj.playSoundAtEntity(player, "legendgear:lift", 0.2F, 1.2F);
        player.swingItem();
        syncAttachment(player, target, player);
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
        EntityLivingBase carried = getCarriedKnockedDownMob(player);
        if (carried == null || getCarryTicks(player) <= THROW_GRACE_TICKS) {
            return;
        }

        throwCarriedMob(player, carried);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (event == null || !(event.entityLiving instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        Entity passenger = player.riddenByEntity;
        if (!(passenger instanceof EntityLivingBase)
                || !PetKnockdownCarry.isCarried((EntityLivingBase) passenger)) {
            if (!player.worldObj.isRemote) {
                player.getEntityData().setInteger(PetKnockdownCarry.PLAYER_CARRY_TICKS_TAG, 0);
            }
            return;
        }

        EntityLivingBase carried = (EntityLivingBase) passenger;
        if (!ModConfig.enablePetKnockdownPickupAndThrow || !PetKnockdown.isKnockedDown(carried)) {
            PetKnockdownCarry.clearState(carried);
            syncAttachment(player, carried, null);
            return;
        }

        if (!player.worldObj.isRemote) {
            player.getEntityData().setInteger(
                    PetKnockdownCarry.PLAYER_CARRY_TICKS_TAG,
                    getCarryTicks(player) + 1
            );
        }
    }

    private static EntityLivingBase getCarriedKnockedDownMob(EntityPlayer player) {
        if (!ModConfig.enablePetKnockdownPickupAndThrow
                || !(player.riddenByEntity instanceof EntityLivingBase)) {
            return null;
        }
        EntityLivingBase entity = (EntityLivingBase) player.riddenByEntity;
        return PetKnockdownCarry.isCarried(entity) && PetKnockdown.isKnockedDown(entity) ? entity : null;
    }

    private static int getCarryTicks(EntityPlayer player) {
        return player.getEntityData().getInteger(PetKnockdownCarry.PLAYER_CARRY_TICKS_TAG);
    }

    public static void throwCarriedMob(EntityPlayer player, EntityLivingBase entity) {
        PetKnockdownCarry.updateCarriedPosition(entity, player);
        entity.mountEntity(null);
        PetKnockdownCarry.markThrown(entity);

        float speed = 0.8F;
        float yawRadians = player.rotationYaw / 180.0F * (float) Math.PI;
        float pitchRadians = player.rotationPitch / 180.0F * (float) Math.PI;
        entity.motionX = -MathHelper.sin(yawRadians) * MathHelper.cos(pitchRadians) * speed;
        entity.motionZ = MathHelper.cos(yawRadians) * MathHelper.cos(pitchRadians) * speed;
        entity.motionY = -MathHelper.sin(pitchRadians) * speed + 0.2D;
        entity.fallDistance = 0.0F;
        entity.onGround = false;
        entity.isAirBorne = true;
        entity.velocityChanged = true;

        player.getEntityData().setInteger(PetKnockdownCarry.PLAYER_CARRY_TICKS_TAG, 0);
        player.worldObj.playSoundAtEntity(
                player,
                "random.bow",
                0.5F,
                0.4F / (player.getRNG().nextFloat() * 0.4F + 0.8F)
        );
        player.swingItem();
        syncAttachment(player, entity, null);
        syncThrownMotion(player, entity);
    }

    private static void syncAttachment(EntityPlayer player, EntityLivingBase entity, Entity mount) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(
                    new S1BPacketEntityAttach(0, entity, mount)
            );
        }
    }

    private static void syncThrownMotion(EntityPlayer player, EntityLivingBase entity) {
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
            serverPlayer.playerNetServerHandler.sendPacket(new S18PacketEntityTeleport(entity));
            serverPlayer.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
        }
    }
}
