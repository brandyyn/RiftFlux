package com.voidsrift.riftflux.blessings.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class BlessingPillarBlockRenderer implements ISimpleBlockRenderingHandler {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("riftflux", "textures/blocks/blessing_pillar.png");

    private final int renderId;
    private final ModelBlessingPillar model = new ModelBlessingPillar();

    public BlessingPillarBlockRenderer(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -0.35f, 0.0f);
        GL11.glScalef(0.65f, 0.65f, 0.65f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(0.5f, -1.5f, -0.5f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderId;
    }
}
