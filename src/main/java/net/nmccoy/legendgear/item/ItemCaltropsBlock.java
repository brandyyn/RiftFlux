/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;

public class ItemCaltropsBlock
extends ItemBlock {
    IIcon icon;

    public ItemCaltropsBlock(Block p_i45328_1_) {
        super(p_i45328_1_);
        this.setTextureName("legendgear:itemCaltrops");
    }

    public IIcon getIcon(ItemStack stack, int pass) {
        return this.icon;
    }

    public IIcon getIconFromDamage(int p_77617_1_) {
        return this.icon;
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister ireg) {
        this.icon = ireg.registerIcon("legendgear:itemCaltrops");
    }

    public int getSpriteNumber() {
        return 1;
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return false;
    }

    public IIcon getIconIndex(ItemStack p_77650_1_) {
        return this.icon;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        --stack.stackSize;
        player.dropPlayerItemWithRandomChoice(new ItemStack(stack.getItem()), false);
        return stack;
    }

    public static boolean trySettlingOn(World world, double x, double y, double z) {
        int pz;
        int py;
        int px = (int)Math.floor(x);
        if (world.getBlock(px, py = (int)Math.floor(y), pz = (int)Math.floor(z)) == LegendGear2.caltropsBlock) {
            return false;
        }
        if (LegendGear2.caltropsBlock.canPlaceBlockAt(world, px, py, pz)) {
            world.setBlock(px, py, pz, (Block)LegendGear2.caltropsBlock);
            world.playSoundEffect(x, y, z, "legendgear:caltropsland", 1.0f, world.rand.nextFloat() * 0.4f + 0.9f);
            return true;
        }
        return false;
    }

    public boolean onEntityItemUpdate(EntityItem ei) {
        if (!ei.worldObj.isRemote && ei.onGround) {
            boolean settled = false;
            if (!settled) {
                settled = ItemCaltropsBlock.trySettlingOn(ei.worldObj, ei.posX, ei.posY, ei.posZ);
            }
            if (!settled) {
                settled = ItemCaltropsBlock.trySettlingOn(ei.worldObj, ei.posX + 0.5, ei.posY, ei.posZ);
            }
            if (!settled) {
                settled = ItemCaltropsBlock.trySettlingOn(ei.worldObj, ei.posX - 0.5, ei.posY, ei.posZ);
            }
            if (!settled) {
                settled = ItemCaltropsBlock.trySettlingOn(ei.worldObj, ei.posX, ei.posY, ei.posZ + 0.5);
            }
            if (!settled) {
                settled = ItemCaltropsBlock.trySettlingOn(ei.worldObj, ei.posX, ei.posY, ei.posZ - 0.5);
            }
            if (settled) {
                ItemStack stack = ei.getEntityItem();
                --stack.stackSize;
                if (stack.stackSize <= 0) {
                    ei.setDead();
                }
            }
        }
        return false;
    }
}

