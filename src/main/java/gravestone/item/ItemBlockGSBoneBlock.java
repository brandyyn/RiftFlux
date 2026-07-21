package gravestone.item;

import gravestone.block.enums.EnumBoneBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockGSBoneBlock extends ItemBlock {
   public ItemBlockGSBoneBlock(Block block) {
      super(block);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int damageValue) {
      return damageValue;
   }

   public String getUnlocalizedName(ItemStack itemstack) {
      return EnumBoneBlock.values()[itemstack.getItemDamage()].getName();
   }
}
