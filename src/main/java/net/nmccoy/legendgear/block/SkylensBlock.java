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

public class SkylensBlock
extends Block {
    public SkylensBlock() {
        super(Material.rock);
        this.setHardness(1.5f);
        this.setHarvestLevel("pickaxe", 1);
        this.setStepSound(soundTypePiston);
        this.setBlockName("skylensBlock");
        this.setBlockTextureName("legendgear:skylensBlock");
        this.setCreativeTab(LegendGear2.legendgearTab);
        this.setLightOpacity(0);
    }
}

