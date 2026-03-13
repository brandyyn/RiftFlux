/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 */
package net.nmccoy.legendgear.ritual;

import net.minecraft.enchantment.Enchantment;

public class EnchantBoon {
    public int level;
    public Enchantment enchantment;

    public EnchantBoon(Enchantment enchantment, int level) {
        this.level = level;
        this.enchantment = enchantment;
    }
}

