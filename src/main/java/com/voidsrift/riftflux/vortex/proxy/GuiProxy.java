package com.voidsrift.riftflux.vortex.proxy;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import com.voidsrift.riftflux.vortex.client.gui.GuiInventoryBackpack;
import com.voidsrift.riftflux.vortex.client.gui.GuiInventoryGluttonyCharm;
import com.voidsrift.riftflux.vortex.client.gui.GuiInventoryToolbelt;
import com.voidsrift.riftflux.vortex.lib.container.ContainerBackpack;
import com.voidsrift.riftflux.vortex.lib.container.ContainerGluttonyCharm;
import com.voidsrift.riftflux.vortex.lib.container.ContainerToolbelt;

public class GuiProxy implements IGuiHandler {
   public static final int toolbeltId = 0;
   public static final int backpackId = 1;
   public static final int gluttonyCharmId = 2;

   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      switch(id) {
      case 0:
         return new ContainerToolbelt(player);
      case 1:
         return new ContainerBackpack(player);
      case 2:
         return new ContainerGluttonyCharm(player);
      default:
         return null;
      }
   }

   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      switch(id) {
      case 0:
         return new GuiInventoryToolbelt(player);
      case 1:
         return new GuiInventoryBackpack(player);
      case 2:
         return new GuiInventoryGluttonyCharm(player);
      default:
         return null;
      }
   }
}
