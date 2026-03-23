package com.voidsrift.riftflux.axolotl;

import java.util.Locale;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class AxolotlFishUtil {
    private AxolotlFishUtil() {
    }

    public static boolean isFish(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        if (stack.getItem() == Items.fish) {
            return true;
        }

        int[] oreIds = OreDictionary.getOreIDs(stack);
        for (int oreId : oreIds) {
            String oreName = OreDictionary.getOreName(oreId);
            if (oreName != null && oreName.toLowerCase(Locale.ROOT).contains("fish")) {
                return true;
            }
        }
        return false;
    }
}
