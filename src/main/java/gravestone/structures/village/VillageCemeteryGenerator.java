package gravestone.structures.village;

import gravestone.structures.GSStructureGenerator;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

public class VillageCemeteryGenerator implements GSStructureGenerator {
   private static VillageCemeteryGenerator instance;

   private VillageCemeteryGenerator() {
      instance = this;
   }

   public static VillageCemeteryGenerator getInstance() {
      return instance == null ? new VillageCemeteryGenerator() : instance;
   }

   public boolean generate(World world, Random rand, int x, int z, double chance, boolean isCommand) {
      if (isCommand) {
         StructureBoundingBox boundingBox = ComponentGSVillageCemetery.getBoundingBox(x, z);
         (new ComponentGSVillageCemetery(new Start(), 0, rand, boundingBox, 0)).addComponentParts(world, rand, boundingBox);
         return true;
      } else {
         return false;
      }
   }
}
