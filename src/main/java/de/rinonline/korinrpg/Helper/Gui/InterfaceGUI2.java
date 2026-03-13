/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.EventPriority
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Chat
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Pre
 *  org.lwjgl.opengl.GL11
 */
package de.rinonline.korinrpg.Helper.Gui;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.rinonline.korinrpg.ConfigurationMoD2;
import de.rinonline.korinrpg.Helper.NBT.RINPlayer2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class InterfaceGUI2
extends GuiScreen {
    private Minecraft mc;
    private static final ResourceLocation texturepathBars = new ResourceLocation("dss:textures/gui/stamina_bar.png");

    public InterfaceGUI2(Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public void onRenderExperienceBar(RenderGameOverlayEvent.Pre event) {
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onRenderExperienceBar(RenderGameOverlayEvent.Chat event) {
        if (this.mc == null || this.mc.thePlayer == null || this.mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }

        RINPlayer2 rins = RINPlayer2.get((EntityPlayer)this.mc.thePlayer);
        if (rins == null) {
            return;
        }

        ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int sw = res.getScaledWidth();
        int sh = res.getScaledHeight();
        int xPos = sw / 2;
        int yPos = sh / 2;
        int confhig = 15 + ConfigurationMoD2.BarY;
        int confwit = 0 + ConfigurationMoD2.BarX;

        float alpha = clamp01(rins.getRendertime());
        if (alpha <= 0.0f) {
            return;
        }

        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(texturepathBars);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, alpha);

        if (ConfigurationMoD2.BarType == 2) {
            int u = computeFillOffset(rins, 70);
            int overlayHeight = 70 - u;
            this.drawTexturedModalRect(xPos + confwit, yPos - confhig, 0, 0, 32, 70);
            if (overlayHeight > 0) {
                this.drawTexturedModalRect(xPos + confwit, yPos - confhig + u, rins.isOvercharged() ? 64 : 32, u, 32, overlayHeight);
            }
        }

        if (ConfigurationMoD2.BarType == 1) {
            int u = computeFillOffset(rins, 34);
            int overlayHeight = 34 - u;
            this.drawTexturedModalRect(xPos + confwit, yPos - confhig, 0, 70, 16, 34);
            if (overlayHeight > 0) {
                this.drawTexturedModalRect(xPos + confwit, yPos - confhig + u, rins.isOvercharged() ? 32 : 16, 70 + u, 16, overlayHeight);
            }
        }

        if (ConfigurationMoD2.BarType == 0) {
            int p = 13;
            int u2 = computeFillOffset(rins, 26);
            int overlayHeight = 26 - u2;
            this.drawTexturedModalRect(xPos + confwit - 14, yPos - confhig + p, 0, 108, 16, 27);
            if (overlayHeight > 0) {
                this.drawTexturedModalRect(xPos + confwit - 14, yPos - confhig + u2 + 1 + p, rins.isOvercharged() ? 32 : 16, 109 + u2, 16, overlayHeight);
            }
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private static int computeFillOffset(RINPlayer2 rins, int pixelHeight) {
        double current = rins.isOvercharged() ? rins.getRetime() : rins.getSprintime();
        double max = rins.isOvercharged() ? rins.getOverchargetime() : rins.getMaxSprintingtime();
        if (!(max > 0.0)) {
            return 0;
        }
        double ratio = current / max;
        if (ratio < 0.0) {
            ratio = 0.0;
        } else if (ratio > 1.0) {
            ratio = 1.0;
        }
        int value = (int)Math.round((double)pixelHeight * ratio);
        if (value < 0) {
            return 0;
        }
        if (value > pixelHeight) {
            return pixelHeight;
        }
        return value;
    }

    private static float clamp01(float value) {
        if (value < 0.0f) {
            return 0.0f;
        }
        if (value > 1.0f) {
            return 1.0f;
        }
        return value;
    }
}
