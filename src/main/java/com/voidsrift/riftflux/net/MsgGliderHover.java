package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.avatar.glider.GliderState;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class MsgGliderHover implements IMessage {
    private boolean hovering;

    public MsgGliderHover() {
    }

    public MsgGliderHover(boolean hovering) {
        this.hovering = hovering;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.hovering = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.hovering);
    }

    public static class Handler implements IMessageHandler<MsgGliderHover, IMessage> {
        @Override
        public IMessage onMessage(MsgGliderHover msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                return null;
            }
            String name = player.getDisplayName();
            GliderState.setPlayerHovering(name, msg.hovering);
            return null;
        }
    }
}
