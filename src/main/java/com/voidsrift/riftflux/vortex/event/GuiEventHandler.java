package com.voidsrift.riftflux.vortex.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import com.voidsrift.riftflux.vortex.client.gui.GuiRuneGameOver;
import com.voidsrift.riftflux.vortex.lib.helper.WorldHelper;

public class GuiEventHandler {
   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onGuiOpen(GuiOpenEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      EntityPlayer player = mc.thePlayer;
      if (event.gui != null) {
         if (event.gui instanceof GuiGameOver && mc.theWorld.getWorldInfo().isHardcoreModeEnabled() && WorldHelper.canPlayerHCRevive(player)) {
            event.gui = new GuiRuneGameOver();
         }

      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onInitGuiPostLowest(Post event) {
      // no-op for backpack GUI; satchels inventory now handles backpack slots.
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onActionPerformedPostLowest(net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post event) {
      // no-op: backpack inventory now uses the satchels GUI.
   }
}
