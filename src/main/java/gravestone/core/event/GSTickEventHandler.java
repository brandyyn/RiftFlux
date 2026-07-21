package gravestone.core.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.core.TimeHelper;
import net.minecraft.client.Minecraft;

public class GSTickEventHandler {
   private static short ticCount = 0;
   private static short fogTicCount = 0;
   public static final short MAX_FOG_TICK_COUNT = 100;

   public static short getFogTicCount() {
      return fogTicCount;
   }

   @SubscribeEvent
   public void worldTick(WorldTickEvent event) {
      if (event.phase == Phase.END) {
         ++ticCount;
         if (ticCount >= 500) {
            TimeHelper.updateIsGraveSpawnTime(event.world);
            ticCount = 0;
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void playerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.player.equals(Minecraft.getMinecraft().thePlayer)) {
         ++fogTicCount;
         if (fogTicCount > 100) {
            fogTicCount = 0;
            GSRenderEventHandler.resetAmountOfFogSources(event.player.worldObj);
         }
      }

   }
}
