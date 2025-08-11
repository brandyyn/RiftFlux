package com.excsi.riftfixes;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ModConfig {

    public static Configuration config;

    public static boolean enableChromatiCraftMixin;

    public static boolean hasSound;

    public static boolean hasShader;

    public static boolean disableStrataVents;

    public static boolean disableStrataOreVeins;

    public static boolean enableArmorMixin;

    public static boolean changeArmorBarAmount;

    public static float protectionMultiplier;

    public static boolean enableChestLaunch;
    public static float chestLaunchHorizontal;
    public static float chestLaunchUpward;

    public static boolean enableFullExplosionDrops;

    public static boolean enableMeleeDamageTooltip;

    public static boolean reworkVillageGolems;
    public static int initialVillageGolems;

    public static boolean disableSleepRainClear;


    public static void init(File file){
        config = new Configuration(file);
        syncConfig();
    }

    public static void syncConfig(){
        enableChromatiCraftMixin = config.getBoolean("ChromatiCraftMixin","general",true,"Toggles the progression effects");
        hasSound = config.getBoolean("SoundEffect","general",false,"Toggles progression's sound effects");
        hasShader= config.getBoolean("ShaderEffect","general",false,"Toggles progression's shader effects");
        disableStrataVents = config.getBoolean("DisableStrataVents","general",false,"Toggles GeoStrata's vent spawn");
        disableStrataOreVeins = config.getBoolean("DisableStrataOreVeins","general",false,"Toggles GeoStrata's ore vein spawn");
        enableArmorMixin = config.getBoolean("EnableArmorMixin","general",true,"Enable mixin scaling armor protections");
        changeArmorBarAmount = config.getBoolean("ChangeArmorBarAmount","general",true,
                "Enable mixin that changes armor bar displayed amount, may affect other things");
        protectionMultiplier = config.getFloat("protectionMultiplier","general",0.5F,0.0F,1F,"Scales armor's protection(1.0 is unchanged armor)");

        enableChestLaunch = config.getBoolean("EnableChestLaunch","general", true,
                "Launch entities standing on top when a chest opens.");
        chestLaunchHorizontal = (float) config.get("general", "ChestLaunchHorizontal", 5,
                "Horizontal push strength when a chest opens. (no bounds)").getDouble(5);
        chestLaunchUpward = (float) config.get("general", "ChestLaunchUpward", 5,
                "Upward boost when a chest opens. (no bounds)").getDouble(5);

        enableFullExplosionDrops = config.getBoolean(
                "EnableFullExplosionDrops", "general", true,
                "If true, ALL explosions drop 100% of affected blocks.");

        enableMeleeDamageTooltip = config.getBoolean(
                "EnableMeleeDamageTooltip", "general", true,
                "Replace '+X Attack Damage' with a single gray 'X.X Melee Damage' line (includes +1 base and Sharpness)."
        );


        reworkVillageGolems = config.getBoolean(
                "ReworkVillageGolems", "general", true,
                "Stops villager-based iron golem farms; spawns a fixed number at village worldgen."
        );
        initialVillageGolems = config.getInt(
                "InitialVillageGolems", "general", 5, 0, 64,
                "Iron golems to spawn once per village at worldgen."
        );

        disableSleepRainClear = config.getBoolean(
                "DisableSleepRainClear", "general", true,
                "If true, sleeping no longer clears rain or thunder."
        );

        config.save();
    }
}