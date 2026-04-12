/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 */
package tk.nukeduck.hearts.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityHeartCrystal
extends TileEntity {
    private boolean naturallyGenerated;

    public boolean canUpdate() {
        return false;
    }

    public boolean isNaturallyGenerated() {
        return this.naturallyGenerated;
    }

    public void setNaturallyGenerated(boolean naturallyGenerated) {
        this.naturallyGenerated = naturallyGenerated;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.naturallyGenerated = tag.getBoolean("NaturallyGenerated");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("NaturallyGenerated", this.naturallyGenerated);
    }
}
