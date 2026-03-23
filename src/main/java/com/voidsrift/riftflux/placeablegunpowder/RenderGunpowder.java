package com.voidsrift.riftflux.placeablegunpowder;

import com.voidsrift.riftflux.tweaks.ladder.client.RFRenderIds;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderGunpowder implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (modelId != RFRenderIds.placeableGunpowderRenderId) {
            return false;
        }

        Tessellator tessellator = Tessellator.instance;
        int meta = world.getBlockMetadata(x, y, z);
        IIcon cross = BlockGunpowder.getGunpowderIcon("cross");
        IIcon line = BlockGunpowder.getGunpowderIcon("line");
        IIcon crossOverlay = BlockGunpowder.getGunpowderIcon("cross_overlay");
        IIcon lineOverlay = BlockGunpowder.getGunpowderIcon("line_overlay");
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));

        float litAmount = (float) meta / 7.0F;
        float red = litAmount * 0.7F + 0.3F;
        float green = litAmount * litAmount * 0.4F + 0.3F;
        float blue = 0.3F;
        if (green < 0.0F) {
            green = 0.0F;
        }
        tessellator.setColorOpaque_F(red, green, blue);

        boolean west = BlockGunpowder.canConnect(world, x - 1, y, z, 1)
                || !world.getBlock(x - 1, y, z).isBlockNormalCube()
                && BlockGunpowder.canConnect(world, x - 1, y - 1, z, -1);
        boolean east = BlockGunpowder.canConnect(world, x + 1, y, z, 3)
                || !world.getBlock(x + 1, y, z).isBlockNormalCube()
                && BlockGunpowder.canConnect(world, x + 1, y - 1, z, -1);
        boolean north = BlockGunpowder.canConnect(world, x, y, z - 1, 2)
                || !world.getBlock(x, y, z - 1).isBlockNormalCube()
                && BlockGunpowder.canConnect(world, x, y - 1, z - 1, -1);
        boolean south = BlockGunpowder.canConnect(world, x, y, z + 1, 0)
                || !world.getBlock(x, y, z + 1).isBlockNormalCube()
                && BlockGunpowder.canConnect(world, x, y - 1, z + 1, -1);

        if (!world.getBlock(x, y + 1, z).isBlockNormalCube()) {
            if (world.getBlock(x - 1, y, z).isBlockNormalCube() && BlockGunpowder.canConnect(world, x - 1, y + 1, z, -1)) {
                west = true;
            }
            if (world.getBlock(x + 1, y, z).isBlockNormalCube() && BlockGunpowder.canConnect(world, x + 1, y + 1, z, -1)) {
                east = true;
            }
            if (world.getBlock(x, y, z - 1).isBlockNormalCube() && BlockGunpowder.canConnect(world, x, y + 1, z - 1, -1)) {
                north = true;
            }
            if (world.getBlock(x, y, z + 1).isBlockNormalCube() && BlockGunpowder.canConnect(world, x, y + 1, z + 1, -1)) {
                south = true;
            }
        }

        float minX = x;
        float maxX = x + 1;
        float minZ = z;
        float maxZ = z + 1;
        int shape = 0;
        if ((west || east) && !north && !south) {
            shape = 1;
        }
        if ((north || south) && !east && !west) {
            shape = 2;
        }

        if (shape == 0) {
            int uMin = 0;
            int vMin = 0;
            int uMax = 16;
            int vMax = 16;
            if (!west) {
                minX += 0.3125F;
                uMin += 5;
            }
            if (!east) {
                maxX -= 0.3125F;
                uMax -= 5;
            }
            if (!north) {
                minZ += 0.3125F;
                vMin += 5;
            }
            if (!south) {
                maxZ -= 0.3125F;
                vMax -= 5;
            }

            tessellator.addVertexWithUV(maxX, y + 0.015625D, maxZ, cross.getInterpolatedU(uMax), cross.getInterpolatedV(vMax));
            tessellator.addVertexWithUV(maxX, y + 0.015625D, minZ, cross.getInterpolatedU(uMax), cross.getInterpolatedV(vMin));
            tessellator.addVertexWithUV(minX, y + 0.015625D, minZ, cross.getInterpolatedU(uMin), cross.getInterpolatedV(vMin));
            tessellator.addVertexWithUV(minX, y + 0.015625D, maxZ, cross.getInterpolatedU(uMin), cross.getInterpolatedV(vMax));

            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            tessellator.addVertexWithUV(maxX, y + 0.015625D, maxZ, crossOverlay.getInterpolatedU(uMax), crossOverlay.getInterpolatedV(vMax));
            tessellator.addVertexWithUV(maxX, y + 0.015625D, minZ, crossOverlay.getInterpolatedU(uMax), crossOverlay.getInterpolatedV(vMin));
            tessellator.addVertexWithUV(minX, y + 0.015625D, minZ, crossOverlay.getInterpolatedU(uMin), crossOverlay.getInterpolatedV(vMin));
            tessellator.addVertexWithUV(minX, y + 0.015625D, maxZ, crossOverlay.getInterpolatedU(uMin), crossOverlay.getInterpolatedV(vMax));
        } else if (shape == 1) {
            tessellator.addVertexWithUV(maxX, y + 0.015625D, maxZ, line.getMaxU(), line.getMaxV());
            tessellator.addVertexWithUV(maxX, y + 0.015625D, minZ, line.getMaxU(), line.getMinV());
            tessellator.addVertexWithUV(minX, y + 0.015625D, minZ, line.getMinU(), line.getMinV());
            tessellator.addVertexWithUV(minX, y + 0.015625D, maxZ, line.getMinU(), line.getMaxV());

            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            tessellator.addVertexWithUV(maxX, y + 0.015625D, maxZ, lineOverlay.getMaxU(), lineOverlay.getMaxV());
            tessellator.addVertexWithUV(maxX, y + 0.015625D, minZ, lineOverlay.getMaxU(), lineOverlay.getMinV());
            tessellator.addVertexWithUV(minX, y + 0.015625D, minZ, lineOverlay.getMinU(), lineOverlay.getMinV());
            tessellator.addVertexWithUV(minX, y + 0.015625D, maxZ, lineOverlay.getMinU(), lineOverlay.getMaxV());
        } else {
            tessellator.addVertexWithUV(maxX, y + 0.015625D, maxZ, line.getMaxU(), line.getMaxV());
            tessellator.addVertexWithUV(maxX, y + 0.015625D, minZ, line.getMinU(), line.getMaxV());
            tessellator.addVertexWithUV(minX, y + 0.015625D, minZ, line.getMinU(), line.getMinV());
            tessellator.addVertexWithUV(minX, y + 0.015625D, maxZ, line.getMaxU(), line.getMinV());

            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            tessellator.addVertexWithUV(maxX, y + 0.015625D, maxZ, lineOverlay.getMaxU(), lineOverlay.getMaxV());
            tessellator.addVertexWithUV(maxX, y + 0.015625D, minZ, lineOverlay.getMinU(), lineOverlay.getMaxV());
            tessellator.addVertexWithUV(minX, y + 0.015625D, minZ, lineOverlay.getMinU(), lineOverlay.getMinV());
            tessellator.addVertexWithUV(minX, y + 0.015625D, maxZ, lineOverlay.getMaxU(), lineOverlay.getMinV());
        }

        if (!world.getBlock(x, y + 1, z).isBlockNormalCube()) {
            if (world.getBlock(x - 1, y, z).isBlockNormalCube() && world.getBlock(x - 1, y + 1, z) == BlockGunpowder.instance) {
                tessellator.setColorOpaque_F(red, green, blue);
                tessellator.addVertexWithUV(x + 0.015625D, y + 1.021875D, z + 1, line.getMaxU(), line.getMinV());
                tessellator.addVertexWithUV(x + 0.015625D, y, z + 1, line.getMinU(), line.getMinV());
                tessellator.addVertexWithUV(x + 0.015625D, y, z, line.getMinU(), line.getMaxV());
                tessellator.addVertexWithUV(x + 0.015625D, y + 1.021875D, z, line.getMaxU(), line.getMaxV());

                tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                tessellator.addVertexWithUV(x + 0.015625D, y + 1.021875D, z + 1, lineOverlay.getMaxU(), lineOverlay.getMinV());
                tessellator.addVertexWithUV(x + 0.015625D, y, z + 1, lineOverlay.getMinU(), lineOverlay.getMinV());
                tessellator.addVertexWithUV(x + 0.015625D, y, z, lineOverlay.getMinU(), lineOverlay.getMaxV());
                tessellator.addVertexWithUV(x + 0.015625D, y + 1.021875D, z, lineOverlay.getMaxU(), lineOverlay.getMaxV());
            }
            if (world.getBlock(x + 1, y, z).isBlockNormalCube() && world.getBlock(x + 1, y + 1, z) == BlockGunpowder.instance) {
                tessellator.setColorOpaque_F(red, green, blue);
                tessellator.addVertexWithUV(x + 0.984375D, y, z + 1, line.getMinU(), line.getMaxV());
                tessellator.addVertexWithUV(x + 0.984375D, y + 1.021875D, z + 1, line.getMaxU(), line.getMaxV());
                tessellator.addVertexWithUV(x + 0.984375D, y + 1.021875D, z, line.getMaxU(), line.getMinV());
                tessellator.addVertexWithUV(x + 0.984375D, y, z, line.getMinU(), line.getMinV());

                tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                tessellator.addVertexWithUV(x + 0.984375D, y, z + 1, lineOverlay.getMinU(), lineOverlay.getMaxV());
                tessellator.addVertexWithUV(x + 0.984375D, y + 1.021875D, z + 1, lineOverlay.getMaxU(), lineOverlay.getMaxV());
                tessellator.addVertexWithUV(x + 0.984375D, y + 1.021875D, z, lineOverlay.getMaxU(), lineOverlay.getMinV());
                tessellator.addVertexWithUV(x + 0.984375D, y, z, lineOverlay.getMinU(), lineOverlay.getMinV());
            }
            if (world.getBlock(x, y, z - 1).isBlockNormalCube() && world.getBlock(x, y + 1, z - 1) == BlockGunpowder.instance) {
                tessellator.setColorOpaque_F(red, green, blue);
                tessellator.addVertexWithUV(x + 1, y, z + 0.015625D, line.getMinU(), line.getMaxV());
                tessellator.addVertexWithUV(x + 1, y + 1.021875D, z + 0.015625D, line.getMaxU(), line.getMaxV());
                tessellator.addVertexWithUV(x, y + 1.021875D, z + 0.015625D, line.getMaxU(), line.getMinV());
                tessellator.addVertexWithUV(x, y, z + 0.015625D, line.getMinU(), line.getMinV());

                tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                tessellator.addVertexWithUV(x + 1, y, z + 0.015625D, lineOverlay.getMinU(), lineOverlay.getMaxV());
                tessellator.addVertexWithUV(x + 1, y + 1.021875D, z + 0.015625D, lineOverlay.getMaxU(), lineOverlay.getMaxV());
                tessellator.addVertexWithUV(x, y + 1.021875D, z + 0.015625D, lineOverlay.getMaxU(), lineOverlay.getMinV());
                tessellator.addVertexWithUV(x, y, z + 0.015625D, lineOverlay.getMinU(), lineOverlay.getMinV());
            }
            if (world.getBlock(x, y, z + 1).isBlockNormalCube() && world.getBlock(x, y + 1, z + 1) == BlockGunpowder.instance) {
                tessellator.setColorOpaque_F(red, green, blue);
                tessellator.addVertexWithUV(x + 1, y + 1.021875D, z + 0.984375D, line.getMaxU(), line.getMinV());
                tessellator.addVertexWithUV(x + 1, y, z + 0.984375D, line.getMinU(), line.getMinV());
                tessellator.addVertexWithUV(x, y, z + 0.984375D, line.getMinU(), line.getMaxV());
                tessellator.addVertexWithUV(x, y + 1.021875D, z + 0.984375D, line.getMaxU(), line.getMaxV());

                tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                tessellator.addVertexWithUV(x + 1, y + 1.021875D, z + 0.984375D, lineOverlay.getMaxU(), lineOverlay.getMinV());
                tessellator.addVertexWithUV(x + 1, y, z + 0.984375D, lineOverlay.getMinU(), lineOverlay.getMinV());
                tessellator.addVertexWithUV(x, y, z + 0.984375D, lineOverlay.getMinU(), lineOverlay.getMaxV());
                tessellator.addVertexWithUV(x, y + 1.021875D, z + 0.984375D, lineOverlay.getMaxU(), lineOverlay.getMaxV());
            }
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return RFRenderIds.placeableGunpowderRenderId;
    }
}
