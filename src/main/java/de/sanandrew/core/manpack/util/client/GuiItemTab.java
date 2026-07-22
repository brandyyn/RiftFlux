/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.core.manpack.util.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiItemTab
extends GuiButton {
    protected static RenderItem itemRenderer = new RenderItem();
    protected static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public ResourceLocation baseTexture = TextureMap.locationItemsTexture;
    protected IIcon renderedIcon;
    protected ResourceLocation texture = null;
    protected boolean isRight;
    protected boolean hasEffect;
    public int textureBaseX = 0;
    public int textureBaseY = 0;

    public GuiItemTab(int id, int xPos, int yPos, String name, IIcon icon, boolean right, boolean hasEff, ResourceLocation tabTexture) {
        super(id, xPos, yPos, name);
        this.width = 26;
        this.height = 26;
        this.renderedIcon = icon;
        this.isRight = right;
        this.hasEffect = hasEff;
        this.texture = tabTexture;
    }

    public void setIcon(IIcon ico) {
        this.renderedIcon = ico;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer var4 = mc.fontRenderer;
            mc.getTextureManager().bindTexture(this.texture);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, this.textureBaseX + 26 * (this.isRight ? 0 : 1), this.textureBaseY + var5 * 26, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            GL11.glEnable((int)2929);
            GL11.glDisable((int)32826);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable((int)2896);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glPushMatrix();
            GL11.glEnable((int)32826);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GuiItemTab.itemRenderer.zLevel = 300.0f;
            this.drawIcon(this.renderedIcon, this.xPosition + 5, this.yPosition + 5, mc, 0xFFFFFF);
            GuiItemTab.itemRenderer.zLevel = 0.0f;
            GL11.glPopMatrix();
            GL11.glEnable((int)2896);
            GL11.glDisable((int)2929);
            if (this.field_146123_n) {
                this.drawTabHoveringText(this.displayString, this.xPosition - (this.isRight ? var4.getStringWidth(this.displayString) + 5 : -5), this.yPosition + 21, var4);
            }
            RenderHelper.disableStandardItemLighting();
        }
    }

    protected void drawTabHoveringText(String par1Str, int par2, int par3, FontRenderer fontRenderer) {
        GL11.glDisable((int)32826);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable((int)2896);
        GL11.glEnable((int)2929);
        int var4 = fontRenderer.getStringWidth(par1Str);
        int var5 = par2 + 12;
        int var6 = par3 - 12;
        int var8 = 8;
        this.zLevel = 300.0f;
        int var9 = -267386864;
        this.drawGradientRect(var5 - 3, var6 - 4, var5 + var4 + 3, var6 - 3, var9, var9);
        this.drawGradientRect(var5 - 3, var6 + var8 + 3, var5 + var4 + 3, var6 + var8 + 4, var9, var9);
        this.drawGradientRect(var5 - 3, var6 - 3, var5 + var4 + 3, var6 + var8 + 3, var9, var9);
        this.drawGradientRect(var5 - 4, var6 - 3, var5 - 3, var6 + var8 + 3, var9, var9);
        this.drawGradientRect(var5 + var4 + 3, var6 - 3, var5 + var4 + 4, var6 + var8 + 3, var9, var9);
        int var10 = 0x505000FF;
        int var11 = (var10 & 0xFEFEFE) >> 1 | var10 & 0xFF000000;
        this.drawGradientRect(var5 - 3, var6 - 3 + 1, var5 - 3 + 1, var6 + var8 + 3 - 1, var10, var11);
        this.drawGradientRect(var5 + var4 + 2, var6 - 3 + 1, var5 + var4 + 3, var6 + var8 + 3 - 1, var10, var11);
        this.drawGradientRect(var5 - 3, var6 - 3, var5 + var4 + 3, var6 - 3 + 1, var10, var10);
        this.drawGradientRect(var5 - 3, var6 + var8 + 2, var5 + var4 + 3, var6 + var8 + 3, var11, var11);
        GL11.glDisable((int)2929);
        fontRenderer.drawStringWithShadow(par1Str, var5, var6, -1);
        this.zLevel = 0.0f;
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable((int)32826);
    }

    private void drawIcon(IIcon ico, int x, int y, Minecraft mc, int color) {
        GL11.glDisable((int)2896);
        ResourceLocation resourcelocation = this.baseTexture;
        mc.renderEngine.bindTexture(resourcelocation);
        if (this.renderedIcon == null) {
            this.renderedIcon = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
        }
        int i1 = color;
        float f = (float)(i1 >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(i1 >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(i1 & 0xFF) / 255.0f;
        GL11.glColor4f((float)f, (float)f1, (float)f2, (float)1.0f);
        itemRenderer.renderIcon(x, y, this.renderedIcon, 16, 16);
        GL11.glEnable((int)2896);
        if (this.hasEffect) {
            this.renderEffect(mc.renderEngine, x, y);
        }
        GL11.glEnable((int)2884);
    }

    protected void renderEffect(TextureManager manager, int x, int y) {
        GL11.glDepthFunc((int)516);
        GL11.glDisable((int)2896);
        GL11.glDepthMask((boolean)false);
        manager.bindTexture(RES_ITEM_GLINT);
        GuiItemTab.itemRenderer.zLevel -= 50.0f;
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)774, (int)774);
        GL11.glColor4f((float)0.5f, (float)0.25f, (float)0.8f, (float)1.0f);
        this.renderGlint(x * 431278612 + y * 32178161, x - 2, y - 2, 20, 20);
        GL11.glDisable((int)3042);
        GL11.glDepthMask((boolean)true);
        GuiItemTab.itemRenderer.zLevel += 50.0f;
        GL11.glEnable((int)2896);
        GL11.glDepthFunc((int)515);
    }

    private void renderGlint(int par1, int x, int y, int width, int height) {
        for (int j1 = 0; j1 < 2; ++j1) {
            if (j1 == 0) {
                GL11.glBlendFunc((int)768, (int)1);
            }
            if (j1 == 1) {
                GL11.glBlendFunc((int)768, (int)1);
            }
            float f = 0.00390625f;
            float f1 = 0.00390625f;
            float f2 = (float)(Minecraft.getSystemTime() % (long)(3000 + j1 * 1873)) / (3000.0f + (float)(j1 * 1873)) * 256.0f;
            float f3 = 0.0f;
            Tessellator tessellator = Tessellator.instance;
            float f4 = 4.0f;
            if (j1 == 1) {
                f4 = -1.0f;
            }
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(x, y + height, this.zLevel, (f2 + (float)height * f4) * f, (f3 + (float)height) * f1);
            tessellator.addVertexWithUV(x + width, y + height, this.zLevel, (f2 + (float)width + (float)height * f4) * f, (f3 + (float)height) * f1);
            tessellator.addVertexWithUV(x + width, y, this.zLevel, (f2 + (float)width) * f, (f3 + 0.0f) * f1);
            tessellator.addVertexWithUV(x, y, this.zLevel, (f2 + 0.0f) * f, (f3 + 0.0f) * f1);
            tessellator.draw();
        }
    }
}

