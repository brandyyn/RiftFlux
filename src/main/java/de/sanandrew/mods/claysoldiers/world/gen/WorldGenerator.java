/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.IWorldGenerator
 */
package de.sanandrew.mods.claysoldiers.world.gen;

import cpw.mods.fml.common.IWorldGenerator;
import de.sanandrew.mods.claysoldiers.util.ModConfig;
import de.sanandrew.mods.claysoldiers.world.gen.feature.WorldGenClayHut;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;

public class WorldGenerator
implements IWorldGenerator {
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if ((chunkGenerator instanceof ChunkProviderGenerate || chunkGenerator instanceof ChunkProviderFlat) && ModConfig.clayHutSpawnChance > 0 && random.nextInt(ModConfig.clayHutSpawnChance) == 0) {
            WorldGenClayHut.generate(world, random, chunkX, chunkZ);
        }
    }
}

