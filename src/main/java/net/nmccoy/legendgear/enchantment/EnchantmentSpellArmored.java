/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnumEnchantmentType
 *  net.minecraft.item.ItemStack
 */
package net.nmccoy.legendgear.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.nmccoy.legendgear.item.spell.SpellItem;

public class EnchantmentSpellArmored
extends Enchantment {
    public EnchantmentSpellArmored(int id, int rarity) {
        super(id, rarity, EnumEnchantmentType.all);
        this.setName("spellArmored");
    }

    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof SpellItem;
    }

    public int getMaxLevel() {
        return 3;
    }

    public int getMinEnchantability(int level) {
        return 10 + (level - 1) * 20;
    }

    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 30;
    }
}

