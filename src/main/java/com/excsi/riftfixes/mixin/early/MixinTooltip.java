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

import java.util.*;
import java.util.regex.Pattern;

@Mixin(ItemStack.class)
public abstract class MixinTooltip {

    @SuppressWarnings("unchecked")
    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    private void riftfixes$meleeTooltip(EntityPlayer player, boolean advanced, CallbackInfoReturnable<List> cir) {
        if (!ModConfig.enableMeleeDamageTooltip) return;

        ItemStack self = (ItemStack)(Object)this;
        List<String> tip = cir.getReturnValue();
        if (self == null || tip == null) return;

        Double dmg = computeAttackDamage(self);
        if (dmg == null) {
            // still remove spacer gaps even if no melee line
            removeBlankSpacerLines(tip);
            return;
        }

        // Strip vanilla attack-damage and any existing "Melee Damage" lines (e.g., AoA)
        final String vanillaAttr = StatCollector.translateToLocal("attribute.name.generic.attackDamage");
        for (Iterator<String> it = tip.iterator(); it.hasNext();) {
            String s = it.next();
            String plain = stripFmt(s);
            if (plain.contains(vanillaAttr) || plain.toLowerCase(Locale.ROOT).contains("melee damage")) {
                it.remove();
            }
        }

        // Insert our unified gray line after enchants/CT, before meta tails
        String line = EnumChatFormatting.GRAY + String.format(Locale.ROOT, "%.1f Melee Damage", dmg);
        int insertAt = findPostEnchantsPreMetaIndex(tip);
        tip.add(insertAt, line);

        // Remove all blank spacer lines to kill vertical gaps
        removeBlankSpacerLines(tip);

        cir.setReturnValue(tip);
    }

    private static Double computeAttackDamage(ItemStack stack) {
        try {
            @SuppressWarnings("unchecked")
            Multimap<String, AttributeModifier> map = stack.getAttributeModifiers();
            if (map == null) return null;

            String key = SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(); // "generic.attackDamage"
            Collection<AttributeModifier> mods = map.get(key);
            if (mods == null || mods.isEmpty()) return null;

            double base = 1.0D;      // fist/base in 1.7.10
            double add0 = 0.0D;      // op0 sum
            double mult1 = 0.0D;     // op1 sum
            double mult2 = 1.0D;     // op2 product

            for (AttributeModifier m : mods) {
                if (m == null) continue;
                switch (m.getOperation()) {
                    case 0: add0 += m.getAmount(); break;
                    case 1: mult1 += m.getAmount(); break;
                    case 2: mult2 *= (1.0D + m.getAmount()); break;
                }
            }

            double dmg = (base + add0) * (1.0D + mult1) * mult2;

            // Sharpness adds +1.25 per level in 1.7.10
            int sharp = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            if (sharp > 0) dmg += 1.25D * sharp;

            if (dmg < 0) dmg = 0;
            return dmg;
        } catch (Throwable t) {
            return null;
        }
    }

    private static final Pattern RAW_REGISTRY = Pattern.compile("^[a-z0-9_.-]+:[a-z0-9_/.-]+$");
    private static final Pattern HAS_NUM_ID   = Pattern.compile(".*#\\d+.*"); // e.g., "#275"

    private static int findPostEnchantsPreMetaIndex(List<String> tip) {
        int n = tip.size();
        if (n <= 1) return n;
        for (int i = n - 1; i >= 1; i--) {
            String s = tip.get(i);
            String noFmt = stripFmt(s);
            boolean italic = s.contains("\u00A7o");
            boolean darkGray = s.contains("\u00A78");
            boolean looksRegistry = RAW_REGISTRY.matcher(noFmt).matches();
            boolean hasNumId = HAS_NUM_ID.matcher(noFmt).matches();
            boolean nbtLine = noFmt.regionMatches(true, 0, "nbt:", 0, 4);

            boolean isMetaTail = italic || darkGray || looksRegistry || hasNumId || nbtLine;
            if (!isMetaTail) return i + 1;
        }
        return Math.min(1, n);
    }

    private static void removeBlankSpacerLines(List<String> tip) {
        for (Iterator<String> it = tip.iterator(); it.hasNext();) {
            String s = it.next();
            String plain = stripFmt(s).trim();
            if (plain.isEmpty()) it.remove(); // kills "" or formatting-only spacer lines
        }
    }

    private static String stripFmt(String s) {
        return s == null ? "" : s.replaceAll("\u00A7[0-9A-FK-ORa-fk-or]", "");
    }
}