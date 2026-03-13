/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.IWorldGenerator
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.IChunkProvider
 */
package tk.nukeduck.hearts.event;

import cpw.mods.fml.common.IWorldGenerator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import tk.nukeduck.hearts.HeartCrystal;
import tk.nukeduck.hearts.registry.HeartsBlocks;

public class WorldGeneratorHearts
implements IWorldGenerator {
    private static final int MAX_FLOOR_SEARCH_DEPTH = 8;
    private static final int SAFE_CHUNK_BORDER = 1;
    private static final int SAFE_CHUNK_WIDTH = 14;

    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId != 0) {
            return;
        }
        this.generateSurface(world, random, chunkX * 16, chunkZ * 16);
    }

    private void generateSurface(World world, Random random, int chunkX, int chunkZ) {
        if (HeartCrystal.config == null) {
            return;
        }
        int genCount = Math.max(0, HeartCrystal.config.getGenCount());
        int maxHeight = Math.min(world.getActualHeight(), Math.max(2, HeartCrystal.config.getGenHeight()));
        if (genCount == 0 || maxHeight <= 1) {
            return;
        }

        for (int i = 0; i < genCount; ++i) {
            if (random.nextBoolean()) {
                continue;
            }

            int x = chunkX + SAFE_CHUNK_BORDER + random.nextInt(SAFE_CHUNK_WIDTH);
            int z = chunkZ + SAFE_CHUNK_BORDER + random.nextInt(SAFE_CHUNK_WIDTH);
            int y = 1 + random.nextInt(maxHeight - 1);

            int candidateY = this.findCrystalFloor(world, x, y, z);
            if (candidateY < 1) {
                continue;
            }

            Block target = world.getBlock(x, candidateY, z);
            if (!target.isReplaceable((IBlockAccess)world, x, candidateY, z)) {
                continue;
            }

            Block ground = world.getBlock(x, candidateY - 1, z);
            if (ground.getMaterial() != Material.rock) {
                continue;
            }

            world.setBlock(x, candidateY, z, (Block)HeartsBlocks.crystal, 0, 0);
        }
    }

    private int findCrystalFloor(World world, int x, int startY, int z) {
        int y = startY;
        int depth = 0;

        while (y > 1 && depth < MAX_FLOOR_SEARCH_DEPTH) {
            Block current = world.getBlock(x, y, z);
            if (!current.isReplaceable((IBlockAccess)world, x, y, z)) {
                return -1;
            }

            Block ground = world.getBlock(x, y - 1, z);
            if (!ground.isReplaceable((IBlockAccess)world, x, y - 1, z)) {
                return y;
            }

            --y;
            ++depth;
        }

        return -1;
    }
}
