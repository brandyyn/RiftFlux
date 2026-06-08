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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

        final String vanillaAttr = StatCollector.translateToLocal("attribute.name.generic.attackDamage");
        for (Iterator<String> it = tip.iterator(); it.hasNext();) {
            String s = it.next();
            String plain = stripFmt(s);
            if (plain.contains(vanillaAttr) || plain.toLowerCase(Locale.ROOT).contains("melee damage")) {
                it.remove();
            }
        }

        String line = EnumChatFormatting.GRAY + String.format(Locale.ROOT, "%.1f Melee Damage", dmg);
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

            double base = 1.0D;
            double add0 = 0.0D;
            double mult1 = 0.0D;
            double mult2 = 1.0D;

            for (AttributeModifier m : mods) {
                if (m == null) continue;
                switch (m.getOperation()) {
                    case 0: add0 += m.getAmount(); break;
                    case 1: mult1 += m.getAmount(); break;
                    case 2: mult2 *= (1.0D + m.getAmount()); break;
                }
            }

            double dmg = (base + add0) * (1.0D + mult1) * mult2;

            int sharp = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            if (sharp > 0) dmg += 1.25D * sharp;

            if (dmg < 0) dmg = 0;
            return dmg;
        } catch (Throwable t) {
            return null;
        }
    }

    private static int findEnderCoreDurabilityIndex(List<String> tip) {
        for (int i = 0; i < tip.size(); i++) {
            if (isDurabilityLine(tip.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static int findPostEnchantsPreMetaIndex(List<String> tip) {
        int n = tip.size();
        if (n <= 1) return n;
        for (int i = n - 1; i >= 1; i--) {
            String s = tip.get(i);
            String noFmt = stripFmt(s);
            boolean italic = s.contains("\u00A7o");
            boolean darkGray = s.contains("\u00A78");
            boolean looksRegistry = looksLikeRawRegistry(noFmt);
            boolean hasNumId = hasNumericId(noFmt);
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
        if (s == null) {
            return "";
        }
        StringBuilder out = null;
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c == '\u00A7' && i + 1 < length) {
                if (out == null) {
                    out = new StringBuilder(length);
                    out.append(s, 0, i);
                }
                i++;
            } else if (out != null) {
                out.append(c);
            }
        }
        return out == null ? s : out.toString();
    }

    private static boolean isDurabilityLine(String value) {
        if (value == null) {
            return false;
        }
        int length = value.length();
        int i = skipWhitespaceAndFormatting(value, 0, length);
        String label = "durability";
        for (int j = 0; j < label.length(); j++) {
            if (i >= length || Character.toLowerCase(value.charAt(i)) != label.charAt(j)) {
                return false;
            }
            i++;
        }
        i = skipWhitespaceAndFormatting(value, i, length);
        if (i >= length) {
            return false;
        }
        char colon = value.charAt(i);
        if (colon != ':' && colon != '\uFF1A') {
            return false;
        }
        i = skipWhitespaceAndFormatting(value, i + 1, length);
        int firstDigits = 0;
        while (i < length && Character.isDigit(value.charAt(i))) {
            i++;
            firstDigits++;
        }
        if (firstDigits <= 0) {
            return false;
        }
        i = skipWhitespaceAndFormatting(value, i, length);
        if (i >= length || value.charAt(i) != '/') {
            return false;
        }
        i = skipWhitespaceAndFormatting(value, i + 1, length);
        int secondDigits = 0;
        while (i < length && Character.isDigit(value.charAt(i))) {
            i++;
            secondDigits++;
        }
        if (secondDigits <= 0) {
            return false;
        }
        return skipWhitespaceAndFormatting(value, i, length) >= length;
    }

    private static int skipWhitespaceAndFormatting(String value, int index, int length) {
        int i = index;
        while (i < length) {
            char c = value.charAt(i);
            if (c == '\u00A7' && i + 1 < length) {
                i += 2;
            } else if (Character.isWhitespace(c)) {
                i++;
            } else {
                break;
            }
        }
        return i;
    }

    private static boolean looksLikeRawRegistry(String value) {
        if (value == null) {
            return false;
        }
        int colon = value.indexOf(':');
        if (colon <= 0 || colon >= value.length() - 1 || value.indexOf(':', colon + 1) >= 0) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (i == colon) {
                continue;
            }
            boolean valid = c >= 'a' && c <= 'z'
                    || c >= '0' && c <= '9'
                    || c == '_' || c == '.' || c == '-'
                    || (i > colon && c == '/');
            if (!valid) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasNumericId(String value) {
        if (value == null) {
            return false;
        }
        int hash = value.indexOf('#');
        while (hash >= 0 && hash + 1 < value.length()) {
            if (Character.isDigit(value.charAt(hash + 1))) {
                return true;
            }
            hash = value.indexOf('#', hash + 1);
        }
        return false;
    }
}
