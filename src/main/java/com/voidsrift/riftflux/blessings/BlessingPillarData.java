package com.voidsrift.riftflux.blessings;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import java.util.HashSet;
import java.util.Set;

public class BlessingPillarData extends WorldSavedData {
    private static final String NAME = "riftflux_blessing_pillars";
    private static final String TAG_BROKEN = "BrokenPillars";

    private final Set<String> brokenPillars = new HashSet<String>();

    public BlessingPillarData(String name) {
        super(name);
    }

    public static BlessingPillarData get(World world) {
        if (world == null) {
            return null;
        }
        MapStorage storage = world.mapStorage;
        BlessingPillarData data = (BlessingPillarData) storage.loadData(BlessingPillarData.class, NAME);
        if (data == null) {
            data = new BlessingPillarData(NAME);
            storage.setData(NAME, data);
        }
        return data;
    }

    public static void markBroken(World world, int x, int y, int z, int dim) {
        if (world == null || world.isRemote) {
            return;
        }
        BlessingPillarData data = get(world);
        if (data != null && data.brokenPillars.add(key(dim, x, y, z))) {
            data.markDirty();
        }
    }

    public static void clearBroken(World world, int x, int y, int z, int dim) {
        if (world == null || world.isRemote) {
            return;
        }
        BlessingPillarData data = get(world);
        if (data != null && data.brokenPillars.remove(key(dim, x, y, z))) {
            data.markDirty();
        }
    }

    public static boolean isBroken(World world, int x, int y, int z, int dim) {
        if (world == null) {
            return false;
        }
        BlessingPillarData data = get(world);
        return data != null && data.brokenPillars.contains(key(dim, x, y, z));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        brokenPillars.clear();
        NBTTagList list = tag.getTagList(TAG_BROKEN, 8);
        for (int i = 0; i < list.tagCount(); i++) {
            String key = list.getStringTagAt(i);
            if (key != null && !key.isEmpty()) {
                brokenPillars.add(key);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (String key : brokenPillars) {
            list.appendTag(new NBTTagString(key));
        }
        tag.setTag(TAG_BROKEN, list);
    }

    private static String key(int dim, int x, int y, int z) {
        return dim + ":" + x + ":" + y + ":" + z;
    }
}
