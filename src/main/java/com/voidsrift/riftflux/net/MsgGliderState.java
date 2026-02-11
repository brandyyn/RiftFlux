package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.avatar.glider.GliderState;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MsgGliderState implements IMessage {
    private boolean gliding;
    private String name;

    public MsgGliderState() {
    }

    public MsgGliderState(boolean gliding, String name) {
        this.gliding = gliding;
        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.gliding = buf.readBoolean();
        this.name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.gliding);
        ByteBufUtils.writeUTF8String(buf, this.name == null ? "" : this.name);
    }

    public static class Handler implements IMessageHandler<MsgGliderState, IMessage> {
        @Override
        public IMessage onMessage(MsgGliderState msg, MessageContext ctx) {
            if (msg.name == null || msg.name.isEmpty()) {
                return null;
            }
            if (msg.gliding) {
                GliderState.addGlidingPlayerName(msg.name, true);
            } else {
                GliderState.removeGlidingPlayerName(msg.name, true);
            }
            return null;
        }
    }
}
