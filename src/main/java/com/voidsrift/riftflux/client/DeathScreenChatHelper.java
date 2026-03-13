package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiGameOver;
import com.voidsrift.riftflux.vortex.client.gui.GuiRuneGameOver;

public final class DeathScreenChatHelper {
    private DeathScreenChatHelper() {
    }

    public static boolean tryOpenDeathScreenChat(int keyCode) {
        if (!ModConfig.allowChatOnDeathScreen) {
            return false;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.gameSettings == null) {
            return false;
        }
        GuiScreen current = mc.currentScreen;
        if (!(current instanceof GuiGameOver) && !(current instanceof GuiRuneGameOver)) {
            return false;
        }

        if (keyCode == mc.gameSettings.keyBindChat.getKeyCode()) {
            mc.displayGuiScreen(new GuiDeathOverlayChat(current));
            return true;
        }

        if (keyCode == mc.gameSettings.keyBindCommand.getKeyCode()) {
            mc.displayGuiScreen(new GuiDeathOverlayChat(current, "/"));
            return true;
        }

        return false;
    }
}
