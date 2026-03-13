/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.IBlockAccess
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.nmccoy.legendgear.client.ClientProxy;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderStarglass
implements ISimpleBlockRenderingHandler {
    private static final ResourceLocation starTexture = new ResourceLocation("legendgear", "textures/chaosrainbow2.png");

    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        Tessellator tess = Tessellator.instance;
        tess.draw();
        GL11.glDisable((int)2896);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)1, (int)1);
        GL11.glDepthMask((boolean)false);
        renderer.enableAO = false;
        tess.startDrawingQuads();
        renderer.renderFaceXNeg(block, (double)x, (double)y, (double)z, block.getIcon(0, 0));
        renderer.renderFaceXPos(block, (double)x, (double)y, (double)z, block.getIcon(0, 0));
        renderer.renderFaceYNeg(block, (double)x, (double)y, (double)z, block.getIcon(0, 0));
        renderer.renderFaceYPos(block, (double)x, (double)y, (double)z, block.getIcon(0, 0));
        renderer.renderFaceZNeg(block, (double)x, (double)y, (double)z, block.getIcon(0, 0));
        renderer.renderFaceZPos(block, (double)x, (double)y, (double)z, block.getIcon(0, 0));
        tess.draw();
        GL11.glEnable((int)2896);
        GL11.glDepthMask((boolean)true);
        tess.startDrawingQuads();
        return true;
    }

    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    public int getRenderId() {
        return ClientProxy.starglassRenderID;
    }
}

