package com.voidsrift.riftflux;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.io.File;
import makamys.mclib.config.item.BackpackConfigHelper;
import com.voidsrift.riftflux.dualhotbar.DualHotbarConfig;

public class ModConfig {

    public static Configuration config;

    public static boolean enableChromatiCraftMixin;

    public static boolean DisableAether2Portal;

    public static boolean hasSound;


    public static boolean playerOnlyHurtSound;
    public static boolean hasShader;

    public static boolean disableStrataVents;

    public static boolean disableStrataOreVeins;
    public static boolean disableThermalDynamicsFacades;
    public static boolean fixChocolateQuestDivideByZero;
    public static boolean hideChocolateQuestGeneratingStructureOverlay;

    public static boolean enableArmorMixin;

    public static boolean changeArmorBarAmount;

    public static float protectionMultiplier;

    public static boolean enableChestLaunch;
    public static float chestLaunchHorizontal;
    public static float chestLaunchUpward;

    public static boolean enableFullExplosionDrops;

    public static boolean enableMeleeDamageTooltip;

    // Combat tweaks
    public static boolean enableFistDamageBoost;
    public static float fistDamageAmount;
    public static boolean enableStickDamageBonus;
    public static float stickDamageBonus;

    public static boolean reworkVillageGolems;
    public static int initialVillageGolems;

    public static boolean disableSleepRainClear;

    public static boolean enableBedChill;

    // Item pickup star (client)
    public static boolean enableItemPickupStar;
    public static boolean itemPickupStarOnStackIncrease; // highlight simple count increases too?
    public static boolean itemPickupStarClearHeldItem;
    public static boolean itemPickupStarClearOnLeaveInventory;
    public static boolean itemPickupStarClearOnInventoryClose;
    public static boolean itemPickupStarShowHotbarHud;
    public static boolean enableHotbarSelectorTexture;
    public static boolean hotbarSelectorAboveItemText;
    public static boolean enableCelestialEventTextures;
    public static float celestialSunEventChance;
    public static float celestialMoonEventChance;
    public static String[] celestialSunEventTextures;
    public static String[] celestialMoonEventTextures;
    public static String celestialSunEventTexture;
    public static String celestialMoonEventTexture;

    // Zelda
    public static boolean zeldaHeartsEnabled;
    public static boolean zeldaDisableRegen;
    public static int zeldaHeartPieceRarity;
    public static int zeldaStartingHearts;
    public static int zeldaMaximumHearts;
    public static int zeldaMobDrop;
    public static int zeldaBlockDrop;

    // Avatar glider
    public static boolean gliderDyeRecipes;
    public static boolean appaRequireTameToRide;
    public static boolean appaRestrictRideToOwner;
    public static boolean appaAllowMobPassengers;
    public static boolean appaMobPassengerWhitelistMode;
    public static String[] appaMobPassengerEntityFilter;
    public static float appaMovementSpeed;

    // Zyin's HUD
    public static boolean zyinQuickDepositEnabled;
    public static boolean zyinQuickDepositIgnoreHotbar;
    public static boolean zyinQuickDepositCloseChest;
    public static boolean zyinQuickDepositBlacklistTorch;
    public static boolean zyinQuickDepositBlacklistWeapons;
    public static boolean zyinQuickDepositBlacklistArrow;
    public static boolean zyinQuickDepositBlacklistEnderPearl;
    public static boolean zyinQuickDepositBlacklistFood;
    public static boolean zyinQuickDepositBlacklistWaterBucket;
    public static boolean zyinQuickDepositBlacklistClockCompass;
    public static boolean zyinItemSelectorEnabled;
    public static int zyinItemSelectorTimeout;
    public static boolean zyinItemSelectorSideButtons;
    public static int zyinItemSelectorHudOffsetY;
    public static boolean zyinItemSelectorIncludeHotbar;

    // Dual Hotbar
    public static boolean dualHotbarEnable;
    public static boolean dualHotbarLongHotbar;
    public static boolean dualHotbarDoubleTap;
    public static boolean dualHotbarKeyCombo;
    public static int dualHotbarDoubleTapTime;
    public static int dualHotbarNumHotbars;

    // Satchels
    public static boolean satchelsHotSwap;
    public static String satchelsPouchBgColor;
    public static String satchelsSatchelBgColor;
    public static int satchelsPouchUpgradeWeight;
    public static boolean satchelsEnablePouchUpgrades;
    public static boolean satchelsEnablePouchUpgradeLoot;
    public static boolean satchelsDrawSatchel;
    public static boolean satchelsDrawSatchelStrap;
    public static boolean satchelsDrawLeftPouch;
    public static boolean satchelsDrawRightPouch;
    public static boolean satchelsCompatTechguns;
    public static String satchelsIngredient1;
    public static String satchelsIngredient2;
    public static String[] satchelsItemBlacklist;

    public static boolean enablePickupNotifier;
    public static int pickupNotifyDurationSeconds;       // e.g. 3
    public static int pickupNotifyMergeWindowSeconds;    // e.g. 1
    public static float pickupNotifyFadeSeconds;         // e.g. 0.6f
    public static int pickupNotifyMaxEntries; // e.g. 50

    public static boolean enableSafeEntityTick;
    public static boolean safeEntityTickLog;
    public static int     safeEntityTickSkipTicks;
    public static int     safeEntityTickMaxErrorsBeforeRemove;

    public static boolean enableStackOverflowGuard;
    public static int     stackOverflowMaxDepth;

    public static boolean protectItemsFromExplosions;

    public static boolean enableItemRenderLimiter;
    public static int droppedItemLimit;
    public static int droppedItemMaxRenderDistance;

    public static boolean woolRequireShears;
    public static boolean shearsDamageOnAnyBlock;

    public static boolean disableBonemeal;
    public static double bonemealFlowerChance;

    public static boolean allowPlantsOnAnyBlock;

    public static boolean strictMobSpawnsZeroBlockLight;

    public static boolean wrongUseSingleDurability;

    public static boolean invincibleOwnedMobs;

    public static boolean invincibleOwnedAllMobs;

    public static boolean invincibleRideableEntities;

    public static boolean disableTintedSugarcane;

    public static boolean enableNetherrackTweak;
    public static boolean enableDoorAirPlacement;
    public static boolean enablePodzolDirtTexture;
    public static boolean enableCustomPaintings;
    public static boolean enablePaintingSelection;
    public static boolean enablePaintingAirPlacement;
    public static String customPaintingDefaultTexture;
    public static String[] customPaintingEntries;

    public static boolean enableNewBlockHighlight;

    public static float THICKNESS;

    public static float ALPHA_BASE;
    public static boolean PULSE_ENABLED;
    public static float PULSE_SPEED_HZ;

    public static boolean disableSpecificPotions;

    public static boolean legacyBoatBuoyancy;
    public static float legacyBoatBuoyancyStrength;
    public static float boatsFallBreakDistance;

    // Hanging ladders (place ladders downward by right-clicking an existing ladder with a ladder item)
    public static boolean enableHangingLadders;
    public static boolean floatingLaddersRequireSneak;
    public static int floatingLaddersMaxScan;

    // parsed set of disabled potion IDs (e.g. 14 for invisibility)
    private static final Set<Integer> disabledPotionIdsSet = new HashSet<Integer>();

    // vortex configs
    public static boolean enableUnloader;
    public static int[] unloaderBlacklistedDimensions;
    public static boolean enablePlacedItem;
    public static int butterflyKnifeDurability;
    public static float butterflyKnifeDamage;
    public static float butterflyKnifeBackstabDamage;
    public static boolean butterflyKnifeShowBackstabCounter;
    public static String butterflyKnifeBackstabCounterLabel;
    public static String[] butterflyKnifeFlickBoostEffects;
    public static int butterflyKnifeFlickBoostWindowTicks;
    public static boolean backpackStorage;
    public static boolean backpackDurability;
    public static int backpackDurabilityAmount;
    public static int backpackArmorPoints;
    public static boolean GluttonyCharm;

    public static void init(File file){
        config = new Configuration(file);
        syncConfig();
    }

    public static void reload() {
        if (config == null) {
            return;
        }
        config.load();
        syncConfig();
    }

