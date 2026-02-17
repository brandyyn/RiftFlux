package com.voidsrift.riftflux.mixin.late.dimdoors.legacy;

import com.voidsrift.riftflux.mixin.late.chunkloading.ChunkloadingCompatHelper;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "StevenDimDoors.mod_pocketDim.schematic.ChunkBlockSetter", remap = false)
public abstract class MixinChunkBlockSetter {
    @Inject(at = @At("HEAD"), method = "setBlock", remap = false, require = 0)
    private static void riftflux$setBlock$loadChunk(World world, int x, int y, int z, Block block, int metadata,
                                                     CallbackInfo ci) {
        ChunkloadingCompatHelper.ensureBlockExists(world, x, y, z);
    }
}
