package com.voidsrift.riftflux.vortex.block;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;

public final class ModBlocks {

    public static Block glowCarpet;
    public static int glowCarpetRenderId = -1;
    public static Block voidFlux;

    private ModBlocks() {
    }

    public static void init() {
        glowCarpet = new BlockGlowCarpet()
                .setHardness(0.8F)
                .setStepSound(Block.soundTypeCloth)
                .setBlockName("glow_carpet")
                .setBlockTextureName("riftflux:glow_carpet")
                .setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(glowCarpet, "glow_carpet");
        Blocks.fire.setFireInfo(glowCarpet, 30, 60);

        voidFlux = new BlockVoidFlux()
                .setHardness(50.0F)
                .setResistance(2000.0F)
                .setStepSound(Block.soundTypeStone)
                .setBlockName("void_flux")
                .setBlockTextureName("riftflux:void_flux")
                .setCreativeTab(CreativeTabs.tabBlock);
        voidFlux.setHarvestLevel("pickaxe", 3);
        GameRegistry.registerBlock(voidFlux, "void_flux");
    }
    @SideOnly(Side.CLIENT)
    public static void initClient() {
        if (glowCarpetRenderId >= 0) {
            return;
        }
        glowCarpetRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderGlowCarpet(glowCarpetRenderId));
    }

}