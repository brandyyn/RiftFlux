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

public class EnchantmentSpellSpread
extends Enchantment {
    public EnchantmentSpellSpread(int id, int rarity) {
        super(id, rarity, EnumEnchantmentType.all);
        this.setName("spellSpread");
    }

    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof SpellItem;
    }

    public int getMaxLevel() {
        return 4;
    }

    public int getMinEnchantability(int level) {
        return 1 + (level - 1) * 10;
    }

    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 16;
    }
}

