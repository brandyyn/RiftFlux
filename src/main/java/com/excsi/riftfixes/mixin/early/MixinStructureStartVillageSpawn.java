package com.excsi.riftfixes.mixin.early;


import com.excsi.riftfixes.ModConfig;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(StructureStart.class)
public abstract class MixinStructureStartVillageSpawn {

    @Shadow public StructureBoundingBox boundingBox;

    // MCP + SRG signatures — pick whichever your env resolves
    @Inject(
            method = {
                    "generateStructure(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)V",
            },
            at = @At("TAIL")
    )
    private void riftfixes$spawnVillageGolems(World world, Random rand, StructureBoundingBox box, CallbackInfo ci) {
        if (!ModConfig.reworkVillageGolems) return;
        if (!( (Object)this instanceof MapGenVillage.Start)) return; // only villages
        if (world == null || world.isRemote) return;

        final int n = ModConfig.initialVillageGolems;
        if (n <= 0) return;

        // Use the Start’s BB as a coarse center/radius for spawn positions
        final int cx = (boundingBox.minX + boundingBox.maxX) / 2;
        final int cz = (boundingBox.minZ + boundingBox.maxZ) / 2;
        final int rx = Math.max(8, (boundingBox.maxX - boundingBox.minX) / 4);
        final int rz = Math.max(8, (boundingBox.maxZ - boundingBox.minZ) / 4);
        final int radius = Math.max(rx, rz);

        int spawned = 0, attempts = 0, maxAttempts = n * 16;
        while (spawned < n && attempts++ < maxAttempts) {
            int x = cx + rand.nextInt(radius * 2 + 1) - radius;
            int z = cz + rand.nextInt(radius * 2 + 1) - radius;
            int y = world.getTopSolidOrLiquidBlock(x, z);

            // Need 2 blocks of air for a golem
            if (!world.isAirBlock(x, y, z) || !world.isAirBlock(x, y + 1, z)) continue;

            EntityIronGolem g = new EntityIronGolem(world);
            g.setLocationAndAngles(x + 0.5D, y, z + 0.5D, rand.nextFloat() * 360F, 0F);
            if (world.spawnEntityInWorld(g)) spawned++;
        }
    }
}