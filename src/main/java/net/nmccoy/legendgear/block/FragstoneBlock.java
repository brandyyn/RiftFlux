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

public class FragstoneBlock
extends Block {
    public FragstoneBlock() {
        super(Material.rock);
        this.setBlockTextureName("legendgear:fragstone");
        this.setBlockName("fragstoneBlock");
        this.setHardness(3.0f);
        this.setResistance(15.0f);
        this.setCreativeTab(LegendGear2.legendgearTab);
    }
}

