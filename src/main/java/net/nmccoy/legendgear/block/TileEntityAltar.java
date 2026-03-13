/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.tileentity.TileEntity
 */
package net.nmccoy.legendgear.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAltar
extends TileEntity {
    public void updateEntity() {
        super.updateEntity();
        EntityPlayer entityplayer = this.worldObj.getClosestPlayer((double)((float)this.xCoord + 0.5f), (double)((float)this.yCoord + 0.5f), (double)((float)this.zCoord + 0.5f), 3.0);
        if (entityplayer != null) {
            // empty if block
        }
    }

    public int checkDedication() {
        return 1;
    }
}

