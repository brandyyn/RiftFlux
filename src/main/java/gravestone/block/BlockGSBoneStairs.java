package gravestone.block;

import gravestone.core.GSBlock;
import gravestone.core.GSTabs;
import gravestone.core.Resources;
import net.minecraft.block.BlockStairs;

public class BlockGSBoneStairs extends BlockStairs {
   public BlockGSBoneStairs() {
      super(GSBlock.boneBlock, 0);
      this.setBlockName("bone_stairs");
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setBlockTextureName(Resources.BONE_BLOCK);
      this.setHarvestLevel("pickaxe", 0);
   }
}
