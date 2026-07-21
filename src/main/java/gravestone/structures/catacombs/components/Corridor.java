package gravestone.structures.catacombs.components;

import gravestone.config.GraveStoneConfig;
import gravestone.core.GSBlock;
import gravestone.structures.BoundingBoxHelper;
import gravestone.structures.MobSpawnHelper;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class Corridor extends CatacombsBaseComponent {
   public static final int X_LENGTH = 5;
   public static final int HEIGHT = 5;
   public static final int Z_LENGTH = 5;

   public Corridor(int direction, int level, Random random, int x, int y, int z) {
      super(direction, level);
      this.boundingBox = BoundingBoxHelper.getCorrectBox(direction, x, y, z, 5, 5, 5, this.xShift);
      this.topXEnd = 0;
      this.topZEnd = 4;
      switch(direction) {
      case 0:
         this.leftXEnd = 4;
         this.leftZEnd = 0;
         this.rightXEnd = 0;
         this.rightZEnd = 0;
         break;
      case 1:
         this.leftXEnd = 4;
         this.leftZEnd = 4;
         this.rightXEnd = 0;
         this.rightZEnd = 4;
         break;
      case 2:
         this.leftXEnd = 0;
         this.leftZEnd = 4;
         this.rightXEnd = 4;
         this.rightZEnd = 4;
         break;
      case 3:
         this.leftXEnd = 0;
         this.leftZEnd = 0;
         this.rightXEnd = 4;
         this.rightZEnd = 0;
      }

   }

   public boolean addComponentParts(World world, Random random) {
      this.fillWithAir(world, this.boundingBox, 1, 1, 0, 3, 3, 3);
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 0, 1, 4, 0, 3, false, random, this.getCemeteryCatacombsStones());
      if (GraveStoneConfig.generatePilesOfBones) {
         this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.35F, 1, 1, 1, 4, 1, 4, GSBlock.pileOfBones, GSBlock.pileOfBones, false);
      }

      this.fillWithBlocks(world, this.boundingBox, 0, 0, 0, 4, 0, 0, GSBlock.trap, GSBlock.trap, false);
      this.fillWithBlocks(world, this.boundingBox, 0, 4, 0, 4, 4, 3, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 1, 1, 0, 3, 3, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 4, 1, 1, 4, 3, 3, false, random, this.getCemeteryCatacombsStones());
      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 0, 4, 4, 4, 4, false, random, this.getCemeteryCatacombsStones());
      this.fillWithBlocks(world, this.boundingBox, 0, 1, 0, 0, 3, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 4, 1, 0, 4, 3, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      MobSpawnHelper.spawnBats(world, random, this.boundingBox);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 1, 3, 2, 1, 3, 2, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 1, 2, 1, 1, 2, 1, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 3, 2, 3, 3, 2, 3, Blocks.web, Blocks.web, false);
      this.randomlyFillWithBlocks(world, this.boundingBox, random, 0.2F, 3, 1, 0, 3, 1, 0, Blocks.web, Blocks.web, false);
      return true;
   }

   public boolean canGoOnlyTop() {
      return false;
   }
}
