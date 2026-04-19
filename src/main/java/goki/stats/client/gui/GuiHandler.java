/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.IGuiHandler
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.World
 */
package goki.stats.client.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import goki.stats.client.gui.GuiCompatibilityHelper;
import goki.stats.client.gui.GuiStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler
implements IGuiHandler {
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            return new GuiStats(player);
        }
        if (ID == 1) {
            return new GuiCompatibilityHelper(player);
        }
        return null;
    }
}

