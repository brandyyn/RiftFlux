package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.mixinhooks.IRiftChestRandomMobState;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(Entity.class)
public abstract class MixinEntity_RiftChestRandomMobState implements IRiftChestRandomMobState {
    @Unique private static final int RF_FLAG_WATCHER = 26;
    @Unique private static final int RF_INIT_X_WATCHER = 27;
    @Unique private static final int RF_INIT_Y_WATCHER = 28;
    @Unique private static final int RF_INIT_Z_WATCHER = 29;
    @Unique private static final int RF_BIOME_WATCHER = 30;
    @Unique private static final int RF_SEED_WATCHER = 31;
    @Unique private static final int RF_UNSET_INT = Integer.MIN_VALUE;
    @Unique private static final String RF_TAG_ENABLED = "RiftChestRandomMobState";
    @Unique private static final String RF_TAG_INIT_X = "RiftChestInitialX";
    @Unique private static final String RF_TAG_INIT_Y = "RiftChestInitialY";
    @Unique private static final String RF_TAG_INIT_Z = "RiftChestInitialZ";
    @Unique private static final String RF_TAG_BIOME = "RiftChestInitialBiome";
    @Unique private static final String RF_TAG_SEED = "RiftChestRandomSeed";
    @Unique private static final Field RF_MCP_INIT_X = rf$findField("mcp$initX");
    @Unique private static final Field RF_MCP_INIT_Y = rf$findField("mcp$initY");
    @Unique private static final Field RF_MCP_INIT_Z = rf$findField("mcp$initZ");
    @Unique private static final Field RF_MCP_INIT_BIOME = rf$findField("mcp$initBiome");
    @Unique private static final Field RF_MCP_INIT = rf$findField("mcp$init");
    @Unique private static final Field RF_MCP_RANDOM_SEED = rf$findField("mcp$randomMobsSeed");
    @Unique private static final Field RF_MCP_RANDOM_SEED_INIT = rf$findField("mcp$randomMobsSeedInit");

    @Shadow protected DataWatcher dataWatcher;
    @Shadow public World worldObj;

    @Unique private boolean rf$randomMobStateApplied = false;

