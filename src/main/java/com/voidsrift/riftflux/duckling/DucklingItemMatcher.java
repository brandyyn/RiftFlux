package com.voidsrift.riftflux.duckling;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

final class DucklingItemMatcher {
    private static final int ANY_META = -1;

    private static String[] cachedItems;
    private static String[] cachedOres;
    private static List<ItemEntry> itemEntries;
    private static List<String> oreEntries;

    private DucklingItemMatcher() {
    }

    static boolean matchesQuacklingBreedItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }

        ensureQuacklingBreedCache();

        for (int i = 0; i < itemEntries.size(); i++) {
            if (itemEntries.get(i).matches(stack)) {
                return true;
            }
        }

        int[] ids = OreDictionary.getOreIDs(stack);
        for (int i = 0; i < ids.length; i++) {
            String name = OreDictionary.getOreName(ids[i]);
            if (oreEntries.contains(name)) {
                return true;
            }
        }

        return false;
    }

    private static void ensureQuacklingBreedCache() {
        String[] itemConfig = ModConfig.ducklingQuacklingBreedItems == null ? new String[0] : ModConfig.ducklingQuacklingBreedItems;
        String[] oreConfig = ModConfig.ducklingQuacklingBreedOreDictionary == null ? new String[0] : ModConfig.ducklingQuacklingBreedOreDictionary;
        if (itemEntries != null && Arrays.equals(cachedItems, itemConfig) && Arrays.equals(cachedOres, oreConfig)) {
            return;
        }

        cachedItems = itemConfig.clone();
        cachedOres = oreConfig.clone();
        itemEntries = new ArrayList<ItemEntry>();
        oreEntries = new ArrayList<String>();

        for (int i = 0; i < itemConfig.length; i++) {
            addConfiguredEntry(itemConfig[i]);
        }
        for (int i = 0; i < oreConfig.length; i++) {
            addOreEntry(oreConfig[i]);
        }
    }

    private static void addConfiguredEntry(String raw) {
        if (raw == null) {
            return;
        }

        String entry = raw.trim();
        if (entry.isEmpty() || entry.startsWith("#")) {
            return;
        }

        if (entry.startsWith("ore:")) {
            addOreEntry(entry.substring(4));
            return;
        }

        int meta = ANY_META;
        int metaIndex = entry.lastIndexOf('@');
        if (metaIndex >= 0) {
            meta = parseMeta(entry.substring(metaIndex + 1));
            entry = entry.substring(0, metaIndex).trim();
            if (meta < 0) {
                warnInvalid(entry, "invalid metadata");
                return;
            }
        }

        Item item = resolveItem(entry);
        if (item == null) {
            warnInvalid(raw, "could not resolve item");
            return;
        }
        itemEntries.add(new ItemEntry(item, meta));
    }

    private static void addOreEntry(String raw) {
        if (raw == null) {
            return;
        }

        String entry = raw.trim();
        if (entry.isEmpty() || entry.startsWith("#")) {
            return;
        }
        oreEntries.add(entry);
    }

    private static int parseMeta(String raw) {
        try {
            int value = Integer.parseInt(raw.trim());
            return value >= 0 && value <= 32767 ? value : -1;
        } catch (NumberFormatException ignored) {
            return -1;
        }
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
            String modid = split[0].toLowerCase(Locale.ROOT);
            item = GameRegistry.findItem(modid, split[1]);
            if (item == null) {
                Block block = GameRegistry.findBlock(modid, split[1]);
                if (block != null) {
                    item = Item.getItemFromBlock(block);
                }
            }
        }
        if (item == null) {
            Object registryObject = Item.itemRegistry.getObject(namespacedId);
            if (registryObject instanceof Item) {
                item = (Item)registryObject;
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

    private static void warnInvalid(String entry, String reason) {
        FMLLog.warning("[RiftFlux] Skipping invalid Duckling Quackling breed item '%s': %s.", entry, reason);
    }

    private static final class ItemEntry {
        private final Item item;
        private final int meta;

        private ItemEntry(Item item, int meta) {
            this.item = item;
            this.meta = meta;
        }

        private boolean matches(ItemStack stack) {
            return stack.getItem() == this.item && (this.meta == ANY_META || stack.getItemDamage() == this.meta);
        }
    }
}
