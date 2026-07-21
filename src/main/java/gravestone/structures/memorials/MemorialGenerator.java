package gravestone.structures.memorials;

import gravestone.config.GraveStoneConfig;
import gravestone.core.logger.GSLogger;
import gravestone.structures.GSStructureGenerator;
import gravestone.structures.catacombs.CatacombsGenerator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MemorialGenerator implements GSStructureGenerator {
   private static MemorialGenerator instance;
   public static final double CHANCE = 0.05D;
   public static final short RANGE = 400;
   private static LinkedList<ChunkCoordIntPair> structuresList = new LinkedList<>();

   private MemorialGenerator() {
      instance = this;
   }

   public static MemorialGenerator getInstance() {
      return instance == null ? new MemorialGenerator() : instance;
   }

   public boolean generate(World world, Random rand, int x, int z, double chance, boolean isCommand) {
      if (!isCommand) {
         x += 6;
         z += 6;
      }

      if (!isCommand && (!GraveStoneConfig.generateMemorials || !canSpawnStructureAtCoords(world, x, z, chance) || !isNoWarterUnder(world, x, z))) {
         return false;
      } else {
         (new ComponentGSMemorial(rand.nextInt(4), rand, x, z)).addComponentParts(world, rand);
         GSLogger.logInfo("Generate memorial at " + x + "x" + z);
         structuresList.add(new ChunkCoordIntPair(x, z));
         return true;
      }
   }

   protected static boolean canSpawnStructureAtCoords(World world, int x, int z, double chance) {
      return chance < 0.05D && isBiomeAllowed(world, x, z) && noAnyInRange(x, z);
   }

   protected static boolean isBiomeAllowed(World world, int x, int z) {
      LinkedList<Type> biomeTypesList = new LinkedList<>(Arrays.asList(BiomeDictionary.getTypesForBiome(world.getBiomeGenForCoords(x, z))));
      return !biomeTypesList.contains(Type.WATER);
   }

   protected static boolean noAnyInRange(int x, int z) {
      for(ChunkCoordIntPair position : structuresList) {
         if (position.chunkXPos > x - 400 && position.chunkXPos < x + 400 && position.chunkZPos > z - 400 && position.chunkZPos < z + 400) {
            return false;
         }
      }

      for(ChunkCoordIntPair position : CatacombsGenerator.getStructuresList()) {
         if (position.chunkXPos > x - 100 && position.chunkXPos < x + 100 && position.chunkZPos > z - 100 && position.chunkZPos < z + 100) {
            return false;
         }
      }

      return true;
   }

   public static LinkedList<ChunkCoordIntPair> getStructuresList() {
      return structuresList;
   }

   private static boolean isNoWarterUnder(World world, int x, int z) {
      int y = world.getTopSolidOrLiquidBlock(x, z);
      return !world.getBlock(x, y, z).getMaterial().equals(Material.water);
   }
}
