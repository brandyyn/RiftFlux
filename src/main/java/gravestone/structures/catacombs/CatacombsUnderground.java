package gravestone.structures.catacombs;

import gravestone.config.GraveStoneConfig;
import gravestone.structures.GraveGenerationHelper;
import gravestone.structures.ObjectsGenerationHelper;
import gravestone.structures.catacombs.components.CatacombsBaseComponent;
import gravestone.tileentity.TileEntityGSGraveStone;
import java.util.List;
import gravestone.structures.catacombs.components.Entrance;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class CatacombsUnderground {
   private final int direction;
   private CatacombsBaseComponent entrance;

   public CatacombsUnderground(World world, Random rand, int direction, int x, int y, int z) {
      this.direction = direction;
      this.prepareStructurePieces(rand, x, y, z);
      this.build(world, rand);
   }

   private void prepareStructurePieces(Random rand, int x, int y, int z) {
      this.entrance = new Entrance(this.direction, rand, x, y, z);
   }

   public final void build(World world, Random rand) {
      this.entrance.addComponentParts(world, rand);
      LinkedList<CatacombsBaseComponent> startComponents = new LinkedList<>();
      startComponents.add(this.entrance);
      List<TileEntityGSGraveStone> undergroundGraves;
      CatacombsLevel[] levels = new CatacombsLevel[4];
      GraveGenerationHelper.beginGraveCollection();
      try {
         levels[0] = new CatacombsLevel(startComponents, 1, world, rand);
         levels[1] = new CatacombsLevel(levels[0].getEndParts(), 2, world, rand);
         levels[2] = new CatacombsLevel(levels[1].getEndParts(), 3, world, rand);
         levels[3] = new CatacombsLevel(levels[2].getEndParts(), 4, world, rand);
      } finally {
         undergroundGraves = GraveGenerationHelper.endGraveCollection();
      }
      GraveGenerationHelper.addEyeboneLoot(undergroundGraves, rand, GraveStoneConfig.generateEyeboneInCatacombsGrave);
      TileEntityChest lowestLevelChest = ObjectsGenerationHelper.generateLowestLevelEyeboneChest(rand, levels[3].getGeneratedChests());
      ObjectsGenerationHelper.generateRandomLevelEyeboneChest(world, rand, levels, lowestLevelChest);
   }
}
