/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.Block$SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;

public class CaltropsBlock
extends Block {
    DamageSource caltropsDamage;
    public static float steppedOnDropChance = 0.5f;

    public CaltropsBlock() {
        super(Material.circuits);
        this.setCreativeTab(LegendGear2.legendgearTab);
        this.setBlockBounds(0.1875f, 0.0f, 0.1875f, 0.8125f, 0.1875f, 0.8125f);
        this.setHardness(0.0f);
        this.setBlockName("caltrops");
        this.setBlockTextureName("legendgear:blockCaltrops");
        this.caltropsDamage = new DamageSource("caltrops");
        this.caltropsDamage.setDamageBypassesArmor();
        Block.SoundType caltropsSound = new Block.SoundType("caltrops", 0.5f, 1.2f){

            public String getDigResourcePath() {
                return "legendgear:caltropsland";
            }

            public String getStepSound() {
                return "legendgear:caltropstap";
            }

            public String getPlaceSound() {
                return "legendgear:caltropstap";
            }
        };
        this.setStepSound(caltropsSound);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    public int getRenderType() {
        return 6;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean canBlockStay(World par1World, int par2, int par3, int par4) {
        return World.doesBlockHaveSolidTopSurface((IBlockAccess)par1World, (int)par2, (int)(par3 - 1), (int)par4);
    }

    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        if (!this.canBlockStay(par1World, par2, par3, par4)) {
            return false;
        }
        Block target = par1World.getBlock(par2, par3, par4);
        if (target.getMaterial().isLiquid()) {
            return false;
        }
        return target.isReplaceable((IBlockAccess)par1World, par2, par3, par4);
    }

    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block block) {
        if (!this.canBlockStay(par1World, par2, par3, par4)) {
            this.dropBlockAsItem(par1World, par2, par3, par4, 0, 0);
            par1World.setBlockToAir(par2, par3, par4);
        }
    }

    public void addRecipes() {
        GameRegistry.addRecipe((ItemStack)new ItemStack((Block)this, 4), (Object[])new Object[]{" X ", " X ", "X X", Character.valueOf('X'), Items.iron_ingot});
    }

    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
        if (par5Entity instanceof EntityLivingBase && !par5Entity.isSneaking() && par5Entity.attackEntityFrom(this.caltropsDamage, 1.0f)) {
            ((EntityLivingBase)par5Entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2, true));
            ((EntityLivingBase)par5Entity).addPotionEffect(new PotionEffect(Potion.jump.id, 100, -3, true));
            par1World.setBlockToAir(par2, par3, par4);
            this.dropBlockAsItemWithChance(par1World, par2, par3, par4, 0, steppedOnDropChance, 0);
        }
    }
}

