package gravestone.item;

import gravestone.block.enums.EnumSkullCandle;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockGSSkullCandle extends ItemBlock {
   public ItemBlockGSSkullCandle(Block block) {
      super(block);
      this.setHasSubtypes(true);
      this.setUnlocalizedName("Skull Candle");
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return this.getUnlocalizedName() + "." + EnumSkullCandle.values()[itemStack.getItemDamage()].getName();
   }
}
