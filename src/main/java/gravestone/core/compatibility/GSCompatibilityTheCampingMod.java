package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class GSCompatibilityTheCampingMod {
   protected static boolean isInstalled = false;

   private GSCompatibilityTheCampingMod() {
   }

   public static void addItems(List<ItemStack> items, EntityPlayer player) {
      if (isInstalled() && GraveStoneConfig.storeTheCampingModItems) {
         items.addAll(getItems(player));
      }

   }

   private static List<ItemStack> getItems(EntityPlayer player) {
      List<ItemStack> items = new LinkedList<>();
      NBTTagCompound tag = player.getEntityData().getCompoundTag("campInv");
      NBTTagList inventory = tag.getTagList("Items", 10);

      for(int i = 0; i < inventory.tagCount(); ++i) {
         NBTTagCompound Slots = inventory.getCompoundTagAt(i);
         Slots.getByte("Slot");
         items.add(ItemStack.loadItemStackFromNBT(Slots).copy());
      }

      player.getEntityData().setTag("campInv", new NBTTagCompound());
      return items;
   }

   public static boolean isInstalled() {
      return isInstalled;
   }
}
