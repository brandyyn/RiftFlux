package gravestone.block;

import gravestone.core.GSTabs;
import gravestone.core.Resources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;

public class BlockGSBoneSlab extends BlockSlab {
   public BlockGSBoneSlab() {
      super(false, Material.rock);
      this.setStepSound(Block.soundTypeStone);
      this.setBlockName("bone_slab");
      this.setHardness(2.0F);
      this.setResistance(2.0F);
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setBlockTextureName(Resources.BONE_BLOCK);
      this.setHarvestLevel("pickaxe", 0);
   }

   public String func_150002_b(int par1) {
      return this.getUnlocalizedName();
   }
}
