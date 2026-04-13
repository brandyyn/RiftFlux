package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class MsgSetChatBubblesConfig implements IMessage {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private String setting;
    private String value;

    public MsgSetChatBubblesConfig() {
    }

    public MsgSetChatBubblesConfig(String setting, String value) {
        this.setting = setting == null ? "" : setting;
        this.value = value == null ? "" : value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        setting = readString(buf);
        value = readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeString(buf, setting);
        writeString(buf, value);
    }

    private static String readString(ByteBuf buf) {
        int length = Math.max(0, buf.readInt());
        if (length == 0) {
            return "";
        }
        byte[] data = new byte[length];
        buf.readBytes(data);
        return new String(data, UTF8);
    }

    private static void writeString(ByteBuf buf, String value) {
        byte[] data = value == null ? new byte[0] : value.getBytes(UTF8);
        buf.writeInt(data.length);
        if (data.length > 0) {
            buf.writeBytes(data);
        }
    }

    public static class Handler implements IMessageHandler<MsgSetChatBubblesConfig, IMessage> {
        @Override
        public IMessage onMessage(MsgSetChatBubblesConfig message, MessageContext ctx) {
            try {
                ModConfig.applyChatBubblesConfigValue(message.setting, message.value, true);
            } catch (IllegalArgumentException ignored) {
            }
            return null;
        }
    }
}
