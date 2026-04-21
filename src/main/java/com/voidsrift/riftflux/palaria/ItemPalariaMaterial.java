package com.voidsrift.riftflux.palaria;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class ItemPalariaMaterial extends Item {
    public ItemPalariaMaterial(String textureName) {
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabMaterials);
        setTextureName("riftflux:palaria/" + textureName);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        if ("item.creeptile_eye".equals(this.getUnlocalizedName(stack))) {
            list.add(EnumChatFormatting.GRAY + "Nimatin Snack");
        }
    }
}
