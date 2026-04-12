/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.internal.FMLProxyPacket
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.util.StatCollector
 */
package assets.levelup;

import assets.levelup.LevelUp;
import assets.levelup.SkillPacketHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

public final class GuiClasses
extends GuiScreen {
    private boolean closedWithButton = false;
    private byte cl = 0;

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawScreen(int i, int j, float f) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal((String)("class" + this.cl + ".tooltip")), this.width / 2, this.height / 6 + 148, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocalFormatted((String)"gui.class.title", (Object[])new Object[]{StatCollector.translateToLocal((String)("class" + this.cl + ".name"))}), this.width / 2, this.height / 6 + 174, 0xFFFFFF);
        super.drawScreen(i, j, f);
    }

    public void initGui() {
        this.closedWithButton = false;
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 + 96, this.height / 6 + 168, 96, 20, StatCollector.translateToLocal((String)"gui.done")));
        this.buttonList.add(new GuiButton(100, this.width / 2 - 192, this.height / 6 + 168, 96, 20, StatCollector.translateToLocal((String)"gui.cancel")));
        for (int j = 1; j < 13; j += 3) {
            for (int i = 0; i < 3; ++i) {
                this.buttonList.add(new GuiButton(i + j, this.width / 2 - 160 + i * 112, 18 + 32 * (j - 1) / 3, 96, 20, StatCollector.translateToLocal((String)("class" + (i + j) + ".name"))));
            }
        }
        this.buttonList.add(new GuiButton(13, this.width / 2 - 48, 146, 96, 20, StatCollector.translateToLocal((String)"class13.name")));
    }

    public void onGuiClosed() {
        if (this.closedWithButton && this.cl != 0) {
            FMLProxyPacket packet = SkillPacketHandler.getPacket(Side.SERVER, 1, this.cl, new int[0]);
            LevelUp.classChannel.sendToServer(packet);
        }
    }

    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) {
            this.closedWithButton = true;
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else if (guibutton.id == 100) {
            this.closedWithButton = false;
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else {
            this.cl = (byte)guibutton.id;
        }
    }
}

