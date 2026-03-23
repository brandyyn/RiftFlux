package com.voidsrift.riftflux.fence;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FenceOverrideData extends WorldSavedData {
    private static final String NAME = "riftflux_fence_overrides";
    private static final String TAG_OVERRIDES = "OriginalTextureFences";

    private final Set<String> originalTextureFences = new HashSet<String>();

    public FenceOverrideData(String name) {
        super(name);
    }

    public static FenceOverrideData get(World world) {
        if (world == null || world.mapStorage == null) {
            return null;
        }

        MapStorage storage = world.mapStorage;
        FenceOverrideData data = (FenceOverrideData) storage.loadData(FenceOverrideData.class, NAME);
        if (data == null) {
            data = new FenceOverrideData(NAME);
            storage.setData(NAME, data);
        }
        return data;
    }

    public static boolean markOriginal(World world, int x, int y, int z, int dim) {
        if (world == null || world.isRemote) {
            return false;
        }
        FenceOverrideData data = get(world);
        if (data == null) {
            return false;
        }
        boolean changed = data.originalTextureFences.add(key(dim, x, y, z));
        if (changed) {
            data.markDirty();
        }
        return changed;
    }

    public static boolean clearOriginal(World world, int x, int y, int z, int dim) {
        if (world == null || world.isRemote) {
            return false;
        }
        FenceOverrideData data = get(world);
        if (data == null) {
            return false;
        }
        boolean changed = data.originalTextureFences.remove(key(dim, x, y, z));
        if (changed) {
            data.markDirty();
        }
        return changed;
    }

    public static int[] getDimensionCoordinates(World world, int dim) {
        FenceOverrideData data = get(world);
        if (data == null || data.originalTextureFences.isEmpty()) {
            return new int[0];
        }

        List<Integer> values = new ArrayList<Integer>();
        String prefix = dim + ":";

        for (String entry : data.originalTextureFences) {
            if (entry == null || !entry.startsWith(prefix)) {
                continue;
            }
            String[] parts = entry.split(":");
            if (parts.length != 4) {
                continue;
            }
            values.add(Integer.valueOf(parseInt(parts[1])));
            values.add(Integer.valueOf(parseInt(parts[2])));
            values.add(Integer.valueOf(parseInt(parts[3])));
        }

        int[] packed = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            packed[i] = values.get(i).intValue();
        }
        return packed;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        originalTextureFences.clear();
        NBTTagList list = tag.getTagList(TAG_OVERRIDES, 8);
        for (int i = 0; i < list.tagCount(); i++) {
            String entry = list.getStringTagAt(i);
            if (entry != null && !entry.isEmpty()) {
                originalTextureFences.add(entry);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (String entry : originalTextureFences) {
            list.appendTag(new NBTTagString(entry));
        }
        tag.setTag(TAG_OVERRIDES, list);
    }

    private static String key(int dim, int x, int y, int z) {
        return dim + ":" + x + ":" + y + ":" + z;
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