    public static void syncConfig(){
        enableChromatiCraftMixin = config.getBoolean("ChromatiCraftMixin","general",true,"Toggles the progression effects");

        DisableAether2Portal = config.getBoolean("MixinAetherPortal","general",true,"Disables Aether 2 Portal, used for Aether Legacy Departure");

        hasSound = config.getBoolean("SoundEffect","general",true,"Toggles progression's sound effects");

        playerOnlyHurtSound = config.getBoolean("PlayerOnlyHurtSound","general",true,"Uses RiftFlux's custom hurt sound, this uses the original OOF hurt sound but was made so that modded mobs and player hurt sound are separated.");

        hasShader= config.getBoolean("ShaderEffect","general",false,"Toggles progression's shader effects");

        disableStrataVents = config.getBoolean("DisableStrataVents","general",false,"Toggles GeoStrata's vent spawn");

        disableStrataOreVeins = config.getBoolean("DisableStrataOreVeins","general",false,"Toggles GeoStrata's ore vein spawn");

        disableThermalDynamicsFacades = config.getBoolean(
                "DisableThermalDynamicsFacades",
                "general",
                true,
                "If true, disables ThermalDynamics facades/covers (recipes, placement, and persisted cover data)."
        );

        fixChocolateQuestDivideByZero = config.getBoolean(
                "FixChocolateQuestDivideByZero",
                "general",
                true,
                "If true, catches Chocolate Quest ArithmeticException (/ by zero) crashes during dungeon worldgen and skips only the failed generation call."
        );

        hideChocolateQuestGeneratingStructureOverlay = config.getBoolean(
                "HideChocolateQuestGeneratingStructureOverlay",
                "client",
                true,
                "If true, hides Chocolate Quest's \"Generating Structure... Please wait\" overlay."
        );

        enableArmorMixin = config.getBoolean("EnableArmorMixin","combat",true,"Enable mixin scaling armor protections");

        changeArmorBarAmount = config.getBoolean("ChangeArmorBarAmount","combat",true,
                "Enable mixin that changes armor bar displayed amount, may affect other things");

        protectionMultiplier = config.getFloat("protectionMultiplier","combat",0.5F,0.0F,1F,"Scales armor's protection(1.0 is unchanged armor)");

        enableChestLaunch = config.getBoolean("EnableChestLaunch","chest launching", true,
                "Launch entities standing on top when a chest opens.");

        chestLaunchHorizontal = (float) config.get("chest launching", "ChestLaunchHorizontal", 5,
                "Horizontal push strength when a chest opens. (no bounds)").getDouble(5);

        chestLaunchUpward = (float) config.get("chest launching", "ChestLaunchUpward", 1,
                "Upward boost when a chest opens. (no bounds)").getDouble(1);

        enableFullExplosionDrops = config.getBoolean(
                "EnableFullExplosionDrops", "general", true,
                "If true, ALL explosions drop 100% of affected blocks.");

        enableMeleeDamageTooltip = config.getBoolean(
                "EnableMeleeDamageTooltip", "general", true,
                "Replace '+X Attack Damage' with a single gray 'X.X Melee Damage' line (includes +1 base and Sharpness)."
        );

        enableFistDamageBoost = config.getBoolean(
                "EnableFistDamageBoost", "combat", true,
                "If true, empty-hand melee hits are boosted to at least FistDamageAmount (does not lower crit/strength hits)."
        );

        fistDamageAmount = (float) config.get(
                "combat", "FistDamageAmount", 2.0D,
                "Minimum damage for empty-hand melee hits when EnableFistDamageBoost is true. (no bounds)"
        ).getDouble(2.0D);

        enableStickDamageBonus = config.getBoolean(
                "EnableStickDamageBonus", "combat", true,
                "If true, sticks deal extra melee damage added on top of vanilla damage."
        );

        stickDamageBonus = (float) config.get(
                "combat", "StickDamageBonus", 1.0D,
                "Additional damage added to stick melee hits when EnableStickDamageBonus is true. (no bounds)"
        ).getDouble(1.0D);

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

        itemPickupStarClearHeldItem = config.getBoolean(
                "ItemPickupStarClearHeldItem", "general", true,
                "If true, items currently held in your hand (selected hotbar slot) immediately lose the pickup star."
        );

        itemPickupStarClearOnLeaveInventory = config.getBoolean(
                "ItemPickupStarClearOnLeaveInventory", "general", true,
                "If true, pickup stars are removed as soon as items leave your inventory (moved to other containers or dropped)."
        );

        itemPickupStarClearOnInventoryClose = config.getBoolean(
                "ItemPickupStarClearOnInventoryClose", "general", false,
                "If true, closing an inventory GUI clears pickup stars for items in that container."
        );

        itemPickupStarShowHotbarHud = config.getBoolean(
                "ItemPickupStarShowHotbarHud", "general", true,
                "If true, show pickup stars on the in-game hotbar HUD."
        );

        enableHotbarSelectorTexture = config.getBoolean(
                "EnableRiftHotbarSelectorTexture", "general", true,
                "Use riftselector.png for the hotbar selection box to avoid vanilla cut-off."
        );

        hotbarSelectorAboveItemText = config.getBoolean(
                "HotbarSelectorAboveItemText", "general", true,
                "If true, render the hotbar selector above item count text. If false, render beneath text like vanilla."
        );

        enableCelestialEventTextures = config.getBoolean(
                "EnableCelestialEventTextures",
                "client",
                true,
                "If true, sun/moon textures can be swapped on random special event days."
        );

        celestialSunEventChance = config.getFloat(
                "CelestialSunEventChance",
                "client",
                0.10F,
                0.0F,
                1.0F,
                "Chance between 0.0 and 1.0 each day for the sun texture to use CelestialSunEventTexture."
        );

        celestialSunEventTextures = sanitizeCelestialTextureList(config.getStringList(
                "CelestialSunEventTextures",
                "client",
                new String[]{"riftflux:textures/environment/sun_event.png"},
                "List of candidate sun textures for event days. One is picked each event day.\n" +
                        "Format per entry: namespace:path\n" +
                        "If empty, falls back to CelestialSunEventTexture."
        ));
        config.getCategory("client")
                .get("CelestialSunEventTextures")
                .set(celestialSunEventTextures);

        celestialSunEventTexture = config.getString(
                "CelestialSunEventTexture",
                "client",
                "riftflux:textures/environment/sun_event.png",
                "Legacy single sun texture for event days. Used only when CelestialSunEventTextures is empty."
        );

        celestialMoonEventChance = config.getFloat(
                "CelestialMoonEventChance",
                "client",
                0.0F,
                0.0F,
                1.0F,
                "Chance between 0.0 and 1.0 each day for the moon texture to use CelestialMoonEventTexture."
        );

        celestialMoonEventTextures = sanitizeCelestialTextureList(config.getStringList(
                "CelestialMoonEventTextures",
                "client",
                new String[]{"riftflux:textures/environment/moon_event_phases.png"},
                "List of candidate moon phase-sheet textures for event days. One is picked each event day.\n" +
                        "Each texture must be a vanilla-style 4x2 phase sheet.\n" +
                        "A default tiled sheet based on the included moon event texture is provided.\n" +
                        "If empty, falls back to CelestialMoonEventTexture."
        ));
        config.getCategory("client")
                .get("CelestialMoonEventTextures")
                .set(celestialMoonEventTextures);

        celestialMoonEventTexture = config.getString(
                "CelestialMoonEventTexture",
                "client",
                "textures/environment/moon_phases.png",
                "Legacy single moon texture for event days. Used only when CelestialMoonEventTextures is empty."
        );
        if (celestialMoonEventTextures.length == 1
                && "textures/environment/moon_phases.png".equalsIgnoreCase(celestialMoonEventTextures[0])
                && "textures/environment/moon_phases.png".equalsIgnoreCase(celestialMoonEventTexture)) {
            celestialMoonEventTextures = new String[]{"riftflux:textures/environment/moon_event_phases.png"};
            config.getCategory("client")
                    .get("CelestialMoonEventTextures")
                    .set(celestialMoonEventTextures);
        }

        zeldaHeartsEnabled = config.getBoolean(
                "EnableHeartsModule",
                "zelda",
                true,
                "If false, disables the Zelda hearts module (HUD, heart items/drops, and regen rule changes)."
        );

        zeldaDisableRegen = config.getBoolean(
                "disableRegen",
                "zelda",
                false,
                "False if you want to enable natural health regen."
        );

        zeldaHeartPieceRarity = config.getInt(
                "heartPieceRarity",
                "zelda",
                10,
                0,
                Integer.MAX_VALUE,
                "The higher the number, the more common the heart pieces will generate in chests. 0 to disable."
        );

        zeldaStartingHearts = config.getInt(
                "startingHearts",
                "zelda",
                3,
                1,
                Integer.MAX_VALUE,
                "The amount of hearts you will initially spawn with."
        );

        zeldaMaximumHearts = config.getInt(
                "maximumHearts",
                "zelda",
                10,
                1,
                Integer.MAX_VALUE,
                "The maximum amount of hearts you can have."
        );

        zeldaMobDrop = config.getInt(
                "mobDrop",
                "zelda",
                3,
                0,
                Integer.MAX_VALUE,
                "The higher the number, the rarer the hearts will drop from mobs. 0 to disable."
        );

        zeldaBlockDrop = config.getInt(
                "blockDrop",
                "zelda",
                20,
                0,
                Integer.MAX_VALUE,
                "The higher the number, the rarer the hearts will drop from tall grass. 0 to disable."
        );

        gliderDyeRecipes = config.getBoolean(
                "GliderDyeRecipes",
                "avatar",
                true,
                "If true, gliders can be recolored with dyes in a crafting grid."
        );

        appaRequireTameToRide = config.getBoolean(
                "AppaRequireTameToRide",
                "avatar",
                true,
                "If true, Appa can only be ridden after being tamed."
        );

        appaRestrictRideToOwner = config.getBoolean(
                "AppaRestrictRideToOwner",
                "avatar",
                false,
                "If true, only the owner can ride a tamed Appa."
        );

        appaAllowMobPassengers = config.getBoolean(
                "AppaAllowMobPassengers",
                "avatar",
                true,
                "If true, non-player mobs can occupy Appa's passenger seats when pushed into them."
        );

        appaMobPassengerWhitelistMode = config.getBoolean(
                "AppaMobPassengerWhitelistMode",
                "avatar",
                false,
                "If false, AppaMobPassengerEntityFilter is a blacklist.\n" +
                        "If true, AppaMobPassengerEntityFilter is a whitelist."
        );

        appaMobPassengerEntityFilter = config.getStringList(
                "AppaMobPassengerEntityFilter",
                "avatar",
                new String[]{
                        "EntityBison"
                },
                "Entity IDs/class names used to filter which mobs can ride Appa.\n" +
                        "Matches entity ID, class simple name, or full class name.\n" +
                        "Default blocks Appa from mounting itself."
        );
        appaMobPassengerEntityFilter = sanitizeAppaPassengerFilter(appaMobPassengerEntityFilter);
        config.getCategory("avatar")
                .get("AppaMobPassengerEntityFilter")
                .set(appaMobPassengerEntityFilter);

        appaMovementSpeed = config.getFloat(
                "AppaMovementSpeed",
                "avatar",
                0.1F,
                0.0F,
                2.0F,
                "Base movement speed for Appa. Lower values make Appa slower."
        );

        zyinQuickDepositEnabled = config.getBoolean(
                "EnableQuickDeposit",
                "quickdeposit",
                true,
                "Enables Quick Deposit."
        );

        zyinQuickDepositIgnoreHotbar = config.getBoolean(
                "IgnoreItemsInHotbar",
                "quickdeposit",
                false,
                "Determines if items in your hotbar will be deposited into chests."
        );

        zyinQuickDepositCloseChest = config.getBoolean(
                "CloseChestAfterDepositing",
                "quickdeposit",
                false,
                "Closes the chest GUI after you deposit your items in it."
        );

        zyinQuickDepositBlacklistTorch = config.getBoolean(
                "BlacklistTorch",
                "quickdeposit",
                false,
                "Stop Quick Deposit from putting torches in chests?"
        );

        zyinQuickDepositBlacklistWeapons = config.getBoolean(
                "BlacklistWeapons",
                "quickdeposit",
                false,
                "Stop Quick Deposit from putting swords and bows in chests?"
        );

        zyinQuickDepositBlacklistArrow = config.getBoolean(
                "BlacklistArrow",
                "quickdeposit",
                false,
                "Stop Quick Deposit from putting arrows in chests?"
        );

        zyinQuickDepositBlacklistEnderPearl = config.getBoolean(
                "BlacklistEnderPearl",
                "quickdeposit",
                false,
                "Stop Quick Deposit from putting ender pearls in chests?"
        );

        zyinQuickDepositBlacklistFood = config.getBoolean(
                "BlacklistFood",
                "quickdeposit",
                false,
                "Stop Quick Deposit from putting food in chests?"
        );

        zyinQuickDepositBlacklistWaterBucket = config.getBoolean(
                "BlacklistWaterBucket",
                "quickdeposit",
                false,
                "Stop Quick Deposit from putting water buckets in chests?"
        );

        zyinQuickDepositBlacklistClockCompass = config.getBoolean(
                "BlacklistClockCompass",
                "quickdeposit",
                false,
                "Stop Quick Deposit from putting clocks and compasses in chests?"
        );

        zyinItemSelectorEnabled = config.getBoolean(
                "EnableItemSelector",
                "itemselector",
                true,
                "Enable/disable using mouse wheel scrolling to swap the selected hotbar item."
        );

        zyinItemSelectorTimeout = config.getInt(
                "ItemSelectorTimeout",
                "itemselector",
                1200,
                0,
                2000,
                "How many ticks the item selector overlay stays visible before it disappears."
        );

        zyinItemSelectorSideButtons = config.getBoolean(
                "ItemSelectorSideButtons",
                "itemselector",
                false,
                "Enable/disable use of side buttons for item selection."
        );

        zyinItemSelectorHudOffsetY = config.getInt(
                "ItemSelectorHudOffsetY",
                "itemselector",
                30,
                -2000,
                2000,
                "Pixels to move the item selector overlay up (positive moves up)."
        );

        zyinItemSelectorIncludeHotbar = config.getBoolean(
                "ItemSelectorIncludeHotbar",
                "itemselector",
                true,
                "If true, include hotbar rows in the item selector overlay and selection."
        );

        dualHotbarEnable = config.getBoolean(
                "Enable",
                "dualhotbar",
                true,
                "Enable DualHotbar."
        );

        dualHotbarLongHotbar = config.getBoolean(
                "Long Hotbar",
                "dualhotbar",
                false,
                "If enabled, it will render all 18 slots in one row (not available for 3 hotbar rows)."
        );

        dualHotbarDoubleTap = config.getBoolean(
                "Enable Double Tap",
                "dualhotbar",
                true,
                "Double tap the inventory key to select the upper layer item."
        );

        dualHotbarKeyCombo = config.getBoolean(
                "Enable Key Combo",
                "dualhotbar",
                false,
                "Use key combo to select the upper layer item."
        );

        dualHotbarDoubleTapTime = config.getInt(
                "Double Tap Time",
                "dualhotbar",
                900,
                0,
                2000,
                "Time (in milliseconds) for double tapping."
        );

        dualHotbarNumHotbars = config.getInt(
                "Number of Hotbars",
                "dualhotbar",
                2,
                1,
                4,
                "How many hotbar rows (9 slots each)."
        );

        // --- Satchels ---
        satchelsHotSwap = config.getBoolean(
                "SatchelsHotSwap", "satchels", false,
                "Apply changes made in the config file immediately.\n" +
                        "Off by default because it could potentially cause poor performance on certain platforms.\n" +
                        "Useful for tweaking the GUI."
        );


        satchelsPouchBgColor = config.getString(
                "SatchelsPouchBgColor", "satchels_gui", "FFB266",
                ""
        );


        satchelsSatchelBgColor = config.getString(
                "SatchelsSatchelBgColor", "satchels_gui", "FFBF99",
                ""
        );

        satchelsPouchUpgradeWeight = config.getInt(
                "SatchelsPouchUpgradeWeight", "satchels_worldgen", 7, 0, Integer.MAX_VALUE,
                "The weight of the pouch upgrade in the dungeon loot table.\n" +
                "Increase this to make them more common, or decrease to make them rarer.\n" +
                "For reference, saddles have a weight of 10 while golden apples have a weight of 1.\n" +
                "Based on testing, a weight of 10 with no other mods present roughly corresponds to an average of 1 item per dungeon, and it scales linearly from there.\n" +
                "You might want to bump this up if you have many other mods adding loot, or if this is a multiplayer server.\n" +
                "Ignored if SatchelsEnablePouchUpgradeLoot is false."
        );

        satchelsEnablePouchUpgrades = config.getBoolean(
                "SatchelsEnablePouchUpgrades", "satchels", true,
                "If false, pouch upgrades no longer increase pouch slot count.\n" +
                        "Existing upgrades can still be removed."
        );

        satchelsEnablePouchUpgradeLoot = config.getBoolean(
                "SatchelsEnablePouchUpgradeLoot", "satchels_worldgen", true,
                "If false, pouch upgrades will not appear in dungeon loot."
        );

        satchelsDrawSatchel = config.getBoolean(
                "SatchelsDrawSatchel", "satchels_player_model", true,
                "Draw the satchel on the player model."
        );

        satchelsDrawSatchelStrap = config.getBoolean(
                "SatchelsDrawSatchelStrap", "satchels_player_model", true,
                "Draw the satchel strap on the player model."
        );

        satchelsDrawLeftPouch = config.getBoolean(
                "SatchelsDrawLeftPouch", "satchels_player_model", true,
                "Draw the left pouch on the player model."
        );

        satchelsDrawRightPouch = config.getBoolean(
                "SatchelsDrawRightPouch", "satchels_player_model", true,
                "Draw the right pouch on the player model."
        );

        satchelsIngredient1 = config.getString(
                "SatchelsIngredient1", "satchels_recipes", "diamond_block",
                "The ingredient in the center of the bottom row of the satchel crafting recipe."
        );

        satchelsIngredient2 = config.getString(
                "SatchelsIngredient2", "satchels_recipes", "slime_ball",
                "The ingredient in the left and right of the center row of the satchel crafting recipe."
        );

        satchelsCompatTechguns = config.getBoolean(
                "SatchelsCompatTechguns", "satchels_compat", true,
                "Force Techguns to use vertical tabs (using TConstruct's API) even if TConstruct is not present."
        );

        satchelsItemBlacklist = config.getStringList(
                "SatchelsItemBlacklist", "satchels_inventory",
                BackpackConfigHelper.NON_NESTABLE_BACKPACK_BLACKLIST,
                "Items that aren't allowed in satchels or pouches" + BackpackConfigHelper.CONFIG_DESCRIPTION_SUFFIX
        );

        enablePickupNotifier = config.getBoolean(
                "EnablePickupNotifier", "general", true,
                "Show a bottom-right popup (icon + name + amount) when you pick up items."
        );

        pickupNotifyDurationSeconds = config.getInt(
                "PickupNotifierDurationSeconds", "general", 6, 1, 20,
                "How long (in seconds) each popup stays on screen."
        );

        pickupNotifyMergeWindowSeconds = config.getInt(
                "PickupNotifierMergeWindowSeconds", "general", 2, 0, 10,
                "If you pick up the same item again within this window, merge into one entry."
        );

        pickupNotifyFadeSeconds = config.getFloat(
                "PickupNotifierFadeSeconds", "general", 2.5f, 0.05f, 10f,
                "Seconds to fade out the pickup notification (time-based, not FPS-based)."
        );

        pickupNotifyMaxEntries = config.getInt(
                "PickupNotifierMaxEntries", "general", 50, 1, 200,
                "Max number of pickup notifications kept on-screen at once."
        );

        enableSafeEntityTick = config.get("general", "EnableSafeEntityTick", true,
                        "Catch NullPointerExceptions from entity ticks and temporarily skip those entities\n" +
                                "instead of crashing the game/server.")
                .getBoolean(true);

        safeEntityTickLog = config.get("general", "SafeEntityTickLog", true,
                        "Log when an entity tick NPE is suppressed and a cooldown is applied.")
                .getBoolean(true);

        float skipSeconds = config.getFloat(
                "SafeEntityTickSkipSeconds", "general", 2.0f, 0.05f, 60f,
                "How long to skip updating a faulting entity after an NPE (in seconds).");
        safeEntityTickSkipTicks = Math.max(1, Math.round(skipSeconds * 20f));

        safeEntityTickMaxErrorsBeforeRemove = config.get("general",
                        "SafeEntityTickMaxErrorsBeforeRemove", 0,
                        "If > 0, entities that throw this many consecutive NPEs during tick will be removed.\n" +
                                "Set to 0 to never remove (only skip).")
                .getInt(0);

        enableStackOverflowGuard = config.get("general", "EnableStackOverflowGuard", true,
                "Avoids StackOverflowError in crash reporting and deep world lookups.").getBoolean(true);

        stackOverflowMaxDepth = config.get("general", "StackOverflowMaxDepth", 512,
                "Max recursion depth for World.getBlock before returning air.").getInt(512);

        protectItemsFromExplosions = config.get("general", "protectItemsFromExplosions", true,
                "Stops explosions from deleting items").getBoolean(true);

        enableItemRenderLimiter = config.get("client", "enableItemRenderLimiter", true,
                        "If true, dynamically limits how many dropped items are rendered each frame.")
                .getBoolean(true);

        droppedItemLimit = config.get("client", "droppedItemLimit", 512,
                        "Maximum number of dropped items to render per frame. Use 2048 for 'unlimited'.")
                .getInt(512);

        droppedItemMaxRenderDistance = config.get("client", "droppedItemMaxRenderDistance", 64,
                        "Maximum render distance for dropped items (in blocks). " +
                                "Set to 0 or negative to disable this extra cutoff.")
                .getInt(64);

        woolRequireShears = config.get("general", "woolRequireShears", true,
                "If true, wool blocks only drop when harvested with shears.")
                .getBoolean(true);

        shearsDamageOnAnyBlock = config.get("general", "shearsDamageOnAnyBlock", true,
                        "If true, shears lose 1 durability when breaking any block (excluding the vanilla shears targets to avoid double damage).")
                .getBoolean(true);

        disableBonemeal = config.getBoolean(
                "OverrideBonemealBehavior",
                "general",
                true,
                "If true, overrides bonemeal so that it only works on grass and doesn't grow crops or trees anymore, " +
                        "it also now spawns biome-correct foliage instead of just tall grass."
        );

        bonemealFlowerChance = config.getFloat(
                "BonemealFlowerChance",
                "general",
                0.11F,   // default
                0.0F,    // min
                1.0F,    // max
                "Chance between 0.0 and 1.0 that bonemeal on grass will spawn flowers.\n" +
                        "0.0 = never, 1.0 = always when used on grass."
        );

        allowPlantsOnAnyBlock = config.getBoolean(
                "AllowPlantsOnAnyBlock",
                "general",
                true,
                "If true, tall grass and all BlockBush-based plants can be placed on any block instead of only on grass/dirt/farmland."
        );

        strictMobSpawnsZeroBlockLight = config.getBoolean(
                "StrictMobSpawnsZeroBlockLight",
                "general",
                true,
                "If true, hostile mobs (EntityMob) may only spawn when BLOCK light level is 0.\n" +
                        "Sky light is only used for the vanilla daytime check. Torches etc. fully prevent spawns."
        );

        wrongUseSingleDurability = config.getBoolean(
                "wrongUseSingleDurability",
                "general",
                true,
                "If true, items only lose 1 durability instead of 2 " +
                        "when used for the wrong purpose (e.g. tools hitting mobs, swords breaking blocks)."
        );

        invincibleOwnedMobs = config.getBoolean(
                "invincibleOwnedMobs",
                "general",
                false,
                "If true, all pets owned by players (IEntityOwnable with a player owner) " +
                        "are completely invincible."
        );

        invincibleOwnedAllMobs  = config.getBoolean(
                "invincibleOwnedAllMobs",
                "general",
                false,
                "If true, ALL IEntityOwnable mobs with a player owner are invincible (includes modded summons).\n" +
                        "If false, ONLY tamed pets (EntityTameable.isTamed and EntityHorse.isTame) owned by players are invincible."
        );

        invincibleRideableEntities  = config.getBoolean(
                "invincibleRideableEntities",
                "general",
                true,
                "If true, any entity currently being ridden by a player is completely invincible."
        );

        enableNewBlockHighlight = config.get("client", "enableNewBlockHighlight", true,
                        "If true, replaces the original block highlight with a white pulsating cuboid highlight.")
                .getBoolean(true);

        THICKNESS = config.getFloat(
                "BlockHighlightThickness",
                "client",
                0.033F,
                0.0F,
                1.0F,
                "Edge thickness in world units (half in / half out around the block edges)."
        );

        ALPHA_BASE = config.getFloat(
                "BlockHighlightBaseAlpha",
                "client",
                1.0F,
                0.0F,
                1.0F,
                "Base alpha for the block highlight (1.0 = fully opaque)."
        );

        PULSE_ENABLED = config.get(
                "client",
                "BlockHighlightPulseEnabled",
                true,
                "If false, the block highlight will not pulsate (constant alpha)."
        ).getBoolean(true);

        PULSE_SPEED_HZ = config.getFloat(
                "BlockHighlightPulseSpeedHz",
                "client",
                0.44F,
                0.0F,
                5.0F,
                "Pulse speed in cycles per second. 0 = no pulsation (but PULSE_ENABLED must also be false to fully disable)."
        );


        disableTintedSugarcane = config.getBoolean(
                "disableTintedSugarcane",
                "general",
                true,
                "If true, disables the tinting of sugarcane, returning them to how they look in beta minecraft."
        );

        enableNetherrackTweak = config.getBoolean(
                "enableNetherrackTweak",
                "general",
                true,
                "If true, Netherrack ONLY drops when you're in the Nether."
        );

        enableDoorAirPlacement = config.getBoolean(
                "enableDoorAirPlacement",
                "general",
                false,
                "If true, doors can be placed without a solid block below and will not break if their support is removed, this allows you to place doors ontop of things like stairs."
        );

        enablePodzolDirtTexture = config.getBoolean(
                "enablePodzolDirtTexture",
                "general",
                false,
                "If true, podzol uses riftflux:dirt on the bottom, and when a podzol block is below another podzol it uses riftflux:dirt on all faces. Mycelium also uses riftflux:dirt on the bottom."
        );

        enableCustomPaintings = config.getBoolean(
                "EnableCustomPaintings",
                "general",
                true,
                "If true, RiftFlux can add extra painting motives from CustomPaintingEntries while keeping all vanilla motives."
        );

        enablePaintingSelection = config.getBoolean(
                "EnablePaintingSelection",
                "general",
                true,
                "If true, sneak + right-click with a painting opens a selector GUI to choose the exact motive to place."
        );

        enablePaintingAirPlacement = config.getBoolean(
                "EnablePaintingAirPlacement",
                "general",
                true,
                "If true, paintings can be placed and remain without solid blocks behind them (only collisions and other hangings are checked)."
        );

        customPaintingDefaultTexture = config.getString(
                "CustomPaintingDefaultTexture",
                "general",
                "riftflux:textures/painting/custom_paintings.png",
                "Default texture used by custom paintings when an entry does not provide a texture.\n" +
                        "Format: namespace:path"
        );

        String[] defaultCustomPaintingEntries = new String[]{
                "SeaGlow;32;32;0;128",
                "PinkPath;32;32;32;128",
                "GoldenField;32;32;64;128",
                "BluePeak;32;32;96;128",
                "SunsetMount;32;32;128;128",
                "MistyRange;32;32;160;128",
                "CloudValley;64;64;0;192",
                "GreenBridge;64;64;64;192",
                "NightSky;64;64;128;192",
                "CityPath;64;48;0;0;riftflux:textures/painting/CityPath.png",
                "AutumnRoad;64;48;192;112",
                "FrostRelic;32;32;0;0;riftflux:textures/painting/imported/FrostRelic.png",
                "BlueStamp;16;16;0;0;riftflux:textures/painting/imported/BlueStamp.png",
                "CinderBloom;32;32;0;0;riftflux:textures/painting/imported/CinderBloom.png",
                "MushroomChips;16;16;0;0;riftflux:textures/painting/imported/MushroomChips.png",
                "CreepingFlowers;16;32;0;0;riftflux:textures/painting/imported/CreepingFlowers.png",
                "CrackedSun;32;32;0;0;riftflux:textures/painting/imported/CrackedSun.png",
                "PotionShelf;16;16;0;0;riftflux:textures/painting/imported/PotionShelf.png",
                "FrostGlyph;32;32;0;0;riftflux:textures/painting/imported/FrostGlyph.png",
                "GoldMoss;32;32;0;0;riftflux:textures/painting/imported/GoldMoss.png",
                "Oddities;16;16;0;0;riftflux:textures/painting/imported/Oddities.png",
                "Toolrack;32;32;0;0;riftflux:textures/painting/imported/Toolrack.png",
                "Dusk;32;16;0;0;riftflux:textures/painting/imported/Dusk.png",
                "StarrySky;96;64;0;0;riftflux:textures/painting/StarrySky.png",
                "ButterflyCollection;32;16;0;0;riftflux:textures/painting/imported/ButterflyCollection.png",
                "EchoesOfLove;32;32;0;0;riftflux:textures/painting/imported/EchoesOfLove.png",
                "SkullMark;32;32;0;0;riftflux:textures/painting/imported/SkullMark.png",
                "JollySquid;16;16;0;0;riftflux:textures/painting/imported/JollySquid.png",
                "Noteboard;32;16;0;0;riftflux:textures/painting/imported/Noteboard.png",
                "PondPixel;16;16;0;0;riftflux:textures/painting/imported/PondPixel.png",
                "HushBloom;32;32;0;0;riftflux:textures/painting/imported/HushBloom.png",
                "Bloid;32;32;0;0;riftflux:textures/painting/imported/Bloid.png",
                "VelvetRune;32;32;0;0;riftflux:textures/painting/imported/VelvetRune.png",
                "pack.png;16;16;0;0;riftflux:textures/painting/pack.png",
                "stylized.png;32;32;0;0;riftflux:textures/painting/imported/stylized.png",
                "Pegboard;16;16;0;0;riftflux:textures/painting/imported/Pegboard.png",
                "CopperKnot;32;32;0;0;riftflux:textures/painting/imported/CopperKnot.png",
                "SeedlingBloom;32;32;0;0;riftflux:textures/painting/imported/SeedlingBloom.png",
                "KelpSpark;32;32;0;0;riftflux:textures/painting/imported/KelpSpark.png",
                "BrightSpark;32;32;0;0;riftflux:textures/painting/imported/BrightSpark.png",
                "FogToken;32;32;0;0;riftflux:textures/painting/imported/FogToken.png",
                "UmbralBanner;32;32;0;0;riftflux:textures/painting/imported/UmbralBanner.png",
                "SoftRibbon;32;16;0;0;riftflux:textures/painting/imported/SoftRibbon.png",
                "WarmEcho;32;16;0;0;riftflux:textures/painting/imported/WarmEcho.png",
                "NorthGlyph;16;16;0;0;riftflux:textures/painting/imported/NorthGlyph.png",
                "StarHaze;96;64;0;0;riftflux:textures/painting/StarHaze.png",
                "SilentStem;16;32;0;0;riftflux:textures/painting/imported/SilentStem.png",
                "BurningSkele;32;32;0;0;riftflux:textures/painting/BurningSkele.png",
                "SkyBadge;16;16;16;0;riftflux:textures/painting/imported/SkyBadge.png",
                "Meeseeks;16;16;32;0;riftflux:textures/painting/imported/Meeseeks.png",
                "TornNote;16;16;48;0;riftflux:textures/painting/imported/TornNote.png",
                "IvoryMark;16;16;64;0;riftflux:textures/painting/imported/IvoryMark.png",
                "Shield;16;16;80;0;riftflux:textures/painting/imported/Shield.png",
                "Knives;16;16;96;0;riftflux:textures/painting/imported/Knives.png",
                "WineRack;32;16;0;32;riftflux:textures/painting/imported/WineRack.png",
                "Axe;32;16;32;32;riftflux:textures/painting/imported/Axe.png",
                "Utensils;32;16;64;32;riftflux:textures/painting/imported/Utensils.png",
                "ForestDawn;32;16;96;32;riftflux:textures/painting/imported/ForestDawn.png",
                "Dust;32;16;128;32;riftflux:textures/painting/imported/Dust.png",
                "Clouds;96;64;0;0;riftflux:textures/painting/Clouds.png",
                "Sun;64;64;0;0;riftflux:textures/painting/Sun.png",
                "AtlasBannerGreen;16;32;0;64;riftflux:textures/painting/imported/AtlasBannerGreen.png",
                "AtlasBannerBlue;16;32;16;64;riftflux:textures/painting/imported/AtlasBannerBlue.png",
                "Celestial;32;32;64;128;riftflux:textures/painting/imported/Celestial.png",
                "Flowering;32;32;96;128;riftflux:textures/painting/imported/Flowering.png",
                "CrossedBlades;32;32;128;128;riftflux:textures/painting/imported/CrossedBlades.png",
                "AtlasBlades;32;32;160;128;riftflux:textures/painting/imported/AtlasBlades.png",
                "SummitCliff;64;48;192;64;riftflux:textures/painting/imported/SummitCliff.png",
                "ForestPath;64;48;192;112;riftflux:textures/painting/imported/ForestPath.png",
                "Harbor;64;64;0;192;riftflux:textures/painting/imported/Harbor.png",
                "Lighthouse;64;64;64;192;riftflux:textures/painting/imported/Lighthouse.png",
                "Slowpoke;32;32;0;0;riftflux:textures/painting/Slowpoke.png",
                "AtlasNote;64;32;0;96;riftflux:textures/painting/imported/AtlasNote.png",
                "DarkForest;32;32;0;128;riftflux:textures/painting/imported/DarkForest.png",
                "SnowCoral;16;16;0;0;riftflux:textures/painting/imported/SnowCoral.png",
                "CreepyMint;16;16;0;0;riftflux:textures/painting/imported/CreepyMint.png",
                "PuppyHeart;16;16;0;0;riftflux:textures/painting/imported/PuppyHeart.png",
                "SkullFrog;16;16;0;0;riftflux:textures/painting/imported/SkullFrog.png",
                "BearPaw;16;16;0;0;riftflux:textures/painting/imported/BearPaw.png",
                "LunarEye;16;16;0;0;riftflux:textures/painting/imported/LunarEye.png",
                "NeonPearl;16;16;0;0;riftflux:textures/painting/imported/NeonPearl.png",
                "SpinedCoil;16;16;0;0;riftflux:textures/painting/imported/SpinedCoil.png",
                "SpiralingSeed;16;16;0;0;riftflux:textures/painting/imported/SpiralingSeed.png",
                "MugShine;16;16;0;0;riftflux:textures/painting/imported/MugShine.png",
                "VoidGlyph;16;16;0;0;riftflux:textures/painting/imported/VoidGlyph.png",
                "Umbra;16;16;0;0;riftflux:textures/painting/imported/Umbra.png",
                "DarkStorm;32;32;32;128;riftflux:textures/painting/imported/DarkStorm.png"
        };

        Property customPaintingProp = config.get(
                "general",
                "CustomPaintingEntries",
                defaultCustomPaintingEntries,
                "List of extra painting entries.\n" +
                        "Format: title;sizeX;sizeY;offsetX;offsetY[;texture]\n" +
                        "- title: motive name stored in world data/network packets\n" +
                        "- sizeX/sizeY: painting size in pixels (must be multiples of 16)\n" +
                        "- offsetX/offsetY: top-left pixel in the texture\n" +
                        "- texture (optional): namespace:path; falls back to CustomPaintingDefaultTexture"
        );
        customPaintingEntries = customPaintingProp.getStringList();
        customPaintingEntries = mergeStringListUnique(customPaintingEntries, defaultCustomPaintingEntries);
        customPaintingEntries = applyCustomPaintingMigrations(customPaintingEntries, true);
        customPaintingProp.set(customPaintingEntries);
        // --- Boats: configurable legacy buoyancy + fall-breaking ---
        // Used by Rift Flux's boat mixins and intended to apply to vanilla and modded boats.
        legacyBoatBuoyancy = config.getBoolean(
                "legacyBoatBuoyancy",
                "tweaks",
                true,
                "Restores the old Minecraft boat buoyancy; boats will not dismount riders when submerged and will try to rise back up to the water surface if pushed underwater. (applies to modded boats too)"
        );

        legacyBoatBuoyancyStrength = config.getFloat(
                "legacyBoatBuoyancyStrength",
                "tweaks",
                0.33F,
                0.0F,
                Float.MAX_VALUE,
                "When legacyBoatBuoyancy is enabled, controls how strongly boats rise back up toward the water surface. 0 disables the extra buoyancy strength; higher values rise faster."
        );

        boatsFallBreakDistance = config.getFloat(
                "boatsFallBreakDistance",
                "tweaks",
                33.0F,
                0.0F,
                Float.MAX_VALUE,
                "Fall distance (in blocks, roughly) at which boats break when landing on a solid block. Set to 0 to disable fall-breaking."
        );

        // --- Hanging ladders & ladder movement tweaks ---
        enableHangingLadders = config.getBoolean(
                "enableHangingLadders",
                "tweaks",
                true,
                "Enables Hanging Ladders, extend a ladder downward by right-clicking an existing ladder with another ladder also enables holding jump to climb ladders."
        );

        floatingLaddersRequireSneak = config.getBoolean(
                "floatingLaddersRequireSneak",
                "tweaks",
                false,
                "If true, you must be sneaking while right-clicking the ladder to extend it downward."
        );

        floatingLaddersMaxScan = config.getInt(
                "floatingLaddersMaxScan",
                "tweaks",
                256,
                0,
                256,
                "Maximum number of ladder blocks to scan downward when finding the bottom of a ladder column."
        );

        // disable specific potions on players
        disableSpecificPotions = config.getBoolean(
                "DisableSpecificPotions",
                "general",
                false,
                "If true, potion effects IDs that are listed in 'DisabledPotionIds' will never be applied to players."
        );

        String disabledPotionIdsRaw = config.get(
                "general",
                "DisabledPotionIds",
                "",
                "Comma-separated list of potion IDs to block on players.\n" +
                        "Example: 14 disables invisibility; '14,12' disables invisibility and fire resistance."
        ).getString();

        parseDisabledPotionIds(disabledPotionIdsRaw);

        //vortex configs

        enableUnloader = config.getBoolean("EnableUnloader", "unloader", true,
                "Enable the Unloader port.");

        unloaderBlacklistedDimensions = config.get("unloader", "UnloaderBlacklistedDimensions",
                new int[]{-1, 0, 1},
                "Dimension IDs that should never be unloaded. Default: -1 (Nether), 0 (Overworld), 1 (End)").getIntList();

        enablePlacedItem = config.getBoolean(
                "EnablePlacedItem",
                "vortex",
                true,
                "If true, you can place the held item into the world using the Place Item keybind."
        );

        butterflyKnifeDurability = config.getInt(
                "butterflyKnifeDurability",
                "vortex",
                0,
                0,
                32767,
                "Max durability for the butterfly knife. Set to 0 for infinite durability."
        );

        butterflyKnifeDamage = config.getFloat(
                "butterflyKnifeDamage",
                "vortex",
                4.0F,
                0.0F,
                Float.MAX_VALUE,
                "Normal melee damage dealt by the butterfly knife (+1 base damage before enchantments)."
        );

        butterflyKnifeBackstabDamage = config.getFloat(
                "butterflyKnifeBackstabDamage",
                "vortex",
                45.0F,
                0.0F,
                Float.MAX_VALUE,
                "Damage dealt on a successful backstab (sneak attack from behind, ignores armor)."
        );

        butterflyKnifeShowBackstabCounter = config.getBoolean(
                "butterflyKnifeShowBackstabCounter",
                "vortex",
                true,
                "If true, show a BackTrak counter tooltip on the butterfly knife."
        );

        butterflyKnifeBackstabCounterLabel = config.getString(
                "butterflyKnifeBackstabCounterLabel",
                "vortex",
                "BackTrak: %d",
                "Tooltip format for the backstab counter. Use %d for the number."
        );

        butterflyKnifeFlickBoostEffects = config.getStringList(
                "butterflyKnifeFlickBoostEffects",
                "vortex",
                new String[]{"speed,5,1.0"},
                "Potion effects applied when flicking shortly after a successful backstab.\n" +
                        "Format per entry: potionNameOrId,amplifier,durationSeconds (or durationTicks).\n" +
                        "Example: speed,5,1.0"
        );

        float flickWindowSeconds = config.getFloat(
                "butterflyKnifeFlickBoostWindowSeconds",
                "vortex",
                1.0F,
                0.0F,
                60.0F,
                "Time window (in seconds) after a successful backstab in which a flick grants the boost."
        );
        butterflyKnifeFlickBoostWindowTicks = Math.max(0, Math.round(flickWindowSeconds * 20.0F));

        backpackStorage = config.getBoolean("backpackStorage", "vortex", true,
                "Allows the backpack to be unequipped while containing items.");

        backpackDurability = config.getBoolean("backpackDurability", "vortex", false,
                "If true, the backpack uses durability. The max durability is exactly backpackDurabilityAmount.\n" +
                        "If false, the backpack has no durability.");

        backpackDurabilityAmount = config.getInt("backpackDurabilityAmount", "vortex", 512, 1, 4096,
                "Exact max durability for the backpack when backpackDurability is true (e.g., 1234). Default: 512.");

        backpackArmorPoints = config.getInt("backpackArmorPoints", "vortex", 2, 0, 20,
                "Armor points granted by the backpack (0 = no armor).");

        GluttonyCharm = config.getBoolean("GluttonyCharm", "vortex", false,
                "Gives the gluttony charm an autofeeding functionality. Right-click to put in food items.");

        DualHotbarConfig.syncFromModConfig();
        zelda.Config.syncFromModConfig();

        config.save();
    }

