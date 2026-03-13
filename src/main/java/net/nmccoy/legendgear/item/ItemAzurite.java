/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.nmccoy.legendgear.item.LGItem;

public class ItemAzurite
extends LGItem {
    private static String[] names = new String[]{"azuriteDust", "azuriteDot", "azuriteSphere"};
    public IIcon iconDust;
    public IIcon iconDot;
    public IIcon iconSphere;

    public ItemAzurite() {
        this.setUnlocalizedName("itemAzurite");
        this.tabs.add(CreativeTabs.tabMaterials);
        this.hasSubtypes = true;
    }

    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int damage = par1ItemStack.getItemDamage();
        if (damage >= names.length) {
            damage = 0;
        }
        return "item." + names[damage];
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.iconDust = par1IconRegister.registerIcon("legendgear:azuriteDust");
        this.iconDot = par1IconRegister.registerIcon("legendgear:azuriteDot");
        this.iconSphere = par1IconRegister.registerIcon("legendgear:azuriteSphere");
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        for (int i = 0; i < names.length; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    public IIcon getIconFromDamage(int damage) {
        if (damage == 1) {
            return this.iconDot;
        }
        if (damage == 2) {
            return this.iconSphere;
        }
        return this.iconDust;
    }

    public void addRecipes() {
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)this, 1, 2), (Object[])new Object[]{"ddd", "ddd", "ddd", Character.valueOf('d'), new ItemStack((Item)this, 1, 1)});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, 0), (Object[])new Object[]{new ItemStack((Item)this, 1, 1)});
    }
}

