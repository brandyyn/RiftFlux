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
import com.voidsrift.riftflux.util.EnchantmentIdGuard;
import net.minecraft.enchantment.Enchantment;

public class EnchRegistry {
    public static final Enchantment MaxStamina = createMaxStamina();
    public static final Enchantment StaminaRegen = createStaminaRegen();
    public static final Enchantment Overloadreduction = createOverloadreduction();

    private static Enchantment createMaxStamina() {
        EnchantmentIdGuard.requireFree(
                ConfigurationMoD2.EnchantmentMaxStaminaId,
                "DSS Max Stamina",
                "Darwin Sprinting System.EnchantmentMaxStaminaId"
        );
        return new EnchantmentMaxStamina(ConfigurationMoD2.EnchantmentMaxStaminaId, 5);
    }

    private static Enchantment createStaminaRegen() {
        EnchantmentIdGuard.requireFree(
                ConfigurationMoD2.EnchantmentStaminaRegenId,
                "DSS Stamina Regeneration",
                "Darwin Sprinting System.EnchantmentStaminaRegenId"
        );
        return new EnchantmentStaminaRegen(ConfigurationMoD2.EnchantmentStaminaRegenId, 5);
    }

    private static Enchantment createOverloadreduction() {
        EnchantmentIdGuard.requireFree(
                ConfigurationMoD2.EnchantmentOverloadreductionId,
                "DSS Overload Reduction",
                "Darwin Sprinting System.EnchantmentOverloadReductionId"
        );
        return new EnchantmentOverloadreduction(ConfigurationMoD2.EnchantmentOverloadreductionId, 5);
    }
}
