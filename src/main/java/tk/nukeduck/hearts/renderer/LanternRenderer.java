/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  org.lwjgl.opengl.GL11
 */
package tk.nukeduck.hearts.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tk.nukeduck.hearts.HeartCrystal;
import tk.nukeduck.hearts.renderer.ModelLantern;

public class LanternRenderer
extends TileEntitySpecialRenderer {
    private final ModelLantern model = new ModelLantern();
    private final HeartCrystalRenderer crystalRenderer = new HeartCrystalRenderer();

    protected ResourceLocation getLanternTexture() {
        return new ResourceLocation("hearts", "textures/models/lantern.png");
    }

    protected void renderLanternCore(TileEntity tileEntity, float partialTicks) {
        if (HeartCrystal.config.getOldModel()) {
            this.crystalRenderer.renderOldCrystalInLantern(tileEntity, partialTicks);
        } else {
            this.crystalRenderer.renderModernCrystalInLantern(tileEntity, partialTicks);
        }
    }

    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float p_147500_8_) {
        int metadata = tileEntity.getBlockMetadata();
        GL11.glPushMatrix();
        GL11.glTranslated((double)(x + 0.5), (double)(y + 1.5), (double)(z + 0.5));
        if (HeartCrystalRenderer.isTooltipPreviewRender()) {
            GL11.glTranslatef((float)0.0f, (float)HeartCrystalRenderer.TOOLTIP_PREVIEW_LANTERN_Y_OFFSET, (float)0.0f);
            GL11.glScalef((float)HeartCrystalRenderer.TOOLTIP_PREVIEW_LANTERN_SCALE, (float)HeartCrystalRenderer.TOOLTIP_PREVIEW_LANTERN_SCALE, (float)HeartCrystalRenderer.TOOLTIP_PREVIEW_LANTERN_SCALE);
        }
        this.renderLanternCore(tileEntity, p_147500_8_);
        GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glRotatef((float)(90 * (metadata % 4)), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.getLanternTexture());
        this.model.render(metadata, 0.0625f);
        GL11.glPopMatrix();
    }
}
