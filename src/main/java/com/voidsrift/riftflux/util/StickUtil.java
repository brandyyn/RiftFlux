package com.voidsrift.riftflux.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class StickUtil {

    private static final String ORE_STICK_WOOD = "stickWood";

    // Cache the registered ore stacks for performance (OreDictionary lists are mutable)
    private static List<ItemStack> cachedStickOres = null;
    private static int cachedStickOresSize = -1;

    private static List<ItemStack> getStickOresCached() {
        List<ItemStack> ores = OreDictionary.getOres(ORE_STICK_WOOD);
        if (cachedStickOres == null || cachedStickOresSize != ores.size()) {
            cachedStickOres = new ArrayList<ItemStack>(ores);
            cachedStickOresSize = ores.size();
        }
        return cachedStickOres;
    }

    public static boolean isStick(ItemStack stack) {
        if (stack == null) return false;

        // Most robust: match against the actual registered ore entries (handles wildcard/meta registrations)
        for (ItemStack oreStack : getStickOresCached()) {
            if (oreStack != null && OreDictionary.itemMatches(oreStack, stack, false)) {
                return true;
            }
        }

        // Fallback: direct ore ID lookup
        int[] ids = OreDictionary.getOreIDs(stack);
        for (int id : ids) {
            String name = OreDictionary.getOreName(id);
            if (ORE_STICK_WOOD.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
