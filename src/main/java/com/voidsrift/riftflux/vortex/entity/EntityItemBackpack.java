package com.voidsrift.riftflux.vortex.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.vortex.lib.container.InventoryBackpack;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;

public class EntityItemBackpack extends EntityItem {
   private InventoryBackpack inventoryBackpack;
   private boolean hasDroppedContents = false;

   public EntityItemBackpack(World world) {
      super(world);
   }

   public EntityItemBackpack(World world, double posX, double posY, double posZ, ItemStack itemStack) {
      super(world, posX, posY, posZ, itemStack);
      this.inventoryBackpack = ContainerHelper.getBackpackInventory(itemStack);
   }

   public void onUpdate() {
      // Only drop contents once, not every tick
      if (!this.worldObj.isRemote && !ModConfig.backpackStorage && !hasDroppedContents) {
         if (this.inventoryBackpack == null) {
            this.inventoryBackpack = ContainerHelper.getBackpackInventory(this.getEntityItem());
         }

         for(int i = 0; i < this.inventoryBackpack.getSizeInventory(); ++i) {
            ItemStack itemStack = this.inventoryBackpack.getStackInSlot(i);
            if (itemStack != null) {
               this.entityDropItem(itemStack.copy(), 0.0F);
               this.inventoryBackpack.setInventorySlotContents(i, (ItemStack)null);
            }
         }
         hasDroppedContents = true;
      }

      super.onUpdate();
   }

   public boolean isEmpty() {
      return this.inventoryBackpack.isEmpty();
   }
}
