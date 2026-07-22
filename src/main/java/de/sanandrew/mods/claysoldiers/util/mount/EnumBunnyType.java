/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.mount;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public enum EnumBunnyType {
    BLACK(0x191919, 15, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/black.png")),
    RED(0xCC4646, 14, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/red.png")),
    GREEN(6717235, 13, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/green.png")),
    BROWN(6704179, 12, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/brown.png")),
    BLUE(3361970, 11, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/blue.png")),
    PURPLE(8339378, 10, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/purple.png")),
    CYAN(5013401, 9, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/cyan.png")),
    LIGHT_GRAY(0x999999, 8, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/light_gray.png")),
    GRAY(0x4C4C4C, 7, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/gray.png")),
    PINK(15892389, 6, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/pink.png")),
    LIME(8375321, 5, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/lime.png")),
    YELLOW(0xE5E533, 4, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/yellow.png")),
    LIGHT_BLUE(6724056, 3, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/light_blue.png")),
    MAGENTA(0xE000FF, 2, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/magenta.png")),
    ORANGE(14188339, 1, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/orange.png")),
    WHITE(0xFFFFFF, 0, new ResourceLocation("claysoldiers", "textures/entity/mount/bunny/white.png"));

    public static final EnumBunnyType[] VALUES;
    public final ResourceLocation texture;
    public final int typeColor;
    public final int woolMeta;

    private EnumBunnyType(int typeColor, int woolMeta, ResourceLocation texture) {
        this.texture = texture;
        this.typeColor = typeColor;
        this.woolMeta = woolMeta;
    }

    public static EnumBunnyType getTypeFromItem(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (SAPUtils.isIndexInRange((Object[])VALUES, stack.getItemDamage())) {
            return VALUES[stack.getItemDamage()];
        }
        return null;
    }

    static {
        VALUES = EnumBunnyType.values();
    }
}

