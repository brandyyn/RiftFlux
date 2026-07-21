package gravestone.structures.village;

import gravestone.block.BlockGSMemorial;
import gravestone.core.GSBlock;
import gravestone.tileentity.TileEntityGSMemorial;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;

public class ComponentGSVillageMemorial extends Village {
   private int averageGroundLevel = -1;
   private static final int HEIGHT = 6;

   public ComponentGSVillageMemorial() {
   }

   public ComponentGSVillageMemorial(Start startPiece, int componentType, Random random, StructureBoundingBox structureBoundingBox, int direction) {
      super(startPiece, componentType);
      this.coordBaseMode = direction;
      this.boundingBox = structureBoundingBox;
   }

   public static ComponentGSVillageMemorial buildComponent(Start startPiece, List list, Random random, int par3, int par4, int par5, int direction, int componentType) {
      StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 5, 6, 5, direction);
      return canVillageGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null ? new ComponentGSVillageMemorial(startPiece, componentType, random, structureBoundingBox, direction) : null;
   }

   public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
      if (this.averageGroundLevel < 0) {
         this.averageGroundLevel = this.getAverageGroundLevel(world, structureBoundingBox);
         if (this.averageGroundLevel < 0) {
            return true;
         }

         this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 6 - 2, 0);
      }

      int biomeId = world.getBiomeGenForCoords(this.getXWithOffset(0, 0), this.getZWithOffset(0, 0)).biomeID;
      Block ground;
      if (biomeId != BiomeGenBase.desert.biomeID && biomeId != BiomeGenBase.desertHills.biomeID) {
         ground = Blocks.grass;
      } else {
         ground = Blocks.sand;
      }

      this.fillWithBlocks(world, structureBoundingBox, 0, -5, 0, 5, 0, 5, ground, ground, false);
      this.placeMemorial(world, random, 2, 1, 2);

      for(int x = 0; x < 5; ++x) {
         for(int z = 0; z < 5; ++z) {
            this.clearCurrentPositionBlocksUpwards(world, x, 6, z, structureBoundingBox);
            this.func_151554_b(world, ground, 0, x, -1, z, structureBoundingBox);
         }
      }

      return true;
   }

   protected void placeMemorial(World world, Random random, int x, int y, int z) {
      int memorialMeta = BlockGSMemorial.getMetaDirection(this.coordBaseMode);
      boolean isTortureMemorial = random.nextInt(4) == 0;
      byte memorialType;
      if (isTortureMemorial) {
         memorialType = (byte)BlockGSMemorial.TORTURE_MEMORIALS[random.nextInt(BlockGSMemorial.TORTURE_MEMORIALS.length)].ordinal();
      } else {
         memorialType = BlockGSMemorial.getMemorialType(world, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0), random, 0);
      }

      this.placeBlockAtCurrentPosition(world, GSBlock.memorial, memorialMeta, x, y, z, this.boundingBox);
      TileEntityGSMemorial tileEntity = (TileEntityGSMemorial)world.getTileEntity(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (tileEntity != null) {
         tileEntity.setGraveType(memorialType);
         if (isTortureMemorial) {
            tileEntity.setRandomMob(random);
         } else {
            tileEntity.setMemorialContent(random);
         }
      }

   }
}
