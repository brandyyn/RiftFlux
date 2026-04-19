/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 */
package goki.stats.client.gui;

import goki.stats.ToolSpecificStat;
import goki.stats.client.gui.GuiExtendedButton;
import goki.stats.lib.Reference;
import goki.stats.stats.Stat;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class GuiCompatibilityHelper
extends GuiScreen {
    private EntityPlayer player = null;
    private final int BUTTON_WIDTH = 240;
    private final int BUTTON_HEIGHT = 15;
    private final ToolSpecificStat[] compatibleStats = new ToolSpecificStat[]{Stat.STAT_MINING, Stat.STAT_DIGGING, Stat.STAT_CHOPPING, Stat.STAT_TRIMMING, Stat.STAT_SWORDSMANSHIP, Stat.STAT_BOWMANSHIP};

    public GuiCompatibilityHelper(EntityPlayer player) {
        this.player = player;
    }

    public void initGui() {
        int button = 0;
        int x = this.width / 2;
        int y = this.height / 2 - 45;
        this.buttonList.add(new GuiExtendedButton(button, x - 120, y - 15 + 15 * button++, 240, 15, "Add item to Mining list", 0x333333));
        this.buttonList.add(new GuiExtendedButton(button, x - 120, y - 15 + 15 * button++, 240, 15, "Add item to Digging list", 0x333333));
        this.buttonList.add(new GuiExtendedButton(button, x - 120, y - 15 + 15 * button++, 240, 15, "Add item to Chopping list", 0x333333));
        this.buttonList.add(new GuiExtendedButton(button, x - 120, y - 15 + 15 * button++, 240, 15, "Add item to Trimming list", 0x333333));
        this.buttonList.add(new GuiExtendedButton(button, x - 120, y - 15 + 15 * button++, 240, 15, "Add item to Swordsmanship list", 0x333333));
        this.buttonList.add(new GuiExtendedButton(button, x - 120, y - 15 + 15 * button++, 240, 15, "Add item to Bowmanship list", 0x333333));
        this.checkStatus();
    }

    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.mc.fontRenderer, "This is CLIENT-SIDE. Copy the config to the server and /reloadGokiStats.", this.width / 2, 16, 0xFFFFFF);
    }

    protected void actionPerformed(GuiButton button) {
        if (this.compatibleStats[button.id].isAffectedByStat(this.player.getHeldItem()) != 0) {
            this.compatibleStats[button.id].removeSupportForItem(this.player.getHeldItem());
        } else {
            this.compatibleStats[button.id].addSupportForItem(this.player.getHeldItem());
        }
        Reference.configuration.save();
        this.checkStatus();
    }

    public void checkStatus() {
        Reference.configuration.load();
        for (int i = 0; i < this.compatibleStats.length; ++i) {
            if (this.compatibleStats[i].isAffectedByStat(this.player.getHeldItem()) != 0) {
                ((GuiExtendedButton)((Object)this.buttonList.get((int)i))).displayString = "Remove item from " + this.compatibleStats[i].name + " list.";
                ((GuiExtendedButton)((Object)this.buttonList.get(i))).setBackgroundColor(0x339933);
                continue;
            }
            ((GuiExtendedButton)((Object)this.buttonList.get((int)i))).displayString = "Add item to " + this.compatibleStats[i].name + " list.";
            ((GuiExtendedButton)((Object)this.buttonList.get(i))).setBackgroundColor(0x993333);
        }
    }

    public void onGuiClosed() {
        Reference.configuration.save();
    }
}

