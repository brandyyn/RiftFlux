package gravestone.item;

import gravestone.block.enums.EnumPileOfBones;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockGSPileOfBones extends ItemBlock {
   public ItemBlockGSPileOfBones(Block block) {
      super(block);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.setUnlocalizedName("Pile Of Bones");
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return this.getUnlocalizedName() + "." + EnumPileOfBones.values()[itemStack.getItemDamage()].getName();
   }
}
