package com.voidsrift.riftflux.net.sync;

import net.minecraft.nbt.NBTTagCompound;

public interface IEntitySyncData {
    void rf$writeSyncData(NBTTagCompound tag);

    void rf$readSyncData(NBTTagCompound tag);
}
