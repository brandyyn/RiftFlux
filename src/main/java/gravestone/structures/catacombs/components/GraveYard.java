package gravestone.structures.catacombs.components;

import gravestone.block.BlockGSGraveStone;
import gravestone.block.GraveStoneHelper;
import gravestone.structures.GraveGenerationHelper;
import gravestone.tileentity.TileEntityGSGraveStone;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class GraveYard extends CatacombsBaseComponent {
   private final List<TileEntityGSGraveStone> generatedGraves = new ArrayList<TileEntityGSGraveStone>();

   public GraveYard(int direction, Random random, StructureBoundingBox structureBoundingBox) {
      super(direction);
      this.boundingBox = structureBoundingBox;
   }

   public boolean addComponentParts(World world, Random random) {
      int graveMeta = GraveStoneHelper.getMetaDirection(this.coordBaseMode);

      for(int x = 0; x < 11; x += 2) {
         for(int z = 0; z < 11; z += 2) {
            if (random.nextDouble() < 0.05D) {
               int positionX = this.getXWithOffset(x + 1, z + 1);
               int positionZ = this.getZWithOffset(x + 1, z + 1);
               int y = world.getTopSolidOrLiquidBlock(positionX, positionZ) - this.boundingBox.minY;
               this.placeBlockAtCurrentPosition(world, Blocks.deadbush, 0, x + 1, y, z + 1, this.boundingBox);
            }

            if (random.nextInt(5) < 2) {
               int positionX = this.getXWithOffset(x, z);
               int positionZ = this.getZWithOffset(x, z);
               int y = world.getTopSolidOrLiquidBlock(positionX, positionZ) - this.boundingBox.minY;
               if (GraveGenerationHelper.canPlaceGrave(world, positionX, this.boundingBox.minY + y, positionZ, this.boundingBox.maxY)) {
                  byte graveType = GraveStoneHelper.getGraveType(world, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0), random, BlockGSGraveStone.EnumGraveType.PLAYER_GRAVES);
                  Item sword = GraveStoneHelper.getRandomSwordForGeneration(graveType, random);
                  GraveGenerationHelper.placeGrave(this, world, random, x, y, z, graveMeta, graveType, sword, false);
                  TileEntityGSGraveStone grave = (TileEntityGSGraveStone)world.getTileEntity(positionX, this.boundingBox.minY + y, positionZ);
                  if (grave != null) {
                     this.generatedGraves.add(grave);
                  }
               }
            }
         }
      }

      return true;
   }

   public List<TileEntityGSGraveStone> getGeneratedGraves() {
      return this.generatedGraves;
   }
}
