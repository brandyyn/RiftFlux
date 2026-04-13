package com.voidsrift.riftflux.client;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

public class GuiPauseOverlayChat extends GuiChat {
    private static final int RETURN_DELAY_TICKS_AFTER_SEND = 3;
    private final GuiScreen backgroundScreen;
    private int returnDelayTicks = -1;

    public GuiPauseOverlayChat(GuiScreen backgroundScreen) {
        this(backgroundScreen, "");
    }

    public GuiPauseOverlayChat(GuiScreen backgroundScreen, String defaultText) {
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
        if (returnDelayTicks >= 0) {
            if (returnDelayTicks == 0) {
                returnToBackground();
            } else {
                returnDelayTicks--;
            }
        }
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
                this.inputField.setText("");
                this.returnDelayTicks = RETURN_DELAY_TICKS_AFTER_SEND;
                return;
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

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void returnToBackground() {
        if (this.mc == null) {
            return;
        }
        if (backgroundScreen != null && this.mc.theWorld != null) {
            this.mc.displayGuiScreen(backgroundScreen);
        } else {
            this.mc.displayGuiScreen(null);
        }
    }
}
