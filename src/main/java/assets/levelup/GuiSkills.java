/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.internal.FMLProxyPacket
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.StatCollector
 */
package assets.levelup;

import assets.levelup.ClassBonus;
import assets.levelup.LevelUp;
import assets.levelup.PlayerExtendedProperties;
import assets.levelup.SkillPacketHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public final class GuiSkills
extends GuiScreen {
    private boolean closedWithButton;
    private static final int offset = 80;
    private final int[] skills = new int[ClassBonus.skillNames.length];
    private int[] skillsPrev = null;
    byte cl = (byte)-1;

    protected void actionPerformed(GuiButton guibutton) {
        int step = isShiftKeyDown() ? 2 : 1;
        if (guibutton.id == 0) {
            this.closedWithButton = true;
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else if (guibutton.id == 100) {
            this.closedWithButton = false;
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else if (guibutton.id < 21) {
            int availablePoints = this.getSkillOffset(this.skills.length - 1);
            int skillIndex = guibutton.id - 1;
            int addAmount = Math.min(step, Math.min(availablePoints, ClassBonus.getMaxSkillPoints() - this.getSkillOffset(skillIndex)));
            if (addAmount > 0) {
                int n = skillIndex;
                this.skills[n] = this.skills[n] + addAmount;
                int n2 = this.skills.length - 1;
                this.skills[n2] = this.skills[n2] - addAmount;
            }
        } else if (guibutton.id > 20 && this.skills[guibutton.id - 21] > 0) {
            int n = guibutton.id - 21;
            int removeAmount = Math.min(step, this.skills[n]);
            this.skills[n] = this.skills[n] - removeAmount;
            int n3 = this.skills.length - 1;
            this.skills[n3] = this.skills[n3] + removeAmount;
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawScreen(int i, int j, float f) {
        this.drawDefaultBackground();
        String s = "";
        String s1 = "";
        for (Object button : this.buttonList) {
            int l = ((GuiButton)button).id;
            if (l < 1 || l > 99) continue;
            if (l > 20) {
                l -= 20;
            }
            if (!((GuiButton)button).mousePressed(this.mc, i, j)) continue;
            s = StatCollector.translateToLocal((String)("skill" + l + ".tooltip1"));
            s1 = StatCollector.translateToLocal((String)("skill" + l + ".tooltip2"));
        }
        if (this.cl < 0) {
            this.cl = PlayerExtendedProperties.getPlayerClass((EntityPlayer)this.mc.thePlayer);
        }
        if (this.cl > 0) {
            this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocalFormatted((String)"hud.skill.text2", (Object[])new Object[]{StatCollector.translateToLocal((String)("class" + this.cl + ".name"))}), this.width / 2, 2, 0xFFFFFF);
        }
        for (int x = 0; x < 6; ++x) {
            this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal((String)("skill" + (x + 1) + ".name")) + ": " + this.getSkillOffset(x), this.width / 2 - 80, 20 + 32 * x, 0xFFFFFF);
            this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal((String)("skill" + (x + 7) + ".name")) + ": " + this.getSkillOffset(x + 6), this.width / 2 + 80, 20 + 32 * x, 0xFFFFFF);
        }
        this.drawCenteredString(this.fontRendererObj, s, this.width / 2, this.height / 6 + 168, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, s1, this.width / 2, this.height / 6 + 180, 0xFFFFFF);
        super.drawScreen(i, j, f);
    }

    public void initGui() {
        this.closedWithButton = false;
        this.buttonList.clear();
        this.updateSkillList();
        this.buttonList.add(new GuiButton(0, this.width / 2 + 96, this.height / 6 + 168, 96, 20, StatCollector.translateToLocal((String)"gui.done")));
        this.buttonList.add(new GuiButton(100, this.width / 2 - 192, this.height / 6 + 168, 96, 20, StatCollector.translateToLocal((String)"gui.cancel")));
        for (int index = 0; index < 6; ++index) {
            this.buttonList.add(new GuiButton(1 + index, this.width / 2 + 44 - 80, 15 + 32 * index, 20, 20, "+"));
            this.buttonList.add(new GuiButton(7 + index, this.width / 2 + 44 + 80, 15 + 32 * index, 20, 20, "+"));
            this.buttonList.add(new GuiButton(21 + index, this.width / 2 - 64 - 80, 15 + 32 * index, 20, 20, "-"));
            this.buttonList.add(new GuiButton(27 + index, this.width / 2 - 64 + 80, 15 + 32 * index, 20, 20, "-"));
        }
    }

    public void onGuiClosed() {
        if (this.closedWithButton && this.skills[this.skills.length - 1] != 0) {
            FMLProxyPacket packet = SkillPacketHandler.getPacket(Side.SERVER, 2, (byte)-1, this.skills);
            LevelUp.skillChannel.sendToServer(packet);
        }
    }

    private void updateSkillList() {
        if (this.skillsPrev == null) {
            this.skillsPrev = new int[this.skills.length];
            for (int i = 0; i < this.skills.length; ++i) {
                this.skillsPrev[i] = PlayerExtendedProperties.getSkillFromIndex((EntityPlayer)this.mc.thePlayer, i);
            }
        }
    }

    private int getSkillOffset(int i) {
        return this.skillsPrev[i] + this.skills[i];
    }
}
