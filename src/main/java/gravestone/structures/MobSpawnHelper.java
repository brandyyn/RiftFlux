package gravestone.structures;

import java.util.Random;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class MobSpawnHelper {
   private MobSpawnHelper() {
   }

   public static void spawnBats(World world, Random random, StructureBoundingBox boundingBox) {
      int batsCount = 3 + random.nextInt(8);

      for(byte i = 0; i < batsCount; ++i) {
         EntityBat bat = new EntityBat(world);
         bat.setLocationAndAngles((double)boundingBox.getCenterX() - 1.5D + (double)random.nextInt(5), (double)boundingBox.getCenterY(), (double)boundingBox.getCenterZ() - 1.5D + (double)random.nextInt(5), 0.0F, 0.0F);
         if (bat.getCanSpawnHere()) {
            world.spawnEntityInWorld(bat);
         }
      }

   }
}
