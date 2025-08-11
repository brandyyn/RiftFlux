package com.excsi.riftfixes.mixin.early;


import com.excsi.riftfixes.ModConfig;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/** Spawn N golems exactly once per village at worldgen; no persistent state. */
@Mixin(StructureStart.class)
public abstract class MixinStructureStartVillageSpawn {

    @Shadow public StructureBoundingBox boundingBox;

    @Unique private boolean riftfixes$spawned = false;

    @Inject(
            method = {
                    "generateStructure(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)V",
            },
            at = @At("HEAD")
    )
    private void riftfixes$spawnOnce(World world, Random rand, StructureBoundingBox chunkBox, CallbackInfo ci) {
        if (!ModConfig.reworkVillageGolems) return;
        if (!(((Object) this) instanceof MapGenVillage.Start)) return; // villages only
        if (world == null || world.isRemote) return;
        if (riftfixes$spawned) return;

        // Run only for the village's center chunk
        final int centerX = (boundingBox.minX + boundingBox.maxX) >> 1;
        final int centerZ = (boundingBox.minZ + boundingBox.maxZ) >> 1;
        final int centerChunkX = centerX >> 4;
        final int centerChunkZ = centerZ >> 4;
        final int genChunkX = (chunkBox.minX + 8) >> 4;
        final int genChunkZ = (chunkBox.minZ + 8) >> 4;
        if (genChunkX != centerChunkX || genChunkZ != centerChunkZ) return;

        int n = ModConfig.initialVillageGolems;
        if (n > 0) {
            int rx = Math.max(8, (boundingBox.maxX - boundingBox.minX) / 4);
            int rz = Math.max(8, (boundingBox.maxZ - boundingBox.minZ) / 4);
            int radius = Math.max(rx, rz);

            int spawned = 0, attempts = 0, maxAttempts = n * 20;
            while (spawned < n && attempts++ < maxAttempts) {
                int x = centerX + rand.nextInt(radius * 2 + 1) - radius;
                int z = centerZ + rand.nextInt(radius * 2 + 1) - radius;
                int y = world.getTopSolidOrLiquidBlock(x, z);
                if (!world.isAirBlock(x, y, z) || !world.isAirBlock(x, y + 1, z)) continue;

                EntityIronGolem g = new EntityIronGolem(world);
                g.setLocationAndAngles(x + 0.5D, y, z + 0.5D, rand.nextFloat() * 360F, 0F);
                if (world.spawnEntityInWorld(g)) spawned++;
            }
        }

        riftfixes$spawned = true; // in-memory only; won’t respawn this session
    }
}