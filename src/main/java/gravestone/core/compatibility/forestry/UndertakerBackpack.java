package gravestone.core.compatibility.forestry;

import forestry.api.storage.IBackpackDefinition;
import gravestone.ModGraveStone;
import gravestone.core.GSBlock;
import gravestone.core.GSItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class UndertakerBackpack implements IBackpackDefinition {
   protected List<ItemStack> items = new ArrayList<>(45);
   protected static final List allowedBlocks = Arrays.asList(GSBlock.graveStone, GSBlock.memorial, GSBlock.candle, GSBlock.skullCandle);
   protected static final List allowedItems = Arrays.asList(GSItem.chisel, GSItem.corpse, Items.skull);
   private static UndertakerBackpack instance;

   public static UndertakerBackpack getInstance() {
      if (instance == null) {
         instance = new UndertakerBackpack();
      }

      return instance;
   }

   public String getKey() {
      return "UNDERTAKER";
   }

   public String getName() {
      return null;
   }

   public String getName(ItemStack backpack) {
      return ModGraveStone.proxy.getLocalizedString(backpack.getItem().getUnlocalizedName());
   }

   public int getPrimaryColour() {
      return 1842478;
   }

   public int getSecondaryColour() {
      return 3552587;
   }

   public void addValidItem(ItemStack item) {
      if (item != null) {
         this.items.add(item);
      }

   }

   public void addValidItems(List<ItemStack> validItems) {
      for(ItemStack item : validItems) {
         this.addValidItem(item);
      }

   }

   public boolean isValidItem(EntityPlayer player, ItemStack itemstack) {
      return this.isValidItem(itemstack);
   }

   public boolean isValidItem(ItemStack itemstack) {
      return allowedBlocks.contains(Block.getBlockFromItem(itemstack.getItem())) || allowedItems.contains(itemstack.getItem());
   }
}
