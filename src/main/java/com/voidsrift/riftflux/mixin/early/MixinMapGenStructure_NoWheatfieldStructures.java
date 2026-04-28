package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.wheatfield.world.WheatfieldTerrainUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenStructure.class)
public abstract class MixinMapGenStructure_NoWheatfieldStructures {
    @Shadow
    protected abstract boolean canSpawnStructureAtCoords(int chunkX, int chunkZ);

    @Redirect(
            method = "func_151538_a",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/structure/MapGenStructure;canSpawnStructureAtCoords(II)Z"
            )
    )
    private boolean riftflux$skipWheatfieldStructureStartsAfterSpawnCheck(
            MapGenStructure instance,
            int chunkX,
            int chunkZ,
            World world,
            int candidateChunkX,
            int candidateChunkZ,
            int sourceChunkX,
            int sourceChunkZ,
            Block[] blocks) {
        return this.canSpawnStructureAtCoords(chunkX, chunkZ)
                && !this.riftflux$shouldBlockWheatfieldStructure(world, chunkX, chunkZ);
    }

    @Inject(method = "generateStructuresInChunk", at = @At("HEAD"), cancellable = true)
    private void riftflux$skipWheatfieldStructurePieces(
            World world,
            Random random,
            int chunkX,
            int chunkZ,
            CallbackInfoReturnable<Boolean> cir) {
        if (this.riftflux$shouldBlockWheatfieldStructure(world, chunkX, chunkZ)) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    private boolean riftflux$shouldBlockWheatfieldStructure(World world, int chunkX, int chunkZ) {
        if (!WheatfieldTerrainUtil.isOverworld(world)) {
            return false;
        }
        if (((Object)this) instanceof MapGenVillage && ModConfig.wheatfieldAllowVillage) {
            return false;
        }
        return WheatfieldTerrainUtil.chunkHasWheatfield(world, chunkX, chunkZ);
    }
}
