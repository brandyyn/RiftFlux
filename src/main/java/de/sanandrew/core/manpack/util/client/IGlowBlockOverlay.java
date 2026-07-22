/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@SideOnly(value=Side.CLIENT)
public interface IGlowBlockOverlay {
    @SideOnly(value=Side.CLIENT)
    public IIcon getOverlayTexture(IBlockAccess var1, int var2, int var3, int var4, int var5);

    @SideOnly(value=Side.CLIENT)
    public IIcon getOverlayInvTexture(int var1, int var2);
}

