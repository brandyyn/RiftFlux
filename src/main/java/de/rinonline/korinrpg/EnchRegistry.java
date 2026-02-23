/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 */
package de.rinonline.korinrpg;

import de.rinonline.korinrpg.enchantments.EnchantmentMaxStamina;
import de.rinonline.korinrpg.enchantments.EnchantmentOverloadreduction;
import de.rinonline.korinrpg.enchantments.EnchantmentStaminaRegen;
import net.minecraft.enchantment.Enchantment;

public class EnchRegistry {
    public static final Enchantment MaxStamina = new EnchantmentMaxStamina(84, 5);
    public static final Enchantment StaminaRegen = new EnchantmentStaminaRegen(85, 5);
    public static final Enchantment Overloadreduction = new EnchantmentOverloadreduction(86, 5);
}