    private static String[] sanitizeAppaPassengerFilter(String[] values) {
        if (values == null || values.length == 0) {
            return values;
        }
        final String appaEntityClass = "EntityBison";
        LinkedHashSet<String> unique = new LinkedHashSet<String>();
        HashSet<String> lowered = new HashSet<String>();
        for (String raw : values) {
            if (raw == null) {
                continue;
            }
            String entry = raw.trim();
            if (entry.isEmpty()) {
                continue;
            }
            if ("EntityBison".equalsIgnoreCase(entry)) {
                entry = appaEntityClass;
            }
            String key = entry.toLowerCase();
            if (lowered.add(key)) {
                unique.add(entry);
            }
        }
        if (unique.isEmpty()) {
            unique.add(appaEntityClass);
        }
        return unique.toArray(new String[unique.size()]);
    }

    private static String[] sanitizeCelestialTextureList(String[] values) {
        if (values == null || values.length == 0) {
            return new String[0];
        }
        LinkedHashSet<String> unique = new LinkedHashSet<String>();
        for (String raw : values) {
            if (raw == null) {
                continue;
            }
            String entry = raw.trim();
            if (entry.isEmpty()) {
                continue;
            }
            unique.add(entry);
        }
        return unique.toArray(new String[unique.size()]);
    }

