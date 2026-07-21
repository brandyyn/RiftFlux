package gravestone.structures.memorials;

import gravestone.block.BlockGSMemorial;
import gravestone.structures.BoundingBoxHelper;
import gravestone.structures.ComponentGraveStone;
import gravestone.structures.MemorialGenerationHelper;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class ComponentGSMemorial extends ComponentGraveStone {
   public static final int X_LENGTH = 3;
   public static final int HEIGHT = 7;
   public static final int Z_LENGTH = 3;

   public ComponentGSMemorial(int direction, Random random, int x, int z) {
      super(direction);
      this.boundingBox = BoundingBoxHelper.getCorrectBox(direction, x, 64, z, 3, 7, 3, 0);
   }

   public boolean addComponentParts(World world, Random random) {
      int averageGroundLevel = BoundingBoxHelper.getAverageGroundLevel(world, this.boundingBox);
      if (averageGroundLevel < 0) {
         return true;
      } else {
         this.boundingBox.offset(0, averageGroundLevel - this.boundingBox.maxY + 7 - 1, 0);
         BiomeGenBase biom = world.getBiomeGenForCoords(this.getXWithOffset(0, 0), this.getZWithOffset(0, 0));
         Block ground;
         Block underground;
         if (biom.biomeID != BiomeGenBase.desert.biomeID && biom.biomeID != BiomeGenBase.desertHills.biomeID && biom.biomeID != BiomeGenBase.beach.biomeID) {
            ground = Blocks.grass;
            underground = Blocks.dirt;
         } else {
            ground = Blocks.sand;
            underground = Blocks.sand;
         }

         this.fillWithAir(world, this.boundingBox, 0, 0, 2, 0, 6, 2);
         this.fillWithBlocks(world, this.boundingBox, 0, 0, 0, 2, 0, 2, ground, ground, false);
         byte memorialType = BlockGSMemorial.getMemorialType(world, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0), random, 0);
         MemorialGenerationHelper.placeMemorial(this, world, random, 1, 1, 1, BlockGSMemorial.getMetaDirection(this.coordBaseMode), memorialType);

         for(int x = 0; x < 3; ++x) {
            for(int z = 0; z < 3; ++z) {
               this.func_151554_b(world, underground, 0, x, -1, z, this.boundingBox);
            }
         }

         for(int x = 0; x < 3; ++x) {
            for(int z = 0; z < 3; ++z) {
               this.clearCurrentPositionBlocksUpwards(world, x, 7, z, this.boundingBox);
            }
         }

         return true;
      }
   }
}
