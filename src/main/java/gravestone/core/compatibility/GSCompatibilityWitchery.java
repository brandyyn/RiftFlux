package gravestone.core.compatibility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class GSCompatibilityWitchery {
   protected static boolean isInstalled = false;

   private GSCompatibilityWitchery() {
   }

   public static boolean isVampire(EntityPlayer player, DamageSource source) {
      if (isInstalled()) {
         try {
            Class playerClass = Class.forName("com.emoniph.witchery.common.ExtendedPlayer");
            Method getPlayerMethod = playerClass.getDeclaredMethod("get", EntityPlayer.class);
            Object extendedPlayer = getPlayerMethod.invoke((Object)null, player);
            Method isVampireMethod = playerClass.getDeclaredMethod("isVampire");
            if ((Boolean)isVampireMethod.invoke(extendedPlayer)) {
               Class utilClass = Class.forName("com.emoniph.witchery.util.CreatureUtil");
               Method checkForDeathMethod = utilClass.getDeclaredMethod("checkForVampireDeath", EntityLivingBase.class, DamageSource.class);
               return !(Boolean)checkForDeathMethod.invoke((Object)null, player, source);
            }
         } catch (ClassNotFoundException var8) {
            var8.printStackTrace();
         } catch (NoSuchMethodException var9) {
            var9.printStackTrace();
         } catch (InvocationTargetException var10) {
            var10.printStackTrace();
         } catch (IllegalAccessException var11) {
            var11.printStackTrace();
         }
      }

      return false;
   }

   public static boolean isInstalled() {
      return isInstalled;
   }
}
