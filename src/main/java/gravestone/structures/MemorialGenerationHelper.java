package gravestone.structures;

import gravestone.block.BlockGSMemorial;
import gravestone.core.GSBlock;
import gravestone.tileentity.TileEntityGSMemorial;
import java.util.Random;
import net.minecraft.world.World;

public class MemorialGenerationHelper {
   private MemorialGenerationHelper() {
   }

   public static void placeMemorial(ComponentGraveStone component, World world, Random random, int x, int y, int z, int memorialMeta, byte memorialType) {
      component.placeBlockAtCurrentPosition(world, GSBlock.memorial, memorialMeta, x, y, z, component.getBoundingBox());
      TileEntityGSMemorial tileEntity = (TileEntityGSMemorial)world.getTileEntity(component.getXWithOffset(x, z), component.getYWithOffset(y), component.getZWithOffset(x, z));
      if (tileEntity != null) {
         tileEntity.setGraveType(memorialType);
         tileEntity.setMemorialContent(random);
         BlockGSMemorial.placeWalls(world, x, y, z);
      }

   }
}
