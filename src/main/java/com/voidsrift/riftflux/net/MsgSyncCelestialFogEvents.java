package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MsgSyncCelestialFogEvents implements IMessage {
    private int dimensionId;
    private boolean dayFog;
    private boolean nightFog;
    private boolean weatherFog;

    public MsgSyncCelestialFogEvents() {
    }

    public MsgSyncCelestialFogEvents(int dimensionId, boolean dayFog, boolean nightFog, boolean weatherFog) {
        this.dimensionId = dimensionId;
        this.dayFog = dayFog;
        this.nightFog = nightFog;
        this.weatherFog = weatherFog;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dimensionId = buf.readInt();
        this.dayFog = buf.readBoolean();
        this.nightFog = buf.readBoolean();
        this.weatherFog = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dimensionId);
        buf.writeBoolean(this.dayFog);
        buf.writeBoolean(this.nightFog);
        buf.writeBoolean(this.weatherFog);
    }

    public static class Handler implements IMessageHandler<MsgSyncCelestialFogEvents, IMessage> {
        @Override
        public IMessage onMessage(MsgSyncCelestialFogEvents message, MessageContext ctx) {
            if (riftflux.proxy != null) {
                riftflux.proxy.applyCelestialFogEventSync(
                        message.dimensionId,
                        message.dayFog,
                        message.nightFog,
                        message.weatherFog
                );
            }
            return null;
        }
    }
}
