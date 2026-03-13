/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 */
package net.nmccoy.legendgear.block;

import net.minecraft.tileentity.TileEntity;

public class TileEntityStarglass
extends TileEntity {
    public double getMaxRenderDistanceSquared() {
        return 65536.0;
    }

    public boolean canUpdate() {
        return false;
    }
}

