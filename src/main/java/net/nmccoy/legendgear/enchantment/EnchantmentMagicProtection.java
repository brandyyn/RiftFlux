/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentProtection
 *  net.minecraft.enchantment.EnumEnchantmentType
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.MathHelper
 */
package net.nmccoy.legendgear.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class EnchantmentMagicProtection
extends Enchantment {
    public EnchantmentMagicProtection(int id, int rarity) {
        super(id, rarity, EnumEnchantmentType.armor);
        this.setName("magicProtection");
    }

    public int getMinEnchantability(int level) {
        return 5 + level * 8;
    }

    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 12;
    }

    public int getMaxLevel() {
        return 4;
    }

    public int calcModifierDamage(int input, DamageSource source) {
        if (source.canHarmInCreative()) {
            return 0;
        }
        float f = (float)(6 + input * input) / 3.0f;
        if (source.isMagicDamage()) {
            return MathHelper.floor_float((float)(f * 1.5f));
        }
        return 0;
    }

    public boolean canApplyTogether(Enchantment other) {
        if (other instanceof EnchantmentProtection) {
            EnchantmentProtection enchantmentprotection = (EnchantmentProtection)other;
            return enchantmentprotection.protectionType == 2;
        }
        return super.canApplyTogether(other);
    }
}

