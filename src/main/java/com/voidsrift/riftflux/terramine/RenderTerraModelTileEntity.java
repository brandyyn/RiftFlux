package com.voidsrift.riftflux.terramine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTerraModelTileEntity extends TileEntitySpecialRenderer {
    private final ResourceLocation texture;
    private final TerraModelRenderable model;
    private final float[] rotations;

    public RenderTerraModelTileEntity(ResourceLocation texture, TerraModelRenderable model, float rotation0, float rotation1, float rotation2, float rotation3) {
        this.texture = texture;
        this.model = model;
        this.rotations = new float[] {rotation0, rotation1, rotation2, rotation3};
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        int metadata = tileEntity == null ? 0 : tileEntity.getBlockMetadata();
        float rotation = this.rotations[metadata & 3];

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        bindTexture(this.texture);
        this.model.renderAll();
        GL11.glPopMatrix();
    }
}
