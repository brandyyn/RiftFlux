package com.voidsrift.riftflux.vortex.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import com.voidsrift.riftflux.vortex.item.ModItems;
import org.lwjgl.opengl.GL11;

public class RenderDeathRune extends Render {
   private ItemStack rune;

   public RenderDeathRune() {
      this.rune = new ItemStack(ModItems.runeThanatos);
   }

   public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
      GL11.glPushMatrix();
      double xShake = 0.0D;
      double yShake = 0.0D;
      double zShake = 0.0D;
      if (!Minecraft.getMinecraft().isGamePaused()) {
         xShake = (Math.random() - 0.5D) / 12.0D;
         yShake = (Math.random() - 0.5D) / 12.0D;
         zShake = (Math.random() - 0.25D) / 12.0D;
      }

      GL11.glTranslated(x + xShake, y + yShake + 0.05D, z + zShake);
      GL11.glScalef(0.8F, 0.8F, 0.8F);
      GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
      Tessellator tessellator = Tessellator.instance;
      this.bindTexture(TextureMap.locationItemsTexture);
      IIcon icon = this.rune.getItem().getIcon(this.rune, 0);
      tessellator.startDrawingQuads();
      tessellator.setNormal(0.0F, 1.0F, 0.0F);
      tessellator.addVertexWithUV(-0.5D, -0.25D, 0.0D, (double)icon.getMinU(), (double)icon.getMaxV());
      tessellator.addVertexWithUV(0.5D, -0.25D, 0.0D, (double)icon.getMaxU(), (double)icon.getMaxV());
      tessellator.addVertexWithUV(0.5D, 0.75D, 0.0D, (double)icon.getMaxU(), (double)icon.getMinV());
      tessellator.addVertexWithUV(-0.5D, 0.75D, 0.0D, (double)icon.getMinU(), (double)icon.getMinV());
      tessellator.draw();
      GL11.glPopMatrix();
   }

   protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
      return null;
   }
}
