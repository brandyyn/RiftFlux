package gravestone.structures.catacombs.components;

import gravestone.core.GSStructures;
import gravestone.structures.ComponentGraveStone;
import gravestone.structures.catacombs.CatacombsLevel;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent.BlockSelector;

public abstract class CatacombsBaseComponent extends ComponentGraveStone {
   protected static final float PILE_OF_BONES_GENERATION_CHANCE = 0.35F;
   public boolean goTop = true;
   protected int leftXEnd = 0;
   protected int rightXEnd = 0;
   protected int topXEnd = 0;
   protected int leftZEnd = 0;
   protected int rightZEnd = 0;
   protected int topZEnd = 0;
   protected int yEnd = 0;
   protected int xShift = 0;
   protected int zShift = 0;
   protected int level = 0;
   protected CatacombsBaseComponent prevComponent;
   protected CatacombsBaseComponent[] nextComponents;

   protected CatacombsBaseComponent(int direction) {
      this(direction, 0);
   }

   protected CatacombsBaseComponent(int direction, int level) {
      super(direction);
      this.coordBaseMode = direction;
      this.level = level;
   }

   public static int getLeftDirection(int direction) {
      --direction;
      if (direction < 0) {
         direction = 3;
      }

      return direction;
   }

   public static int getRightDirection(int direction) {
      ++direction;
      if (direction > 3) {
         direction = 0;
      }

      return direction;
   }

   public static int getInvertDirection(int direction) {
      direction = direction + 2;
      if (direction > 3) {
         direction -= 4;
      }

      return direction;
   }

   public static int getLeftItemDirection(int direction) {
      if (direction != 0 && direction != 1) {
         --direction;
         if (direction < 0) {
            direction = 3;
         }
      } else {
         ++direction;
         if (direction > 3) {
            direction = 0;
         }
      }

      return direction;
   }

   public static int getRightItemDirection(int direction) {
      if (direction != 0 && direction != 1) {
         ++direction;
         if (direction > 3) {
            direction = 0;
         }
      } else {
         --direction;
         if (direction < 0) {
            direction = 3;
         }
      }

      return direction;
   }

   public static Block getValuableBlock(Random random) {
      return GSStructures.VALUEBLE_BLOCKS[random.nextInt(GSStructures.VALUEBLE_BLOCKS.length)];
   }

   protected int getGroundLevel(World world, int x, int z) {
      return world.getTopSolidOrLiquidBlock(x, z);
   }

   protected int invertDirection(int direction) {
      return 0;
   }

   public int getLeftXEnd() {
      return this.getXWithOffset(this.leftXEnd, this.leftZEnd);
   }

   public int getLeftZEnd() {
      return this.getZWithOffset(this.leftXEnd, this.leftZEnd);
   }

   public int getRightXEnd() {
      return this.getXWithOffset(this.rightXEnd, this.rightZEnd);
   }

   public int getRightZEnd() {
      return this.getZWithOffset(this.rightXEnd, this.rightZEnd);
   }

   public int getTopZEnd() {
      return this.getZWithOffset(this.topXEnd, this.topZEnd);
   }

   public int getTopXEnd() {
      return this.getXWithOffset(this.topXEnd, this.topZEnd);
   }

   public int getYEnd() {
      return this.boundingBox.minY + this.yEnd;
   }

   public BlockSelector getCemeteryCatacombsStones() {
      return CatacombsLevel.getCatacombsStones(this.level);
   }

   public boolean canBePlacedHere(StructureBoundingBox boundingBox) {
      if (this.coordBaseMode != 0 && this.coordBaseMode != 2) {
         return this.boundingBox.maxX - 1 > boundingBox.minX && this.boundingBox.minX + 1 < boundingBox.maxX && this.boundingBox.maxZ > boundingBox.minZ && this.boundingBox.minZ < boundingBox.maxZ;
      } else {
         return this.boundingBox.maxX > boundingBox.minX && this.boundingBox.minX < boundingBox.maxX && this.boundingBox.maxZ - 1 > boundingBox.minZ && this.boundingBox.minZ + 1 < boundingBox.maxZ;
      }
   }

   public boolean canGoOnlyTop() {
      return true;
   }

   protected void func_151554_b(World world, Block block, int metadata, int xCoord, int yCoord, int zCoord, StructureBoundingBox boundingBox) {
      int x = this.getXWithOffset(xCoord, zCoord);
      int y = this.getYWithOffset(yCoord);

      for(int z = this.getZWithOffset(xCoord, zCoord); (world.isAirBlock(x, y, z) || world.getBlock(x, y, z).getMaterial().isLiquid() || world.getBlock(x, y, z).getMaterial().isReplaceable()) && y > 1; --y) {
         world.setBlock(x, y, z, block, metadata, 2);
      }

   }

   public int getDirection() {
      return this.coordBaseMode;
   }
}
