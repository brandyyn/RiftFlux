package com.voidsrift.riftflux.furniture.client.render;

import com.voidsrift.riftflux.furniture.util.FurnitureRenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class RenderDoorBell implements ISimpleBlockRenderingHandler {
    private final int renderId;

    public RenderDoorBell(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int metadata = world.getBlockMetadata(x, y, z);
        int facing = metadata & 7;
        if (facing == 11) {
            facing = 3;
        } else if (facing == 9) {
            facing = 1;
        } else if (facing == 10) {
            facing = 2;
        } else if (facing == 8) {
            facing = 0;
        }
        double buttonInset = (metadata & 8) != 0 ? 0.89D : 0.85D;

        renderer.setOverrideBlockTexture(Blocks.log.getBlockTextureFromSide(2));
        FurnitureRenderHelper.setRenderBounds(renderer, facing, 0.9D, 0.3D, 0.4D, 1.0D, 0.7D, 0.6D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        renderer.setOverrideBlockTexture(Blocks.iron_block.getBlockTextureFromSide(0));
        FurnitureRenderHelper.setRenderBounds(renderer, facing, buttonInset, 0.45D, 0.45D, 0.9D, 0.55D, 0.55D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        renderer.clearOverrideBlockTexture();
        return true;
    }

    @Override
    public int getRenderId() {
        return this.renderId;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }
}
