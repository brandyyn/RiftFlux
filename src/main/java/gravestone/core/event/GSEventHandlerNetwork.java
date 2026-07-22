package gravestone.core.event;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import gravestone.core.MobHandler;
import gravestone.core.compatibility.GSCompatibilityBaubles;

public class GSEventHandlerNetwork {
   @SubscribeEvent
   public void playerLoggedInEvent(PlayerLoggedInEvent event) {
      if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
         MobHandler.setMobSpawnTime(event.player);
      }

   }

   @SubscribeEvent
   public void playerRespawnEvent(PlayerRespawnEvent event) {
      if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
         MobHandler.setMobSpawnTime(event.player);
         GSCompatibilityBaubles.syncAfterRespawn(event.player);
      }

   }
}
