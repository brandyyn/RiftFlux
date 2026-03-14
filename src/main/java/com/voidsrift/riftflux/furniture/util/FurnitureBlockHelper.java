package com.voidsrift.riftflux.furniture.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public final class FurnitureBlockHelper {
    public static final int CURTAIN_LEFT = 0;
    public static final int CURTAIN_RIGHT = 1;

    private FurnitureBlockHelper() {
    }

    public static int getRotationMeta(EntityLivingBase entity) {
        return MathHelper.floor_double((entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
    }

    public static int[] getCurtainOffset(int rotation, int side) {
        switch (rotation & 3) {
            case 0:
                return side == CURTAIN_LEFT ? new int[] { 1, 0 } : new int[] { -1, 0 };
            case 1:
                return side == CURTAIN_LEFT ? new int[] { 0, 1 } : new int[] { 0, -1 };
            case 2:
                return side == CURTAIN_LEFT ? new int[] { -1, 0 } : new int[] { 1, 0 };
            case 3:
                return side == CURTAIN_LEFT ? new int[] { 0, -1 } : new int[] { 0, 1 };
            default:
                return side == CURTAIN_LEFT ? new int[] { 1, 0 } : new int[] { -1, 0 };
        }
    }
}
