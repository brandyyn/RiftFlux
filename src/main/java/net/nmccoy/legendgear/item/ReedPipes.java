/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.nmccoy.legendgear.item.LGItem;

public class ReedPipes
extends LGItem {
    public static int[] notes = new int[]{0, 3, 7, 9, 12};
    public static int[] altnotes = new int[]{-1, 2, 5, 8, 11};

    public ReedPipes() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("reedPipes");
        this.tabs.add(CreativeTabs.tabMisc);
        this.setTextureName("legendgear:itemReedPipes");
    }

    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.none;
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if (!par3EntityPlayer.isUsingItem()) {
            par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
            int note = ReedPipes.getNoteFromSpan((180.0f - (par3EntityPlayer.rotationPitch + 90.0f)) / 180.0f, notes);
            if (par3EntityPlayer.isSneaking()) {
                note = ReedPipes.getNoteFromSpan((180.0f - (par3EntityPlayer.rotationPitch + 90.0f)) / 180.0f, altnotes);
            }
            ++note;
            System.out.println(par3EntityPlayer.rotationPitch);
            if (!par2World.isRemote) {
                par2World.playSoundAtEntity((Entity)par3EntityPlayer, "legendgear:fluteattack", 1.0f, ReedPipes.getNotePitch(note));
            }
            par3EntityPlayer.getEntityData().setInteger("flutenote", note);
        }
        return par1ItemStack;
    }

    public static float getNotePitch(int note) {
        return (float)Math.pow(2.0, (double)((note += 2) - 12) / 12.0);
    }

    public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4) {
        par3EntityPlayer.getEntityData().setFloat("noteTime", 0.0f);
    }

    public static int getNoteFromSpan(double span, int[] notearray) {
        int which = (int)(span * (double)(notearray.length - 1) + 0.5);
        if (which < 0) {
            which = 0;
        }
        if (which >= notearray.length) {
            which = notearray.length - 1;
        }
        return notearray[which];
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        float noteTime = player.getEntityData().getFloat("noteTime");
        int savednote = player.getEntityData().getInteger("flutenote");
        int anglenote = ReedPipes.getNoteFromSpan((180.0f - (player.rotationPitch + 90.0f)) / 180.0f, notes);
        if (player.isSneaking()) {
            anglenote = ReedPipes.getNoteFromSpan((180.0f - (player.rotationPitch + 90.0f)) / 180.0f, altnotes);
        }
        float pitch = ReedPipes.getNotePitch(++anglenote);
        if (anglenote != savednote) {
            player.getEntityData().setInteger("flutenote", anglenote);
            noteTime = 0.0f;
            if (!player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity((Entity)player, "legendgear:fluteattack", 1.0f, pitch);
            }
        } else {
            noteTime += pitch;
        }
        if (noteTime >= 5.0f) {
            noteTime -= 5.0f;
            if (!player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity((Entity)player, "legendgear:flutesustain", 1.0f, pitch);
            }
        }
        player.getEntityData().setFloat("noteTime", noteTime);
    }

    public void addRecipes() {
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)this), (Object[])new Object[]{"XXX", "XX ", "X  ", Character.valueOf('X'), Items.reeds});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)this), (Object[])new Object[]{"XXX", " XX", "  X", Character.valueOf('X'), Items.reeds});
    }

    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }
}

