package com.voidsrift.riftflux.mixin.late.dimdoors.modern;

import com.llamalad7.mixinextras.sugar.Local;
import com.voidsrift.riftflux.mixin.late.chunkloading.ChunkloadingCompatHelper;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.dimdev.dimdoors.world.PocketBuilder", remap = false)
public abstract class MixinPocketBuilder {
    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"),
            method = "getDoorOrientation",
            remap = false,
            require = 0
    )
    private static void riftflux$getDoorOrientation$loadChunk(Object source, Object properties,
                                                              CallbackInfoReturnable<Integer> cir, @Local World world) {
        ChunkloadingCompatHelper.ensureBlockExists(world, source);
    }

    @Inject(at = @At("HEAD"), method = "setBlockDirectly", remap = false, require = 0)
    private static void riftflux$setBlockDirectly$loadChunk(World world, int x, int y, int z, Block block,
                                                            int metadata, CallbackInfo ci) {
        ChunkloadingCompatHelper.ensureBlockExists(world, x, y, z);
    }
}
