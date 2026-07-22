/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

@SideOnly(value=Side.CLIENT)
public class IconFlippedFixed
implements IIcon {
    private final IIcon baseIcon;
    private final boolean flipU;
    private final boolean flipV;

    public IconFlippedFixed(IIcon icon, boolean doFlipU, boolean doFlipV) {
        this.baseIcon = icon;
        this.flipU = doFlipU;
        this.flipV = doFlipV;
    }

    @Override
    public int getIconWidth() {
        return this.baseIcon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return this.baseIcon.getIconHeight();
    }

    @Override
    public float getMinU() {
        return this.flipU ? this.baseIcon.getMaxU() : this.baseIcon.getMinU();
    }

    @Override
    public float getMaxU() {
        return this.flipU ? this.baseIcon.getMinU() : this.baseIcon.getMaxU();
    }

    @Override
    public float getInterpolatedU(double par1) {
        float diffU = this.getMaxU() - this.getMinU();
        return this.getMinU() + diffU * ((float)par1 / 16.0f);
    }

    @Override
    public float getMinV() {
        return this.flipV ? this.baseIcon.getMaxV() : this.baseIcon.getMinV();
    }

    @Override
    public float getMaxV() {
        return this.flipV ? this.baseIcon.getMinV() : this.baseIcon.getMaxV();
    }

    @Override
    public float getInterpolatedV(double par1) {
        float diffV = this.getMaxV() - this.getMinV();
        return this.getMinV() + diffV * ((float)par1 / 16.0f);
    }

    @Override
    public String getIconName() {
        return this.baseIcon.getIconName();
    }
}

