package com.voidsrift.riftflux.duckling;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

final class SootSpriteTargetCache {
    private static final int ITEM_CACHE_TICKS = 4;
    private static final int INVENTORY_CHUNK_CACHE_TICKS = 60;
    private static final int PRUNE_INTERVAL_TICKS = 40;
    private static final int MAX_ITEM_SCAN_KEYS = 32;
    private static final int MAX_INVENTORY_CHUNK_KEYS = 128;
    private static final List<TilePos> EMPTY_TILE_POSITIONS = Collections.emptyList();
    private static final WeakHashMap<World, WorldCache> CACHES = new WeakHashMap<World, WorldCache>();

    private SootSpriteTargetCache() {
    }

    static EntityItem findNearestItem(EntitySootSprite sprite, double horizontalRange, double verticalRange) {
        World world = sprite.worldObj;
        if (world == null) {
            return null;
        }
        return getWorldCache(world).findNearestItem(world, sprite, horizontalRange, verticalRange);
    }

    static TileEntity findNearestTile(EntitySootSprite sprite, int horizontalRange, int verticalRange, TileValidator validator) {
        World world = sprite.worldObj;
        if (world == null || validator == null) {
            return null;
        }
        return getWorldCache(world).findNearestTile(world, sprite, horizontalRange, verticalRange, validator);
    }

    private static WorldCache getWorldCache(World world) {
        WorldCache cache = CACHES.get(world);
        if (cache == null) {
            cache = new WorldCache();
            CACHES.put(world, cache);
        }
        return cache;
    }

    interface TileValidator {
        boolean isValid(TileEntity tile);
    }

    private static final class WorldCache {
        private final Map<ItemScanKey, TimedItemRefs> itemScans = new HashMap<ItemScanKey, TimedItemRefs>();
        private final Map<ChunkKey, TimedList<TilePos>> inventoryChunks = new HashMap<ChunkKey, TimedList<TilePos>>();
        private long lastPruneTick;

        EntityItem findNearestItem(World world, EntitySootSprite sprite, double horizontalRange, double verticalRange) {
            int horizontal = (int)Math.ceil(horizontalRange);
            int vertical = (int)Math.ceil(verticalRange);
            ItemScanKey key = ItemScanKey.forSprite(sprite, horizontal, vertical);
            long now = world.getTotalWorldTime();
            TimedItemRefs cached = this.itemScans.get(key);
            if (cached == null || now - cached.tick > ITEM_CACHE_TICKS) {
                cached = new TimedItemRefs(now, this.collectItemRefs(world, key.createBounds(horizontal, vertical)));
                this.itemScans.put(key, cached);
            }

            EntityItem best = null;
            double bestDistance = Double.MAX_VALUE;
            for (int i = 0; i < cached.entries.size(); i++) {
                EntityItem item = cached.entries.get(i).get();
                if (item == null) {
                    continue;
                }

                ItemStack stack = item.getEntityItem();
                if (item.isDead || stack == null || !sprite.canAcceptItem(stack)) {
                    continue;
                }

                double dx = item.posX - sprite.posX;
                double dy = item.posY - sprite.posY;
                double dz = item.posZ - sprite.posZ;
                if (Math.abs(dx) > horizontalRange || Math.abs(dy) > verticalRange || Math.abs(dz) > horizontalRange) {
                    continue;
                }

                double distance = dx * dx + dy * dy + dz * dz;
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = item;
                }
            }
            this.pruneIfNeeded(now);
            return best;
        }

