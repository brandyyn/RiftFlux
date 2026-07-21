package gravestone.structures.village;

import gravestone.block.GraveStoneHelper;
import gravestone.core.GSBlock;
import gravestone.tileentity.TileEntityGSGraveStone;
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

public class ComponentGSVillageCemetery extends Village {
   private int averageGroundLevel = -1;
   private static final int HEIGHT = 2;
   private static final int X_LENGTH = 12;
   private static final int Z_LENGTH = 10;

   public ComponentGSVillageCemetery() {
   }

   public ComponentGSVillageCemetery(Start startPiece, int componentType, Random random, StructureBoundingBox structureBoundingBox, int direction) {
      super(startPiece, componentType);
      this.coordBaseMode = direction;
      this.boundingBox = structureBoundingBox;
   }

   public static ComponentGSVillageCemetery buildComponent(Start startPiece, List list, Random random, int par3, int par4, int par5, int direction, int componentType) {
      StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 12, 2, 10, direction);
      return canVillageGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null ? new ComponentGSVillageCemetery(startPiece, componentType, random, structureBoundingBox, direction) : null;
   }

   public static StructureBoundingBox getBoundingBox(int x, int z) {
      return new StructureBoundingBox(x, 64, z, x + 12, 66, z + 10);
   }

   public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
      if (this.averageGroundLevel < 0) {
         this.averageGroundLevel = this.getAverageGroundLevel(world, structureBoundingBox);
         if (this.averageGroundLevel < 0) {
            return true;
         }

         this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 2 - 2, 0);
      }

      this.fillWithAir(world, structureBoundingBox, 0, 1, 0, 0, 1, 10);
      this.fillWithAir(world, structureBoundingBox, 12, 1, 0, 12, 1, 10);
      this.fillWithAir(world, structureBoundingBox, 0, 1, 0, 12, 1, 0);
      this.fillWithAir(world, structureBoundingBox, 0, 1, 10, 12, 1, 10);
      this.fillWithAir(world, structureBoundingBox, 2, 1, 2, 2, 1, 8);
      this.fillWithAir(world, structureBoundingBox, 4, 1, 2, 4, 1, 8);
      this.fillWithAir(world, structureBoundingBox, 6, 1, 2, 6, 1, 8);
      this.fillWithAir(world, structureBoundingBox, 8, 1, 2, 8, 1, 8);
      this.fillWithAir(world, structureBoundingBox, 10, 1, 2, 10, 1, 8);
      this.fillWithAir(world, structureBoundingBox, 3, 1, 2, 9, 1, 2);
      this.fillWithAir(world, structureBoundingBox, 3, 1, 4, 9, 1, 4);
      this.fillWithAir(world, structureBoundingBox, 3, 1, 6, 9, 1, 6);
      this.fillWithAir(world, structureBoundingBox, 3, 1, 8, 9, 1, 8);
      int fenceMeta;
      if (this.coordBaseMode != 1 && this.coordBaseMode != 3) {
         fenceMeta = 0;
      } else {
         fenceMeta = 1;
      }

      Block wall = Blocks.cobblestone_wall;
      int biomeId = world.getBiomeGenForCoords(this.getXWithOffset(0, 0), this.getZWithOffset(0, 0)).biomeID;
      int wallMeta;
      Block ground;
      if (biomeId != BiomeGenBase.desert.biomeID && biomeId != BiomeGenBase.desertHills.biomeID) {
         wallMeta = 1;
         ground = Blocks.grass;
      } else {
         wallMeta = 0;
         ground = Blocks.sand;
      }

      this.fillWithBlocks(world, structureBoundingBox, 0, -5, 0, 11, 0, 9, ground, ground, false);

      for(int x = 1; x < 12; ++x) {
         this.placeBlockAtCurrentPosition(world, wall, wallMeta, x, 1, 1, structureBoundingBox);
         this.placeBlockAtCurrentPosition(world, wall, wallMeta, x, 1, 9, structureBoundingBox);
      }

      for(int z = 1; z < 10; ++z) {
         this.placeBlockAtCurrentPosition(world, wall, wallMeta, 1, 1, z, structureBoundingBox);
         this.placeBlockAtCurrentPosition(world, wall, wallMeta, 11, 1, z, structureBoundingBox);
      }

      this.placeBlockAtCurrentPosition(world, Blocks.fence_gate, fenceMeta, 6, 1, 1, structureBoundingBox);
      this.placeBlockAtCurrentPosition(world, Blocks.fence_gate, fenceMeta, 6, 1, 9, structureBoundingBox);
      int graveMeta = GraveStoneHelper.getMetaDirection(this.coordBaseMode);
      byte graveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getPlayerGraveTypes(world, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0)), random);
      for(int x = 3; x < 11; x += 2) {
         for(int z = 3; z < 9; z += 2) {
            this.placeGrave(world, random, x, 1, z, graveMeta, graveType, structureBoundingBox);
         }
      }

      for(int x = 0; x < 11; ++x) {
         for(int z = 0; z < 9; ++z) {
            this.clearCurrentPositionBlocksUpwards(world, x, 2, z, structureBoundingBox);
            this.func_151554_b(world, ground, 0, x, -1, z, structureBoundingBox);
         }
      }

      return true;
   }

   protected void placeGrave(World world, Random random, int x, int y, int z, int graveMeta, byte graveType, StructureBoundingBox structureBoundingBox) {
      int xCoord = this.getXWithOffset(x, z);
      int yCoord = this.getYWithOffset(y);
      int zCoord = this.getZWithOffset(x, z);
      this.placeBlockAtCurrentPosition(world, GSBlock.graveStone, graveMeta, x, y, z, this.boundingBox);
      TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)world.getTileEntity(xCoord, yCoord, zCoord);
      if (tileEntity != null) {
         tileEntity.setGraveType(graveType);
         tileEntity.setGraveContent(random, false, true);
      }

   }
}
