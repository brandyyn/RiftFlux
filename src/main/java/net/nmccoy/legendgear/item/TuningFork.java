/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.block.TileEntityRitual;
import net.nmccoy.legendgear.item.LGItem;

public class TuningFork
extends LGItem {
    public TuningFork() {
        this.setUnlocalizedName("tuningFork");
        this.tabs.add(CreativeTabs.tabTools);
        this.setTextureName("legendgear:tuningfork");
        this.setMaxStackSize(1);
    }

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World world, int x, int y, int z, int side, float fx, float fy, float fz) {
        if (world.getBlock(x, y, z) == LegendGear2.ritualBlock) {
            TileEntityRitual ter = (TileEntityRitual)world.getTileEntity(x, y, z);
            if (ter != null) {
                ter.tuning();
                return true;
            }
        } else {
            world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "legendgear:fork", 0.5f, 1.0f);
            return true;
        }
        return false;
    }

    public void addRecipes() {
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)this), (Object[])new Object[]{"X X", "XXX ", " X ", Character.valueOf('X'), LegendGear2.starsteelIngot});
    }
}

