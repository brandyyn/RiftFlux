package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MsgSetChatBubbleSize implements IMessage {
    private float scale;

    public MsgSetChatBubbleSize() {
    }

    public MsgSetChatBubbleSize(float scale) {
        this.scale = scale;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        scale = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(scale);
    }

    public static class Handler implements IMessageHandler<MsgSetChatBubbleSize, IMessage> {
        @Override
        public IMessage onMessage(MsgSetChatBubbleSize message, MessageContext ctx) {
            try {
                ModConfig.saveChatBubblesTextScale(message.scale);
            } catch (IllegalArgumentException ignored) {
            }
            return null;
        }
    }
}
