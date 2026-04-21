package com.voidsrift.riftflux.riftexplorer;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.UUID;

public final class RiftChestRandomMobStateHelper {
    public static final String TAG_ENABLED = "RiftChestRandomMobState";
    public static final String TAG_INIT_X = "RiftChestInitialX";
    public static final String TAG_INIT_Y = "RiftChestInitialY";
    public static final String TAG_INIT_Z = "RiftChestInitialZ";
    public static final String TAG_BIOME = "RiftChestInitialBiome";
    public static final String TAG_SEED = "RiftChestRandomSeed";

    private RiftChestRandomMobStateHelper() {
    }

    public static boolean hasStoredState(Entity entity) {
        return entity != null && hasStoredState(entity.getEntityData());
    }

    public static boolean hasStoredState(NBTTagCompound tag) {
        return tag != null
                && tag.getBoolean(TAG_ENABLED)
                && tag.hasKey(TAG_INIT_X, 3)
                && tag.hasKey(TAG_INIT_Y, 3)
                && tag.hasKey(TAG_INIT_Z, 3)
                && tag.hasKey(TAG_BIOME, 3)
                && tag.hasKey(TAG_SEED, 3);
    }

    public static void ensureStoredState(Entity entity) {
        if (entity == null || hasStoredState(entity)) {
            return;
        }

        int initialX = MathHelper.floor_double(entity.posX);
        int initialY = MathHelper.floor_double(entity.posY);
        int initialZ = MathHelper.floor_double(entity.posZ);
        int biomeId = -1;
        if (entity.worldObj != null) {
            BiomeGenBase biome = entity.worldObj.getBiomeGenForCoords(initialX, initialZ);
            biomeId = biome == null ? -1 : biome.biomeID;
        }

        writeStoredState(
                entity.getEntityData(),
                initialX,
                initialY,
                initialZ,
                biomeId,
                computeRandomSeed(entity, initialX, initialY, initialZ));
    }

    public static void applyStoredState(Entity entity, int initialX, int initialY, int initialZ, int biomeId, int randomSeed) {
        if (entity == null) {
            return;
        }
        writeStoredState(entity.getEntityData(), initialX, initialY, initialZ, biomeId, randomSeed);
    }

    public static int getInitialX(Entity entity) {
        return getStateTag(entity).getInteger(TAG_INIT_X);
    }

    public static int getInitialY(Entity entity) {
        return getStateTag(entity).getInteger(TAG_INIT_Y);
    }

    public static int getInitialZ(Entity entity) {
        return getStateTag(entity).getInteger(TAG_INIT_Z);
    }

    public static int getBiomeId(Entity entity) {
        return getStateTag(entity).getInteger(TAG_BIOME);
    }

    public static int getRandomSeed(Entity entity) {
        return getStateTag(entity).getInteger(TAG_SEED);
    }

    private static NBTTagCompound getStateTag(Entity entity) {
        NBTTagCompound tag = entity == null ? null : entity.getEntityData();
        return tag == null ? new NBTTagCompound() : tag;
    }

    private static void writeStoredState(NBTTagCompound tag, int initialX, int initialY, int initialZ, int biomeId, int randomSeed) {
        if (tag == null) {
            return;
        }
        tag.setBoolean(TAG_ENABLED, true);
        tag.setInteger(TAG_INIT_X, initialX);
        tag.setInteger(TAG_INIT_Y, initialY);
        tag.setInteger(TAG_INIT_Z, initialZ);
        tag.setInteger(TAG_BIOME, biomeId);
        tag.setInteger(TAG_SEED, randomSeed);
    }

    private static int computeRandomSeed(Entity entity, int initialX, int initialY, int initialZ) {
        UUID uuid = entity == null ? null : entity.getUniqueID();
        if (uuid != null) {
            long most = uuid.getMostSignificantBits();
            long least = uuid.getLeastSignificantBits();
            int seed = (int)(most ^ (most >>> 32) ^ least ^ (least >>> 32));
            return mcpIntHash(seed) & Integer.MAX_VALUE;
        }
        return mcpIntHash(initialX * 73428767 ^ initialY * 9122713 ^ initialZ) & Integer.MAX_VALUE;
    }

    private static int mcpIntHash(int value) {
        value = 61 ^ value ^ value >> 16;
        value = value + (value << 3);
        value = value ^ value >> 4;
        value = value * 668265261;
        return value ^ value >> 15;
    }
}
