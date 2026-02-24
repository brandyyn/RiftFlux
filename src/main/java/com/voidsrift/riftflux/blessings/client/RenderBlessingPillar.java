package com.voidsrift.riftflux.blessings.client;

import com.voidsrift.riftflux.blessings.TileEntityBlessingPillar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBlessingPillar extends TileEntitySpecialRenderer {
    private static final ResourceLocation TEXTURE_BASE =
            new ResourceLocation("riftflux", "textures/blocks/blessing_pillar.png");
    private static final ResourceLocation TEXTURE_ACTIVE =
            new ResourceLocation("riftflux", "textures/blocks/blessing_pillar_active.png");

    private final ModelBlessingPillar model = new ModelBlessingPillar();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        if (!(tileEntity instanceof TileEntityBlessingPillar)) {
            return;
        }
        TileEntityBlessingPillar pillar = (TileEntityBlessingPillar) tileEntity;
        Tessellator tessellator = Tessellator.instance;
        float brightness = tileEntity.getWorldObj().getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)
                .getMixedBrightnessForBlock(tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        int skyLight = tileEntity.getWorldObj().getLightBrightnessForSkyBlocks(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 0);
        int lightLow = skyLight % 65536;
        int lightHigh = skyLight / 65536;
        tessellator.setColorOpaque_F(brightness, brightness, brightness);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightLow, lightHigh);

        boolean active = false;
        if (tileEntity.getWorldObj() != null) {
            int meta = tileEntity.getWorldObj().getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
            active = meta >= 2;
        } else {
            active = pillar.isActive();
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(0.5f, -1.5f, -0.5f);
        bindTexture(active ? TEXTURE_ACTIVE : TEXTURE_BASE);
        model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
    }
}
