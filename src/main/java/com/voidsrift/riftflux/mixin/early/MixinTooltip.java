package com.voidsrift.riftflux.mixin.early;

import com.google.common.collect.Multimap;
import com.voidsrift.riftflux.ModConfig;
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
    private void riftflux$meleeTooltip(EntityPlayer player, boolean advanced, CallbackInfoReturnable<List> cir) {
        if (!ModConfig.enableMeleeDamageTooltip) return;

        ItemStack self = (ItemStack)(Object)this;
        List<String> tip = cir.getReturnValue();
        if (self == null || tip == null) return;

        Double dmg = computeAttackDamage(self);
        if (dmg == null) {
            removeBlankSpacerLines(tip);
            return;
        }

        // Strip vanilla "+X Attack Damage" and any existing "Melee Damage" lines
        final String vanillaAttr = StatCollector.translateToLocal("attribute.name.generic.attackDamage");
        for (Iterator<String> it = tip.iterator(); it.hasNext();) {
            String s = it.next();
            String plain = stripFmt(s);
            if (plain.contains(vanillaAttr) || plain.toLowerCase(Locale.ROOT).contains("melee damage")) {
                it.remove();
            }
        }

        String line = EnumChatFormatting.GRAY + String.format(Locale.ROOT, "%.1f Melee Damage", dmg);

        // Prefer placing it right ABOVE EnderCore's "Durability: x/y" line
        int durabilityIdx = findEnderCoreDurabilityIndex(tip);
        int insertAt = (durabilityIdx >= 0) ? durabilityIdx : findPostEnchantsPreMetaIndex(tip);

        tip.add(insertAt, line);
        removeBlankSpacerLines(tip);

        cir.setReturnValue(tip);
    }

    private static Double computeAttackDamage(ItemStack stack) {
        try {
            @SuppressWarnings("unchecked")
            Multimap<String, AttributeModifier> map = stack.getAttributeModifiers();
            if (map == null) return null;

            String key = SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName();
            Collection<AttributeModifier> mods = map.get(key);
            if (mods == null || mods.isEmpty()) return null;

            double base = 1.0D;   // fist/base in 1.7.10
            double add0 = 0.0D;   // op0 sum
            double mult1 = 0.0D;  // op1 sum
            double mult2 = 1.0D;  // op2 product

            for (AttributeModifier m : mods) {
                if (m == null) continue;
                switch (m.getOperation()) {
                    case 0: add0 += m.getAmount(); break;
                    case 1: mult1 += m.getAmount(); break;
                    case 2: mult2 *= (1.0D + m.getAmount()); break;
                }
            }

            double dmg = (base + add0) * (1.0D + mult1) * mult2;

            // Sharpness (1.7.10) +1.25 per level
            int sharp = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            if (sharp > 0) dmg += 1.25D * sharp;

            if (dmg < 0) dmg = 0;
            return dmg;
        } catch (Throwable t) {
            return null;
        }
    }

    // --- placement helpers ---

    private static final Pattern RAW_REGISTRY = Pattern.compile("^[a-z0-9_.-]+:[a-z0-9_/.-]+$");
    private static final Pattern HAS_NUM_ID   = Pattern.compile(".*#\\d+.*"); // e.g., "#275"
    private static final Pattern DURABILITY_RX =
            Pattern.compile("(?i)^durability\\s*[:：]\\s*\\d+\\s*/\\s*\\d+\\s*$");

    /** Find EnderCore "Durability: x/y" line index; -1 if not present. */
    private static int findEnderCoreDurabilityIndex(List<String> tip) {
        for (int i = 0; i < tip.size(); i++) {
            String s = stripFmt(tip.get(i)).trim();
            if (DURABILITY_RX.matcher(s).matches()) {
                return i;
            }
        }
        return -1;
    }

    /** Fallback: after enchants/CT, before meta tails (mod name, IDs, NBT, etc.). */
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
            if (plain.isEmpty()) it.remove();
        }
    }

    private static String stripFmt(String s) {
        return s == null ? "" : s.replaceAll("\u00A7[0-9A-FK-ORa-fk-or]", "");
    }
}