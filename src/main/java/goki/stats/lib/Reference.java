/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.common.config.Configuration
 */
package goki.stats.lib;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class Reference {
    public static Configuration configuration;
    public static final String MOD_ID = "gokiStats";
    public static final String MOD_NAME = "gokiStats";
    public static final String VERSION = "1.0.0";
    public static final String CONFIGURATION_VERSION = "v1";
    public static final ResourceLocation RPG_ICON_TEXTURE_LOCATION;
    public static final ResourceLocation RPG_ICON_2_TEXTURE_LOCATION;
    public static final ResourceLocation PARTICLES_TEXTURE;
    public static final String STAT_TAG = "gokiStats_Stats";
    public static boolean isPlayerAPILoaded;

    static {
        RPG_ICON_TEXTURE_LOCATION = new ResourceLocation("gokiStats".toLowerCase(), "textures/rpg_icons.png");
        RPG_ICON_2_TEXTURE_LOCATION = new ResourceLocation("gokiStats".toLowerCase(), "textures/rpg_icons_2.png");
        PARTICLES_TEXTURE = new ResourceLocation("gokiStats".toLowerCase(), "textures/particles.png");
        isPlayerAPILoaded = false;
    }
}

