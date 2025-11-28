package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.voidsrift.riftflux.ModConfig;

@Mixin(RenderGlobal.class)
public abstract class MixinBlockHighlight {

    /** Uniform polygon offset so the outline sits on top consistently. */
    private static final float POLY_FACTOR = -2.0e-3f;
    private static final float POLY_UNITS  = -2.0e-3f;

    @Inject(method = "drawSelectionBox", at = @At("HEAD"), cancellable = true)
    private void rf$continuousBeams(EntityPlayer player, MovingObjectPosition mop, int pass, float pt, CallbackInfo ci) {
        if (pass != 0 || mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        World w = player.worldObj;
        int bx = mop.blockX, by = mop.blockY, bz = mop.blockZ;
        Block b = w.getBlock(bx, by, bz);
        if (b == null || b.getMaterial() == Material.air) return;

        b.setBlockBoundsBasedOnState(w, bx, by, bz);

        double px = player.prevPosX + (player.posX - player.prevPosX) * pt;
        double py = player.prevPosY + (player.posY - player.prevPosY) * pt;
        double pz = player.prevPosZ + (player.posZ - player.prevPosZ) * pt;

        AxisAlignedBB a = b.getSelectedBoundingBoxFromPool(w, bx, by, bz);
        if (a == null) return;

        // View-space box
        a = a.getOffsetBoundingBox(-px, -py, -pz);

        // Pulse (alpha only)
        float alpha = ModConfig.ALPHA_BASE;
        if (ModConfig.PULSE_ENABLED) {
            double t = (Minecraft.getSystemTime() % 100000L) / 1000.0D;
            double s = 0.5D - 0.5D * Math.cos(t * (Math.PI * 2D) * ModConfig.PULSE_SPEED_HZ);
            alpha = ModConfig.ALPHA_BASE * (0.45F + 0.55F * (float)s);
        }

        // GL state
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);

        // PASS 1: depth + stencil prefill (no color)
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        GL11.glColorMask(false, false, false, false);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(POLY_FACTOR, POLY_UNITS);

        drawEdgeBeamsContinuous(a, ModConfig.THICKNESS);

        // PASS 2: shade only the frontmost fragments we just wrote
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(false);
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilMask(0x00);

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GL11.glColor4f(1F, 1F, 1F, alpha);

        GL11.glPolygonOffset(POLY_FACTOR, POLY_UNITS);
        drawEdgeBeamsContinuous(a, ModConfig.THICKNESS);

        // Restore
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        ci.cancel();
    }

    /** 12 rectangular prisms centered on original edges, EXTENDED through corners → no seams. */
    private static void drawEdgeBeamsContinuous(AxisAlignedBB a, float thickness) {
        final Tessellator t = Tessellator.instance;

        double x1=a.minX, y1=a.minY, z1=a.minZ;
        double x2=a.maxX, y2=a.maxY, z2=a.maxZ;

        // Half-thickness (in/out). Clamp to avoid degenerate beams on tiny AABBs.
        double h = Math.max(1.0e-5D, Math.min(thickness * 0.5D,
                Math.min((x2 - x1) * 0.5D, Math.min((y2 - y1) * 0.5D, (z2 - z1) * 0.5D))));

        // To guarantee no microscopic cracks, extend beams slightly past corners.
        final double EPS = 1.0e-5D;

        // X-oriented beams: full length + tiny overshoot → meet vertical beams seamlessly
        box(t, x1 - h - EPS, y1 - h,      z1 - h,      x2 + h + EPS, y1 + h,      z1 + h);      // bottom north
        box(t, x1 - h - EPS, y1 - h,      z2 - h,      x2 + h + EPS, y1 + h,      z2 + h);      // bottom south
        box(t, x1 - h - EPS, y2 - h,      z1 - h,      x2 + h + EPS, y2 + h,      z1 + h);      // top north
        box(t, x1 - h - EPS, y2 - h,      z2 - h,      x2 + h + EPS, y2 + h,      z2 + h);      // top south

        // Z-oriented beams
        box(t, x1 - h,      y1 - h,      z1 - h - EPS, x1 + h,      y1 + h,      z2 + h + EPS); // bottom west
        box(t, x2 - h,      y1 - h,      z1 - h - EPS, x2 + h,      y1 + h,      z2 + h + EPS); // bottom east
        box(t, x1 - h,      y2 - h,      z1 - h - EPS, x1 + h,      y2 + h,      z2 + h + EPS); // top west
        box(t, x2 - h,      y2 - h,      z1 - h - EPS, x2 + h,      y2 + h,      z2 + h + EPS); // top east

        // Y-oriented beams
        box(t, x1 - h,      y1 - h - EPS, z1 - h,      x1 + h,      y2 + h + EPS, z1 + h);      // NW vertical
        box(t, x2 - h,      y1 - h - EPS, z1 - h,      x2 + h,      y2 + h + EPS, z1 + h);      // NE vertical
        box(t, x2 - h,      y1 - h - EPS, z2 - h,      x2 + h,      y2 + h + EPS, z2 + h);      // SE vertical
        box(t, x1 - h,      y1 - h - EPS, z2 - h,      x1 + h,      y2 + h + EPS, z2 + h);      // SW vertical
    }

    /** Simple six-faced rectangular prism. */
    private static void box(Tessellator t,
                            double minX, double minY, double minZ,
                            double maxX, double maxY, double maxZ) {
        t.startDrawingQuads();
        // -X
        t.addVertex(minX, minY, minZ); t.addVertex(minX, maxY, minZ);
        t.addVertex(minX, maxY, maxZ); t.addVertex(minX, minY, maxZ);
        // +X
        t.addVertex(maxX, minY, minZ); t.addVertex(maxX, minY, maxZ);
        t.addVertex(maxX, maxY, maxZ); t.addVertex(maxX, maxY, minZ);
        // -Y
        t.addVertex(minX, minY, minZ); t.addVertex(minX, minY, maxZ);
        t.addVertex(maxX, minY, maxZ); t.addVertex(maxX, minY, minZ);
        // +Y
        t.addVertex(minX, maxY, minZ); t.addVertex(maxX, maxY, minZ);
        t.addVertex(maxX, maxY, maxZ); t.addVertex(minX, maxY, maxZ);
        // -Z
        t.addVertex(minX, minY, minZ); t.addVertex(maxX, minY, minZ);
        t.addVertex(maxX, maxY, minZ); t.addVertex(minX, maxY, minZ);
        // +Z
        t.addVertex(minX, minY, maxZ); t.addVertex(minX, maxY, maxZ);
        t.addVertex(maxX, maxY, maxZ); t.addVertex(maxX, minY, maxZ);
        t.draw();
    }
}