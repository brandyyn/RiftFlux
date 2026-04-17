package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MsgSyncCrossedPlantFacing implements IMessage {
    private int dimensionId;
    private int x;
    private int y;
    private int z;
    private byte facing;

    public MsgSyncCrossedPlantFacing() {
    }

    public MsgSyncCrossedPlantFacing(int dimensionId, int x, int y, int z, int facing) {
        this.dimensionId = dimensionId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = (byte) (facing & 3);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dimensionId = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.facing = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dimensionId);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.facing);
    }

    public static class Handler implements IMessageHandler<MsgSyncCrossedPlantFacing, IMessage> {
        @Override
        public IMessage onMessage(MsgSyncCrossedPlantFacing message, MessageContext ctx) {
            if (riftflux.proxy != null) {
                riftflux.proxy.applyCrossedPlantFacingSync(
                        message.dimensionId,
                        message.x,
                        message.y,
                        message.z,
                        message.facing & 3
                );
            }
            return null;
        }
    }
}
