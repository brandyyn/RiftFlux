/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

@SideOnly(value=Side.CLIENT)
public class IconParticle
implements IIcon {
    private final String iconName;
    private final int iconWidth;
    private final int iconHeight;
    private final float minU;
    private final float minV;
    private final float maxU;
    private final float maxV;

    public IconParticle(String iconName, int sheetWidth, int sheetHeight, int iconCoordU, int iconCoordV, int iconWidth, int iconHeight) {
        this.iconName = iconName;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        float ratioU = 1.0f / (float)sheetWidth;
        float ratioV = 1.0f / (float)sheetHeight;
        this.minU = ratioU * (float)iconCoordU;
        this.minV = ratioV * (float)iconCoordV;
        this.maxU = ratioU * (float)(iconCoordU + iconWidth);
        this.maxV = ratioV * (float)(iconCoordV + iconHeight);
    }

    @Override
    public int getIconWidth() {
        return this.iconWidth;
    }

    @Override
    public int getIconHeight() {
        return this.iconHeight;
    }

    @Override
    public float getMinU() {
        return this.minU;
    }

    @Override
    public float getMaxU() {
        return this.maxU;
    }

    @Override
    public float getInterpolatedU(double amount) {
        float deltaU = this.maxU - this.minU;
        return this.minU + deltaU * (float)amount / (float)this.iconWidth;
    }

    @Override
    public float getMinV() {
        return this.minV;
    }

    @Override
    public float getMaxV() {
        return this.maxV;
    }

    @Override
    public float getInterpolatedV(double amount) {
        float deltaV = this.maxV - this.minV;
        return this.minV + deltaV * (float)amount / (float)this.iconHeight;
    }

    @Override
    public String getIconName() {
        return this.iconName;
    }
}

