package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.pets.PetKnockdownTimeout;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class MsgSyncPetKnockdownTimeout implements IMessage {
    private int entityId;
    private int remainingTicks;

    public MsgSyncPetKnockdownTimeout() {
    }

    public MsgSyncPetKnockdownTimeout(int entityId, int remainingTicks) {
        this.entityId = entityId;
        this.remainingTicks = remainingTicks;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.remainingTicks = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.remainingTicks);
    }

    public static class Handler implements IMessageHandler<MsgSyncPetKnockdownTimeout, IMessage> {
        @Override
        public IMessage onMessage(MsgSyncPetKnockdownTimeout message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) {
                return null;
            }
            World world = mc.theWorld;
            if (world == null) {
                return null;
            }
            Entity entity = world.getEntityByID(message.entityId);
            if (entity instanceof EntityLivingBase) {
                PetKnockdownTimeout.applyClientSync((EntityLivingBase) entity, message.remainingTicks);
            }
            return null;
        }
    }
}
