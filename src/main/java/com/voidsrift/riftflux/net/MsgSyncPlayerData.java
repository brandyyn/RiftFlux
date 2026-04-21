package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.net.sync.IPlayerSyncData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MsgSyncPlayerData implements IMessage {
    private int playerEntityId;
    private String syncKey;
    private NBTTagCompound tag;

    public MsgSyncPlayerData() {
    }

    public MsgSyncPlayerData(int playerEntityId, String syncKey, NBTTagCompound tag) {
        this.playerEntityId = playerEntityId;
        this.syncKey = syncKey;
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerEntityId = buf.readInt();
        this.syncKey = ByteBufUtils.readUTF8String(buf);
        this.tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.playerEntityId);
        ByteBufUtils.writeUTF8String(buf, this.syncKey == null ? "" : this.syncKey);
        ByteBufUtils.writeTag(buf, this.tag);
    }

    public static class Handler implements IMessageHandler<MsgSyncPlayerData, IMessage> {
        @Override
        public IMessage onMessage(MsgSyncPlayerData message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) {
                return null;
            }
            World world = mc.theWorld;
            if (world == null) {
                return null;
            }
            Entity entity = world.getEntityByID(message.playerEntityId);
            if (!(entity instanceof EntityPlayer) || message.syncKey == null || message.syncKey.isEmpty()) {
                return null;
            }
            Object properties = ((EntityPlayer) entity).getExtendedProperties(message.syncKey);
            if (properties instanceof IPlayerSyncData && message.tag != null) {
                ((IPlayerSyncData) properties).rf$readSyncData(message.tag);
            }
            return null;
        }
    }
}
