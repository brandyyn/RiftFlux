package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.mixin.accessor.GuiScreenAccessor;
import com.voidsrift.riftflux.vortex.client.gui.GuiRuneGameOver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiDeathOverlayChat extends GuiChat {
    private final GuiScreen backgroundScreen;
    private final String titleButtonLabel;

    public GuiDeathOverlayChat(GuiScreen backgroundScreen) {
        this(backgroundScreen, "");
    }

    public GuiDeathOverlayChat(GuiScreen backgroundScreen, String defaultText) {
        super(defaultText);
        this.backgroundScreen = backgroundScreen;
        this.titleButtonLabel = findTitleButtonLabel(backgroundScreen);
    }

    public GuiScreen getBackgroundScreen() {
        return backgroundScreen;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (backgroundScreen != null) {
            backgroundScreen.width = this.width;
            backgroundScreen.height = this.height;
            repairBackgroundDeathButtonLabels();
        }
    }

    @Override
    public void updateScreen() {
        if (backgroundScreen != null) {
            backgroundScreen.updateScreen();
            repairBackgroundDeathButtonLabels();
        }
        super.updateScreen();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            returnToBackground();
            return;
        }
        if (keyCode == 28 || keyCode == 156) {
            String text = this.inputField.getText().trim();
            if (text.length() > 0) {
                this.func_146403_a(text);
            }
            returnToBackground();
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (backgroundScreen != null) {
            repairBackgroundDeathButtonLabels();
            drawDeathBackground(mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void returnToBackground() {
        if (this.mc == null) {
            return;
        }
        if (backgroundScreen != null && this.mc.thePlayer != null && !this.mc.thePlayer.isEntityAlive()) {
            this.onGuiClosed();
            repairBackgroundDeathButtonLabels();
            this.mc.currentScreen = backgroundScreen;
        } else {
            this.mc.displayGuiScreen(null);
        }
    }

    private void repairBackgroundDeathButtonLabels() {
        repairDeathButtonLabels(backgroundScreen, titleButtonLabel);
    }

    private void drawDeathBackground(int mouseX, int mouseY) {
        if (!isDeathScreen(backgroundScreen) || this.mc == null) {
            return;
        }

        this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        String titleKey = isHardcoreDeathScreen() ? "deathScreen.title.hardcore" : "deathScreen.title";
        this.drawCenteredString(this.fontRendererObj, I18n.format(titleKey, new Object[0]), this.width / 2 / 2, 30, 0xFFFFFF);
        GL11.glPopMatrix();

        if (backgroundScreen instanceof GuiRuneGameOver) {
            this.drawCenteredString(this.fontRendererObj, I18n.format("deathScreen.hardcoreInfo", new Object[0]), this.width / 2, 144, 0xFFFFFF);
            this.drawCenteredString(this.fontRendererObj, I18n.format("gui.runegameover.blurb1", new Object[0]), this.width / 2, this.height / 4 + 124, 0xFFFFFF);
        }
        if (this.mc.thePlayer != null) {
            this.drawCenteredString(
                    this.fontRendererObj,
                    I18n.format("deathScreen.score", new Object[0]) + ": " + EnumChatFormatting.YELLOW + this.mc.thePlayer.getScore(),
                    this.width / 2,
                    100,
                    0xFFFFFF
            );
        }

        List buttonList = ((GuiScreenAccessor) backgroundScreen).getButtonList();
        if (buttonList == null) {
            return;
        }

        for (Object obj : buttonList) {
            if (!(obj instanceof GuiButton)) {
                continue;
            }
            ((GuiButton) obj).drawButton(this.mc, mouseX, mouseY);
        }
    }

    private boolean isHardcoreDeathScreen() {
        return backgroundScreen instanceof GuiRuneGameOver
                || this.mc != null
                && this.mc.theWorld != null
                && this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled();
    }

    private static String findTitleButtonLabel(GuiScreen screen) {
        if (isDeathScreen(screen)) {
            List buttonList = ((GuiScreenAccessor) screen).getButtonList();
            if (buttonList != null) {
                for (Object obj : buttonList) {
                    if (!(obj instanceof GuiButton)) {
                        continue;
                    }
                    GuiButton button = (GuiButton) obj;
                    if (button.id == 1 && button.displayString != null && button.displayString.length() > 0) {
                        return button.displayString;
                    }
                }
            }
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        boolean integrated = minecraft != null && minecraft.isIntegratedServerRunning();
        return integrated
                ? translateOrDefault("deathScreen.titleScreen", "Title screen")
                : translateOrDefault("deathScreen.leaveServer", "Leave server");
    }

    private static boolean isDeathScreen(GuiScreen screen) {
        return screen instanceof GuiGameOver || screen instanceof GuiRuneGameOver;
    }

    private static void repairDeathButtonLabels(GuiScreen screen, String titleLabel) {
        if (!isDeathScreen(screen)) {
            return;
        }

        List buttonList = ((GuiScreenAccessor) screen).getButtonList();
        if (buttonList == null) {
            return;
        }

        for (Object obj : buttonList) {
            if (!(obj instanceof GuiButton)) {
                continue;
            }
            GuiButton button = (GuiButton) obj;
            if (button.id == 1) {
                button.displayString = titleLabel;
            }
        }
    }

    private static String translateOrDefault(String key, String fallback) {
        String translated = StatCollector.translateToLocal(key);
        if (translated == null || translated.isEmpty() || key.equals(translated)) {
            return fallback;
        }
        return translated;
    }
}
