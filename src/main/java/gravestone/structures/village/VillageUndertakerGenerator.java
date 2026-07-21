package gravestone.structures.village;

import gravestone.structures.GSStructureGenerator;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

public class VillageUndertakerGenerator implements GSStructureGenerator {
   private static VillageUndertakerGenerator instance;

   private VillageUndertakerGenerator() {
      instance = this;
   }

   public static VillageUndertakerGenerator getInstance() {
      return instance == null ? new VillageUndertakerGenerator() : instance;
   }

   public boolean generate(World world, Random rand, int x, int z, double chance, boolean isCommand) {
      if (isCommand) {
         StructureBoundingBox boundingBox = ComponentGSVillageUndertaker.getBoundingBox(x, z);
         (new ComponentGSVillageUndertaker(new Start(), 0, rand, boundingBox, 0)).addComponentParts(world, rand, boundingBox);
         return true;
      } else {
         return false;
      }
   }
}
