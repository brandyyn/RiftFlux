/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  org.lwjgl.util.vector.Vector2f
 */
package goki.stats.client.gui;

import goki.stats.GokiStats;
import goki.stats.client.gui.GuiStatButton;
import goki.stats.client.gui.GuiStatTooltip;
import goki.stats.handlers.PacketStatAlter;
import goki.stats.lib.Helper;
import goki.stats.stats.Stat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.util.vector.Vector2f;

public class GuiStats
extends GuiScreen {
    private EntityPlayer player = null;
    public static final int STATUS_BUTTON_WIDTH = 24;
    public static final int STATUS_BUTTON_HEIGHT = 24;
    public static float SCALE = 1.0f;
    private static final int HORIZONTAL_SPACING = 8;
    private static final int VERTICAL_SPACING = 12;
    public static final int IMAGE_ROWS = 10;
    private static final int[] COLUMNS = new int[]{4, 3, 5, 3, 5};
    private int currentColumn = 0;
    private int currentRow = 0;
    private GuiStatTooltip toolTip = null;
    private FontRenderer fontRenderer;

    public GuiStats(EntityPlayer player) {
        this.player = player;
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
    }

    public void drawScreen(int mouseX, int mouseY, float par3) {
        int ttx = 0;
        int tty = 0;
        this.toolTip = null;
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, par3);
        for (int i = 0; i < this.buttonList.size(); ++i) {
            GuiStatButton button;
            if (!(this.buttonList.get(i) instanceof GuiStatButton) || !(button = (GuiStatButton)((Object)this.buttonList.get(i))).isUnderMouse(mouseX, mouseY)) continue;
            this.toolTip = new GuiStatTooltip(Stat.stats.get(i), this.player);
            ttx = button.xPosition + 12;
            tty = button.yPosition - 1;
            break;
        }
        this.drawCenteredString(this.fontRenderer, "Current XP: " + Helper.getXPTotal(this.player.experienceLevel, this.player.experience) + "xp", this.width / 2, this.height - 16, -1);
        if (this.toolTip != null) {
            this.toolTip.draw(ttx, tty, 0);
        }
    }

    public void initGui() {
        for (int stat = 0; stat < Stat.totalStats; ++stat) {
            Vector2f pos = this.getButtonPosition(stat);
            this.buttonList.add(new GuiStatButton(stat, (int)pos.x, (int)pos.y, 24, 24, Stat.stats.get(stat), this.player));
            ++this.currentColumn;
            if (this.currentColumn >= COLUMNS[this.currentRow]) {
                ++this.currentRow;
                this.currentColumn = 0;
            }
            if (this.currentRow < COLUMNS.length) continue;
            this.currentRow = COLUMNS.length - 1;
        }
    }

    private Vector2f getButtonPosition(int n) {
        Vector2f vec = new Vector2f();
        int columns = COLUMNS[this.currentRow];
        int x = n % columns;
        int y = this.currentRow;
        int rows = COLUMNS.length;
        int amount = columns;
        float width = (float)(amount * 32) * SCALE;
        float height = (float)(rows * 36) * SCALE;
        vec.x = width / (float)amount * (float)x + ((float)this.width - width + 8.0f) / 2.0f;
        vec.y = height / (float)rows * (float)y + ((float)this.height - height + 12.0f) / 2.0f;
        return vec;
    }

    protected void actionPerformed(GuiButton button) {
        if (button.id >= 0 && button.id <= Stat.totalStats && button instanceof GuiStatButton) {
            GuiStatButton statButton = (GuiStatButton)button;
            if (!GuiScreen.isCtrlKeyDown()) {
                GokiStats.packetPipeline.sendToServer(new PacketStatAlter(Stat.stats.indexOf(statButton.stat), 1));
            } else {
                GokiStats.packetPipeline.sendToServer(new PacketStatAlter(Stat.stats.indexOf(statButton.stat), -1));
            }
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}

