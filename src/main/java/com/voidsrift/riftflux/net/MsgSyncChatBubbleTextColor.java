package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.chatbubbles.ChatBubbleColorManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class MsgSyncChatBubbleTextColor implements IMessage {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private String uuid;
    private boolean hasColor;
    private int color;

    public MsgSyncChatBubbleTextColor() {
    }

    public MsgSyncChatBubbleTextColor(String uuid, boolean hasColor, int color) {
        this.uuid = uuid == null ? "" : uuid;
        this.hasColor = hasColor;
        this.color = color & 0xFFFFFF;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = Math.max(0, buf.readInt());
        if (length == 0) {
            uuid = "";
        } else {
            byte[] data = new byte[length];
            buf.readBytes(data);
            uuid = new String(data, UTF8);
        }
        hasColor = buf.readBoolean();
        color = buf.readInt() & 0xFFFFFF;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] data = uuid == null ? new byte[0] : uuid.getBytes(UTF8);
        buf.writeInt(data.length);
        if (data.length > 0) {
            buf.writeBytes(data);
        }
        buf.writeBoolean(hasColor);
        buf.writeInt(color & 0xFFFFFF);
    }

    public static class Handler implements IMessageHandler<MsgSyncChatBubbleTextColor, IMessage> {
        @Override
        public IMessage onMessage(MsgSyncChatBubbleTextColor message, MessageContext ctx) {
            ChatBubbleColorManager.applyClientTextColor(message.uuid, message.hasColor, message.color);
            return null;
        }
    }
}
