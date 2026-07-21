package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import java.util.List;
import mariculture.api.core.MaricultureHandlers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GSCompatibilityMariculture {
   protected static boolean isInstalled = false;

   private GSCompatibilityMariculture() {
   }

   public static void addItems(List<ItemStack> items, EntityPlayer player) {
      if (isLoaded() && GraveStoneConfig.storeMaricultureItems && MaricultureHandlers.mirror != null) {
         ItemStack[] mirrorItems = MaricultureHandlers.mirror.getMirrorContents(player);
         if (mirrorItems != null) {
            for(ItemStack item : mirrorItems) {
               if (item != null) {
                  items.add(item.copy());
               }
            }

            MaricultureHandlers.mirror.setMirrorContents(player, new ItemStack[4]);
         }
      }

   }

   public static boolean isLoaded() {
      return isInstalled;
   }
}
