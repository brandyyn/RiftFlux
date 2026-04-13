package com.voidsrift.riftflux.mixin.accessor;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkCache.class)
public interface ChunkCacheAccessor {

    @Accessor("chunkX")
    int riftflux$getChunkX();

    @Accessor("chunkZ")
    int riftflux$getChunkZ();

    @Accessor("chunkArray")
    Chunk[][] riftflux$getChunkArray();

    @Accessor("worldObj")
    World riftflux$getWorldObj();
}
