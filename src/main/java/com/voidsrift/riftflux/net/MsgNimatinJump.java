package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.palaria.entity.EntityNimatin;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

public class MsgNimatinJump implements IMessage {
    private static final byte ACTION_CHARGED_JUMP = 0;
    private static final byte ACTION_DOUBLE_JUMP = 1;

    private byte action;
    private int entityId;
    private int jumpCharge;

    public MsgNimatinJump() {
    }

    public MsgNimatinJump(int entityId, int jumpCharge) {
        this.action = ACTION_CHARGED_JUMP;
        this.entityId = entityId;
        this.jumpCharge = jumpCharge;
    }

    public static MsgNimatinJump doubleJump(int entityId) {
        MsgNimatinJump msg = new MsgNimatinJump();
        msg.action = ACTION_DOUBLE_JUMP;
        msg.entityId = entityId;
        msg.jumpCharge = 0;
        return msg;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.action = buf.readByte();
        this.entityId = buf.readInt();
        this.jumpCharge = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.action);
        buf.writeInt(this.entityId);
        buf.writeInt(this.jumpCharge);
    }

    public static class Handler implements IMessageHandler<MsgNimatinJump, IMessage> {
        @Override
        public IMessage onMessage(MsgNimatinJump msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                return null;
            }
            EntityNimatin nimatin = resolveNimatin(player, msg.entityId);
            if (nimatin == null || !nimatin.forceDirectRider(player)) {
                return null;
            }
            if (msg.action == ACTION_DOUBLE_JUMP) {
                nimatin.tryDoubleJump(player);
            } else {
                nimatin.setJumpPower(msg.jumpCharge);
            }
            return null;
        }

        private EntityNimatin resolveNimatin(EntityPlayerMP player, int entityId) {
            if (player.ridingEntity instanceof EntityNimatin) {
                return (EntityNimatin) player.ridingEntity;
            }
            Entity entity = player.worldObj.getEntityByID(entityId);
            if (entity instanceof EntityNimatin) {
                return (EntityNimatin) entity;
            }
            java.util.List entities = player.worldObj.getEntitiesWithinAABB(
                    EntityNimatin.class,
                    player.boundingBox.expand(8.0D, 8.0D, 8.0D)
            );
            EntityNimatin closest = null;
            double closestDistance = Double.MAX_VALUE;
            for (Object obj : entities) {
                EntityNimatin nimatin = (EntityNimatin) obj;
                double distance = player.getDistanceSqToEntity(nimatin);
                if ((nimatin.riddenByEntity == player || distance <= 64.0D) && distance < closestDistance) {
                    closest = nimatin;
                    closestDistance = distance;
                }
            }
            return closest;
        }
    }
}
