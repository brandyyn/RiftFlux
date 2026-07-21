package gravestone.core.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

public class GSClientTickEventHandler {
   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void playerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.player.getCommandSenderName().equals(Minecraft.getMinecraft().thePlayer.getCommandSenderName())) {
         GSRenderEventHandler.fogDensityPerTick = 0.0F;
      }

   }
}
