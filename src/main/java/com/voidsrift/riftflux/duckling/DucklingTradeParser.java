package com.voidsrift.riftflux.duckling;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

final class DucklingTradeParser {
    private DucklingTradeParser() {
    }

    static MerchantRecipeList buildRecipes(String[] entries, Random random) {
        MerchantRecipeList recipes = new MerchantRecipeList();
        if (entries == null) {
            return recipes;
        }

        for (int i = 0; i < entries.length; i++) {
            MerchantRecipe recipe = parseRecipe(entries[i], random);
            if (recipe != null) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    private static MerchantRecipe parseRecipe(String entry, Random random) {
        if (entry == null) {
            return null;
        }

        String line = entry.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return null;
        }

        int arrow = line.indexOf("->");
        if (arrow < 0) {
            warnInvalid(line, "missing '->'");
            return null;
        }

        String buySide = line.substring(0, arrow).trim();
        String sellSide = line.substring(arrow + 2).trim();
        String[] buys = buySide.split("\\+");
        if (buys.length < 1 || buys.length > 2) {
            warnInvalid(line, "expected one or two buy stacks");
            return null;
        }

        ItemStack firstBuy = parseStack(buys[0], random);
        ItemStack sell = parseStack(sellSide, random);
        if (firstBuy == null || sell == null) {
            warnInvalid(line, "could not resolve an item stack");
            return null;
        }

        if (buys.length == 2) {
            ItemStack secondBuy = parseStack(buys[1], random);
            if (secondBuy == null) {
                warnInvalid(line, "could not resolve the second buy stack");
                return null;
            }
            return new MerchantRecipe(firstBuy, secondBuy, sell);
        }

        return new MerchantRecipe(firstBuy, sell);
    }

    private static ItemStack parseStack(String token, Random random) {
        if (token == null) {
            return null;
        }

        String itemId = token.trim();
        if (itemId.isEmpty()) {
            return null;
        }

        int meta = 0;
        int metaIndex = itemId.lastIndexOf('@');
        if (metaIndex >= 0) {
            meta = parseInt(itemId.substring(metaIndex + 1), 0, 32767, -1);
            if (meta < 0) {
                return null;
            }
            itemId = itemId.substring(0, metaIndex).trim();
        }

        int count = 1;
        int countIndex = itemId.lastIndexOf('*');
        if (countIndex >= 0) {
            count = parseCount(itemId.substring(countIndex + 1), random);
            if (count < 0) {
                return null;
            }
            itemId = itemId.substring(0, countIndex).trim();
        }

        Item item = resolveItem(itemId);
        return item == null ? null : new ItemStack(item, count, meta);
    }

    private static Item resolveItem(String rawId) {
        if (rawId == null) {
            return null;
        }

        String id = rawId.trim();
        if (id.isEmpty()) {
            return null;
        }

        String namespacedId = id.indexOf(':') >= 0 ? id : "minecraft:" + id;
        String[] split = namespacedId.split(":", 2);
        Item item = null;
        if (split.length == 2) {
            item = GameRegistry.findItem(split[0].toLowerCase(Locale.ROOT), split[1]);
        }
        if (item == null) {
            Object registryObject = Item.itemRegistry.getObject(namespacedId);
            if (registryObject instanceof Item) {
                item = (Item)registryObject;
            }
        }
        if (item == null && split.length == 2) {
            Block block = GameRegistry.findBlock(split[0].toLowerCase(Locale.ROOT), split[1]);
            if (block != null) {
                item = Item.getItemFromBlock(block);
            }
        }
        if (item == null && id.indexOf(':') < 0) {
            Object registryObject = Item.itemRegistry.getObject(id);
            if (registryObject instanceof Item) {
                item = (Item)registryObject;
            }
        }
        return item;
    }

    private static int parseCount(String raw, Random random) {
        if (raw == null) {
            return -1;
        }

        String value = raw.trim();
        int rangeSeparator = value.indexOf('-');
        if (rangeSeparator > 0) {
            int min = parseInt(value.substring(0, rangeSeparator), 1, 64, -1);
            int max = parseInt(value.substring(rangeSeparator + 1), 1, 64, -1);
            if (min < 0 || max < min) {
                return -1;
            }
            return min + random.nextInt(max - min + 1);
        }

        return parseInt(value, 1, 64, -1);
    }

    private static int parseInt(String raw, int min, int max, int invalidValue) {
        if (raw == null) {
            return invalidValue;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            if (value < min || value > max) {
                return invalidValue;
            }
            return value;
        } catch (NumberFormatException ignored) {
            return invalidValue;
        }
    }

    private static void warnInvalid(String entry, String reason) {
        FMLLog.warning("[RiftFlux] Skipping invalid Duckling Quackling trade '%s': %s.", entry, reason);
    }
}
