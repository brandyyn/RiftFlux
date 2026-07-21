package gravestone.tileentity;

import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGSAltar extends TileEntity implements IInventory {
   private ItemStack corpse = null;

   public boolean hasCorpse() {
      return this.corpse != null;
   }

   public ItemStack getCorpse() {
      return this.corpse;
   }

   public void setCorpse(ItemStack corpse) {
      this.corpse = corpse;
   }

   public void dropCorpse() {
      if (this.corpse != null) {
         Random random = new Random();
         float x = random.nextFloat() * 0.8F + 0.1F;
         float y = random.nextFloat() * 0.8F + 1.1F;

         EntityItem entityItem;
         for(float z = random.nextFloat() * 0.8F + 0.1F; this.corpse.stackSize > 0; this.worldObj.spawnEntityInWorld(entityItem)) {
            int stackSize = random.nextInt(21) + 10;
            if (stackSize > this.corpse.stackSize) {
               stackSize = this.corpse.stackSize;
            }

            this.corpse.stackSize -= stackSize;
            entityItem = new EntityItem(this.worldObj, (double)((float)this.xCoord + x), (double)((float)this.yCoord + y), (double)((float)this.zCoord + z), new ItemStack(this.corpse.getItem(), stackSize, this.corpse.getItemDamage()));
            entityItem.motionX = random.nextGaussian() * 0.05D;
            entityItem.motionY = random.nextGaussian() * 0.15D;
            entityItem.motionZ = random.nextGaussian() * 0.05D;
            if (this.corpse.hasTagCompound()) {
               entityItem.getEntityItem().setTagCompound((NBTTagCompound)this.corpse.getTagCompound().copy());
            }
         }

         this.corpse = null;
      }

   }

   public void readFromNBT(NBTTagCompound nbtTag) {
      super.readFromNBT(nbtTag);
      if (nbtTag.hasKey("Corpse")) {
         this.corpse = ItemStack.loadItemStackFromNBT(nbtTag.getCompoundTag("Corpse"));
      }

   }

   public void writeToNBT(NBTTagCompound nbtTag) {
      super.writeToNBT(nbtTag);
      if (this.corpse != null) {
         NBTTagCompound swordNBT = new NBTTagCompound();
         this.corpse.writeToNBT(swordNBT);
         nbtTag.setTag("Corpse", swordNBT);
      }

   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
      this.readFromNBT(packet.func_148857_g());
   }

   public Packet getDescriptionPacket() {
      NBTTagCompound nbtTag = new NBTTagCompound();
      this.writeToNBT(nbtTag);
      return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
   }

   public String getInventoryName() {
      return "";
   }

   public boolean hasCustomInventoryName() {
      return false;
   }

   public void openInventory() {
   }

   public void closeInventory() {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      return false;
   }

   public int getSizeInventory() {
      return 1;
   }

   public ItemStack getStackInSlot(int slot) {
      return this.corpse;
   }

   public void setInventorySlotContents(int slot, ItemStack stack) {
      this.corpse = stack;
   }

   public ItemStack decrStackSize(int slot, int amt) {
      ItemStack stack = this.getStackInSlot(slot);
      if (stack != null) {
         if (stack.stackSize <= amt) {
            this.setInventorySlotContents(slot, (ItemStack)null);
         } else {
            stack = stack.splitStack(amt);
            if (stack.stackSize == 0) {
               this.setInventorySlotContents(slot, (ItemStack)null);
            }
         }
      }

      return stack;
   }

   public ItemStack getStackInSlotOnClosing(int slot) {
      ItemStack stack = this.getStackInSlot(slot);
      if (stack != null) {
         this.setInventorySlotContents(slot, (ItemStack)null);
      }

      return stack;
   }

   public int getInventoryStackLimit() {
      return 1;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) < 64.0D;
   }
}
