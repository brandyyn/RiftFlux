package com.voidsrift.riftflux.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.nmccoy.legendgear.legacy.entities.EntityGrindStar;

public class MsgStarbeamRailJump implements IMessage {
    private static final String CLIENT_JUST_JUMPED = "clientJustJumped";
    private int entityId;

    public MsgStarbeamRailJump() {
    }

    public MsgStarbeamRailJump(int entityId) {
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

    public static class Handler implements IMessageHandler<MsgStarbeamRailJump, IMessage> {
        @Override
        public IMessage onMessage(MsgStarbeamRailJump msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                return null;
            }

            EntityGrindStar star = resolveStar(player, msg.entityId);
            if (star != null) {
                player.getEntityData().setBoolean(CLIENT_JUST_JUMPED, true);
            }
            return null;
        }

        private EntityGrindStar resolveStar(EntityPlayerMP player, int entityId) {
            if (player.ridingEntity instanceof EntityGrindStar) {
                return (EntityGrindStar) player.ridingEntity;
            }

            Entity entity = player.worldObj.getEntityByID(entityId);
            if (entity instanceof EntityGrindStar) {
                EntityGrindStar star = (EntityGrindStar) entity;
                if (star.riddenByEntity == player) {
                    return star;
                }
            }

            return null;
        }
    }
}
