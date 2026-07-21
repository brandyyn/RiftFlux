package gravestone.structures.catacombs.components;

import gravestone.structures.BoundingBoxHelper;
import gravestone.structures.catacombs.CatacombsLevel;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class Stairs extends CatacombsBaseComponent {
   public static final int X_LENGTH = 5;
   public static final int HEIGHT = 16;
   public static final int Z_LENGTH = 13;

   public Stairs(int direction, int level, Random random, int x, int y, int z) {
      super(direction, level);
      y = y - 16 + 4;
      this.boundingBox = BoundingBoxHelper.getCorrectBox(direction, x, y, z, 5, 16, 13, this.xShift);
      this.goTop = true;
      this.topXEnd = 0;
      this.topZEnd = 13;
   }

   public boolean addComponentParts(World world, Random random) {
      int metaBot = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 2);
      int metaTop = this.getMetadataWithOffset(Blocks.stone_brick_stairs, 3) + 4;
      int top = this.boundingBox.maxY - this.boundingBox.minY - 1;
      this.fillWithBlocks(world, this.boundingBox, 0, top + 1, 0, 4, top + 1, 0, Blocks.nether_brick, Blocks.nether_brick, false);

      for(int i = 0; i < 3; ++i) {
         int shiftZ = i * 4;
         int shiftY = top - i * 4 + 1;
         this.fillWithAir(world, this.boundingBox, 1, shiftY - 4, shiftZ, 3, shiftY - 1, shiftZ + 1);
         this.fillWithAir(world, this.boundingBox, 1, shiftY - 5, shiftZ + 1, 3, shiftY - 2, shiftZ + 2);
         this.fillWithAir(world, this.boundingBox, 1, shiftY - 6, shiftZ + 2, 3, shiftY - 3, shiftZ + 3);
         this.fillWithAir(world, this.boundingBox, 1, shiftY - 7, shiftZ + 3, 3, shiftY - 4, shiftZ + 4);
         this.fillWithBlocks(world, this.boundingBox, 0, shiftY - 4, shiftZ, 0, shiftY, shiftZ, Blocks.nether_brick, Blocks.nether_brick, false);
         this.fillWithBlocks(world, this.boundingBox, 4, shiftY - 4, shiftZ, 4, shiftY, shiftZ, Blocks.nether_brick, Blocks.nether_brick, false);
         this.fillWithRandomizedBlocks(world, this.boundingBox, 0, shiftY - 5, shiftZ + 1, 0, shiftY - 1, shiftZ + 1, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 4, shiftY - 5, shiftZ + 1, 4, shiftY - 1, shiftZ + 1, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 0, shiftY - 6, shiftZ + 2, 0, shiftY - 2, shiftZ + 2, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 4, shiftY - 6, shiftZ + 2, 4, shiftY - 2, shiftZ + 2, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 0, shiftY - 7, shiftZ + 3, 0, shiftY - 3, shiftZ + 3, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 4, shiftY - 7, shiftZ + 3, 4, shiftY - 3, shiftZ + 3, false, random, this.getCemeteryCatacombsStones());
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 4, shiftZ, 3, shiftY - 4, shiftZ, Blocks.nether_brick_stairs, metaBot, Blocks.nether_brick_stairs, metaBot, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 5, shiftZ + 1, 3, shiftY - 5, shiftZ + 1, Blocks.nether_brick_stairs, metaBot, Blocks.nether_brick_stairs, metaBot, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 6, shiftZ + 2, 3, shiftY - 6, shiftZ + 2, Blocks.nether_brick_stairs, metaBot, Blocks.nether_brick_stairs, metaBot, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 7, shiftZ + 3, 3, shiftY - 7, shiftZ + 3, Blocks.nether_brick_stairs, metaBot, Blocks.nether_brick_stairs, metaBot, false);
         Block stairsBlock = CatacombsLevel.getCatacombsStairsId(this.level);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 1, shiftZ + 1, 3, shiftY - 1, shiftZ + 1, stairsBlock, metaTop, stairsBlock, metaTop, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 2, shiftZ + 2, 3, shiftY - 2, shiftZ + 2, stairsBlock, metaTop, stairsBlock, metaTop, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 3, shiftZ + 3, 3, shiftY - 3, shiftZ + 3, stairsBlock, metaTop, stairsBlock, metaTop, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 4, shiftZ + 4, 3, shiftY - 4, shiftZ + 4, stairsBlock, metaTop, stairsBlock, metaTop, false);
         this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 1, shiftY - 3, shiftZ, 1, shiftY - 3, shiftZ, Blocks.web, Blocks.web, false);
         this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 3, shiftY - 3, shiftZ + 1, 3, shiftY - 3, shiftZ + 1, Blocks.web, Blocks.web, false);
         this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 2, shiftY - 5, shiftZ + 2, 2, shiftY - 5, shiftZ + 2, Blocks.web, Blocks.web, false);
         this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 3, shiftY - 5, shiftZ + 3, 3, shiftY - 5, shiftZ + 3, Blocks.web, Blocks.web, false);
      }

      this.fillWithBlocks(world, this.boundingBox, 0, 0, 12, 4, 0, 12, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 0, 12, 0, 4, 12, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 4, 0, 12, 4, 4, 12, false, random, this.getCemeteryCatacombsStones());
      return true;
   }
}
