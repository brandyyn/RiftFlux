/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraftforge.event.entity.player.ArrowLooseEvent
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.nmccoy.legendgear.LegendGear2;

public class BadBow
extends ItemBow {
    private IIcon[] iconArray;

    public BadBow() {
        this.setCreativeTab(LegendGear2.legendgearTab);
        this.setUnlocalizedName("badBow");
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister p_94581_1_) {
        this.itemIcon = p_94581_1_.registerIcon("legendgear:badbow");
        this.iconArray = new IIcon[3];
        this.iconArray[0] = p_94581_1_.registerIcon("legendgear:badbow_pulling_1");
        this.iconArray[1] = p_94581_1_.registerIcon("legendgear:badbow_pulling_2");
        this.iconArray[2] = p_94581_1_.registerIcon("legendgear:badbow_pulling_2");
    }

    public int getItemEnchantability() {
        return 0;
    }

    public void addRecipes() {
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)this), (Object[])new Object[]{" SW", "S W", " SW", Character.valueOf('S'), Items.string, Character.valueOf('W'), Items.stick});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)this), (Object[])new Object[]{"WS ", "W S", "WS ", Character.valueOf('S'), Items.string, Character.valueOf('W'), Items.stick});
    }

    @SubscribeEvent
    public void fireBowBadly(ArrowLooseEvent ale) {
        if (ale.bow.getItem() == this) {
            ale.charge = 3;
        }
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getItemIconForUseDuration(int p_94599_1_) {
        return this.iconArray[p_94599_1_];
    }
}

