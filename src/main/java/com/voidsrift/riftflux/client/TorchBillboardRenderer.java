package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.TorchRenderRules;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class TorchBillboardRenderer {
    private static final int HORIZONTAL_SCAN_RADIUS = 48;
    private static final int VERTICAL_SCAN_RADIUS = 24;
    private static final int RESCAN_INTERVAL_TICKS = 10;
    private static final int RESCAN_BLOCK_DELTA = 8;
    private static final TorchBillboardRenderer INSTANCE = new TorchBillboardRenderer();

    private static boolean bootstrapped;

    private final List<TorchEntry> torches = new ArrayList<TorchEntry>();
    private World cachedWorld;
    private int lastScanTick = Integer.MIN_VALUE;
    private int lastScanX = Integer.MIN_VALUE;
    private int lastScanY = Integer.MIN_VALUE;
    private int lastScanZ = Integer.MIN_VALUE;

    private TorchBillboardRenderer() {
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event != null && event.world != null && event.world == this.cachedWorld) {
            this.clearCache();
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        this.clearCache();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!ModConfig.billboardTorchRendering) {
            this.clearCache();
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        WorldClient world = minecraft == null ? null : minecraft.theWorld;
        Entity camera = minecraft == null ? null : minecraft.renderViewEntity;
        if (camera == null && minecraft != null) {
            camera = minecraft.thePlayer;
        }
        if (world == null || camera == null) {
            this.clearCache();
            return;
        }

        this.refreshCacheIfNeeded(world, camera);
        if (this.torches.isEmpty()) {
            return;
        }

        double cameraX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * (double)event.partialTicks;
        double cameraY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * (double)event.partialTicks;
        double cameraZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * (double)event.partialTicks;
        double cameraEyeY = cameraY + (double)camera.getEyeHeight();
        this.renderTorches(minecraft, world, cameraX, cameraY, cameraZ, cameraEyeY);
    }

    private void refreshCacheIfNeeded(WorldClient world, Entity camera) {
        int cameraX = MathHelper.floor_double(camera.posX);
        int cameraY = MathHelper.floor_double(camera.posY);
        int cameraZ = MathHelper.floor_double(camera.posZ);
        int tick = camera.ticksExisted;
        boolean worldChanged = this.cachedWorld != world;
        boolean tickExpired = tick - this.lastScanTick >= RESCAN_INTERVAL_TICKS;
        boolean movedFar = Math.abs(cameraX - this.lastScanX) >= RESCAN_BLOCK_DELTA
                || Math.abs(cameraY - this.lastScanY) >= RESCAN_BLOCK_DELTA
                || Math.abs(cameraZ - this.lastScanZ) >= RESCAN_BLOCK_DELTA;

        if (!worldChanged && !tickExpired && !movedFar) {
            return;
        }

        this.cachedWorld = world;
        this.lastScanTick = tick;
        this.lastScanX = cameraX;
        this.lastScanY = cameraY;
        this.lastScanZ = cameraZ;
        this.scanTorches(world, cameraX, cameraY, cameraZ);
    }

    private void scanTorches(World world, int centerX, int centerY, int centerZ) {
        this.torches.clear();

        int minY = Math.max(0, centerY - VERTICAL_SCAN_RADIUS);
        int maxY = Math.min(255, centerY + VERTICAL_SCAN_RADIUS);
        if (minY > maxY) {
            return;
        }
        int chunkCheckY = Math.max(minY, Math.min(maxY, centerY));

        for (int x = centerX - HORIZONTAL_SCAN_RADIUS; x <= centerX + HORIZONTAL_SCAN_RADIUS; x++) {
            for (int z = centerZ - HORIZONTAL_SCAN_RADIUS; z <= centerZ + HORIZONTAL_SCAN_RADIUS; z++) {
                if (!world.blockExists(x, chunkCheckY, z)) {
                    continue;
                }
                for (int y = minY; y <= maxY; y++) {
                    Block block = world.getBlock(x, y, z);
                    if (!this.shouldRenderBillboard(block, world.getBlockMetadata(x, y, z))) {
                        continue;
                    }
                    this.torches.add(new TorchEntry(x, y, z));
                }
            }
        }
    }

    private boolean shouldRenderBillboard(Block block, int meta) {
        if (!TorchRenderRules.isTorchRenderType(block)) {
            return false;
        }
        boolean useModernTorchModel = ModConfig.modernTorchRendering
                && TorchRenderRules.isModernTorchRenderingEligible(block, meta);
        return TorchRenderRules.shouldUseBillboardTorchRendering(block, meta, useModernTorchModel);
    }

    private void renderTorches(Minecraft minecraft, World world, double cameraX, double cameraY, double cameraZ, double cameraEyeY) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_CURRENT_BIT);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(-cameraX, -cameraY, -cameraZ);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);

            minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            for (int i = 0; i < this.torches.size(); i++) {
                this.renderTorch(tessellator, world, this.torches.get(i), cameraX, cameraEyeY, cameraZ);
            }
            tessellator.draw();
        } finally {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private void renderTorch(Tessellator tessellator, World world, TorchEntry entry, double cameraX, double cameraY, double cameraZ) {
        Block block = world.getBlock(entry.x, entry.y, entry.z);
        int meta = world.getBlockMetadata(entry.x, entry.y, entry.z);
        if (!this.shouldRenderBillboard(block, meta)) {
            return;
        }

        IIcon icon = block.getIcon(0, meta);
        if (icon == null) {
            return;
        }

        double xTilt = this.getXTilt(meta);
        double zTilt = this.getZTilt(meta);
        boolean wallMounted = this.isWallMounted(meta);
        double centerX = entry.x + 0.5D;
        double centerZ = entry.z + 0.5D;
        double originX = wallMounted ? centerX + xTilt * 0.25D : centerX;
        double originZ = wallMounted ? centerZ + zTilt * 0.25D : centerZ;
        double bottomX = originX + xTilt;
        double bottomY = entry.y + (wallMounted ? 0.2D : 0.0D);
        double bottomZ = originZ + zTilt;
        double topX = originX;
        double topY = entry.y + (wallMounted ? 1.2D : 1.0D);
        double topZ = originZ;
        double billboardCenterX = (bottomX + topX) * 0.5D;
        double billboardCenterY = (bottomY + topY) * 0.5D;
        double billboardCenterZ = (bottomZ + topZ) * 0.5D;
        double halfWidth = 0.5D;
        double axisX = topX - bottomX;
        double axisY = topY - bottomY;
        double axisZ = topZ - bottomZ;
        double toCameraX = cameraX - billboardCenterX;
        double toCameraY = cameraY - billboardCenterY;
        double toCameraZ = cameraZ - billboardCenterZ;
        double sideX = axisY * toCameraZ - axisZ * toCameraY;
        double sideY = axisZ * toCameraX - axisX * toCameraZ;
        double sideZ = axisX * toCameraY - axisY * toCameraX;
        double length = Math.sqrt(sideX * sideX + sideY * sideY + sideZ * sideZ);
        if (length > 1.0E-6D) {
            sideX = sideX / length * halfWidth;
            sideY = sideY / length * halfWidth;
            sideZ = sideZ / length * halfWidth;
        } else {
            double horizontalLength = Math.sqrt(toCameraX * toCameraX + toCameraZ * toCameraZ);
            if (horizontalLength > 1.0E-6D) {
                sideX = -toCameraZ / horizontalLength * halfWidth;
                sideY = 0.0D;
                sideZ = toCameraX / horizontalLength * halfWidth;
            } else {
                sideX = halfWidth;
                sideY = 0.0D;
                sideZ = 0.0D;
            }
        }

        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, entry.x, entry.y, entry.z));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        this.addDoubleSidedQuad(
                tessellator,
                topX - sideX, topY - sideY, topZ - sideZ, icon.getMinU(), icon.getMinV(),
                bottomX - sideX, bottomY - sideY, bottomZ - sideZ, icon.getMinU(), icon.getMaxV(),
                bottomX + sideX, bottomY + sideY, bottomZ + sideZ, icon.getMaxU(), icon.getMaxV(),
                topX + sideX, topY + sideY, topZ + sideZ, icon.getMaxU(), icon.getMinV()
        );
    }

    private double getXTilt(int meta) {
        switch (meta & 7) {
            case 1:
                return -0.4D;
            case 2:
                return 0.4D;
            default:
                return 0.0D;
        }
    }

    private boolean isWallMounted(int meta) {
        int side = meta & 7;
        return side >= 1 && side <= 4;
    }

    private double getZTilt(int meta) {
        switch (meta & 7) {
            case 3:
                return -0.4D;
            case 4:
                return 0.4D;
            default:
                return 0.0D;
        }
    }

    private void addDoubleSidedQuad(
            Tessellator tessellator,
            double x1, double y1, double z1, double u1, double v1,
            double x2, double y2, double z2, double u2, double v2,
            double x3, double y3, double z3, double u3, double v3,
            double x4, double y4, double z4, double u4, double v4) {
        tessellator.addVertexWithUV(x1, y1, z1, u1, v1);
        tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
        tessellator.addVertexWithUV(x3, y3, z3, u3, v3);
        tessellator.addVertexWithUV(x4, y4, z4, u4, v4);
        tessellator.addVertexWithUV(x4, y4, z4, u4, v4);
        tessellator.addVertexWithUV(x3, y3, z3, u3, v3);
        tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
        tessellator.addVertexWithUV(x1, y1, z1, u1, v1);
    }

    private void clearCache() {
        this.cachedWorld = null;
        this.torches.clear();
        this.lastScanTick = Integer.MIN_VALUE;
        this.lastScanX = Integer.MIN_VALUE;
        this.lastScanY = Integer.MIN_VALUE;
        this.lastScanZ = Integer.MIN_VALUE;
    }

    private static final class TorchEntry {
        private final int x;
        private final int y;
        private final int z;

        private TorchEntry(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
