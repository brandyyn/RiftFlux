package gravestone.structures.catacombs.components;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class Fence extends CatacombsBaseComponent {
   private final boolean haveEntrance;
   private final boolean haveCorners;
   public static final int ENTRANCE_HEIGHT = 7;

   public Fence(int direction, Random random, StructureBoundingBox structureBoundingBox, boolean haveEntrance, boolean haveCorners) {
      super(direction);
      this.boundingBox = structureBoundingBox;
      this.haveEntrance = haveEntrance;
      this.haveCorners = haveCorners;
   }

   public boolean addComponentParts(World world, Random random) {
      if (this.haveEntrance) {
         this.createEntrance(world, random);
      } else {
         this.createCenterFence(world, random);
      }

      for(int i = 0; i < 10; ++i) {
         this.createPartOfFence(world, random, Fence.FENCE_DIRECTION.RIGHT, i * 4 + 48);
         this.createPartOfFence(world, random, Fence.FENCE_DIRECTION.LEFT, 41 - i * 4);
      }

      this.createGrate(world, 1);
      this.createGrate(world, 88);
      if (this.haveCorners) {
         this.createCornerFence(world, random, true);
         this.createCornerFence(world, random, false);
      }

      return true;
   }

   private void createPartOfFence(World world, Random random, Fence.FENCE_DIRECTION direction, int x) {
      switch(direction) {
      case LEFT: {
         this.createGrate(world, x);
         this.createGrate(world, x - 1);
         this.createGrate(world, x - 2);
         int y = this.getGroundY(world, x - 3);
         if (this.checkGround(world, x - 3, y)) {
            this.fillWithRandomizedBlocks(world, this.boundingBox, x - 3, y, 0, x - 3, y + 3, 0, false, random, this.getCemeteryCatacombsStones());
         }
         break;
      }
      case RIGHT: {
         this.createGrate(world, x);
         this.createGrate(world, x + 1);
         this.createGrate(world, x + 2);
         int y = this.getGroundY(world, x + 3);
         if (this.checkGround(world, x + 3, y)) {
            this.fillWithRandomizedBlocks(world, this.boundingBox, x + 3, y, 0, x + 3, y + 3, 0, false, random, this.getCemeteryCatacombsStones());
         }
      }
      }

   }

   private void createCenterFence(World world, Random random) {
      int y = this.getGroundY(world, 42);
      if (this.checkGround(world, 42, y)) {
         this.fillWithRandomizedBlocks(world, this.boundingBox, 42, y, 0, 42, y + 3, 0, false, random, this.getCemeteryCatacombsStones());
      }

      this.createGrate(world, 43);
      this.createGrate(world, 44);
      this.createGrate(world, 45);
      this.createGrate(world, 46);
      y = this.getGroundY(world, 47);
      if (this.checkGround(world, 47, y)) {
         this.fillWithRandomizedBlocks(world, this.boundingBox, 47, y, 0, 47, y + 3, 0, false, random, this.getCemeteryCatacombsStones());
      }

   }

   private void createCornerFence(World world, Random random, boolean left) {
      byte x;
      if (left) {
         x = 89;
      } else {
         x = 0;
      }

      int y = this.getGroundY(world, x);
      if (this.checkGround(world, x, y)) {
         this.fillWithRandomizedBlocks(world, this.boundingBox, x, y, 0, x, y + 3, 0, false, random, this.getCemeteryCatacombsStones());
      }

   }

   private int getGroundY(World world, int x) {
      int xPos = this.getXWithOffset(x, 0);
      int zPos = this.getZWithOffset(x, 0);

      int y;
      for(y = world.getTopSolidOrLiquidBlock(xPos, zPos); world.getBlock(xPos, y, zPos).getMaterial().equals(Material.wood) || world.getBlock(xPos, y, zPos).getMaterial().equals(Material.leaves); --y) {
      }

      return y;
   }

   private boolean checkGround(World world, int x, int y) {
      Block block = world.getBlock(this.getXWithOffset(x, 0), y, this.getZWithOffset(x, 0));
      if (block == null) {
         return true;
      } else {
         return !block.equals(Blocks.water) && !block.equals(Blocks.lava);
      }
   }

   private boolean checkGround(World world, int startX, int endX, int y) {
      for(int x = startX; x <= endX; ++x) {
         Block block = world.getBlock(this.getXWithOffset(x, 0), y, this.getZWithOffset(x, 0));
         if (block != null && (block.equals(Blocks.water) || block.equals(Blocks.lava))) {
            return false;
         }
      }

      return true;
   }

   private void createGrate(World world, int x) {
      int y = this.getGroundY(world, x);
      if (this.checkGround(world, x, y)) {
         this.fillWithBlocks(world, this.boundingBox, x, y, 0, x, y + 3, 0, Blocks.iron_bars, Blocks.iron_bars, false);
      }

   }

   private void createEntrance(World world, Random random) {
      int y = 0;

      for(int x = 42; x <= 47; ++x) {
         int xPos = this.getXWithOffset(x, 0);
         int zPos = this.getZWithOffset(x, 0);

         int yPos;
         for(yPos = world.getTopSolidOrLiquidBlock(xPos, zPos); world.getBlock(xPos, y, zPos).getMaterial().equals(Material.wood) || world.getBlock(xPos, y, zPos).getMaterial().equals(Material.leaves); --yPos) {
         }

         y += yPos;
      }

      y = y / 6;
      if (this.checkGround(world, 42, 47, y)) {
         this.fillWithRandomizedBlocks(world, this.boundingBox, 42, y, 0, 42, y + 3, 0, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 47, y, 0, 47, y + 3, 0, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 43, y + 4, 0, 43, y + 4, 0, false, random, this.getCemeteryCatacombsStones());
         this.fillWithRandomizedBlocks(world, this.boundingBox, 46, y + 4, 0, 46, y + 4, 0, false, random, this.getCemeteryCatacombsStones());
         this.fillWithBlocks(world, this.boundingBox, 43, y, 0, 43, y + 3, 0, Blocks.iron_bars, Blocks.iron_bars, false);
         this.fillWithBlocks(world, this.boundingBox, 46, y, 0, 46, y + 3, 0, Blocks.iron_bars, Blocks.iron_bars, false);
         this.fillWithBlocks(world, this.boundingBox, 44, y + 3, 0, 45, y + 4, 0, Blocks.iron_bars, Blocks.iron_bars, false);
         this.fillWithMetadataBlocks(world, this.boundingBox, 44, y + 5, 0, 45, y + 5, 0, Blocks.stone_slab, 5, Blocks.stone_slab, 5, false);
         this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 42, y + 4, 0, this.boundingBox);
         this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 47, y + 4, 0, this.boundingBox);
      }

   }

   private static enum FENCE_DIRECTION {
      LEFT,
      RIGHT;
   }
}
