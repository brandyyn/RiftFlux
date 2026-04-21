package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.mixinhooks.IRiftChestRandomMobState;
import com.voidsrift.riftflux.riftexplorer.RiftChestRandomMobStateHelper;
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
    @Unique private static final Field RF_MCP_INIT_X = rf$findField("mcp$initX");
    @Unique private static final Field RF_MCP_INIT_Y = rf$findField("mcp$initY");
    @Unique private static final Field RF_MCP_INIT_Z = rf$findField("mcp$initZ");
    @Unique private static final Field RF_MCP_INIT_BIOME = rf$findField("mcp$initBiome");
    @Unique private static final Field RF_MCP_INIT = rf$findField("mcp$init");
    @Unique private static final Field RF_MCP_RANDOM_SEED = rf$findField("mcp$randomMobsSeed");
    @Unique private static final Field RF_MCP_RANDOM_SEED_INIT = rf$findField("mcp$randomMobsSeedInit");

    @Shadow public World worldObj;

    @Unique private boolean rf$randomMobStateApplied = false;

    @Inject(method = "onUpdate()V", at = @At("HEAD"), require = 0)
    private void riftflux$applyRiftChestRandomMobState(CallbackInfo ci) {
        this.rf$applyStoredRandomMobStateIfNeeded();
    }

    @Inject(method = "setPositionAndRotation2(DDDFFI)V", at = @At("TAIL"), require = 0)
    private void riftflux$applyRiftChestRandomMobStateOnClientSpawn(double x, double y, double z, float yaw, float pitch, int increments, CallbackInfo ci) {
        this.rf$applyStoredRandomMobStateIfNeeded();
    }

    @Inject(method = "readFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("TAIL"), require = 0)
    private void riftflux$readRiftChestRandomMobState(NBTTagCompound tag, CallbackInfo ci) {
        this.rf$randomMobStateApplied = false;
        this.rf$applyStoredRandomMobStateIfNeeded();
    }

    @Override
    public void rf$setRiftChestRandomMobState(int initialX, int initialY, int initialZ, int biomeId, int randomSeed) {
        RiftChestRandomMobStateHelper.applyStoredState((Entity) (Object) this, initialX, initialY, initialZ, biomeId, randomSeed);
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
        if (this.rf$randomMobStateApplied) {
            return;
        }
        if (RF_MCP_INIT_X == null || RF_MCP_INIT_Y == null || RF_MCP_INIT_Z == null
                || RF_MCP_INIT_BIOME == null || RF_MCP_INIT == null
                || RF_MCP_RANDOM_SEED == null || RF_MCP_RANDOM_SEED_INIT == null) {
            this.rf$randomMobStateApplied = true;
            return;
        }
        NBTTagCompound tag = ((Entity) (Object) this).getEntityData();
        if (!RiftChestRandomMobStateHelper.hasStoredState(tag)) {
            return;
        }

        int initialX = tag.getInteger(RiftChestRandomMobStateHelper.TAG_INIT_X);
        int initialY = tag.getInteger(RiftChestRandomMobStateHelper.TAG_INIT_Y);
        int initialZ = tag.getInteger(RiftChestRandomMobStateHelper.TAG_INIT_Z);
        int biomeId = tag.getInteger(RiftChestRandomMobStateHelper.TAG_BIOME);
        int randomSeed = tag.getInteger(RiftChestRandomMobStateHelper.TAG_SEED);
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
}
