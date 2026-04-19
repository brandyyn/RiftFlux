/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.config.Configuration
 */
package zairus.worldexplorer.core;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class WEConfig {
    public static Configuration config;
    public static int KEY_EXPLORERGUI;

    public static void init(File file) {
        config = new Configuration(file);
        config.load();
        KEY_EXPLORERGUI = config.getInt("KEY_EXPLORERGUI", "KEY_BINDINGS", KEY_EXPLORERGUI, 0, 255, "Key binding to open the Explorer's GUI");
        config.save();
    }

    static {
        KEY_EXPLORERGUI = 45;
    }
}

