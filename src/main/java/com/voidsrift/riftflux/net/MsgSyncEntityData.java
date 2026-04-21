package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MsgSyncEntityData implements IMessage {
    private int entityId;
    private NBTTagCompound tag;

    public MsgSyncEntityData() {
    }

    public MsgSyncEntityData(int entityId, NBTTagCompound tag) {
        this.entityId = entityId;
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        ByteBufUtils.writeTag(buf, this.tag);
    }

    public static class Handler implements IMessageHandler<MsgSyncEntityData, IMessage> {
        @Override
        public IMessage onMessage(MsgSyncEntityData message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) {
                return null;
            }
            World world = mc.theWorld;
            if (world == null) {
                return null;
            }
            Entity entity = world.getEntityByID(message.entityId);
            if (entity instanceof IEntitySyncData && message.tag != null) {
                ((IEntitySyncData) entity).rf$readSyncData(message.tag);
            }
            return null;
        }
    }
}
