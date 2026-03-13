package com.voidsrift.riftflux.terramine;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemLens extends Item {
    public ItemLens() {
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setTextureName("riftflux:lens");
    }
}
