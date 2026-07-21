package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GSCompatibilityisArsMagica {
   protected static boolean isInstalled = false;

   private GSCompatibilityisArsMagica() {
   }

   public static void excludeSoulboundPlayerInventory(List<ItemStack> items, EntityPlayer player) {
      if (!isSoulboundHandlingEnabled()) {
         return;
      }

      int itemIndex = 0;
      for(ItemStack stack : player.inventory.mainInventory) {
         if (hasSoulbound(stack)) {
            items.set(itemIndex, (ItemStack)null);
         }
         ++itemIndex;
      }

      for(ItemStack stack : player.inventory.armorInventory) {
         if (hasSoulbound(stack)) {
            items.set(itemIndex, (ItemStack)null);
         }
         ++itemIndex;
      }
   }

   public static void clearNonSoulboundPlayerInventory(EntityPlayer player) {
      if (!isSoulboundHandlingEnabled()) {
         player.inventory.clearInventory((net.minecraft.item.Item)null, -1);
         return;
      }

      for(int i = 0; i < player.inventory.mainInventory.length; ++i) {
         if (!hasSoulbound(player.inventory.mainInventory[i])) {
            player.inventory.mainInventory[i] = null;
         }
      }

      for(int i = 0; i < player.inventory.armorInventory.length; ++i) {
         if (!hasSoulbound(player.inventory.armorInventory[i])) {
            player.inventory.armorInventory[i] = null;
         }
      }

      player.inventory.markDirty();
   }

   public static void getSoulboundItemsBack(List<ItemStack> items, EntityPlayer player) {
      if (isSoulboundHandlingEnabled()) {
         Iterator<ItemStack> it = items.iterator();

         while(it.hasNext()) {
            ItemStack stack = it.next();
            if (stack != null && hasSoulbound(stack)) {
               player.inventory.addItemStackToInventory(stack.copy());
               it.remove();
            }
         }
      }

   }

   private static boolean hasSoulbound(ItemStack stack) {
      if (stack == null) {
         return false;
      }

      Map enchantments = EnchantmentHelper.getEnchantments(stack);

      for(Object id : enchantments.keySet()) {
         Enchantment ench = Enchantment.enchantmentsList[((Integer)id).shortValue()];
         if (ench != null && ench.getClass().getName().equals("am2.enchantments.EnchantmentSoulbound")) {
            return true;
         }
      }

      return false;
   }

   private static boolean isSoulboundHandlingEnabled() {
      return isInstalled() && GraveStoneConfig.enableArsMagicaSoulbound;
   }

   public static boolean isInstalled() {
      return isInstalled;
   }
}
