/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.IGuiHandler
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 */
package zairus.worldexplorer.core.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zairus.worldexplorer.core.gui.GuiScreenEquipment;
import zairus.worldexplorer.core.gui.GuiStudyDesk;
import zairus.worldexplorer.core.inventory.ContainerEquipment;
import zairus.worldexplorer.core.inventory.ContainerStudyDesk;
import zairus.worldexplorer.core.tileentity.TileEntityDesk;

public class GuiHandler
implements IGuiHandler {
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 0: {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (!(tileEntity instanceof TileEntityDesk)) break;
                return new ContainerStudyDesk(player.inventory, (TileEntityDesk)tileEntity, world);
            }
            case 1: {
                return new ContainerEquipment(player.inventory, world);
            }
        }
        return null;
    }

    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 0: {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (!(tileEntity instanceof TileEntityDesk)) break;
                return new GuiStudyDesk(player.inventory, (TileEntityDesk)tileEntity, world);
            }
            case 1: {
                return new GuiScreenEquipment(player.inventory, world);
            }
        }
        return null;
    }
}

