package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MsgSyncFenceOverrides implements IMessage {
    private boolean fullSync;
    private int dimensionId;
    private boolean enabled;
    private int[] coordinates;

    public MsgSyncFenceOverrides() {
    }

    private MsgSyncFenceOverrides(boolean fullSync, int dimensionId, boolean enabled, int[] coordinates) {
        this.fullSync = fullSync;
        this.dimensionId = dimensionId;
        this.enabled = enabled;
        this.coordinates = coordinates == null ? new int[0] : coordinates;
    }

    public static MsgSyncFenceOverrides full(int dimensionId, int[] coordinates) {
        return new MsgSyncFenceOverrides(true, dimensionId, true, coordinates);
    }

    public static MsgSyncFenceOverrides delta(int dimensionId, int x, int y, int z, boolean enabled) {
        return new MsgSyncFenceOverrides(false, dimensionId, enabled, new int[]{x, y, z});
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        fullSync = buf.readBoolean();
        dimensionId = buf.readInt();
        enabled = buf.readBoolean();
        int length = Math.max(0, buf.readInt());
        coordinates = new int[length];
        for (int i = 0; i < length; i++) {
            coordinates[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(fullSync);
        buf.writeInt(dimensionId);
        buf.writeBoolean(enabled);
        int length = coordinates == null ? 0 : coordinates.length;
        buf.writeInt(length);
        for (int i = 0; i < length; i++) {
            buf.writeInt(coordinates[i]);
        }
    }

    public static class Handler implements IMessageHandler<MsgSyncFenceOverrides, IMessage> {
        @Override
        public IMessage onMessage(MsgSyncFenceOverrides message, MessageContext ctx) {
            if (riftflux.proxy != null) {
                riftflux.proxy.applyFenceOverrideSync(message.fullSync, message.dimensionId, message.enabled, message.coordinates);
            }
            return null;
        }
    }
}
