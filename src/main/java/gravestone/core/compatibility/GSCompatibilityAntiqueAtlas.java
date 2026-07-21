package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class GSCompatibilityAntiqueAtlas {
   protected static boolean isInstalled = false;

   private GSCompatibilityAntiqueAtlas() {
   }

   public static void placeDeathMarkerAtDeath(EntityPlayer player) {
      if (isInstalled() && GraveStoneConfig.enableAntiqueAtlasDeathMarkers) {
         try {
            Class<?> atlasApiClass = Class.forName("hunternif.mc.atlas.api.AtlasAPI");
            List<Integer> atlasesIdList = (List<Integer>)atlasApiClass.getMethod("getPlayerAtlases", EntityPlayer.class).invoke(null, player);
            Object markerAPI = atlasApiClass.getMethod("getMarkerAPI").invoke(null);
            if (markerAPI != null && atlasesIdList != null) {
               Method putMarker = findPutMarker(markerAPI.getClass());
               for(Integer atlasId : atlasesIdList) {
                  putMarker.invoke(markerAPI, player.worldObj, true, atlasId, "tomb", player.func_110142_aN().func_151521_b().getUnformattedText(), (int)player.posX, (int)player.posZ);
               }
            }
         } catch (ReflectiveOperationException exception) {
            throw new RuntimeException("Failed to add integrated Gravestone Antique Atlas death marker", exception);
         }
      }

   }

   private static Method findPutMarker(Class<?> markerApiClass) throws NoSuchMethodException {
      for (Method method : markerApiClass.getMethods()) {
         if (method.getName().equals("putMarker") && method.getParameterTypes().length == 7) {
            return method;
         }
      }
      throw new NoSuchMethodException(markerApiClass.getName() + ".putMarker");
   }

   public static boolean isInstalled() {
      return isInstalled;
   }
}
