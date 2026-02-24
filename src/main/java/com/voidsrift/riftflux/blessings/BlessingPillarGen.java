package com.voidsrift.riftflux.blessings;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class BlessingPillarGen implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (!ModConfig.blessingsEnabled || !ModConfig.blessingsPillarGenEnabled) {
            return;
        }
        if (world.provider.dimensionId != 0) {
            return;
        }
        if (ModConfig.blessingsPillarGenChance <= 0) {
            return;
        }
        if (random.nextInt(ModConfig.blessingsPillarGenChance) != 0) {
            return;
        }

        Block pillar = BlessingContent.blessingPillar;
        if (pillar == null) {
            return;
        }

        int max = Math.max(1, ModConfig.blessingsPillarMaxPerChunk);
        int toPlace = random.nextInt(max) + 1;
        int added = 0;
        boolean placed = false;

        for (int y = 127; y > 30 && !placed && added < toPlace; --y) {
            for (int x = chunkX * 16; x < chunkX * 16 + 16 && !placed && added < toPlace; ++x) {
                for (int z = chunkZ * 16; z < chunkZ * 16 + 16 && !placed && added < toPlace; ++z) {
                    if (random.nextInt(15) != 0) {
                        continue;
                    }
                    if (world.isAirBlock(x, y, z)) {
                        continue;
                    }
                    Block ground = world.getBlock(x, y, z);
                    if (ground == null || ground.isReplaceable((IBlockAccess) world, x, y, z)) {
                        continue;
                    }
                    if (ground == Blocks.leaves) {
                        continue;
                    }
                    if (!pillar.canPlaceBlockAt(world, x, y + 1, z)) {
                        continue;
                    }
                    if (!world.isAirBlock(x, y + 1, z) || !world.isAirBlock(x, y + 2, z)) {
                        continue;
                    }

                    world.setBlock(x, y + 1, z, pillar, 0, 2);
                    world.setBlock(x, y + 2, z, pillar, 1, 2);
                    placed = random.nextBoolean();
                    y -= 10;
                    added++;
                }
            }
        }
    }
}
