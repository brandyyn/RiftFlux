package com.voidsrift.riftflux.vortex.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import com.voidsrift.riftflux.vortex.lib.helper.WorldHelper;
import com.voidsrift.riftflux.vortex.lib.world.CustomWorldData;

public class PacketWorldDataSync implements IMessage, IMessageHandler<PacketWorldDataSync, IMessage> {
   private NBTTagCompound data;
   private boolean disableGui;

   public PacketWorldDataSync() {
   }

   public PacketWorldDataSync(NBTTagCompound tag) {
      this(tag, false);
   }

   public PacketWorldDataSync(NBTTagCompound tag, boolean disableGui) {
      this.data = tag;
      this.disableGui = disableGui;
   }

   public void fromBytes(ByteBuf buf) {
      this.data = ByteBufUtils.readTag(buf);
      this.disableGui = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeTag(buf, this.data);
      buf.writeBoolean(this.disableGui);
   }

   @SideOnly(Side.CLIENT)
   public IMessage onMessage(PacketWorldDataSync message, MessageContext ctx) {
      if (ctx.side == Side.CLIENT) {
         CustomWorldData worldData = WorldHelper.getGlobalCustomData(Minecraft.getMinecraft().theWorld);
         worldData.setData(message.data);
         if (message.disableGui && Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
         }
      }

      return null;
   }
}
