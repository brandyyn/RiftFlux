package gravestone.item;

import gravestone.block.enums.EnumTrap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockGSTrap extends ItemBlock {
   public ItemBlockGSTrap(Block block) {
      super(block);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int damageValue) {
      return damageValue;
   }

   public String getUnlocalizedName(ItemStack itemstack) {
      return EnumTrap.values()[itemstack.getItemDamage()].getName();
   }
}
