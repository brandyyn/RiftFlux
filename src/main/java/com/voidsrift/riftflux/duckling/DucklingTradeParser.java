package com.voidsrift.riftflux.duckling;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.List;
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

        List<TradeOption> validOptions = new ArrayList<TradeOption>();
        for (int i = 0; i < entries.length; i++) {
            TradeOption option = parseTradeOption(entries[i], random);
            if (option != null) {
                validOptions.add(option);
                if (random.nextFloat() * 100.0F < option.chancePercent) {
                    recipes.add(option.recipe);
                }
            }
        }

        if (!recipes.isEmpty() || validOptions.isEmpty()) {
            return recipes;
        }

        TradeOption fallback = chooseGuaranteedTrade(validOptions, random);
        if (fallback != null) {
            recipes.add(fallback.recipe);
        }
        return recipes;
    }

    private static TradeOption parseTradeOption(String entry, Random random) {
        if (entry == null) {
            return null;
        }

        String line = entry.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return null;
        }

        String[] segments = line.split(";");
        String tradeText = segments[0].trim();
        float chancePercent = 100.0F;
        for (int i = 1; i < segments.length; i++) {
            String option = segments[i] == null ? "" : segments[i].trim();
            if (option.isEmpty()) {
                continue;
            }
            int equalsIndex = option.indexOf('=');
            if (equalsIndex <= 0) {
                warnInvalid(line, "invalid trade option '" + option + "'");
                return null;
            }
            String optionKey = option.substring(0, equalsIndex).trim().toLowerCase(Locale.ROOT);
            String optionValue = option.substring(equalsIndex + 1).trim();
            if ("chance".equals(optionKey)) {
                chancePercent = parseChance(optionValue);
                if (chancePercent < 0.0F) {
                    warnInvalid(line, "invalid chance percentage");
                    return null;
                }
            } else {
                warnInvalid(line, "unknown trade option '" + optionKey + "'");
                return null;
            }
        }

        int arrow = tradeText.indexOf("->");
        if (arrow < 0) {
            warnInvalid(tradeText, "missing '->'");
            return null;
        }

        String buySide = tradeText.substring(0, arrow).trim();
        String sellSide = tradeText.substring(arrow + 2).trim();
        String[] buys = buySide.split("\\+");
        if (buys.length < 1 || buys.length > 2) {
            warnInvalid(tradeText, "expected one or two buy stacks");
            return null;
        }

        ItemStack firstBuy = parseStack(buys[0], random);
        ItemStack sell = parseStack(sellSide, random);
        if (firstBuy == null || sell == null) {
            warnInvalid(tradeText, "could not resolve an item stack");
            return null;
        }

        if (buys.length == 2) {
            ItemStack secondBuy = parseStack(buys[1], random);
            if (secondBuy == null) {
                warnInvalid(tradeText, "could not resolve the second buy stack");
                return null;
            }
            return new TradeOption(new MerchantRecipe(firstBuy, secondBuy, sell), chancePercent);
        }

        return new TradeOption(new MerchantRecipe(firstBuy, sell), chancePercent);
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
            String namespace = split[0].toLowerCase(Locale.ROOT);
            item = resolveDucklingItem(namespace, split[1]);
            if (item == null) {
                item = GameRegistry.findItem(namespace, split[1]);
            }
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

    private static Item resolveDucklingItem(String namespace, String path) {
        if (namespace == null || path == null) {
            return null;
        }
        if (!DucklingContent.MODID.equals(namespace)
                && !"duckling".equals(namespace)
                && !"sootspritecraft".equals(namespace)) {
            return null;
        }

        if ("raw_duck".equals(path)) {
            return DucklingContent.rawDuck;
        }
        if ("cooked_duck".equals(path)) {
            return DucklingContent.cookedDuck;
        }
        if ("duck_egg".equals(path)) {
            return DucklingContent.duckEgg;
        }
        if ("duck_spawn_egg".equals(path)) {
            return DucklingContent.duckSpawnEgg;
        }
        if ("quackling_spawn_egg".equals(path)) {
            return DucklingContent.quacklingSpawnEgg;
        }
        if ("star_candy".equals(path)) {
            return DucklingContent.starCandy;
        }
        if ("soot_jar".equals(path)) {
            return DucklingContent.sootJar;
        }
        if ("soot_sprite_spawn_egg".equals(path)) {
            return DucklingContent.sootSpriteSpawnEgg;
        }
        return null;
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

    private static float parseChance(String raw) {
        if (raw == null) {
            return -1.0F;
        }
        try {
            float chance = Float.parseFloat(raw.trim());
            if (chance < 0.0F || chance > 100.0F) {
                return -1.0F;
            }
            return chance;
        } catch (NumberFormatException ignored) {
            return -1.0F;
        }
    }

    private static TradeOption chooseGuaranteedTrade(List<TradeOption> options, Random random) {
        if (options == null || options.isEmpty()) {
            return null;
        }

        float totalWeight = 0.0F;
        for (int i = 0; i < options.size(); i++) {
            totalWeight += Math.max(0.0F, options.get(i).chancePercent);
        }

        if (totalWeight <= 0.0F) {
            return options.get(random.nextInt(options.size()));
        }

        float target = random.nextFloat() * totalWeight;
        for (int i = 0; i < options.size(); i++) {
            TradeOption option = options.get(i);
            target -= Math.max(0.0F, option.chancePercent);
            if (target <= 0.0F) {
                return option;
            }
        }

        return options.get(options.size() - 1);
    }

    private static void warnInvalid(String entry, String reason) {
        FMLLog.warning("[RiftFlux] Skipping invalid Duckling Quackling trade '%s': %s.", entry, reason);
    }

    private static final class TradeOption {
        private final MerchantRecipe recipe;
        private final float chancePercent;

        private TradeOption(MerchantRecipe recipe, float chancePercent) {
            this.recipe = recipe;
            this.chancePercent = chancePercent;
        }
    }
}
