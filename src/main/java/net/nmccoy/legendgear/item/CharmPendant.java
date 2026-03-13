/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaubleType
 *  baubles.api.IBauble
 *  cpw.mods.fml.common.Optional$Interface
 *  cpw.mods.fml.common.Optional$Method
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 */
package net.nmccoy.legendgear.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.item.LGItem;

@Optional.Interface(iface="baubles.api.IBauble", modid="Baubles")
public class CharmPendant
extends LGItem
implements IBauble {
    private static String[] names = new String[]{"phoenixCharm", "azureMantle", "phoenixMantle", "blastCharm", "featherCharm"};
    public IIcon phoenixCharmIcon;
    public IIcon azureMantleIcon;
    public IIcon phoenixMantleIcon;
    public IIcon blastCharmIcon;
    public IIcon featherCharmIcon;

    public CharmPendant() {
        this.setUnlocalizedName("itemCharm");
        this.tabs.add(CreativeTabs.tabCombat);
        this.hasSubtypes = true;
        this.setMaxStackSize(1);
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
        this.phoenixCharmIcon = par1IconRegister.registerIcon("legendgear:phoenixCharm");
        this.azureMantleIcon = par1IconRegister.registerIcon("legendgear:azureMantle");
        this.phoenixMantleIcon = par1IconRegister.registerIcon("legendgear:phoenixMantle");
        this.blastCharmIcon = par1IconRegister.registerIcon("legendgear:blastCharm");
        this.featherCharmIcon = par1IconRegister.registerIcon("legendgear:featherCharm");
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return par1ItemStack.getItemDamage() == 2;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        for (int i = 0; i < names.length; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    public IIcon getIconFromDamage(int damage) {
        if (damage == 0) {
            return this.phoenixCharmIcon;
        }
        if (damage == 1) {
            return this.azureMantleIcon;
        }
        if (damage == 2) {
            return this.phoenixMantleIcon;
        }
        if (damage == 3) {
            return this.blastCharmIcon;
        }
        if (damage == 4) {
            return this.featherCharmIcon;
        }
        return this.phoenixCharmIcon;
    }

    public void addRecipes() {
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, 0), (Object[])new Object[]{LegendGear2.phoenixFeather, new ItemStack((Item)LegendGear2.starDust, 1, 4), Items.string});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, 3), (Object[])new Object[]{Items.gunpowder, new ItemStack((Item)LegendGear2.starDust, 1, 4), Items.string});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, 4), (Object[])new Object[]{Items.feather, new ItemStack((Item)LegendGear2.starDust, 1, 4), Items.string});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)this, 1, 1), (Object[])new Object[]{"FGF", "FFF", "FFF", Character.valueOf('F'), LegendGear2.azureFeather, Character.valueOf('G'), Items.gold_ingot});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)this, 1, 2), (Object[])new Object[]{"FGF", "FFF", "FFF", Character.valueOf('F'), LegendGear2.phoenixFeather, Character.valueOf('G'), LegendGear2.sunfireDiamond});
    }

    @Optional.Method(modid="Baubles")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Optional.Method(modid="Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
    }

    @Optional.Method(modid="Baubles")
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
    }

    @Optional.Method(modid="Baubles")
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
    }

    @Optional.Method(modid="Baubles")
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Optional.Method(modid="Baubles")
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}

