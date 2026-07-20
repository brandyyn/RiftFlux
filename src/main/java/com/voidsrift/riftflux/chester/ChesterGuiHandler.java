package com.voidsrift.riftflux.chester;

import com.voidsrift.riftflux.chester.client.GuiChester;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ChesterGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Entity entity = world.getEntityByID(x);
        if (id != ChesterContent.GUI_ID || !(entity instanceof EntityChester)) {
            return null;
        }
        EntityChester chester = (EntityChester) entity;
        return new ContainerChester(player.inventory, chester.getInventory(), chester);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Entity entity = world.getEntityByID(x);
        if (id != ChesterContent.GUI_ID || !(entity instanceof EntityChester)) {
            return null;
        }
        EntityChester chester = (EntityChester) entity;
        ChesterInventory clientInventory = new ChesterInventory("Chester", chester.getInventorySize());
        return new GuiChester(player.inventory, clientInventory, chester);
    }
}
