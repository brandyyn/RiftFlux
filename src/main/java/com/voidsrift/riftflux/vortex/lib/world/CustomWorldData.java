package com.voidsrift.riftflux.vortex.lib.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class CustomWorldData extends WorldSavedData {
   private NBTTagCompound data = new NBTTagCompound();

   public CustomWorldData(String p_i2141_1_) {
      super(p_i2141_1_);
   }

   public void readFromNBT(NBTTagCompound tag) {
      this.data = tag.getCompoundTag("riftflux_vortex");
   }

   public void writeToNBT(NBTTagCompound tag) {
      tag.setTag("riftflux", this.data);
   }

   public NBTTagCompound getData() {
      return this.data;
   }

   public void setData(NBTTagCompound tag) {
      this.data = tag;
      this.markDirty();
   }
}
