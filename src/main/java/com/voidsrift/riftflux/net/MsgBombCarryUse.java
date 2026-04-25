package com.voidsrift.riftflux.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
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

            EntityBomb bomb = resolveBomb(player, msg.entityId);
            if (bomb != null && player.getEntityData().getInteger(EntityBomb.PLAYER_BOMB_CARRYING_TIME_TAG) > 2) {
                bomb.throwFromPlayer(player);
            }
            return null;
        }

        private EntityBomb resolveBomb(EntityPlayerMP player, int entityId) {
            if (player.riddenByEntity instanceof EntityBomb) {
                return (EntityBomb) player.riddenByEntity;
            }

            Entity entity = player.worldObj.getEntityByID(entityId);
            if (entity instanceof EntityBomb) {
                EntityBomb bomb = (EntityBomb) entity;
                if (bomb.ridingEntity == player) {
                    return bomb;
                }
            }
            return null;
        }
    }
}
