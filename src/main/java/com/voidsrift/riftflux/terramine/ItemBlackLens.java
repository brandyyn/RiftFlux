package com.voidsrift.riftflux.terramine;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBlackLens extends Item {
    public ItemBlackLens() {
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setTextureName("riftflux:black_lens");
    }
}
