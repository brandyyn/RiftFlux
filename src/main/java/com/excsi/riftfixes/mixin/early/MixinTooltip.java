package com.excsi.riftfixes.mixin.early;


import com.google.common.collect.Multimap;
import com.excsi.riftfixes.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class MixinTooltip {

    @SuppressWarnings("unchecked")
    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    private void riftfixes$meleeTooltip(EntityPlayer player, boolean advanced, CallbackInfoReturnable<List> cir) {
        if (!ModConfig.enableMeleeDamageTooltip) return;

        ItemStack self = (ItemStack)(Object)this;
        List<String> tip = cir.getReturnValue();
        if (self == null || tip == null) return;


        Multimap<String, AttributeModifier> map = self.getAttributeModifiers();
        if (map == null) return;

        String key = SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(); // "generic.attackDamage"
        Collection<AttributeModifier> mods = map.get(key);
        if (mods == null || mods.isEmpty()) return;

        // Compute total attack damage from attributes + base fist (1.0)
        double base = 1.0D;      // base fist damage in 1.7.10
        double add0 = 0.0D;      // op 0 sum
        double mult1 = 0.0D;     // op 1 sum (adds to base before op2)
        double mult2 = 1.0D;     // op 2 product

        for (AttributeModifier m : mods) {
            if (m == null) continue;
            int op = m.getOperation();
            if (op == 0) add0 += m.getAmount();
            else if (op == 1) mult1 += m.getAmount();
            else if (op == 2) mult2 *= (1.0D + m.getAmount());
        }

        double dmg = (base + add0) * (1.0D + mult1) * mult2;

        // Add Sharpness bonus (1.7.10): +1.25 per level (Smite/Bane are conditional on target, so omitted)
        int sharp = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, self);
        if (sharp > 0) dmg += 1.25D * sharp;

        if (dmg < 0) dmg = 0;

        // Remove vanilla "+X Attack Damage" lines and any existing melee lines
        final String vanillaAttr = StatCollector.translateToLocal("attribute.name.generic.attackDamage"); // "Attack Damage"
        for (Iterator<String> it = tip.iterator(); it.hasNext();) {
            String s = it.next();
            String plain = stripFmt(s);
            if (plain.contains(vanillaAttr) || plain.toLowerCase().contains("melee damage")) {
                it.remove();
            }
        }

        // Insert our gray line near the top (after item name)
        String line = EnumChatFormatting.GRAY + String.format("%.1f Melee Damage", dmg);
        int insertAt = Math.min(1, tip.size());
        tip.add(insertAt, line);

        cir.setReturnValue(tip);
    }

    private static String stripFmt(String s) {
        return s == null ? "" : s.replaceAll("\u00A7[0-9A-FK-ORa-fk-or]", "");
    }
}