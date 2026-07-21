package gravestone.structures.catacombs.components;

import gravestone.block.BlockGSGraveStone;
import gravestone.block.GraveStoneHelper;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSBlock;
import gravestone.structures.BoundingBoxHelper;
import gravestone.structures.GraveGenerationHelper;
import gravestone.structures.MobSpawnHelper;
import gravestone.structures.ObjectsGenerationHelper;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class GraveCorridor extends CatacombsBaseComponent {
   public static final int X_LENGTH = 7;
   public static final int HEIGHT = 5;
   public static final int Z_LENGTH = 5;

   public GraveCorridor(int direction, int level, Random random, int x, int y, int z) {
      super(direction, level);
      this.xShift = 1;
      this.boundingBox = BoundingBoxHelper.getCorrectBox(direction, x, y, z, 7, 5, 5, this.xShift);
      this.goTop = true;
      this.topXEnd = 1;
      this.topZEnd = 4;
   }

   public boolean addComponentParts(World world, Random random) {
      this.fillWithAir(world, this.boundingBox, 1, 1, 1, 5, 3, 3);
      this.fillWithAir(world, this.boundingBox, 2, 1, 0, 4, 3, 0);
      this.fillWithRandomizedBlocks(world, this.boundingBox, 2, 0, 1, 4, 0, 3, false, random, this.getCemeteryCatacombsStones());
      if (GraveStoneConfig.generatePilesOfBones) {
         this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.35F, 2, 1, 2, 5, 1, 3, GSBlock.pileOfBones, GSBlock.pileOfBones, false);
      }

      this.fillWithBlocks(world, this.boundingBox, 1, 0, 0, 5, 0, 0, GSBlock.trap, GSBlock.trap, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 0, 4, 5, 0, 4, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 0, 1, 1, 0, 3, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 5, 0, 1, 5, 0, 3, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 4, 0, 5, 4, 4, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 0, 1, 0, 4, 3, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 6, 0, 1, 6, 4, 3, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 2, 0, 4, 4, 4, 4, false, random, this.getCemeteryCatacombsStones());
      this.fillWithBlocks(world, this.boundingBox, 1, 1, 0, 1, 3, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 1, 4, 1, 3, 4, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 5, 1, 0, 5, 3, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 5, 1, 4, 5, 3, 4, Blocks.nether_brick, Blocks.nether_brick, false);
      byte graveType = GraveStoneHelper.getGraveType(world, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0), random, BlockGSGraveStone.EnumGraveType.ALL_GRAVES);
      Item sword = GraveStoneHelper.getRandomSwordForGeneration(graveType, random);
      int metaLeft = GraveStoneHelper.getMetaDirection(getLeftItemDirection(this.coordBaseMode));
      int metaRight = GraveStoneHelper.getMetaDirection(getRightItemDirection(this.coordBaseMode));
      GraveGenerationHelper.fillGraves(this, world, random, 1, 1, 1, 1, 1, 3, metaLeft, graveType, sword, true);
      GraveGenerationHelper.fillGraves(this, world, random, 5, 1, 1, 5, 1, 3, metaRight, graveType, sword, true);
      if (random.nextInt(5) < 2) {
         ObjectsGenerationHelper.generateChest(this, world, random, 3, 1, 2, true, ObjectsGenerationHelper.EnumChestTypes.ALL_CHESTS);
      }

      MobSpawnHelper.spawnBats(world, random, this.boundingBox);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 2, 2, 1, 2, 2, 1, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 4, 1, 2, 4, 1, 2, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 3, 3, 3, 3, 3, 3, Blocks.web, Blocks.web, false);
      return true;
   }
}
