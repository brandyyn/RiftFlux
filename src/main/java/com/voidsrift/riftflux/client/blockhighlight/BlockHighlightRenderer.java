package com.voidsrift.riftflux.client.blockhighlight;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.PostProcessRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

@SideOnly(Side.CLIENT)
public final class BlockHighlightRenderer {
    private static final float POLY_FACTOR = -2.0e-3F;
    private static final float POLY_UNITS = -2.0e-3F;
    private static AxisAlignedBB deferredBox;
    private static float deferredAlpha;
    private static float deferredThickness;

    private BlockHighlightRenderer() {
    }

    public static void beginDeferredRenderFrame() {
        deferredBox = null;
    }

    public static void renderOrDefer(
            EntityPlayer player,
            MovingObjectPosition hit,
            int renderPass,
            float partialTicks
    ) {
        if (renderPass != 0 || player == null || hit == null
                || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return;
        }

        World world = player.worldObj;
        int x = hit.blockX;
        int y = hit.blockY;
        int z = hit.blockZ;
        Block block = world.getBlock(x, y, z);
        if (block == null || block.getMaterial() == Material.air) {
            return;
        }

        block.setBlockBoundsBasedOnState(world, x, y, z);
        AxisAlignedBB selectedBox = block.getSelectedBoundingBoxFromPool(world, x, y, z);
        if (selectedBox == null) {
            return;
        }

        double cameraX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double cameraY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double cameraZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        AxisAlignedBB renderBox = selectedBox.getOffsetBoundingBox(-cameraX, -cameraY, -cameraZ);
        float alpha = getPulseAlpha();

        boolean bloomActive = PostProcessRenderer.shouldDeferBloomExcludedWorldOverlays();
        if (bloomActive && ModConfig.blockHighlightRenderAfterPostProcessing) {
            cacheDeferred(renderBox, ModConfig.THICKNESS, alpha);
            return;
        }

        float bloomScale = bloomActive
                ? clamp01(ModConfig.blockHighlightBloomStrengthPercent / 100.0F)
                : 1.0F;
        render(
                renderBox,
                ModConfig.THICKNESS,
                alpha,
                bloomScale < 0.9999F ? bloomScale : -1.0F
        );
    }

    private static void cacheDeferred(AxisAlignedBB box, float thickness, float alpha) {
        deferredBox = AxisAlignedBB.getBoundingBox(
                box.minX,
                box.minY,
                box.minZ,
                box.maxX,
                box.maxY,
                box.maxZ
        );
        deferredAlpha = alpha;
        deferredThickness = thickness;
    }

    private static float clamp01(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        if (value > 1.0F) {
            return 1.0F;
        }
        return value;
    }

    public static void renderDeferred() {
        if (deferredBox == null) {
            return;
        }

        AxisAlignedBB box = deferredBox;
        float thickness = deferredThickness;
        float alpha = deferredAlpha;
        deferredBox = null;
        render(box, thickness, alpha, -1.0F);
    }

    private static float getPulseAlpha() {
        float alpha = ModConfig.ALPHA_BASE;
        if (ModConfig.PULSE_ENABLED) {
            double timeSeconds = (Minecraft.getSystemTime() % 100000L) / 1000.0D;
            double pulse = 0.5D - 0.5D * Math.cos(
                    timeSeconds * (Math.PI * 2.0D) * ModConfig.PULSE_SPEED_HZ
            );
            alpha = ModConfig.ALPHA_BASE * (0.45F + 0.55F * (float) pulse);
        }
        return alpha;
    }

