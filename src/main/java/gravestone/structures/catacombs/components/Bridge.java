package gravestone.structures.catacombs.components;

import gravestone.block.BlockGSGraveStone;
import gravestone.block.GraveStoneHelper;
import gravestone.structures.BoundingBoxHelper;
import gravestone.structures.GraveGenerationHelper;
import gravestone.structures.MobSpawnHelper;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class Bridge extends CatacombsBaseComponent {
   public static final int X_LENGTH = 13;
   public static final int HEIGHT = 14;
   public static final int Z_LENGTH = 7;

   public Bridge(int direction, int level, Random random, int x, int y, int z) {
      super(direction, level);
      this.xShift = 4;
      y = y - 14 + 6;
      this.boundingBox = BoundingBoxHelper.getCorrectBox(direction, x, y, z, 13, 14, 7, this.xShift);
      this.yEnd = 8;
      this.topXEnd = 4;
      this.topZEnd = 7;
      this.goTop = true;
   }

   public boolean addComponentParts(World world, Random random) {
      this.fillWithAir(world, this.boundingBox, 3, 3, 1, 9, 12, 6);
      this.fillWithAir(world, this.boundingBox, 1, 9, 1, 2, 10, 6);
      this.fillWithAir(world, this.boundingBox, 10, 9, 1, 11, 10, 6);
      this.fillWithBlocks(world, this.boundingBox, 2, 0, 0, 10, 0, 7, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 2, 13, 0, 10, 13, 7, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 3, 1, 0, 9, 12, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 3, 1, 7, 9, 12, 7, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithRandomizedBlocks(world, this.boundingBox, 2, 1, 0, 2, 8, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 2, 11, 0, 2, 12, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 10, 1, 0, 10, 8, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 10, 11, 0, 10, 12, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 1, 8, 0, 1, 8, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 1, 11, 0, 1, 11, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 11, 8, 0, 11, 8, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 11, 11, 0, 11, 11, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 9, 0, 0, 10, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 12, 9, 0, 12, 10, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 1, 9, 0, 2, 10, 0, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 1, 9, 0, 2, 10, 0, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 1, 9, 7, 2, 10, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 1, 9, 7, 2, 10, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 10, 9, 0, 11, 10, 0, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 10, 9, 0, 11, 10, 0, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 10, 9, 7, 11, 10, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 10, 9, 7, 11, 10, 7, false, random, this.getCemeteryCatacombsStones());
      byte graveType = GraveStoneHelper.getGraveType(world, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0), random, BlockGSGraveStone.EnumGraveType.PLAYER_GRAVES);
      Item sword = GraveStoneHelper.getRandomSwordForGeneration(graveType, random);
      int metaLeft = GraveStoneHelper.getMetaDirection(getLeftItemDirection(this.coordBaseMode));
      int metaRight = GraveStoneHelper.getMetaDirection(getRightItemDirection(this.coordBaseMode));
      GraveGenerationHelper.fillGraves(this, world, random, 1, 9, 1, 1, 9, 6, metaLeft, graveType, sword, true);
      GraveGenerationHelper.fillGraves(this, world, random, 11, 9, 1, 11, 9, 6, metaRight, graveType, sword, true);
      this.fillWithBlocks(world, this.boundingBox, 3, 1, 1, 9, 2, 6, Blocks.lava, Blocks.lava, false);
      this.fillWithMetadataBlocks(world, this.boundingBox, 6, 8, 1, 6, 8, 6, Blocks.stone_slab, 14, Blocks.stone_slab, 14, false);
      if (random.nextInt(10) < 4) {
         this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 6, 8, 5, this.boundingBox);
      }

      this.fillWithRandomizedBlocks(world, this.boundingBox, 5, 9, 7, 7, 11, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithAir(world, this.boundingBox, 5, 9, 0, 7, 11, 0);
      MobSpawnHelper.spawnBats(world, random, this.boundingBox);
      return true;
   }
}
