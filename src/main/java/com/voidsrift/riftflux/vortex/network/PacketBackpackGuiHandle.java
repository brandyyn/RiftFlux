package com.voidsrift.riftflux.vortex.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.container.ContainerBackpack;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;

public class PacketBackpackGuiHandle implements IMessage, IMessageHandler<PacketBackpackGuiHandle, IMessage> {
   private int dim;
   private int playerID;
   private boolean open;

   public PacketBackpackGuiHandle() {
   }

   public PacketBackpackGuiHandle(EntityPlayer player, boolean open) {
      this.dim = player.worldObj.provider.dimensionId;
      this.playerID = player.getEntityId();
      this.open = open;
   }

   public void fromBytes(ByteBuf buf) {
      this.dim = buf.readInt();
      this.playerID = buf.readInt();
      this.open = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.dim);
      buf.writeInt(this.playerID);
      buf.writeBoolean(this.open);
   }

   public IMessage onMessage(PacketBackpackGuiHandle message, MessageContext ctx) {
      World world = DimensionManager.getWorld(message.dim);
      if (world != null && (ctx.getServerHandler().playerEntity == null || ctx.getServerHandler().playerEntity.getEntityId() == message.playerID)) {
         Entity entity = world.getEntityByID(message.playerID);
         if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (message.open) {
               if (ItemHelper.hasArmor(player, ModItems.backpack, 2)) {
                  try {
                     ItemStack armor = player.getCurrentArmor(2);
                     if (armor != null) {
                        NBTTagCompound tag = armor.getTagCompound();
                        if (tag == null) {
                           tag = new NBTTagCompound();
                           armor.setTagCompound(tag);
                        }
                        if (!tag.hasKey("backpackGuiId", 3)) {
                           int id = new java.util.Random().nextInt();
                           tag.setInteger("backpackGuiId", id);
                           if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
                              net.minecraft.entity.player.EntityPlayerMP mp = (net.minecraft.entity.player.EntityPlayerMP) player;
                              ModPackets.instance.sendTo(new PacketBackpackSync(player, id), mp);
                           }
                        }
                     }
                  } catch (Throwable ignored) {
                  }
                  player.openGui(riftflux.instance, 1, world, 0, 0, 0);
               }
            } else {
               if (player.openContainer instanceof ContainerBackpack) {
                  ContainerBackpack containerBackpack = (ContainerBackpack)player.openContainer;

                  for(int i = 0; i < 4; ++i) {
                     ItemStack itemStack = containerBackpack.craftMatrix.getStackInSlotOnClosing(i);
                     if (itemStack != null) {
                        player.dropPlayerItemWithRandomChoice(itemStack, false);
                     }
                  }

                  containerBackpack.craftResult.setInventorySlotContents(0, (ItemStack)null);
               }

               player.openContainer = player.inventoryContainer;
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
