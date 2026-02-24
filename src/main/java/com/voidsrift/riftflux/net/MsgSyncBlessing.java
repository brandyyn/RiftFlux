package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.blessings.BlessingHelper;
import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class MsgSyncBlessing implements IMessage {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private String blessing;
    private boolean hasSource;
    private int sourceX;
    private int sourceY;
    private int sourceZ;
    private int sourceDim;

    public MsgSyncBlessing() {
    }

    public MsgSyncBlessing(String blessing) {
        this.blessing = blessing == null ? "" : blessing;
        this.hasSource = false;
    }

    public MsgSyncBlessing(net.minecraft.entity.player.EntityPlayer player) {
        if (player == null) {
            this.blessing = "";
            this.hasSource = false;
            return;
        }
        String b = BlessingHelper.getBlessing(player);
        this.blessing = b == null ? "" : b;
        this.hasSource = BlessingHelper.hasBlessingSource(player);
        if (this.hasSource) {
            this.sourceX = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_X);
            this.sourceY = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_Y);
            this.sourceZ = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_Z);
            this.sourceDim = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_DIM);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int len = buf.readInt();
        if (len <= 0) {
            blessing = "";
        } else {
            byte[] data = new byte[len];
            buf.readBytes(data);
            blessing = new String(data, UTF8);
        }
        hasSource = buf.readBoolean();
        if (hasSource) {
            sourceX = buf.readInt();
            sourceY = buf.readInt();
            sourceZ = buf.readInt();
            sourceDim = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] data = blessing == null ? new byte[0] : blessing.getBytes(UTF8);
        buf.writeInt(data.length);
        if (data.length > 0) {
            buf.writeBytes(data);
        }
        buf.writeBoolean(hasSource);
        if (hasSource) {
            buf.writeInt(sourceX);
            buf.writeInt(sourceY);
            buf.writeInt(sourceZ);
            buf.writeInt(sourceDim);
        }
    }

    public static class Handler implements IMessageHandler<MsgSyncBlessing, IMessage> {
        @Override
        public IMessage onMessage(MsgSyncBlessing msg, MessageContext ctx) {
            if (riftflux.proxy != null) {
                riftflux.proxy.applyBlessingSync(msg.blessing, msg.hasSource, msg.sourceX, msg.sourceY, msg.sourceZ, msg.sourceDim);
            }
            return null;
        }
    }
}
