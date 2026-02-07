package com.voidsrift.riftflux.vortex.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import com.voidsrift.riftflux.vortex.lib.helper.RenderHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ButtonToolbeltRadial {
   private final int itemoffset = 7;
   public int id;
   private double x;
   private double y;
   private int division;
   private int position;
   private int sides;
   private int iradius;
   private int oradius;
   private Polygon ringportion;
   public ItemStack itemstack;
   private int itemx;
   private int itemy;
   private int itemradius;
   private Rectangle itemportion;

   public ButtonToolbeltRadial(int id, int x, int y, int iradius, int oradius, int sides, int division, int position, ItemStack itemstack, int itemradius) {
      this.id = id;
      this.x = (double)x;
      this.y = (double)y;
      this.division = division;
      this.position = position;
      this.sides = sides;
      this.iradius = iradius;
      this.oradius = oradius;
      this.itemstack = itemstack;
      this.itemradius = itemradius;
      this.createButton();
   }

   private void createButton() {
      List<Integer> posX = new ArrayList();
      List<Integer> posY = new ArrayList();
      List<Integer> outerposX = new ArrayList();
      List<Integer> outerposY = new ArrayList();

      for(int i = 0; i <= this.sides; ++i) {
         double angle = 6.283185307179586D / (double)this.division * (double)i / (double)this.sides + Math.toRadians((double)(180 + (180 - 360 / this.division) / 2 - 360 / this.division * this.position));
         posX.add((int)(this.x - Math.cos(angle) * (double)this.iradius));
         outerposX.add((int)(this.x - Math.cos(angle) * (double)this.oradius));
         posY.add((int)(this.y + Math.sin(angle) * (double)this.iradius));
         outerposY.add((int)(this.y + Math.sin(angle) * (double)this.oradius));
      }

      Collections.reverse(outerposX);
      Collections.reverse(outerposY);
      posX.addAll(outerposX);
      posY.addAll(outerposY);
      int[] X = new int[posX.size()];
      int[] Y = new int[posY.size()];

      for(int i = 0; i < posX.size(); ++i) {
         X[i] = (Integer)posX.get(i);
         Y[i] = (Integer)posY.get(i);
      }

      this.ringportion = new Polygon(X, Y, posX.size());
      double itemangle = (double)(this.position * (360 / this.division));
      this.itemx = (int)(this.x - 7.0D + (double)this.itemradius * Math.sin(Math.toRadians(itemangle)));
      this.itemy = (int)(this.y - 7.0D - (double)this.itemradius * Math.cos(Math.toRadians(itemangle)));
      this.itemportion = new Rectangle(this.itemx, this.itemy, 16, 16);
   }

   public void drawButton(Minecraft mc, RenderItem ri, Tessellator tessellator, float red, float green, float blue, float alpha) {
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(red, green, blue, alpha);
      tessellator.startDrawing(5);

      for(int i = 0; i <= this.sides; ++i) {
         double angle = 6.283185307179586D / (double)this.division * (double)i / (double)this.sides + Math.toRadians((double)(180 + (180 - 360 / this.division) / 2 - 360 / this.division * this.position));
         tessellator.addVertex(this.x - Math.cos(angle) * (double)this.iradius, this.y + Math.sin(angle) * (double)this.iradius, 0.0D);
         tessellator.addVertex(this.x - Math.cos(angle) * (double)this.oradius, this.y + Math.sin(angle) * (double)this.oradius, 0.0D);
      }

      tessellator.draw();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.drawItemStack(mc, ri, this.itemstack, this.itemx, this.itemy);
   }

   public boolean isHovered(int mX, int mY) {
      return this.ringportion != null && this.ringportion.contains(mX, mY);
   }

   public boolean isItemHovered(int mX, int mY) {
      return this.itemportion != null && this.itemportion.contains(mX, mY);
   }
}
