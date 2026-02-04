package com.voidsrift.riftflux.vortex.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.container.InventoryBackpack;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;

public class CraftingEventHandler {
   @SubscribeEvent
   public void onAnvilUpdate(AnvilUpdateEvent event) {
      ItemStack leftInput = event.left;
      ItemStack rightInput = event.right;
      // Apply Quark Glint Runes to any item via anvil. (Original OA limited this to enchanted
      // items, but RiftFlux expects it to work for any item.)
      if (leftInput != null && rightInput != null && rightInput.getItem() == ModItems.glintRune) {
         ItemStack output = leftInput.copy();
         if (output.getTagCompound() == null) {
            output.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
         }
         output.getTagCompound().setInteger("customGlint", rightInput.getItemDamage());
         event.output = output;
         event.cost = 15;
         event.materialCost = 1;
      }

   }

   @SubscribeEvent
   public void onAnvilRepair(AnvilRepairEvent event) {
      EntityPlayer player = event.entityPlayer;
      ItemStack leftInput = event.left;
      ItemStack rightInput = event.right;
      if (!player.worldObj.isRemote && leftInput != null && rightInput != null && leftInput.getItem() == ModItems.backpack) {
         InventoryBackpack inventoryBackpack = ContainerHelper.getBackpackInventory(leftInput);

         for(int i = 0; i < inventoryBackpack.getSizeInventory(); ++i) {
            ItemStack itemStack = inventoryBackpack.getStackInSlot(i);
            if (itemStack != null) {
               player.entityDropItem(itemStack.copy(), 0.0F);
               inventoryBackpack.setInventorySlotContents(i, (ItemStack)null);
            }
         }
      }

   }
}
