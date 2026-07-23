package com.voidsrift.riftflux.vortex.proxy;

import com.voidsrift.riftflux.chester.ChesterContent;
import com.voidsrift.riftflux.chester.ChesterGuiHandler;
import com.voidsrift.riftflux.furniture.FurnitureGuiIds;
import com.voidsrift.riftflux.furniture.client.gui.GuiBedsideCabinet;
import com.voidsrift.riftflux.furniture.client.gui.GuiCabinet;
import com.voidsrift.riftflux.furniture.container.ContainerBedsideCabinet;
import com.voidsrift.riftflux.furniture.container.ContainerCabinet;
import com.voidsrift.riftflux.furniture.tileentity.TileEntityBedsideCabinet;
import com.voidsrift.riftflux.furniture.tileentity.TileEntityCabinet;
import com.voidsrift.riftflux.riftexplorer.RiftExplorerGuiIds;
import cpw.mods.fml.common.network.IGuiHandler;
import gravestone.core.GSGuiHandler;
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
import java.util.HashMap;
import java.util.Map;
import zairus.worldexplorer.core.gui.GuiStudyDesk;
import zairus.worldexplorer.core.inventory.ContainerStudyDesk;
import zairus.worldexplorer.core.tileentity.TileEntityDesk;

public class GuiProxy implements IGuiHandler {
   public static final int toolbeltId = 0;
   public static final int backpackId = 1;
   public static final int gluttonyCharmId = 2;
   private static final ChesterGuiHandler CHESTER_GUI_HANDLER = new ChesterGuiHandler();
   private static final GSGuiHandler GRAVESTONE_GUI_HANDLER = new GSGuiHandler();

   static {
      Map<Integer, String> claimedIds = new HashMap<Integer, String>();
      claimGuiId(claimedIds, toolbeltId, "Vortex toolbelt");
      claimGuiId(claimedIds, backpackId, "Vortex backpack");
      claimGuiId(claimedIds, gluttonyCharmId, "Vortex gluttony charm");
      claimGuiId(claimedIds, GuiHandler.ID_EQUIPMENT, "Satchels equipment");
      claimGuiId(claimedIds, GuiHandler.ID_POUCH, "Satchels pouch");
      claimGuiId(claimedIds, FurnitureGuiIds.CABINET, "Furniture cabinet");
      claimGuiId(claimedIds, FurnitureGuiIds.BEDSIDE_CABINET, "Furniture bedside cabinet");
      claimGuiId(claimedIds, RiftExplorerGuiIds.STUDY_DESK, "Rift Explorer study desk");
      claimGuiId(claimedIds, ChesterContent.GUI_ID, "Chester");
      claimGuiId(claimedIds, GSGuiHandler.GRAVE_INVENTORY_GUI_ID, "Gravestone inventory");
      claimGuiId(claimedIds, GSGuiHandler.ALTAR_GUI_ID, "Gravestone altar");
   }

   private static void claimGuiId(Map<Integer, String> claimedIds, int id, String owner) {
      String existing = claimedIds.put(id, owner);
      if (existing != null) {
         throw new IllegalStateException("RiftFlux GUI ID collision at " + id + " between " + existing + " and " + owner);
      }
   }

   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      Object gravestoneGui = GRAVESTONE_GUI_HANDLER.getServerGuiElement(id, player, world, x, y, z);
      if (gravestoneGui != null) {
         return gravestoneGui;
      }
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
      case ChesterContent.GUI_ID:
         return CHESTER_GUI_HANDLER.getServerGuiElement(id, player, world, x, y, z);
      default:
         return null;
      }
   }

   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      Object gravestoneGui = GRAVESTONE_GUI_HANDLER.getClientGuiElement(id, player, world, x, y, z);
      if (gravestoneGui != null) {
         return gravestoneGui;
      }
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
      case ChesterContent.GUI_ID:
         return CHESTER_GUI_HANDLER.getClientGuiElement(id, player, world, x, y, z);
      default:
         return null;
      }
   }
}
