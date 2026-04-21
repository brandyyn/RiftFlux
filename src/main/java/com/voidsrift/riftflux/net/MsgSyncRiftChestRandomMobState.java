package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.mixinhooks.IRiftChestRandomMobState;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class MsgSyncRiftChestRandomMobState implements IMessage {
    private int entityId;
    private int initialX;
    private int initialY;
    private int initialZ;
    private int biomeId;
    private int randomSeed;

    public MsgSyncRiftChestRandomMobState() {
    }

    public MsgSyncRiftChestRandomMobState(int entityId, int initialX, int initialY, int initialZ, int biomeId, int randomSeed) {
        this.entityId = entityId;
        this.initialX = initialX;
        this.initialY = initialY;
        this.initialZ = initialZ;
        this.biomeId = biomeId;
        this.randomSeed = randomSeed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.initialX = buf.readInt();
        this.initialY = buf.readInt();
        this.initialZ = buf.readInt();
        this.biomeId = buf.readInt();
        this.randomSeed = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.initialX);
        buf.writeInt(this.initialY);
        buf.writeInt(this.initialZ);
        buf.writeInt(this.biomeId);
        buf.writeInt(this.randomSeed);
    }

    public static class Handler implements IMessageHandler<MsgSyncRiftChestRandomMobState, IMessage> {
        @Override
        public IMessage onMessage(MsgSyncRiftChestRandomMobState message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) {
                return null;
            }
            World world = mc.theWorld;
            if (world == null) {
                return null;
            }
            Entity entity = world.getEntityByID(message.entityId);
            if (entity instanceof IRiftChestRandomMobState) {
                ((IRiftChestRandomMobState) entity).rf$setRiftChestRandomMobState(
                        message.initialX,
                        message.initialY,
                        message.initialZ,
                        message.biomeId,
                        message.randomSeed
                );
            }
            return null;
        }
    }
}
