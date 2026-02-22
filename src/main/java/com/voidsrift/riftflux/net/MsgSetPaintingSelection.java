package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.painting.PaintingSelectionData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class MsgSetPaintingSelection implements IMessage {

    private String motive;

    public MsgSetPaintingSelection() {}

    public MsgSetPaintingSelection(String motive) {
        this.motive = motive;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.motive = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.motive == null ? "" : this.motive);
    }

    public static class Handler implements IMessageHandler<MsgSetPaintingSelection, IMessage> {
        @Override
        public IMessage onMessage(MsgSetPaintingSelection msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                return null;
            }

            String motive = msg.motive;
            if (motive != null) {
                motive = motive.trim();
                if (motive.isEmpty()) {
                    motive = null;
                }
            }

            PaintingSelectionData.setSelectedMotive(player, motive);
            return null;
        }
    }
}