    private static String[] mergeStringListUnique(String[] primary, String[] fallback) {
        LinkedHashSet<String> merged = new LinkedHashSet<String>();

        if (primary != null) {
            for (String raw : primary) {
                if (raw == null) {
                    continue;
                }
                String entry = raw.trim();
                if (!entry.isEmpty()) {
                    merged.add(entry);
                }
            }
        }

        if (fallback != null) {
            for (String raw : fallback) {
                if (raw == null) {
                    continue;
                }
                String entry = raw.trim();
                if (!entry.isEmpty()) {
                    merged.add(entry);
                }
            }
        }

        return merged.toArray(new String[merged.size()]);
    }

    private static boolean allowCustomPaintingInsert = true;

    private static String[] forceCustomPaintingEntry(String[] entries, String title, String replacement) {
        LinkedHashSet<String> out = new LinkedHashSet<String>();
        boolean inserted = false;

        if (entries != null) {
            for (String raw : entries) {
                if (raw == null) {
                    continue;
                }
                String entry = raw.trim();
                if (entry.isEmpty()) {
                    continue;
                }

                String existingTitle = entry;
                int separator = entry.indexOf(';');
                if (separator >= 0) {
                    existingTitle = entry.substring(0, separator).trim();
                }

                if (existingTitle.equalsIgnoreCase(title)) {
                    if (!inserted) {
                        out.add(replacement);
                        inserted = true;
                    }
                    continue;
                }

                out.add(entry);
            }
        }

        if (!inserted && allowCustomPaintingInsert) {
            out.add(replacement);
        }

        return out.toArray(new String[out.size()]);
    }

