package gravestone.core.recipe;

import gravestone.block.GraveStoneHelper;
import gravestone.core.GSBlock;
import gravestone.core.GSItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class SwordGravestoneRecipe extends ShapelessOreRecipe {
   private final Item sword;

   public SwordGravestoneRecipe(Item sword) {
      super(GraveStoneHelper.getSwordAsGrave(Item.getItemFromBlock(GSBlock.graveStone), new ItemStack(sword)),
              new ItemStack(sword, 1, OreDictionary.WILDCARD_VALUE), GSChiselRegistry.ORE_NAME);
      this.sword = sword;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inventory) {
      for(int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
         ItemStack stack = inventory.getStackInSlot(slot);
         if (stack != null && stack.getItem() == this.sword) {
            ItemStack preservedSword = stack.copy();
            preservedSword.stackSize = 1;
            return GraveStoneHelper.getSwordAsGrave(Item.getItemFromBlock(GSBlock.graveStone), preservedSword);
         }
      }
      return null;
   }
}
