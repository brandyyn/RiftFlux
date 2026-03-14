package com.voidsrift.riftflux.furniture.tileentity;

import com.voidsrift.riftflux.Constants;

public class TileEntityBedsideCabinet extends AbstractTileEntityInventory {
    public TileEntityBedsideCabinet() {
        super(15, "Bedside Cabinet");
    }

    @Override
    public void openInventory() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.worldObj.playSoundEffect(
                    this.xCoord + 0.5D,
                    this.yCoord + 0.5D,
                    this.zCoord + 0.5D,
                    Constants.MODID + ":cabinetopen",
                    0.75F,
                    0.9F
            );
        }
    }

    @Override
    public void closeInventory() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.worldObj.playSoundEffect(
                    this.xCoord + 0.5D,
                    this.yCoord + 0.5D,
                    this.zCoord + 0.5D,
                    Constants.MODID + ":cabinetclose",
                    0.75F,
                    0.8F
            );
        }
    }
}
