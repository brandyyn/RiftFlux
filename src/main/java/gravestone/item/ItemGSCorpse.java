package gravestone.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.core.GSTabs;
import gravestone.item.corpse.CorpseHelper;
import gravestone.item.enums.EnumCorpse;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemGSCorpse extends Item {
   private static IIcon[] icons;

   public ItemGSCorpse() {
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setUnlocalizedName("Corpse");
      this.setHasSubtypes(true);
   }

   public void onCreated(ItemStack stack, World world, EntityPlayer player) {
      if (stack.stackTagCompound == null) {
         stack.setTagCompound(new NBTTagCompound());
      }

   }

   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
      if (stack.stackTagCompound == null) {
         stack.setTagCompound(new NBTTagCompound());
      } else {
         CorpseHelper.addInfo(stack.getItemDamage(), list, stack.stackTagCompound);
      }

   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs tab, List list) {
      for(int damage = 0; damage < EnumCorpse.values().length; ++damage) {
         list.addAll(CorpseHelper.getDefaultCorpse(item, damage));
      }

   }

   public int getMetadata(int metadata) {
      return metadata;
   }

   public String getItemStackDisplayName(ItemStack itemStack) {
      return EnumCorpse.getById((byte)itemStack.getItemDamage()).getName();
   }
}
