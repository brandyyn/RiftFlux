package com.voidsrift.riftflux.jukebox;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.BlockJukebox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class JukeboxLoopHelper {
    public static final long NO_LOOP_TICK = -1L;
    public static final long NO_RECORD_KEY = -1L;
    private static final Map<String, Integer> RECORD_LENGTH_SECONDS = buildRecordLengthSeconds();
    private static String[] cachedTrackTimingEntries;
    private static Map<String, TrackTiming> cachedTrackTimings;

    private JukeboxLoopHelper() {
    }

    public static void scheduleLoop(BlockJukebox.TileEntityJukebox jukebox, ItemStack record) {
        if (!ModConfig.jukeboxAutoLoopEnabled || jukebox == null || record == null || !(record.getItem() instanceof ItemRecord)) {
            setNextLoopTick(jukebox, NO_LOOP_TICK);
            setScheduledRecordKey(jukebox, NO_RECORD_KEY);
            return;
        }

        World world = jukebox.getWorldObj();
        if (world == null || world.isRemote) {
            return;
        }

        TrackTiming timing = getRecordTiming(record);
        long loopAfterTicks;
        if (timing == null) {
            loopAfterTicks = (long) Math.max(1, ModConfig.jukeboxUnknownTrackLoopAfterSeconds) * 20L;
        } else {
            int delaySeconds = timing.loopDelaySeconds >= 0
                    ? timing.loopDelaySeconds
                    : Math.max(0, ModConfig.jukeboxLoopDelaySeconds);
            loopAfterTicks = (long) Math.max(1, timing.lengthSeconds) * 20L + (long) delaySeconds * 20L;
        }
        long nextLoopTick = world.getTotalWorldTime() + loopAfterTicks;
        setNextLoopTick(jukebox, nextLoopTick);
        setScheduledRecordKey(jukebox, getRecordKey(record));
        world.scheduleBlockUpdate(jukebox.xCoord, jukebox.yCoord, jukebox.zCoord, Blocks.jukebox, toBlockUpdateDelay(loopAfterTicks));
        jukebox.markDirty();
    }

    public static void clearLoop(BlockJukebox.TileEntityJukebox jukebox) {
        setNextLoopTick(jukebox, NO_LOOP_TICK);
        setScheduledRecordKey(jukebox, NO_RECORD_KEY);
        if (jukebox != null && jukebox.getWorldObj() != null && !jukebox.getWorldObj().isRemote) {
            jukebox.markDirty();
        }
    }

    public static void tick(BlockJukebox.TileEntityJukebox jukebox) {
        if (!ModConfig.jukeboxAutoLoopEnabled || jukebox == null) {
            return;
        }

        World world = jukebox.getWorldObj();
        if (world == null || world.isRemote) {
            return;
        }

        long nextLoopTick = getNextLoopTick(jukebox);
        if (nextLoopTick < 0L || world.getTotalWorldTime() < nextLoopTick) {
            return;
        }

        ItemStack record = jukebox.func_145856_a();
        if (record == null || !(record.getItem() instanceof ItemRecord)) {
            clearLoop(jukebox);
            return;
        }
        long scheduledRecordKey = getScheduledRecordKey(jukebox);
        if (scheduledRecordKey != NO_RECORD_KEY && scheduledRecordKey != getRecordKey(record)) {
            clearLoop(jukebox);
            return;
        }

        playRecord(world, jukebox.xCoord, jukebox.yCoord, jukebox.zCoord, record);
        scheduleLoop(jukebox, record);
    }

    public static void runScheduledLoop(World world, int x, int y, int z) {
        if (!ModConfig.jukeboxAutoLoopEnabled || world == null || world.isRemote) {
            return;
        }

        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof BlockJukebox.TileEntityJukebox)) {
            return;
        }

        BlockJukebox.TileEntityJukebox jukebox = (BlockJukebox.TileEntityJukebox) tile;
        long nextLoopTick = getNextLoopTick(jukebox);
        if (nextLoopTick < 0L) {
            return;
        }

        long now = world.getTotalWorldTime();
        if (now < nextLoopTick) {
            world.scheduleBlockUpdate(x, y, z, Blocks.jukebox, toBlockUpdateDelay(nextLoopTick - now));
            return;
        }

        tick(jukebox);
    }

    public static void handleRedstoneChange(World world, int x, int y, int z) {
        if (!ModConfig.jukeboxRedstoneRestartEnabled || world == null || world.isRemote) {
            return;
        }

        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof BlockJukebox.TileEntityJukebox) || !(tile instanceof JukeboxLoopState)) {
            return;
        }

        JukeboxLoopState state = (JukeboxLoopState) tile;
        boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
        boolean wasPowered = state.riftflux$wasPowered();
        if (powered && !wasPowered) {
            ItemStack record = ((BlockJukebox.TileEntityJukebox) tile).func_145856_a();
            if (record != null && record.getItem() instanceof ItemRecord) {
                playRecord(world, x, y, z, record);
                scheduleLoop((BlockJukebox.TileEntityJukebox) tile, record);
            }
        }

        if (powered != wasPowered) {
            state.riftflux$setWasPowered(powered);
            tile.markDirty();
        }
    }

    public static void syncInitialPower(BlockJukebox.TileEntityJukebox jukebox) {
        if (!ModConfig.jukeboxRedstoneRestartEnabled || jukebox == null) {
            return;
        }

        World world = jukebox.getWorldObj();
        if (world == null || world.isRemote) {
            return;
        }

        if (jukebox instanceof JukeboxLoopState) {
            ((JukeboxLoopState) jukebox).riftflux$setWasPowered(
                    world.isBlockIndirectlyGettingPowered(jukebox.xCoord, jukebox.yCoord, jukebox.zCoord)
            );
        }
    }

    private static void playRecord(World world, int x, int y, int z, ItemStack record) {
        Item item = record.getItem();
        world.playAuxSFXAtEntity((EntityPlayer) null, 1005, x, y, z, Item.getIdFromItem(item));
    }

    private static long getNextLoopTick(BlockJukebox.TileEntityJukebox jukebox) {
        return jukebox instanceof JukeboxLoopState ? ((JukeboxLoopState) jukebox).riftflux$getNextLoopTick() : NO_LOOP_TICK;
    }

    private static void setNextLoopTick(BlockJukebox.TileEntityJukebox jukebox, long tick) {
        if (jukebox instanceof JukeboxLoopState) {
            ((JukeboxLoopState) jukebox).riftflux$setNextLoopTick(tick);
        }
    }

    private static long getScheduledRecordKey(BlockJukebox.TileEntityJukebox jukebox) {
        return jukebox instanceof JukeboxLoopState ? ((JukeboxLoopState) jukebox).riftflux$getScheduledRecordKey() : NO_RECORD_KEY;
    }

    private static void setScheduledRecordKey(BlockJukebox.TileEntityJukebox jukebox, long key) {
        if (jukebox instanceof JukeboxLoopState) {
            ((JukeboxLoopState) jukebox).riftflux$setScheduledRecordKey(key);
        }
    }

    private static long getRecordKey(ItemStack record) {
        if (record == null || record.getItem() == null) {
            return NO_RECORD_KEY;
        }
        return ((long) Item.getIdFromItem(record.getItem()) << 32) ^ (record.getItemDamage() & 0xFFFFFFFFL);
    }

    private static int toBlockUpdateDelay(long ticks) {
        if (ticks <= 0L) {
            return 1;
        }
        return ticks > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) ticks;
    }

    private static TrackTiming getRecordTiming(ItemStack record) {
        Item item = record.getItem();
        if (item instanceof ItemRecord) {
            Map<String, TrackTiming> configured = getConfiguredTrackTimings();
            TrackTiming configuredTiming = getFirstConfiguredTiming(configured, record);
            if (configuredTiming != null) {
                return configuredTiming;
            }

            Integer seconds = RECORD_LENGTH_SECONDS.get(normalize(((ItemRecord) item).recordName));
            if (seconds != null && seconds.intValue() > 0) {
                return new TrackTiming(seconds.intValue(), -1);
            }
        }
        return null;
    }

    private static TrackTiming getFirstConfiguredTiming(Map<String, TrackTiming> configured, ItemStack record) {
        if (configured == null || configured.isEmpty()) {
            return null;
        }

        Item item = record.getItem();
        if (item instanceof ItemRecord) {
            TrackTiming byRecordName = configured.get(normalize(((ItemRecord) item).recordName));
            if (byRecordName != null) {
                return byRecordName;
            }
        }

        String registryName = getItemRegistryName(item);
        TrackTiming byRegistryName = configured.get(normalize(registryName));
        if (byRegistryName != null) {
            return byRegistryName;
        }

        if (registryName != null && registryName.startsWith("minecraft:")) {
            TrackTiming byUnprefixedRegistryName = configured.get(normalize(registryName.substring("minecraft:".length())));
            if (byUnprefixedRegistryName != null) {
                return byUnprefixedRegistryName;
            }
        }

        String unlocalizedName = item.getUnlocalizedName(record);
        TrackTiming byUnlocalizedName = configured.get(normalize(unlocalizedName));
        if (byUnlocalizedName != null) {
            return byUnlocalizedName;
        }

        if (unlocalizedName != null && unlocalizedName.startsWith("item.")) {
            return configured.get(normalize(unlocalizedName.substring("item.".length())));
        }

        return null;
    }

    private static Map<String, TrackTiming> getConfiguredTrackTimings() {
        String[] entries = ModConfig.jukeboxTrackTimings;
        if (cachedTrackTimings != null && cachedTrackTimingEntries == entries) {
            return cachedTrackTimings;
        }

        Map<String, TrackTiming> timings = new HashMap<String, TrackTiming>();
        if (entries != null) {
            for (int i = 0; i < entries.length; i++) {
                TrackTimingEntry entry = parseTrackTimingEntry(entries[i]);
                if (entry != null) {
                    timings.put(entry.key, entry.timing);
                }
            }
        }
        cachedTrackTimingEntries = entries;
        cachedTrackTimings = timings;
        return timings;
    }

    private static TrackTimingEntry parseTrackTimingEntry(String raw) {
        if (raw == null) {
            return null;
        }

        String entry = raw.trim();
        if (entry.isEmpty() || entry.startsWith("#")) {
            return null;
        }

        int split = entry.indexOf('=');
        String key;
        String values;
        if (split >= 0) {
            key = entry.substring(0, split).trim();
            values = entry.substring(split + 1).trim();
        } else {
            String[] parts = entry.split("\\|", 2);
            if (parts.length != 2) {
                return null;
            }
            key = parts[0].trim();
            values = parts[1].trim();
        }

        if (key.isEmpty() || values.isEmpty()) {
            return null;
        }

        String[] valueParts = values.split("[,|]", 2);
        int lengthSeconds = parseDurationSeconds(valueParts[0], -1);
        if (lengthSeconds <= 0) {
            return null;
        }

        int loopDelaySeconds = -1;
        if (valueParts.length > 1) {
            loopDelaySeconds = parseDurationSeconds(valueParts[1], -1);
            if (loopDelaySeconds < 0) {
                loopDelaySeconds = -1;
            }
        }

        return new TrackTimingEntry(normalize(key), new TrackTiming(lengthSeconds, loopDelaySeconds));
    }

    private static int parseDurationSeconds(String raw, int fallback) {
        if (raw == null) {
            return fallback;
        }

        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.isEmpty()) {
            return fallback;
        }

        if (value.endsWith("s")) {
            value = value.substring(0, value.length() - 1).trim();
        }

        int colon = value.indexOf(':');
        if (colon >= 0) {
            try {
                int minutes = Integer.parseInt(value.substring(0, colon).trim());
                int seconds = Integer.parseInt(value.substring(colon + 1).trim());
                return minutes * 60 + seconds;
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static String getItemRegistryName(Item item) {
        Object name = Item.itemRegistry.getNameForObject(item);
        return name == null ? null : name.toString();
    }

    private static Map<String, Integer> buildRecordLengthSeconds() {
        Map<String, Integer> lengths = new HashMap<String, Integer>();
        lengths.put("13", 178);
        lengths.put("cat", 185);
        lengths.put("blocks", 345);
        lengths.put("chirp", 185);
        lengths.put("far", 174);
        lengths.put("mall", 197);
        lengths.put("mellohi", 96);
        lengths.put("stal", 150);
        lengths.put("strad", 188);
        lengths.put("ward", 251);
        lengths.put("11", 71);
        lengths.put("wait", 238);
        return lengths;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static final class TrackTiming {
        private final int lengthSeconds;
        private final int loopDelaySeconds;

        private TrackTiming(int lengthSeconds, int loopDelaySeconds) {
            this.lengthSeconds = lengthSeconds;
            this.loopDelaySeconds = loopDelaySeconds;
        }
    }

    private static final class TrackTimingEntry {
        private final String key;
        private final TrackTiming timing;

        private TrackTimingEntry(String key, TrackTiming timing) {
            this.key = key;
            this.timing = timing;
        }
    }
}
