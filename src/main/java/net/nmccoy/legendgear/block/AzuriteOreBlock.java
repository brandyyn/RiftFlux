/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 */
package net.nmccoy.legendgear.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.nmccoy.legendgear.LegendGear2;

public class AzuriteOreBlock
extends Block {
    public AzuriteOreBlock() {
        super(Material.rock);
        this.setBlockTextureName("legendgear:blockAzuriteStone");
        this.setBlockName("azuriteOre");
        this.setHardness(3.0f);
        this.setHarvestLevel("pickaxe", 2);
        this.setCreativeTab(LegendGear2.legendgearTab);
    }

    protected boolean canSilkHarvest() {
        return true;
    }
}

