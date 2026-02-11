package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.avatar.appa.EntityBison;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

public class MsgAppaControl implements IMessage {
    private int entityId;
    private boolean up;
    private boolean down;

    public MsgAppaControl() {
    }

    public MsgAppaControl(int entityId, boolean up, boolean down) {
        this.entityId = entityId;
        this.up = up;
        this.down = down;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.up = buf.readBoolean();
        this.down = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeBoolean(this.up);
        buf.writeBoolean(this.down);
    }

    public static class Handler implements IMessageHandler<MsgAppaControl, IMessage> {
        @Override
        public IMessage onMessage(MsgAppaControl msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                return null;
            }
            Entity entity = player.worldObj.getEntityByID(msg.entityId);
            if (entity instanceof EntityBison && entity.riddenByEntity == player) {
                ((EntityBison) entity).setGlideControls(msg.up, msg.down);
            }
            return null;
        }
    }
}
