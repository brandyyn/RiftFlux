package gravestone.core.recipe;

import cpw.mods.fml.common.registry.GameRegistry;
import gravestone.config.GraveStoneConfig;
import gravestone.core.logger.GSLogger;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class GSChiselRegistry {
   public static final String ORE_NAME = "craftingToolGravestoneChisel";

   private GSChiselRegistry() {
   }

   public static void registerConfiguredChisels() {
      for (String configuredName : GraveStoneConfig.chiselItems) {
         register(configuredName);
      }
   }

   public static boolean isConfiguredChisel(ItemStack stack) {
      if (stack == null) {
         return false;
      }
      for (ItemStack configured : OreDictionary.getOres(ORE_NAME)) {
         if (configured.getItem() == stack.getItem()
                 && (configured.getItemDamage() == OreDictionary.WILDCARD_VALUE
                 || configured.getItemDamage() == stack.getItemDamage())) {
            return true;
         }
      }
      return false;
   }

   public static boolean isBuiltInChiselEnabled() {
      for (String configuredName : GraveStoneConfig.chiselItems) {
         if (configuredName != null && (configuredName.equalsIgnoreCase("riftflux:Chisel")
                 || configuredName.toLowerCase().startsWith("riftflux:chisel:"))) {
            return true;
         }
      }
      return false;
   }

   public static ItemStack getDamagedCraftingChisel(ItemStack stack) {
      ItemStack result = stack.copy();
      result.stackSize = 1;
      if (!result.isItemStackDamageable()) {
         return result;
      }
      int nextDamage = result.getItemDamage() + 1;
      return nextDamage >= result.getMaxDamage() ? null : setDamage(result, nextDamage);
   }

   private static ItemStack setDamage(ItemStack stack, int damage) {
      stack.setItemDamage(damage);
      return stack;
   }

   private static void register(String configuredName) {
      if (configuredName == null || configuredName.trim().isEmpty()) {
         return;
      }
      String[] parts = configuredName.trim().split(":", 3);
      if (parts.length < 2) {
         GSLogger.logError("Invalid ChiselItems entry '" + configuredName + "'. Expected modid:item or modid:item:metadata.");
         return;
      }
      Item item = GameRegistry.findItem(parts[0], parts[1]);
      if (item == null) {
         GSLogger.logError("Could not find configured Gravestone chisel item '" + configuredName + "'.");
         return;
      }
      int metadata = OreDictionary.WILDCARD_VALUE;
      if (parts.length == 3 && !parts[2].isEmpty()) {
         try {
            metadata = Integer.parseInt(parts[2]);
         } catch (NumberFormatException error) {
            GSLogger.logError("Invalid metadata in ChiselItems entry '" + configuredName + "'.");
            return;
         }
      }
      OreDictionary.registerOre(ORE_NAME, new ItemStack(item, 1, metadata));
   }
}
