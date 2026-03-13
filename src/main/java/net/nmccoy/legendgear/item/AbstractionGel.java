/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.item.LGItem;

public class AbstractionGel
extends LGItem {
    public AbstractionGel() {
        this.setUnlocalizedName("abstractionGel");
        this.tabs.add(CreativeTabs.tabMaterials);
        this.setTextureName("legendgear:abstractionGel");
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    public void addRecipes() {
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 4), (Object[])new Object[]{Items.slime_ball, new ItemStack((Item)LegendGear2.starDust, 1, 3)});
    }
}

