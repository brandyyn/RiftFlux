package com.voidsrift.riftflux.vortex.proxy;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import com.voidsrift.riftflux.vortex.client.gui.GuiInventoryGluttonyCharm;
import com.voidsrift.riftflux.vortex.client.gui.GuiInventoryToolbelt;
import com.voidsrift.riftflux.vortex.lib.container.ContainerGluttonyCharm;
import com.voidsrift.riftflux.vortex.lib.container.ContainerToolbelt;
import makamys.satchels.GuiHandler;
import makamys.satchels.gui.GuiSatchelsInventory;
import makamys.satchels.gui.GuiChestGeneric;
import makamys.satchels.gui.GuiEquipment;
import makamys.satchels.inventory.ContainerSatchels;
import makamys.satchels.inventory.ContainerEquipment;
import makamys.satchels.item.ItemPouch;

public class GuiProxy implements IGuiHandler {
   public static final int toolbeltId = 0;
   public static final int backpackId = 1;
   public static final int gluttonyCharmId = 2;

   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      switch(id) {
      case 0:
         return new ContainerToolbelt(player);
      case 1:
         return player.inventoryContainer;
      case 2:
         return new ContainerGluttonyCharm(player);
      case GuiHandler.ID_EQUIPMENT:
         return new ContainerEquipment(player.inventory, !player.worldObj.isRemote, player);
      case GuiHandler.ID_POUCH: {
         net.minecraft.inventory.IInventory pouchInv = ItemPouch.getInventory(player.getHeldItem(), world);
         return pouchInv == null ? null : ItemPouch.constructUpgradesContainer(player.inventory, pouchInv);
      }
      default:
         return null;
      }
   }

   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      switch(id) {
      case 0:
         return new GuiInventoryToolbelt(player);
      case 1:
         return (player.inventoryContainer instanceof ContainerSatchels) ? new GuiSatchelsInventory(player) : new net.minecraft.client.gui.inventory.GuiInventory(player);
      case 2:
         return new GuiInventoryGluttonyCharm(player);
      case GuiHandler.ID_EQUIPMENT:
         return new GuiEquipment(player);
      case GuiHandler.ID_POUCH: {
         net.minecraft.inventory.IInventory pouchInv = ItemPouch.getInventory(player.getHeldItem(), world);
         return pouchInv == null ? null : new GuiChestGeneric(player.inventory, pouchInv);
      }
      default:
         return null;
      }
   }
}
