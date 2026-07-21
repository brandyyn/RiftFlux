package com.voidsrift.riftflux.gravestone;

import gravestone.block.enums.EnumGraves;
import gravestone.block.enums.EnumMemorials;
import gravestone.item.ItemBlockGSGraveStone;
import gravestone.item.ItemBlockGSMemorial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class GravestoneTypeGrouping {
    private static final String[] MATERIAL_PREFIXES = {
            "WOODEN_", "SANDSTONE_", "STONE_", "MOSSY_", "IRON_", "GOLDEN_", "DIAMOND_",
            "EMERALD_", "LAPIS_", "REDSTONE_", "OBSIDIAN_", "QUARTZ_", "ICE_"
    };

    private GravestoneTypeGrouping() {
    }

    public static String getFamily(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }
        int type = getType(stack);
        if (stack.getItem() instanceof ItemBlockGSGraveStone) {
            EnumGraves[] values = EnumGraves.values();
            return normalize(values[type >= 0 && type < values.length ? type : 0].name());
        }
        if (stack.getItem() instanceof ItemBlockGSMemorial) {
            EnumMemorials[] values = EnumMemorials.values();
            return normalize(values[type >= 0 && type < values.length ? type : 0].name());
        }
        return null;
    }

    public static boolean areSameFamily(ItemStack first, ItemStack second) {
        if (first == null || second == null || first.getItem() != second.getItem()) {
            return false;
        }
        String firstFamily = getFamily(first);
        return firstFamily != null && firstFamily.equals(getFamily(second));
    }

    private static int getType(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("GraveType") ? tag.getByte("GraveType") : 0;
    }

    private static String normalize(String enumName) {
        for (String prefix : MATERIAL_PREFIXES) {
            if (enumName.startsWith(prefix)) {
                return enumName.substring(prefix.length());
            }
        }
        return enumName;
    }
}
