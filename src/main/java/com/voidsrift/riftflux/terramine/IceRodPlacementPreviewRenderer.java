package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.compat.BackhandCompat;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class IceRodPlacementPreviewRenderer {

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!ModConfig.enableTerraModule || !ModConfig.iceRodPlacementPreviewEnabled) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        EntityPlayer player = mc.thePlayer;
        ItemStack activeStack = player.getHeldItem();
        if (activeStack == null || !(activeStack.getItem() instanceof ItemIceRod)) {
            activeStack = null;
        }

        if (activeStack == null && BackhandCompat.isAvailable()) {
            ItemStack offhand = BackhandCompat.getOffhandItem(player);
            if (offhand != null && offhand.getItem() instanceof ItemIceRod
                    && ItemIceRod.canUseFromOffhand(offhand, player)) {
                activeStack = offhand;
            }
        }

        if (activeStack == null) {
            return;
        }

        ItemIceRod iceRod = (ItemIceRod) activeStack.getItem();
        int[] placePos = iceRod.findPlacementForPlayer(mc.theWorld, player);
        if (placePos == null) {
            return;
        }

        Entity camera = mc.renderViewEntity == null ? player : mc.renderViewEntity;
        double px = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * event.partialTicks;
        double py = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * event.partialTicks;
        double pz = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * event.partialTicks;

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                placePos[0],
                placePos[1],
                placePos[2],
                placePos[0] + 1.0D,
                placePos[1] + 1.0D,
                placePos[2] + 1.0D
        ).expand(0.002D, 0.002D, 0.002D).getOffsetBoundingBox(-px, -py, -pz);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LINE_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);
            GL11.glLineWidth(2.0F);

            GL11.glColor4f(0.45F, 0.80F, 1.0F, 0.15F);
            drawFilledBox(box);

            GL11.glColor4f(0.70F, 0.90F, 1.0F, 0.90F);
            RenderGlobal.drawOutlinedBoundingBox(box, 0xB2E8FF);
        } finally {
            GL11.glDepthMask(true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopAttrib();
        }
    }

    private static void drawFilledBox(AxisAlignedBB box) {
        Tessellator t = Tessellator.instance;
        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        t.startDrawingQuads();
        t.addVertex(minX, minY, minZ);
        t.addVertex(minX, maxY, minZ);
        t.addVertex(minX, maxY, maxZ);
        t.addVertex(minX, minY, maxZ);

        t.addVertex(maxX, minY, minZ);
        t.addVertex(maxX, minY, maxZ);
        t.addVertex(maxX, maxY, maxZ);
        t.addVertex(maxX, maxY, minZ);

        t.addVertex(minX, minY, minZ);
        t.addVertex(minX, minY, maxZ);
        t.addVertex(maxX, minY, maxZ);
        t.addVertex(maxX, minY, minZ);

        t.addVertex(minX, maxY, minZ);
        t.addVertex(maxX, maxY, minZ);
        t.addVertex(maxX, maxY, maxZ);
        t.addVertex(minX, maxY, maxZ);

        t.addVertex(minX, minY, minZ);
        t.addVertex(maxX, minY, minZ);
        t.addVertex(maxX, maxY, minZ);
        t.addVertex(minX, maxY, minZ);

        t.addVertex(minX, minY, maxZ);
        t.addVertex(minX, maxY, maxZ);
        t.addVertex(maxX, maxY, maxZ);
        t.addVertex(maxX, minY, maxZ);
        t.draw();
    }
}
