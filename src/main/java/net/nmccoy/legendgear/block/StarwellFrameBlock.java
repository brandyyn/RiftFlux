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

public class StarwellFrameBlock
extends Block {
    public StarwellFrameBlock() {
        super(Material.rock);
        this.setHarvestLevel("pickaxe", 3);
        this.setHardness(50.0f);
        this.setResistance(2000.0f);
        this.setStepSound(soundTypePiston);
        this.setBlockName("starwellFrame");
        this.setBlockTextureName("legendgear:starwellFrame");
        this.setCreativeTab(LegendGear2.legendgearTab);
    }
}

