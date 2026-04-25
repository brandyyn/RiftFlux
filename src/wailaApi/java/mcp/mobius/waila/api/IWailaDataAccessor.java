package mcp.mobius.waila.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public interface IWailaDataAccessor {
    TileEntity getTileEntity();

    NBTTagCompound getNBTData();
}
