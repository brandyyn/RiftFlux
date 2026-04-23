package net.nmccoy.legendgear.legacy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

public class EnchantmentFocus extends Enchantment {
    public EnchantmentFocus(int id, int rarity) {
        super(id, rarity, EnumEnchantmentType.weapon);
        this.setName("focus");
    }

    @Override
    public int getMinEnchantability(int level) {
        return 5 + 20 * (level - 1);
    }

    @Override
    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}
