/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.config.Configuration
 */
package de.sanandrew.core.manpack.mod;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationManager {
    private static final String CFG_VERSION = "1.1";
    private static Configuration config;
    public static boolean subscribeToUnstable;
    public static boolean enableUpdater;
    public static boolean enableWindowTitleMsg;
    private static final String DESC_SUBUNSTABLE = "If set to true, the update manager checks for alpha/beta/release candidate updates of mods.\nNote: installed mods within one of the before mentioned states\nreceive unstable updates regardless of this setting!";
    public static final String DESC_ENABLEUPDMGR = "This will enable (true) or disable (false) the update checker.";
    public static final String DESC_ENABLEWNDTITLE = "This will enable (true) or disable (false) the window title easter egg.";

    public static void load(File file) {
        config = new Configuration(file, CFG_VERSION);
        config.load();
        subscribeToUnstable = config.getBoolean("subscribeToUnstable", "updater", subscribeToUnstable, DESC_SUBUNSTABLE);
        enableUpdater = config.getBoolean("enableUpdater", "updater", enableUpdater, DESC_ENABLEUPDMGR);
        enableWindowTitleMsg = config.getBoolean("enableWindowTitleMsg", "general", enableWindowTitleMsg, DESC_ENABLEWNDTITLE);
        config.save();
    }

    static {
        subscribeToUnstable = false;
        enableUpdater = true;
        enableWindowTitleMsg = true;
    }
}

