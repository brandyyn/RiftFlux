package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.dualhotbar.DualHotbarConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

public final class PickupStarHotbarHud {

    private static final PickupStarHotbarHud INSTANCE = new PickupStarHotbarHud();
    private static final String TAG_NEW = "riftflux_new";
    private static final ResourceLocation TEX =
            new ResourceLocation(Constants.MODID, "textures/gui/pickup_star.png"); // 16x16
    private static boolean bootstrapped;

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onHud(RenderGameOverlayEvent.Post e) {
        if (!ModConfig.enableItemPickupStar) return;
        if (!ModConfig.itemPickupStarShowHotbarHud) return;
        if (e.type != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) return;

        final boolean dualEnabled = DualHotbarConfig.enable;
        final int numBars = dualEnabled ? DualHotbarConfig.numHotbars : 1;
        int totalSlots = 9 * numBars;
        if (!hasStarredHotbarSlot(mc, totalSlots)) {
            return;
        }

        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        final int sw = sr.getScaledWidth();
        final int sh = sr.getScaledHeight();

        final boolean longHotbar = dualEnabled && !DualHotbarConfig.twoLayerRendering;
        final int columns = longHotbar ? 18 : 9;
        final int baseX = (sw / 2) - 90 + 2 + (longHotbar ? -90 : 0);
        final int baseY = sh - 16 - 3;
        final int offset = 20;

        mc.getTextureManager().bindTexture(TEX);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glColor4f(1F, 1F, 1F, 1F);

            final Tessellator t = Tessellator.instance;
            boolean drawing = false;
            for (int i = 0; i < totalSlots; i++) {
                ItemStack st = mc.thePlayer.inventory.mainInventory[i];
                if (st == null) continue;
                NBTTagCompound tag = st.getTagCompound();
                if (tag == null || !tag.getBoolean(TAG_NEW)) continue;

                int x = baseX + (i % columns) * 20;
                int y = baseY - (i / columns) * offset;

                if (!drawing) {
                    t.startDrawingQuads();
                    drawing = true;
                }
                t.addVertexWithUV(x     , y + 16, 0, 0, 1);
                t.addVertexWithUV(x + 16, y + 16, 0, 1, 1);
                t.addVertexWithUV(x + 16, y     , 0, 1, 0);
                t.addVertexWithUV(x     , y     , 0, 0, 0);
            }
            if (drawing) {
                t.draw();
            }
        } finally {
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopAttrib();
        }
    }

    private static boolean hasStarredHotbarSlot(Minecraft mc, int totalSlots) {
        if (mc == null || mc.thePlayer == null || mc.thePlayer.inventory == null) {
            return false;
        }
        ItemStack[] inventory = mc.thePlayer.inventory.mainInventory;
        if (inventory == null) {
            return false;
        }
        int count = Math.min(totalSlots, inventory.length);
        for (int i = 0; i < count; i++) {
            ItemStack stack = inventory[i];
            if (stack == null) {
                continue;
            }
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.getBoolean(TAG_NEW)) {
                return true;
            }
        }
        return false;
    }
}
