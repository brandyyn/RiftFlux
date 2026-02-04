package com.voidsrift.riftflux.vortex.entity;

import baubles.api.BaublesApi;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;

public class EntityDeathRune extends Entity implements IInventory {
   public static final String invName = "Rune Contents";
   public static final int invSize = 44;
   private ItemStack[] inventory = new ItemStack[44];
   private UUID playerUuid;
   private String playerID;

   public EntityDeathRune(World world) {
      super(world);
      this.setSize(0.5F, 0.5F);
   }

   public EntityDeathRune(EntityPlayer player) {
      super(player.worldObj);
      this.setSize(0.5F, 0.5F);
      this.playerUuid = player.getPersistentID();
      this.playerID = this.playerUuid.toString();

      int i;
      ItemStack stack;
      for(i = 0; i < 40; ++i) {
         stack = player.inventory.getStackInSlot(i);
         if (stack != null) {
            this.inventory[i] = stack.copy();
            player.inventory.setInventorySlotContents(i, (ItemStack)null);
         }
      }

      for(i = 40; i < 44; ++i) {
         stack = BaublesApi.getBaubles(player).getStackInSlot(i - 40);
         if (stack != null) {
            this.inventory[i] = stack.copy();
            BaublesApi.getBaubles(player).setInventorySlotContents(i - 40, (ItemStack)null);
         }
      }

   }

   public void onCollideWithPlayer(EntityPlayer player) {
      if (!this.worldObj.isRemote) {
         if (this.playerUuid == null && this.playerID != null) {
            this.playerUuid = UUID.fromString(this.playerID);
         }

         if (player.isEntityAlive() && player.getPersistentID().equals(this.playerUuid)) {
            this.restockPlayer(player);
         }
      }

   }

   private void restockPlayer(EntityPlayer player) {
      int i;
      ItemStack stack;
      for(i = 0; i < 40; ++i) {
         stack = this.getStackInSlot(i);
         if (stack != null) {
            if (player.inventory.getStackInSlot(i) == null) {
               player.inventory.setInventorySlotContents(i, stack.copy());
            } else if (!player.inventory.addItemStackToInventory(stack.copy())) {
               player.entityDropItem(stack.copy(), 0.0F);
            }

            this.setInventorySlotContents(i, (ItemStack)null);
         }
      }

      for(i = 40; i < 44; ++i) {
         stack = this.getStackInSlot(i);
         if (stack != null) {
            if (BaublesApi.getBaubles(player).getStackInSlot(i - 40) == null) {
               BaublesApi.getBaubles(player).setInventorySlotContents(i - 40, stack.copy());
            } else if (!player.inventory.addItemStackToInventory(stack.copy())) {
               player.entityDropItem(stack.copy(), 0.0F);
            }
         }

         this.setInventorySlotContents(i, (ItemStack)null);
      }

      this.worldObj.playSoundAtEntity(this, "riftflux:craftstart", 1.0F, 1.0F);
      this.worldObj.playSoundAtEntity(this, "riftflux:hhon", 1.0F, 1.0F);
      this.worldObj.playSoundAtEntity(this, "riftflux:shock1", 1.0F, 1.0F);
      this.worldObj.playSoundAtEntity(this, "riftflux:heartbeat", 1.0F, 1.0F);
      Thaumcraft.proxy.burst(this.worldObj, this.posX, this.posY, this.posZ, 0.5F);
      this.setDead();
   }

   public int getSizeInventory() {
      return 44;
   }

   public ItemStack getStackInSlot(int slot) {
      return this.inventory[slot];
   }

   public ItemStack decrStackSize(int slot, int amount) {
      ItemStack stack = this.getStackInSlot(slot);
      if (stack != null) {
         if (stack.stackSize > amount) {
            stack = stack.splitStack(amount);
         } else {
            this.setInventorySlotContents(slot, (ItemStack)null);
         }
      }

      return stack;
   }

   public ItemStack getStackInSlotOnClosing(int slot) {
      return this.getStackInSlot(slot);
   }

   public void setInventorySlotContents(int slot, ItemStack stack) {
      this.inventory[slot] = stack;
   }

   public String getInventoryName() {
      return "Rune Contents";
   }

   public boolean hasCustomInventoryName() {
      return true;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      return false;
   }

   public void openInventory() {
   }

   public void closeInventory() {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      return true;
   }

   protected void entityInit() {
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
   }

   protected void readEntityFromNBT(NBTTagCompound tag) {
      this.playerID = tag.getString("PlayerID");
      if (this.playerID != null) {
         this.playerUuid = UUID.fromString(this.playerID);
      }

      NBTTagList items = tag.getTagList("Rune Contents", 10);

      for(int i = 0; i < items.tagCount(); ++i) {
         NBTTagCompound item = items.getCompoundTagAt(i);
         int slot = item.getInteger("Slot");
         if (slot >= 0 && slot < this.getSizeInventory()) {
            this.inventory[slot] = ItemStack.loadItemStackFromNBT(item);
         }
      }

   }

   protected void writeEntityToNBT(NBTTagCompound tag) {
      tag.setString("PlayerID", this.playerID);
      NBTTagList items = new NBTTagList();

      for(int i = 0; i < this.getSizeInventory(); ++i) {
         if (this.getStackInSlot(i) != null) {
            NBTTagCompound item = new NBTTagCompound();
            item.setInteger("Slot", i);
            this.getStackInSlot(i).writeToNBT(item);
            items.appendTag(item);
         }
      }

      tag.setTag("Rune Contents", items);
   }

   public boolean isInRangeToRenderDist(double dist) {
      double d1 = this.boundingBox.getAverageEdgeLength() * 128.0D * this.renderDistanceWeight;
      return dist < d1 * d1;
   }
}
