package gravestone.item;

import gravestone.block.enums.EnumHauntedChest;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemBlockGSHauntedChest extends ItemBlock {
   public ItemBlockGSHauntedChest(Block block) {
      super(block);
      this.setHasSubtypes(true);
      this.setUnlocalizedName("Haunted chest");
   }

   public int getMetadata(int damageValue) {
      return 0;
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey("ChestType") ? EnumHauntedChest.getById(itemStack.stackTagCompound.getByte("ChestType")).getName() : EnumHauntedChest.getById((byte)0).getName();
   }

   public void onCreated(ItemStack stack, World world, EntityPlayer player) {
      if (stack.stackTagCompound == null) {
         stack.setTagCompound(new NBTTagCompound());
      }

   }
}
