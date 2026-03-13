package com.voidsrift.riftflux.client;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

public class GuiDeathOverlayChat extends GuiChat {
    private final GuiScreen backgroundScreen;

    public GuiDeathOverlayChat(GuiScreen backgroundScreen) {
        this(backgroundScreen, "");
    }

    public GuiDeathOverlayChat(GuiScreen backgroundScreen, String defaultText) {
        super(defaultText);
        this.backgroundScreen = backgroundScreen;
    }

    public GuiScreen getBackgroundScreen() {
        return backgroundScreen;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (backgroundScreen != null && mc != null) {
            backgroundScreen.setWorldAndResolution(mc, this.width, this.height);
        }
    }

    @Override
    public void updateScreen() {
        if (backgroundScreen != null) {
            backgroundScreen.updateScreen();
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
            backgroundScreen.drawScreen(mouseX, mouseY, partialTicks);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void returnToBackground() {
        if (this.mc == null) {
            return;
        }
        if (backgroundScreen != null && this.mc.thePlayer != null && !this.mc.thePlayer.isEntityAlive()) {
            this.mc.displayGuiScreen(backgroundScreen);
        } else {
            this.mc.displayGuiScreen(null);
        }
    }
}
