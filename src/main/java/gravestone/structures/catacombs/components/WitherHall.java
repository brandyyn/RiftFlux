package gravestone.structures.catacombs.components;

import gravestone.core.GSBlock;
import gravestone.core.logger.GSLogger;
import gravestone.structures.BoundingBoxHelper;
import gravestone.structures.MobSpawnHelper;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WitherHall extends CatacombsBaseComponent {
   public static final int X_LENGTH = 23;
   public static final int HEIGHT = 10;
   public static final int Z_LENGTH = 24;
   private int metaTop;
   private int metaBot;
   private int metaRight;
   private int metaLeft;

   public WitherHall(int direction, int level, Random random, int x, int y, int z) {
      super(direction, level);
      this.xShift = 9;
      this.boundingBox = BoundingBoxHelper.getCorrectBox(direction, x, y, z, 23, 10, 24, this.xShift);
      this.goTop = false;
   }

   public boolean addComponentParts(World world, Random random) {
      GSLogger.logInfo("Generate Wither hall at " + this.boundingBox.getCenterX() + "x" + this.boundingBox.getCenterY() + "x" + this.boundingBox.getCenterZ());
      this.metaTop = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 2);
      this.metaBot = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 3);
      this.metaRight = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 1);
      this.metaLeft = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 0);
      this.fillWithAir(world, this.boundingBox, 1, 1, 2, 21, 9, 22);
      this.fillWithBlocks(world, this.boundingBox, 10, 0, 0, 12, 0, 4, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 5, 0, 5, 17, 0, 16, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 10, 1, 21, 10, 22, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 0, 0, 1, 0, 10, 23, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 22, 0, 1, 22, 10, 23, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 0, 1, 21, 10, 1, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 0, 23, 21, 10, 23, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithAir(world, this.boundingBox, 10, 1, 0, 12, 3, 1);
      this.fillWithBlocks(world, this.boundingBox, 9, 0, 0, 9, 4, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 13, 0, 0, 13, 4, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 10, 4, 0, 12, 4, 0, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 0, 2, 9, 0, 4, Blocks.lava, Blocks.lava, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 0, 5, 4, 0, 19, Blocks.lava, Blocks.lava, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 0, 20, 8, 0, 22, Blocks.lava, Blocks.lava, false);
      this.fillWithBlocks(world, this.boundingBox, 13, 0, 2, 21, 0, 4, Blocks.lava, Blocks.lava, false);
      this.fillWithBlocks(world, this.boundingBox, 18, 0, 5, 21, 0, 19, Blocks.lava, Blocks.lava, false);
      this.fillWithBlocks(world, this.boundingBox, 14, 0, 20, 21, 0, 22, Blocks.lava, Blocks.lava, false);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 0, 6, 3, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 0, 6, 9, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 0, 6, 15, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 0, 6, 21, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 22, 6, 3, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 22, 6, 9, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 22, 6, 15, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 22, 6, 21, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 4, 6, 23, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 18, 6, 23, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 4, 6, 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 18, 6, 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 9, 6, 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 13, 6, 1, this.boundingBox);
      this.buildFire(world, 6, 1, 6);
      this.buildFire(world, 6, 1, 12);
      this.buildFire(world, 6, 4, 18);
      this.buildFire(world, 16, 1, 6);
      this.buildFire(world, 16, 1, 12);
      this.buildFire(world, 16, 4, 18);
      this.buildColumnLeft(world, 6);
      this.buildColumnLeft(world, 12);
      this.buildColumnLeft(world, 18);
      this.buildColumnRight(world, 6);
      this.buildColumnRight(world, 12);
      this.buildColumnRight(world, 18);
      this.buildLight(world, 6, 6);
      this.buildLight(world, 6, 12);
      this.buildLight(world, 6, 18);
      this.buildLight(world, 16, 6);
      this.buildLight(world, 16, 12);
      this.buildLight(world, 16, 18);
      this.fillWithBlocks(world, this.boundingBox, 9, 1, 12, 13, 1, 14, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 9, 2, 13, 13, 2, 14, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 9, 3, 14, 13, 3, 14, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithMetadataBlocks(world, this.boundingBox, 10, 1, 12, 12, 1, 14, Blocks.nether_brick_stairs, this.metaBot, Blocks.nether_brick_stairs, this.metaBot, false);
      this.fillWithMetadataBlocks(world, this.boundingBox, 10, 2, 13, 12, 2, 14, Blocks.nether_brick_stairs, this.metaBot, Blocks.nether_brick_stairs, this.metaBot, false);
      this.fillWithMetadataBlocks(world, this.boundingBox, 10, 3, 14, 12, 3, 14, Blocks.nether_brick_stairs, this.metaBot, Blocks.nether_brick_stairs, this.metaBot, false);
      this.fillWithBlocks(world, this.boundingBox, 8, 0, 15, 14, 3, 21, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 5, 0, 17, 7, 3, 19, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 15, 0, 17, 17, 3, 19, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 7, 0, 20, 7, 3, 20, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 15, 0, 20, 15, 3, 20, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 9, 0, 22, 13, 2, 22, Blocks.nether_brick, Blocks.nether_brick, false);
      this.placeBlockAtCurrentPosition(world, Blocks.netherrack, 0, 9, 3, 15, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.fire, 0, 9, 4, 15, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.netherrack, 0, 13, 3, 15, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.fire, 0, 13, 4, 15, this.boundingBox);
      this.fillWithBlocks(world, this.boundingBox, 9, 3, 22, 13, 3, 22, Blocks.obsidian, Blocks.obsidian, false);
      this.fillWithBlocks(world, this.boundingBox, 9, 7, 22, 13, 7, 22, Blocks.obsidian, Blocks.obsidian, false);
      this.fillWithBlocks(world, this.boundingBox, 9, 4, 22, 9, 6, 22, Blocks.obsidian, Blocks.obsidian, false);
      this.fillWithBlocks(world, this.boundingBox, 13, 4, 22, 13, 6, 22, Blocks.obsidian, Blocks.obsidian, false);
      this.fillWithBlocks(world, this.boundingBox, 10, 4, 22, 12, 6, 22, Blocks.portal, Blocks.portal, false);
      this.placeBlockAtCurrentPosition(world, GSBlock.spawner, 0, 11, 4, 18, this.boundingBox);
      this.fillWithBlocks(world, this.boundingBox, 10, 3, 17, 12, 3, 19, Blocks.diamond_block, Blocks.diamond_block, false);
      this.placeBlockAtCurrentPosition(world, Blocks.emerald_block, 0, 11, 3, 18, this.boundingBox);
      MobSpawnHelper.spawnBats(world, random, this.boundingBox);
      return true;
   }

   private void buildFire(World world, int x, int y, int z) {
      this.placeBlockAtCurrentPosition(world, Blocks.netherrack, 0, x, y, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.fire, 0, x, y + 1, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot, x - 1, y, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot, x, y, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot, x + 1, y, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaLeft, x - 1, y, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaRight, x + 1, y, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop, x - 1, y, z + 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop, x, y, z + 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop, x + 1, y, z + 1, this.boundingBox);
   }

   private void buildLight(World world, int x, int z) {
      this.placeBlockAtCurrentPosition(world, Blocks.glowstone, 0, x, 9, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot + 4, x - 1, 9, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot + 4, x, 9, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot + 4, x + 1, 9, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaLeft + 4, x - 1, 9, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaRight + 4, x + 1, 9, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop + 4, x - 1, 9, z + 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop + 4, x, 9, z + 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop + 4, x + 1, 9, z + 1, this.boundingBox);
   }

   private void buildColumnLeft(World world, int z) {
      this.fillWithBlocks(world, this.boundingBox, 1, 0, z - 1, 2, 0, z + 1, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 1, 1, z, 1, 9, z, Blocks.nether_brick, Blocks.nether_brick, false);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot, 1, 1, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot, 2, 1, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaRight, 2, 1, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop, 1, 1, z + 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop, 2, 1, z + 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot + 4, 1, 9, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot + 4, 2, 9, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaRight + 4, 2, 9, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop + 4, 1, 9, z + 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop + 4, 2, 9, z + 1, this.boundingBox);
   }

   private void buildColumnRight(World world, int z) {
      this.fillWithBlocks(world, this.boundingBox, 20, 0, z - 1, 21, 0, z + 1, Blocks.nether_brick, Blocks.nether_brick, false);
      this.fillWithBlocks(world, this.boundingBox, 21, 1, z, 21, 9, z, Blocks.nether_brick, Blocks.nether_brick, false);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot, 20, 1, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot, 21, 1, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaLeft, 20, 1, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop, 20, 1, z + 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop, 21, 1, z + 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot + 4, 20, 9, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaBot + 4, 21, 9, z - 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaLeft + 4, 20, 9, z, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop + 4, 20, 9, z + 1, this.boundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.metaTop + 4, 21, 9, z + 1, this.boundingBox);
   }
}
