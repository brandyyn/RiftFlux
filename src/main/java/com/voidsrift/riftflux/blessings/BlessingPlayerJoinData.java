package com.voidsrift.riftflux.blessings;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import java.util.HashSet;
import java.util.Set;

public class BlessingPlayerJoinData extends WorldSavedData {
    private static final String NAME = "riftflux_blessing_player_joins";
    private static final String TAG_SEEN = "SeenPlayers";

    private final Set<String> seenPlayers = new HashSet<String>();

    public BlessingPlayerJoinData(String name) {
        super(name);
    }

    public static BlessingPlayerJoinData get(World world) {
        if (world == null) {
            return null;
        }
        MapStorage storage = world.mapStorage;
        BlessingPlayerJoinData data = (BlessingPlayerJoinData) storage.loadData(BlessingPlayerJoinData.class, NAME);
        if (data == null) {
            data = new BlessingPlayerJoinData(NAME);
            storage.setData(NAME, data);
        }
        return data;
    }

    public static boolean hasSeenPlayer(World world, String playerId) {
        if (world == null || playerId == null || playerId.isEmpty()) {
            return false;
        }
        BlessingPlayerJoinData data = get(world);
        return data != null && data.seenPlayers.contains(playerId);
    }

    public static void markSeen(World world, String playerId) {
        if (world == null || world.isRemote || playerId == null || playerId.isEmpty()) {
            return;
        }
        BlessingPlayerJoinData data = get(world);
        if (data != null && data.seenPlayers.add(playerId)) {
            data.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.seenPlayers.clear();
        NBTTagList list = tag.getTagList(TAG_SEEN, 8);
        for (int i = 0; i < list.tagCount(); i++) {
            String playerId = list.getStringTagAt(i);
            if (playerId != null && !playerId.isEmpty()) {
                this.seenPlayers.add(playerId);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (String playerId : this.seenPlayers) {
            list.appendTag(new NBTTagString(playerId));
        }
        tag.setTag(TAG_SEEN, list);
    }
}
