package com.voidsrift.riftflux.glowstonedust;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.tweaks.ladder.client.RFRenderIds;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;

public final class GlowstoneDustContent {

    public static BlockGlowstoneDust glowstoneDustBlock;

    private GlowstoneDustContent() {
    }

    public static void preInit() {
        if (!ModConfig.enableGlowstoneDust || glowstoneDustBlock != null) {
            return;
        }

        glowstoneDustBlock = (BlockGlowstoneDust) new BlockGlowstoneDust()
                .setHardness(0.0F)
                .setLightLevel(ModConfig.glowstoneDustLightLevel / 15.0F)
                .setStepSound(Block.soundTypeStone)
                .setBlockName("glowstoneDust")
                .setBlockTextureName("riftflux:glowstone_dust");
        GameRegistry.registerBlock(glowstoneDustBlock, "glowstone_dust");
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        if (!ModConfig.enableGlowstoneDust || glowstoneDustBlock == null || RFRenderIds.glowstoneDustRenderId >= 0) {
            return;
        }

        RFRenderIds.glowstoneDustRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderGlowstoneDust());
    }
}
