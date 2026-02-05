package com.voidsrift.riftflux.vortex.network;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.container.InventoryToolbelt;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;

public class PacketToolbeltSwap implements IMessage, IMessageHandler<PacketToolbeltSwap, IMessage> {
   private int dim;
   private int playerID;
   private ItemStack toolbeltItem;
   private int mode;
   private int id;

   private static boolean sameStack(ItemStack a, ItemStack b) {
      if (a == b) return true;
      if (a == null || b == null) return false;
      if (a.getItem() != b.getItem()) return false;
      if (a.getItemDamage() != b.getItemDamage()) return false;
      return ItemStack.areItemStackTagsEqual(a, b);
   }

   private static int findMatchingSlot(InventoryToolbelt toolbelt, ItemStack target) {
      if (toolbelt == null || target == null) return -1;
      for (int i = 0; i < toolbelt.getSizeInventory(); i++) {
         ItemStack slot = toolbelt.getStackInSlot(i);
         if (sameStack(slot, target)) {
            return i;
         }
      }
      return -1;
   }

   public PacketToolbeltSwap() {
   }

   public PacketToolbeltSwap(EntityPlayer player, int mode, int id, ItemStack toolbeltItem) {
      this.dim = player.worldObj.provider.dimensionId;
      this.playerID = player.getEntityId();
      this.mode = mode;
      this.id = id;
      this.toolbeltItem = toolbeltItem;
   }

   public void fromBytes(ByteBuf buf) {
      this.dim = buf.readInt();
      this.playerID = buf.readInt();
      this.mode = buf.readInt();
      this.id = buf.readInt();
      this.toolbeltItem = ByteBufUtils.readItemStack(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.dim);
      buf.writeInt(this.playerID);
      buf.writeInt(this.mode);
      buf.writeInt(this.id);
      ByteBufUtils.writeItemStack(buf, this.toolbeltItem);
   }

   public IMessage onMessage(PacketToolbeltSwap message, MessageContext ctx) {
      World world = DimensionManager.getWorld(message.dim);
      if (world != null && (ctx.getServerHandler().playerEntity == null || ctx.getServerHandler().playerEntity.getEntityId() == message.playerID)) {
         Entity entity = world.getEntityByID(message.playerID);
         if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!ItemHelper.hasBauble(player, ModItems.toolbelt)) {
               return null;
            }

            InventoryToolbelt toolbelt = ContainerHelper.getToolbeltInventory(BaublesApi.getBaubles(player).getStackInSlot(3));
            boolean changed = false;
            int slotId = message.id;
            if (slotId < 0 || slotId >= toolbelt.getSizeInventory()) {
               slotId = -1;
            }
            if (message.toolbeltItem != null) {
               if (slotId < 0 || !sameStack(toolbelt.getStackInSlot(slotId), message.toolbeltItem)) {
                  int found = findMatchingSlot(toolbelt, message.toolbeltItem);
                  if (found >= 0) {
                     slotId = found;
                  }
               }
            }
            if (slotId < 0 || slotId >= toolbelt.getSizeInventory()) {
               return null;
            }
            switch(message.mode) {
            case 0: { // withdraw
               ItemStack slotStack = toolbelt.getStackInSlot(slotId);
               if (player.inventory.getCurrentItem() == null && slotStack != null) {
                  toolbelt.setInventorySlotContents(slotId, null);
                  player.inventory.setInventorySlotContents(player.inventory.currentItem, slotStack);
                  changed = true;
               }
               break;
            }
            case 1: { // insert into first empty slot
               ItemStack held = player.inventory.getCurrentItem();
               if (held != null && ContainerHelper.toolbeltValid(held)) {
                  for (int i = 0; i < toolbelt.getSizeInventory(); ++i) {
                     if (toolbelt.getStackInSlot(i) == null) {
                        toolbelt.setInventorySlotContents(i, held);
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        changed = true;
                        break;
                     }
                  }
               }
               break;
            }
            case 2: { // swap
               ItemStack slotStack = toolbelt.getStackInSlot(slotId);
               ItemStack held = player.inventory.getCurrentItem();
               if (held != null && !ContainerHelper.toolbeltValid(held)) {
                  break;
               }
               if (slotStack == null) {
                  if (held != null) {
                     toolbelt.setInventorySlotContents(slotId, held);
                     player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                     changed = true;
                  }
               } else {
                  toolbelt.setInventorySlotContents(slotId, held);
                  player.inventory.setInventorySlotContents(player.inventory.currentItem, slotStack);
                  changed = true;
               }
               break;
            }
            default:
               break;
            }

            if (changed) {
               player.inventory.markDirty();
               toolbelt.markDirty();
	               try {
	                  // Force client sync of the updated bauble stack NBT.
	                  if (player instanceof EntityPlayerMP) {
	                     EntityPlayerMP mp = (EntityPlayerMP) player;
	                     ItemStack tb = BaublesApi.getBaubles(player).getStackInSlot(3);
	                     ModPackets.instance.sendTo(new PacketToolbeltSync(player, tb), mp);
                        try {
                           BaublesApi.getBaubles(player).markDirty();
                        } catch (Throwable ignored) {
                        }
	                  }
	               } catch (Throwable ignored) {
	               }

               if (player instanceof EntityPlayerMP) {
                  EntityPlayerMP mp = (EntityPlayerMP) player;
                  mp.inventoryContainer.detectAndSendChanges();
                  mp.openContainer.detectAndSendChanges();
                  try {
                     mp.sendContainerToPlayer(mp.inventoryContainer);
                  } catch (Throwable ignored) {
                  }
                  try {
                     int hotbarSlot = 36 + mp.inventory.currentItem;
                     ItemStack held = mp.inventory.getCurrentItem();
                     mp.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, hotbarSlot, held));
                  } catch (Throwable ignored) {
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
