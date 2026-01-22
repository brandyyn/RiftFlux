package com.voidsrift.riftflux;

import net.minecraftforge.common.config.Configuration;
import java.util.HashSet;
import java.util.Set;
import java.io.File;

public class ModConfig {

    public static Configuration config;

    public static boolean enableChromatiCraftMixin;

    public static boolean DisableAether2Portal;

    public static boolean hasSound;


    public static boolean playerOnlyHurtSound;
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

    public static void init(File file){
        config = new Configuration(file);
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

        enableArmorMixin = config.getBoolean("EnableArmorMixin","general",true,"Enable mixin scaling armor protections");

        changeArmorBarAmount = config.getBoolean("ChangeArmorBarAmount","general",true,
                "Enable mixin that changes armor bar displayed amount, may affect other things");
        protectionMultiplier = config.getFloat("protectionMultiplier","general",0.5F,0.0F,1F,"Scales armor's protection(1.0 is unchanged armor)");

        enableChestLaunch = config.getBoolean("EnableChestLaunch","general", true,
                "Launch entities standing on top when a chest opens.");

        chestLaunchHorizontal = (float) config.get("general", "ChestLaunchHorizontal", 5,
                "Horizontal push strength when a chest opens. (no bounds)").getDouble(5);

        chestLaunchUpward = (float) config.get("general", "ChestLaunchUpward", 1,
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
                true,
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
        config.save();
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