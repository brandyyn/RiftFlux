package gravestone.structures.catacombs.components;

import gravestone.core.GSBlock;
import gravestone.structures.BoundingBoxHelper;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class Entrance extends CatacombsBaseComponent {
   public static final int X_LENGTH = 4;
   private int stairsLength;
   private int corridorLength;

   public Entrance(int direction, Random random, int x, int y, int z) {
      super(direction);
      this.stairsLength = 4 + random.nextInt(4);
      this.corridorLength = 2 + random.nextInt(2);
      this.boundingBox = BoundingBoxHelper.getCorrectBox(direction, x, y - this.stairsLength * 3, z, 4, this.stairsLength * 3, (this.stairsLength + this.corridorLength) * 3 + 5, this.xShift);
      switch(direction) {
      case 0:
         this.leftXEnd = 3;
         this.leftZEnd = (this.stairsLength + this.corridorLength) * 3;
         this.rightXEnd = 0;
         this.rightZEnd = this.leftZEnd;
         break;
      case 1:
         this.leftXEnd = 3;
         this.leftZEnd = (this.stairsLength + this.corridorLength) * 3 + 4;
         this.rightXEnd = 0;
         this.rightZEnd = this.leftZEnd;
         break;
      case 2:
         this.leftXEnd = 0;
         this.leftZEnd = (this.stairsLength + this.corridorLength) * 3 + 4;
         this.rightXEnd = 3;
         this.rightZEnd = this.leftZEnd;
         break;
      case 3:
         this.leftXEnd = 0;
         this.leftZEnd = (this.stairsLength + this.corridorLength) * 3;
         this.rightXEnd = 3;
         this.rightZEnd = this.leftZEnd;
      }

   }

   public boolean addComponentParts(World world, Random random) {
      int top = this.boundingBox.maxY - this.boundingBox.minY - 1;
      int metaBot = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 2);
      int metaTop = this.getMetadataWithOffset(Blocks.stone_brick_stairs, 3) + 4;
      int shiftY = top;
      int shiftZ = 0;

      for(int i = 0; i < this.stairsLength; ++i) {
         shiftZ = i * 3;
         shiftY = top - i * 3 + 1;
         this.fillWithAir(world, this.boundingBox, 1, shiftY, shiftZ, 2, shiftY, shiftZ + 3);
         this.fillWithAir(world, this.boundingBox, 1, shiftY - 1, shiftZ + 1, 2, shiftY - 1, shiftZ + 4);
         this.fillWithAir(world, this.boundingBox, 1, shiftY - 2, shiftZ + 2, 2, shiftY - 2, shiftZ + 5);
         this.fillWithBlocks(world, this.boundingBox, 0, shiftY, shiftZ, 0, shiftY + 4, shiftZ, Blocks.nether_brick, Blocks.nether_brick, false);
         this.fillWithBlocks(world, this.boundingBox, 3, shiftY, shiftZ, 3, shiftY + 4, shiftZ, Blocks.nether_brick, Blocks.nether_brick, false);
         this.fillWithRandomizedBlocks(world, this.boundingBox, 0, shiftY - 2, shiftZ + 1, 0, shiftY + 3, shiftZ + 2, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 3, shiftY - 2, shiftZ + 1, 3, shiftY + 3, shiftZ + 2, false, random, this.getCemeteryCatacombsStones());
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY, shiftZ, 2, shiftY, shiftZ, Blocks.nether_brick_stairs, metaBot, Blocks.nether_brick_stairs, metaBot, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 1, shiftZ + 1, 2, shiftY - 1, shiftZ + 1, Blocks.nether_brick_stairs, metaBot, Blocks.nether_brick_stairs, metaBot, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 2, shiftZ + 2, 2, shiftY - 2, shiftZ + 2, Blocks.nether_brick_stairs, metaBot, Blocks.nether_brick_stairs, metaBot, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY, shiftZ + 4, 2, shiftY, shiftZ + 4, Blocks.stone_brick_stairs, metaTop, Blocks.stone_brick_stairs, metaTop, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 1, shiftZ + 5, 2, shiftY - 1, shiftZ + 5, Blocks.stone_brick_stairs, metaTop, Blocks.stone_brick_stairs, metaTop, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 1, shiftY - 2, shiftZ + 6, 2, shiftY - 2, shiftZ + 6, Blocks.stone_brick_stairs, metaTop, Blocks.stone_brick_stairs, metaTop, false);
      }

      ++shiftY;
      shiftZ = shiftZ + 3;
      int zLength = this.corridorLength * 3;
      this.fillWithAir(world, this.boundingBox, 1, shiftY - 2, shiftZ, 2, shiftY - 1, shiftZ);
      this.fillWithAir(world, this.boundingBox, 1, shiftY - 3, shiftZ + 1, 2, shiftY - 1, shiftZ + zLength + 4);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 0, shiftY, shiftZ, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 3, shiftY, shiftZ, this.boundingBox);
      this.fillWithBlocks(world, this.boundingBox, 0, shiftY, shiftZ + 1, 3, shiftY, shiftZ + zLength + 4, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 0, shiftY - 4, shiftZ, 3, shiftY - 4, shiftZ + zLength + 4, GSBlock.trap, GSBlock.trap, false);

      for(int j = 0; j < this.corridorLength; ++j) {
         this.fillWithBlocks(world, this.boundingBox, 0, shiftY - 3, shiftZ, 0, shiftY - 1, shiftZ, Blocks.nether_brick, Blocks.nether_brick, false);
         this.fillWithBlocks(world, this.boundingBox, 3, shiftY - 3, shiftZ, 3, shiftY - 1, shiftZ, Blocks.nether_brick, Blocks.nether_brick, false);
         this.fillWithRandomizedBlocks(world, this.boundingBox, 0, shiftY - 3, shiftZ + 1, 0, shiftY - 1, shiftZ + 2, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 3, shiftY - 3, shiftZ + 1, 3, shiftY - 1, shiftZ + 2, false, random, this.getCemeteryCatacombsStones());
         this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.3F, shiftY - 1, shiftY - 3, shiftZ + 1, shiftY - 1, shiftY - 3, shiftZ + 1, Blocks.web, Blocks.web, false);
         this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.3F, shiftY - 2, shiftY - 3, shiftZ + 2, shiftY - 2, shiftY - 3, shiftZ + 2, Blocks.web, Blocks.web, false);
         shiftZ += 3;
      }

      shiftZ = shiftZ + 4;
      this.fillWithRandomizedBlocks(world, this.boundingBox, 1, shiftY - 3, shiftZ, 2, shiftY - 1, shiftZ, false, random, this.getCemeteryCatacombsStones());
      return true;
   }

   public boolean canGoOnlyTop() {
      return false;
   }
}
