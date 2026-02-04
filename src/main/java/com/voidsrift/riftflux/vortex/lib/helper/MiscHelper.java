package com.voidsrift.riftflux.vortex.lib.helper;

import java.util.ArrayList;

public class MiscHelper {
   public static boolean strictArrayListContains(ArrayList list, Object obj) {
      for(int i = 0; i < list.size(); ++i) {
         if (list.get(i) == obj) {
            return true;
         }
      }

      return false;
   }

   public static float round(float value, int places) {
      int scale = (int)Math.pow(10.0D, (double)places);
      return (float)Math.round(value * (float)scale) / (float)scale;
   }
}
