/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package net.nmccoy.legendgear.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.nmccoy.legendgear.LegendGear2;

public class LGItem
extends Item {
    protected List<CreativeTabs> tabs = new ArrayList<CreativeTabs>();
    private boolean isShiny;

    public LGItem() {
        this.tabs.add(LegendGear2.legendgearTab);
    }

    public LGItem setCreativeTab(CreativeTabs tab) {
        this.tabs.add(tab);
        return this;
    }

    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return this.isShiny && par1ItemStack.getItemDamage() != 9999;
    }

    public LGItem setShiny() {
        this.isShiny = true;
        return this;
    }

    public CreativeTabs[] getCreativeTabs() {
        return this.tabs.toArray(new CreativeTabs[this.tabs.size()]);
    }
}

