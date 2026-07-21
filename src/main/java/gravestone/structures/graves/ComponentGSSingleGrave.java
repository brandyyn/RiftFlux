package gravestone.structures.graves;

import gravestone.block.BlockGSGraveStone;
import gravestone.block.GraveStoneHelper;
import gravestone.core.logger.GSLogger;
import gravestone.structures.ComponentGraveStone;
import gravestone.structures.GraveGenerationHelper;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentGSSingleGrave extends ComponentGraveStone {
   public ComponentGSSingleGrave(int direction, Random random, int x, int z) {
      super(direction);
      this.boundingBox = new StructureBoundingBox(x, 0, z, x, 240, z);
   }

   public boolean addComponentParts(World world, Random random) {
      int positionX = this.getXWithOffset(0, 0);
      int positionZ = this.getZWithOffset(0, 0);
      int y = world.getTopSolidOrLiquidBlock(positionX, positionZ) - this.boundingBox.minY;
      if (GraveGenerationHelper.canPlaceGrave(world, positionX, this.boundingBox.minY + y, positionZ, this.boundingBox.maxY)) {
         GSLogger.logInfo("Generate grave at " + positionX + "x" + positionZ);
         byte graveType = GraveStoneHelper.getGraveType(world, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0), random, BlockGSGraveStone.EnumGraveType.ALL_GRAVES);
         Item sword = GraveStoneHelper.getRandomSwordForGeneration(graveType, random);
         GraveGenerationHelper.placeGrave(this, world, random, 0, y, 0, GraveStoneHelper.getMetaDirection(this.coordBaseMode), graveType, sword, true);
      }

      return true;
   }

   public NBTTagCompound func_143010_b() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("id", "GSSingleGrave");
      nbttagcompound.setTag("BB", this.boundingBox.func_151535_h());
      nbttagcompound.setInteger("O", this.coordBaseMode);
      nbttagcompound.setInteger("GD", this.componentType);
      this.func_143012_a(nbttagcompound);
      return nbttagcompound;
   }
}