        TileEntity findNearestTile(World world, EntitySootSprite sprite, int horizontalRange, int verticalRange, TileValidator validator) {
            int minX = (int)Math.floor(sprite.posX - horizontalRange);
            int maxX = (int)Math.floor(sprite.posX + horizontalRange);
            int minZ = (int)Math.floor(sprite.posZ - horizontalRange);
            int maxZ = (int)Math.floor(sprite.posZ + horizontalRange);
            int minChunkX = minX >> 4;
            int maxChunkX = maxX >> 4;
            int minChunkZ = minZ >> 4;
            int maxChunkZ = maxZ >> 4;
            long now = world.getTotalWorldTime();
            TileEntity best = null;
            double bestDistance = Double.MAX_VALUE;

            for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                    List<TilePos> positions = this.getInventoryChunk(world, chunkX, chunkZ, now);
                    for (int i = 0; i < positions.size(); i++) {
                        TilePos pos = positions.get(i);
                        if (!pos.isInside(sprite, horizontalRange, verticalRange)) {
                            continue;
                        }

                        TileEntity tile = pos.getTile(world);
                        if (tile == null || tile.isInvalid() || !(tile instanceof IInventory) || !validator.isValid(tile)) {
                            continue;
                        }

                        double distance = sprite.getDistanceSq(pos.x + 0.5D, pos.y + 0.5D, pos.z + 0.5D);
                        if (distance < bestDistance) {
                            bestDistance = distance;
                            best = tile;
                        }
                    }
                }
            }
            this.pruneIfNeeded(now);
            return best;
        }

        private List<WeakReference<EntityItem>> collectItemRefs(World world, AxisAlignedBB bounds) {
            List rawItems = world.getEntitiesWithinAABB(EntityItem.class, bounds);
            if (rawItems == null || rawItems.isEmpty()) {
                return Collections.emptyList();
            }

            ArrayList<WeakReference<EntityItem>> result = new ArrayList<WeakReference<EntityItem>>(rawItems.size());
            for (int i = 0; i < rawItems.size(); i++) {
                Object entry = rawItems.get(i);
                if (entry instanceof EntityItem) {
                    result.add(new WeakReference<EntityItem>((EntityItem)entry));
                }
            }
            return result.isEmpty() ? Collections.<WeakReference<EntityItem>>emptyList() : result;
        }

        private List<TilePos> getInventoryChunk(World world, int chunkX, int chunkZ, long now) {
            ChunkKey key = new ChunkKey(chunkX, chunkZ);
            TimedList<TilePos> cached = this.inventoryChunks.get(key);
            if (cached == null || now - cached.tick > INVENTORY_CHUNK_CACHE_TICKS) {
                cached = new TimedList<TilePos>(now, this.collectInventoryTiles(world, chunkX, chunkZ));
                this.inventoryChunks.put(key, cached);
            }
            return cached.entries;
        }

        private List<TilePos> collectInventoryTiles(World world, int chunkX, int chunkZ) {
            if (!world.getChunkProvider().chunkExists(chunkX, chunkZ)) {
                return EMPTY_TILE_POSITIONS;
            }

            Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
            if (chunk == null || chunk.chunkTileEntityMap == null || chunk.chunkTileEntityMap.isEmpty()) {
                return EMPTY_TILE_POSITIONS;
            }

            ArrayList<TilePos> result = null;
            for (Object entry : chunk.chunkTileEntityMap.values()) {
                if (!(entry instanceof TileEntity)) {
                    continue;
                }

                TileEntity tile = (TileEntity)entry;
                if (tile instanceof IInventory && !tile.isInvalid()) {
                    if (result == null) {
                        result = new ArrayList<TilePos>();
                    }
                    result.add(new TilePos(tile.xCoord, tile.yCoord, tile.zCoord));
                }
            }
            return result == null ? EMPTY_TILE_POSITIONS : result;
        }

        private void pruneIfNeeded(long now) {
            if (now - this.lastPruneTick < PRUNE_INTERVAL_TICKS) {
                trimItemScans(this.itemScans, MAX_ITEM_SCAN_KEYS);
                trimTimedLists(this.inventoryChunks, MAX_INVENTORY_CHUNK_KEYS);
                return;
            }
            this.lastPruneTick = now;
            pruneItemScans(this.itemScans, now, ITEM_CACHE_TICKS + PRUNE_INTERVAL_TICKS);
            pruneTimedLists(this.inventoryChunks, now, INVENTORY_CHUNK_CACHE_TICKS + PRUNE_INTERVAL_TICKS);
            trimItemScans(this.itemScans, MAX_ITEM_SCAN_KEYS);
            trimTimedLists(this.inventoryChunks, MAX_INVENTORY_CHUNK_KEYS);
        }

        private static void pruneItemScans(Map<ItemScanKey, TimedItemRefs> scans, long now, int maxAge) {
            ArrayList<ItemScanKey> stale = new ArrayList<ItemScanKey>();
            for (Map.Entry<ItemScanKey, TimedItemRefs> entry : scans.entrySet()) {
                if (now - entry.getValue().tick > maxAge) {
                    stale.add(entry.getKey());
                }
            }
            for (int i = 0; i < stale.size(); i++) {
                scans.remove(stale.get(i));
            }
        }

        private static <K, T> void pruneTimedLists(Map<K, TimedList<T>> scans, long now, int maxAge) {
            ArrayList<K> stale = new ArrayList<K>();
            for (Map.Entry<K, TimedList<T>> entry : scans.entrySet()) {
                if (now - entry.getValue().tick > maxAge) {
                    stale.add(entry.getKey());
                }
            }
            for (int i = 0; i < stale.size(); i++) {
                scans.remove(stale.get(i));
            }
        }

        private static void trimItemScans(Map<ItemScanKey, TimedItemRefs> scans, int maxSize) {
            while (scans.size() > maxSize) {
                ItemScanKey oldestKey = null;
                long oldestTick = Long.MAX_VALUE;
                for (Map.Entry<ItemScanKey, TimedItemRefs> entry : scans.entrySet()) {
                    if (entry.getValue().tick < oldestTick) {
                        oldestTick = entry.getValue().tick;
                        oldestKey = entry.getKey();
                    }
                }
                if (oldestKey == null) {
                    return;
                }
                scans.remove(oldestKey);
            }
        }

        private static <K, T> void trimTimedLists(Map<K, TimedList<T>> scans, int maxSize) {
            while (scans.size() > maxSize) {
                K oldestKey = null;
                long oldestTick = Long.MAX_VALUE;
                for (Map.Entry<K, TimedList<T>> entry : scans.entrySet()) {
                    if (entry.getValue().tick < oldestTick) {
                        oldestTick = entry.getValue().tick;
                        oldestKey = entry.getKey();
                    }
                }
                if (oldestKey == null) {
                    return;
                }
                scans.remove(oldestKey);
            }
        }
    }

    private static final class TilePos {
        private final int x;
        private final int y;
        private final int z;

        private TilePos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private boolean isInside(EntitySootSprite sprite, int horizontalRange, int verticalRange) {
            return Math.abs(this.x + 0.5D - sprite.posX) <= horizontalRange
                    && Math.abs(this.y + 0.5D - sprite.posY) <= verticalRange
                    && Math.abs(this.z + 0.5D - sprite.posZ) <= horizontalRange;
        }

        private TileEntity getTile(World world) {
            return world.getTileEntity(this.x, this.y, this.z);
        }
    }

    private static final class ItemScanKey {
        private final int chunkX;
        private final int chunkZ;
        private final int yBucket;
        private final int horizontalRange;
        private final int verticalRange;

        private ItemScanKey(int chunkX, int chunkZ, int yBucket, int horizontalRange, int verticalRange) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.yBucket = yBucket;
            this.horizontalRange = horizontalRange;
            this.verticalRange = verticalRange;
        }

        static ItemScanKey forSprite(EntitySootSprite sprite, int horizontalRange, int verticalRange) {
            int blockX = (int)Math.floor(sprite.posX);
            int blockY = (int)Math.floor(sprite.posY);
            int blockZ = (int)Math.floor(sprite.posZ);
            return new ItemScanKey(blockX >> 4, blockZ >> 4, blockY >> 4, horizontalRange, verticalRange);
        }

        AxisAlignedBB createBounds(int horizontalRange, int verticalRange) {
            int minX = this.chunkX * 16 - horizontalRange;
            int maxX = this.chunkX * 16 + 16 + horizontalRange;
            int minY = this.yBucket * 16 - verticalRange;
            int maxY = this.yBucket * 16 + 16 + verticalRange;
            int minZ = this.chunkZ * 16 - horizontalRange;
            int maxZ = this.chunkZ * 16 + 16 + horizontalRange;
            return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ItemScanKey)) {
                return false;
            }
            ItemScanKey other = (ItemScanKey)obj;
            return this.chunkX == other.chunkX
                    && this.chunkZ == other.chunkZ
                    && this.yBucket == other.yBucket
                    && this.horizontalRange == other.horizontalRange
                    && this.verticalRange == other.verticalRange;
        }

        @Override
        public int hashCode() {
            int result = this.chunkX;
            result = 31 * result + this.chunkZ;
            result = 31 * result + this.yBucket;
            result = 31 * result + this.horizontalRange;
            result = 31 * result + this.verticalRange;
            return result;
        }
    }

    private static final class ChunkKey {
        private final int chunkX;
        private final int chunkZ;

        private ChunkKey(int chunkX, int chunkZ) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ChunkKey)) {
                return false;
            }
            ChunkKey other = (ChunkKey)obj;
            return this.chunkX == other.chunkX && this.chunkZ == other.chunkZ;
        }

        @Override
        public int hashCode() {
            return 31 * this.chunkX + this.chunkZ;
        }
    }

    private static final class TimedItemRefs {
        private final long tick;
        private final List<WeakReference<EntityItem>> entries;

        private TimedItemRefs(long tick, List<WeakReference<EntityItem>> entries) {
            this.tick = tick;
            this.entries = entries == null ? Collections.<WeakReference<EntityItem>>emptyList() : entries;
        }
    }

    private static final class TimedList<T> {
        private final long tick;
        private final List<T> entries;

        private TimedList(long tick, List<T> entries) {
            this.tick = tick;
            this.entries = entries == null ? new ArrayList<T>() : entries;
        }
    }
}
