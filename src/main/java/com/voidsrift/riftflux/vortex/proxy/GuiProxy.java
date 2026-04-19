package com.voidsrift.riftflux.vortex.proxy;

import com.voidsrift.riftflux.furniture.FurnitureGuiIds;
import com.voidsrift.riftflux.furniture.client.gui.GuiBedsideCabinet;
import com.voidsrift.riftflux.furniture.client.gui.GuiCabinet;
import com.voidsrift.riftflux.furniture.container.ContainerBedsideCabinet;
import com.voidsrift.riftflux.furniture.container.ContainerCabinet;
import com.voidsrift.riftflux.furniture.tileentity.TileEntityBedsideCabinet;
import com.voidsrift.riftflux.furniture.tileentity.TileEntityCabinet;
import com.voidsrift.riftflux.riftexplorer.RiftExplorerGuiIds;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
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
import zairus.worldexplorer.core.gui.GuiStudyDesk;
import zairus.worldexplorer.core.inventory.ContainerStudyDesk;
import zairus.worldexplorer.core.tileentity.TileEntityDesk;

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
      case FurnitureGuiIds.CABINET: {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         return tileEntity instanceof TileEntityCabinet
                 ? new ContainerCabinet(player.inventory, (TileEntityCabinet) tileEntity)
                 : null;
      }
      case FurnitureGuiIds.BEDSIDE_CABINET: {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         return tileEntity instanceof TileEntityBedsideCabinet
                 ? new ContainerBedsideCabinet(player.inventory, (TileEntityBedsideCabinet) tileEntity)
                 : null;
      }
      case GuiHandler.ID_EQUIPMENT:
         return new ContainerEquipment(player.inventory, !player.worldObj.isRemote, player);
      case GuiHandler.ID_POUCH: {
         net.minecraft.inventory.IInventory pouchInv = ItemPouch.getInventory(player.getHeldItem(), world);
         return pouchInv == null ? null : ItemPouch.constructUpgradesContainer(player.inventory, pouchInv);
      }
      case RiftExplorerGuiIds.STUDY_DESK: {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         return tileEntity instanceof TileEntityDesk
                 ? new ContainerStudyDesk(player.inventory, (TileEntityDesk) tileEntity, world)
                 : null;
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
      case FurnitureGuiIds.CABINET: {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         return tileEntity instanceof TileEntityCabinet
                 ? new GuiCabinet(player.inventory, (TileEntityCabinet) tileEntity)
                 : null;
      }
      case FurnitureGuiIds.BEDSIDE_CABINET: {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         return tileEntity instanceof TileEntityBedsideCabinet
                 ? new GuiBedsideCabinet(player.inventory, (TileEntityBedsideCabinet) tileEntity)
                 : null;
      }
      case GuiHandler.ID_EQUIPMENT:
         return new GuiEquipment(player);
      case GuiHandler.ID_POUCH: {
         net.minecraft.inventory.IInventory pouchInv = ItemPouch.getInventory(player.getHeldItem(), world);
         return pouchInv == null ? null : new GuiChestGeneric(player.inventory, pouchInv);
      }
      case RiftExplorerGuiIds.STUDY_DESK: {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         return tileEntity instanceof TileEntityDesk
                 ? new GuiStudyDesk(player.inventory, (TileEntityDesk) tileEntity, world)
                 : null;
      }
      default:
         return null;
      }
   }
}
