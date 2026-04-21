package com.voidsrift.riftflux.net.sync;

import net.minecraft.nbt.NBTTagCompound;

public interface IPlayerSyncData {
    String rf$getSyncKey();

    void rf$writeSyncData(NBTTagCompound tag);

    void rf$readSyncData(NBTTagCompound tag);
}
