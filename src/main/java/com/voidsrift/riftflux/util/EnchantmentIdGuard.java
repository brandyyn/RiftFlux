package com.voidsrift.riftflux.util;

import net.minecraft.enchantment.Enchantment;

public final class EnchantmentIdGuard {
    private EnchantmentIdGuard() {
    }

    public static void requireFree(int id, String displayName, String configKey) {
        String conflict = describeConflict(id, displayName, configKey);
        if (conflict != null) {
            throw new IllegalStateException(conflict);
        }
    }

    public static String describeConflict(int id, String displayName, String configKey) {
        if (id < 0 || id >= Enchantment.enchantmentsList.length) {
            return "Invalid enchantment ID for " + displayName + ": " + id
                    + " (config: " + configKey + "). Valid range is 0-"
                    + (Enchantment.enchantmentsList.length - 1) + ".";
        }

        Enchantment existing = Enchantment.enchantmentsList[id];
        if (existing == null) {
            return null;
        }

        return "Enchantment ID conflict for " + displayName + ": id " + id
                + " (config: " + configKey + ") is already used by "
                + describeEnchantment(existing) + ". Pick a free enchantment id"
                + suggestedFreeIdSuffix() + ".";
    }

    private static String describeEnchantment(Enchantment enchantment) {
        StringBuilder result = new StringBuilder(enchantment.getClass().getName());
        String name = getEnchantmentName(enchantment);
        if (name != null && name.length() > 0) {
            result.append(" (name: ").append(name).append(")");
        }
        return result.toString();
    }

    private static String getEnchantmentName(Enchantment enchantment) {
        try {
            return enchantment.getName();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String suggestedFreeIdSuffix() {
        for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
            if (Enchantment.enchantmentsList[i] == null) {
                return "; suggested free id: " + i;
            }
        }
        return "";
    }
}
