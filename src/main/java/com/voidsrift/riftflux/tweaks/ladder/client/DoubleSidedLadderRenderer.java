package com.voidsrift.riftflux.tweaks.ladder.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * Renders a ladder face on both sides so ladders look correct when "floating".
 *
 * Adapted from the provided DoubleSidedLadder.java.
 */
public class DoubleSidedLadderRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        // no 3D inventory render
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = renderer.getBlockIconFromSide(block, 0);

        if (renderer.hasOverrideBlockTexture()) {
            iicon = renderer.overrideBlockTexture;
        }

        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double d0 = iicon.getMinU();
        double d1 = iicon.getMinV();
        double d2 = iicon.getMaxU();
        double d3 = iicon.getMaxV();
        int l = world.getBlockMetadata(x, y, z);
        double d4 = 0.0D;
        double d5 = 0.05000000074505806D;

        if (l == 5) {
            tessellator.addVertexWithUV(x + d5, y + 1 + d4, z + 1 + d4, d0, d1);
            tessellator.addVertexWithUV(x + d5, y - d4, z + 1 + d4, d0, d3);
            tessellator.addVertexWithUV(x + d5, y - d4, z - d4, d2, d3);
            tessellator.addVertexWithUV(x + d5, y + 1 + d4, z - d4, d2, d1);

            tessellator.addVertexWithUV(x + d5, y + 1 + d4, z - d4, d2, d1);
            tessellator.addVertexWithUV(x + d5, y - d4, z - d4, d2, d3);
            tessellator.addVertexWithUV(x + d5, y - d4, z + 1 + d4, d0, d3);
            tessellator.addVertexWithUV(x + d5, y + 1 + d4, z + 1 + d4, d0, d1);
        }

        if (l == 4) {
            tessellator.addVertexWithUV(x + 1 - d5, y - d4, z + 1 + d4, d2, d3);
            tessellator.addVertexWithUV(x + 1 - d5, y + 1 + d4, z + 1 + d4, d2, d1);
            tessellator.addVertexWithUV(x + 1 - d5, y + 1 + d4, z - d4, d0, d1);
            tessellator.addVertexWithUV(x + 1 - d5, y - d4, z - d4, d0, d3);

            tessellator.addVertexWithUV(x + 1 - d5, y - d4, z - d4, d0, d3);
            tessellator.addVertexWithUV(x + 1 - d5, y + 1 + d4, z - d4, d0, d1);
            tessellator.addVertexWithUV(x + 1 - d5, y + 1 + d4, z + 1 + d4, d2, d1);
            tessellator.addVertexWithUV(x + 1 - d5, y - d4, z + 1 + d4, d2, d3);
        }

        if (l == 3) {
            tessellator.addVertexWithUV(x + 1 + d4, y - d4, z + d5, d2, d3);
            tessellator.addVertexWithUV(x + 1 + d4, y + 1 + d4, z + d5, d2, d1);
            tessellator.addVertexWithUV(x - d4, y + 1 + d4, z + d5, d0, d1);
            tessellator.addVertexWithUV(x - d4, y - d4, z + d5, d0, d3);

            tessellator.addVertexWithUV(x - d4, y - d4, z + d5, d0, d3);
            tessellator.addVertexWithUV(x - d4, y + 1 + d4, z + d5, d0, d1);
            tessellator.addVertexWithUV(x + 1 + d4, y + 1 + d4, z + d5, d2, d1);
            tessellator.addVertexWithUV(x + 1 + d4, y - d4, z + d5, d2, d3);
        }

        if (l == 2) {
            tessellator.addVertexWithUV(x + 1 + d4, y + 1 + d4, z + 1 - d5, d0, d1);
            tessellator.addVertexWithUV(x + 1 + d4, y - d4, z + 1 - d5, d0, d3);
            tessellator.addVertexWithUV(x - d4, y - d4, z + 1 - d5, d2, d3);
            tessellator.addVertexWithUV(x - d4, y + 1 + d4, z + 1 - d5, d2, d1);

            tessellator.addVertexWithUV(x - d4, y + 1 + d4, z + 1 - d5, d2, d1);
            tessellator.addVertexWithUV(x - d4, y - d4, z + 1 - d5, d2, d3);
            tessellator.addVertexWithUV(x + 1 + d4, y - d4, z + 1 - d5, d0, d3);
            tessellator.addVertexWithUV(x + 1 + d4, y + 1 + d4, z + 1 - d5, d0, d1);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return RFRenderIds.doubleSidedLadderRenderId;
    }
}
