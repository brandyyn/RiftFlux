/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.entity.EntityMagicBoomerang;
import net.nmccoy.legendgear.item.LGItem;

public class ItemMagicBoomerang
extends LGItem {
    public static final int MAX_DAMAGE = 512;

    public ItemMagicBoomerang() {
        this.setMaxStackSize(1);
        this.tabs.add(CreativeTabs.tabCombat);
        this.setMaxDamage(512);
        this.setFull3D();
        this.hasSubtypes = false;
        this.setUnlocalizedName("magicBoomerang");
        this.setTextureName("legendgear:starsteelBoomerang");
    }

    public boolean getIsRepairable(ItemStack stack, ItemStack material) {
        return material.getItem() == LegendGear2.starsteelIngot;
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if (!par2World.isRemote) {
            EntityMagicBoomerang emb = new EntityMagicBoomerang(par2World, (EntityLivingBase)par3EntityPlayer, par1ItemStack.copy());
            emb.thrown_from_slot = par3EntityPlayer.inventory.currentItem;
            par2World.spawnEntityInWorld((Entity)emb);
            par2World.playSoundAtEntity((Entity)par3EntityPlayer, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
        }
        par1ItemStack.stackSize = 0;
        return par1ItemStack;
    }
}

