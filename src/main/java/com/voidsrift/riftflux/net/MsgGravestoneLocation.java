package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.gravestone.GravestoneWaypointTracker;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class MsgGravestoneLocation implements IMessage {
    private int x;
    private int y;
    private int z;
    private int dimensionId;

    public MsgGravestoneLocation() {
    }

    public MsgGravestoneLocation(int x, int y, int z, int dimensionId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimensionId = dimensionId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.dimensionId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.dimensionId);
    }

    public static class Handler implements IMessageHandler<MsgGravestoneLocation, IMessage> {
        @Override
        public IMessage onMessage(MsgGravestoneLocation message, MessageContext ctx) {
            GravestoneWaypointTracker.receive(message.x, message.y, message.z, message.dimensionId);
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            GravestoneWaypointTracker.createPendingXaeroDeathpoint(player);
            return null;
        }
    }
}
