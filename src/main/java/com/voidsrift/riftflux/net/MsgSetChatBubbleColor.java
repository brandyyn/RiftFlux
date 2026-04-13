package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.chatbubbles.ChatBubbleColorManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class MsgSetChatBubbleColor implements IMessage {
    private boolean hasColor;
    private int color;

    public MsgSetChatBubbleColor() {
    }

    public MsgSetChatBubbleColor(boolean hasColor, int color) {
        this.hasColor = hasColor;
        this.color = color & 0xFFFFFF;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hasColor = buf.readBoolean();
        color = buf.readInt() & 0xFFFFFF;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(hasColor);
        buf.writeInt(color & 0xFFFFFF);
    }

    public static class Handler implements IMessageHandler<MsgSetChatBubbleColor, IMessage> {
        @Override
        public IMessage onMessage(MsgSetChatBubbleColor message, MessageContext ctx) {
            if (ctx == null || ctx.getServerHandler() == null) {
                return null;
            }
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (message.hasColor) {
                ChatBubbleColorManager.setServerColor(player, message.color);
            } else {
                ChatBubbleColorManager.clearServerColor(player);
            }
            return null;
        }
    }
}
