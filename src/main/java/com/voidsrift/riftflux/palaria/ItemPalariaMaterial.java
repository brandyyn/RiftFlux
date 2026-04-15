package com.voidsrift.riftflux.palaria;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemPalariaMaterial extends Item {
    public ItemPalariaMaterial(String textureName) {
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabMaterials);
        setTextureName("riftflux:palaria/" + textureName);
    }
}
