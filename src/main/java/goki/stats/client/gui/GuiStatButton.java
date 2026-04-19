/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.entity.player.EntityPlayer
 *  org.lwjgl.opengl.GL11
 */
package goki.stats.client.gui;

import goki.stats.client.gui.GuiStats;
import goki.stats.lib.Helper;
import goki.stats.lib.Reference;
import goki.stats.stats.Stat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class GuiStatButton
extends GuiButton {
    public Stat stat;
    public EntityPlayer player;

    public GuiStatButton(int id, int x, int y, int width, int height, Stat stat, EntityPlayer player) {
        super(id, x, y, width, height, "");
        this.stat = stat;
        this.player = player;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            int iconu = 0;
            int iconv = 24 * (this.stat.imageID % 10);
            int level = Helper.getPlayerStatLevel(this.player, this.stat);
            int cost = this.stat.getCost(level);
            int playerXP = Helper.getXPTotal(this.player);
            String message = level + "";
            int messageColor = 0xFFFFFF;
            FontRenderer fontrenderer = mc.fontRenderer;
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.field_146123_n = this.isUnderMouse(mouseX, mouseY);
            int which = this.getHoverState(this.field_146123_n);
            if (which == 2) {
                iconu = 24;
            }
            if (playerXP < cost) {
                iconu = 48;
            }
            if (level >= this.stat.getLimit()) {
                iconu = 72;
            }
            if (!this.stat.enabled) {
                iconu = 48;
                message = "X";
            }
            if (level >= this.stat.getLimit()) {
                message = "*" + level + "*";
                messageColor = 0xFFCC00;
            }
            iconu += this.stat.imageID % 20 / 10 * 24 * 4;
            if (this.stat.imageID >= 20) {
                mc.getTextureManager().bindTexture(Reference.RPG_ICON_2_TEXTURE_LOCATION);
            } else {
                mc.getTextureManager().bindTexture(Reference.RPG_ICON_TEXTURE_LOCATION);
            }
            GL11.glPushMatrix();
            GL11.glTranslatef((float)this.xPosition, (float)this.yPosition, (float)0.0f);
            GL11.glScalef((float)GuiStats.SCALE, (float)GuiStats.SCALE, (float)0.0f);
            this.drawTexturedModalRect(0, 0, iconu, iconv, this.width, this.height);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslatef((float)this.xPosition, (float)this.yPosition, (float)0.0f);
            this.drawCenteredString(fontrenderer, message, (int)((float)(this.width / 2) * GuiStats.SCALE), (int)((float)this.height * GuiStats.SCALE) + 2, messageColor);
            GL11.glPopMatrix();
        }
    }

    public boolean isUnderMouse(int mouseX, int mouseY) {
        return mouseX >= this.xPosition && mouseY >= this.yPosition && (float)mouseX < (float)this.xPosition + (float)this.width * GuiStats.SCALE && (float)mouseY < (float)this.yPosition + (float)this.height * GuiStats.SCALE;
    }

    public boolean mousePressed(Minecraft par1Minecraft, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && (float)mouseX < (float)this.xPosition + (float)this.width * GuiStats.SCALE && (float)mouseY < (float)this.yPosition + (float)this.height * GuiStats.SCALE;
    }

    public String getHoverMessage(int which) {
        if (which == 0) {
            return this.stat.name + " L" + Helper.getPlayerStatLevel(this.player, this.stat);
        }
        return this.stat.getAppliedDescriptionString(this.player);
    }
}

