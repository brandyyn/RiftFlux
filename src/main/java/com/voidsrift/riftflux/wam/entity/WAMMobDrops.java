package com.voidsrift.riftflux.wam.entity;

import com.voidsrift.riftflux.Constants;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Locale;

final class WAMMobDrops {
    private WAMMobDrops() {
    }

    static void dropConfigured(EntityLivingBase entity, String[] entries) {
        if (entity == null || entries == null) {
            return;
        }
        for (int i = 0; i < entries.length; i++) {
            DropEntry parsed = parse(entity, entries[i]);
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

    private static DropEntry parse(EntityLivingBase entity, String raw) {
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

        ItemStack stack = resolveStack(entity, itemPart);
        return stack == null ? null : new DropEntry(stack, chance, min, max);
    }

    private static ItemStack resolveStack(EntityLivingBase entity, String raw) {
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
        if (("flowermanflower".equals(alias) || "flowermanlife".equals(alias) || "lifeflower".equals(alias))
                && entity instanceof EntityFlowerMan) {
            return ((EntityFlowerMan)entity).createLifeDropStack();
        }
        if ("beef".equals(alias) || "rawbeef".equals(alias)) return new ItemStack(Items.beef);
        if ("blazepowder".equals(alias)) return new ItemStack(Items.blaze_powder);
        if ("enderpearl".equals(alias) || "enderpearls".equals(alias)) return new ItemStack(Items.ender_pearl);
        if ("spidereye".equals(alias)) return new ItemStack(Items.spider_eye);
        if ("string".equals(alias)) return new ItemStack(Items.string);
        if ("pumpkin".equals(alias)) return new ItemStack(Blocks.pumpkin);
        if ("obsidian".equals(alias)) return new ItemStack(Blocks.obsidian);

        Item item = findItem(itemName);
        if (item != null) {
            return new ItemStack(item, 1, meta);
        }

        Block block = findBlock(itemName);
        return block == null ? null : new ItemStack(block, 1, meta);
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

    private static Block findBlock(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        String trimmed = raw.trim();
        int colon = trimmed.indexOf(':');
        if (colon > 0 && colon < trimmed.length() - 1) {
            return GameRegistry.findBlock(trimmed.substring(0, colon), trimmed.substring(colon + 1));
        }
        return GameRegistry.findBlock(Constants.MODID, trimmed);
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
