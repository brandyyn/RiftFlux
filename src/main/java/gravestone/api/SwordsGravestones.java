package gravestone.api;

import cpw.mods.fml.common.FMLLog;
import java.lang.reflect.Method;
import net.minecraft.item.Item;

public class SwordsGravestones {
   public static void addSwordGravestone(Item sword) {
      try {
         Class<?> aClass = Class.forName("gravestone.block.GraveStoneHelper");
         Method method = aClass.getDeclaredMethod("addSwordToSwordsList", Item.class);
         method.invoke((Object)null, sword);
      } catch (Exception var3) {
         FMLLog.warning("[GraveStone] Can't add sword to swords gravestones list!");
         var3.printStackTrace();
      }

   }
}
