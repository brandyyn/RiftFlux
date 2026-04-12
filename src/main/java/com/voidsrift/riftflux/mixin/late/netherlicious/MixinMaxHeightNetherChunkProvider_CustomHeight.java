package com.voidsrift.riftflux.mixin.late.netherlicious;

import DelirusCrux.Netherlicious.Dimension.MaxHeightNetherChunkProvider;
import com.voidsrift.riftflux.compat.netherlicious.NetherliciousHeightHelper;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MaxHeightNetherChunkProvider.class, remap = false)
public abstract class MixinMaxHeightNetherChunkProvider_CustomHeight {

    @Shadow
    private World worldObj;

    @Inject(method = "replaceBiomeBlocks", at = @At("TAIL"))
    private void riftflux$applyConfiguredCeilingToChunkArray(int chunkX, int chunkZ, Block[] blocks, byte[] meta, BiomeGenBase[] biomes, CallbackInfo ci) {
        NetherliciousHeightHelper.applyConfiguredCeilingToBlockArray(this.worldObj.getSeed(), chunkX, chunkZ, blocks);
    }

    @Inject(method = "populate", at = @At("TAIL"))
    private void riftflux$reapplyConfiguredCeilingAfterPopulate(IChunkProvider chunkProvider, int chunkX, int chunkZ, CallbackInfo ci) {
        if (!NetherliciousHeightHelper.hasCustomBigNetherTopY()) {
            return;
        }

        Chunk chunk = this.worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
        NetherliciousHeightHelper.applyConfiguredCeilingToChunk(this.worldObj.getSeed(), chunk);
    }
}
