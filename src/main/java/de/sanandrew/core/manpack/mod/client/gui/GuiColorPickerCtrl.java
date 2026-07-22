/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Floats
 *  org.apache.commons.lang3.ArrayUtils
 */
package de.sanandrew.core.manpack.mod.client.gui;

import com.google.common.primitives.Floats;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.client.helpers.GuiUtils;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.ArrayUtils;

@SideOnly(value=Side.CLIENT)
public class GuiColorPickerCtrl
extends Gui {
    private int hue = 360;
    private int sat = 100;
    private int bright = 100;
    private int resColorHued = -65536;
    private int resColor = -1;
    public int xPos = 0;
    public int yPos = 0;

    public GuiColorPickerCtrl(int posX, int posY) {
        this.xPos = posX;
        this.yPos = posY;
        this.calcResultColor();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseKey) {
        mouseX -= this.xPos;
        mouseY -= this.yPos;
        if (mouseKey == 0) {
            if (mouseX >= 105 && mouseX < 115 && mouseY >= 0 && mouseY < 90) {
                this.hue = 359 - mouseY * 4;
                this.calcResultColor();
            } else if (mouseX >= 0 && mouseX < 90 && mouseY >= 0 && mouseY < 90) {
                this.sat = mouseX * 100 / 90;
                this.bright = 100 - mouseY * 100 / 90;
                this.calcResultColor();
            }
        }
    }

    public int getOutputColor() {
        return this.resColor;
    }

    public void setHsb(int hue, int saturation, int brightness) {
        this.hue = hue;
        this.sat = saturation;
        this.bright = brightness;
        this.calcResultColor();
    }

    public int getHue() {
        return this.hue;
    }

    public int getSaturation() {
        return this.sat;
    }

    public int getBrightness() {
        return this.bright;
    }

    public void setHsbFromRgb(int rgb) {
        float min;
        float[] splitColors = ArrayUtils.remove((float[])SAPUtils.getRgbaFromColorInt(rgb).getColorFloatArray(), (int)3);
        float max = Floats.max((float[])splitColors);
        if (max == (min = Floats.min((float[])splitColors))) {
            this.hue = 0;
        } else if (max == splitColors[0]) {
            this.hue = Math.round(60.0f * ((splitColors[1] - splitColors[2]) / (max - min)));
        } else if (max == splitColors[1]) {
            this.hue = Math.round(60.0f * (2.0f + (splitColors[2] - splitColors[0]) / (max - min)));
        } else if (max == splitColors[2]) {
            this.hue = Math.round(60.0f * (4.0f + (splitColors[0] - splitColors[1]) / (max - min)));
        }
        if (this.hue < 0) {
            this.hue += 360;
        }
        this.sat = max <= 0.01f ? 0 : Math.round((max - min) / max * 100.0f);
        this.bright = Math.round(max * 100.0f);
        this.calcResultColor();
    }

    public void drawControl() {
        GuiUtils.drawGradientRect(this.xPos, this.yPos, 90 + this.xPos, 90 + this.yPos, -1, this.resColorHued, this.zLevel);
        this.drawGradientRect(this.xPos, this.yPos, 90 + this.xPos, 90 + this.yPos, 0, -16777216);
        int x1 = 105 + this.xPos;
        int x2 = 115 + this.xPos;
        this.drawGradientRect(x1, this.yPos, x2, 15 + this.yPos, -65536, -65281);
        this.drawGradientRect(x1, 15 + this.yPos, x2, 30 + this.yPos, -65281, -16776961);
        this.drawGradientRect(x1, 30 + this.yPos, x2, 45 + this.yPos, -16776961, -16711681);
        this.drawGradientRect(x1, 45 + this.yPos, x2, 60 + this.yPos, -16711681, -16711936);
        this.drawGradientRect(x1, 60 + this.yPos, x2, 75 + this.yPos, -16711936, -256);
        this.drawGradientRect(x1, 75 + this.yPos, x2, 90 + this.yPos, -256, -65536);
        GuiColorPickerCtrl.drawRect(x1, this.getHueForGUI() + this.yPos, x2, this.getHueForGUI() + this.yPos + 1, -16777216);
        GuiColorPickerCtrl.drawRect(this.getSatForGUI() + this.xPos - 4, this.getBrightForGUI() + this.yPos - 4, this.getSatForGUI() + this.xPos + 4, this.getBrightForGUI() + this.yPos + 4, -16777216);
        GuiColorPickerCtrl.drawRect(this.getSatForGUI() + this.xPos - 3, this.getBrightForGUI() + this.yPos - 3, this.getSatForGUI() + this.xPos + 3, this.getBrightForGUI() + this.yPos + 3, -1);
        GuiColorPickerCtrl.drawRect(this.getSatForGUI() + this.xPos - 2, this.getBrightForGUI() + this.yPos - 2, this.getSatForGUI() + this.xPos + 2, this.getBrightForGUI() + this.yPos + 2, this.resColor);
    }

    private int getHueForGUI() {
        return (359 - this.hue) / 4;
    }

    private int getSatForGUI() {
        return this.sat * 90 / 100;
    }

    private int getBrightForGUI() {
        return 90 - this.bright * 90 / 100;
    }

    private void calcResultColor() {
        int pickedColor = -16777216;
        int invHue = 360 - this.hue;
        if (invHue >= 0 && invHue < 60) {
            pickedColor = 0xFFFF0000 | (int)((float)(255 * invHue) / 60.0f);
        } else if (invHue >= 60 && invHue < 120) {
            pickedColor = 0xFF0000FF | (int)(255.0f - (float)(255 * (invHue - 60)) / 60.0f) << 16;
        } else if (invHue >= 120 && invHue < 180) {
            pickedColor = 0xFF0000FF | (int)((float)(255 * (invHue - 120)) / 60.0f) << 8;
        } else if (invHue >= 180 && invHue < 240) {
            pickedColor = 0xFF00FF00 | (int)(255.0f - (float)(255 * (invHue - 180)) / 60.0f);
        } else if (invHue >= 240 && invHue < 300) {
            pickedColor = 0xFF00FF00 | (int)((float)(255 * (invHue - 240)) / 15.0f) << 16;
        } else if (invHue >= 300 && invHue < 360) {
            pickedColor = 0xFFFF0000 | (int)(255.0f - (float)(255 * (invHue - 300)) / 60.0f) << 8;
        }
        float satFloat = 1.0f - (float)this.sat / 100.0f;
        int redPart = pickedColor >> 16 & 0xFF;
        int redSat = (int)((float)(255 - redPart) * satFloat);
        int greenPart = pickedColor >> 8 & 0xFF;
        int greenSat = (int)((float)(255 - greenPart) * satFloat);
        int bluePart = pickedColor & 0xFF;
        int blueSat = (int)((float)(255 - bluePart) * satFloat);
        int brightColor = (redPart += redSat) << 16 | (greenPart += greenSat) << 8 | (bluePart += blueSat);
        float brightFloat = (float)this.bright / 100.0f;
        this.resColor = brightColor = 0xFF000000 | (int)((float)(brightColor >> 16 & 0xFF) * brightFloat) << 16 | (int)((float)(brightColor >> 8 & 0xFF) * brightFloat) << 8 | (int)((float)(brightColor & 0xFF) * brightFloat);
        this.resColorHued = pickedColor;
    }
}

