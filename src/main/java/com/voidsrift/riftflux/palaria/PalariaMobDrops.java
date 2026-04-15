package com.voidsrift.riftflux.palaria;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.asgardshield.AsgardShieldContent;
import com.voidsrift.riftflux.inventorypets.InventoryPetsContent;
import com.voidsrift.riftflux.terramine.TerrariaContent;
import com.voidsrift.riftflux.vortex.item.ModItems;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.nmccoy.legendgear.LegendGear2;

import java.util.Locale;

public final class PalariaMobDrops {
    private PalariaMobDrops() {
    }

    public static void dropConfigured(EntityLivingBase entity, String[] entries) {
        if (entity == null || entries == null) {
            return;
        }
        for (String entry : entries) {
            DropEntry parsed = parse(entry);
            if (parsed == null || parsed.stack == null || parsed.stack.getItem() == null) {
                continue;
            }
            if (entity.getRNG().nextFloat() > parsed.chance) {
                continue;
            }
            int count = parsed.minCount;
            if (parsed.maxCount > parsed.minCount) {
                count += entity.getRNG().nextInt(parsed.maxCount - parsed.minCount + 1);
            }
            if (count <= 0) {
                continue;
            }
            ItemStack drop = parsed.stack.copy();
            drop.stackSize = count;
            entity.entityDropItem(drop, 0.0F);
        }
    }

    public static void dropChance(EntityLivingBase entity, Item item, float chance, int count) {
        if (entity == null || item == null || count <= 0 || chance <= 0.0F) {
            return;
        }
        if (chance >= 1.0F || entity.getRNG().nextFloat() <= chance) {
            entity.entityDropItem(new ItemStack(item, count), 0.0F);
        }
    }

    public static void dropRange(EntityLivingBase entity, Item item, int min, int max) {
        if (entity == null || item == null || max <= 0) {
            return;
        }
        int lower = Math.max(0, min);
        int upper = Math.max(lower, max);
        int count = lower;
        if (upper > lower) {
            count += entity.getRNG().nextInt(upper - lower + 1);
        }
        if (count > 0) {
            entity.entityDropItem(new ItemStack(item, count), 0.0F);
        }
    }

    public static boolean matchesConfiguredItem(ItemStack stack, String[] entries) {
        if (stack == null || stack.getItem() == null || entries == null) {
            return false;
        }
        for (String raw : entries) {
            ItemStack configured = resolveStack(stripDropDecorators(raw));
            if (configured == null || configured.getItem() == null) {
                continue;
            }
            if (configured.getItem() != stack.getItem()) {
                continue;
            }
            if (configured.getItemDamage() == 32767 || configured.getItemDamage() == stack.getItemDamage()) {
                return true;
            }
        }
        return false;
    }

    private static DropEntry parse(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String[] chanceSplit = trimmed.split("\\|", 2);
        float chance = chanceSplit.length > 1 ? parseChance(chanceSplit[1].trim()) : 1.0F;
        if (chance <= 0.0F) {
            return null;
        }

        String itemPart = chanceSplit[0].trim();
        int min = 1;
        int max = 1;
        int star = itemPart.indexOf('*');
        if (star >= 0) {
            String countPart = itemPart.substring(star + 1).trim();
            itemPart = itemPart.substring(0, star).trim();
            int[] range = parseCountRange(countPart);
            min = range[0];
            max = range[1];
        }

        ItemStack stack = resolveStack(itemPart);
        if (stack == null) {
            return null;
        }
        return new DropEntry(stack, chance, min, max);
    }

    private static float parseChance(String value) {
        try {
            float parsed = Float.parseFloat(value);
            if (parsed > 1.0F) {
                parsed /= 100.0F;
            }
            if (parsed < 0.0F) {
                return 0.0F;
            }
            return Math.min(1.0F, parsed);
        } catch (NumberFormatException ignored) {
            return 1.0F;
        }
    }

