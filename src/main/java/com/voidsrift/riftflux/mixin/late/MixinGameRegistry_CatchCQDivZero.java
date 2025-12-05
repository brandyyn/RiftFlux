package com.voidsrift.riftflux.mixin.late;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(value = GameRegistry.class, remap = false)
public abstract class MixinGameRegistry_CatchCQDivZero {

    /**
     * Wrap calls to IWorldGenerator.generate coming from GameRegistry.generateWorld.
     * If a Chocolate Quest generator throws an ArithmeticException (/ by zero in
     * BuilderTemplateSurface.generate), we log it and skip that generator call
     * instead of crashing the server.
     */
    @Redirect(
            method = "generateWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/IWorldGenerator;generate(Ljava/util/Random;IILnet/minecraft/world/World;Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;)V"
            ),
            remap = false
    )
    private static void riftflux$catchCQDivZero(
            IWorldGenerator generator,
            Random random,
            int chunkX,
            int chunkZ,
            World world,
            IChunkProvider chunkGenerator,
            IChunkProvider chunkProvider
    ) {
        try {
            generator.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        } catch (ArithmeticException e) {
            // Only swallow the CQ worldgen bug; rethrow for everything else
            String name = generator.getClass().getName();
            if (name.startsWith("com.chocolate.chocolateQuest.")
                    || name.startsWith("chocolate.mods.BD.")) {
                System.err.println("[RiftFlux] Suppressed ArithmeticException from CQ worldgen ("
                        + name + ") at chunk " + chunkX + "," + chunkZ + ": " + e);
                e.printStackTrace();
                // skipped
            } else {
                throw e;
            }
        }
    }
}
