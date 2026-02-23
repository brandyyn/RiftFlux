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
        if (!this.mc.thePlayer.capabilities.isCreativeMode) {
            int u;
            RINPlayer2 rins = RINPlayer2.get((EntityPlayer)this.mc.thePlayer);
            ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            int sw = res.getScaledWidth();
            int sh = res.getScaledHeight();
            int xPos = sw / 2;
            int yPos = sh / 2;
            int confhig = 15 + ConfigurationMoD2.BarY;
            int confwit = 0 + ConfigurationMoD2.BarX;
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.mc.getTextureManager().bindTexture(texturepathBars);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)rins.getRendertime());
            if (ConfigurationMoD2.BarType == 2) {
                u = (int)(70.0 * (rins.getSprintime() / rins.getMaxSprintingtime()));
                if (rins.isOvercharged()) {
                    u = (int)(70.0 * (rins.getRetime() / rins.getOverchargetime()));
                }
                this.drawTexturedModalRect(xPos + confwit, yPos - confhig, 0, 0, 32, 70);
                if (rins.isOvercharged()) {
                    this.drawTexturedModalRect(xPos + confwit, yPos - confhig + u, 64, 0 + u, 32, 70 - u);
                } else {
                    this.drawTexturedModalRect(xPos + confwit, yPos - confhig + u, 32, 0 + u, 32, 70 - u);
                }
            }
            if (ConfigurationMoD2.BarType == 1) {
                u = (int)(34.0 * (rins.getSprintime() / rins.getMaxSprintingtime()));
                if (rins.isOvercharged()) {
                    u = (int)(34.0 * (rins.getRetime() / rins.getOverchargetime()));
                }
                this.drawTexturedModalRect(xPos + confwit, yPos - confhig, 0, 70, 16, 34);
                if (rins.isOvercharged()) {
                    this.drawTexturedModalRect(xPos + confwit, yPos - confhig + u, 32, 70 + u, 16, 34 - u);
                } else {
                    this.drawTexturedModalRect(xPos + confwit, yPos - confhig + u, 16, 70 + u, 16, 34 - u);
                }
            }
            if (ConfigurationMoD2.BarType == 0) {
                int p = 13;
                int u2 = (int)(26.0 * (rins.getSprintime() / rins.getMaxSprintingtime()));
                if (rins.isOvercharged()) {
                    u2 = (int)(26.0 * (rins.getRetime() / rins.getOverchargetime()));
                }
                this.drawTexturedModalRect(xPos + confwit - 14, yPos - confhig + p, 0, 108, 16, 27);
                if (rins.isOvercharged()) {
                    this.drawTexturedModalRect(xPos + confwit - 14, yPos - confhig + u2 + 1 + p, 32, 109 + u2, 16, 26 - u2);
                } else {
                    this.drawTexturedModalRect(xPos + confwit - 14, yPos - confhig + u2 + 1 + p, 16, 109 + u2, 16, 26 - u2);
                }
            }
        }
    }
}

