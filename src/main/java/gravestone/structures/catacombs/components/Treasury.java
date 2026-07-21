package gravestone.structures.catacombs.components;

import gravestone.block.BlockGSGraveStone;
import gravestone.block.GraveStoneHelper;
import gravestone.structures.BoundingBoxHelper;
import gravestone.structures.GraveGenerationHelper;
import gravestone.structures.ObjectsGenerationHelper;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class Treasury extends CatacombsBaseComponent {
   public static final int X_LENGTH = 6;
   public static final int HEIGHT = 5;
   public static final int Z_LENGTH = 7;

   public Treasury(int direction, int level, Random random, int x, int y, int z) {
      super(direction, level);
      this.xShift = 1;
      this.boundingBox = BoundingBoxHelper.getCorrectBox(direction, x, y, z, 6, 5, 7, this.xShift);
      this.goTop = false;
   }

   public boolean addComponentParts(World world, Random random) {
      this.fillWithAir(world, this.boundingBox, 1, 1, 2, 5, 3, 6);
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 0, 1, 6, 0, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 4, 1, 6, 4, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 1, 2, 0, 3, 6, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 6, 1, 2, 6, 3, 6, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 1, 1, 6, 3, 1, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 1, 7, 6, 3, 7, false, random, this.getCemeteryCatacombsStones());
      this.fillWithAir(world, this.boundingBox, 2, 1, 1, 4, 3, 1);
      this.fillWithRandomizedBlocks(world, this.boundingBox, 2, 1, 0, 4, 3, 0, false, random, this.getCemeteryCatacombsStones());
      this.fillWithBlocks(world, this.boundingBox, 1, 0, 0, 5, 0, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 4, 0, 5, 4, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 1, 0, 1, 3, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 5, 1, 0, 5, 3, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.4F, 2, 2, 2, 2, 2, 2, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.4F, 4, 1, 3, 4, 1, 3, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.4F, 4, 3, 5, 4, 3, 5, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.4F, 2, 1, 4, 2, 1, 4, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.4F, 1, 2, 5, 1, 2, 5, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.4F, 2, 3, 6, 2, 3, 6, Blocks.web, Blocks.web, false);
      byte graveType = GraveStoneHelper.getGraveType(world, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0), random, BlockGSGraveStone.EnumGraveType.ALL_GRAVES);
      Item sword = GraveStoneHelper.getRandomSwordForGeneration(graveType, random);
      int metaLeft = GraveStoneHelper.getMetaDirection(getLeftItemDirection(this.coordBaseMode));
      int metaRight = GraveStoneHelper.getMetaDirection(getRightItemDirection(this.coordBaseMode));
      GraveGenerationHelper.placeGrave(this, world, random, 1, 1, 2, metaLeft, graveType, sword, true);
      GraveGenerationHelper.placeGrave(this, world, random, 1, 1, 4, metaLeft, graveType, sword, true);
      GraveGenerationHelper.placeGrave(this, world, random, 1, 1, 6, metaLeft, graveType, sword, true);
      GraveGenerationHelper.placeGrave(this, world, random, 5, 1, 2, metaRight, graveType, sword, true);
      GraveGenerationHelper.placeGrave(this, world, random, 5, 1, 4, metaRight, graveType, sword, true);
      GraveGenerationHelper.placeGrave(this, world, random, 5, 1, 6, metaRight, graveType, sword, true);
      this.fillWithBlocks(world, this.boundingBox, 0, 0, 3, 1, 0, 3, Blocks.tnt, Blocks.tnt, false);
      this.fillWithBlocks(world, this.boundingBox, 0, 0, 5, 1, 0, 5, Blocks.tnt, Blocks.tnt, false);
      this.placeBlockAtCurrentPosition(world, Blocks.tnt, 0, 0, 0, 4, this.boundingBox);
      this.fillWithBlocks(world, this.boundingBox, 5, 0, 3, 6, 0, 3, Blocks.tnt, Blocks.tnt, false);
      this.fillWithBlocks(world, this.boundingBox, 5, 0, 5, 6, 0, 5, Blocks.tnt, Blocks.tnt, false);
      this.placeBlockAtCurrentPosition(world, Blocks.tnt, 0, 6, 0, 4, this.boundingBox);
      this.fillWithBlocks(world, this.boundingBox, 3, 0, 6, 3, 0, 7, Blocks.tnt, Blocks.tnt, false);
      ObjectsGenerationHelper.generateChest(this, world, random, 1, 1, 3, false, ObjectsGenerationHelper.EnumChestTypes.VALUABLE_CHESTS);
      ObjectsGenerationHelper.generateChest(this, world, random, 1, 1, 5, false, ObjectsGenerationHelper.EnumChestTypes.VALUABLE_CHESTS);
      ObjectsGenerationHelper.generateChest(this, world, random, 5, 1, 3, false, ObjectsGenerationHelper.EnumChestTypes.VALUABLE_CHESTS);
      ObjectsGenerationHelper.generateChest(this, world, random, 5, 1, 5, false, ObjectsGenerationHelper.EnumChestTypes.VALUABLE_CHESTS);
      ObjectsGenerationHelper.generateChest(this, world, random, 3, 1, 6, false, ObjectsGenerationHelper.EnumChestTypes.VALUABLE_CHESTS);
      Block valuableBlock = getValuableBlock(random);
      this.fillWithBlocks(world, this.boundingBox, 3, 1, 4, 3, 3, 4, valuableBlock, valuableBlock, false);
      return true;
   }
}
