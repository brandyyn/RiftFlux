package com.voidsrift.riftflux.vortex.lib.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;

public class ContainerBackpack extends ContainerPlayer {
   public final InventoryBackpack inventoryBackpack;
   private final EntityPlayer player;
   private static final Random GUI_ID_RANDOM = new Random();

   public ContainerBackpack(final EntityPlayer player) {
      super(player.inventory, !player.worldObj.isRemote, player);
      this.player = player;
      ItemStack backpack = player.getCurrentArmor(2);
      this.inventoryBackpack = ContainerHelper.getBackpackInventory(backpack);
      if (!player.worldObj.isRemote) {
         this.ensureBackpackGuiId(backpack);
      }

      // Backpack inventory at the bottom (extension; vanilla inventory unchanged)
      for (int row = 0; row < 3; ++row) {
         for (int col = 0; col < 9; ++col) {
            final int slot = col + row * 9;
            this.addSlotToContainer(new Slot(this.inventoryBackpack, slot, 8 + col * 18, 166 + row * 18) {
               @Override
               public boolean isItemValid(ItemStack itemStack) {
                  return itemStack != null && itemStack.getItem() != ModItems.backpack;
               }
            });
         }
      }

      this.onCraftMatrixChanged(this.craftMatrix);
   }

   public void detectAndSendChanges() {
      boolean changed = false;

      for(int i = 0; i < this.inventorySlots.size(); ++i) {
         ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
         ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);
         if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
            itemstack1 = itemstack == null ? null : itemstack.copy();
            this.inventoryItemStacks.set(i, itemstack1);

            for(int j = 0; j < this.crafters.size(); ++j) {
               ((ICrafting)this.crafters.get(j)).sendSlotContents(this, i, itemstack1);
            }

            changed = true;
         }
      }

      if (changed && this.player instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.player).sendContainerToPlayer(this.player.inventoryContainer);
      }

   }

   private void ensureBackpackGuiId(ItemStack itemStack) {
      NBTTagCompound nbttagcompound = itemStack.getTagCompound();
      if (nbttagcompound == null) {
         nbttagcompound = new NBTTagCompound();
         itemStack.setTagCompound(nbttagcompound);
      }

      if (!nbttagcompound.hasKey("backpackGuiId", 3)) {
         int id = GUI_ID_RANDOM.nextInt();
         nbttagcompound.setInteger("backpackGuiId", id);
         if (this.player instanceof EntityPlayerMP) {
            try {
               EntityPlayerMP mp = (EntityPlayerMP)this.player;
               com.voidsrift.riftflux.vortex.network.ModPackets.instance
                       .sendTo(new com.voidsrift.riftflux.vortex.network.PacketBackpackSync(this.player, id), mp);
            } catch (Throwable ignored) {
            }
         }
      }
   }
}
