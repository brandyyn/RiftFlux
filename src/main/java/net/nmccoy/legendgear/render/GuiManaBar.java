/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.event.FOVUpdateEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.common.ForgeHooks
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.magic.IMana;
import org.lwjgl.opengl.GL11;

public class GuiManaBar
extends Gui {
    protected static final ResourceLocation vignetteTexPath = new ResourceLocation("legendgear", "textures/rainbowWhoosh.png");
    protected static final ResourceLocation vignette2TexPath = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation mod_icons = new ResourceLocation("legendgear", "textures/icons.png");
    private int updateCounter = 0;
    private Random rand = new Random();
    public boolean smoothCameraReset;

    @SubscribeEvent
    public void scopeFOV(FOVUpdateEvent event) {
        EntityPlayerSP player = event.entity;
        ItemStack stack = player.getHeldItem();
        if (player.getItemInUseDuration() > 5 && stack != null && stack.getItem() == LegendGear2.spottingScope) {
            event.newfov *= 0.15f;
            Minecraft mc = Minecraft.getMinecraft();
            if (!mc.gameSettings.smoothCamera) {
                this.smoothCameraReset = true;
            }
        } else if (this.smoothCameraReset) {
            this.smoothCameraReset = false;
            Minecraft.getMinecraft().gameSettings.smoothCamera = false;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        PlayerStarstatsExtension pse;
        float drinkTime;
        float fade;
        if (!event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.HELMET) {
            return;
        }
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        ItemStack stack = player.getHeldItem();
        if (player.getItemInUseDuration() > 5 && stack != null && stack.getItem() == LegendGear2.spottingScope) {
            ScaledResolution res = event.resolution;
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            GL11.glDisable((int)2929);
            GL11.glDepthMask((boolean)false);
            OpenGlHelper.glBlendFunc((int)0, (int)769, (int)1, (int)0);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(vignette2TexPath);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(0.0, (double)height, -90.0, 0.0, 1.0);
            tessellator.addVertexWithUV((double)width, (double)height, -90.0, 1.0, 1.0);
            tessellator.addVertexWithUV((double)width, 0.0, -90.0, 1.0, 0.0);
            tessellator.addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
            tessellator.draw();
            GL11.glDepthMask((boolean)true);
            GL11.glEnable((int)2929);
        }
        if ((fade = (60.0f - (drinkTime = (float)(pse = PlayerStarstatsExtension.get((EntityPlayer)Minecraft.getMinecraft().thePlayer)).starwellDrinkTime() + event.partialTicks)) / 60.0f + 0.25f * (float)(pse.starwellCharge - 3)) <= 0.0f) {
            return;
        }
        ScaledResolution res = event.resolution;
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        double voff = (double)Minecraft.getSystemTime() / 150.0;
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glBlendFunc((int)770, (int)1);
        GL11.glColor4f((float)fade, (float)fade, (float)fade, (float)1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(vignetteTexPath);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0, (double)height, -90.0, 0.0, 1.0 + voff);
        tessellator.addVertexWithUV((double)width, (double)height, -90.0, 1.0, 1.0 + voff);
        tessellator.addVertexWithUV((double)width, 0.0, -90.0, 1.0, 0.0 + voff);
        tessellator.addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0 + voff);
        tessellator.draw();
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2929);
    }

    @SubscribeEvent
    public void onRenderArmorBar(RenderGameOverlayEvent event) {
        int level;
        if (!event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.ARMOR) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        this.updateCounter = (int)(mc.getSystemTime() / 50L % 1000L);
        this.rand.setSeed(this.updateCounter);
        ItemStack held = mc.thePlayer.getHeldItem();
        boolean holdingSpell = false;
        if (held != null && held.getItem() instanceof IMana) {
            holdingSpell = true;
        }
        if ((level = (int)(20.0f - PlayerStarstatsExtension.get((EntityPlayer)mc.thePlayer).getMana())) == 20 && !holdingSpell) {
            return;
        }
        event.setCanceled(true);
        ScaledResolution res = event.resolution;
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        int right_height = 39;
        int left_height = 39;
        mc.getTextureManager().bindTexture(mod_icons);
        GL11.glEnable((int)3042);
        int left = width / 2 - 91;
        int top = height - left_height - 10;
        if (mc.thePlayer.getAbsorptionAmount() > 0.0f) {
            top -= 10;
        }
        int armor = ForgeHooks.getTotalArmorValue((EntityPlayer)mc.thePlayer);
        for (int i = 1; i < 20; i += 2) {
            int offset = 0;
            if (i < armor) {
                offset = 9;
            }
            if (i == armor) {
                offset = 18;
            }
            int y = top;
            if (level <= armor && this.updateCounter % 20 == 0) {
                y = top + (this.rand.nextInt(3) - 1);
            }
            if (i < level) {
                this.drawTexturedModalRect(left, y, 0, offset, 9, 9);
            } else if (i == level) {
                this.drawTexturedModalRect(left, y, 9, offset, 9, 9);
            } else if (i > level) {
                this.drawTexturedModalRect(left, y, 18, offset, 9, 9);
            }
            left += 8;
        }
        GL11.glDisable((int)3042);
        mc.getTextureManager().bindTexture(Gui.icons);
    }
}

