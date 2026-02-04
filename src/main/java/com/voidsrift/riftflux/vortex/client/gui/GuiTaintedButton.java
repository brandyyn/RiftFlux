package com.voidsrift.riftflux.vortex.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiTaintedButton extends GuiButton {
   public static final ResourceLocation texture = new ResourceLocation("riftflux", "textures/gui/taintbutton.png");

   public GuiTaintedButton(int id, int x, int y, String string) {
      super(id, x, y, string);
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         FontRenderer fontrenderer = mc.fontRenderer;
         mc.getTextureManager().bindTexture(texture);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(3042);
         OpenGlHelper.glBlendFunc(770, 771, 1, 0);
         GL11.glBlendFunc(770, 771);
         this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         int k = this.getHoverState(this.field_146123_n);
         this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0 + k * 20, this.width / 2, this.height);
         this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 0 + k * 20, this.width / 2, this.height);
         this.mouseDragged(mc, mouseX, mouseY);
         int l = 14737632;
         if (this.packedFGColour != 0) {
            l = this.packedFGColour;
         } else if (!this.enabled) {
            l = 10526880;
         } else if (this.field_146123_n) {
            l = 16777120;
         }

         this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
      }

   }
}
