/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.event.entity.player.EntityItemPickupEvent
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.nmccoy.legendgear.LegendGear2;

public class EmeraldShard
extends Item {
    private IIcon shardIcon;
    private IIcon pieceIcon;

    public EmeraldShard() {
        this.setUnlocalizedName("shardEmerald");
        this.setCreativeTab(LegendGear2.legendgearTab);
        this.setHasSubtypes(true);
    }

    public void addRecipes() {
        ItemStack oneShard = new ItemStack((Item)this, 1, 0);
        ItemStack eightShards = new ItemStack((Item)this, 8, 0);
        ItemStack eightPieces = new ItemStack((Item)this, 8, 1);
        ItemStack onePiece = new ItemStack((Item)this, 1, 1);
        ItemStack oneEmerald = new ItemStack(Items.emerald);
        GameRegistry.addShapelessRecipe((ItemStack)eightShards, (Object[])new Object[]{onePiece});
        GameRegistry.addShapelessRecipe((ItemStack)eightPieces, (Object[])new Object[]{oneEmerald});
        if (LegendGear2.emeraldExchangeRate <= 9) {
            Object[] shards = new Object[LegendGear2.emeraldExchangeRate];
            Object[] pieces = new Object[LegendGear2.emeraldExchangeRate];
            for (int i = 0; i < LegendGear2.emeraldExchangeRate; ++i) {
                shards[i] = oneShard;
                pieces[i] = onePiece;
            }
            GameRegistry.addShapelessRecipe((ItemStack)onePiece, (Object[])shards);
            GameRegistry.addShapelessRecipe((ItemStack)oneEmerald, (Object[])pieces);
        }
    }

    @SubscribeEvent
    public void makeEmeraldCollectionSounds(EntityItemPickupEvent event) {
        ItemStack stack = event.item.getEntityItem();
        Item item = stack.getItem();
        if (item != this && item != Items.emerald) {
            return;
        }
        boolean success = event.entityPlayer.inventory.addItemStackToInventory(stack);
        if (success) {
            if (item == this) {
                if (stack.getItemDamage() == 0 && !event.entityPlayer.worldObj.isRemote) {
                    event.entityPlayer.worldObj.playSoundAtEntity((Entity)event.entityPlayer, "legendgear:moneysmall", LegendGear2.CONFIG_PICKUP_SOUND_VOLUME, 1.0f);
                }
                if (stack.getItemDamage() == 1 && !event.entityPlayer.worldObj.isRemote) {
                    event.entityPlayer.worldObj.playSoundAtEntity((Entity)event.entityPlayer, "legendgear:moneymid", LegendGear2.CONFIG_PICKUP_SOUND_VOLUME, 1.0f);
                }
            }
            if (item == Items.emerald && !event.entityPlayer.worldObj.isRemote) {
                event.entityPlayer.worldObj.playSoundAtEntity((Entity)event.entityPlayer, "legendgear:moneybig", LegendGear2.CONFIG_PICKUP_SOUND_VOLUME, 1.0f);
            }
        }
    }

    public String getUnlocalizedName(ItemStack stack) {
        if (stack.getItemDamage() == 1) {
            return "item.pieceEmerald";
        }
        return "item.shardEmerald";
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister ireg) {
        this.shardIcon = ireg.registerIcon("legendgear:itemEmeraldShard");
        this.pieceIcon = ireg.registerIcon("legendgear:itemEmeraldPiece");
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (damage == 1) {
            return this.pieceIcon;
        }
        return this.shardIcon;
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        ItemStack merged;
        if (par1ItemStack.stackSize < LegendGear2.emeraldExchangeRate) {
            return par1ItemStack;
        }
        par1ItemStack.stackSize -= LegendGear2.emeraldExchangeRate;
        if (par1ItemStack.getItemDamage() == 0) {
            merged = new ItemStack((Item)this, 1, 1);
            if (!par2World.isRemote) {
                par2World.playSoundAtEntity((Entity)par3EntityPlayer, "legendgear:moneymid", 0.5f, 1.0f);
            }
        } else {
            merged = new ItemStack(Items.emerald);
            if (!par2World.isRemote) {
                par2World.playSoundAtEntity((Entity)par3EntityPlayer, "legendgear:moneybig", 0.5f, 1.0f);
            }
        }
        if (!par3EntityPlayer.inventory.addItemStackToInventory(merged)) {
            ForgeHooks.onPlayerTossEvent((EntityPlayer)par3EntityPlayer, (ItemStack)merged, (boolean)false);
        }
        return par1ItemStack;
    }
}

