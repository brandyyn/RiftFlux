package com.voidsrift.riftflux.pumpkinpastures;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemPumpkinAxe extends ItemAxe {
    private static final Set<Block> SHAX_EFFECTIVE_BLOCKS = new HashSet<Block>(Arrays.asList(
            Blocks.clay,
            Blocks.dirt,
            Blocks.farmland,
            Blocks.grass,
            Blocks.gravel,
            Blocks.mycelium,
            Blocks.sand,
            Blocks.snow,
            Blocks.snow_layer,
            Blocks.soul_sand
    ));

    public ItemPumpkinAxe(ToolMaterial material) {
        super(material);
        setUnlocalizedName("pumpkin_axe");
        setTextureName("riftflux:pumpkinpastures/pumpkin_axe");
        setCreativeTab(CreativeTabs.tabTools);
        setMaxDamage(Math.max(0, ModConfig.pumpkinPasturesEnderflameShaxDurability));
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        boolean result = super.hitEntity(stack, target, attacker);
        if (target != null) {
            target.setFire(8);
        }
        return result;
    }

    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        if (block == null) {
            return 1.0F;
        }
        if (SHAX_EFFECTIVE_BLOCKS.contains(block)) {
            return this.efficiencyOnProperMaterial;
        }
        Material material = block.getMaterial();
        if (material == Material.ground
                || material == Material.grass
                || material == Material.sand
                || material == Material.snow
                || material == Material.craftedSnow
                || material == Material.clay) {
            return this.efficiencyOnProperMaterial;
        }
        return super.func_150893_a(stack, block);
    }

    @Override
    public boolean func_150897_b(Block block) {
        if (super.func_150897_b(block)) {
            return true;
        }
        if (block == null) {
            return false;
        }
        if (SHAX_EFFECTIVE_BLOCKS.contains(block)) {
            return true;
        }
        Material material = block.getMaterial();
        return material == Material.ground
                || material == Material.grass
                || material == Material.sand
                || material == Material.snow
                || material == Material.craftedSnow
                || material == Material.clay;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return EnumChatFormatting.GOLD + super.getItemStackDisplayName(stack);
    }
}
