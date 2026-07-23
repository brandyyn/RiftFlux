package gravestone.structures;

import cpw.mods.fml.common.IWorldGenerator;
import gravestone.config.GraveStoneConfig;
import gravestone.structures.catacombs.CatacombsGenerator;
import gravestone.structures.graves.SingleGraveGenerator;
import gravestone.structures.memorials.MemorialGenerator;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class GraveStoneWorldGenerator implements IWorldGenerator {
   public static final int DEFAULT_DIMENSION_ID = 0;

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
      int x = chunkX * 16;
      int z = chunkZ * 16;
      double chance = random.nextDouble();
      boolean generatedCatacombs = CatacombsGenerator.getInstance().generate(world, random, x, z, chance, false);
      if (world.provider.dimensionId == DEFAULT_DIMENSION_ID && !generatedCatacombs) {
         this.generateSurface(world, random, x, z, chance);
      }

   }

   public void generateSurface(World world, Random rand, int x, int z, double chance) {
      if (!MemorialGenerator.getInstance().generate(world, rand, x, z, chance, false)) {
         SingleGraveGenerator.getInstance().generate(world, rand, x, z, chance, false);
      }

   }
}
