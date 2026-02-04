package com.voidsrift.riftflux.vortex.lib.helper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import com.voidsrift.riftflux.vortex.client.gui.IDefaultGui;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;

@SideOnly(Side.CLIENT)
public class RenderHelper {
   public static final ResourceLocation defaultGui = new ResourceLocation("riftflux", "textures/gui/defaultgui.png");

   @SideOnly(Side.CLIENT)
   public static void drawItemStack(Minecraft mc, RenderItem ri, ItemStack stack, int x, int y) {
      if (stack != null) {
         GL11.glPushMatrix();
         ri.zLevel = 300.0F;
         GL11.glEnable(2929);
         GL11.glEnable(32826);
         net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
         ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
         ri.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
         net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
         GL11.glDisable(32826);
         GL11.glDisable(2929);
         ri.zLevel = 0.0F;
         GL11.glPopMatrix();
      }

   }

   @SideOnly(Side.CLIENT)
   public static void drawCustomTooltip(FontRenderer fr, List tooltips, int x, int y, int subTipColor, ItemStack stack, boolean doRarity) {
      GL11.glPushMatrix();
      net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
      GL11.glDisable(32826);
      GL11.glDisable(2929);
      if (!tooltips.isEmpty()) {
         int var5 = 0;

         int var15;
         int var9;
         for(var15 = 0; var15 < tooltips.size(); ++var15) {
            String var7 = (String)tooltips.get(var15);
            var9 = fr.getStringWidth(var7);
            if (var9 > var5) {
               var5 = var9;
            }
         }

         var15 = x + 12;
         int var16 = y - 12;
         var9 = 8;
         if (tooltips.size() > 1) {
            var9 += 2 + (tooltips.size() - 1) * 10;
         }

         int var10 = -267386864;
         UtilsFX.drawGradientRect(var15 - 3, var16 - 4, var15 + var5 + 3, var16 - 3, var10, var10);
         UtilsFX.drawGradientRect(var15 - 3, var16 + var9 + 3, var15 + var5 + 3, var16 + var9 + 4, var10, var10);
         UtilsFX.drawGradientRect(var15 - 3, var16 - 3, var15 + var5 + 3, var16 + var9 + 3, var10, var10);
         UtilsFX.drawGradientRect(var15 - 4, var16 - 3, var15 - 3, var16 + var9 + 3, var10, var10);
         UtilsFX.drawGradientRect(var15 + var5 + 3, var16 - 3, var15 + var5 + 4, var16 + var9 + 3, var10, var10);
         int var11 = 1347420415;
         int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
         UtilsFX.drawGradientRect(var15 - 3, var16 - 3 + 1, var15 - 3 + 1, var16 + var9 + 3 - 1, var11, var12);
         UtilsFX.drawGradientRect(var15 + var5 + 2, var16 - 3 + 1, var15 + var5 + 3, var16 + var9 + 3 - 1, var11, var12);
         UtilsFX.drawGradientRect(var15 - 3, var16 - 3, var15 + var5 + 3, var16 - 3 + 1, var11, var11);
         UtilsFX.drawGradientRect(var15 - 3, var16 + var9 + 2, var15 + var5 + 3, var16 + var9 + 3, var12, var12);

         for(int var13 = 0; var13 < tooltips.size(); ++var13) {
            String var14 = (String)tooltips.get(var13);
            if (var13 == 0) {
               if (stack != null && doRarity) {
                  var14 = stack.getRarity().rarityColor + var14;
               } else {
                  var14 = "§" + Integer.toHexString(subTipColor) + var14;
               }
            } else {
               var14 = "§7" + var14;
            }

            fr.drawStringWithShadow(var14, var15, var16, -1);
            if (var13 == 0) {
               var16 += 2;
            }

            var16 += 10;
         }
      }

      GL11.glEnable(2929);
      net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
      GL11.glPopMatrix();
   }

   @SideOnly(Side.CLIENT)
   public static void drawCenteredString(FontRenderer fr, String string, int x, int y, int color) {
      fr.drawStringWithShadow(string, x - fr.getStringWidth(string) / 2, y, color);
   }

   @SideOnly(Side.CLIENT)
   public static int[] generateDefaultGuiEdges(GuiScreen screen, int width, int height, int xOffset, int yOffset) {
      int left = screen.width / 2 - width / 2 + xOffset;
      int right = screen.width / 2 + width / 2 + xOffset;
      int top = screen.height / 2 - height / 2 + yOffset;
      int bottom = screen.height / 2 + height / 2 + yOffset;
      return new int[]{left, right, top, bottom};
   }

   @SideOnly(Side.CLIENT)
   public static void drawDefaultGui(IDefaultGui gui, int[] edges) {
      GuiScreen screen = checkBoundState(gui);
      int left = edges[0];
      int right = edges[1];
      int top = edges[2];
      int bottom = edges[3];
      int width = right - left;
      int height = bottom - top;
      screen.drawTexturedModalRect(left, top, 0, 0, 4, 4);
      screen.drawTexturedModalRect(left, bottom - 4, 0, 8, 4, 4);
      screen.drawTexturedModalRect(right - 4, top, 8, 0, 4, 4);
      screen.drawTexturedModalRect(right - 4, bottom - 4, 8, 8, 4, 4);

      int i;
      for(i = 0; i < height - 8; i += 4) {
         screen.drawTexturedModalRect(left, top + 4 + i, 0, 4, 4, 4);
      }

      for(i = 0; i < width - 8; i += 4) {
         screen.drawTexturedModalRect(left + 4 + i, top, 4, 0, 4, 4);
      }

      for(i = 0; i < height - 8; i += 4) {
         screen.drawTexturedModalRect(right - 4, top + 4 + i, 8, 4, 4, 4);
      }

      for(i = 0; i < width - 8; i += 4) {
         screen.drawTexturedModalRect(left + 4 + i, bottom - 4, 4, 8, 4, 4);
      }

      for(i = 0; i < width - 8; i += 4) {
         for(int j = 0; j < height - 8; j += 4) {
            screen.drawTexturedModalRect(left + 4 + i, top + 4 + j, 4, 4, 4, 4);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static void drawDefaultGui(IDefaultGui gui, int width, int height, int xOffset, int yOffset) {
      GuiScreen screen = checkBoundState(gui);
      int left = screen.width / 2 - width / 2 + xOffset;
      int right = screen.width / 2 + width / 2 + xOffset;
      int top = screen.height / 2 - height / 2 + yOffset;
      int bottom = screen.height / 2 + height / 2 + yOffset;
      drawDefaultGui(gui, new int[]{left, right, top, bottom});
   }

   @SideOnly(Side.CLIENT)
   public static void drawDefaultSlotRow(IDefaultGui gui, int x, int y) {
      GuiScreen screen = checkBoundState(gui);
      screen.drawTexturedModalRect(screen.width / 2 - 81 + x, screen.height / 2 - 9 + y, 13, 0, 162, 18);
   }

   @SideOnly(Side.CLIENT)
   public static void drawDefaultSlot(IDefaultGui gui, int x, int y) {
      GuiScreen screen = checkBoundState(gui);
      screen.drawTexturedModalRect(screen.width / 2 - 9 + x, screen.height / 2 - 9 + y, 13, 0, 18, 18);
   }

   @SideOnly(Side.CLIENT)
   private static GuiScreen checkBoundState(IDefaultGui gui) {
      GuiScreen screen = gui.getGui();
      if (!gui.boundDefaultTexture()) {
         screen.mc.getTextureManager().bindTexture(defaultGui);
         gui.setBoundDefaultTexture();
      }

      return screen;
   }
}
