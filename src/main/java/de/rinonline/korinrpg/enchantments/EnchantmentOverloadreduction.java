/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnumEnchantmentType
 */
package de.rinonline.korinrpg.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

public class EnchantmentOverloadreduction
extends Enchantment {
    public EnchantmentOverloadreduction(int id, int rarity) {
        super(id, rarity, EnumEnchantmentType.armor_feet);
        this.setName("Overloadreduction");
    }

    public int getMinEnchantability(int par1) {
        return 5 + (par1 - 1) * 10;
    }

    public int getMaxEnchantability(int par1) {
        return this.getMinEnchantability(par1) + 20;
    }

    public int getMaxLevel() {
        return 3;
    }
}

