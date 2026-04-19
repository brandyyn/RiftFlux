/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiButton
 *  org.lwjgl.opengl.GL11
 */
package goki.stats.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiExtendedButton
extends GuiButton {
    private int backgroundColor;
    private int borderColor = -16777216;
    private boolean pressed = false;

    public GuiExtendedButton(int id, int x, int y, int width, int height, String text, int color) {
        super(id, x, y, width, height, text);
        this.backgroundColor = color;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.enabled) {
            if (!this.isUnderMouse(mouseX, mouseY)) {
                this.drawIdle(mc, mouseX, mouseY);
            } else if (this.pressed) {
                this.drawDown(mc, mouseX, mouseY);
            } else {
                this.drawHover(mc, mouseX, mouseY);
            }
        } else {
            this.drawDisabled(mc, mouseX, mouseY);
        }
    }

    public boolean isUnderMouse(int mouseX, int mouseY) {
        return mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    private void drawBorder() {
        this.drawHorizontalLine(-1, this.width, 0, this.borderColor);
        this.drawHorizontalLine(-1, this.width, this.height, this.borderColor);
        this.drawVerticalLine(-1, 0, this.height, this.borderColor);
        this.drawVerticalLine(this.width, 0, this.height, this.borderColor);
    }

    private void drawDisabled(Minecraft mc, int mouseX, int mouseY) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)this.xPosition, (float)this.yPosition, (float)0.0f);
        Gui.drawRect((int)0, (int)0, (int)this.width, (int)this.height, (int)-2011028958);
        this.drawCenteredString(mc.fontRenderer, this.displayString, this.width / 2, this.height / 2 - mc.fontRenderer.FONT_HEIGHT / 2 + 1, 0x333333);
        this.drawBorder();
        GL11.glPopMatrix();
    }

    private void drawIdle(Minecraft mc, int mouseX, int mouseY) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)this.xPosition, (float)this.yPosition, (float)0.0f);
        Gui.drawRect((int)0, (int)0, (int)this.width, (int)this.height, (int)(this.backgroundColor + -2013265920));
        this.drawCenteredString(mc.fontRenderer, this.displayString, this.width / 2, this.height / 2 - mc.fontRenderer.FONT_HEIGHT / 2 + 1, 0xFFFFFF);
        this.drawBorder();
        GL11.glPopMatrix();
    }

    private void drawHover(Minecraft mc, int mouseX, int mouseY) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)this.xPosition, (float)this.yPosition, (float)0.0f);
        Gui.drawRect((int)0, (int)0, (int)this.width, (int)this.height, (int)(this.backgroundColor + -16777216));
        this.drawCenteredString(mc.fontRenderer, this.displayString, this.width / 2, this.height / 2 - mc.fontRenderer.FONT_HEIGHT / 2 + 1, 0xFFCC00);
        this.drawBorder();
        GL11.glPopMatrix();
    }

    private void drawDown(Minecraft mc, int mouseX, int mouseY) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)this.xPosition, (float)this.yPosition, (float)0.0f);
        Gui.drawRect((int)0, (int)0, (int)this.width, (int)this.height, (int)-16777216);
        this.drawCenteredString(mc.fontRenderer, this.displayString, this.width / 2, this.height / 2 - mc.fontRenderer.FONT_HEIGHT / 2 + 1, 0xFFCC00);
        this.drawBorder();
        GL11.glPopMatrix();
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public void onPressed() {
        this.pressed = true;
    }

    public void onReleased() {
        this.pressed = false;
    }

    public boolean isPressed() {
        return this.pressed;
    }
}