    private static void render(AxisAlignedBB box, float thickness, float alpha, float bloomScale) {
        int previousActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        boolean shaderSupported = GLContext.getCapabilities().OpenGL20;
        int previousProgram = shaderSupported ? GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) : 0;
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        if (shaderSupported) {
            GL20.glUseProgram(0);
        }
        GL11.glPushAttrib(
                GL11.GL_ENABLE_BIT
                        | GL11.GL_COLOR_BUFFER_BIT
                        | GL11.GL_DEPTH_BUFFER_BIT
                        | GL11.GL_STENCIL_BUFFER_BIT
                        | GL11.GL_POLYGON_BIT
                        | GL11.GL_CURRENT_BIT
                        | GL11.GL_TEXTURE_BIT
        );
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_CULL_FACE);

            // Post-process framebuffer guarantees depth, but does not have stencil.
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_STENCIL_TEST);
            GL11.glColorMask(false, false, false, false);
            GL11.glDepthMask(true);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(POLY_FACTOR, POLY_UNITS);
            drawEdgeBeamsContinuous(box, thickness);

            GL11.glColorMask(true, true, true, true);
            GL11.glDepthMask(false);
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            drawEdgeBeamsContinuous(box, thickness);

            if (bloomScale >= 0.0F) {
                /*
                 * Tag only highlight pixels in scene alpha. World-bloom shader
                 * decodes this marker and scales their bloom source directly;
                 * visible RGB and pulse opacity remain untouched.
                 */
                float markerAlpha = 0.02F + clamp01(bloomScale) * 0.10F;
                GL11.glColorMask(false, false, false, true);
                OpenGlHelper.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, markerAlpha);
                drawEdgeBeamsContinuous(box, thickness);
            }
        } finally {
            GL11.glPopAttrib();
            if (shaderSupported) {
                GL20.glUseProgram(previousProgram);
            }
            OpenGlHelper.setActiveTexture(previousActiveTexture);
        }
    }

    private static void drawEdgeBeamsContinuous(AxisAlignedBB box, float thickness) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        double x1 = box.minX;
        double y1 = box.minY;
        double z1 = box.minZ;
        double x2 = box.maxX;
        double y2 = box.maxY;
        double z2 = box.maxZ;
        double halfThickness = Math.max(
                1.0e-5D,
                Math.min(
                        thickness * 0.5D,
                        Math.min(
                                (x2 - x1) * 0.5D,
                                Math.min((y2 - y1) * 0.5D, (z2 - z1) * 0.5D)
                        )
                )
        );
        double epsilon = 1.0e-5D;

        addBox(tessellator, x1 - halfThickness - epsilon, y1 - halfThickness, z1 - halfThickness,
                x2 + halfThickness + epsilon, y1 + halfThickness, z1 + halfThickness);
        addBox(tessellator, x1 - halfThickness - epsilon, y1 - halfThickness, z2 - halfThickness,
                x2 + halfThickness + epsilon, y1 + halfThickness, z2 + halfThickness);
        addBox(tessellator, x1 - halfThickness - epsilon, y2 - halfThickness, z1 - halfThickness,
                x2 + halfThickness + epsilon, y2 + halfThickness, z1 + halfThickness);
        addBox(tessellator, x1 - halfThickness - epsilon, y2 - halfThickness, z2 - halfThickness,
                x2 + halfThickness + epsilon, y2 + halfThickness, z2 + halfThickness);

        addBox(tessellator, x1 - halfThickness, y1 - halfThickness, z1 - halfThickness - epsilon,
                x1 + halfThickness, y1 + halfThickness, z2 + halfThickness + epsilon);
        addBox(tessellator, x2 - halfThickness, y1 - halfThickness, z1 - halfThickness - epsilon,
                x2 + halfThickness, y1 + halfThickness, z2 + halfThickness + epsilon);
        addBox(tessellator, x1 - halfThickness, y2 - halfThickness, z1 - halfThickness - epsilon,
                x1 + halfThickness, y2 + halfThickness, z2 + halfThickness + epsilon);
        addBox(tessellator, x2 - halfThickness, y2 - halfThickness, z1 - halfThickness - epsilon,
                x2 + halfThickness, y2 + halfThickness, z2 + halfThickness + epsilon);

        addBox(tessellator, x1 - halfThickness, y1 - halfThickness - epsilon, z1 - halfThickness,
                x1 + halfThickness, y2 + halfThickness + epsilon, z1 + halfThickness);
        addBox(tessellator, x2 - halfThickness, y1 - halfThickness - epsilon, z1 - halfThickness,
                x2 + halfThickness, y2 + halfThickness + epsilon, z1 + halfThickness);
        addBox(tessellator, x2 - halfThickness, y1 - halfThickness - epsilon, z2 - halfThickness,
                x2 + halfThickness, y2 + halfThickness + epsilon, z2 + halfThickness);
        addBox(tessellator, x1 - halfThickness, y1 - halfThickness - epsilon, z2 - halfThickness,
                x1 + halfThickness, y2 + halfThickness + epsilon, z2 + halfThickness);
        tessellator.draw();
    }

    private static void addBox(
            Tessellator tessellator,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(maxX, minY, maxZ);
    }
}
