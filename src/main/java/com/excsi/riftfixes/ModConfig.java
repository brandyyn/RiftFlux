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

    public static boolean enableBedChill;

    // Item pickup star (client)
    public static boolean enableItemPickupStar;
    public static boolean itemPickupStarOnStackIncrease; // highlight simple count increases too?

    public static boolean enablePickupNotifier;
    public static int pickupNotifyDurationSeconds;       // e.g. 3
    public static int pickupNotifyMergeWindowSeconds;    // e.g. 1
    public static float pickupNotifyFadeSeconds;         // e.g. 0.6f
    public static int pickupNotifyMaxEntries; // e.g. 50

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

        enableBedChill = config.getBoolean(
                "EnableBedChill", "general", true,
                "Lie in beds any time; ignore monsters & proximity; no night skip; no fade."
        );

        enableItemPickupStar = config.getBoolean(
                "EnableItemPickupStar", "general", true,
                "Show a small star on items you just picked up in your inventory until hovered."
        );

        itemPickupStarOnStackIncrease = config.getBoolean(
                "ItemPickupStarOnStackIncrease", "general", true,
                "Also show the star when a stack increases (same item, higher count). If false, only new/different stacks are highlighted."
        );
        enablePickupNotifier = config.getBoolean(
                "EnablePickupNotifier", "general", true,
                "Show a bottom-right popup (icon + name + amount) when you pick up items."
        );
        pickupNotifyDurationSeconds = config.getInt(
                "PickupNotifierDurationSeconds", "general", 3, 1, 20,
                "How long (in seconds) each popup stays on screen."
        );
        pickupNotifyMergeWindowSeconds = config.getInt(
                "PickupNotifierMergeWindowSeconds", "general", 1, 0, 10,
                "If you pick up the same item again within this window, merge into one entry."
        );
        pickupNotifyFadeSeconds = config.getFloat(
                "PickupNotifierFadeSeconds", "general", 0.6f, 0.05f, 5f,
                "Seconds to fade out the pickup notification (time-based, not FPS-based)."
        );
        pickupNotifyMaxEntries = config.getInt(
                "PickupNotifierMaxEntries", "general", 50, 1, 200,
                "Max number of pickup notifications kept on-screen at once."
        );



        config.save();
    }
}