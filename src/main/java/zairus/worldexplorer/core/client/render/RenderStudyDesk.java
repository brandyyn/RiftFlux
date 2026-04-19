/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.IBlockAccess
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.core.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.core.block.BlockStudyDesk;
import zairus.worldexplorer.core.block.WorldExplorerBlocks;
import zairus.worldexplorer.core.model.ModelStudyDesk;

public class RenderStudyDesk
extends TileEntitySpecialRenderer
implements ISimpleBlockRenderingHandler {
    ResourceLocation texture = new ResourceLocation("worldexplorer", "textures/model/StudyDesk.png");
    private ModelStudyDesk model = new ModelStudyDesk();

    public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
        this.renderDesk(x, y, z, 0.5f, 1.5f, 0.5f, entity);
    }

    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        this.renderDesk(0.0, 0.0, 0.0, 0.0f, 1.0f, 0.0f, null);
    }

    private void renderDesk(double x, double y, double z, float xIncrement, float yIncrement, float zIncrement, TileEntity entity) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)x + xIncrement), (float)((float)y + yIncrement), (float)((float)z + zIncrement));
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        if (entity != null) {
            int renderFacing = entity.getWorldObj().getBlockMetadata(entity.xCoord, entity.yCoord, entity.zCoord);
            switch (renderFacing) {
                case 3: {
                    GL11.glRotatef((float)180.0f, (float)1.0f, (float)800.0f, (float)1.0f);
                    break;
                }
                case 4: {
                    GL11.glRotatef((float)270.0f, (float)1.0f, (float)800.0f, (float)1.0f);
                    break;
                }
                case 5: {
                    GL11.glRotatef((float)90.0f, (float)0.0f, (float)10.0f, (float)0.0f);
                }
            }
        } else {
            GL11.glRotatef((float)180.0f, (float)1.0f, (float)800.0f, (float)1.0f);
        }
        this.bindTexture(this.texture);
        GL11.glPushMatrix();
        this.model.renderModel(0.0625f);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return false;
    }

    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    public int getRenderId() {
        return ((BlockStudyDesk)WorldExplorerBlocks.studydesk).getRenderType();
    }
}

