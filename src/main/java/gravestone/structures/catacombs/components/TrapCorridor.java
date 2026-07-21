package gravestone.structures.catacombs.components;

import gravestone.core.GSBlock;
import gravestone.structures.BoundingBoxHelper;
import gravestone.structures.ObjectsGenerationHelper;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class TrapCorridor extends CatacombsBaseComponent {
   public static final int X_LENGTH = 6;
   public static final int HEIGHT = 5;
   public static final int Z_LENGTH = 5;

   public TrapCorridor(int direction, int level, Random random, int x, int y, int z) {
      super(direction, level);
      this.xShift = 1;
      this.boundingBox = BoundingBoxHelper.getCorrectBox(direction, x, y, z, 6, 5, 5, this.xShift);
      this.topZEnd = 4;
      this.topXEnd = 1;
   }

   public boolean addComponentParts(World world, Random random) {
      this.fillWithAir(world, this.boundingBox, 2, 1, 0, 4, 3, 3);
      this.fillWithRandomizedBlocks(world, this.boundingBox, 1, 0, 1, 5, 0, 3, false, random, this.getCemeteryCatacombsStones());
      this.fillWithBlocks(world, this.boundingBox, 1, 0, 0, 5, 0, 0, GSBlock.trap, GSBlock.trap, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 4, 0, 5, 4, 3, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithRandomizedBlocks(world, this.boundingBox, 1, 1, 1, 1, 3, 3, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 5, 1, 1, 5, 3, 3, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 1, 0, 4, 5, 4, 4, false, random, this.getCemeteryCatacombsStones());
      this.fillWithBlocks(world, this.boundingBox, 1, 1, 0, 1, 3, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 5, 1, 0, 5, 3, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 0, 1, 2, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 6, 1, 2, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.tripwire_hook, getRightItemDirection(this.coordBaseMode), 1, 1, 2, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.tripwire_hook, getLeftItemDirection(this.coordBaseMode), 5, 1, 2, this.boundingBox);
      this.fillWithBlocks(world, this.boundingBox, 2, 1, 2, 4, 1, 2, Blocks.tripwire, Blocks.tripwire, false);
      ObjectsGenerationHelper.generateDispenser(world, this, random, 0, 2, 2, getLeftItemDirection(this.coordBaseMode));
      ObjectsGenerationHelper.generateDispenser(world, this, random, 6, 2, 2, getRightItemDirection(this.coordBaseMode));
      this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 2, 2, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 5, 2, 2, this.boundingBox);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 2, 1, 1, 2, 1, 1, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 4, 2, 2, 4, 2, 2, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 3, 3, 3, 3, 3, 3, Blocks.web, Blocks.web, false);
      return true;
   }
}
