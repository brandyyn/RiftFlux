package com.voidsrift.riftflux.dualhotbar;

import java.lang.reflect.Constructor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RenderHandler {
    private static final ResourceLocation WIDGITS = new ResourceLocation("textures/gui/widgets.png");

    private boolean recievedPost = true;

    private static Constructor<ScaledResolution> scaledResolution172Constructor = null;

    static {
        try {
            scaledResolution172Constructor = ScaledResolution.class.getConstructor(
                    GameSettings.class,
                    int.class,
                    int.class
            );
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderHotbar(RenderGameOverlayEvent.Pre event) {
        if (!DualHotbarConfig.enable) {
            return;
        }

        if (event.type == ElementType.HOTBAR) {
            Minecraft mc = Minecraft.getMinecraft();

            ScaledResolution res;
            if (scaledResolution172Constructor != null) {
                try {
                    res = scaledResolution172Constructor.newInstance(
                            mc.gameSettings,
                            mc.displayWidth,
                            mc.displayHeight
                    );
                } catch (Exception ignored) {
                    return;
                }
            } else {
                res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            }

            int width = res.getScaledWidth();
            int height = res.getScaledHeight();

            mc.mcProfiler.startSection("actionBar");

            int offset = 20;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(WIDGITS);

            InventoryPlayer inv = mc.thePlayer.inventory;
            if (DualHotbarConfig.twoLayerRendering) {
                mc.ingameGUI.drawTexturedModalRect(width / 2 - 91, height - 22, 0, 0, 182, 22);

                if (!DualHotbarState.installedOnServer) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                }

                for (int i = 1; i < DualHotbarConfig.numHotbars; i++) {
                    mc.ingameGUI.drawTexturedModalRect(
                            width / 2 - 91,
                            height - 22 * i - offset + (i - 1) * 2,
                            0,
                            0,
                            182,
                            21
                    );
                }

                if (!DualHotbarState.installedOnServer) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f);
                }

                mc.ingameGUI.drawTexturedModalRect(
                        width / 2 - 91 - 1 + (inv.currentItem % 9) * 20,
                        height - 22 - 1 - ((inv.currentItem / 9) * offset),
                        0,
                        22,
                        24,
                        22
                );
                mc.ingameGUI.drawTexturedModalRect(
                        width / 2 - 91 - 1 + (inv.currentItem % 9) * 20,
                        height - 1 - ((inv.currentItem / 9) * offset),
                        0,
                        22,
                        24,
                        1
                );
            } else {
                mc.ingameGUI.drawTexturedModalRect(width / 2 - 91 - 90, height - 22, 0, 0, 182, 22);
                mc.ingameGUI.drawTexturedModalRect(width / 2 - 91 + 91, height - 22, 1, 0, 181, 22);
                mc.ingameGUI.drawTexturedModalRect(width / 2 - 91 + 91 - 1, height - 22, 20, 0, 22, 22);
                if (DualHotbarConfig.numHotbars == 4) {
                    mc.ingameGUI.drawTexturedModalRect(
                            width / 2 - 91 - 90,
                            height - 22 - offset,
                            0,
                            0,
                            182,
                            21
                    );
                    mc.ingameGUI.drawTexturedModalRect(
                            width / 2 - 91 + 91,
                            height - 22 - offset,
                            1,
                            0,
                            181,
                            21
                    );
                    mc.ingameGUI.drawTexturedModalRect(
                            width / 2 - 91 + 91 - 1,
                            height - 22 - offset,
                            20,
                            0,
                            22,
                            21
                    );
                }
                mc.ingameGUI.drawTexturedModalRect(
                        width / 2 - 91 - 1 + (inv.currentItem % 18) * 20 - 90,
                        height - 22 - 1 - ((inv.currentItem / 18) * offset),
                        0,
                        22,
                        24,
                        22
                );
                mc.ingameGUI.drawTexturedModalRect(
                        width / 2 - 91 - 1 + (inv.currentItem % 18) * 20 - 90,
                        height - 1 - ((inv.currentItem / 18) * offset),
                        0,
                        22,
                        24,
                        1
                );
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();

            for (int i = 0; i < 9 * DualHotbarConfig.numHotbars; ++i) {
                if (DualHotbarConfig.twoLayerRendering) {
                    int x = width / 2 - 90 + (i % 9) * 20 + 2;
                    int z = height - 16 - 3 - ((i / 9) * offset);
                    if (!DualHotbarState.installedOnServer) {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                    }
                    renderInventorySlot(i, x, z, 1f);
                    if (!DualHotbarState.installedOnServer) {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f);
                    }
                } else {
                    int x = width / 2 - 90 + (i % 18) * 20 + 2 - 90;
                    int z = height - 16 - 3 - ((i / 18) * offset);
                    renderInventorySlot(i, x, z, 1f);
                }
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            mc.mcProfiler.endSection();

            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void shiftRendererUp(RenderGameOverlayEvent.Pre event) {
        if (!DualHotbarConfig.enable
                || (!DualHotbarConfig.twoLayerRendering && DualHotbarConfig.numHotbars != 4)) {
            return;
        }

        if (event.type == ElementType.ARMOR || event.type == ElementType.EXPERIENCE
                || event.type == ElementType.FOOD || event.type == ElementType.HEALTH
                || event.type == ElementType.HEALTHMOUNT || event.type == ElementType.JUMPBAR
                || event.type == ElementType.AIR) {
            if (recievedPost == false) {
                GL11.glPopMatrix();
            }

            recievedPost = false;
            GL11.glPushMatrix();

            if (DualHotbarConfig.twoLayerRendering) {
                GL11.glTranslatef(0, -20 * (DualHotbarConfig.numHotbars - 1), 0);
            } else {
                GL11.glTranslatef(0, -20 * (DualHotbarConfig.numHotbars / 2 - 1), 0);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void shiftRendererDown(RenderGameOverlayEvent.Post event) {
        if (!DualHotbarConfig.enable
                || (!DualHotbarConfig.twoLayerRendering && DualHotbarConfig.numHotbars != 4)) {
            return;
        }

        if (event.type == ElementType.ARMOR || event.type == ElementType.EXPERIENCE
                || event.type == ElementType.FOOD || event.type == ElementType.HEALTH
                || event.type == ElementType.HEALTHMOUNT || event.type == ElementType.JUMPBAR
                || event.type == ElementType.AIR) {
            recievedPost = true;
            GL11.glPopMatrix();
        }
    }

    public static void shiftUp() {
        if (!DualHotbarConfig.enable
                || (!DualHotbarConfig.twoLayerRendering && DualHotbarConfig.numHotbars != 4)) {
            return;
        }

        GL11.glPushMatrix();
        if (DualHotbarConfig.twoLayerRendering) {
            GL11.glTranslatef(0, -20 * (DualHotbarConfig.numHotbars - 1), 0);
        } else {
            GL11.glTranslatef(0, -20 * (DualHotbarConfig.numHotbars / 2 - 1), 0);
        }
    }

    public static void shiftDown() {
        if (!DualHotbarConfig.enable
                || (!DualHotbarConfig.twoLayerRendering && DualHotbarConfig.numHotbars != 4)) {
            return;
        }

        GL11.glPopMatrix();
    }

    protected void renderInventorySlot(int slotIndex, int x, int y, float partialTicks) {
        if (!DualHotbarConfig.enable) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        RenderItem itemRenderer = new RenderItem();

        ItemStack itemstack = mc.thePlayer.inventory.mainInventory[slotIndex];

        if (itemstack != null) {
            float anim = (float) itemstack.animationsToGo - partialTicks;

            if (anim > 0.0F) {
                GL11.glPushMatrix();
                float scale = 1.0F + anim / 5.0F;
                GL11.glTranslatef((float) (x + 8), (float) (y + 12), 0.0F);
                GL11.glScalef(1.0F / scale, (scale + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
            }

            itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);

            if (anim > 0.0F) {
                GL11.glPopMatrix();
            }

            itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);
        }
    }
}
