package com.voidsrift.riftflux.vortex.block;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;

import java.util.Random;

public final class ModBlocks {

    public static final int GLOW_CARPET_VARIANTS = 9;

    public static Block glowCarpet;
    public static Block[] glowCarpets = new Block[GLOW_CARPET_VARIANTS];
    public static int glowCarpetRenderId = -1;
    public static Block voidFlux;

    private ModBlocks() {
    }

    public static void init() {
        for (int i = 0; i < GLOW_CARPET_VARIANTS; ++i) {
            String name = i == 0 ? "glow_carpet" : "glow_carpet" + i;
            BlockGlowCarpet block = (BlockGlowCarpet) new BlockGlowCarpet()
                    .setHardness(0.8F)
                    .setStepSound(Block.soundTypeCloth)
                    .setBlockName(name)
                    .setBlockTextureName("riftflux:" + name);
            if (i == 0) {
                block.setCreativeTab(CreativeTabs.tabDecorations);
                glowCarpet = block;
            }
            glowCarpets[i] = block;
            GameRegistry.registerBlock(block, ItemBlockGlowCarpet.class, name);
            Blocks.fire.setFireInfo(block, 30, 60);
        }

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

    public static Block getRandomGlowCarpetVariant(Random rand) {
        if (rand == null || glowCarpets.length == 0) {
            return glowCarpet;
        }
        Block block = glowCarpets[rand.nextInt(glowCarpets.length)];
        return block == null ? glowCarpet : block;
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