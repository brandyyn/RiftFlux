package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.terramine.TerrariaContent;
import com.voidsrift.riftflux.vortex.item.ModItems;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.item.MagicRing;
import net.nmccoy.legendgear.magic.IMana;

import java.util.List;
import java.util.Locale;

public class LegendGearManaTooltipHandler {

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (event == null || event.itemStack == null || event.toolTip == null) {
            return;
        }

        ItemStack stack = event.itemStack;
        Item item = stack.getItem();
        if (item == null) {
            return;
        }

        if (item == TerrariaContent.whoopieCushion) {
            this.addNumericTooltip(event.toolTip, "Mana Cost", ModConfig.whoopieCushionLegendGearManaCost);
        }

        if (item == TerrariaContent.iceRod && ModConfig.iceRodUseLegendGearMana) {
            this.addNumericTooltip(event.toolTip, "Mana Cost", ModConfig.iceRodLegendGearManaCost);
        }

        if (item == ModItems.poptart) {
            this.addNumericTooltip(event.toolTip, "Mana Restore", ModConfig.poptartLegendGearManaRestore);
        }

        if (!ModConfig.enableLegendGearModule) {
            return;
        }

        if (item instanceof IMana) {
            this.addNumericTooltip(event.toolTip, "Mana Cost", ((IMana) item).getManaCost());
            return;
        }

        if (item == LegendGear2.magicRing) {
            this.addMagicRingManaTooltip(event.toolTip, stack);
        }
    }

    private void addNumericTooltip(List tooltip, String label, float value) {
        float clamped = Math.max(0.0F, value);
        if (clamped <= 0.0F) {
            return;
        }

        String prefix = label + ":";
        if (hasTooltipPrefix(tooltip, prefix)) {
            return;
        }

        EnumChatFormatting color = "Mana Cost".equalsIgnoreCase(label)
                ? EnumChatFormatting.AQUA
                : EnumChatFormatting.GRAY;
        tooltip.add(color + prefix + " " + this.formatMana(clamped));
    }

    private void addMagicRingManaTooltip(List tooltip, ItemStack stack) {
        MagicRing.RingType[] ringTypes = MagicRing.RingType.values();
        int meta = stack == null ? 0 : stack.getItemDamage();
        if (meta < 0 || meta >= ringTypes.length) {
            return;
        }

        MagicRing.RingType ringType = ringTypes[meta];
        switch (ringType) {
            case SPEED_RING:
                this.addUniqueLine(tooltip, "Mana Cost: " + this.formatMana(MagicRing.SPRINT_RING_COST) + "/tick while sprinting");
                break;
            case CONVECTION_RING:
                this.addUniqueLine(tooltip, "Mana Cost: " + this.formatMana(MagicRing.CONVECTION_RING_COST) + "/tick in lava");
                break;
            case SOFT_FALL_RING:
                this.addUniqueLine(tooltip, "Mana Cost: " + this.formatMana(MagicRing.FALL_RING_COST) + " per excess fall block");
                break;
            case COLDFEET_RING:
                this.addUniqueLine(tooltip, "Mana Cost: " + this.formatMana(MagicRing.COLDFEET_RING_COST) + " per frozen water block");
                break;
            case THIEF_RING:
                this.addUniqueLine(tooltip, "Mana Cost: " + this.formatMana(MagicRing.THIEF_RING_COST) + "/tick while sneaking");
                break;
            case WARRIOR_RING:
                this.addUniqueLine(tooltip, "Mana Cost: " + this.formatMana(MagicRing.WARRIOR_MANA_COST) + " per boosted hit");
                break;
            case PHOENIX_RING:
                this.addUniqueLine(tooltip, "Mana Cost: equals absorbed fire damage");
                break;
            default:
                break;
        }
    }

    private void addUniqueLine(List tooltip, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        String target = this.normalizeTooltip(message);
        for (Object lineObj : tooltip) {
            if (!(lineObj instanceof String)) {
                continue;
            }
            if (target.equals(this.normalizeTooltip((String) lineObj))) {
                return;
            }
        }
        EnumChatFormatting color = target.startsWith("mana cost:")
                ? EnumChatFormatting.AQUA
                : EnumChatFormatting.GRAY;
        tooltip.add(color + message);
    }

    private static boolean hasTooltipPrefix(List tooltip, String prefix) {
        if (tooltip == null || prefix == null || prefix.isEmpty()) {
            return false;
        }
        String normalizedPrefix = normalizeTooltip(prefix);
        for (Object lineObj : tooltip) {
            if (!(lineObj instanceof String)) {
                continue;
            }
            String normalized = normalizeTooltip((String) lineObj);
            if (normalized.startsWith(normalizedPrefix)) {
                return true;
            }
        }
        return false;
    }

    private String formatMana(float value) {
        String formatted = String.format(Locale.ROOT, "%.2f", Math.max(0.0F, value));
        int end = formatted.length();
        while (end > 0 && formatted.charAt(end - 1) == '0') {
            end--;
        }
        if (end > 0 && formatted.charAt(end - 1) == '.') {
            end--;
        }
        return end > 0 ? formatted.substring(0, end) : "0";
    }

    private static String normalizeTooltip(String value) {
        String stripped = value == null ? "" : value.replaceAll("\u00A7[0-9A-FK-ORa-fk-or]", "");
        return stripped.trim().toLowerCase(Locale.ROOT);
    }
}
