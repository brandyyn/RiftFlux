package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MsgSyncRespawnDelay implements IMessage {
    private long remainingMs;

    public MsgSyncRespawnDelay() {
    }

    public MsgSyncRespawnDelay(long remainingMs) {
        this.remainingMs = Math.max(0L, remainingMs);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.remainingMs = Math.max(0L, buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(Math.max(0L, this.remainingMs));
    }

    public static class Handler implements IMessageHandler<MsgSyncRespawnDelay, IMessage> {
        @Override
        public IMessage onMessage(MsgSyncRespawnDelay message, MessageContext ctx) {
            if (riftflux.proxy != null) {
                riftflux.proxy.applyRespawnDelaySync(message.remainingMs);
            }
            return null;
        }
    }
}
