package gravestone.structures.catacombs;

import gravestone.config.GraveStoneConfig;
import gravestone.core.logger.GSLogger;
import gravestone.structures.GSStructureGenerator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.village.Village;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class CatacombsGenerator implements GSStructureGenerator {
   private static CatacombsGenerator instance;
   private static final int VILLAGE_RANGE = 200;
   public static final byte CATACOMBS_RANGE = 100;
   public static final int CATACOMBS_DISTANCE = 1500;
   public static final int DISTANCE_FROM_SPAWN = 1000;
   public static final double DEFAULT_GENERATION_CHANCE = 2.5E-4D;
   protected static LinkedList<ChunkCoordIntPair> structuresList = new LinkedList<>();

   private CatacombsGenerator() {
      instance = this;
   }

   public static CatacombsGenerator getInstance() {
      return instance == null ? new CatacombsGenerator() : instance;
   }

   public boolean generate(World world, Random rand, int x, int z, double chance, boolean isCommand) {
      if (isCommand || GraveStoneConfig.generateCatacombs && canSpawnStructureAtCoords(world, x, z, chance) && isHeightAcceptable(world, x, z)) {
         int direction = rand.nextInt(4);
         CatacombsSurface surface = new CatacombsSurface(world, rand, x, z, direction);
         GSLogger.logInfo("Generate catacombs at " + x + "x" + z);
         if (surface.getMausoleumY() > 55) {
            new CatacombsUnderground(world, rand, direction, surface.getMausoleumX(), surface.getMausoleumY(), surface.getMausoleumZ());
         }

         structuresList.add(new ChunkCoordIntPair(x, z));
         return true;
      } else {
         return false;
      }
   }

   protected static boolean canSpawnStructureAtCoords(World world, int x, int z, double chance) {
      return chance < GraveStoneConfig.catacombsGenerationChance && isBiomeAllowed(world, x, z) && noAnyInRange(x, z, world);
   }

   protected static boolean isBiomeAllowed(World world, int x, int z) {
      LinkedList<Type> biomeTypesList = new LinkedList<>(Arrays.asList(BiomeDictionary.getTypesForBiome(world.getBiomeGenForCoords(x, z))));
      return !biomeTypesList.contains(Type.WATER) && !biomeTypesList.contains(Type.SWAMP) && !biomeTypesList.contains(Type.JUNGLE) && !biomeTypesList.contains(Type.MAGICAL) && !biomeTypesList.contains(Type.HILLS) && !biomeTypesList.contains(Type.MOUNTAIN) && (biomeTypesList.contains(Type.PLAINS) || biomeTypesList.contains(Type.FOREST) || biomeTypesList.contains(Type.FROZEN) || biomeTypesList.contains(Type.WASTELAND));
   }

   protected static boolean noAnyInRange(int x, int z, World world) {
      GSLogger.logInfo("Catacombs generation - Begin Checking area for another catacombs or villages");

      for(ChunkCoordIntPair position : structuresList) {
         if (checkStructuresInRange(position.chunkXPos, position.chunkZPos, x, z, 1500)) {
            return false;
         }
      }

      if (world.villageCollectionObj != null && world.villageCollectionObj.getVillageList() != null) {
         for(Object villageObj : world.villageCollectionObj.getVillageList()) {
            ChunkCoordinates villageCenter = ((Village)villageObj).getCenter();
            if (checkStructuresInRange(villageCenter.posX, villageCenter.posZ, x, z, 200)) {
               return false;
            }
         }
      }

      if (checkStructuresInRange(world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnZ(), x, z, 1000)) {
         return false;
      } else {
         GSLogger.logInfo("Catacombs generation - End Checking area for another catacombs or villages");
         return true;
      }
   }

   private static boolean checkStructuresInRange(int xPos, int zPos, int x, int z, int range) {
      return xPos > x - range && xPos < x + range && zPos > z - range && zPos < z + range;
   }

   public static LinkedList<ChunkCoordIntPair> getStructuresList() {
      return structuresList;
   }

   private static boolean isHeightAcceptable(World world, int x, int z) {
      GSLogger.logInfo("Catacombs generation - Begin Checking area height");
      int height = 0;
      int count = 0;

      for(int xPos = x; xPos < x + 16; ++xPos) {
         for(int zPos = z; zPos < z + 16; ++zPos) {
            height += world.getTopSolidOrLiquidBlock(xPos, zPos);
            ++count;
         }
      }

      GSLogger.logInfo("Catacombs generation - End Checking area height");
      return height / count < GraveStoneConfig.maxCatacombsHeight;
   }
}