    private static String[] removeCustomPaintingEntry(String[] entries, String title) {
        if (entries == null || entries.length == 0) {
            return new String[0];
        }

        LinkedHashSet<String> out = new LinkedHashSet<String>();
        for (String raw : entries) {
            if (raw == null) {
                continue;
            }
            String entry = raw.trim();
            if (entry.isEmpty()) {
                continue;
            }

            String existingTitle = entry;
            int separator = entry.indexOf(';');
            if (separator >= 0) {
                existingTitle = entry.substring(0, separator).trim();
            }

            if (!existingTitle.equalsIgnoreCase(title)) {
                out.add(entry);
            }
        }
        return out.toArray(new String[out.size()]);
    }

        private static String[] applyCustomPaintingMigrations(String[] entries, boolean addMissing) {
        String[] out = entries;
        boolean previousAllow = allowCustomPaintingInsert;
        allowCustomPaintingInsert = addMissing;
        try {
            out = forceCustomPaintingEntry(out, "SeaGlow", "SeaGlow;32;32;0;128");
            out = forceCustomPaintingEntry(out, "PinkPath", "PinkPath;32;32;32;128");
            out = forceCustomPaintingEntry(out, "GoldenField", "GoldenField;32;32;64;128");
            out = forceCustomPaintingEntry(out, "BluePeak", "BluePeak;32;32;96;128");
            out = forceCustomPaintingEntry(out, "SunsetMount", "SunsetMount;32;32;128;128");
            out = forceCustomPaintingEntry(out, "MistyRange", "MistyRange;32;32;160;128");
            out = forceCustomPaintingEntry(out, "CloudValley", "CloudValley;64;64;0;192");
            out = forceCustomPaintingEntry(out, "GreenBridge", "GreenBridge;64;64;64;192");
            out = forceCustomPaintingEntry(out, "NightSky", "NightSky;64;64;128;192");
            out = forceCustomPaintingEntry(out, "CityPath", "CityPath;64;48;0;0;riftflux:textures/painting/CityPath.png");
            out = forceCustomPaintingEntry(out, "AutumnRoad", "AutumnRoad;64;48;192;112");
            out = forceCustomPaintingEntry(out, "FrostRelic", "FrostRelic;32;32;0;0;riftflux:textures/painting/imported/FrostRelic.png");
            out = forceCustomPaintingEntry(out, "BlueStamp", "BlueStamp;16;16;0;0;riftflux:textures/painting/imported/BlueStamp.png");
            out = forceCustomPaintingEntry(out, "CinderBloom", "CinderBloom;32;32;0;0;riftflux:textures/painting/imported/CinderBloom.png");
            out = forceCustomPaintingEntry(out, "MushroomChips", "MushroomChips;16;16;0;0;riftflux:textures/painting/imported/MushroomChips.png");
            out = forceCustomPaintingEntry(out, "CreepingFlowers", "CreepingFlowers;16;32;0;0;riftflux:textures/painting/imported/CreepingFlowers.png");
            out = forceCustomPaintingEntry(out, "CrackedSun", "CrackedSun;32;32;0;0;riftflux:textures/painting/imported/CrackedSun.png");
            out = forceCustomPaintingEntry(out, "PotionShelf", "PotionShelf;16;16;0;0;riftflux:textures/painting/imported/PotionShelf.png");
            out = forceCustomPaintingEntry(out, "FrostGlyph", "FrostGlyph;32;32;0;0;riftflux:textures/painting/imported/FrostGlyph.png");
            out = forceCustomPaintingEntry(out, "GoldMoss", "GoldMoss;32;32;0;0;riftflux:textures/painting/imported/GoldMoss.png");
            out = forceCustomPaintingEntry(out, "Oddities", "Oddities;16;16;0;0;riftflux:textures/painting/imported/Oddities.png");
            out = forceCustomPaintingEntry(out, "Toolrack", "Toolrack;32;32;0;0;riftflux:textures/painting/imported/Toolrack.png");
            out = forceCustomPaintingEntry(out, "Dusk", "Dusk;32;16;0;0;riftflux:textures/painting/imported/Dusk.png");
            out = forceCustomPaintingEntry(out, "StarrySky", "StarrySky;96;64;0;0;riftflux:textures/painting/StarrySky.png");
            out = forceCustomPaintingEntry(out, "ButterflyCollection", "ButterflyCollection;32;16;0;0;riftflux:textures/painting/imported/ButterflyCollection.png");
            out = forceCustomPaintingEntry(out, "EchoesOfLove", "EchoesOfLove;32;32;0;0;riftflux:textures/painting/imported/EchoesOfLove.png");
            out = forceCustomPaintingEntry(out, "SkullMark", "SkullMark;32;32;0;0;riftflux:textures/painting/imported/SkullMark.png");
            out = forceCustomPaintingEntry(out, "JollySquid", "JollySquid;16;16;0;0;riftflux:textures/painting/imported/JollySquid.png");
            out = forceCustomPaintingEntry(out, "Noteboard", "Noteboard;32;16;0;0;riftflux:textures/painting/imported/Noteboard.png");
            out = forceCustomPaintingEntry(out, "PondPixel", "PondPixel;16;16;0;0;riftflux:textures/painting/imported/PondPixel.png");
            out = forceCustomPaintingEntry(out, "HushBloom", "HushBloom;32;32;0;0;riftflux:textures/painting/imported/HushBloom.png");
            out = forceCustomPaintingEntry(out, "Bloid", "Bloid;32;32;0;0;riftflux:textures/painting/imported/Bloid.png");
            out = forceCustomPaintingEntry(out, "VelvetRune", "VelvetRune;32;32;0;0;riftflux:textures/painting/imported/VelvetRune.png");
            out = forceCustomPaintingEntry(out, "pack.png", "pack.png;16;16;0;0;riftflux:textures/painting/pack.png");
            out = forceCustomPaintingEntry(out, "stylized.png", "stylized.png;32;32;0;0;riftflux:textures/painting/imported/stylized.png");
            out = forceCustomPaintingEntry(out, "Pegboard", "Pegboard;16;16;0;0;riftflux:textures/painting/imported/Pegboard.png");
            out = forceCustomPaintingEntry(out, "CopperKnot", "CopperKnot;32;32;0;0;riftflux:textures/painting/imported/CopperKnot.png");
            out = forceCustomPaintingEntry(out, "SeedlingBloom", "SeedlingBloom;32;32;0;0;riftflux:textures/painting/imported/SeedlingBloom.png");
            out = forceCustomPaintingEntry(out, "KelpSpark", "KelpSpark;32;32;0;0;riftflux:textures/painting/imported/KelpSpark.png");
            out = forceCustomPaintingEntry(out, "BrightSpark", "BrightSpark;32;32;0;0;riftflux:textures/painting/imported/BrightSpark.png");
            out = forceCustomPaintingEntry(out, "FogToken", "FogToken;32;32;0;0;riftflux:textures/painting/imported/FogToken.png");
            out = forceCustomPaintingEntry(out, "UmbralBanner", "UmbralBanner;32;32;0;0;riftflux:textures/painting/imported/UmbralBanner.png");
            out = forceCustomPaintingEntry(out, "SoftRibbon", "SoftRibbon;32;16;0;0;riftflux:textures/painting/imported/SoftRibbon.png");
            out = forceCustomPaintingEntry(out, "WarmEcho", "WarmEcho;32;16;0;0;riftflux:textures/painting/imported/WarmEcho.png");
            out = forceCustomPaintingEntry(out, "NorthGlyph", "NorthGlyph;16;16;0;0;riftflux:textures/painting/imported/NorthGlyph.png");
            out = forceCustomPaintingEntry(out, "StarHaze", "StarHaze;96;64;0;0;riftflux:textures/painting/StarHaze.png");
            out = forceCustomPaintingEntry(out, "SilentStem", "SilentStem;16;32;0;0;riftflux:textures/painting/imported/SilentStem.png");
            out = forceCustomPaintingEntry(out, "BurningSkele", "BurningSkele;32;32;0;0;riftflux:textures/painting/BurningSkele.png");
            out = forceCustomPaintingEntry(out, "SkyBadge", "SkyBadge;16;16;16;0;riftflux:textures/painting/imported/SkyBadge.png");
            out = forceCustomPaintingEntry(out, "Meeseeks", "Meeseeks;16;16;32;0;riftflux:textures/painting/imported/Meeseeks.png");
            out = forceCustomPaintingEntry(out, "TornNote", "TornNote;16;16;48;0;riftflux:textures/painting/imported/TornNote.png");
            out = forceCustomPaintingEntry(out, "IvoryMark", "IvoryMark;16;16;64;0;riftflux:textures/painting/imported/IvoryMark.png");
            out = forceCustomPaintingEntry(out, "Shield", "Shield;16;16;80;0;riftflux:textures/painting/imported/Shield.png");
            out = forceCustomPaintingEntry(out, "Knives", "Knives;16;16;96;0;riftflux:textures/painting/imported/Knives.png");
            out = forceCustomPaintingEntry(out, "WineRack", "WineRack;32;16;0;32;riftflux:textures/painting/imported/WineRack.png");
            out = forceCustomPaintingEntry(out, "Axe", "Axe;32;16;32;32;riftflux:textures/painting/imported/Axe.png");
            out = forceCustomPaintingEntry(out, "Utensils", "Utensils;32;16;64;32;riftflux:textures/painting/imported/Utensils.png");
            out = forceCustomPaintingEntry(out, "ForestDawn", "ForestDawn;32;16;96;32;riftflux:textures/painting/imported/ForestDawn.png");
            out = forceCustomPaintingEntry(out, "Dust", "Dust;32;16;128;32;riftflux:textures/painting/imported/Dust.png");
            out = forceCustomPaintingEntry(out, "Clouds", "Clouds;96;64;0;0;riftflux:textures/painting/Clouds.png");
            out = forceCustomPaintingEntry(out, "Sun", "Sun;64;64;0;0;riftflux:textures/painting/Sun.png");
            out = forceCustomPaintingEntry(out, "AtlasBannerGreen", "AtlasBannerGreen;16;32;0;64;riftflux:textures/painting/imported/AtlasBannerGreen.png");
            out = forceCustomPaintingEntry(out, "AtlasBannerBlue", "AtlasBannerBlue;16;32;16;64;riftflux:textures/painting/imported/AtlasBannerBlue.png");
            out = forceCustomPaintingEntry(out, "Celestial", "Celestial;32;32;64;128;riftflux:textures/painting/imported/Celestial.png");
            out = forceCustomPaintingEntry(out, "Flowering", "Flowering;32;32;96;128;riftflux:textures/painting/imported/Flowering.png");
            out = forceCustomPaintingEntry(out, "CrossedBlades", "CrossedBlades;32;32;128;128;riftflux:textures/painting/imported/CrossedBlades.png");
            out = forceCustomPaintingEntry(out, "AtlasBlades", "AtlasBlades;32;32;160;128;riftflux:textures/painting/imported/AtlasBlades.png");
            out = forceCustomPaintingEntry(out, "SummitCliff", "SummitCliff;64;48;192;64;riftflux:textures/painting/imported/SummitCliff.png");
            out = forceCustomPaintingEntry(out, "ForestPath", "ForestPath;64;48;192;112;riftflux:textures/painting/imported/ForestPath.png");
            out = forceCustomPaintingEntry(out, "Harbor", "Harbor;64;64;0;192;riftflux:textures/painting/imported/Harbor.png");
            out = forceCustomPaintingEntry(out, "Lighthouse", "Lighthouse;64;64;64;192;riftflux:textures/painting/imported/Lighthouse.png");
            out = forceCustomPaintingEntry(out, "Slowpoke", "Slowpoke;32;32;0;0;riftflux:textures/painting/Slowpoke.png");
            out = forceCustomPaintingEntry(out, "AtlasNote", "AtlasNote;64;32;0;96;riftflux:textures/painting/imported/AtlasNote.png");
            out = forceCustomPaintingEntry(out, "DarkForest", "DarkForest;32;32;0;128;riftflux:textures/painting/imported/DarkForest.png");
            out = forceCustomPaintingEntry(out, "SnowCoral", "SnowCoral;16;16;0;0;riftflux:textures/painting/imported/SnowCoral.png");
            out = forceCustomPaintingEntry(out, "CreepyMint", "CreepyMint;16;16;0;0;riftflux:textures/painting/imported/CreepyMint.png");
            out = forceCustomPaintingEntry(out, "PuppyHeart", "PuppyHeart;16;16;0;0;riftflux:textures/painting/imported/PuppyHeart.png");
            out = forceCustomPaintingEntry(out, "SkullFrog", "SkullFrog;16;16;0;0;riftflux:textures/painting/imported/SkullFrog.png");
            out = forceCustomPaintingEntry(out, "BearPaw", "BearPaw;16;16;0;0;riftflux:textures/painting/imported/BearPaw.png");
            out = forceCustomPaintingEntry(out, "LunarEye", "LunarEye;16;16;0;0;riftflux:textures/painting/imported/LunarEye.png");
            out = forceCustomPaintingEntry(out, "NeonPearl", "NeonPearl;16;16;0;0;riftflux:textures/painting/imported/NeonPearl.png");
            out = forceCustomPaintingEntry(out, "SpinedCoil", "SpinedCoil;16;16;0;0;riftflux:textures/painting/imported/SpinedCoil.png");
            out = forceCustomPaintingEntry(out, "SpiralingSeed", "SpiralingSeed;16;16;0;0;riftflux:textures/painting/imported/SpiralingSeed.png");
            out = forceCustomPaintingEntry(out, "MugShine", "MugShine;16;16;0;0;riftflux:textures/painting/imported/MugShine.png");
            out = forceCustomPaintingEntry(out, "VoidGlyph", "VoidGlyph;16;16;0;0;riftflux:textures/painting/imported/VoidGlyph.png");
            out = forceCustomPaintingEntry(out, "Umbra", "Umbra;16;16;0;0;riftflux:textures/painting/imported/Umbra.png");
            out = forceCustomPaintingEntry(out, "DarkStorm", "DarkStorm;32;32;32;128;riftflux:textures/painting/imported/DarkStorm.png");
            out = forceCustomPaintingEntry(out, "RustRelic", "FrostRelic;32;32;0;0;riftflux:textures/painting/imported/FrostRelic.png");
            out = forceCustomPaintingEntry(out, "MintChip", "MushroomChips;16;16;0;0;riftflux:textures/painting/imported/MushroomChips.png");
            out = forceCustomPaintingEntry(out, "LongSpear", "CreepingFlowers;16;32;0;0;riftflux:textures/painting/imported/CreepingFlowers.png");
            out = forceCustomPaintingEntry(out, "TinyDrum", "PotionShelf;16;16;0;0;riftflux:textures/painting/imported/PotionShelf.png");
            out = forceCustomPaintingEntry(out, "PearlDot", "Oddities;16;16;0;0;riftflux:textures/painting/imported/Oddities.png");
            out = forceCustomPaintingEntry(out, "StoneOrbit", "Toolrack;32;32;0;0;riftflux:textures/painting/imported/Toolrack.png");
            out = forceCustomPaintingEntry(out, "DuskBand", "Dusk;32;16;0;0;riftflux:textures/painting/imported/Dusk.png");
            out = forceCustomPaintingEntry(out, "BronzeLine", "ButterflyCollection;32;16;0;0;riftflux:textures/painting/imported/ButterflyCollection.png");
            out = forceCustomPaintingEntry(out, "IvoryEcho", "EchoesOfLove;32;32;0;0;riftflux:textures/painting/imported/EchoesOfLove.png");
            out = forceCustomPaintingEntry(out, "DriftMark", "SkullMark;32;32;0;0;riftflux:textures/painting/imported/SkullMark.png");
            out = forceCustomPaintingEntry(out, "SkyThread", "Noteboard;32;16;0;0;riftflux:textures/painting/imported/Noteboard.png");
            out = forceCustomPaintingEntry(out, "CopperOrb", "Bloid;32;32;0;0;riftflux:textures/painting/imported/Bloid.png");
            out = forceCustomPaintingEntry(out, "LumenSeed", "Pegboard;16;16;0;0;riftflux:textures/painting/imported/Pegboard.png");
            out = forceCustomPaintingEntry(out, "AmberKnot", "CopperKnot;32;32;0;0;riftflux:textures/painting/imported/CopperKnot.png");
            out = forceCustomPaintingEntry(out, "GlassBloom", "SeedlingBloom;32;32;0;0;riftflux:textures/painting/imported/SeedlingBloom.png");
            out = forceCustomPaintingEntry(out, "MossOrbit", "BrightSpark;32;32;0;0;riftflux:textures/painting/imported/BrightSpark.png");
            out = forceCustomPaintingEntry(out, "CrimsonDew", "UmbralBanner;32;32;0;0;riftflux:textures/painting/imported/UmbralBanner.png");
            out = forceCustomPaintingEntry(out, "RunedBrick", "JollySquid;16;16;0;0;riftflux:textures/painting/imported/JollySquid.png");
            out = forceCustomPaintingEntry(out, "BonePile", "Meeseeks;16;16;32;0;riftflux:textures/painting/imported/Meeseeks.png");
            out = forceCustomPaintingEntry(out, "RedMedal", "TornNote;16;16;48;0;riftflux:textures/painting/imported/TornNote.png");
            out = forceCustomPaintingEntry(out, "TotemDuo", "Shield;16;16;80;0;riftflux:textures/painting/imported/Shield.png");
            out = forceCustomPaintingEntry(out, "NullPocket", "Knives;16;16;96;0;riftflux:textures/painting/imported/Knives.png");
            out = forceCustomPaintingEntry(out, "VineCode", "WineRack;32;16;0;32;riftflux:textures/painting/imported/WineRack.png");
            out = forceCustomPaintingEntry(out, "WireRest", "Axe;32;16;32;32;riftflux:textures/painting/imported/Axe.png");
            out = forceCustomPaintingEntry(out, "HookLoop", "Utensils;32;16;64;32;riftflux:textures/painting/imported/Utensils.png");
            out = forceCustomPaintingEntry(out, "CyanPort", "ForestDawn;32;16;96;32;riftflux:textures/painting/imported/ForestDawn.png");
            out = forceCustomPaintingEntry(out, "DustSlope", "Dust;32;16;128;32;riftflux:textures/painting/imported/Dust.png");
            out = forceCustomPaintingEntry(out, "AtlasBannerA", "AtlasBannerGreen;16;32;0;64;riftflux:textures/painting/imported/AtlasBannerGreen.png");
            out = forceCustomPaintingEntry(out, "AtlasBannerB", "AtlasBannerBlue;16;32;16;64;riftflux:textures/painting/imported/AtlasBannerBlue.png");
            out = forceCustomPaintingEntry(out, "AtlasPurpleRange", "Celestial;32;32;64;128;riftflux:textures/painting/imported/Celestial.png");
            out = forceCustomPaintingEntry(out, "AtlasFlowerSky", "Flowering;32;32;96;128;riftflux:textures/painting/imported/Flowering.png");
            out = forceCustomPaintingEntry(out, "AtlasCrossedBlades", "CrossedBlades;32;32;128;128;riftflux:textures/painting/imported/CrossedBlades.png");
            out = forceCustomPaintingEntry(out, "AtlasBladeTrio", "AtlasBlades;32;32;160;128;riftflux:textures/painting/imported/AtlasBlades.png");
            out = forceCustomPaintingEntry(out, "AtlasMountainVista", "SummitCliff;64;48;192;64;riftflux:textures/painting/imported/SummitCliff.png");
            out = forceCustomPaintingEntry(out, "AtlasForestPath", "ForestPath;64;48;192;112;riftflux:textures/painting/imported/ForestPath.png");
            out = forceCustomPaintingEntry(out, "AtlasHarborSun", "Harbor;64;64;0;192;riftflux:textures/painting/imported/Harbor.png");
            out = forceCustomPaintingEntry(out, "AtlasLighthouse", "Lighthouse;64;64;64;192;riftflux:textures/painting/imported/Lighthouse.png");
            out = forceCustomPaintingEntry(out, "AtlasDuskTwin", "DarkStorm;32;32;32;128;riftflux:textures/painting/imported/DarkStorm.png");
            out = forceCustomPaintingEntry(out, "AtlasDarkRight", "DarkStorm;32;32;32;128;riftflux:textures/painting/imported/DarkStorm.png");
            out = forceCustomPaintingEntry(out, "AtlasDark", "DarkForest;32;32;0;128;riftflux:textures/painting/imported/DarkForest.png");
            out = forceCustomPaintingEntry(out, "Import_1105242611714886714", "FrostRelic;32;32;0;0;riftflux:textures/painting/imported/FrostRelic.png");
            out = forceCustomPaintingEntry(out, "Gallery01", "FrostRelic;32;32;0;0;riftflux:textures/painting/imported/FrostRelic.png");
            out = forceCustomPaintingEntry(out, "Import_15898581183307818233", "BlueStamp;16;16;0;0;riftflux:textures/painting/imported/BlueStamp.png");
            out = forceCustomPaintingEntry(out, "Gallery02", "BlueStamp;16;16;0;0;riftflux:textures/painting/imported/BlueStamp.png");
            out = forceCustomPaintingEntry(out, "Import_5707704980356058947", "CinderBloom;32;32;0;0;riftflux:textures/painting/imported/CinderBloom.png");
            out = forceCustomPaintingEntry(out, "Gallery03", "CinderBloom;32;32;0;0;riftflux:textures/painting/imported/CinderBloom.png");
            out = forceCustomPaintingEntry(out, "Import_8390554114962693927", "MushroomChips;16;16;0;0;riftflux:textures/painting/imported/MushroomChips.png");
            out = forceCustomPaintingEntry(out, "Gallery04", "MushroomChips;16;16;0;0;riftflux:textures/painting/imported/MushroomChips.png");
            out = forceCustomPaintingEntry(out, "Import_2453731557530369994", "CreepingFlowers;16;32;0;0;riftflux:textures/painting/imported/CreepingFlowers.png");
            out = forceCustomPaintingEntry(out, "Gallery05", "CreepingFlowers;16;32;0;0;riftflux:textures/painting/imported/CreepingFlowers.png");
            out = forceCustomPaintingEntry(out, "Import_15686945115718463924", "CrackedSun;32;32;0;0;riftflux:textures/painting/imported/CrackedSun.png");
            out = forceCustomPaintingEntry(out, "Gallery06", "CrackedSun;32;32;0;0;riftflux:textures/painting/imported/CrackedSun.png");
            out = forceCustomPaintingEntry(out, "Import_14926847136614811964", "PotionShelf;16;16;0;0;riftflux:textures/painting/imported/PotionShelf.png");
            out = forceCustomPaintingEntry(out, "Gallery07", "PotionShelf;16;16;0;0;riftflux:textures/painting/imported/PotionShelf.png");
            out = forceCustomPaintingEntry(out, "Import_8437322785318950020", "FrostGlyph;32;32;0;0;riftflux:textures/painting/imported/FrostGlyph.png");
            out = forceCustomPaintingEntry(out, "Gallery08", "FrostGlyph;32;32;0;0;riftflux:textures/painting/imported/FrostGlyph.png");
            out = forceCustomPaintingEntry(out, "Import_14961980454791572902", "GoldMoss;32;32;0;0;riftflux:textures/painting/imported/GoldMoss.png");
            out = forceCustomPaintingEntry(out, "Gallery09", "GoldMoss;32;32;0;0;riftflux:textures/painting/imported/GoldMoss.png");
            out = forceCustomPaintingEntry(out, "Import_3038250295555765778", "Oddities;16;16;0;0;riftflux:textures/painting/imported/Oddities.png");
            out = forceCustomPaintingEntry(out, "Gallery10", "Oddities;16;16;0;0;riftflux:textures/painting/imported/Oddities.png");
            out = forceCustomPaintingEntry(out, "Import_14853050705435720726", "Toolrack;32;32;0;0;riftflux:textures/painting/imported/Toolrack.png");
            out = forceCustomPaintingEntry(out, "Gallery11", "Toolrack;32;32;0;0;riftflux:textures/painting/imported/Toolrack.png");
            out = forceCustomPaintingEntry(out, "Import_13494592494013951062", "Dusk;32;16;0;0;riftflux:textures/painting/imported/Dusk.png");
            out = forceCustomPaintingEntry(out, "Gallery12", "Dusk;32;16;0;0;riftflux:textures/painting/imported/Dusk.png");
            out = forceCustomPaintingEntry(out, "Import_10433218889319366305", "ButterflyCollection;32;16;0;0;riftflux:textures/painting/imported/ButterflyCollection.png");
            out = forceCustomPaintingEntry(out, "Gallery14", "ButterflyCollection;32;16;0;0;riftflux:textures/painting/imported/ButterflyCollection.png");
            out = forceCustomPaintingEntry(out, "Import_3316100501211956769", "EchoesOfLove;32;32;0;0;riftflux:textures/painting/imported/EchoesOfLove.png");
            out = forceCustomPaintingEntry(out, "Gallery15", "EchoesOfLove;32;32;0;0;riftflux:textures/painting/imported/EchoesOfLove.png");
            out = forceCustomPaintingEntry(out, "Import_1405110628700110104", "SkullMark;32;32;0;0;riftflux:textures/painting/imported/SkullMark.png");
            out = forceCustomPaintingEntry(out, "Gallery16", "SkullMark;32;32;0;0;riftflux:textures/painting/imported/SkullMark.png");
            out = forceCustomPaintingEntry(out, "Import_3980202720619743710", "Noteboard;32;16;0;0;riftflux:textures/painting/imported/Noteboard.png");
            out = forceCustomPaintingEntry(out, "Gallery17", "Noteboard;32;16;0;0;riftflux:textures/painting/imported/Noteboard.png");
            out = forceCustomPaintingEntry(out, "Import_12002261695906088408", "PondPixel;16;16;0;0;riftflux:textures/painting/imported/PondPixel.png");
            out = forceCustomPaintingEntry(out, "Gallery18", "PondPixel;16;16;0;0;riftflux:textures/painting/imported/PondPixel.png");
            out = forceCustomPaintingEntry(out, "Import_4865533768723008097", "HushBloom;32;32;0;0;riftflux:textures/painting/imported/HushBloom.png");
            out = forceCustomPaintingEntry(out, "Gallery19", "HushBloom;32;32;0;0;riftflux:textures/painting/imported/HushBloom.png");
            out = forceCustomPaintingEntry(out, "Import_3537128328782683102", "Bloid;32;32;0;0;riftflux:textures/painting/imported/Bloid.png");
            out = forceCustomPaintingEntry(out, "Gallery20", "Bloid;32;32;0;0;riftflux:textures/painting/imported/Bloid.png");
            out = forceCustomPaintingEntry(out, "Import_10789327830195471405", "VelvetRune;32;32;0;0;riftflux:textures/painting/imported/VelvetRune.png");
            out = forceCustomPaintingEntry(out, "Gallery21", "VelvetRune;32;32;0;0;riftflux:textures/painting/imported/VelvetRune.png");
            out = forceCustomPaintingEntry(out, "Import_943304298191828284", "stylized.png;32;32;0;0;riftflux:textures/painting/imported/stylized.png");
            out = forceCustomPaintingEntry(out, "Gallery22", "stylized.png;32;32;0;0;riftflux:textures/painting/imported/stylized.png");
            out = forceCustomPaintingEntry(out, "styalized.png", "stylized.png;32;32;0;0;riftflux:textures/painting/imported/stylized.png");
            out = forceCustomPaintingEntry(out, "stylized.png", "stylized.png;32;32;0;0;riftflux:textures/painting/imported/stylized.png");
            out = forceCustomPaintingEntry(out, "Import_4131215028221111236", "Pegboard;16;16;0;0;riftflux:textures/painting/imported/Pegboard.png");
            out = forceCustomPaintingEntry(out, "Gallery23", "Pegboard;16;16;0;0;riftflux:textures/painting/imported/Pegboard.png");
            out = forceCustomPaintingEntry(out, "Import_11795424469239502756", "CopperKnot;32;32;0;0;riftflux:textures/painting/imported/CopperKnot.png");
            out = forceCustomPaintingEntry(out, "Gallery24", "CopperKnot;32;32;0;0;riftflux:textures/painting/imported/CopperKnot.png");
            out = forceCustomPaintingEntry(out, "Import_492653515534584664", "SeedlingBloom;32;32;0;0;riftflux:textures/painting/imported/SeedlingBloom.png");
            out = forceCustomPaintingEntry(out, "Gallery25", "SeedlingBloom;32;32;0;0;riftflux:textures/painting/imported/SeedlingBloom.png");
            out = forceCustomPaintingEntry(out, "Import_8680897047976471280", "KelpSpark;32;32;0;0;riftflux:textures/painting/imported/KelpSpark.png");
            out = forceCustomPaintingEntry(out, "Gallery26", "KelpSpark;32;32;0;0;riftflux:textures/painting/imported/KelpSpark.png");
            out = forceCustomPaintingEntry(out, "Import_17468346436947212311", "BrightSpark;32;32;0;0;riftflux:textures/painting/imported/BrightSpark.png");
            out = forceCustomPaintingEntry(out, "Gallery27", "BrightSpark;32;32;0;0;riftflux:textures/painting/imported/BrightSpark.png");
            out = forceCustomPaintingEntry(out, "Import_3583122789790429381", "FogToken;32;32;0;0;riftflux:textures/painting/imported/FogToken.png");
            out = forceCustomPaintingEntry(out, "Gallery28", "FogToken;32;32;0;0;riftflux:textures/painting/imported/FogToken.png");
            out = forceCustomPaintingEntry(out, "Import_2103629306946470516", "UmbralBanner;32;32;0;0;riftflux:textures/painting/imported/UmbralBanner.png");
            out = forceCustomPaintingEntry(out, "Gallery29", "UmbralBanner;32;32;0;0;riftflux:textures/painting/imported/UmbralBanner.png");
            out = forceCustomPaintingEntry(out, "Import_9464401474315549901", "SoftRibbon;32;16;0;0;riftflux:textures/painting/imported/SoftRibbon.png");
            out = forceCustomPaintingEntry(out, "Gallery30", "SoftRibbon;32;16;0;0;riftflux:textures/painting/imported/SoftRibbon.png");
            out = forceCustomPaintingEntry(out, "Import_14899282787076458166", "WarmEcho;32;16;0;0;riftflux:textures/painting/imported/WarmEcho.png");
            out = forceCustomPaintingEntry(out, "Gallery31", "WarmEcho;32;16;0;0;riftflux:textures/painting/imported/WarmEcho.png");
            out = forceCustomPaintingEntry(out, "Import_13680866727003923712", "NorthGlyph;16;16;0;0;riftflux:textures/painting/imported/NorthGlyph.png");
            out = forceCustomPaintingEntry(out, "Gallery32", "NorthGlyph;16;16;0;0;riftflux:textures/painting/imported/NorthGlyph.png");
            out = forceCustomPaintingEntry(out, "Import_82306519022149241", "SilentStem;16;32;0;0;riftflux:textures/painting/imported/SilentStem.png");
            out = forceCustomPaintingEntry(out, "Gallery33", "SilentStem;16;32;0;0;riftflux:textures/painting/imported/SilentStem.png");
            out = forceCustomPaintingEntry(out, "Import_8145294216501436008", "StarrySky;96;64;0;0;riftflux:textures/painting/StarrySky.png");
            out = forceCustomPaintingEntry(out, "Import_10083096985915292797", "pack.png;16;16;0;0;riftflux:textures/painting/pack.png");
            out = forceCustomPaintingEntry(out, "Import_1061680030994392119", "StarHaze;96;64;0;0;riftflux:textures/painting/StarHaze.png");
            out = forceCustomPaintingEntry(out, "Import_5396209286604440417", "JollySquid;16;16;0;0;riftflux:textures/painting/imported/JollySquid.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-11629597661984161539.png", "SnowCoral;16;16;0;0;riftflux:textures/painting/imported/SnowCoral.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-10933815052753999217.png", "CreepyMint;16;16;0;0;riftflux:textures/painting/imported/CreepyMint.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-18270552977104938692.png", "PuppyHeart;16;16;0;0;riftflux:textures/painting/imported/PuppyHeart.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-15568491389411106162.png", "SkullFrog;16;16;0;0;riftflux:textures/painting/imported/SkullFrog.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-9713999812205739366.png", "BearPaw;16;16;0;0;riftflux:textures/painting/imported/BearPaw.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-18087994978578207719.png", "LunarEye;16;16;0;0;riftflux:textures/painting/imported/LunarEye.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-5515462690562765049.png", "NeonPearl;16;16;0;0;riftflux:textures/painting/imported/NeonPearl.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-8108177091012945282.png", "SpinedCoil;16;16;0;0;riftflux:textures/painting/imported/SpinedCoil.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-13399677967542943283.png", "SpiralingSeed;16;16;0;0;riftflux:textures/painting/imported/SpiralingSeed.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-629525076553057384.png", "MugShine;16;16;0;0;riftflux:textures/painting/imported/MugShine.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-6247862312713638472.png", "VoidGlyph;16;16;0;0;riftflux:textures/painting/imported/VoidGlyph.png");
            out = forceCustomPaintingEntry(out, "ai-chat-attachment-1254734670699394308.png", "Umbra;16;16;0;0;riftflux:textures/painting/imported/Umbra.png");
            out = removeCustomPaintingEntry(out, "AtlasCompass");
            out = removeCustomPaintingEntry(out, "AtlasPillar");
            out = removeCustomPaintingEntry(out, "AtlasSnowReach");
            out = removeCustomPaintingEntry(out, "CrownPath");
            return out;
        } finally {
            allowCustomPaintingInsert = previousAllow;
        }
    }

    // parse the string of IDs into a set
    private static void parseDisabledPotionIds(String raw) {
        disabledPotionIdsSet.clear();
        if (raw == null) {
            return;
        }

        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        String[] parts = trimmed.split("[,; ]+"); // commas / semicolons / spaces
        for (String part : parts) {
            try {
                int id = Integer.parseInt(part.trim());
                disabledPotionIdsSet.add(id);
            } catch (NumberFormatException ignored) {
                // ignore invalid entries
            }
        }
    }

    // helper used by the mixin
    public static boolean isPotionIdDisabled(int id) {
        return disabledPotionIdsSet.contains(id);
    }

}

