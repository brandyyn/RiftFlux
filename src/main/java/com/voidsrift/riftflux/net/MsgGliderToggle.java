package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.avatar.glider.GliderState;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class MsgGliderToggle implements IMessage {
    private boolean gliding;
    private String name;

    public MsgGliderToggle() {
    }

    public MsgGliderToggle(boolean gliding, String name) {
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

    public static class Handler implements IMessageHandler<MsgGliderToggle, IMessage> {
        @Override
        public IMessage onMessage(MsgGliderToggle msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                return null;
            }
            String name = player.getDisplayName();
            if (msg.gliding) {
                GliderState.addGlidingPlayerName(name, true);
            } else {
                GliderState.removeGlidingPlayerName(name, true);
            }
            if (RFNetwork.CH != null) {
                RFNetwork.CH.sendToAll(new MsgGliderState(msg.gliding, name));
            }
            return null;
        }
    }
}
