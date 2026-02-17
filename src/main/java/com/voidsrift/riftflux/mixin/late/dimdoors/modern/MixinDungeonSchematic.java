package com.voidsrift.riftflux.mixin.late.dimdoors.modern;

import com.llamalad7.mixinextras.sugar.Local;
import com.voidsrift.riftflux.compat.chunkloading.ChunkloadingCompatHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.dimdev.dimdoors.dungeon.DungeonSchematic", remap = false)
public abstract class MixinDungeonSchematic {
    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/dimdev/dimdoors/dungeon/DungeonSchematic;initDoorTileEntity(Lnet/minecraft/world/World;Lorg/dimdev/dimdoors/Point3D;)V"
            ),
            method = "createEntranceReverseLink",
            remap = false,
            require = 0
    )
    private static void riftflux$createEntranceReverseLink$loadChunk(CallbackInfo ci,
                                                                     @Local(argsOnly = true, index = 0) World world,
                                                                     @Local(argsOnly = true, index = 2) Object pocketCenter) {
        ChunkloadingCompatHelper.ensureBlockExists(world, pocketCenter);
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/dimdev/dimdoors/schematic/BlockRotator;transformPoint(Lorg/dimdev/dimdoors/Point3D;Lorg/dimdev/dimdoors/Point3D;ILorg/dimdev/dimdoors/Point3D;)V",
                    shift = At.Shift.AFTER
            ),
            method = "createExitDoorLink",
            remap = false,
            require = 0
    )
    private static void riftflux$createExitDoorLink$loadChunk(CallbackInfo ci,
                                                              @Local(argsOnly = true, index = 0) World world,
                                                              @Local(ordinal = 3) Object location) {
        ChunkloadingCompatHelper.ensureBlockExists(world, location);
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/dimdev/dimdoors/schematic/BlockRotator;transformPoint(Lorg/dimdev/dimdoors/Point3D;Lorg/dimdev/dimdoors/Point3D;ILorg/dimdev/dimdoors/Point3D;)V",
                    shift = At.Shift.AFTER
            ),
            method = "createDimensionalDoorLink",
            remap = false,
            require = 0
    )
    private static void riftflux$createDimensionalDoorLink$loadChunk(CallbackInfo ci,
                                                                     @Local(argsOnly = true, index = 0) World world,
                                                                     @Local(ordinal = 3) Object location) {
        ChunkloadingCompatHelper.ensureBlockExists(world, location);
    }

    @Inject(at = @At("HEAD"), method = "initDoorTileEntity", remap = false, require = 0)
    private static void riftflux$initDoorTileEntity$loadChunk(CallbackInfo ci,
                                                              @Local(argsOnly = true, index = 0) World world,
                                                              @Local(argsOnly = true, index = 1) Object point) {
        ChunkloadingCompatHelper.ensureBlockExists(world, point);
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"),
            method = "writeDepthSign",
            remap = false,
            require = 0
    )
    private static void riftflux$writeDepthSign$loadChunk(CallbackInfo ci,
                                                          @Local(argsOnly = true, index = 0) World world,
                                                          @Local(ordinal = 0) int x, @Local(ordinal = 1) int y,
                                                          @Local(ordinal = 2) int z) {
        ChunkloadingCompatHelper.ensureBlockExists(world, x, y, z);
    }
}
