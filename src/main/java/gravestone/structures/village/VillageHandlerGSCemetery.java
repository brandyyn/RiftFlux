package gravestone.structures.village;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import java.util.List;
import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

public class VillageHandlerGSCemetery implements IVillageCreationHandler {
   public PieceWeight getVillagePieceWeight(Random random, int size) {
      return new PieceWeight(ComponentGSVillageCemetery.class, 4, MathHelper.getRandomIntegerInRange(random, 0, 1));
   }

   public Class getComponentClass() {
      return ComponentGSVillageCemetery.class;
   }

   public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
      return ComponentGSVillageCemetery.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
   }
}
