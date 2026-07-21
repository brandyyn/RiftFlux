package gravestone.structures.catacombs;

import gravestone.config.GraveStoneConfig;
import gravestone.structures.catacombs.components.CatacombsBaseComponent;
import gravestone.structures.catacombs.components.Fence;
import gravestone.structures.catacombs.components.GraveYard;
import gravestone.structures.catacombs.components.Mausoleum;
import gravestone.structures.catacombs.components.MausoleumEntrance;
import gravestone.structures.GraveGenerationHelper;
import gravestone.tileentity.TileEntityGSGraveStone;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CatacombsSurface {
   private final byte DIRECTION;
   private final int X;
   private final int Z;
   private int mausoleumX;
   private int mausoleumY;
   private int mausoleumZ;
   private final List<TileEntityGSGraveStone> graveyardGraves = new ArrayList<TileEntityGSGraveStone>();

   public CatacombsSurface(World world, Random rand, int x, int z, int direction) {
      this.DIRECTION = (byte)direction;
      this.X = x;
      this.Z = z;
      this.build(world, rand);
   }

   private void build(World world, Random rand) {
      this.buildMausoleum(world, rand);
      int xCoord = 0;
      int zCoord = 0;
      switch(this.DIRECTION) {
      case 0:
         xCoord = this.X;
         zCoord = this.Z - 14;
         break;
      case 1:
         xCoord = this.X + 14;
         zCoord = this.Z;
         break;
      case 2:
         xCoord = this.X;
         zCoord = this.Z + 14;
         break;
      case 3:
         xCoord = this.X - 14;
         zCoord = this.Z;
      }

      (new MausoleumEntrance(this.DIRECTION, rand, new StructureBoundingBox(xCoord, 60, zCoord, 13 + xCoord, 90, 13 + zCoord), this.mausoleumY)).addComponentParts(world, rand);
      if (GraveStoneConfig.generateCatacombsGraveyard) {
         switch(this.DIRECTION) {
         case 0:
            this.buildGraveYard(world, rand, this.X + 15, this.Z + 2);
            this.buildGraveYard(world, rand, this.X - 12, this.Z + 2);
            this.buildGraveYard(world, rand, this.X + 2, this.Z + 15);
            break;
         case 1:
            this.buildGraveYard(world, rand, this.X + 2, this.Z + 15);
            this.buildGraveYard(world, rand, this.X + 2, this.Z - 12);
            this.buildGraveYard(world, rand, this.X - 11, this.Z + 2);
            break;
         case 2:
            this.buildGraveYard(world, rand, this.X + 15, this.Z + 2);
            this.buildGraveYard(world, rand, this.X - 12, this.Z + 2);
            this.buildGraveYard(world, rand, this.X + 2, this.Z - 13);
            break;
         case 3:
            this.buildGraveYard(world, rand, this.X + 2, this.Z + 15);
            this.buildGraveYard(world, rand, this.X + 2, this.Z - 12);
            this.buildGraveYard(world, rand, this.X + 15, this.Z + 2);
         }

         for(byte shiftX = 1; shiftX < 4; ++shiftX) {
            for(byte shiftZ = 1; shiftZ < 4; ++shiftZ) {
               xCoord = this.X + shiftX * 12 + 3;
               zCoord = this.Z + shiftZ * 12 + 3;
               this.buildGraveYard(world, rand, xCoord, zCoord);
               xCoord = this.X - shiftX * 12;
               zCoord = this.Z - shiftZ * 12;
               this.buildGraveYard(world, rand, xCoord, zCoord);
               xCoord = this.X + shiftX * 12 + 3;
               zCoord = this.Z - shiftZ * 12;
               this.buildGraveYard(world, rand, xCoord, zCoord);
               xCoord = this.X - shiftX * 12;
               zCoord = this.Z + shiftZ * 12 + 3;
               this.buildGraveYard(world, rand, xCoord, zCoord);
            }
         }

         this.buildGraveYard(world, rand, this.X + 27, this.Z + 2);
         this.buildGraveYard(world, rand, this.X - 24, this.Z + 2);
         this.buildGraveYard(world, rand, this.X + 2, this.Z + 27);
         this.buildGraveYard(world, rand, this.X + 2, this.Z - 24);
         this.buildGraveYard(world, rand, this.X + 39, this.Z + 2);
         this.buildGraveYard(world, rand, this.X - 36, this.Z + 2);
         this.buildGraveYard(world, rand, this.X + 2, this.Z + 39);
         this.buildGraveYard(world, rand, this.X + 2, this.Z - 36);
         if (this.DIRECTION != 0 && this.DIRECTION != 2) {
            if (this.DIRECTION == 1) {
               (new Fence(this.DIRECTION, rand, new StructureBoundingBox(this.X + 51, 0, this.Z - 38, this.X + 51, 255, this.Z + 51), true, false)).addComponentParts(world, rand);
               (new Fence(this.DIRECTION, rand, new StructureBoundingBox(this.X - 38, 0, this.Z - 38, this.X - 38, 255, this.Z + 51), false, false)).addComponentParts(world, rand);
            } else {
               (new Fence(this.DIRECTION, rand, new StructureBoundingBox(this.X + 51, 0, this.Z - 38, this.X + 51, 255, this.Z + 51), false, false)).addComponentParts(world, rand);
               (new Fence(this.DIRECTION, rand, new StructureBoundingBox(this.X - 38, 0, this.Z - 38, this.X - 38, 255, this.Z + 51), true, false)).addComponentParts(world, rand);
            }

            (new Fence(CatacombsBaseComponent.getLeftDirection(this.DIRECTION), rand, new StructureBoundingBox(this.X - 38, 0, this.Z - 38, this.X + 51, 240, this.Z - 38), false, true)).addComponentParts(world, rand);
            (new Fence(CatacombsBaseComponent.getLeftDirection(this.DIRECTION), rand, new StructureBoundingBox(this.X - 38, 0, this.Z + 51, this.X + 51, 240, this.Z + 51), false, true)).addComponentParts(world, rand);
         } else {
            if (this.DIRECTION == 0) {
               (new Fence(this.DIRECTION, rand, new StructureBoundingBox(this.X - 38, 0, this.Z - 38, this.X + 51, 240, this.Z - 38), true, true)).addComponentParts(world, rand);
               (new Fence(this.DIRECTION, rand, new StructureBoundingBox(this.X - 38, 0, this.Z + 51, this.X + 51, 240, this.Z + 51), false, true)).addComponentParts(world, rand);
            } else {
               (new Fence(this.DIRECTION, rand, new StructureBoundingBox(this.X - 38, 0, this.Z - 38, this.X + 51, 240, this.Z - 38), false, true)).addComponentParts(world, rand);
               (new Fence(this.DIRECTION, rand, new StructureBoundingBox(this.X - 38, 0, this.Z + 51, this.X + 51, 240, this.Z + 51), true, true)).addComponentParts(world, rand);
            }

            (new Fence(CatacombsBaseComponent.getLeftDirection(this.DIRECTION), rand, new StructureBoundingBox(this.X - 38, 0, this.Z - 38, this.X - 38, 255, this.Z + 51), false, false)).addComponentParts(world, rand);
            (new Fence(CatacombsBaseComponent.getLeftDirection(this.DIRECTION), rand, new StructureBoundingBox(this.X + 51, 0, this.Z - 38, this.X + 51, 255, this.Z + 51), false, false)).addComponentParts(world, rand);
         }

         GraveGenerationHelper.addEyeboneLoot(this.graveyardGraves, rand, GraveStoneConfig.generateEyeboneInGraveyards);
      }

   }

   private void buildMausoleum(World world, Random rand) {
      CatacombsBaseComponent mausoleum = new Mausoleum(this.DIRECTION, rand, new StructureBoundingBox(this.X, 60, this.Z, 13 + this.X, 90, 13 + this.Z));
      mausoleum.addComponentParts(world, rand);
      this.mausoleumX = mausoleum.getTopXEnd();
      this.mausoleumY = mausoleum.getYEnd();
      this.mausoleumZ = mausoleum.getTopZEnd();
   }

   private void buildGraveYard(World world, Random rand, int x, int z) {
      GraveYard graveYard = new GraveYard(this.DIRECTION, rand, new StructureBoundingBox(x, 4, z, x + 11, 255, z + 11));
      graveYard.addComponentParts(world, rand);
      this.graveyardGraves.addAll(graveYard.getGeneratedGraves());
   }

   public int getMausoleumX() {
      return this.mausoleumX;
   }

   public int getMausoleumY() {
      return this.mausoleumY;
   }

   public int getMausoleumZ() {
      return this.mausoleumZ;
   }
}
