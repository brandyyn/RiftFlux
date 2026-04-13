package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;

public final class PauseScreenChatHelper {
    private PauseScreenChatHelper() {
    }

    public static boolean tryOpenPauseScreenChat(int keyCode) {
        if (!ModConfig.allowChatOnPauseScreen) {
            return false;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.gameSettings == null) {
            return false;
        }
        GuiScreen current = mc.currentScreen;
        if (!(current instanceof GuiIngameMenu)) {
            return false;
        }

        if (keyCode == mc.gameSettings.keyBindChat.getKeyCode()) {
            mc.displayGuiScreen(new GuiPauseOverlayChat(current));
            return true;
        }

        if (keyCode == mc.gameSettings.keyBindCommand.getKeyCode()) {
            mc.displayGuiScreen(new GuiPauseOverlayChat(current, "/"));
            return true;
        }

        return false;
    }
}
