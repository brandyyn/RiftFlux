package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import gravestone.core.logger.GSLogger;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GSCompatibilityBackpacksMod {
   protected static boolean isInstalled = false;

   private GSCompatibilityBackpacksMod() {
   }

   public static void addItems(List<ItemStack> items, EntityPlayer player) {
      if (isInstalled() && GraveStoneConfig.storeBackpacksItems) {
         try {
            Class<?> PlayerSaveClass = Class.forName("de.eydamos.backpack.saves.PlayerSave");
            if (PlayerSaveClass != null) {
               Constructor constructor = PlayerSaveClass.getConstructor(EntityPlayer.class);
               Object playerSave = constructor.newInstance(player);
               Method getPersonalBackpackMethod = playerSave.getClass().getDeclaredMethod("getPersonalBackpack");
               Method setPersonalBackpackMethod = playerSave.getClass().getDeclaredMethod("setPersonalBackpack", ItemStack.class);
               if (getPersonalBackpackMethod != null && setPersonalBackpackMethod != null) {
                  Object backpackObject = getPersonalBackpackMethod.invoke(playerSave);
                  if (backpackObject != null && backpackObject instanceof ItemStack) {
                     items.add(((ItemStack)backpackObject).copy());
                     setPersonalBackpackMethod.invoke(playerSave, (ItemStack)null);
                  }
               }
            }
         } catch (Exception var8) {
            GSLogger.logError("Can't save Backpacks items!!!");
            var8.printStackTrace();
         }
      }

   }

   public static boolean isInstalled() {
      return isInstalled;
   }
}
