package gravestone.structures.catacombs.components;

import gravestone.core.GSBlock;
import gravestone.structures.BoundingBoxHelper;
import gravestone.structures.ObjectsGenerationHelper;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class SpidersCorridor extends CatacombsBaseComponent {
   public static final int X_LENGTH = 5;
   public static final int HEIGHT = 5;
   public static final int Z_LENGTH = 13;

   public SpidersCorridor(int direction, int level, Random random, int x, int y, int z) {
      super(direction, level);
      this.boundingBox = BoundingBoxHelper.getCorrectBox(direction, x, y, z, 5, 5, 13, this.xShift);
      this.topXEnd = 0;
      this.topZEnd = 12;
      this.goTop = true;
   }

   public boolean addComponentParts(World world, Random random) {
      this.fillWithAir(world, this.boundingBox, 1, 1, 0, 3, 3, 12);
      this.fillWithBlocks(world, this.boundingBox, 1, 1, 0, 3, 3, 12, Blocks.web, Blocks.web, false);

      for(int i = 0; i < 3; ++i) {
         int z = i * 4;
         this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 0, 1 + z, 4, 0, 3 + z, false, random, this.getCemeteryCatacombsStones());
         this.fillWithBlocks(world, this.boundingBox, 0, 4, 0 + z, 4, 4, 3 + z, Blocks.nether_brick, Blocks.nether_brick, false);
         this.fillWithBlocks(world, this.boundingBox, 0, 0, 0 + z, 4, 0, 0 + z, GSBlock.trap, GSBlock.trap, false);
         this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 1, 1 + z, 0, 3, 3 + z, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 4, 1, 1 + z, 4, 3, 3 + z, false, random, this.getCemeteryCatacombsStones());
         this.fillWithBlocks(world, this.boundingBox, 0, 1, 0 + z, 0, 3, 0 + z, Blocks.nether_brick, Blocks.nether_brick, false);
         this.fillWithBlocks(world, this.boundingBox, 4, 1, 0 + z, 4, 3, 0 + z, Blocks.nether_brick, Blocks.nether_brick, false);
      }

      this.fillWithRandomizedBlocks(world, this.boundingBox, 0, 0, 12, 4, 4, 12, false, random, this.getCemeteryCatacombsStones());
      ObjectsGenerationHelper.generateMinecraftSpawner(this, world, 2, 1, 6, "CaveSpider");
      return true;
   }
}
