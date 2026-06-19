package com.voidsrift.riftflux.geostrata.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GeoStrataDecoGenParticle extends EntityFX {
    private static final ResourceLocation PARTICLES_TEXTURE = new ResourceLocation("textures/particle/particles.png");

    private final ResourceLocation texture;

    public GeoStrataDecoGenParticle(World world, double x, double y, double z,
                                    double motionX, double motionY, double motionZ, int meta) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.texture = DecoGenItemRenderer.getItemTexture(meta);
        this.particleGravity = 1.0F;
        this.particleRed = 0.6F;
        this.particleGreen = 0.6F;
        this.particleBlue = 0.6F;
        this.particleScale *= 0.5F;
    }

    @Override
    public int getFXLayer() {
        return 0;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float rotationX, float rotationXZ,
                               float rotationZ, float rotationYZ, float rotationXY) {
        if (this.texture == null) {
            return;
        }

        tessellator.draw();
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
        int oldWrapS = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S);
        int oldWrapT = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        tessellator.startDrawingQuads();
        float scale = 0.1F * this.particleScale;
        float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tessellator.addVertexWithUV(
                renderX - rotationX * scale - rotationYZ * scale,
                renderY - rotationXZ * scale,
                renderZ - rotationZ * scale - rotationXY * scale,
                1.0D,
                1.0D
        );
        tessellator.addVertexWithUV(
                renderX - rotationX * scale + rotationYZ * scale,
                renderY + rotationXZ * scale,
                renderZ - rotationZ * scale + rotationXY * scale,
                1.0D,
                0.0D
        );
        tessellator.addVertexWithUV(
                renderX + rotationX * scale + rotationYZ * scale,
                renderY + rotationXZ * scale,
                renderZ + rotationZ * scale + rotationXY * scale,
                0.0D,
                0.0D
        );
        tessellator.addVertexWithUV(
                renderX + rotationX * scale - rotationYZ * scale,
                renderY - rotationXZ * scale,
                renderZ + rotationZ * scale - rotationXY * scale,
                0.0D,
                1.0D
        );
        tessellator.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, oldWrapS);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, oldWrapT);
        Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLES_TEXTURE);
        tessellator.startDrawingQuads();
    }
}