    private static int[] parseCountRange(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new int[]{1, 1};
        }
        String[] parts = value.trim().split("-", 2);
        int min = parseInt(parts[0], 1);
        int max = parts.length > 1 ? parseInt(parts[1], min) : min;
        min = Math.max(0, min);
        max = Math.max(min, max);
        return new int[]{min, max};
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    public static ItemStack resolveStack(String raw) {
        if (raw == null) {
            return null;
        }
        String itemName = raw.trim();
        int meta = 0;
        int at = itemName.lastIndexOf('@');
        if (at >= 0) {
            meta = parseInt(itemName.substring(at + 1), 0);
            itemName = itemName.substring(0, at).trim();
        }

        String alias = normalize(itemName);
        if ("rawbeef".equals(alias) || "beef".equals(alias)) return new ItemStack(Items.beef);
        if ("leather".equals(alias)) return new ItemStack(Items.leather);
        if ("bone".equals(alias) || "bones".equals(alias)) return new ItemStack(Items.bone);
        if ("gunpowder".equals(alias)) return new ItemStack(Items.gunpowder);
        if ("enderpearl".equals(alias) || "enderpearls".equals(alias)) return new ItemStack(Items.ender_pearl);

        if ("focusband".equals(alias)) return stack(ModItems.focusBand);
        if ("highlandspirit".equals(alias)) return stack(ModItems.highlandSpirit);
        if ("whoopiecushion".equals(alias)) return stack(TerrariaContent.whoopieCushion);
        if ("creeptileeye".equals(alias)) return stack(PalariaMobContent.creeptileEye);
        if ("endermiteshard".equals(alias) || "endermite".equals(alias)) return stack(PalariaMobContent.endermiteShard);
        if ("raptorclaw".equals(alias)) return stack(PalariaMobContent.raptorClaw);
        if ("gildedironshield".equals(alias) || "asgildedironshield".equals(alias)) {
            return stack(AsgardShieldContent.asgardShieldIronGilded);
        }
        if ("blastcharm".equals(alias)) return stack(LegendGear2.charmPendant, 3);
        if ("feathercharm".equals(alias)) return stack(LegendGear2.charmPendant, 4);

        Item pet = InventoryPetsContent.getItemByKey(itemName);
        if (pet != null) {
            return new ItemStack(pet);
        }

        Item item = findItem(itemName);
        return item == null ? null : new ItemStack(item, 1, meta);
    }

    private static String stripDropDecorators(String raw) {
        if (raw == null) {
            return "";
        }
        String value = raw.trim();
        int pipe = value.indexOf('|');
        if (pipe >= 0) {
            value = value.substring(0, pipe).trim();
        }
        int star = value.indexOf('*');
        if (star >= 0) {
            value = value.substring(0, star).trim();
        }
        return value;
    }

    private static ItemStack stack(Item item) {
        return stack(item, 0);
    }

    private static ItemStack stack(Item item, int meta) {
        return item == null ? null : new ItemStack(item, 1, meta);
    }

    private static Item findItem(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        String trimmed = raw.trim();
        int colon = trimmed.indexOf(':');
        if (colon > 0 && colon < trimmed.length() - 1) {
            return GameRegistry.findItem(trimmed.substring(0, colon), trimmed.substring(colon + 1));
        }
        return GameRegistry.findItem(Constants.MODID, trimmed);
    }

    private static String normalize(String value) {
        String lower = value == null ? "" : value.toLowerCase(Locale.ROOT);
        StringBuilder out = new StringBuilder(lower.length());
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                out.append(c);
            }
        }
        return out.toString();
    }

    private static final class DropEntry {
        private final ItemStack stack;
        private final float chance;
        private final int minCount;
        private final int maxCount;

        private DropEntry(ItemStack stack, float chance, int minCount, int maxCount) {
            this.stack = stack;
            this.chance = chance;
            this.minCount = minCount;
            this.maxCount = maxCount;
        }
    }
}
