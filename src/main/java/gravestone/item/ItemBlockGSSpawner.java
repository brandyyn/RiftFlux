package gravestone.item;

import gravestone.block.enums.EnumSpawner;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockGSSpawner extends ItemBlock {
   public ItemBlockGSSpawner(Block block) {
      super(block);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int damageValue) {
      return damageValue;
   }

   public String getUnlocalizedName(ItemStack itemstack) {
      return EnumSpawner.values()[itemstack.getItemDamage()].getName();
   }
}