    @Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At("RETURN"), require = 0)
    private void riftflux$initRiftChestRandomMobWatchers(World world, CallbackInfo ci) {
        this.dataWatcher.addObject(RF_FLAG_WATCHER, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(RF_INIT_X_WATCHER, Integer.valueOf(RF_UNSET_INT));
        this.dataWatcher.addObject(RF_INIT_Y_WATCHER, Integer.valueOf(RF_UNSET_INT));
        this.dataWatcher.addObject(RF_INIT_Z_WATCHER, Integer.valueOf(RF_UNSET_INT));
        this.dataWatcher.addObject(RF_BIOME_WATCHER, Integer.valueOf(RF_UNSET_INT));
        this.dataWatcher.addObject(RF_SEED_WATCHER, Integer.valueOf(RF_UNSET_INT));
    }

    @Inject(method = "onUpdate()V", at = @At("HEAD"), require = 0)
    private void riftflux$applyRiftChestRandomMobState(CallbackInfo ci) {
        this.rf$applyStoredRandomMobStateIfNeeded();
    }

    @Inject(method = "func_145781_i(I)V", at = @At("TAIL"), require = 0)
    private void riftflux$applyRiftChestRandomMobStateOnWatcherUpdate(int watcherId, CallbackInfo ci) {
        if (this.rf$isRiftChestRandomMobWatcher(watcherId)) {
            this.rf$applyStoredRandomMobStateIfNeeded();
        }
    }

    @Inject(method = "setPositionAndRotation2(DDDFFI)V", at = @At("TAIL"), require = 0)
    private void riftflux$applyRiftChestRandomMobStateOnClientSpawn(double x, double y, double z, float yaw, float pitch, int increments, CallbackInfo ci) {
        this.rf$applyStoredRandomMobStateIfNeeded();
    }

    @Inject(method = "writeToNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("TAIL"), require = 0)
    private void riftflux$writeRiftChestRandomMobState(NBTTagCompound tag, CallbackInfo ci) {
        if (this.dataWatcher.getWatchableObjectByte(RF_FLAG_WATCHER) == 0) {
            return;
        }
        tag.setBoolean(RF_TAG_ENABLED, true);
        tag.setInteger(RF_TAG_INIT_X, this.dataWatcher.getWatchableObjectInt(RF_INIT_X_WATCHER));
        tag.setInteger(RF_TAG_INIT_Y, this.dataWatcher.getWatchableObjectInt(RF_INIT_Y_WATCHER));
        tag.setInteger(RF_TAG_INIT_Z, this.dataWatcher.getWatchableObjectInt(RF_INIT_Z_WATCHER));
        tag.setInteger(RF_TAG_BIOME, this.dataWatcher.getWatchableObjectInt(RF_BIOME_WATCHER));
        tag.setInteger(RF_TAG_SEED, this.dataWatcher.getWatchableObjectInt(RF_SEED_WATCHER));
    }

    @Inject(method = "readFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("TAIL"), require = 0)
    private void riftflux$readRiftChestRandomMobState(NBTTagCompound tag, CallbackInfo ci) {
        if (!tag.getBoolean(RF_TAG_ENABLED)) {
            return;
        }
        this.rf$setRiftChestRandomMobState(
                tag.getInteger(RF_TAG_INIT_X),
                tag.getInteger(RF_TAG_INIT_Y),
                tag.getInteger(RF_TAG_INIT_Z),
                tag.getInteger(RF_TAG_BIOME),
                tag.getInteger(RF_TAG_SEED));
    }

    @Override
    public void rf$setRiftChestRandomMobState(int initialX, int initialY, int initialZ, int biomeId, int randomSeed) {
        this.dataWatcher.updateObject(RF_INIT_X_WATCHER, Integer.valueOf(initialX));
        this.dataWatcher.updateObject(RF_INIT_Y_WATCHER, Integer.valueOf(initialY));
        this.dataWatcher.updateObject(RF_INIT_Z_WATCHER, Integer.valueOf(initialZ));
        this.dataWatcher.updateObject(RF_BIOME_WATCHER, Integer.valueOf(biomeId));
        this.dataWatcher.updateObject(RF_SEED_WATCHER, Integer.valueOf(randomSeed));
        this.dataWatcher.updateObject(RF_FLAG_WATCHER, Byte.valueOf((byte) 1));
        this.rf$randomMobStateApplied = false;
        this.rf$applyStoredRandomMobStateIfNeeded();
    }

    @Unique
    private static Field rf$findField(String name) {
        try {
            Field field = Entity.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private BiomeGenBase rf$getBiome(int biomeId, int x, int z) {
        BiomeGenBase biome = null;
        try {
            biome = BiomeGenBase.getBiome(biomeId);
        } catch (Throwable ignored) {
        }
        if (biome == null && this.worldObj != null) {
            biome = this.worldObj.getBiomeGenForCoords(x, z);
        }
        return biome;
    }

    @Unique
    private void rf$applyStoredRandomMobStateIfNeeded() {
        if (this.rf$randomMobStateApplied || this.dataWatcher.getWatchableObjectByte(RF_FLAG_WATCHER) == 0) {
            return;
        }
        if (RF_MCP_INIT_X == null || RF_MCP_INIT_Y == null || RF_MCP_INIT_Z == null
                || RF_MCP_INIT_BIOME == null || RF_MCP_INIT == null
                || RF_MCP_RANDOM_SEED == null || RF_MCP_RANDOM_SEED_INIT == null) {
            this.rf$randomMobStateApplied = true;
            return;
        }

        int initialX = this.dataWatcher.getWatchableObjectInt(RF_INIT_X_WATCHER);
        int initialY = this.dataWatcher.getWatchableObjectInt(RF_INIT_Y_WATCHER);
        int initialZ = this.dataWatcher.getWatchableObjectInt(RF_INIT_Z_WATCHER);
        int biomeId = this.dataWatcher.getWatchableObjectInt(RF_BIOME_WATCHER);
        int randomSeed = this.dataWatcher.getWatchableObjectInt(RF_SEED_WATCHER);
        if (initialX == RF_UNSET_INT
                || initialY == RF_UNSET_INT
                || initialZ == RF_UNSET_INT
                || biomeId == RF_UNSET_INT
                || randomSeed == RF_UNSET_INT) {
            return;
        }
        BiomeGenBase biome = rf$getBiome(biomeId, initialX, initialZ);

        try {
            RF_MCP_INIT_X.setInt(this, initialX);
            RF_MCP_INIT_Y.setInt(this, initialY);
            RF_MCP_INIT_Z.setInt(this, initialZ);
            RF_MCP_INIT_BIOME.set(this, biome);
            RF_MCP_INIT.setBoolean(this, true);
            RF_MCP_RANDOM_SEED.setInt(this, randomSeed);
            RF_MCP_RANDOM_SEED_INIT.setBoolean(this, true);
        } catch (Throwable ignored) {
        }
        this.rf$randomMobStateApplied = true;
    }

    @Unique
    private boolean rf$isRiftChestRandomMobWatcher(int watcherId) {
        return watcherId >= RF_FLAG_WATCHER && watcherId <= RF_SEED_WATCHER;
    }
}
