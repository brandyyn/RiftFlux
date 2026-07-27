package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.entity.PrimedTntCarry;
import com.voidsrift.riftflux.entity.PrimedTntCarryEvents;
import com.voidsrift.riftflux.pets.PetKnockdown;
import com.voidsrift.riftflux.pets.PetKnockdownCarry;
import com.voidsrift.riftflux.pets.PetKnockdownCarryEvents;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayerMP;
import net.nmccoy.legendgear.legacy.entities.EntityBomb;

public class MsgBombCarryUse implements IMessage {
    private int entityId;

    public MsgBombCarryUse() {
    }

    public MsgBombCarryUse(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
    }

    public static class Handler implements IMessageHandler<MsgBombCarryUse, IMessage> {
        @Override
        public IMessage onMessage(MsgBombCarryUse msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                return null;
            }

            Entity carried = resolveCarriedEntity(player, msg.entityId);
            if (carried instanceof EntityBomb
                    && player.getEntityData().getInteger(EntityBomb.PLAYER_BOMB_CARRYING_TIME_TAG) > 2) {
                ((EntityBomb) carried).throwFromPlayer(player);
            } else if (carried instanceof EntityTNTPrimed
                    && ModConfig.enablePrimedTntPickupAndThrow
                    && PrimedTntCarry.isCarried((EntityTNTPrimed) carried)
                    && player.getEntityData().getInteger(PrimedTntCarry.PLAYER_CARRY_TICKS_TAG) > 2) {
                PrimedTntCarryEvents.throwCarriedTnt(player, (EntityTNTPrimed) carried);
            } else if (carried instanceof EntityLivingBase
                    && ModConfig.enablePetKnockdownPickupAndThrow
                    && PetKnockdownCarry.isCarried((EntityLivingBase) carried)
                    && PetKnockdown.isKnockedDown((EntityLivingBase) carried)
                    && player.getEntityData().getInteger(PetKnockdownCarry.PLAYER_CARRY_TICKS_TAG) > 2) {
                PetKnockdownCarryEvents.throwCarriedMob(player, (EntityLivingBase) carried);
            }
            return null;
        }

        private Entity resolveCarriedEntity(EntityPlayerMP player, int entityId) {
            if (player.riddenByEntity != null) {
                return player.riddenByEntity;
            }

            Entity entity = player.worldObj.getEntityByID(entityId);
            if (entity != null && entity.ridingEntity == player) {
                return entity;
            }
            return null;
        }
    }
}
