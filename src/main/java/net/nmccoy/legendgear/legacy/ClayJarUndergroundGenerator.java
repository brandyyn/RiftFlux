package net.nmccoy.legendgear.legacy;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import net.nmccoy.legendgear.legacy.blocks.TileEntityJar;

public class ClayJarUndergroundGenerator implements IWorldGenerator {
    private static final int MAX_SPAWN_ATTEMPTS_PER_CHUNK = 256;
    private static final int MAX_VERTICAL_PROBE_DISTANCE = 2;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world == null || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        if (!LegendGear.clayJarUndergroundGenEnabled || LegendGear.jarBlock == null) {
            return;
        }

        int spawnAttempts = Math.max(0, Math.min(MAX_SPAWN_ATTEMPTS_PER_CHUNK, LegendGear.clayJarUndergroundSpawnChance));
        if (spawnAttempts <= 0) {
            return;
        }

        int maxBuildY = Math.max(2, world.getActualHeight() - 2);
        int minY = Math.max(1, Math.min(maxBuildY, LegendGear.clayJarUndergroundMinY));
        int maxY = Math.max(1, Math.min(maxBuildY, LegendGear.clayJarUndergroundMaxY));
        if (maxY < minY) {
            int swap = minY;
            minY = maxY;
            maxY = swap;
        }

        List<JarLootEntry> lootEntries = parseLootEntries(LegendGear.clayJarNaturalLootEntries);
        int worldXBase = chunkX * 16;
        int worldZBase = chunkZ * 16;
        for (int attempt = 0; attempt < spawnAttempts; ++attempt) {
            int x = worldXBase + random.nextInt(16);
            int z = worldZBase + random.nextInt(16);
            int startY = minY + random.nextInt(maxY - minY + 1);
            int y = this.findPlacementY(world, x, z, minY, maxY, startY);
            if (y < 0) {
                continue;
            }
            if (!world.setBlock(x, y, z, LegendGear.jarBlock, 0, 2)) {
                continue;
            }

            ItemStack loot = createRandomLoot(random, lootEntries);
            if (loot == null) {
                continue;
            }

            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityJar) {
                ((TileEntityJar) tile).setInventorySlotContents(0, loot);
            }
        }
    }

    private int findPlacementY(World world, int x, int z, int minY, int maxY, int startY) {
        if (this.canPlaceUndergroundJar(world, x, startY, z)) {
            return startY;
        }

        for (int offset = 1; offset <= MAX_VERTICAL_PROBE_DISTANCE; ++offset) {
            int lowerY = startY - offset;
            if (lowerY >= minY && this.canPlaceUndergroundJar(world, x, lowerY, z)) {
                return lowerY;
            }

            int upperY = startY + offset;
            if (upperY <= maxY && this.canPlaceUndergroundJar(world, x, upperY, z)) {
                return upperY;
            }
        }
        return -1;
    }

    private boolean canPlaceUndergroundJar(World world, int x, int y, int z) {
        if (world.canBlockSeeTheSky(x, y, z) || !world.isAirBlock(x, y, z)) {
            return false;
        }

        Block floor = world.getBlock(x, y - 1, z);
        Material material = floor.getMaterial();
        boolean naturalFloor = material == Material.rock
                || material == Material.ground
                || material == Material.sand
                || floor == Blocks.gravel
                || floor == Blocks.cobblestone
                || floor == Blocks.mossy_cobblestone;
        return naturalFloor && floor.isSideSolid(world, x, y - 1, z, ForgeDirection.UP);
    }

    private static List<JarLootEntry> parseLootEntries(String[] entries) {
        List<JarLootEntry> lootEntries = new ArrayList<JarLootEntry>();
        if (entries == null || entries.length == 0) {
            return lootEntries;
        }

        for (String rawEntry : entries) {
            if (rawEntry == null) {
                continue;
            }

            String entry = rawEntry.trim();
            if (entry.isEmpty()) {
                continue;
            }

            int meta = 0;
            String itemPart = entry;
            int metaIndex = itemPart.indexOf('@');
            if (metaIndex >= 0) {
                meta = parseInt(itemPart.substring(metaIndex + 1), 0, 32767, 0);
                itemPart = itemPart.substring(0, metaIndex).trim();
            }

            int minCount = 1;
            int maxCount = 1;
            int stackIndex = itemPart.indexOf('*');
            if (stackIndex >= 0) {
                int[] range = parseCountRange(itemPart.substring(stackIndex + 1));
                minCount = range[0];
                maxCount = range[1];
                itemPart = itemPart.substring(0, stackIndex).trim();
            }

            Item item = resolveLootItem(itemPart);
            if (item == null || minCount <= 0 || maxCount < minCount) {
                continue;
            }

            lootEntries.add(new JarLootEntry(item, meta, minCount, maxCount));
        }

        return lootEntries;
    }

    private static ItemStack createRandomLoot(Random random, List<JarLootEntry> lootEntries) {
        if (lootEntries == null || lootEntries.isEmpty() || random == null) {
            return null;
        }

        JarLootEntry entry = lootEntries.get(random.nextInt(lootEntries.size()));
        int count = entry.minCount + random.nextInt(entry.maxCount - entry.minCount + 1);
        ItemStack stack = new ItemStack(entry.item, Math.max(1, count), Math.max(0, entry.meta));
        if (stack.getItem() != null && stack.getItem().getItemStackLimit(stack) <= 1) {
            stack.stackSize = 1;
        } else {
            stack.stackSize = Math.min(stack.stackSize, Math.max(1, LegendGear.clayJarItemCapacity));
        }
        return stack;
    }

    private static Item resolveLootItem(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return null;
        }

        String[] split = itemId.split(":", 2);
        Item item = null;
        if (split.length == 2) {
            item = GameRegistry.findItem(split[0].toLowerCase(Locale.ROOT), split[1]);
        }
        if (item == null) {
            Object registryObject = Item.itemRegistry.getObject(itemId);
            if (registryObject instanceof Item) {
                item = (Item) registryObject;
            }
        }
        if (item == null && split.length == 2) {
            Block block = GameRegistry.findBlock(split[0].toLowerCase(Locale.ROOT), split[1]);
            if (block != null) {
                item = Item.getItemFromBlock(block);
            }
        }
        return item;
    }

    private static int[] parseCountRange(String raw) {
        if (raw == null) {
            return new int[]{1, 1};
        }

        String trimmed = raw.trim();
        int separator = trimmed.indexOf('-');
        if (separator > 0) {
            int min = parseInt(trimmed.substring(0, separator), 1, 32767, 1);
            int max = parseInt(trimmed.substring(separator + 1), min, 32767, min);
            return new int[]{min, Math.max(min, max)};
        }

        int count = parseInt(trimmed, 1, 32767, 1);
        return new int[]{count, count};
    }

    private static int parseInt(String raw, int min, int max, int fallback) {
        if (raw == null) {
            return fallback;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            if (value < min || value > max) {
                return fallback;
            }
            return value;
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static final class JarLootEntry {
        private final Item item;
        private final int meta;
        private final int minCount;
        private final int maxCount;

        private JarLootEntry(Item item, int meta, int minCount, int maxCount) {
            this.item = item;
            this.meta = meta;
            this.minCount = minCount;
            this.maxCount = maxCount;
        }
    }
}
