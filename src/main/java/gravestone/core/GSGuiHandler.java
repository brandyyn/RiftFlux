package gravestone.core;

import cpw.mods.fml.common.network.IGuiHandler;
import gravestone.gui.GSAltarGui;
import gravestone.gui.GSGraveInventoryGui;
import gravestone.gui.container.AltarContainer;
import gravestone.gui.container.GraveContainer;
import gravestone.tileentity.TileEntityGSAltar;
import gravestone.tileentity.TileEntityGSGraveStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GSGuiHandler implements IGuiHandler {
   public static final int GRAVE_INVENTORY_GUI_ID = 0;
   public static final int ALTAR_GUI_ID = 2;

   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      switch(id) {
      case 0: {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         if (tileEntity instanceof TileEntityGSGraveStone) {
            return new GraveContainer(player.inventory, (TileEntityGSGraveStone)tileEntity);
         }
         break;
      }
      case 2: {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         if (tileEntity instanceof TileEntityGSAltar) {
            return new AltarContainer(player.inventory, (TileEntityGSAltar)tileEntity);
         }
      }
      }

      return null;
   }

   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      switch(id) {
      case 0: {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         if (tileEntity instanceof TileEntityGSGraveStone) {
            return new GSGraveInventoryGui(player.inventory, (TileEntityGSGraveStone)tileEntity);
         }
         break;
      }
      case 2: {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         if (tileEntity instanceof TileEntityGSAltar) {
            return new GSAltarGui(player.inventory, (TileEntityGSAltar)tileEntity);
         }
      }
      }

      return null;
   }
}
