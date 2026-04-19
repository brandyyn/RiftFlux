/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.entity.player.EntityPlayer
 */
package goki.stats.client.gui;

import goki.stats.lib.Helper;
import goki.stats.stats.Stat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;

public class GuiStatTooltip
extends Gui {
    private Stat stat;
    private EntityPlayer player;
    private Minecraft mc = Minecraft.getMinecraft();
    private int padding = 4;

    public GuiStatTooltip(Stat stat, EntityPlayer player) {
        this.stat = stat;
        this.player = player;
    }

    public void draw(int drawX, int drawY, int mouseButton) {
        int rightEdge;
        int right;
        int level = Helper.getPlayerStatLevel(this.player, this.stat);
        String header = this.stat.name + " L" + level;
        String message = this.stat.getAppliedDescriptionString(this.player);
        String cost = "Costs " + this.stat.getCost(level) + "xp";
        if (level >= this.stat.getLimit()) {
            cost = "Maxed!";
        }
        int width = Math.max(this.mc.fontRenderer.getStringWidth(message), this.mc.fontRenderer.getStringWidth(header)) + this.padding * 2;
        int height = this.mc.fontRenderer.FONT_HEIGHT * 3 + this.padding * 2;
        int h = height / 3;
        int x = drawX - width / 2;
        int y = drawY;
        int left = x;
        int leftEdge = 0;
        if (left < leftEdge) {
            x -= leftEdge - left + 1;
        }
        if ((right = x + width) > (rightEdge = this.mc.currentScreen.width)) {
            x += rightEdge - right - 1;
        }
        GuiStatTooltip.drawRect((int)x, (int)y, (int)(x + width), (int)(y - height), (int)-872415232);
        this.drawString(this.mc.fontRenderer, header, x + this.padding / 2, y - h * 3 + this.padding / 2, -13312);
        this.drawString(this.mc.fontRenderer, message, x + this.padding / 2, y - h * 2 + this.padding / 2, -1);
        this.drawString(this.mc.fontRenderer, cost, x + this.padding / 2, y - h + this.padding / 2, -16724737);
        this.drawBorder(x, y, width, height, -1);
    }

    private void drawBorder(int x, int y, int width, int height, int borderColor) {
        this.drawHorizontalLine(x - 1, x + width, y, borderColor);
        this.drawHorizontalLine(x - 1, x + width, y - height, borderColor);
        this.drawVerticalLine(x - 1, y, y - height, borderColor);
        this.drawVerticalLine(x + width, y, y - height, borderColor);
    }
}

