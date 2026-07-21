package gravestone.structures.catacombs;

import gravestone.core.GSBlock;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureComponent.BlockSelector;

public class CatacombsBoneBlocks extends BlockSelector {
   public void selectBlocks(Random random, int par2, int par3, int par4, boolean flag) {
      if (flag) {
         this.field_151562_a = GSBlock.boneBlock;
         if (random.nextInt(5) == 0) {
            this.selectedBlockMetaData = 1;
         } else {
            this.selectedBlockMetaData = 0;
         }

         if (random.nextInt(100) < 60) {
            this.selectedBlockMetaData += 2;
         }
      } else {
         this.field_151562_a = Blocks.air;
         this.selectedBlockMetaData = 0;
      }

   }
}
