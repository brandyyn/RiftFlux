package gravestone.structures.graves;

import gravestone.config.GraveStoneConfig;
import gravestone.structures.GSStructureGenerator;
import gravestone.structures.catacombs.CatacombsGenerator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class SingleGraveGenerator implements GSStructureGenerator {
   private static SingleGraveGenerator instance;
   public static final double CHANCE = 0.1D;
   public static final byte RANGE = 100;
   private static LinkedList<ChunkCoordIntPair> structuresList = new LinkedList<>();

   private SingleGraveGenerator() {
      instance = this;
   }

   public static SingleGraveGenerator getInstance() {
      return instance == null ? new SingleGraveGenerator() : instance;
   }

   public boolean generate(World world, Random rand, int x, int z, double chance, boolean isCommand) {
      if (isCommand || GraveStoneConfig.generateSingleGraves && canSpawnStructureAtCoords(world, x, z, chance)) {
         if (!isCommand) {
            x += 7;
            z += 7;
         }

         (new ComponentGSSingleGrave(rand.nextInt(4), rand, x, z)).addComponentParts(world, rand);
         structuresList.add(new ChunkCoordIntPair(x, z));
         return true;
      } else {
         return false;
      }
   }

   protected static boolean canSpawnStructureAtCoords(World world, int x, int z, double chance) {
      return chance < 0.1D && isBiomeAllowed(world, x, z) && noAnyInRange(x, z);
   }

   protected static boolean isBiomeAllowed(World world, int x, int z) {
      LinkedList<Type> biomeTypesList = new LinkedList<>(Arrays.asList(BiomeDictionary.getTypesForBiome(world.getBiomeGenForCoords(x, z))));
      return !biomeTypesList.contains(Type.WATER) && (GraveStoneConfig.generateGravesInMushroomBiomes || !BiomeDictionary.getTypesForBiome(world.getBiomeGenForCoords(x, z)).equals(Type.MUSHROOM));
   }

   protected static boolean noAnyInRange(int x, int z) {
      for(ChunkCoordIntPair position : structuresList) {
         if (position.chunkXPos > x - 100 && position.chunkXPos < x + 100 && position.chunkZPos > z - 100 && position.chunkZPos < z + 100) {
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
}
