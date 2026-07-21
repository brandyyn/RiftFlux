package gravestone.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.block.enums.EnumGraves;
import gravestone.block.enums.EnumMemorials;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GSTabs {
   public static CreativeTabs gravesTab;
   public static CreativeTabs memorialsTab;
   public static CreativeTabs otherItemsTab;

   private GSTabs() {
   }

   public static void registration() {
      gravesTab = new CreativeTabs("tabGraveStone") {
         public ItemStack getIconItemStack() {
            ItemStack stack = new ItemStack(GSBlock.graveStone, 1, 0);
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setByte("GraveType", (byte)EnumGraves.STONE_VERTICAL_PLATE.ordinal());
            stack.setTagCompound(nbt);
            return stack;
         }

         @SideOnly(Side.CLIENT)
         public Item getTabIconItem() {
            return Item.getItemFromBlock(GSBlock.graveStone);
         }
      };
      memorialsTab = new CreativeTabs("tabGraveStone") {
         public ItemStack getIconItemStack() {
            ItemStack stack = new ItemStack(GSBlock.memorial, 1, 0);
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setByte("GraveType", (byte)EnumMemorials.STONE_CREEPER_STATUE.ordinal());
            stack.setTagCompound(nbt);
            return stack;
         }

         @SideOnly(Side.CLIENT)
         public Item getTabIconItem() {
            return Item.getItemFromBlock(GSBlock.memorial);
         }
      };
      otherItemsTab = new CreativeTabs("tabGraveStone") {
         public ItemStack getIconItemStack() {
            return new ItemStack(GSBlock.skullCandle, 1, 1);
         }

         @SideOnly(Side.CLIENT)
         public Item getTabIconItem() {
            return Item.getItemFromBlock(GSBlock.skullCandle);
         }
      };
   }
}
