package com.voidsrift.riftflux.compat.hats;

import cpw.mods.fml.client.registry.ClientRegistry;
import hats.common.Hats;
import ichun.client.keybind.KeyBind;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public final class HatsKeybinds {

    private static final String CATEGORY = "Hats";
    private static final String DESCRIPTION = "Open Hats GUI";
    private static boolean registered = false;
    private static KeyBinding openGui;

    private HatsKeybinds() {
    }

    public static void ensureRegistered() {
        if (registered) {
            return;
        }
        int keyCode = Keyboard.KEY_H;
        try {
            if (Hats.config != null) {
                KeyBind keyBind = Hats.config.getKeyBind("guiKeyBind");
                if (keyBind != null) {
                    keyCode = keyBind.keyIndex;
                }
            }
        } catch (Throwable ignored) {
            // If Hats isn't fully initialized, fall back to the default.
        }
        openGui = new KeyBinding(DESCRIPTION, keyCode, CATEGORY);
        ClientRegistry.registerKeyBinding(openGui);
        registered = true;
    }

    public static boolean isOpenGuiPressed() {
        return registered && openGui != null && openGui.isPressed();
    }
}
