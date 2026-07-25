package com.voidsrift.riftflux.client.photomode;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.RenderBlockFluid;
import org.lwjgl.opengl.GL11;

public final class PhotoModeLiquidBoundaryRenderer {

    private static final double OFFSET = 0.0010000000474974513D;
    private static final List<BoundaryFace> NORTH_FACES = new ArrayList<BoundaryFace>();
    private static final List<BoundaryFace> SOUTH_FACES = new ArrayList<BoundaryFace>();
    private static final List<BoundaryFace> WEST_FACES = new ArrayList<BoundaryFace>();
    private static final List<BoundaryFace> EAST_FACES = new ArrayList<BoundaryFace>();

    private static World cachedWorld;
    private static long cachedGridVersion = Long.MIN_VALUE;
    private static int cachedLoadedChunkCount = -1;

    private PhotoModeLiquidBoundaryRenderer() {
    }

    public static void render(RenderWorldLastEvent event) {
        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft == null ? null : minecraft.theWorld;
        EntityLivingBase camera = minecraft == null ? null : minecraft.renderViewEntity;
        AngelicaPhotoModeRenderGrid.Bounds bounds = AngelicaPhotoModeRenderGrid.get();
        if (!controller.isActive()
                || world == null
                || camera == null
                || bounds == null
                || world.provider == null
                || world.provider.isHellWorld
                || world.provider.dimensionId == -1) {
            clearCache();
            return;
        }

        ensureCache(world, bounds);

        float partialTicks = event.partialTicks;
        double cameraX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * partialTicks;
        double cameraY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * partialTicks;
        double cameraZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * partialTicks;
        GL11.glPushAttrib(
                GL11.GL_ENABLE_BIT
                        | GL11.GL_COLOR_BUFFER_BIT
                        | GL11.GL_DEPTH_BUFFER_BIT
                        | GL11.GL_TEXTURE_BIT
                        | GL11.GL_CURRENT_BIT
                        | GL11.GL_POLYGON_BIT
        );
        GL11.glPushMatrix();
        boolean lightmapEnabled = false;
        try {
            minecraft.entityRenderer.enableLightmap(event.partialTicks);
            lightmapEnabled = true;
            minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslated(-cameraX, -cameraY, -cameraZ);

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            renderFaces(tessellator, NORTH_FACES);
            renderFaces(tessellator, SOUTH_FACES);
            renderFaces(tessellator, WEST_FACES);
            renderFaces(tessellator, EAST_FACES);
            tessellator.draw();
        } finally {
            if (lightmapEnabled) {
                minecraft.entityRenderer.disableLightmap(event.partialTicks);
            }
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static void ensureCache(World world, AngelicaPhotoModeRenderGrid.Bounds bounds) {
        int loadedChunkCount = world.getChunkProvider().getLoadedChunkCount();
        if (cachedWorld == world
                && cachedGridVersion == bounds.version
                && cachedLoadedChunkCount == loadedChunkCount) {
            return;
        }

        clearFaces();
        RenderBlocks renderBlocks = new RenderBlocks(world);
        buildSide(world, renderBlocks, bounds, 2, NORTH_FACES);
        buildSide(world, renderBlocks, bounds, 3, SOUTH_FACES);
        buildSide(world, renderBlocks, bounds, 4, WEST_FACES);
        buildSide(world, renderBlocks, bounds, 5, EAST_FACES);
        cachedWorld = world;
        cachedGridVersion = bounds.version;
        cachedLoadedChunkCount = loadedChunkCount;
    }

    private static void buildSide(
            World world,
            RenderBlocks renderBlocks,
            AngelicaPhotoModeRenderGrid.Bounds bounds,
            int side,
            List<BoundaryFace> faces
    ) {
        int minX = bounds.minChunkX << 4;
        int maxX = ((bounds.maxChunkX + 1) << 4) - 1;
        int minZ = bounds.minChunkZ << 4;
        int maxZ = ((bounds.maxChunkZ + 1) << 4) - 1;
        if (side == 4 || side == 5) {
            int x = side == 4 ? minX : maxX;
            for (int z = minZ; z <= maxZ; z++) {
                buildColumn(world, renderBlocks, faces, x, z, side);
            }
        } else {
            int z = side == 2 ? minZ : maxZ;
            for (int x = minX; x <= maxX; x++) {
                buildColumn(world, renderBlocks, faces, x, z, side);
            }
        }
    }

    private static void buildColumn(
            World world,
            RenderBlocks renderBlocks,
            List<BoundaryFace> faces,
            int x,
            int z,
            int side
    ) {
        if (!world.blockExists(x, 64, z)) {
            return;
        }
        for (int y = 0; y < 256; y++) {
            Block block = RiftFluxFluidloggedLookup.getFluidOrBlock(world, x, y, z);
            if (block == null || block.getMaterial() == null || !block.getMaterial().isLiquid()) {
                continue;
            }
            BoundaryFace face = createFace(world, renderBlocks, block, x, y, z, side);
            if (face != null) {
                faces.add(face);
            }
        }
    }

    private static BoundaryFace createFace(
            World world,
            RenderBlocks renderBlocks,
            Block block,
            int x,
            int y,
            int z,
            int side
    ) {
        IIcon icon = block.getIcon(side, world.getBlockMetadata(x, y, z));
        if (icon == null) {
            return null;
        }

        double[] heights = getHeights(world, renderBlocks, block, x, y, z);
        int color = block.colorMultiplier(world, x, y, z);
        float sideShade = side < 4 ? 0.8F : 0.6F;
        int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
        if (world.provider == null || !world.provider.hasNoSky) {
            brightness = 0x00F00000 | brightness & 0x000000F0;
        }
        return new BoundaryFace(
                x,
                y,
                z,
                side,
                heights,
                icon,
                (color >> 16 & 255) / 255.0F * sideShade,
                (color >> 8 & 255) / 255.0F * sideShade,
                (color & 255) / 255.0F * sideShade,
                brightness
        );
    }

    private static void renderFaces(Tessellator tessellator, List<BoundaryFace> faces) {
        for (BoundaryFace face : faces) {
            face.render(tessellator);
        }
    }

    private static double[] getHeights(World world, RenderBlocks renderBlocks, Block block, int x, int y, int z) {
        if (block instanceof BlockFluidBase) {
            BlockFluidBase fluid = (BlockFluidBase) block;
            float center = RenderBlockFluid.instance.getFluidHeightForRender(world, x, y, z, fluid);
            if (center == 1.0F) {
                return new double[]{1.0D, 1.0D, 1.0D, 1.0D};
            }
        }
        if (block instanceof BlockLiquid) {
            return new double[]{
                    renderBlocks.getLiquidHeight(x, y, z, block.getMaterial()),
                    renderBlocks.getLiquidHeight(x, y, z + 1, block.getMaterial()),
                    renderBlocks.getLiquidHeight(x + 1, y, z + 1, block.getMaterial()),
                    renderBlocks.getLiquidHeight(x + 1, y, z, block.getMaterial())
            };
        }
        return new double[]{1.0D, 1.0D, 1.0D, 1.0D};
    }

    private static void clearCache() {
        if (cachedWorld == null && cachedGridVersion == Long.MIN_VALUE) {
            return;
        }
        clearFaces();
        cachedWorld = null;
        cachedGridVersion = Long.MIN_VALUE;
        cachedLoadedChunkCount = -1;
    }

    private static void clearFaces() {
        NORTH_FACES.clear();
        SOUTH_FACES.clear();
        WEST_FACES.clear();
        EAST_FACES.clear();
    }

    private static final class BoundaryFace {
        private final int x;
        private final int y;
        private final int z;
        private final int side;
        private final double firstHeight;
        private final double secondHeight;
        private final IIcon icon;
        private final float red;
        private final float green;
        private final float blue;
        private final int brightness;

        private BoundaryFace(
                int x,
                int y,
                int z,
                int side,
                double[] heights,
                IIcon icon,
                float red,
                float green,
                float blue,
                int brightness
        ) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.side = side;
            this.icon = icon;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.brightness = brightness;
            if (side == 2) {
                this.firstHeight = heights[0];
                this.secondHeight = heights[3];
            } else if (side == 3) {
                this.firstHeight = heights[2];
                this.secondHeight = heights[1];
            } else if (side == 4) {
                this.firstHeight = heights[1];
                this.secondHeight = heights[0];
            } else {
                this.firstHeight = heights[3];
                this.secondHeight = heights[2];
            }
        }

        private void render(Tessellator tessellator) {
            tessellator.setBrightness(this.brightness);
            tessellator.setColorOpaque_F(this.red, this.green, this.blue);
            double u0 = this.icon.getInterpolatedU(0.0D);
            double u8 = this.icon.getInterpolatedU(8.0D);
            double v8 = this.icon.getInterpolatedV(8.0D);
            if (this.side == 2) {
                this.addQuad(tessellator, this.x, this.z + OFFSET, this.x + 1, u0, u8, v8);
            } else if (this.side == 3) {
                this.addQuad(tessellator, this.x + 1, this.z + 1 - OFFSET, this.x, u0, u8, v8);
            } else if (this.side == 4) {
                this.addQuad(tessellator, this.z + 1, this.x + OFFSET, this.z, u0, u8, v8);
            } else {
                this.addQuad(tessellator, this.z, this.x + 1 - OFFSET, this.z + 1, u0, u8, v8);
            }
        }

        private void addQuad(
                Tessellator tessellator,
                double firstHorizontal,
                double fixed,
                double secondHorizontal,
                double u0,
                double u8,
                double bottomV
        ) {
            double firstTopV = this.icon.getInterpolatedV((1.0D - this.firstHeight) * 8.0D);
            double secondTopV = this.icon.getInterpolatedV((1.0D - this.secondHeight) * 8.0D);
            if (this.side == 2 || this.side == 3) {
                tessellator.addVertexWithUV(firstHorizontal, this.y + this.firstHeight, fixed, u0, firstTopV);
                tessellator.addVertexWithUV(secondHorizontal, this.y + this.secondHeight, fixed, u8, secondTopV);
                tessellator.addVertexWithUV(secondHorizontal, this.y, fixed, u8, bottomV);
                tessellator.addVertexWithUV(firstHorizontal, this.y, fixed, u0, bottomV);
            } else {
                tessellator.addVertexWithUV(fixed, this.y + this.firstHeight, firstHorizontal, u0, firstTopV);
                tessellator.addVertexWithUV(fixed, this.y + this.secondHeight, secondHorizontal, u8, secondTopV);
                tessellator.addVertexWithUV(fixed, this.y, secondHorizontal, u8, bottomV);
                tessellator.addVertexWithUV(fixed, this.y, firstHorizontal, u0, bottomV);
            }
        }
    }
}
