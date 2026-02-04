package com.voidsrift.riftflux.vortex.event;

import baubles.client.gui.GuiPlayerExpanded;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import com.voidsrift.riftflux.vortex.client.gui.GuiInventoryBackpack;
import com.voidsrift.riftflux.vortex.client.gui.GuiRuneGameOver;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;
import com.voidsrift.riftflux.vortex.lib.helper.WorldHelper;
import com.voidsrift.riftflux.vortex.network.ModPackets;
import com.voidsrift.riftflux.vortex.network.PacketBackpackGuiHandle;

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

         if (event.gui.getClass() == GuiInventory.class && !player.capabilities.isCreativeMode && ItemHelper.hasArmor(player, ModItems.backpack, 2)) {
            ModPackets.instance.sendToServer(new PacketBackpackGuiHandle(player, true));
            event.setCanceled(true);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onInitGuiPostLowest(Post event) {
      ArrayList<GuiButton> buttonList = (ArrayList)event.buttonList;
      if (event.gui != null && event.gui instanceof GuiInventoryBackpack) {
         for(int i = 0; i < buttonList.size(); ++i) {
            GuiButton button = (GuiButton)buttonList.get(i);
            if (button.id == 55) {
               //button.yPosition -= 29;
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onActionPerformedPostLowest(net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post event) {
      Minecraft mc = Minecraft.getMinecraft();
      EntityPlayer player = mc.thePlayer;
      if (event.gui != null && event.gui instanceof GuiPlayerExpanded && event.button.id == 55 && !player.capabilities.isCreativeMode && ItemHelper.hasArmor(player, ModItems.backpack, 2)) {
         ModPackets.instance.sendToServer(new PacketBackpackGuiHandle(player, true));
      }

   }
}
