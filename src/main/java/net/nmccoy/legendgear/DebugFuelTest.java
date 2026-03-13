/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.IFuelHandler
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package net.nmccoy.legendgear;

import cpw.mods.fml.common.IFuelHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.nmccoy.legendgear.LegendGear2;

public class DebugFuelTest
implements IFuelHandler {
    public int getBurnTime(ItemStack fuel) {
        if (fuel.getItem().equals(Item.getItemFromBlock((Block)LegendGear2.caltropsBlock))) {
            return 500;
        }
        return 0;
    }
}

