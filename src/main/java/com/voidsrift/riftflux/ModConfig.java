package com.voidsrift.riftflux;

import com.voidsrift.riftflux.util.ConfigResolver;
import com.voidsrift.riftflux.world.MoonPhaseHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.io.File;
import makamys.mclib.config.item.BackpackConfigHelper;
import com.voidsrift.riftflux.dualhotbar.DualHotbarConfig;

public class ModConfig {
    private static final String PALARIA_CATEGORY = "palaria";
    private static final String MOB_SPAWNING_CATEGORY = "mobspawning";
    private static final String POST_PROCESSING_CATEGORY = "post processing";
    private static final String CLAY_SOLDIERS_CATEGORY = "claysoldiers";
    private static final String[] DEFAULT_MOB_SPAWN_WHITELIST = new String[]{
            "riftflux.DemonEye|300|1-3|0",
            "riftflux.Cyclops|77|1|0",
            "riftflux.FlowerMan|330|1-2|0|forest,magical,river|Flower Forest,Forest,wheatfield",
            "riftflux.EnderTroll|150|1|0|forest,coniferous|wheatfield",
            "riftflux.Jaxx|150|1|0|forest,coniferous|wheatfield",
            "riftflux.BlackWidow|450|1-2|0|forest,coniferous|wheatfield",
            "riftflux.PumpkinZombie|450|3-5|0||wheatfield",
            "riftflux.PumpkinSkeleton|450|2-4|0||wheatfield",
            "riftflux.PumpkinCreeper|250|1-3|0||wheatfield",
            "riftflux.Creeptile|150|2-3|0||Extreme Hills,Extreme Hills Edge,Jungle,JungleHills",
            "riftflux.RaptorChicken|150|4-6|0||Extreme Hills,Extreme Hills Edge,Jungle,JungleHills",
            "riftflux.Cowasaurus|150|2-3|0||Extreme Hills,Extreme Hills Edge,Jungle,JungleHills",
            "riftflux.EnderWalker|150|2-5|0,1",
            "riftflux.Nimatin|88|1|0||Extreme Hills,Extreme Hills Edge,Jungle,JungleHills",
            "riftflux.EnderRaptorChicken|150|1|1,0||Sky",
            "riftflux.MagmaRaptorChicken|150|1|-1",
            "riftflux.Axolotl|330|1-3|0|swamp,river",
            "riftflux.duck|660|3-4|0|river",
            "riftflux.quackling|440|1-2|0|swamp,river",
            "riftflux.soot_sprite|800|4-8|0|magical,spooky"
    };
    private static final String[] DEFAULT_MOB_SPAWN_BLACKLIST = new String[]{
            "riftflux.Axolotl|||0|snowy",
            "riftflux.duck|||0|snowy",
            "riftflux.quackling|||0|snowy"
    };
    private static final String[] DEFAULT_MOB_SPAWN_WHITELIST_ONLY_BIOMES = new String[0];
    private static final Map<String, String> RIFTFLUX_NATURAL_MOB_CONFIG_IDS = buildRiftFluxNaturalMobConfigIds();
    private static final String[] DEFAULT_JUKEBOX_TRACK_TIMINGS = new String[]{
            "13=178",
            "cat=185",
            "blocks=345",
            "chirp=185",
            "far=174",
            "mall=197",
            "mellohi=96",
            "stal=150",
            "strad=188",
            "ward=251",
            "11=71",
            "wait=238"
    };

    public static Configuration config;
    private static final int VANILLA_POTION_ID_SLOWNESS = 2;
    private static final int VANILLA_POTION_ID_CONFUSION = 9;
    private static final int VANILLA_POTION_ID_REGENERATION = 10;
    private static final int VANILLA_POTION_ID_RESISTANCE = 11;
    private static final int VANILLA_POTION_ID_FIRE_RESISTANCE = 12;
    private static final int VANILLA_POTION_ID_INVISIBILITY = 14;

    public static boolean enableChromatiCraftMixin;

    public static boolean DisableAether2Portal;

    public static boolean hasSound;


    public static boolean playerOnlyHurtSound;
    public static double playerOnlyHurtSoundOofChance;
    public static boolean otherPlayersOOF;
    public static boolean hasShader;

    public static boolean disableStrataVents;

    public static boolean disableStrataOreVeins;
    public static boolean fixGeoStrataCrystalSpikeWaterlogging;
    public static boolean enableGeoStrataCeilingCrystalSpikes;
    public static boolean enableGeoStrataWallCrystalSpikes;
    public static boolean enableGeoStrataCrystalSpikeMatchedHitboxes;
    public static boolean stabilizeGeoStrataDecoGenShapes;
    public static boolean disableGeoStrataCrystalSpikeHeightDarkening;
    public static int geoStrataCrystalSpikeTextureBrightnessPercent;
    public static boolean disableGeoStrataCrystalSpikeFullbright;
    public static boolean disableDragonAPILogging;
    public static boolean optimizeDragonAPIBlockRenderFastPaths;
    public static boolean optimizeDragonAPIEntityRenderLoopFastPaths;
    public static boolean optimizeDragonAPIParticleRenderFastPaths;
    public static boolean optimizeChromatiCraftRenderEventFastPaths;
    public static boolean optimizeChromatiCraftCliffsChunkGeneration;
    public static boolean disableChromatiCraftItemFabricator;
    public static boolean chromatiCraftNetherBedrockBreakableLikeObsidian;
    public static boolean chromatiCraftNetherStructureShieldBreakableLikeObsidian;
    public static boolean chromatiCraftNetherStructureHutEnabled;
    public static boolean chromatiCraftNetherStructureTempleEnabled;
    public static boolean chromatiCraftNetherStructureMazeEnabled;
    public static boolean chromatiCraftNetherStructureSpiralEnabled;
    public static boolean chromatiCraftNetherStructureDioramaEnabled;
    public static boolean chromatiCraftNetherHolesEnabled;
    public static boolean chromatiCraftNetherLavaRiversEnabled;
    public static int chromatiCraftNetherStructureHutY;
    public static int chromatiCraftNetherStructureTempleY;
    public static int chromatiCraftNetherStructureMazeY;
    public static int chromatiCraftNetherStructureSpiralY;
    public static int chromatiCraftNetherStructureDioramaY;
    public static int chromatiCraftNetherHolesY;
    public static int chromatiCraftNetherLavaRiverMinY;
    public static int chromatiCraftNetherLavaRiverMaxY;
    public static boolean disableThermalDynamicsFacades;
    public static boolean fixChocolateQuestDivideByZero;
    public static boolean hideChocolateQuestGeneratingStructureOverlay;
    public static boolean thaumcraftOnlyWarpResearchRequiresMinigame;

    public static boolean enableArmorMixin;

    public static boolean changeArmorBarAmount;

    public static float protectionMultiplier;

    public static boolean enableChestLaunch;
    public static float chestLaunchHorizontal;
    public static float chestLaunchUpward;

    public static boolean enableFullExplosionDrops;
    public static boolean explosionsIgnoreThinPlantsForExposure;

    public static boolean enableMeleeDamageTooltip;
    public static boolean enableUniversalDurabilityTooltip;

    private static Map<String, String> buildRiftFluxNaturalMobConfigIds() {
        HashMap<String, String> ids = new HashMap<String, String>();
        ids.put("demoneye", "riftflux.DemonEye");
        ids.put("cyclops", "riftflux.Cyclops");
        ids.put("flowerman", "riftflux.FlowerMan");
        ids.put("endertroll", "riftflux.EnderTroll");
        ids.put("jaxx", "riftflux.Jaxx");
        ids.put("blackwidow", "riftflux.BlackWidow");
        ids.put("pumpkinzombie", "riftflux.PumpkinZombie");
        ids.put("pumpkinskeleton", "riftflux.PumpkinSkeleton");
        ids.put("pumpkincreeper", "riftflux.PumpkinCreeper");
        ids.put("creeptile", "riftflux.Creeptile");
        ids.put("raptorchicken", "riftflux.RaptorChicken");
        ids.put("cowasaurus", "riftflux.Cowasaurus");
        ids.put("enderwalker", "riftflux.EnderWalker");
        ids.put("nimatin", "riftflux.Nimatin");
        ids.put("enderraptorchicken", "riftflux.EnderRaptorChicken");
        ids.put("magmaraptorchicken", "riftflux.MagmaRaptorChicken");
        ids.put("axolotl", "riftflux.Axolotl");
        ids.put("duck", "riftflux.duck");
        ids.put("quackling", "riftflux.quackling");
        ids.put("sootsprite", "riftflux.soot_sprite");
        return ids;
    }

    // Combat tweaks
    public static boolean enableFistDamageBoost;
    public static float fistDamageAmount;
    public static boolean enableStickDamageBonus;
    public static float stickDamageBonus;
    public static boolean enableThornsArmorTweaks;
    public static float vanillaArrowInaccuracy;
    public static float riftExplorerSpecialArrowInaccuracy;
    public static float riftExplorerDartInaccuracy;
    public static float riftExplorerPebbleInaccuracy;
    public static boolean enableToroHealthModule;
    public static boolean toroHealthShowDamageParticles;
    public static boolean toroHealthShowThroughWalls;
    public static float toroHealthParticleSize;
    public static int toroHealthHealColor;
    public static int toroHealthDamageColor;

    public static boolean reworkVillageGolems;
    public static int initialVillageGolems;
    public static boolean disableWitchingGadgetsVillageHouseGeneration;

    public static boolean disableSleepRainClear;
    public static boolean jukeboxAutoLoopEnabled;
    public static int jukeboxLoopDelaySeconds;
    public static int jukeboxUnknownTrackLoopAfterSeconds;
    public static String[] jukeboxTrackTimings;
    public static boolean jukeboxRedstoneRestartEnabled;
    public static boolean pulseLockedHoppers;

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
    public static boolean enableFenceTextureModule;
    public static boolean disableFencePumpkinConnections;
    public static boolean dualHotbarShowMountedHealth;
    public static boolean dualHotbarUseCustomMountOnboardPrompt;
    public static boolean enableJackOLanternHelmet;
    public static boolean disablePumpkinOverlay;
    public static boolean centerCrosshair;
    public static boolean disableUnderwaterOverlay;
    public static boolean fixUnderwaterMobDarkening;
    public static boolean enablePostProcessing;
    public static boolean postProcessConfigHotSwap;
    public static int[] postProcessDimensionWhitelist;
    public static int[] postProcessDimensionBlacklist;
    public static int[] postProcessCelestialBloomDimensionWhitelist;
    public static int[] postProcessCelestialBloomDimensionBlacklist;
    public static float postProcessGamma;
    public static float postProcessBrightness;
    public static float postProcessContrast;
    public static float postProcessExposure;
    public static float postProcessSunExposureCompensationPercent;
    public static float postProcessMoonExposureCompensationPercent;
    public static float postProcessSunEventExposureCompensationPercent;
    public static float postProcessMoonEventExposureCompensationPercent;
    public static float postProcessSaturationPercent;
    public static float postProcessRedMultiplier;
    public static float postProcessGreenMultiplier;
    public static float postProcessBlueMultiplier;
    public static float postProcessColorGradeShadowProtection;
    public static float postProcessBloomStrengthPercent;
    public static float postProcessBloomThreshold;
    public static float postProcessBloomRadiusPixels;
    public static float postProcessBloomResolutionPercent;
    public static boolean enablePostProcessWorldBloomCache;
    public static boolean enablePostProcessCelestialBloomCache;
    public static boolean postProcessBloomCacheHighPrecision;
    public static boolean postProcessBloomCacheUsePackedFormat;
    public static boolean enablePostProcessPersistentRenderTarget;
    public static boolean postProcessBloomAffectsHeldItem;
    public static float postProcessCelestialBloomStrengthPercent;
    public static float postProcessCelestialBloomThreshold;
    public static float postProcessCelestialBloomRadiusPixels;
    public static int postProcessCelestialBloomStartTime;
    public static int postProcessCelestialBloomEndTime;
    public static boolean enableCelestialEventTextures;
    public static float celestialSunEventChance;
    public static float celestialMoonEventChance;
    public static String[] celestialSunEventTextures;
    public static String[] celestialMoonEventTextures;
    public static String celestialSunEventTexture;
    public static String celestialMoonEventTexture;
    public static boolean celestialFullSunriseSunsetTint;
    public static boolean celestialFogMatchesSky;
    public static boolean celestialBetaStyleFogBiomeTint;
    public static boolean celestialBetaStyleFogBiomeTintWeatherEvent;
    public static boolean celestialFogChanceEventsUseSkyMatchingFog;
    public static float celestialBetaStyleFogBiomeTintWeatherEventChancePercent;
    public static float celestialBetaStyleFogBiomeTintDayEventChancePercent;
    public static float celestialBetaStyleFogBiomeTintNightEventChancePercent;
    public static int[] celestialFogHorizonDimensionWhitelist;
    public static int[] celestialFogHorizonDimensionBlacklist;
    public static int[] celestialFogChanceEventDimensionWhitelist;
    public static int[] celestialFogChanceEventDimensionBlacklist;
    public static boolean celestialBlackNightFog;
    public static float celestialFogDesaturationPercent;
    public static float celestialNightFogDesaturationPercent;
    public static boolean celestialFogDistanceGradient;
    public static float celestialFogDistanceGradientStrengthPercent;
    public static float celestialFogDistanceGradientStartPercent;
    public static float celestialFogDistanceGradientEndPercent;
    public static int celestialVoidFogStartHeight;
    public static int celestialVoidParticleStartHeight;
    public static boolean betaStarsEnabled;
    public static int betaStarsCount;
    public static float betaStarsSizeMultiplier;
    public static boolean betaStarsRandomPatternEveryNight;
    public static boolean betaStarsRandomBlink;
    public static float betaStarsTwinkleSpeedMultiplier;
    public static int betaStarsSunsetFadeStartTick;
    public static int betaStarsSunsetFadeEndTick;
    public static boolean betaStarsSpinWithSunMoon;
    public static boolean betaStarsDisableBetterSkiesStars;
    public static boolean suppressTwilightForestStars;

    // Zelda
    public static boolean zeldaHeartsEnabled;
    public static boolean zeldaDisableRegen;
    public static int zeldaHeartPieceRarity;
    public static int zeldaStartingHearts;
    public static int zeldaMaximumHearts;
    public static String[] zeldaHeartContainerDropMobIds;
    public static boolean zeldaHeartContainerFirstKillOnly;
    public static int zeldaMobDrop;
    public static int zeldaBlockDrop;

    // DSS (Darwin Sprint System)
    public static boolean dssEnabled;
    public static double dssMaxSprintingTimeSeconds;
    public static double dssOverchargeRegenTimeSeconds;
    public static boolean dssEnableEnchantments;
    public static int dssEnchantmentMaxStaminaId;
    public static int dssEnchantmentStaminaRegenId;
    public static int dssEnchantmentOverloadReductionId;
    public static double dssEnchantmentMaxStaminaSeconds;
    public static double dssEnchantmentStaminaRegenSeconds;
    public static double dssEnchantmentOverloadReductionSeconds;
    public static boolean dssEnablePotionEffects;
    public static String dssPotionExpandMaxStaminaIds;
    public static String dssPotionReduceMaxStaminaIds;
    public static String dssPotionReduceRegenIds;
    public static String dssPotionRegenSpeedIds;
    public static double dssPotionExpandMaxStaminaMultiplier;
    public static double dssPotionReduceMaxStaminaMultiplier;
    public static double dssPotionReduceRegenMultiplier;
    public static double dssPotionRegenSpeedMultiplier;
    public static int dssBarSize;
    public static int dssBarOffsetX;
    public static int dssBarOffsetY;
    public static double dssBarTransparencyPercent;
    public static boolean enableLevelUpModule;
    public static boolean levelUpAllowHud;
    public static boolean levelUpRenderHudTopLeft;
    public static boolean levelUpRenderHudExpBar;
    public static boolean levelUpChangeFovWithSpeed;
    public static boolean levelUpRegisterTalismanOfWonder;
    public static boolean levelUpEnableUnlearningBook;
    public static boolean levelUpEnableLegacyRecipes;
    public static boolean levelUpEnableItemRecipes;
    public static int levelUpUnlearningBookDungeonLootWeight;
    public static boolean levelUpEarnSkillPointsBeforeClassChoice;
    public static boolean levelUpUnlearningBookResetClass;
    public static String[] levelUpFarmingBlacklist;
    public static int levelUpMaxPointsPerSkill;
    public static int levelUpBonusPointsForClasses;
    public static double levelUpXpGainPerLevel;
    public static int levelUpSkillPointsLostOnDeathPercent;
    public static boolean levelUpUseOldSpeedDirtAndGravelDigging;
    public static boolean levelUpUseOldSpeedRedstoneBreaking;
    public static boolean levelUpResetPlayerClassOnDeath;
    public static boolean levelUpPreventDuplicatedOresPlacing;
    public static boolean levelUpAddBonusXpOnCraft;
    public static boolean levelUpAddBonusXpOnMining;
    public static boolean levelUpAddXpOnCraftingSomeItems;
    public static boolean levelUpAddXpOnMiningSomeOre;
    public static boolean levelUpAddBonusXpOnFighting;
    public static float levelUpHudPulseSpeedHz;
    public static boolean levelUpEnableMeleeSneakAttackBonusDamage;
    public static boolean levelUpEnableRangedSneakAttackBonusDamage;
    public static float levelUpMeleeSneakAttackDamageMultiplier;
    public static float levelUpRangedSneakAttackDamageMultiplier;

    // Blessings
    public static boolean blessingsEnabled;
    public static boolean blessingsGrantOnFirstJoin;
    public static boolean blessingsAnnounceFirstJoinBlessing;
    public static boolean blessingsAllowInfernoOnFirstJoin;
    public static boolean blessingsPillarGenEnabled;
    public static int blessingsPillarGenChance;
    public static int blessingsPillarMaxPerChunk;
    public static int artifactActiveLightLevel;
    public static boolean artifactExclusiveActivation;
    public static int blessingNinjaInvisCooldownSeconds;
    public static float blessingVampireHealPercent;
    public static boolean loseBlessingOnArtifactBreak;
    public static boolean artifactActivationAroundMonsters;
    public static boolean loseBlessingOnDeath;
    public static String[] blessingThiefDropEntries;
    public static int[] blessingAlchemistPotionIds;
    public static int[] blessingDrunkNegativePotionIds;
    public static String[] blessingsDisabledList;

    // Natural mob spawning
    public static String initialMoonPhase;
    public static int initialMoonPhaseIndex;
    public static String[] hostileMobSpawnDisabledMoonPhases;
    public static boolean[] hostileMobSpawnDisabledMoonPhaseFlags;
    public static int hostileMobSpawnGracePeriodDays;
    public static String[] mobSpawnWhitelist;
    public static String[] mobSpawnBlacklist;
    public static String[] mobSpawnWhitelistOnlyBiomes;
    public static boolean useSpawnTypeForMobCap;

    // Avatar
    public static boolean enableAvatarModule;
    public static boolean gliderDyeRecipes;
    public static boolean gliderStackable;
    public static boolean gliderUseItemInHand;
    public static boolean enableGliderHoldAltitude;
    public static boolean blockEtFuturumElytraWhileAvatarGliding;
    public static boolean appaRequireTameToRide;
    public static boolean appaRestrictRideToOwner;
    public static boolean appaAllowMobPassengers;
    public static boolean appaMobPassengerWhitelistMode;
    public static String[] appaMobPassengerEntityFilter;
    public static float appaMovementSpeed;

    // Terraria module
    public static boolean enableTerraModule;
    public static float demonEyeHealth;
    public static float eyeOfCthulhuHealth;
    public static int eyeOfCthulhuExperience;
    public static float lensDropChance;
    public static float blackLensDropChance;
    public static String[] eyeOfCthulhuDrops;
    public static boolean eyeOfCthulhuMusicEnabled;
    public static int eyeOfCthulhuDemonEyeWaveCount;
    public static int eyeOfCthulhuDemonEyeWaveIntervalSeconds;
    public static int eyeOfCthulhuDemonEyeCap;
    public static int eyeOfCthulhuDespawnNoPlayerDelaySeconds;
    public static int eyeOfCthulhuDespawnNoPlayerChunkRadius;
    public static int eyeOfCthulhuDespawnNoPlayerRadius;
    public static boolean destroyerEnabled;
    public static boolean destroyerMusicEnabled;
    public static float destroyerHealth;
    public static int destroyerExperience;
    public static int destroyerSegmentCount;
    public static float destroyerSegmentDistance;
    public static float destroyerDiveDepth;
    public static int destroyerDiveCycleTicks;
    public static float destroyerArmoredHealthThreshold;
    public static float destroyerHeadContactDamage;
    public static float destroyerHeadContactHealthPercent;
    public static float destroyerBodyContactDamage;
    public static float destroyerBodyContactHealthPercent;
    public static float destroyerProbeContactDamage;
    public static float destroyerProbeContactHealthPercent;
    public static float destroyerHeadLaserDamage;
    public static float destroyerHeadLaserMaxHealthPercent;
    public static float destroyerBodyLaserDamage;
    public static float destroyerProbeLaserDamage;
    public static int destroyerHeadLaserCooldownTicks;
    public static int destroyerBodyLaserCooldownTicks;
    public static int destroyerProbeLaserCooldownTicks;
    public static int destroyerBodyLaserEverySegments;
    public static int destroyerLaserLifetimeTicks;
    public static boolean destroyerProbesEnabled;
    public static float destroyerProbeHealth;
    public static int destroyerProbeChance;
    public static int destroyerProbeCooldownTicks;
    public static int destroyerProbeLifetimeTicks;
    public static int destroyerDespawnNoPlayerDelaySeconds;
    public static int destroyerDespawnNoPlayerChunkRadius;
    public static String[] destroyerDrops;
    public static int iceRodDurability;
    public static float iceRodBlockLifetimeSeconds;
    public static float iceRodSpawnDistance;
    public static boolean iceRodPlacementPreviewEnabled;
    public static boolean iceRodUseLegendGearMana;
    public static float iceRodLegendGearManaCost;
    public static boolean magicIceRequireSilkTouch;
    public static int caneOfSomariaDurability;
    public static float caneOfSomariaBlockLifetimeSeconds;
    public static float caneOfSomariaSpawnDistance;
    public static boolean caneOfSomariaPlacementPreviewEnabled;
    public static boolean caneOfSomariaUseLegendGearMana;
    public static float caneOfSomariaLegendGearManaCost;
    public static boolean somariaBlockRequireSilkTouch;
    public static boolean somariaBlockRequirePickaxeToDrop;
    public static boolean whoopieCushionKnockbackEnabled;
    public static float whoopieCushionKnockbackRadius;
    public static float whoopieCushionKnockbackStrength;
    public static float whoopieCushionLegendGearManaCost;
    public static int terraMushroomSpawnAttempts;
    public static int daybloomSpawnAttempts;
    public static int blinkrootSpawnAttempts;
    public static int waterleafSpawnAttempts;
    public static int deathweedSpawnAttempts;
    public static int fireblossomSpawnAttempts;
    public static int jungleSporeSpawnAttempts;
    public static int moonglowSpawnAttempts;
    public static boolean terraMushroomRequireShears;
    public static boolean daybloomRequireShears;
    public static boolean blinkrootRequireShears;
    public static boolean waterleafRequireShears;
    public static boolean deathweedRequireShears;
    public static boolean fireblossomRequireShears;
    public static boolean jungleSporeRequireShears;
    public static boolean moonglowRequireShears;

    // Wheatfield biome
    public static boolean enableWheatfieldBiome;
    public static int wheatfieldBiomeId;
    public static int wheatfieldBiomeWeight;
    public static boolean wheatfieldAllowVillage;
    public static int wheatfieldTreeChunkChance;
    public static int wheatfieldPumpkinChunkChance;
    public static int wheatfieldBarleyFistDropChancePercent;
    public static boolean wheatfieldBarleyOnlyDropsWhenSheared;

    // Biomes O' Plenty 1.6.4 Hot Springs port
    public static boolean enableHotSpringsModule;
    public static int hotSpringsBiomeId;
    public static int hotSpringsBiomeWeight;
    public static boolean hotSpringsAllowVillages;
    public static String[] hotSpringsPotionEffects;
    public static int hotSpringsSpringLakesPerChunk;
    public static int hotSpringsLavaLakesPerChunk;
    public static int hotSpringsLakeRarity;
    public static String[] hotSpringsLakeBiomeWhitelist;
    public static String[] hotSpringsLakeBiomeBlacklist;
    public static int[] hotSpringsLakeDimensionWhitelist;
    public static int[] hotSpringsLakeDimensionBlacklist;
    public static boolean configurableWaterLakeYLevels;
    public static int waterLakeMinY;
    public static int waterLakeMaxY;

    // Witches and More ports
    public static boolean enableWitchesAndMoreModule;
    public static boolean enableCyclopsMob;
    public static int cyclopsMaxHealth;
    public static boolean enableFlowerManMob;
    public static int flowerManMaxHealth;
    public static boolean enableEnderTrollMob;
    public static int enderTrollMaxHealth;
    public static boolean enableJaxxMob;
    public static int jaxxMaxHealth;
    public static boolean enableBlackWidowMob;
    public static int blackWidowMaxHealth;
    public static String[] wamCyclopsDropEntries;
    public static String[] wamFlowerManDropEntries;
    public static String[] wamEnderTrollDropEntries;
    public static String[] wamJaxxDropEntries;
    public static String[] wamBlackWidowDropEntries;

    // OffLawn ports
    public static boolean enableOffLawnModule;
    public static boolean offLawnEnableSunflowerWorldgen;
    public static int offLawnSunflowerPatchChance;
    public static int offLawnSunflowerAttemptsPerChunk;
    public static int offLawnSunflowerPatchRadiusBlocks;
    public static String[] offLawnSunflowerBiomeTypes;
    public static String[] offLawnSunflowerBiomeList;
    public static boolean offLawnEnableBrightSunflowerWorldgen;
    public static int offLawnBrightSunflowerPatchChance;
    public static int offLawnBrightSunflowerAttemptsPerChunk;
    public static int offLawnBrightSunflowerPatchRadiusBlocks;
    public static String[] offLawnBrightSunflowerBiomeTypes;
    public static String[] offLawnBrightSunflowerBiomeList;
    public static boolean offLawnEnableMixedSunflowerWorldgen;
    public static int offLawnMixedSunflowerPatchChance;
    public static int offLawnMixedSunflowerAttemptsPerChunk;
    public static int offLawnMixedSunflowerPatchRadiusBlocks;
    public static String[] offLawnMixedSunflowerBiomeTypes;
    public static String[] offLawnMixedSunflowerBiomeList;
    public static int offLawnBeanstalkMaxGrowthLevel;
    public static boolean offLawnSunflowerAuraEnabled;
    public static float offLawnSunflowerAuraRadius;
    public static String[] offLawnSunflowerAuraEffects;
    public static boolean offLawnBrightSunflowerAuraEnabled;
    public static float offLawnBrightSunflowerAuraRadius;
    public static String[] offLawnBrightSunflowerAuraEffects;
    public static boolean offLawnBrightSunflowerSpeedBoostEnabled;
    public static float offLawnBrightSunflowerSpeedBoostPercent;
    public static int offLawnBrightSunflowerHappyPotionId;
    public static float offLawnBrightSunflowerSpeedBoostRadius;
    public static int offLawnBrightSunflowerSpeedBoostDurationSeconds;
    public static float offLawnBrightSunflowerSeedChance;

    // Pumpkin Pastures ports
    public static boolean enablePumpkinPasturesModule;
    public static float pumpkinPasturesCreeperExplosionStrength;
    public static float pumpkinPasturesCreeperDamageMultiplier;
    public static float pumpkinPasturesCreeperKnockbackMultiplier;
    public static boolean pumpkinPasturesCreeperExplosionDamagesEnvironment;
    public static float pumpkinPasturesEnderflameSwordDamage;
    public static int pumpkinPasturesEnderflameSwordDurability;
    public static int pumpkinPasturesEnderflamePickaxeDurability;
    public static int pumpkinPasturesEnderflameShaxDurability;
    public static float pumpkinPasturesEnderflameToolEfficiency;
    public static boolean pumpkinPasturesEnderflamePickaxeAutoSmelt;
    public static boolean pumpkinPasturesEnderflameShaxAutoSmelt;
    public static String[] pumpkinPasturesCorruptedSoulDropEntries;
    public static String[] pumpkinPasturesPumpkinSoulDropEntries;
    public static boolean pumpkinPasturesEnderflameStaffCastsSpell;
    public static float pumpkinPasturesEnderflameStaffManaCost;
    public static int pumpkinPasturesEnderflameStaffDurability;
    public static float pumpkinPasturesEnderflameStaffFireSeconds;
    public static float pumpkinPasturesEnderflameStaffSpellDamage;

    // Embedded legacy module ports
    public static boolean enableGokiStatsModule;
    public static boolean enableRiftExplorerModule;
    public static boolean enableMoreBowsModule;
    public static boolean riftExplorerEnablePebbleRecipes;
    public static boolean riftExplorerPebblesDropFromGrass;
public static String[] riftExplorerSlingshotAmmoItems;
public static boolean riftExplorerSlingshotEnableOreDictionaryAmmo;
public static String[] riftExplorerSlingshotAmmoOreDictionary;
public static String[] riftExplorerSlingshotSpecialAmmoEntries;
public static String[] riftExplorerSlingshotDisabledAmmoItems;
public static String[] riftExplorerSlingshotSkeletonAmmoEntries;
public static String riftExplorerSlingshotAmmoIconCorner;
public static boolean riftExplorerSlingshotBlockBossCapture;
public static String[] riftExplorerSlingshotCaptureMobBlacklist;
    public static float riftExplorerSlingshotBaseDamage;
    public static String[] riftExplorerDisabledLongbowArrows;
    public static int riftExplorerLongbowDurability;
    public static int riftExplorerSlingshotDurability;
    public static int riftExplorerBoomerangDurability;
    public static String[] riftExplorerBoomerangImpactModifierItems;
    public static String[] riftExplorerBoomerangCapacityModifierItems;
    public static String[] riftExplorerBoomerangPowerModifierItems;
    public static String[] riftExplorerBoomerangReachModifierItems;
    public static String[] riftExplorerBoomerangEnderModifierItems;
    public static String[] riftExplorerBoomerangUnbreakingModifierItems;
    public static float riftExplorerBoomerangImpactModifierMaxPercent;
    public static int riftExplorerBoomerangCapacityModifierMaxStacks;
    public static float riftExplorerBoomerangPowerModifierMaxPercent;
    public static float riftExplorerBoomerangReachModifierMaxPercent;
    public static float riftExplorerBoomerangEnderModifierMaxPercent;
    public static float riftExplorerBoomerangUnbreakingModifierMaxPercent;
    public static int riftExplorerBoomerangMaxModifierTypes;
    public static String[] riftExplorerBoomerangStarInfusedItems;
    public static int riftExplorerBoomerangStarInfusedBonusModifierTypes;
    public static float riftExplorerBoomerangStarInfusedBonusPercent;
    public static float riftExplorerBoomerangMaxCombinedModifierPercent;
    public static int riftExplorerBlowpipeDurability;
    public static float riftExplorerDartDamage;
    public static int riftExplorerDartMaxPotionModifiers;
    public static float riftExplorerDartPotionDurationSeconds;
    public static boolean riftExplorerDartPotionEffectFilterWhitelistMode;
    public static String[] riftExplorerDartPotionEffectFilter;
    public static int riftExplorerDartPotionLevelCap;

    // Palaria mob ports
    public static boolean enablePalariaModule;
    public static boolean enablePalariaCowasaurus;
    public static float palariaCowasaurusMaxHealth;
    public static boolean enablePalariaCreeptile;
    public static float palariaCreeptileMaxHealth;
    public static float palariaCreeptileExplosionStrength;
    public static float palariaCreeptileDamageMultiplier;
    public static float palariaCreeptileKnockbackMultiplier;
    public static boolean palariaCreeptileExplosionDamagesEnvironment;
    public static boolean enablePalariaRaptorChicken;
    public static float palariaRaptorChickenMaxHealth;
    public static boolean enablePalariaEnderWalker;
    public static float palariaEnderWalkerMaxHealth;
    public static boolean enablePalariaNimatin;
    public static boolean palariaNimatinTameable;
    public static float palariaNimatinTameChance;
    public static float palariaNimatinMaxHealth;
    public static float palariaNimatinTamedMaxHealth;
    public static float palariaNimatinTamedDamage;
    public static float palariaNimatinRidingSpeed;
    public static float palariaNimatinMaxJumpHeight;
    public static boolean palariaNimatinDoubleJumpEnabled;
    public static float palariaNimatinDoubleJumpHeight;
    public static float palariaNimatinTeleportDistance;
    public static int palariaNimatinTalkInterval;
    public static float palariaNimatinKillHealAmount;
    public static float palariaNimatinOwnerKillHealMultiplier;
    public static String[] palariaNimatinTameItems;
    public static boolean palariaNimatinAllowMobPassengers;
    public static String[] palariaNimatinMobPassengerBlacklist;
    public static boolean enablePalariaEnderRaptorChicken;
    public static float palariaEnderRaptorChickenMaxHealth;
    public static boolean enablePalariaMagmaRaptorChicken;
    public static float palariaMagmaRaptorChickenMaxHealth;
    public static boolean palariaMagmaRaptorChickenPlaceFire;
    public static String[] palariaNimatinDropEntries;
    public static String[] palariaCowasaurusDropEntries;
    public static String[] palariaCreeptileDropEntries;

    public static boolean satisforestryLizardDoggoAllowNametagRename;
    public static boolean satisforestryLizardDoggoDisableRandomItemFinding;

    public static boolean enableWitchHouseStructure;
    public static int witchHouseChunkChance;
    public static int witchHouseMinDistanceBlocks;
    public static int witchHouseInsideMobCount;
    public static int witchHouseOutsideMobCount;
    public static String[] witchHouseInsideMobIds;
    public static String[] witchHouseOutsideMobIds;
    public static String[] witchHouseChestLootEntries;

    // SpecialArmor integration
    public static boolean specialArmorLootSlimeHelmet;
    public static boolean specialArmorLootDoubleJumpBoots;
    public static boolean specialArmorLootSkates;
    public static boolean specialArmorLootHeavyBoots;



    public static boolean enableInventoryPetsModule;
    public static String[] inventoryPetsEnabledEntries;
    public static String[] inventoryPetsDungeonLootEntries;
    public static int inventoryPetsDungeonLootWeight;
    public static float inventoryPetsBananaDamage;

    private static final String[] DEFAULT_INVENTORY_PET_DUNGEON_LOOT = new String[]{
            "cow",
            "cow_flux",
            "sheep",
            "sheep_flux",
            "pig",
            "pig_flux",
            "chicken",
            "chicken_flux",
            "squid",
            "squid_flux",
            "ocelot",
            "ocelot_flux",
            "mooshroom",
            "mooshroom_flux",
            "ghast",
            "ghast_flux",
            "spider",
            "spider_flux",
            "iron_golem",
            "iron_golem_flux",
            "snow_golem",
            "snow_golem_flux",
            "enderman",
            "enderman_flux",
            "creeper",
            "creeper_flux",
            "magma_cube",
            "magma_cube_flux",
            "wither",
            "wither_flux",
            "blaze",
            "blaze_flux",
            "bed",
            "bed_flux",
            "chest",
            "chest_flux",
            "sated_chest",
            "double_chest",
            "double_chest_flux",
            "sated_double_chest",
            "ender_chest",
            "ender_chest_flux",
            "furnace",
            "furnace_flux",
            "crafting_table",
            "crafting_table_flux",
            "enchanting_table",
            "enchanting_table_flux",
            "jukebox",
            "jukebox_flux",
            "anvil",
            "anvil_flux",
            "brewing_stand",
            "brewing_stand_flux",
            "nether_portal",
            "nether_portal_flux",
            "end_portal",
            "sponge",
            "sponge_flux",
            "purplicious_cow",
            "purplicious_cow_flux",
            "mickerson",
            "mickerson_flux",
            "pingot",
            "dingot",
            "quantum_crystal_monster",
            "banana",
            "loot",
            "loot_flux",
            "illuminati",
            "illuminati_flux",
            "juggernaut",
            "juggernaut_flux",
            "grave",
            "quiver",
            "quiver_flux",
            "pacman",
            "pacman_flux",
            "cheetah",
            "cheetah_flux",
            "biome",
            "house",
            "house_flux",
            "silverfish",
            "silverfish_flux",
            "wolf",
            "wolf_flux",
            "siamese",
            "apple",
            "apple_flux",
            "sun",
            "slime",
            "slime_flux",
            "cloud",
            "cloud_flux",
            "pixie",
            "pufferfish",
            "pufferfish_flux",
            "black_hole",
            "black_hole_flux",
            "lead",
            "saddle",
            "flying_saddle",
            "shield",
            "shield_flux",
            "torch",
            "heart",
            "heart_flux",
            "moon",
            "moon_flux",
            "dubstep",
            "dubstep_flux",
            "custom",
            "dirt",
            "cobblestone",
            "christmas_tree",
            "christmas_tree_flux",
            "menorah",
            "menorah_flux",
            "mishumaa_saba",
            "mishumaa_saba_flux",
            "politically_correct",
            "politically_correct_flux",
            "april_fool",
            "april_fool_flux"
    };

    private static final String INVENTORY_PET_DUNGEON_LOOT_COMMENT =
            Arrays.toString(DEFAULT_INVENTORY_PET_DUNGEON_LOOT);

    private static final String[] DEFAULT_PALARIA_NIMATIN_DROPS = new String[]{
            "riftflux:petBanana|0.30",
            "riftflux:petCheetah|0.05",
            "riftflux:petCheetahVariant|0.05",
            "riftflux:petOcelot|0.05",
            "riftflux:petOcelotVariant|0.05",
            "riftflux:charmPendant@4|0.15",
            "riftflux:highlandspirit|0.15",
            "minecraft:beef*1-6|1.0",
            "minecraft:leather*1-6|1.0",
            "minecraft:bone*1-6|1.0"
    };

    private static final String[] DEFAULT_PALARIA_COWASAURUS_DROPS = new String[]{
            "riftflux:petCow|0.15",
            "riftflux:petCowVariant|0.15",
            "riftflux:petPurpliciousCow|0.05",
            "riftflux:petPurpliciousCowVariant|0.05",
            "riftflux:focusband|0.05",
            "minecraft:beef*1-6|1.0",
            "minecraft:leather*1-6|1.0",
            "minecraft:bone*1-6|1.0"
    };

    private static final String[] DEFAULT_PALARIA_CREEPTILE_DROPS = new String[]{
            "riftflux:whoopie_cushion|0.05",
            "riftflux:petCreeper|0.05",
            "riftflux:petCreeperVariant|0.05",
            "riftflux:creeptile_eye|0.05",
            "riftflux:charmPendant@3|0.05",
            "riftflux:as_shield_iron_gilded|0.05",
            "minecraft:gunpowder*1-12|1.0"
    };

    private static final String[] DEFAULT_WAM_CYCLOPS_DROPS = new String[]{
            "minecraft:beef*1-2|1.0"
    };

    private static final String[] DEFAULT_WAM_FLOWER_MAN_DROPS = new String[]{
            "flowerman_flower|1.0"
    };

    private static final String[] DEFAULT_WAM_ENDER_TROLL_DROPS = new String[]{
            "minecraft:ender_pearl|0.5",
            "minecraft:obsidian|0.5"
    };

    private static final String[] DEFAULT_WAM_JAXX_DROPS = new String[]{
            "minecraft:pumpkin|0.5",
            "minecraft:blaze_powder|0.5"
    };

    private static final String[] DEFAULT_WAM_BLACK_WIDOW_DROPS = new String[]{
            "minecraft:string*0-2|1.0",
            "minecraft:spider_eye|0.333"
    };

    private static final String[] DEFAULT_PALARIA_NIMATIN_TAME_ITEMS = new String[]{
            "riftflux:creeptile_eye"
    };

    private static final String[] DEFAULT_PALARIA_NIMATIN_PASSENGER_BLACKLIST = new String[]{
            "EntityNimatin",
            "EntityBison",
            "EntitySootSprite"
    };

    private static final String[] DEFAULT_DUCKLING_QUACKLING_TRADES = new String[]{
            "minecraft:emerald*1-6 -> minecraft:fish*1-6",
            "minecraft:emerald*1-6 -> riftflux:duck_egg*1-6",
            "minecraft:emerald*1-6 -> riftflux:reedPipes*1; chance=50",
            "minecraft:emerald*1-6 -> riftflux:creeptile_eye*1; chance=1",
            "minecraft:emerald*1-6 -> riftflux:glider_yellow*1; chance=5",
            "minecraft:emerald*1-6 -> riftflux:poptart*1-6; chance=15",
            "minecraft:emerald*1-6 -> riftflux:glintrune*1-3@4; chance=10",
            "minecraft:emerald*1-6 -> riftflux:suspicious_looking_eye*1; chance=2",
            "minecraft:emerald*1-6 -> riftflux:axolotl_bucket*1@2; chance=10",
            "minecraft:emerald*1-6 -> riftflux:pet_sun*1; chance=2",
            "minecraft:emerald*1-6 -> riftflux:bright_sunflower*16-32; chance=20",
            "minecraft:emerald*1-6 -> riftflux:sunflower_bush*16-32; chance=20",
            "minecraft:emerald*1-6 -> riftflux:beanstalk*16-32; chance=20",
            "minecraft:emerald*1-6 -> riftflux:blockBombFlower*1-3; chance=5",
            "minecraft:emerald*1-6 -> riftflux:furniture_curtain_yellow*1-3; chance=7",
            "minecraft:emerald*1-6 -> riftflux:heartContainer*1; chance=2",
            "minecraft:emerald*1-6 -> riftflux:petBanana*1; chance=2",
            "minecraft:emerald*1-6 -> riftflux:soot_jar*1; chance=7",
            "minecraft:emerald*1-6 -> riftflux:blowpipe*1; chance=25",
            "minecraft:emerald*1-6 -> riftflux:star_candy*1; chance=25",
            "minecraft:emerald*1-6 -> riftflux:toolbelt*1; chance=7",
            "minecraft:emerald*1-6 -> riftflux:pet_pixie*1; chance=1"
    };

    private static final String[] DEFAULT_DUCKLING_QUACKLING_BREED_ITEMS = new String[]{
            "minecraft:fish"
    };

    private static final String[] DEFAULT_DUCKLING_QUACKLING_BREED_ORE_DICTIONARY = new String[]{
            "listAllfishraw",
            "foodFishraw",
            "fishRaw"
    };

    private static final String[] DEFAULT_SOOT_SPRITE_HEALING_ITEMS = new String[]{
            "minecraft:coal@0",
            "minecraft:coal@1",
            "riftflux:star_candy"
    };

    private static final String[] OLD_DEFAULT_LEGACY_MYSTIC_SHRUB_DROP_ENTRIES = new String[]{
            "heartPickup*1|0.2",
            "emeraldShard*1|0.2",
            "minecraft:arrow*1|0.2",
            "riftflux:star_candy*1|0.01"
    };
    private static final String[] DEFAULT_LEGACY_MYSTIC_SHRUB_DROP_ENTRIES = new String[]{
            "heartPickup*1|0.2",
            "emeraldShard*1|0.2",
            "minecraft:arrow*1|0.2",
            "pebble*1|0.2",
            "riftflux:star_candy*1|0.01"
    };

    private static final String[] DEFAULT_LEGACY_MYSTIC_SHRUB_CHARGED_PRIZE_ENTRIES = new String[]{
            "minecraft:gold_nugget*1|20",
            "riftflux:heartPickup*3|20",
            "minecraft:arrow*5|20",
            "riftflux:emeraldShard*1@1|20",
            "riftflux:itemBomb*3|20"
    };
    private static final String[] OLD_DEFAULT_LEGACY_MYSTIC_SHRUB_BIOME_BLACKLIST = new String[]{
            "wheatfield"
    };
    private static final String[] DEFAULT_LEGACY_MYSTIC_SHRUB_BIOME_BLACKLIST = new String[]{
            "wheatfield",
            "sandy"
    };

    // Chester module
    public static boolean enableChesterModule;
    public static boolean enableChesterBaubleSlot;
    public static float chesterTeleportDistance;
    public static String chesterInventoryColor;
    public static String shadowChesterInventoryColor;

    // Axolotl module
    public static boolean enableAxolotlModule;
    public static float axolotlMaxHealth;

    // Ghibli module
    public static boolean enableDucklingModule;
    public static boolean ghibliStarCandyRecipeEnabled;
    public static float ducklingAgentDNaturalVariantChancePercent;
    public static float ghibliSootSpriteCoalOreSpawnChancePercent;
    public static int ghibliSootSpriteCoalOreMinSpawns;
    public static int ghibliSootSpriteCoalOreMaxSpawns;
    public static int ghibliSootSpriteChirpIntervalTicks;
    public static float ghibliSootSpriteMaxHealth;
    public static String[] ghibliSootSpriteHealingItems;
    public static float ducklingQuacklingMaxHealth;
    public static boolean ducklingQuacklingTradingEnabled;
    public static boolean ducklingQuacklingTradeOnlyWhileFishing;
    public static String[] ducklingQuacklingTrades;
    public static boolean ducklingQuacklingRefreshTradesDaily;
    public static String[] ducklingQuacklingBreedItems;
    public static String[] ducklingQuacklingBreedOreDictionary;
    public static int[] ducklingQuacklingFishingCatchDelayTicks;
    public static int[] ducklingQuacklingFishingCatchesBeforeStop;
    public static int[] ducklingQuacklingFishingSessionsPerDay;
    public static int ducklingQuacklingFishingBumpRecoveryTicks;

    // LegendGear module
    public static boolean enableLegendGearModule;
    public static int legendGearMagicProtectionId;
    public static int legendGearSpellReachId;
    public static int legendGearSpellSpreadId;
    public static int legendGearSpellArmoredId;
    public static boolean legendGearEnableLegacyLegendGear;
    public static boolean legendGearSoulTetherEnabled;
    public static int legendGearLegacyFocusEnchantmentId;
    public static int legendGearLegacySoulTetherEnchantmentId;
    public static int legendGearLegacyBombBagCapacity;
    public static int legendGearLegacyBombMaxStackSize;
    public static int legendGearLegacyBombDamage;
    public static int legendGearLegacyBombFuseTimeTicks;
    public static float legendGearLegacyBombExplosionStrength;
    public static boolean legendGearLegacyBombsIgniteExplosiveBlocks;
    public static boolean legendGearLegacyBombFlowerPlaceAnywhere;
    public static boolean legendGearLegacyBombFlowerPlaceOnAnyBlockNearLava;
    public static boolean legendGearLegacyBombFlowerPickupWithShears;
    public static boolean legendGearLegacyBombFlowerPickupWithSilkTouch;
    public static boolean legendGearLegacyBombFlowerMobTrigger;
    public static int legendGearLegacyClayJarItemCapacity;
    public static boolean legendGearLegacyClayJarSneakPickupEnabled;
    public static boolean legendGearLegacyClayJarUndergroundGenEnabled;
    public static int legendGearLegacyClayJarUndergroundSpawnChance;
    public static int legendGearLegacyClayJarUndergroundMinY;
    public static int legendGearLegacyClayJarUndergroundMaxY;
    public static String[] legendGearLegacyClayJarNaturalLootEntries;
    public static String[] legendGearLegacyBombableBlocks;
    public static double legendGearLegacyMysticShrubGenStarChance;
    public static String[] legendGearLegacyMysticShrubChargedPrizeEntries;
    public static double legendGearLegacyMysticShrubJackpotChance;
    public static String[] legendGearLegacyMysticShrubDropEntries;
    public static int legendGearLegacyMysticShrubRarity;
    public static String[] legendGearLegacyMysticShrubBiomeWhitelist;
    public static String[] legendGearLegacyMysticShrubBiomeBlacklist;
    public static String[] legendGearAzuriteBiomeWhitelist;
    public static String[] legendGearAzuriteBiomeBlacklist;
    public static int[] legendGearLegacyMysticShrubDimensionWhitelist;
    public static int[] legendGearLegacyBombFlowerDimensionWhitelist;
    public static int legendGearLegacyQuiverMaxCapacity;
    public static boolean legendGearLegacyAllowCandy;
    public static boolean legendGearLegacyBombsAllowed;
    public static boolean legendGearLegacyEmeraldShardsAllowed;
    public static boolean legendGearLegacyHeartsAllowed;
    public static boolean legendGearLegacyMagicMirrorAllowed;
    public static int legendGearLegacyMagicMirrorDurability;
    public static String[] legendGearFortuneCookiePotionEffects;
    public static String[] legendGearSweetSnackPotionEffects;
    public static boolean legendGearLegacyMedallionsAllowed;
    public static boolean legendGearLegacyMysticShrubAllowed;
    public static boolean legendGearLegacyMysticShrubSuperPrizes;
    public static boolean legendGearLegacyQuiverAllowed;
    public static boolean legendGearLegacyHookshotAnyBlock;
    public static String[] legendGearLegacyHookshotBlocks;
    public static int legendGearLegacyHookshotDurability;
    public static int legendGearLegacyAeroAmuletDurability;
    public static int legendGearLegacyGeoAmuletDurability;
    public static float legendGearLegacyGeoAmuletQuakeDamageMultiplier;
    public static int legendGearLegacyPyroAmuletDurability;
    public static boolean legendGearLegacyAmuletsUseBaublesSlot;
    public static boolean legendGearLegacyMedallionEffectsAffectPlayer;
    public static boolean legendGearLegacyWhirlwindBootsDashSound;
    public static double legendGearLegacyStarbeamRailLaunchStrength;
    public static int legendGearLegacyStarbeamRailConnectionRange;
    public static boolean legendGearLegacyStarbeamRailNoSlowdown;
    public static boolean legendGearLegacyStarbeamRailNoFallDamage;
    public static boolean legendGearLegacyStarbeamRailConnectAcrossTypes;
    public static boolean legendGearLegacyStarbeamRailRightClickAnyDirection;
    public static boolean legendGearLegacyTitanBandBlockBossPickup;
    public static String[] legendGearLegacyTitanBandPickupBlacklist;
    public static boolean legendGearLegacyTitanBandKeepPassengerOnCarrierHurt;
    public static int legendGearLegacyTitanBandDurability;
    public static boolean legendGearLegacyHeadbandOfValorDamageBonusWithArmor;
    public static boolean legendGearLegacyHeadbandOfValorHeadBaubleDamageBonus;
    public static int legendGearLegacyHeadbandOfValorDurability;
    public static float legendGearLegacyHeadbandOfValorBonusDamage;
    public static boolean legendGearLegacyStarbeamRailRightClickTravel;
    public static boolean legendGearAllowEmeraldDrops;
    public static boolean legendGearAllowHeartDrops;
    public static float legendGearItemSoundVolume;
    public static boolean legendGearFancyExperience;
    public static int legendGearMaxStarwellRetries;
    public static boolean legendGearFallingStarDamageEnabled;
    public static float legendGearFallingStarDamage;
    public static float legendGearNightFallingStarFrequency;
    public static boolean legendGearAprilFoolsBananaFallingStars;
    public static boolean legendGearMagicBoomerangInfiniteDurability;
    public static float legendGearMagicBoomerangDamage;
    public static boolean legendGearMagicBoomerangBreakPlants;
    public static boolean legendGearMagicBoomerangActivateLegacyPlants;
    public static boolean legendGearMagicBoomerangPickupBombFlowerBombs;
    public static boolean legendGearEnableBadBow;
    public static int legendGearDashRingMaxAirJumps;
    public static boolean legendGearDashRingUseOriginalBehavior;
    public static boolean legendGearDashRingAirJumpsRequireSprinting;
    public static float legendGearDashRingAirJumpManaCost;
    public static float legendGearDescentRingManaCost;
    public static boolean legendGearSprinkleStardustRequireSneak;
    public static boolean legendGearSpottingScopeConsumesMana;
    public static boolean legendGearLoginSparkleEffectEnabled;
    public static boolean legendGearDimensionChangeSparkleEffectEnabled;
    public static int legendGearTwinkleStaffDurability;
    public static int legendGearFireStaffDurability;
    public static int legendGearZapStaffDurability;
    public static int legendGearIceStaffDurability;
    public static float legendGearTwinkleStaffManaCost;
    public static float legendGearFireStaffManaCost;
    public static float legendGearZapStaffManaCost;
    public static float legendGearIceStaffManaCost;
    public static int legendGearStarPieceInfuseLevels;
    public static boolean legendGearPlaceableStarPieces;
    public static boolean legendGearInfusedStarPiecesActAsStarbeamRails;
    public static int legendGearStarInJarLightLevel;
    public static int legendGearPlacedStarPiecesLightLevel;
    public static boolean legendGearFulguriteRequiresSilkTouch;
    public static float legendGearEmberStaffFireSeconds;
    public static int legendGearManaRegenPotionId;
    public static int legendGearGroundedPotionId;
    public static float legendGearManaRegenPotionManaPerSecond;
    public static int legendGearStoneskinResistancePotionId;
    public static boolean legendGearCaltropsBreakOnTrigger;
    public static boolean legendGearCaltropsTriggerDropEnabled;
    public static float legendGearCaltropsMobDamageHearts;
    public static float legendGearCaltropsPlayerDamagePercent;
    public static boolean legendGearCaltropsIronBarsMiningSpeed;
    public static boolean legendGearCaltropsRequirePickaxeToDrop;
    public static boolean legendGearCaltropsUndergroundGenEnabled;
    public static int legendGearCaltropsUndergroundSpawnChance;
    public static int legendGearCaltropsUndergroundMinY;
    public static int legendGearCaltropsUndergroundMaxY;
    public static int legendGearCaltropsSlownessPotionId;
    public static int legendGearExitConfusionPotionId;
    public static int legendGearIceSpellSlownessPotionId;
    public static int legendGearPhoenixReviveResistancePotionId;
    public static int legendGearPhoenixReviveRegenerationPotionId;
    public static int legendGearPhoenixReviveFireResistancePotionId;
    public static boolean legendGearPhoenixFeatherReviveEnabled;
    public static int legendGearThiefRingInvisibilityPotionId;
    public static int legendGearPhoenixEmblemFireResistancePotionId;
    public static boolean enableAsgardShieldModule;
    public static int asgardShieldEquipmentDurabilityMultiplier;
    public static int asgardShieldHarkenVitalityAugmentId;
    public static int asgardShieldHarkenExudeAugmentId;
    public static int asgardShieldHarkenWardAugmentId;
    public static int asgardShieldHarkenSanguinaryAugmentId;
    public static boolean asgardShieldEnableVanguard;
    public static int asgardShieldHudVanguardYOffset;
    public static int asgardShieldHudGuardGaugeYOffset;
    public static int asgardShieldColorWood;
    public static int asgardShieldColorStone;
    public static int asgardShieldColorIron;
    public static int asgardShieldColorDiamond;
    public static int asgardShieldColorNether;
    public static int asgardShieldColorEnder;
    public static int asgardShieldColorSkull;
    public static int asgardShieldColorPatchwork;
    public static int asgardShieldColorLivingmetal;
    public static int asgardShieldColorBiomass;
    // Soul Hearts
    public static boolean enableSoulHeartsModule;
    public static float soulHeartsDamageMultiplier;
    public static boolean soulHeartsConsumeOneHeartPerHit;

    // Heart Crystal
    public static boolean enableHeartCrystalModule;
    public static int heartCrystalHeartsPerCrystal;
    public static int heartCrystalMiningLevel;
    public static int heartCrystalMaxHearts;
    public static int heartCrystalGenHeight;
    public static int heartCrystalGenCount;
    public static boolean heartCrystalOldModel;
    public static float heartCrystalHeartPetDropChance;
    public static boolean heartLanternAuraEnabled;
    public static float heartLanternAuraRadius;
    public static String[] heartLanternAuraEffects;
    public static boolean starLanternAuraEnabled;
    public static float starLanternAuraRadius;
    public static String[] starLanternAuraEffects;

    // Furniture
    public static boolean enableFurnitureModule;

    // Armor Overlay
    public static boolean enableArmorOverlayModule;
    public static int armorOverlayLevels;
    public static int armorOverlayArmorPieces;
    public static boolean armorOverlayShowNumbers;
    public static boolean divineRpgDisableHaliteExtraArmorPieceRender;

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
    public static boolean dualHotbarEnablePickBlockImplementation;
    public static boolean dualHotbarLongHotbar;
    public static boolean dualHotbarDoubleTap;
    public static boolean dualHotbarKeyCombo;
    public static int dualHotbarDoubleTapTime;
    public static int dualHotbarNumHotbars;
    public static boolean dualHotbarHeldItemTooltipAboveBars;
    public static int dualHotbarHeldItemTooltipPadding;

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
    public static boolean disableFalseCrashImprover;
    public static int     stackOverflowMaxDepth;

    public static boolean protectItemsFromExplosions;

    public static boolean enableDroppedItemRenderTweaks;
    public static int droppedItemLimit;
    public static int droppedItemMaxRenderDistance;

    public static boolean woolRequireShears;
    public static boolean shearsDamageOnAnyBlock;
    public static int deathRespawnDelaySeconds;
    public static boolean allowChatOnDeathScreen;
    public static boolean allowChatOnPauseScreen;
    public static boolean enableBetaLeavesLook;
    public static boolean enableChatBubblesModule;
    public static boolean chatBubblesShowOwnMessages;
    public static boolean chatBubblesShowBackground;
    public static boolean chatBubblesRenderInPhotoMode;
    public static boolean chatBubblesUseCustomOwnBubbleColor;
    public static int chatBubblesOwnBubbleColor;
    public static boolean chatBubblesUseCustomOwnTextColor;
    public static int chatBubblesOwnTextColor;
    public static boolean chatBubblesRandomizeTextColorByUuid;
    public static boolean chatBubblesBlackTextBackground;
    public static final int CHAT_BUBBLES_MESSAGE_GAP_DEFAULT = 1;
    public static final int CHAT_BUBBLES_MESSAGE_GAP_MIN = 0;
    public static final int CHAT_BUBBLES_MESSAGE_GAP_MAX = 8;
    public static final String CHAT_BUBBLES_MESSAGE_GAP_COMMENT =
            "Number of blank line gaps between stacked chat bubble messages. 0 = no extra gap; 1 = default.";
    public static int chatBubblesMessageGap = CHAT_BUBBLES_MESSAGE_GAP_DEFAULT;
    public static final float CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_DEFAULT = 1.0F;
    public static final float CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_MIN = 0.0F;
    public static final float CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_MAX = 1.0F;
    public static final String CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_COMMENT =
            "Opacity for the solid black chat bubble text bar. 0.0 = transparent; 1.0 = fully opaque.";
    public static float chatBubblesBlackTextBackgroundOpacity = CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_DEFAULT;
    public static final float CHAT_BUBBLES_TEXT_SCALE_DEFAULT = 1.0F;
    public static final float CHAT_BUBBLES_TEXT_SCALE_MIN = 0.25F;
    public static final float CHAT_BUBBLES_TEXT_SCALE_MAX = 4.0F;
    public static final String CHAT_BUBBLES_TEXT_SCALE_COMMENT =
            "Multiplier for rendered chat bubble text and bubble size. 1.0 = default.";
    public static float chatBubblesTextScale = CHAT_BUBBLES_TEXT_SCALE_DEFAULT;
    public static int chatBubblesMessageLifetimeSeconds;
    public static int chatBubblesMaxLineLength;
    public static boolean enableChatSelectionCopy;
    public static boolean enableIsometricPhotoMode;
    public static double isometricPhotoModeMaxZoomOut;
    public static float isometricPhotoModeHoldRotateDegreesPerTick;
    public static boolean isometricPhotoModeHideNetherNetherrackAndBedrock;
    public static boolean enableWorldTooltips;
    public static boolean worldTooltipsHideModName;
    public static int worldTooltipsMaxDistance;
    public static boolean worldTooltipsOverrideOutline;
    public static float worldTooltipsTransparency;
    public static float worldTooltipsVerticalOffset;
    public static float worldTooltipsTextScale;
    public static double worldTooltipsHoverRadiusHorizontal;
    public static double worldTooltipsHoverRadiusVertical;
    public static int worldTooltipsBackgroundColor;
    public static int worldTooltipsOutlineColor;

    public static boolean disableBonemeal;
    public static double bonemealFlowerChance;

    public static boolean allowPlantsOnAnyBlock;
    public static boolean allowPumpkinsOnAnyBlock;
    public static boolean allowSugarcaneOnAnyBlock;
    public static boolean allowSugarcaneInWater;
    public static boolean sugarcaneGeneratesOnRiverFloors;
    public static int sugarcaneRiverFloorRarity;
    public static int sugarcaneRiverFloorMinHeightAboveWater;
    public static int sugarcaneRiverFloorMaxHeightAboveWater;
    public static int sugarcaneMaxHeight;
    public static int sugarcaneMaxHeightAboveTopWaterBlock;
    public static boolean allowHangingSugarcane;
    public static boolean hangingSugarcaneGrowsWithWaterAboveSupport;
    public static boolean sugarcaneGrowsWhenSupportHasBlockBelow;
    public static boolean directionalCrossedPlantRenderingByPlacement;
    public static boolean directionalCrossedPlantFacePlayerOnPlacement;
    public static boolean allowTorchesOnAnyBlock;
    public static boolean doubleSidedTorchRendering;
    public static boolean billboardTorchRendering;
    public static String[] billboardTorchRenderingBlacklist;
    public static String[] billboardTorchRenderingWhitelist;
    public static boolean modernTorchRendering;
    public static boolean betterTorchTexture;
    public static String[] modernTorchRenderingBlacklist;
    public static String[] modernTorchRenderingWhitelist;
    public static boolean asyncWorldSelection;
    public static boolean saveWorldBeforeWindowClose;
    public static boolean preventBlockBreakingResetOnHeldItemChange;
    public static float movementSpeedFovFactorMax;

    public static boolean strictMobSpawnsZeroBlockLight;

    public static boolean wrongUseSingleDurability;

    public static boolean invincibleOwnedMobs;

    public static boolean invincibleOwnedAllMobs;

    public static boolean invincibleRideableEntities;

    public static boolean teleportOwnedPetsFromUnloadedChunks;
    public static float teleportOwnedPetsMinimumDistance;
    public static boolean preventLeadsBreaking;
    public static boolean preventLeashedMobFallDamage;

    public static boolean unsilenceCoveredNoteBlocks;
    public static boolean disableTintedSugarcane;

    public static boolean enableNetherrackTweak;
    public static int netherliciousBigNetherTopY;
    public static boolean enableDoorAirPlacement;
    public static boolean protectCircuitryFromWater;
    public static boolean preventWaterGrassDecay;
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
    private static final Set<String> disabledBlessingsSet = new HashSet<String>();

    // vortex configs
    public static boolean enableUnloader;
    public static int[] unloaderBlacklistedDimensions;
    public static boolean enablePlacedItem;
    public static boolean enablePlaceableGunpowder;
    public static boolean enableGlowstoneDust;
    public static int glowstoneDustLightLevel;
    public static int glowCarpetLightLevel;
    public static boolean randomizeGlowCarpetTextureOnPlacement;
    public static boolean randomizeGlowCarpetRotation;
    public static int voidFluxLightLevel;
    public static boolean placeableGunpowderSetsFireBelow;
    public static boolean placeableGunpowderEmitsRedstone;
    public static boolean placeableGunpowderIgnitesHbmBarrels;
    public static int butterflyKnifeDurability;
    public static float butterflyKnifeDamage;
    public static float butterflyKnifeBackstabDamage;
    public static boolean butterflyKnifeStaminaBoost;
    public static float butterflyKnifeStaminaBoostAmount;
    public static boolean butterflyKnifeShowBackstabCounter;
    public static String butterflyKnifeBackstabCounterLabel;
    public static String[] butterflyKnifeFlickBoostEffects;
    public static int butterflyKnifeFlickBoostWindowTicks;
    public static boolean butterflyKnifeFlickParry;
    public static int butterflyKnifeFlickParryWindowTicks;
    public static int butterflyKnifeFlickCooldownTicks;
    public static boolean backpackStorage;
    public static boolean backpackDurability;
    public static int backpackDurabilityAmount;
    public static int backpackArmorPoints;
    public static int poptartFoodValue;
    public static float poptartSaturation;
    public static float poptartLegendGearManaRestore;
    public static int poptartDungeonLootWeight;
    public static boolean vortexGlintRuneDungeonLoot;
    public static boolean randomizeEnchantedGlintColors;
    public static boolean GluttonyCharm;
    public static int highlanderPotionEffectId;
    public static boolean enableClaySoldiersModule;
    public static boolean claySoldiersUseOldHurtSound;
    public static float claySoldiersBaseHealth;
    public static float claySoldiersBaseDamage;
    public static double claySoldiersStatItemRange;
    public static int claySoldiersClayHutSpawnChance;
    public static int claySoldiersClayHutZombieChance;

    public static void init(File file){
        config = new Configuration(file);
        config.load();
        syncConfig();
    }

    public static boolean reload() {
        if (config == null) {
            return false;
        }
        try {
            config.load();
            syncConfig();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void syncConfig(){
        enableClaySoldiersModule = config.getBoolean(
                "enableClaySoldiersModule",
                CLAY_SOLDIERS_CATEGORY,
                true,
                "Enables RiftFlux's native Clay Soldiers module. Requires restart."
        );
        claySoldiersUseOldHurtSound = config.getBoolean(
                "Use old hurt sound",
                CLAY_SOLDIERS_CATEGORY,
                true,
                "Should soldiers use the old hurt sound?"
        );
        claySoldiersBaseHealth = config.getFloat(
                "Soldier Base Health",
                CLAY_SOLDIERS_CATEGORY,
                20.0F,
                1.0F,
                40.0F,
                "A soldier's base health."
        );
        claySoldiersBaseDamage = config.getFloat(
                "Soldier Base Damage",
                CLAY_SOLDIERS_CATEGORY,
                1.0F,
                1.0F,
                40.0F,
                "A soldier's unarmed/base damage."
        );
        claySoldiersStatItemRange = config.getFloat(
                "Stat Item Range",
                CLAY_SOLDIERS_CATEGORY,
                48.0F,
                1.0F,
                256.0F,
                "Maximum range used by the Clay Soldiers stat display."
        );
        claySoldiersClayHutSpawnChance = config.getInt(
                "Clay-Hut Spawn Chance",
                CLAY_SOLDIERS_CATEGORY,
                0,
                0,
                Integer.MAX_VALUE,
                "Clay hut rarity denominator. Higher values are rarer; 0 disables clay huts."
        );
        claySoldiersClayHutZombieChance = config.getInt(
                "Clay-Hut Zombie Chance",
                CLAY_SOLDIERS_CATEGORY,
                0,
                0,
                Integer.MAX_VALUE,
                "Zombie-infested clay hut rarity denominator. Higher values are rarer; 0 disables infestation."
        );

        enableChromatiCraftMixin = config.getBoolean("ChromatiCraftMixin","general",true,"Toggles the progression effects");

        DisableAether2Portal = config.getBoolean("MixinAetherPortal","general",true,"Disables Aether 2 Portal, used for Aether Legacy Departure");

        disableWitchingGadgetsVillageHouseGeneration = config.getBoolean(
                "DisableWitchingGadgetsVillageHouseGeneration",
                "general",
                false,
                "If true, prevents Witching Gadgets' Photographer's Workshop house from being added to villages. Requires restart."
        );

        hasSound = config.getBoolean("SoundEffect","general",true,"Toggles progression's sound effects");

        playerOnlyHurtSound = config.getBoolean("PlayerOnlyHurtSound","general",true,"Uses RiftFlux's custom hurt sound, this uses the original OOF hurt sound but was made so that modded mobs and player hurt sound are separated.");
        playerOnlyHurtSoundOofChance = config.getFloat(
                "PlayerOnlyHurtSoundOofChance",
                "general",
                0.02F,
                0.0F,
                1.0F,
                "Chance (0.0-1.0) for player hurt sound to use the roblox instead of riftflux:player_hurt when PlayerOnlyHurtSound is enabled."
        );
        otherPlayersOOF = config.getBoolean(
                "OtherPlayersOOF",
                "general",
                true,
                "If PlayerOnlyHurtSound is enabled, allows player-vs-player hurt sounds to use the roblox oof chance too."
        );

        hasShader= config.getBoolean("ShaderEffect","general",false,"Toggles progression's shader effects");

        disableStrataVents = config.getBoolean("DisableStrataVents","general",false,"Toggles GeoStrata's vent spawn");

        disableStrataOreVeins = config.getBoolean("DisableStrataOreVeins","general",false,"Toggles GeoStrata's ore vein spawn");

        fixGeoStrataCrystalSpikeWaterlogging = config.getBoolean(
                "FixCrystalSpikeWaterlogging",
                "geostrata",
                true,
                "If true, GeoStrata crystal spikes render a waterlogged surface while adjacent to water instead of cutting the water texture off at the surface. Requires restart."
        );

        enableGeoStrataCeilingCrystalSpikes = config.getBoolean(
                "EnableCeilingCrystalSpikes",
                "geostrata",
                true,
                "If true, GeoStrata crystal spikes placed against the underside of a block render downward from the ceiling. Requires restart."
        );

        enableGeoStrataWallCrystalSpikes = config.getBoolean(
                "EnableWallCrystalSpikes",
                "geostrata",
                true,
                "If true, GeoStrata crystal spikes placed against the side of a block render sideways out from the wall. Requires restart."
        );

        enableGeoStrataCrystalSpikeMatchedHitboxes = config.getBoolean(
                "EnableCrystalSpikeMatchedHitboxes",
                "geostrata",
                true,
                "If true, GeoStrata crystal spike collision and selection boxes are narrowed and oriented to match the rendered spike shape. If false, they use the original full-block bounds. Requires restart."
        );

        stabilizeGeoStrataDecoGenShapes = config.getBoolean(
                "StabilizeDecoGenShapes",
                "geostrata",
                true,
                "If true, GeoStrata decorative generation shapes use a deterministic startup seed so crystal spikes do not change their model shape between game reloads. Requires restart."
        );

        disableGeoStrataCrystalSpikeHeightDarkening = config.getBoolean(
                "DisableCrystalSpikeHeightDarkening",
                "geostrata",
                true,
                "If true, GeoStrata crystal spikes keep the same texture brightness along a stack instead of getting darker near the base as they get taller. Requires restart."
        );

        geoStrataCrystalSpikeTextureBrightnessPercent = config.getInt(
                "CrystalSpikeTextureBrightnessPercent",
                "geostrata",
                100,
                0,
                300,
                "Texture brightness multiplier for GeoStrata crystal spikes. 100 is vanilla, 0 is black, 200 is twice as bright, clamped to white. Requires restart."
        );

        disableGeoStrataCrystalSpikeFullbright = config.getBoolean(
                "DisableCrystalSpikeFullbright",
                "geostrata",
                true,
                "If true, GeoStrata crystal spikes use normal block lighting instead of rendering fullbright. Requires restart."
        );

        disableDragonAPILogging = config.getBoolean(
                "DisableDragonAPILogging",
                "general",
                true,
                "If true, suppresses DragonAPI's logging sinks and logger entry points with the ASM core transformer. This also silences Reika mod logging routed through DragonAPI's shared logger classes. Requires restart."
        );

        optimizeDragonAPIBlockRenderFastPaths = config.getBoolean(
                "OptimizeDragonAPIBlockRenderFastPaths",
                "dragonapi",
                true,
                "If true, skips DragonAPI's block render listener bus for blocks that cannot use its water-submerge overlay path. This preserves DragonAPI block render behavior while avoiding per-block callback overhead on normal blocks. Requires restart."
        );

        optimizeDragonAPIEntityRenderLoopFastPaths = config.getBoolean(
                "OptimizeDragonAPIEntityRenderLoopFastPaths",
                "dragonapi",
                true,
                "If true, skips DragonAPI's per-entity-render-loop event post unless its debug change-packet renderer is active. This removes a Forge event post from the normal entity render path. Requires restart."
        );

        optimizeDragonAPIParticleRenderFastPaths = config.getBoolean(
                "OptimizeDragonAPIParticleRenderFastPaths",
                "dragonapi",
                true,
                "If true, removes DragonAPI's per-particle frustum visibility gate so its particle renderer follows the same broad hot path vanilla uses. This avoids the extra culling overhead that can make DragonAPI particles slower than vanilla. Requires restart."
        );

        optimizeChromatiCraftRenderEventFastPaths = config.getBoolean(
                "OptimizeChromatiCraftRenderEventFastPaths",
                "chromaticraft",
                true,
                "If true, adds common-path short-circuits to ChromatiCraft's DragonAPI block, entity, and tile render listeners. This keeps the DragonAPI callbacks intact for ChromatiCraft while skipping most work when none of ChromatiCraft's special render features are active. Requires restart."
        );

        optimizeChromatiCraftCliffsChunkGeneration = config.getBoolean(
                "OptimizeChromatiCraftCliffsChunkGeneration",
                "chromaticraft",
                true,
                "If true, skips ChromatiCraft's Glowing Cliffs auxiliary generation and cave-indicator passes on chunks that do not actually contain any Glowing Cliffs biome cells. This does not change generated content in relevant chunks; it only avoids wasted work in unrelated chunks. Requires restart."
        );

        disableChromatiCraftItemFabricator = config.getBoolean(
                "DisableChromatiCraftItemFabricator",
                "chromaticraft",
                true,
                "If true, prevents ChromatiCraft from registering the Item Fabricator casting recipe and its NEI recipe handler. Requires restart."
        );

        chromatiCraftNetherBedrockBreakableLikeObsidian = config.getBoolean(
                "NetherBedrockBreakableLikeObsidian",
                "chromaticraft",
                true,
                "If true, vanilla bedrock has obsidian hardness in the Nether only. Other dimensions remain unbreakable. Requires restart."
        );

        chromatiCraftNetherStructureShieldBreakableLikeObsidian = config.getBoolean(
                "NetherStructureShieldBreakableLikeObsidian",
                "chromaticraft",
                true,
                "If true, ChromatiCraft structure shielding stone has obsidian hardness and is not treated as unbreakable in the Nether only. Requires restart."
        );

        chromatiCraftNetherStructureHutEnabled = getChromatiCraftNetherStructureEnabled("Hut", true);
        chromatiCraftNetherStructureTempleEnabled = getChromatiCraftNetherStructureEnabled("Temple", true);
        chromatiCraftNetherStructureMazeEnabled = getChromatiCraftNetherStructureEnabled("Maze", true);
        chromatiCraftNetherStructureSpiralEnabled = getChromatiCraftNetherStructureEnabled("Spiral", true);
        chromatiCraftNetherStructureDioramaEnabled = getChromatiCraftNetherStructureEnabled("Diorama", true);
        chromatiCraftNetherHolesEnabled = config.getBoolean(
                "NetherHolesEnabled",
                "chromaticraft",
                true,
                "If false, disables ChromatiCraft's Nether roof holes with kill/hurt/push gate zones. Requires restart."
        );
        chromatiCraftNetherLavaRiversEnabled = config.getBoolean(
                "NetherLavaRiversEnabled",
                "chromaticraft",
                true,
                "If false, disables ChromatiCraft's floating lava/liquid rivers above the Nether roof. Requires restart."
        );

        chromatiCraftNetherStructureHutY = getChromatiCraftNetherStructureY("Hut", 128);
        chromatiCraftNetherStructureTempleY = getChromatiCraftNetherStructureY("Temple", 128);
        chromatiCraftNetherStructureMazeY = getChromatiCraftNetherStructureY("Maze", 128);
        chromatiCraftNetherStructureSpiralY = getChromatiCraftNetherStructureY("Spiral", 128);
        chromatiCraftNetherStructureDioramaY = getChromatiCraftNetherStructureY("Diorama", 128);
        chromatiCraftNetherHolesY = config.getInt(
                "NetherHolesY",
                "chromaticraft",
                127,
                1,
                254,
                "Base Y level for ChromatiCraft's Nether roof holes with kill/hurt/push gate zones. The top bedrock cap is placed one block above this. Requires restart."
        );
        chromatiCraftNetherLavaRiverMinY = config.getInt(
                "NetherLavaRiverMinY",
                "chromaticraft",
                127,
                1,
                255,
                "Minimum Y level for ChromatiCraft's floating lava/liquid rivers above the Nether roof. Requires restart."
        );
        chromatiCraftNetherLavaRiverMaxY = config.getInt(
                "NetherLavaRiverMaxY",
                "chromaticraft",
                240,
                1,
                255,
                "Maximum Y level for ChromatiCraft's floating lava/liquid rivers above the Nether roof. Requires restart."
        );
        if (chromatiCraftNetherLavaRiverMinY > chromatiCraftNetherLavaRiverMaxY) {
            int swap = chromatiCraftNetherLavaRiverMinY;
            chromatiCraftNetherLavaRiverMinY = chromatiCraftNetherLavaRiverMaxY;
            chromatiCraftNetherLavaRiverMaxY = swap;
        }

        disableThermalDynamicsFacades = config.getBoolean(
                "DisableThermalDynamicsFacades",
                "general",
                true,
                "If true, disables ThermalDynamics facades/covers (creative tab, recipes, placement, and persisted cover data)."
        );

        thaumcraftOnlyWarpResearchRequiresMinigame = config.getBoolean(
                "OnlyWarpResearchRequiresMinigame",
                "thaumcraft",
                true,
                "If true, direct-learn warp research creates a Thaumcraft research note instead of completing immediately. This lets Thaumcraft's own research_difficulty setting stay in control while keeping warp research dangerous. Requires restart."
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

        explosionsIgnoreThinPlantsForExposure = config.getBoolean(
                "ExplosionsIgnoreThinPlantsForExposure", "general", true,
                "If true, explosion exposure ray checks ignore collisionless plants like tall grass and Rift Flux barley so they do not block explosion damage.");

        enableMeleeDamageTooltip = config.getBoolean(
                "EnableMeleeDamageTooltip", "general", true,
                "Replace '+X Attack Damage' with a single gray 'X.X Melee Damage' line (includes +1 base and Sharpness)."
        );

        enableUniversalDurabilityTooltip = config.getBoolean(
                "EnableUniversalDurabilityTooltip", "general", true,
                "If true, any item with durability shows a gray 'Durability: current/max' tooltip line."
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

        enableThornsArmorTweaks = config.getBoolean(
                "EnableThornsArmorTweaks",
                "combat",
                true,
                "If true, Thorns can be applied to any armor at the enchanting table and does not consume extra armor durability when it triggers. Requires restart."
        );

        vanillaArrowInaccuracy = config.getFloat(
                "VanillaArrowInaccuracy",
                "combat",
                0.0F,
                0.0F,
                100.0F,
                "Inaccuracy passed to vanilla EntityArrow.setThrowableHeading. Vanilla bow default is 1.0; 0.0 removes random spread."
        );

        riftExplorerSpecialArrowInaccuracy = config.getFloat(
                "RiftExplorerSpecialArrowInaccuracy",
                "combat",
                0.0F,
                0.0F,
                100.0F,
                "Inaccuracy for Rift Explorer EntitySpecialArrow. Original value is 1.0; 0.0 removes random spread."
        );

        riftExplorerDartInaccuracy = config.getFloat(
                "RiftExplorerDartInaccuracy",
                "combat",
                1.0F,
                0.0F,
                100.0F,
                "Inaccuracy for Rift Explorer EntityDart. Original value is 1.0; 0.0 removes random spread."
        );

        riftExplorerPebbleInaccuracy = config.getFloat(
                "RiftExplorerPebbleInaccuracy",
                "combat",
                1.0F,
                0.0F,
                100.0F,
                "Inaccuracy for Rift Explorer EntityPebble. Original value is 1.0; 0.0 removes random spread."
        );

        enableToroHealthModule = config.getBoolean(
                "EnableToroHealthModule",
                "combat",
                true,
                "Master switch for RiftFlux-integrated ToroHealth damage/heal popoff particles."
        );

        toroHealthShowDamageParticles = config.getBoolean(
                "ToroHealthShowDamageParticles",
                "combat",
                true,
                "Show floating damage/heal numbers over entities."
        );

        toroHealthShowThroughWalls = config.getBoolean(
                "ToroHealthRenderThroughWalls",
                "combat",
                false,
                "Render ToroHealth particles through walls."
        );

        toroHealthParticleSize = config.getFloat(
                "ToroHealthParticleSize",
                "combat",
                2.5F,
                0.1F,
                32.0F,
                "Size multiplier for ToroHealth floating numbers."
        );

        String toroHealthHealColorRaw = config.getString(
                "ToroHealthHealColor",
                "combat",
                "LIME",
                "Heal text color name or hex (#RRGGBB, 0xRRGGBB, or RRGGBB)."
        );
        String toroHealthDamageColorRaw = config.getString(
                "ToroHealthDamageColor",
                "combat",
                "GOLD",
                "Damage text color name or hex (#RRGGBB, 0xRRGGBB, or RRGGBB)."
        );
        toroHealthHealColor = parseToroHealthColor(toroHealthHealColorRaw, 0x00FF00);
        toroHealthDamageColor = parseToroHealthColor(toroHealthDamageColorRaw, 0xFF0000);

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

        jukeboxAutoLoopEnabled = config.getBoolean(
                "JukeboxAutoLoopEnabled", "general", true,
                "If true, jukeboxes automatically replay the inserted disc after it finishes."
        );

        jukeboxLoopDelaySeconds = config.getInt(
                "JukeboxLoopDelaySeconds", "general", 5, 0, 3600,
                "Generic seconds to wait after a known/configured disc finishes before a looping jukebox starts it again."
        );

        jukeboxUnknownTrackLoopAfterSeconds = config.getInt(
                "JukeboxUnknownTrackLoopAfterSeconds", "general", 150, 1, 7200,
                "Total seconds after playback starts before replaying records not listed in JukeboxTrackTimings."
        );

        jukeboxTrackTimings = config.getStringList(
                "JukeboxTrackTimings",
                "general",
                DEFAULT_JUKEBOX_TRACK_TIMINGS,
                "Known jukebox track timings. Format: name=lengthSeconds or name=lengthSeconds,loopDelaySeconds.\n" +
                        "Name can be an ItemRecord recordName such as cat, an item registry name such as minecraft:record_cat, or a modded item name.\n" +
                        "Durations also accept mm:ss, for example minecraft:record_cat=3:05,5.\n" +
                        "Entries here override built-in vanilla timings and can provide per-track loop delays."
        );

        jukeboxRedstoneRestartEnabled = config.getBoolean(
                "JukeboxRedstoneRestartEnabled", "general", true,
                "If true, a redstone signal restarts an inserted jukebox disc from the beginning."
        );
        pulseLockedHoppers = config.getBoolean(
                "PulseLockedHoppers", "general", true,
                "If true, hoppers do not tick or transfer items normally. Each rising redstone pulse lets the hopper pull once and then push once. A constant signal only triggers once until it turns off and on again. Requires restart."
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

        enableFenceTextureModule = config.getBoolean(
                "EnableFenceTextureModule",
                "client",
                true,
                "Master toggle for the custom fence textures, fence connections, and axe-based original-texture overrides."
        );

        disableFencePumpkinConnections = config.getBoolean(
                "DisableFencePumpkinConnections",
                "general",
                true,
                "If true, fences will not connect to pumpkins or jack o'lanterns."
        );

        enableJackOLanternHelmet = config.getBoolean(
                "EnableJackOLanternHelmet",
                "general",
                true,
                "If true, jack o' lanterns can be worn in the head slot and behave like pumpkins for overlays and Endermen."
        );

        disablePumpkinOverlay = config.getBoolean(
                "DisablePumpkinOverlay",
                "client",
                true,
                "If true, hides the pumpkin blur overlay while wearing a pumpkin."
        );

        centerCrosshair = config.getBoolean(
                "CenterCrosshair",
                "client",
                true,
                "If true, render the crosshair around the exact center of the scaled GUI instead of using vanilla integer positioning. Requires restart."
        );

        disableUnderwaterOverlay = config.getBoolean(
                "DisableUnderwaterOverlay",
                "client",
                true,
                "If true, hides the underwater distortion overlay while submerged."
        );

        fixUnderwaterMobDarkening = config.getBoolean(
                "FixUnderwaterMobDarkening",
                "client",
                true,
                "If true, living entities use water-surface light while underwater so mobs do not become unusually dark when submerged."
        );

        enablePostProcessing = config.getBoolean(
                "EnablePostProcessing",
                POST_PROCESSING_CATEGORY,
                true,
                "If true, applies configurable post processing to the 3D scene after world and hand rendering. HUDs and menus are left untouched."
        );
        postProcessConfigHotSwap = config.getBoolean(
                "EnablePostProcessingConfigHotSwap",
                POST_PROCESSING_CATEGORY,
                false,
                "If true, post processing checks riftflux.cfg during play and reloads changed values without a restart."
        );
        postProcessDimensionWhitelist = ConfigResolver.parseIntegerList(config.getStringList(
                "PostProcessDimensionWhitelist",
                POST_PROCESSING_CATEGORY,
                new String[0],
                "Dimension IDs where post processing may render. Leave empty to allow every dimension unless blacklisted."
        ));
        postProcessDimensionBlacklist = ConfigResolver.parseIntegerList(config.getStringList(
                "PostProcessDimensionBlacklist",
                POST_PROCESSING_CATEGORY,
                new String[0],
                "Dimension IDs where post processing must not render. Blacklist takes precedence over whitelist."
        ));
        postProcessCelestialBloomDimensionWhitelist = ConfigResolver.parseIntegerList(config.getStringList(
                "PostProcessCelestialBloomDimensionWhitelist",
                POST_PROCESSING_CATEGORY,
                new String[0],
                "Dimension IDs where celestial bloom may render. Leave empty to allow every dimension unless blacklisted."
        ));
        postProcessCelestialBloomDimensionBlacklist = ConfigResolver.parseIntegerList(config.getStringList(
                "PostProcessCelestialBloomDimensionBlacklist",
                POST_PROCESSING_CATEGORY,
                new String[0],
                "Dimension IDs where celestial bloom must not render. Blacklist takes precedence over whitelist."
        ));
        postProcessGamma = config.getFloat(
                "PostProcessGamma",
                POST_PROCESSING_CATEGORY,
                0.525F,
                0.0F,
                1.0F,
                "Gamma adjustment using the reference final-shader curve. 0.5 is unchanged; higher values brighten, lower values darken."
        );
        postProcessBrightness = config.getFloat(
                "PostProcessBrightness",
                POST_PROCESSING_CATEGORY,
                0.0F,
                -1.0F,
                1.0F,
                "Flat brightness offset added to RGB after gamma. 0 is unchanged; negative values darken, positive values brighten."
        );
        postProcessContrast = config.getFloat(
                "PostProcessContrast",
                POST_PROCESSING_CATEGORY,
                -0.033F,
                -1.0F,
                1.0F,
                "Contrast adjustment around mid-gray. 0 is unchanged; negative values flatten, positive values increase contrast."
        );
        postProcessExposure = config.getFloat(
                "PostProcessExposure",
                POST_PROCESSING_CATEGORY,
                0.25F,
                -1.0F,
                1.0F,
                "Exposure multiplier adjustment. 0 is unchanged; positive values brighten, negative values darken."
        );
        postProcessSunExposureCompensationPercent = config.getFloat(
                "PostProcessSunExposureCompensationPercent",
                POST_PROCESSING_CATEGORY,
                333.0F,
                0.0F,
                1000.0F,
                "How much the sun is pre-compensated against PostProcessExposure before color grading. 100 roughly cancels exposure; higher values darken it further before post processing; 0 lets exposure affect it normally."
        );
        postProcessSunEventExposureCompensationPercent = config.getFloat(
                "PostProcessSunEventExposureCompensationPercent",
                POST_PROCESSING_CATEGORY,
                369.0F,
                0.0F,
                1000.0F,
                "How much custom/random sun event textures are pre-compensated against PostProcessExposure before color grading. 100 roughly cancels exposure; higher values darken them further before post processing; 0 lets exposure affect them normally."
        );
        postProcessMoonExposureCompensationPercent = config.getFloat(
                "PostProcessMoonExposureCompensationPercent",
                POST_PROCESSING_CATEGORY,
                369.0F,
                0.0F,
                1000.0F,
                "How much the moon is pre-compensated against PostProcessExposure before color grading. 100 roughly cancels exposure; higher values darken it further before post processing; 0 lets exposure affect it normally."
        );
        postProcessMoonEventExposureCompensationPercent = config.getFloat(
                "PostProcessMoonEventExposureCompensationPercent",
                POST_PROCESSING_CATEGORY,
                369.0F,
                0.0F,
                1000.0F,
                "How much custom/random moon event textures are pre-compensated against PostProcessExposure before color grading. 100 roughly cancels exposure; higher values darken them further before post processing; 0 lets exposure affect them normally."
        );
        postProcessSaturationPercent = config.getFloat(
                "PostProcessSaturationPercent",
                POST_PROCESSING_CATEGORY,
                7.5F,
                -100.0F,
                100.0F,
                "Saturation adjustment using the reference final-shader math. 0 is unchanged; negative values desaturate, positive values oversaturate."
        );
        postProcessRedMultiplier = config.getFloat(
                "PostProcessRedMultiplier",
                POST_PROCESSING_CATEGORY,
                1.1F,
                0.0F,
                3.0F,
                "Red channel multiplier applied after saturation. 1 is unchanged."
        );
        postProcessGreenMultiplier = config.getFloat(
                "PostProcessGreenMultiplier",
                POST_PROCESSING_CATEGORY,
                1.0F,
                0.0F,
                3.0F,
                "Green channel multiplier applied after saturation. 1 is unchanged."
        );
        postProcessBlueMultiplier = config.getFloat(
                "PostProcessBlueMultiplier",
                POST_PROCESSING_CATEGORY,
                1.1F,
                0.0F,
                3.0F,
                "Blue channel multiplier applied after saturation. 1 is unchanged."
        );
        postProcessColorGradeShadowProtection = config.getFloat(
                "PostProcessColorGradeShadowProtection",
                POST_PROCESSING_CATEGORY,
                25.0F,
                0.0F,
                100.0F,
                "Protects dark pixels from desaturation and gamma, in percent. 0 affects all brightness levels; higher values preserve more shadows and darkness."
        );
        postProcessBloomStrengthPercent = config.getFloat(
                "PostProcessBloomStrengthPercent",
                POST_PROCESSING_CATEGORY,
                66.0F,
                0.0F,
                100.0F,
                "Strength of the subtle bright-pass bloom, in percent. 0 disables bloom while leaving desaturation/gamma active."
        );
        postProcessBloomThreshold = config.getFloat(
                "PostProcessBloomThreshold",
                POST_PROCESSING_CATEGORY,
                0.0F,
                0.0F,
                1.0F,
                "Brightness threshold where bloom starts. Lower values bloom more of the scene; higher values restrict bloom to brighter pixels."
        );
        postProcessBloomRadiusPixels = config.getFloat(
                "PostProcessBloomRadiusPixels",
                POST_PROCESSING_CATEGORY,
                6.6F,
                0.25F,
                16.0F,
                "Approximate bloom sample radius in screen pixels."
        );
        postProcessBloomResolutionPercent = config.getFloat(
                "PostProcessBloomResolutionPercent",
                POST_PROCESSING_CATEGORY,
                100.0F,
                25.0F,
                100.0F,
                "Internal bloom buffer resolution, in percent of screen resolution. 100 preserves current bloom rendering; lower values reduce bloom GPU cost and soften/reduce detail. Requires EnablePostProcessingConfigHotSwap for live changes."
        );
        enablePostProcessWorldBloomCache = config.getBoolean(
                "EnablePostProcessWorldBloomCache",
                POST_PROCESSING_CATEGORY,
                true,
                "If true, world bloom precomputes its full-resolution bright pass once per pixel and reuses it for blur samples. Falls back to direct bloom if framebuffer caching is unavailable."
        );
        enablePostProcessCelestialBloomCache = config.getBoolean(
                "EnablePostProcessCelestialBloomCache",
                POST_PROCESSING_CATEGORY,
                true,
                "If true, celestial bloom precomputes its full-resolution bright pass once per pixel and reuses it for blur samples. Falls back to direct bloom if framebuffer caching is unavailable."
        );
        postProcessBloomCacheHighPrecision = config.getBoolean(
                "PostProcessBloomCacheHighPrecision",
                POST_PROCESSING_CATEGORY,
                true,
                "If true, bloom caches prefer a 16-bit-per-channel texture to minimize interpolation and banding differences. Automatically falls back to RGBA8 if unsupported."
        );
        postProcessBloomCacheUsePackedFormat = config.getBoolean(
                "PostProcessBloomCacheUsePackedFormat",
                POST_PROCESSING_CATEGORY,
                false,
                "If true, bloom caches use RGB10_A2 to halve cache memory and bandwidth versus RGBA16. This may slightly change bloom appearance on some GPUs. Takes precedence over PostProcessBloomCacheHighPrecision and falls back automatically if unsupported."
        );
        enablePostProcessPersistentRenderTarget = config.getBoolean(
                "EnablePostProcessPersistentRenderTarget",
                POST_PROCESSING_CATEGORY,
                true,
                "If true, renders the 3D scene directly into persistent color/depth textures and ping-pongs post processing without framebuffer copies. Automatically falls back to the copy renderer when incompatible."
        );
        postProcessBloomAffectsHeldItem = config.getBoolean(
                "PostProcessBloomAffectsHeldItem",
                POST_PROCESSING_CATEGORY,
                false,
                "If true, bloom is applied after first-person hand and held item rendering. If false, bloom is applied before the hand renders so held items do not glow."
        );
        postProcessCelestialBloomStrengthPercent = config.getFloat(
                "PostProcessCelestialBloomStrengthPercent",
                POST_PROCESSING_CATEGORY,
                66.0F,
                0.0F,
                800.0F,
                "Extra bloom strength for bright non-blue sky objects like the sun, moon, and stars. This sky-only pass is separate from normal world bloom."
        );
        postProcessCelestialBloomThreshold = config.getFloat(
                "PostProcessCelestialBloomThreshold",
                POST_PROCESSING_CATEGORY,
                0.0F,
                0.0F,
                100.0F,
                "Sky bloom inclusion, in percent. 0 blooms only small star-like points; higher values gradually include broader bright sun, moon, and sky pixels."
        );
        postProcessCelestialBloomRadiusPixels = config.getFloat(
                "PostProcessCelestialBloomRadiusPixels",
                POST_PROCESSING_CATEGORY,
                2.5F,
                0.25F,
                32.0F,
                "Approximate sky-only bloom radius in screen pixels for sun, moon, and stars. Separate from normal world bloom radius."
        );
        postProcessCelestialBloomStartTime = config.getInt(
                "PostProcessCelestialBloomStartTime",
                POST_PROCESSING_CATEGORY,
                12500,
                0,
                23999,
                "World time tick when sky-only bloom starts. No sky bloom is applied before this time unless the window wraps past midnight."
        );
        postProcessCelestialBloomEndTime = config.getInt(
                "PostProcessCelestialBloomEndTime",
                POST_PROCESSING_CATEGORY,
                21500,
                0,
                23999,
                "World time tick when sky-only bloom ends. No sky bloom is applied after this time unless the window wraps past midnight."
        );

        deathRespawnDelaySeconds = config.getInt(
                "DeathRespawnDelaySeconds",
                "client",
                12,
                0,
                120,
                "Delay in seconds before the Respawn button can be clicked after dying. Set to 0 to disable."
        );

        allowChatOnDeathScreen = config.getBoolean(
                "AllowChatOnDeathScreen",
                "client",
                true,
                "If true, pressing chat/command keys on the death screen opens chat so you can type before respawning."
        );

        allowChatOnPauseScreen = config.getBoolean(
                "AllowChatOnPauseScreen",
                "client",
                true,
                "If true, pressing chat/command keys on the pause screen opens chat without leaving the pause menu."
        );

        enableBetaLeavesLook = config.getBoolean(
                "EnableBetaLeavesLook",
                "client",
                true,
                "If true, uses beta-style leaf interior shading while keeping normal leaf shadows below trees. Requires restart."
        );

        enableChatBubblesModule = config.getBoolean(
                "EnableChatBubblesModule",
                "chatbubbles",
                true,
                "If true, renders chat bubbles above players when their chat messages can be parsed."
        );
        chatBubblesShowOwnMessages = config.getBoolean(
                "ChatBubblesShowOwnMessages",
                "chatbubbles",
                true,
                "If true, your own parsed chat messages can render above your own head in third-person, including in singleplayer."
        );
        chatBubblesShowBackground = config.getBoolean(
                "ChatBubblesShowBackground",
                "chatbubbles",
                false,
                "If true, chat bubbles render their speech-bubble background and tail behind the text."
        );
        chatBubblesRenderInPhotoMode = config.getBoolean(
                "ChatBubblesRenderInPhotoMode",
                "chatbubbles",
                true,
                "If true, chat bubbles are rendered while isometric photo mode is active."
        );
        String chatBubblesOwnBubbleColorRaw = config.getString(
                "ChatBubblesOwnBubbleColor",
                "chatbubbles",
                "",
                "Optional custom color for your own player's chat bubble. Leave blank or set to AUTO to use local gold for yourself and UUID-based colors for others. Accepts #RRGGBB, 0xRRGGBB, or named colors; custom values are advertised to the server so other RiftFlux clients can see them."
        );
        String chatBubblesOwnBubbleColorValue =
                chatBubblesOwnBubbleColorRaw == null ? "" : chatBubblesOwnBubbleColorRaw.trim();
        chatBubblesUseCustomOwnBubbleColor =
                !chatBubblesOwnBubbleColorValue.isEmpty()
                        && !"AUTO".equalsIgnoreCase(chatBubblesOwnBubbleColorValue);
        chatBubblesOwnBubbleColor = parseRgbColor(
                chatBubblesUseCustomOwnBubbleColor ? chatBubblesOwnBubbleColorValue : null,
                0xFFD700
        );
        String chatBubblesOwnTextColorRaw = config.getString(
                "ChatBubblesOwnTextColor",
                "chatbubbles",
                "AUTO",
                "Optional custom color for your own player's chat bubble text. Leave blank or set to AUTO for the default white text, or UUID-random text if ChatBubblesRandomizeTextColorByUuid is enabled. Accepts #RRGGBB, 0xRRGGBB, or named colors; custom values are advertised to the server so other RiftFlux clients can see them."
        );
        String chatBubblesOwnTextColorValue =
                chatBubblesOwnTextColorRaw == null ? "" : chatBubblesOwnTextColorRaw.trim();
        chatBubblesUseCustomOwnTextColor =
                !chatBubblesOwnTextColorValue.isEmpty()
                        && !"AUTO".equalsIgnoreCase(chatBubblesOwnTextColorValue);
        chatBubblesOwnTextColor = parseRgbColor(
                chatBubblesUseCustomOwnTextColor ? chatBubblesOwnTextColorValue : null,
                0xFFFFFF
        );
        chatBubblesRandomizeTextColorByUuid = config.getBoolean(
                "ChatBubblesRandomizeTextColorByUuid",
                "chatbubbles",
                false,
                "If true, chat bubble text uses a stable UUID-based random color for players who have not set their own synced text color."
        );
        chatBubblesMessageGap = config.getInt(
                "ChatBubblesMessageGap",
                "chatbubbles",
                CHAT_BUBBLES_MESSAGE_GAP_DEFAULT,
                CHAT_BUBBLES_MESSAGE_GAP_MIN,
                CHAT_BUBBLES_MESSAGE_GAP_MAX,
                CHAT_BUBBLES_MESSAGE_GAP_COMMENT
        );
        chatBubblesBlackTextBackground = config.getBoolean(
                "ChatBubblesBlackTextBackground",
                "chatbubbles",
                false,
                "If true, draws a solid black bar behind each chat bubble text line."
        );
        chatBubblesBlackTextBackgroundOpacity = clampChatBubblesBlackTextBackgroundOpacity(config.getFloat(
                "ChatBubblesBlackTextBackgroundOpacity",
                "chatbubbles",
                CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_DEFAULT,
                CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_MIN,
                CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_MAX,
                CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_COMMENT
        ));

        chatBubblesTextScale = clampChatBubblesTextScale(config.getFloat(
                "ChatBubblesTextScale",
                "chatbubbles",
                CHAT_BUBBLES_TEXT_SCALE_DEFAULT,
                CHAT_BUBBLES_TEXT_SCALE_MIN,
                CHAT_BUBBLES_TEXT_SCALE_MAX,
                CHAT_BUBBLES_TEXT_SCALE_COMMENT
        ));

        chatBubblesMessageLifetimeSeconds = config.getInt(
                "ChatBubblesMessageLifetimeSeconds",
                "chatbubbles",
                12,
                1,
                120,
                "How long chat bubbles remain visible, in seconds."
        );

        chatBubblesMaxLineLength = config.getInt(
                "ChatBubblesMaxLineLength",
                "chatbubbles",
                30,
                8,
                120,
                "Maximum approximate characters per rendered chat bubble line before wrapping."
        );

        enableChatSelectionCopy = config.getBoolean(
                "EnableChatSelectionCopy",
                "client",
                true,
                "Allow click-drag selection in chat and copy the exact visible text, including clickable command output."
        );

        enableIsometricPhotoMode = config.getBoolean(
                "EnableIsometricPhotoMode",
                "client",
                true,
                "Master toggle for the isometric photo mode feature and its keybind/mixins."
        );

        isometricPhotoModeMaxZoomOut = config.getFloat(
                "IsometricPhotoModeMaxZoomOut",
                "client",
                256.0F,
                4.0F,
                4096.0F,
                "Maximum orthographic view height allowed when zooming out in isometric photo mode."
        );

        isometricPhotoModeHoldRotateDegreesPerTick = config.getFloat(
                "IsometricPhotoModeHoldRotateDegreesPerTick",
                "client",
                3.3F,
                0.05F,
                10.0F,
                "Continuous horizontal orbit speed in isometric photo mode while holding left/right, in degrees per client tick."
        );

        isometricPhotoModeHideNetherNetherrackAndBedrock = config.getBoolean(
                "IsometricPhotoModeHideNetherNetherrackAndBedrock",
                "client",
                true,
                "If true, netherrack and bedrock are hidden while isometric photo mode is active in the Nether."
        );

        enableWorldTooltips = config.getBoolean(
                "EnableWorldTooltips",
                "client",
                true,
                "Render hovered item tooltips directly in the world."
        );

        worldTooltipsHideModName = config.getBoolean(
                "WorldTooltipsHideModName",
                "client",
                true,
                "Hide mod names on world item tooltips."
        );

        worldTooltipsMaxDistance = config.getInt(
                "WorldTooltipsMaxDistance",
                "client",
                5,
                2,
                64,
                "Maximum distance in blocks where world item tooltips can be shown."
        );

        worldTooltipsOverrideOutline = config.getBoolean(
                "WorldTooltipsOverrideOutline",
                "client",
                false,
                "If true, world item tooltips use WorldTooltipsOutlineColor instead of item rarity colors."
        );

        worldTooltipsTransparency = config.getFloat(
                "WorldTooltipsTransparency",
                "client",
                0.0F,
                0.0F,
                1.0F,
                "Opacity for world item tooltips. 0.0 is fully transparent and 1.0 is fully opaque."
        );

        worldTooltipsVerticalOffset = config.getFloat(
                "WorldTooltipsVerticalOffset",
                "client",
                0.55F,
                -2.0F,
                4.0F,
                "Vertical anchor offset for world item tooltips in blocks. Negative values move the tooltip down; positive values move it up."
        );

        worldTooltipsTextScale = config.getFloat(
                "WorldTooltipsTextScale",
                "client",
                1.0F,
                0.25F,
                4.0F,
                "Scale multiplier for world tooltip text and its background. 1.0 is the default size."
        );

        worldTooltipsHoverRadiusHorizontal = config.get(
                "client",
                "WorldTooltipsHoverRadiusHorizontal",
                0.12D,
                "Extra horizontal hover radius used when aiming at dropped-item world tooltips. Higher values make tooltips easier to trigger."
        ).getDouble(0.12D);

        worldTooltipsHoverRadiusVertical = config.get(
                "client",
                "WorldTooltipsHoverRadiusVertical",
                0.12D,
                "Extra vertical hover radius used when aiming at dropped-item world tooltips. Higher values make tooltips easier to trigger."
        ).getDouble(0.12D);

        String worldTooltipsBackgroundColorRaw = config.getString(
                "WorldTooltipsBackgroundColor",
                "client",
                "0x100010",
                "Background color for world item tooltips. Accepts #RRGGBB, 0xRRGGBB, or RRGGBB."
        );
        String worldTooltipsOutlineColorRaw = config.getString(
                "WorldTooltipsOutlineColor",
                "client",
                "0x5000FF",
                "Outline color for world item tooltips when WorldTooltipsOverrideOutline is enabled. Accepts #RRGGBB, 0xRRGGBB, or RRGGBB."
        );
        worldTooltipsBackgroundColor = parseRgbColor(worldTooltipsBackgroundColorRaw, 0x100010);
        worldTooltipsOutlineColor = parseRgbColor(worldTooltipsOutlineColorRaw, 0x5000FF);

        enableCelestialEventTextures = config.getBoolean(
                "EnableCelestialEventTextures",
                "celestial",
                true,
                "If true, sun/moon textures can be swapped on random special event days."
        );

        celestialSunEventChance = config.getFloat(
                "CelestialSunEventChance",
                "celestial",
                0.10F,
                0.0F,
                1.0F,
                "Chance between 0.0 and 1.0 each day for the sun texture to use CelestialSunEventTexture."
        );

        celestialSunEventTextures = sanitizeCelestialTextureList(config.getStringList(
                "CelestialSunEventTextures",
                "celestial",
                new String[]{"riftflux:textures/environment/sun_event.png"},
                "List of candidate sun textures for event days. One is picked each event day.\n" +
                        "Format per entry: namespace:path\n" +
                        "If empty, falls back to CelestialSunEventTexture."
        ));
        config.getCategory("celestial")
                .get("CelestialSunEventTextures")
                .set(celestialSunEventTextures);

        celestialSunEventTexture = config.getString(
                "CelestialSunEventTexture",
                "celestial",
                "riftflux:textures/environment/sun_event.png",
                "Legacy single sun texture for event days. Used only when CelestialSunEventTextures is empty."
        );

        celestialMoonEventChance = config.getFloat(
                "CelestialMoonEventChance",
                "celestial",
                0.0F,
                0.0F,
                1.0F,
                "Chance between 0.0 and 1.0 each day for the moon texture to use CelestialMoonEventTexture."
        );

        celestialMoonEventTextures = sanitizeCelestialTextureList(config.getStringList(
                "CelestialMoonEventTextures",
                "celestial",
                new String[]{"riftflux:textures/environment/moon_event_phases.png"},
                "List of candidate moon phase-sheet textures for event days. One is picked each event day.\n" +
                        "Each texture must be a vanilla-style 4x2 phase sheet.\n" +
                        "A default tiled sheet based on the included moon event texture is provided.\n" +
                        "If empty, falls back to CelestialMoonEventTexture."
        ));
        config.getCategory("celestial")
                .get("CelestialMoonEventTextures")
                .set(celestialMoonEventTextures);

        celestialMoonEventTexture = config.getString(
                "CelestialMoonEventTexture",
                "celestial",
                "textures/environment/moon_phases.png",
                "Legacy single moon texture for event days. Used only when CelestialMoonEventTextures is empty."
        );
        celestialFullSunriseSunsetTint = config.getBoolean(
                "CelestialFullSunriseSunsetTint",
                "celestial",
                true,
                "If true, sunrise and sunset tint the full sky and fog instead of only the direction you are looking."
        );
        celestialFogMatchesSky = config.getBoolean(
                "CelestialFogMatchesSky",
                "celestial",
                true,
                "If true, normal overworld-style fog inherits the current sky tint, including biome sky color."
        );
        celestialBetaStyleFogBiomeTint = config.getBoolean(
                "CelestialBetaStyleFogBiomeTint",
                "celestial",
                false,
                "If true, overworld fog keeps a desaturated beta-style grey and only tints toward non-default biome sky colors. Takes precedence over CelestialFogMatchesSky."
        );
        celestialBetaStyleFogBiomeTintWeatherEvent = config.getBoolean(
                "CelestialBetaStyleFogBiomeTintWeatherEvent",
                "celestial",
                true,
                "If true, the chance-based fog event can activate during rain and thunderstorms even when CelestialBetaStyleFogBiomeTint itself is disabled. In multiplayer, the server owns this event state."
        );
        celestialFogChanceEventsUseSkyMatchingFog = config.getBoolean(
                "CelestialFogChanceEventsUseSkyMatchingFog",
                "celestial",
                true,
                "If true, the weather/day/night fog chance events use the CelestialFogMatchesSky color style with the day or night fog desaturation percent instead of beta-style biome fog. CelestialBetaStyleFogBiomeTint still forces beta-style fog when enabled."
        );
        celestialBetaStyleFogBiomeTintWeatherEventChancePercent = config.getFloat(
                "CelestialBetaStyleFogBiomeTintWeatherEventChancePercent",
                "celestial",
                30.0F,
                0.0F,
                100.0F,
                "Server-side chance that a rain or thunderstorm starts a fog event. Only used when CelestialBetaStyleFogBiomeTintWeatherEvent is true."
        );
        celestialBetaStyleFogBiomeTintDayEventChancePercent = config.getFloat(
                "CelestialBetaStyleFogBiomeTintDayEventChancePercent",
                "celestial",
                15.0F,
                0.0F,
                100.0F,
                "Server-side chance per Minecraft day that a fog event appears during the daytime without rain. 0 disables random daytime fog events."
        );
        celestialBetaStyleFogBiomeTintNightEventChancePercent = config.getFloat(
                "CelestialBetaStyleFogBiomeTintNightEventChancePercent",
                "celestial",
                15.0F,
                0.0F,
                100.0F,
                "Server-side chance per Minecraft night that a fog event appears during the night without rain. 0 disables random nighttime fog events."
        );
        celestialFogHorizonDimensionWhitelist = ConfigResolver.parseIntegerList(config.getStringList(
                "CelestialFogHorizonDimensionWhitelist",
                "celestial",
                new String[]{"0"},
                "Dimension IDs where RiftFlux fog and horizon changes may render. Leave empty to allow every dimension unless blacklisted."
        ));
        celestialFogHorizonDimensionBlacklist = ConfigResolver.parseIntegerList(config.getStringList(
                "CelestialFogHorizonDimensionBlacklist",
                "celestial",
                new String[0],
                "Dimension IDs where RiftFlux fog and horizon changes must not render. Blacklist takes precedence over whitelist."
        ));
        celestialFogChanceEventDimensionWhitelist = ConfigResolver.parseIntegerList(config.getStringList(
                "CelestialFogChanceEventDimensionWhitelist",
                "celestial",
                new String[]{"0"},
                "Dimension IDs where random foggy day, night, and weather events may occur. Leave empty to allow every dimension unless blacklisted."
        ));
        celestialFogChanceEventDimensionBlacklist = ConfigResolver.parseIntegerList(config.getStringList(
                "CelestialFogChanceEventDimensionBlacklist",
                "celestial",
                new String[0],
                "Dimension IDs where random foggy day, night, and weather events must not occur. Blacklist takes precedence over whitelist."
        ));
        celestialBlackNightFog = config.getBoolean(
                "CelestialBlackNightFog",
                "celestial",
                true,
                "If true, night-time fog is forced fully black after sunrise and sunset tint have finished. Ignored while CelestialFogMatchesSky or CelestialBetaStyleFogBiomeTint is enabled."
        );
        celestialFogDesaturationPercent = config.getFloat(
                "CelestialFogDesaturationPercent",
                "celestial",
                15.0F,
                0.0F,
                100.0F,
                "Desaturates fog colors by this percentage for RiftFlux celestial fog modes (CelestialFogMatchesSky, CelestialBlackNightFog, CelestialBetaStyleFogBiomeTint). 0 disables desaturation."
        );
        celestialNightFogDesaturationPercent = config.getFloat(
                "CelestialNightFogDesaturationPercent",
                "celestial",
                0.0F,
                0.0F,
                100.0F,
                "Separate night-time fog desaturation/brightness lift. 0 keeps night fog black; higher values make night fog less black without changing daytime fog."
        );
        celestialFogDistanceGradient = config.getBoolean(
                "CelestialFogDistanceGradient",
                "celestial",
                false,
                "If true, applies the depth-aware fog colour gradient after world rendering so entities and tile entities are covered by the same distant fog as blocks. [WIP]"
        );
        celestialFogDistanceGradientStrengthPercent = config.getFloat(
                "CelestialFogDistanceGradientStrengthPercent",
                "celestial",
                100.0F,
                0.0F,
                100.0F,
                "How strongly distant fog shifts toward the current sky colour. 100 applies the full colour shift to the fogged portion of far pixels."
        );
        celestialFogDistanceGradientStartPercent = config.getFloat(
                "CelestialFogDistanceGradientStartPercent",
                "celestial",
                30.0F,
                0.0F,
                100.0F,
                "Where the distance colour shift begins, as a percent through the active vanilla fog distance range."
        );
        celestialFogDistanceGradientEndPercent = config.getFloat(
                "CelestialFogDistanceGradientEndPercent",
                "celestial",
                100.0F,
                0.0F,
                100.0F,
                "Where the distance colour shift reaches full strength, as a percent through the active vanilla fog distance range."
        );
        celestialVoidFogStartHeight = config.getInt(
                "CelestialVoidFogStartHeight",
                "celestial",
                28,
                -256,
                512,
                "Approximate Y height where vanilla void fog starts ramping in. Vanilla behavior is about 28."
        );
        celestialVoidParticleStartHeight = config.getInt(
                "CelestialVoidParticleStartHeight",
                "celestial",
                8,
                -256,
                512,
                "Y height below which vanilla void particles can spawn. Vanilla behavior is 8."
        );
        if (celestialMoonEventTextures.length == 1
                && "textures/environment/moon_phases.png".equalsIgnoreCase(celestialMoonEventTextures[0])
                && "textures/environment/moon_phases.png".equalsIgnoreCase(celestialMoonEventTexture)) {
            celestialMoonEventTextures = new String[]{"riftflux:textures/environment/moon_event_phases.png"};
            config.getCategory("celestial")
                    .get("CelestialMoonEventTextures")
                    .set(celestialMoonEventTextures);
        }

        betaStarsEnabled = config.getBoolean(
                "EnableBetaStars",
                "celestial",
                true,
                "If true, replaces vanilla sky stars with larger beta-style stars."
        );

        betaStarsCount = config.getInt(
                "BetaStarsCount",
                "celestial",
                1500,
                0,
                20000,
                "How many RiftFlux beta-style stars to render. Higher values increase sky density."
        );

        betaStarsSizeMultiplier = config.getFloat(
                "BetaStarsSizeMultiplier",
                "celestial",
                1.0F,
                0.1F,
                8.0F,
                "Size multiplier for beta-style stars. 1.0 = default RiftFlux beta-star size."
        );
        betaStarsRandomPatternEveryNight = config.getBoolean(
                "BetaStarsRandomPatternEveryNight",
                "celestial",
                true,
                "If true, RiftFlux beta stars use a different deterministic star pattern each Minecraft night."
        );
        betaStarsRandomBlink = config.getBoolean(
                "BetaStarsTwinkle",
                "celestial",
                true,
                "If true, RiftFlux beta stars use subtle per-star randomized twinkling."
        );
        betaStarsTwinkleSpeedMultiplier = config.getFloat(
                "BetaStarsTwinkleSpeedMultiplier",
                "celestial",
                0.25F,
                0.05F,
                4.0F,
                "Speed multiplier for beta-star twinkling. Lower values twinkle less frequently; 0.25 is slow and subtle, 1.0 is the original faster twinkle."
        );

        betaStarsSunsetFadeStartTick = config.getInt(
                "BetaStarsSunsetFadeStartTick",
                "celestial",
                13200,
                12000,
                18000,
                "World time when RiftFlux beta stars start fading in during sunset. Increase this if stars appear too early."
        );
        betaStarsSunsetFadeEndTick = config.getInt(
                "BetaStarsSunsetFadeEndTick",
                "celestial",
                15000,
                12001,
                19000,
                "World time when RiftFlux beta stars finish fading in during sunset. Must be after BetaStarsSunsetFadeStartTick."
        );
        if (betaStarsSunsetFadeEndTick <= betaStarsSunsetFadeStartTick) {
            betaStarsSunsetFadeEndTick = betaStarsSunsetFadeStartTick + 1;
        }

        betaStarsSpinWithSunMoon = config.getBoolean(
                "BetaStarsSpinWithSunMoon",
                "celestial",
                true,
                "If true, RiftFlux beta stars use the same celestial rotation as the sun and moon. If false, they stay fixed."
        );

        betaStarsDisableBetterSkiesStars = config.getBoolean(
                "BetaStarsDisableBetterSkiesStars",
                "celestial",
                true,
                "If true, suppresses MCPatcherForge/BetterSkies star layers while beta stars are enabled so RiftFlux beta stars replace them."
        );
        suppressTwilightForestStars = config.getBoolean(
                "SuppressTwilightForestStars",
                "celestial",
                true,
                "If true, suppresses the Twilight Forest sky renderer's built-in stars without affecting its sky color, fog, or sky planes."
        );

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

        zeldaHeartContainerDropMobIds = config.getStringList(
                "HeartContainerDropMobIds",
                "zelda",
                new String[]{"enderdragon", "wither", "eyeofcthulhu"},
                "Normalized entity ids, simple class names, or full class names that should always drop a heart container.\n"
                        + "Examples: wither, enderdragon, eyeofcthulhu, entitywither, net.minecraft.entity.boss.EntityWither\n"
                        + "Leave empty to disable guaranteed heart container drops."
        );

        zeldaHeartContainerFirstKillOnly = config.getBoolean(
                "HeartContainerFirstKillOnly",
                "zelda",
                true,
                "If true, each configured entity in HeartContainerDropMobIds drops a heart container only the first time each player kills that specific entity.\n"
                        + "Example: a player can get one from their first wither kill, one from their first ender dragon kill, and one from their first Eye of Cthulhu kill.\n"
                        + "Only player-caused kills count when this is enabled."
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

        dssEnabled = config.getBoolean(
                "EnableDSS",
                "Darwin Sprinting System",
                true,
                "If false, disables the DSS stamina system, HUD, and commands."
        );

        dssMaxSprintingTimeSeconds = config.get(
                "Darwin Sprinting System",
                "MaxSprintingTimeSeconds",
                15.0,
                "Max sprinting time in seconds (1.0 = 1s)."
        ).getDouble(15.0);

        dssOverchargeRegenTimeSeconds = config.get(
                "Darwin Sprinting System",
                "OverchargeRegenTimeSeconds",
                22.5,
                "Time to recover after stamina reaches 0% (1.0 = 1s)."
        ).getDouble(22.5);

        dssEnableEnchantments = config.getBoolean(
                "EnableEnchantments",
                "Darwin Sprinting System",
                true,
                "Enable or disable DSS stamina enchantments."
        );

        dssEnchantmentMaxStaminaId = config.getInt(
                "EnchantmentMaxStaminaId",
                "Darwin Sprinting System",
                120,
                0,
                255,
                "Enchantment ID for DSS Max Stamina."
        );

        dssEnchantmentStaminaRegenId = config.getInt(
                "EnchantmentStaminaRegenId",
                "Darwin Sprinting System",
                121,
                0,
                255,
                "Enchantment ID for DSS Stamina Regeneration."
        );

        dssEnchantmentOverloadReductionId = config.getInt(
                "EnchantmentOverloadReductionId",
                "Darwin Sprinting System",
                122,
                0,
                255,
                "Enchantment ID for DSS Overload Reduction."
        );

        dssEnchantmentMaxStaminaSeconds = config.get(
                "Darwin Sprinting System",
                "EnchantmentMaxStaminaSeconds",
                1.0,
                "Increase max stamina per enchantment level (in seconds)."
        ).getDouble(1.0);

        dssEnchantmentStaminaRegenSeconds = config.get(
                "Darwin Sprinting System",
                "EnchantmentStaminaRegenSeconds",
                0.5,
                "Increase stamina regeneration per enchantment level (in seconds)."
        ).getDouble(0.5);

        dssEnchantmentOverloadReductionSeconds = config.get(
                "Darwin Sprinting System",
                "EnchantmentOverloadReductionSeconds",
                1.0,
                "Reduce overcharge recovery time per enchantment level (in seconds)."
        ).getDouble(1.0);

        dssEnablePotionEffects = config.getBoolean(
                "EnablePotionEffects",
                "Darwin Sprinting System",
                false,
                "Enable or disable DSS potion effect modifiers."
        );

        dssPotionExpandMaxStaminaIds = config.get(
                "Darwin Sprinting System",
                "PotionExpandMaxStaminaIds",
                "",
                "Potion IDs that expand max stamina (comma-separated)."
        ).getString();

        dssPotionReduceMaxStaminaIds = config.get(
                "Darwin Sprinting System",
                "PotionReduceMaxStaminaIds",
                "",
                "Potion IDs that reduce max stamina (comma-separated)."
        ).getString();

        dssPotionReduceRegenIds = config.get(
                "Darwin Sprinting System",
                "PotionReduceRegenIds",
                "",
                "Potion IDs that reduce stamina regeneration (comma-separated)."
        ).getString();

        dssPotionRegenSpeedIds = config.get(
                "Darwin Sprinting System",
                "PotionRegenSpeedIds",
                "",
                "Potion IDs that speed up stamina regeneration (comma-separated)."
        ).getString();

        dssPotionExpandMaxStaminaMultiplier = config.get(
                "Darwin Sprinting System",
                "PotionExpandMaxStaminaMultiplier",
                0.2,
                "Percent to expand max stamina (0.2 = 20%)."
        ).getDouble(0.2);

        dssPotionReduceMaxStaminaMultiplier = config.get(
                "Darwin Sprinting System",
                "PotionReduceMaxStaminaMultiplier",
                0.2,
                "Percent to reduce max stamina (0.2 = 20%)."
        ).getDouble(0.2);

        dssPotionReduceRegenMultiplier = config.get(
                "Darwin Sprinting System",
                "PotionReduceRegenMultiplier",
                0.2,
                "Percent to reduce stamina regeneration (0.2 = 20%)."
        ).getDouble(0.2);

        dssPotionRegenSpeedMultiplier = config.get(
                "Darwin Sprinting System",
                "PotionRegenSpeedMultiplier",
                0.2,
                "Percent to speed up stamina regeneration (0.2 = 20%)."
        ).getDouble(0.2);

        dssBarSize = config.getInt(
                "BarSize",
                "Darwin Sprinting System",
                0,
                0,
                2,
                "Size of the bar: 0 = tiny, 1 = small, 2 = big."
        );

        dssBarOffsetX = config.getInt(
                "BarOffsetX",
                "Darwin Sprinting System",
                22,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                "X offset of the bar from the middle of the screen."
        );

        dssBarOffsetY = config.getInt(
                "BarOffsetY",
                "Darwin Sprinting System",
                0,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                "Y offset of the bar from the middle of the screen."
        );

        dssBarTransparencyPercent = config.get(
                "Darwin Sprinting System",
                "BarTransparencyPercent",
                100.0,
                "Transparency in percent (10 = 10%)."
        ).getDouble(100.0);

        enableLevelUpModule = config.getBoolean(
                "EnableLevelUpModule",
                "LevelUp",
                true,
                "Master switch for integrated LevelUp! content (skills, classes, HUD, items, and event handlers)."
        );
        levelUpAllowHud = config.getBoolean(
                "AllowHud",
                "LevelUp",
                true,
                "If anything from the LevelUp HUD should be rendered on screen at all."
        );
        levelUpRenderHudTopLeft = config.getBoolean(
                "RenderHudOnTopLeft",
                "LevelUp",
                false,
                "Render the LevelUp Class HUD in the top-left corner."
        );
        levelUpRenderHudExpBar = config.getBoolean(
                "RenderHudOnExpBar",
                "LevelUp",
                true,
                "Render the LevelUp HUD above the vanilla experience bar."
        );
        levelUpChangeFovWithSpeed = config.getBoolean(
                "ChangeFovWithSpeed",
                "LevelUp",
                true,
                "If true, LevelUp athletics and sneak speed changes can affect FOV."
        );
        levelUpRegisterTalismanOfWonder = config.getBoolean(
                "RegisterTalismanOfWonder",
                "LevelUp",
                false,
                "If false, the Talisman of Wonder is not registered and none of its recipes are added."
        );
        levelUpEnableUnlearningBook = config.getBoolean(
                "EnableUnlearningBook",
                "LevelUp",
                true,
                "Enable the Book of Unlearning item and its recipe."
        );
        levelUpEnableLegacyRecipes = config.getBoolean(
                "EnableLegacyRecipes",
                "LevelUp",
                false,
                "Enable LevelUp's legacy pumpkin seed and flint-to-gravel recipes."
        );
        levelUpEnableItemRecipes = config.getBoolean(
                "EnableItemRecipes",
                "LevelUp",
                true,
                "If false, LevelUp item recipes are disabled (Talisman of Wonder and Book of Unlearning)."
        );
        levelUpUnlearningBookDungeonLootWeight = config.getInt(
                "UnlearningBookDungeonLootWeight",
                "LevelUp",
                1,
                0,
                1000,
                "Dungeon chest weight for the Book of Unlearning. Set to 0 to disable dungeon loot."
        );
        levelUpEarnSkillPointsBeforeClassChoice = config.getBoolean(
                "EarnSkillPointsBeforeClassChoice",
                "LevelUp",
                true,
                "If true, skill points from XP continue accumulating before choosing a class."
        );
        levelUpUnlearningBookResetClass = config.getBoolean(
                "UnlearningBookResetClass",
                "LevelUp",
                true,
                "If true, the Book of Unlearning also removes the player's class."
        );
        levelUpFarmingBlacklist = config.getStringList(
                "FarmingBlacklist",
                "LevelUp",
                new String[]{""},
                "Block registry names ignored by LevelUp farming growth effects."
        );
        levelUpMaxPointsPerSkill = config.getInt(
                "MaxPointsPerSkill",
                "LevelUp",
                50,
                1,
                Integer.MAX_VALUE,
                "Maximum points that can be invested into a single LevelUp skill."
        );
        levelUpBonusPointsForClasses = config.getInt(
                "BonusPointsForClasses",
                "LevelUp",
                20,
                0,
                Integer.MAX_VALUE,
                "Skill points granted automatically when choosing a class."
        );
        levelUpXpGainPerLevel = config.get(
                "LevelUp",
                "XpGainPerLevel",
                2.0,
                "Base LevelUp XP gain per level."
        ).getDouble(3.0);
        levelUpSkillPointsLostOnDeathPercent = config.getInt(
                "SkillPointsLostOnDeathPercent",
                "LevelUp",
                0,
                0,
                100,
                "Percent of unspent LevelUp skill points lost on death."
        );
        levelUpUseOldSpeedDirtAndGravelDigging = config.getBoolean(
                "UseOldSpeedDirtAndGravelDigging",
                "LevelUp",
                false,
                "Use the original LevelUp digging speed behavior for dirt and gravel."
        );
        levelUpUseOldSpeedRedstoneBreaking = config.getBoolean(
                "UseOldSpeedRedstoneBreaking",
                "LevelUp",
                false,
                "Use the original LevelUp redstone ore breaking speed behavior."
        );
        levelUpResetPlayerClassOnDeath = config.getBoolean(
                "ResetPlayerClassOnDeath",
                "LevelUp",
                false,
                "If true, the player's LevelUp class is removed on death."
        );
        levelUpPreventDuplicatedOresPlacing = config.getBoolean(
                "PreventDuplicatedOresPlacing",
                "LevelUp",
                true,
                "Prevent duplicated ores created by LevelUp skills from being placed for infinite duplication."
        );
        levelUpAddBonusXpOnCraft = config.getBoolean(
                "AddBonusXpOnCraft",
                "LevelUp",
                true,
                "Enable class-based bonus XP on crafting."
        );
        levelUpAddBonusXpOnMining = config.getBoolean(
                "AddBonusXpOnMining",
                "LevelUp",
                true,
                "Enable class-based bonus XP on mining."
        );
        levelUpAddXpOnCraftingSomeItems = config.getBoolean(
                "AddXpOnCraftingSomeItems",
                "LevelUp",
                true,
                "Enable general LevelUp XP gains for crafting certain items."
        );
        levelUpAddXpOnMiningSomeOre = config.getBoolean(
                "AddXpOnMiningSomeOre",
                "LevelUp",
                true,
                "Enable general LevelUp XP gains for mining coal, lapis, redstone, iron, gold, emerald, diamond, and nether quartz ore, plus modded ore blocks registered in the Ore Dictionary under names starting with ore."
        );
        levelUpAddBonusXpOnFighting = config.getBoolean(
                "AddBonusXpOnFighting",
                "LevelUp",
                true,
                "Enable class-based bonus XP on fighting."
        );

        levelUpHudPulseSpeedHz = config.getFloat(
                "HudTextPulseSpeedHz",
                "LevelUp",
                0.33F,
                0.0F,
                5.0F,
                "Pulse speed in cycles per second for LevelUp class selection prompt. Set to 0 for a constant full-bright color."
        );

        levelUpEnableMeleeSneakAttackBonusDamage = config.getBoolean(
                "EnableMeleeSneakAttackBonusDamage",
                "LevelUp",
                false,
                "If true, melee sneak attacks from behind apply the configured multiplier."
        );
        levelUpEnableRangedSneakAttackBonusDamage = config.getBoolean(
                "EnableRangedSneakAttackBonusDamage",
                "LevelUp",
                false,
                "If true, ranged sneak attacks from behind apply the configured multiplier."
        );
        levelUpMeleeSneakAttackDamageMultiplier = config.getFloat(
                "MeleeSneakAttackDamageMultiplier",
                "LevelUp",
                1.5F,
                0.0F,
                20.0F,
                "Damage multiplier for melee sneak attacks from behind."
        );
        levelUpRangedSneakAttackDamageMultiplier = config.getFloat(
                "RangedSneakAttackDamageMultiplier",
                "LevelUp",
                1.5F,
                0.0F,
                20.0F,
                "Damage multiplier for ranged sneak attacks from behind."
        );

        blessingsEnabled = config.getBoolean(
                "EnableBlessings",
                "Blessings",
                true,
                "If false, disables the Blessings system."
        );

        blessingsGrantOnFirstJoin = config.getBoolean(
                "BlessingsGrantOnFirstJoin",
                "Blessings",
                true,
                "If true, players receive a random blessing when they first join a world."
        );

        blessingsAnnounceFirstJoinBlessing = config.getBoolean(
                "BlessingsAnnounceFirstJoinBlessing",
                "Blessings",
                true,
                "If true, players are told their random blessing in chat when BlessingsGrantOnFirstJoin grants one."
        );

        blessingsAllowInfernoOnFirstJoin = config.getBoolean(
                "BlessingsAllowInfernoOnFirstJoin",
                "Blessings",
                true,
                "If true, Inferno may be chosen as a starting blessing."
        );

        blessingsPillarGenEnabled = config.getBoolean(
                "BlessingsPillarGenEnabled",
                "Blessings",
                true,
                "If true, blessing pillars generate in the overworld."
        );

        blessingsPillarGenChance = config.getInt(
                "BlessingsPillarGenChance",
                "Blessings",
                33,
                1,
                Integer.MAX_VALUE,
                "1 in N chance per chunk to attempt placing blessing pillars."
        );

        blessingsPillarMaxPerChunk = config.getInt(
                "BlessingsPillarMaxPerChunk",
                "Blessings",
                1,
                1,
                64,
                "Maximum blessing pillars that can generate in a chunk when generation triggers."
        );

        artifactActiveLightLevel = config.getInt(
                "ArtifactActiveLightLevel",
                "Blessings",
                8,
                0,
                15,
                "Light level emitted by an activated artifact (0-15)."
        );

        artifactExclusiveActivation = config.getBoolean(
                "ArtifactExclusiveActivation",
                "Blessings",
                false,
                "If true, only one player can have a given artifact as their blessing source at a time."
        );

        blessingNinjaInvisCooldownSeconds = config.getInt(
                "BlessingNinjaInvisCooldownSeconds",
                "Blessings",
                12,
                0,
                3600,
                "Cooldown in seconds before Ninja invisibility can be applied again."
        );

        blessingVampireHealPercent = config.getFloat(
                "BlessingVampireHealPercent",
                "Blessings",
                7.0F,
                0.0F,
                1000.0F,
                "Percent of damage dealt that Vampire heals back."
        );

        loseBlessingOnArtifactBreak = config.getBoolean(
                "LoseBlessingOnArtifactBreak",
                "Blessings",
                true,
                "If true, breaking the pillar that granted your blessing removes that blessing."
        );

        artifactActivationAroundMonsters = config.getBoolean(
                "ArtifactActivationAroundMonsters",
                "Blessings",
                true,
                "If true, blessing pillars can be activated even when monsters are nearby."
        );

        loseBlessingOnDeath = config.getBoolean(
                "LoseBlessingOnDeath",
                "Blessings",
                false,
                "If true, dying clears your current blessing."
        );

        blessingsDisabledList = config.getStringList(
                "BlessingsDisabled",
                "Blessings",
                new String[]{},
                "List of blessings to disable by name (case-insensitive). Disabled blessings won't be granted or selectable."
        );
        parseDisabledBlessings(blessingsDisabledList);

        blessingThiefDropEntries = config.getStringList(
                "BlessingThiefExtraDrops",
                "Blessings",
                new String[]{
                        "minecraft:gold_ingot|0.05",
                        "minecraft:iron_ingot|0.05"
                },
                "Items that may drop additionally when you have the Thief blessing.\n" +
                        "Format: modid:item[@meta]|chance (chance can be 0-1 or percent)."
        );

        blessingAlchemistPotionIds = parsePotionIdList(config.getStringList(
                "BlessingAlchemistPotionIds",
                "Blessings",
                new String[]{"1", "3", "5", "8", "10", "11", "12", "13", "14", "16", "21", "22"},
                "Potion IDs eligible for the Alchemist blessing bonus effect."
        ));

        blessingDrunkNegativePotionIds = parsePotionIdList(config.getStringList(
                "BlessingDrunkNegativePotionIds",
                "Blessings",
                new String[]{"2", "4", "9", "15", "17", "18", "19", "20"},
                "Potion IDs eligible for the Drunk blessing negative effect."
        ));

        initialMoonPhase = MoonPhaseHelper.canonicalName(config.getString(
                "InitialMoonPhase",
                "celestial",
                "waning_crescent",
                "Moon phase used on the first night of the world's lunar cycle. This offsets the cycle without changing world time.\n"
                        + "Accepted names: full_moon, waning_gibbous, last_quarter, waning_crescent, new_moon, waxing_crescent, first_quarter, waxing_gibbous. Numeric values 0-7 are also accepted.\n"
                        + "Changing this shifts the moon phase in existing worlds too."
        ), MoonPhaseHelper.WANING_CRESCENT);
        initialMoonPhaseIndex = MoonPhaseHelper.parsePhase(initialMoonPhase, MoonPhaseHelper.WANING_CRESCENT);
        config.getCategory("celestial").get("InitialMoonPhase").set(initialMoonPhase);

        hostileMobSpawnDisabledMoonPhases = MoonPhaseHelper.sanitizePhaseList(config.getStringList(
                "HostileMobSpawnDisabledMoonPhases",
                MOB_SPAWNING_CATEGORY,
                new String[0],
                "Moon phases where natural hostile mob spawning is disabled for vanilla, modded, and RiftFlux-configured mobs.\n"
                        + "Accepted names: full_moon, waning_gibbous, last_quarter, waning_crescent, new_moon, waxing_crescent, first_quarter, waxing_gibbous. Numeric values 0-7 are also accepted.\n"
                        + "Spawn eggs, mob spawners, structures, commands, and scripted boss/minion spawns are not affected."
        ));
        hostileMobSpawnDisabledMoonPhaseFlags =
                MoonPhaseHelper.parsePhaseFlags(hostileMobSpawnDisabledMoonPhases);
        config.getCategory(MOB_SPAWNING_CATEGORY)
                .get("HostileMobSpawnDisabledMoonPhases")
                .set(hostileMobSpawnDisabledMoonPhases);

        hostileMobSpawnGracePeriodDays = config.getInt(
                "HostileMobSpawnGracePeriodDays",
                MOB_SPAWNING_CATEGORY,
                0,
                0,
                1000000,
                "Number of complete Minecraft days after world creation during which natural hostile mob spawning is disabled.\n"
                        + "0 allows hostile spawning immediately. Uses the current Minecraft day, so custom day speeds, sleeping, and time changes affect the grace period.\n"
                        + "Applies to vanilla, modded, and RiftFlux-configured mobs. Spawn eggs, mob spawners, structures, commands, and scripted boss/minion spawns are not affected."
        );

        mobSpawnWhitelist = config.getStringList(
                "Whitelist",
                MOB_SPAWNING_CATEGORY,
                DEFAULT_MOB_SPAWN_WHITELIST,
                "Natural mob spawn whitelist. RiftFlux mobs only spawn naturally when listed here; vanilla/other mod mobs listed here have their natural biome spawn entries overwritten per mob.\n"
                        + "Format: mobname|spawnweight|min-max group size|dimension ids|biome types|biome names or ids\n"
                        + "Spawn weight is relative to other entries in the same biome and creature type: higher = more common, lower = rarer. A whitelist entry with weight 0 is ignored.\n"
                        + "A biome name/id may override the entry weight with biome(weight), for example Jungle(60), Desert(40), Taiga(500). Higher per-biome weight = more common in that biome.\n"
                        + "Leave dimension ids empty for every dimension. Leave biome types and biome names/ids empty for every biome in the matched dimensions.\n"
                        + "Separate dimensions, biome types, or biome names/ids with commas. Biome types are Forge BiomeDictionary tags such as forest, magic, dry, river, swamp. The aliases ice and icy map to the snowy tag.\n"
                        + "Biome names/ids accept exact biome display names, numeric ids, id:123, name:Biome Name, or the special token wheatfield. Names are resolved to numeric biome ids once at startup; spawn checks do not perform string matching.\n"
                        + "Use the Forge 1.7.10 entity id for RiftFlux mobs, for example riftflux.BlackWidow or riftflux.quackling, to avoid matching another mod's entity with the same simple name. riftflux: still resolves as an alias.\n"
                        + "Structure and manual mob spawning are not configured here."
        );
        mobSpawnWhitelist = sanitizeMobSpawnRules(mobSpawnWhitelist);
        config.getCategory(MOB_SPAWNING_CATEGORY).get("Whitelist").set(mobSpawnWhitelist);

        mobSpawnBlacklist = config.getStringList(
                "Blacklist",
                MOB_SPAWNING_CATEGORY,
                DEFAULT_MOB_SPAWN_BLACKLIST,
                "Natural mob spawn blacklist using the same fields as Whitelist. Spawn weight and group size are ignored here.\n"
                        + "Blacklist entries take precedence over whitelist entries, so a mob can be whitelisted for a dimension/type and then blocked from one biome name/id."
        );
        mobSpawnBlacklist = sanitizeMobSpawnRules(mobSpawnBlacklist);
        config.getCategory(MOB_SPAWNING_CATEGORY).get("Blacklist").set(mobSpawnBlacklist);

        mobSpawnWhitelistOnlyBiomes = config.getStringList(
                "WhitelistOnlyBiomes",
                MOB_SPAWNING_CATEGORY,
                DEFAULT_MOB_SPAWN_WHITELIST_ONLY_BIOMES,
                "Biome names or ids where natural spawning is locked to mobs that have a matching Whitelist entry for that biome.\n"
                        + "Any existing vanilla/mod spawn-list entries in these biomes are removed unless that mob is whitelisted here. CheckSpawn also denies unlisted mobs in these biomes.\n"
                        + "Accepts exact biome display names, numeric ids, id:123, name:Biome Name, or wheatfield. Names are resolved once at startup."
        );
        mobSpawnWhitelistOnlyBiomes = sanitizeBiomeNameIdList(mobSpawnWhitelistOnlyBiomes);
        config.getCategory(MOB_SPAWNING_CATEGORY).get("WhitelistOnlyBiomes").set(mobSpawnWhitelistOnlyBiomes);

        useSpawnTypeForMobCap = config.getBoolean(
                "UseSpawnTypeForMobCap",
                MOB_SPAWNING_CATEGORY,
                true,
                "If true, RiftFlux determines each modded mob's creature type from the biome spawn registry instead of class inheritance when mob caps are checked. "
                        + "This fixes mobs such as aquatic creatures counting toward the wrong cap. "
                        + "RiftFlux prewarms the lookup once at load-complete from the final spawn registry, then uses a cached class lookup during play. "
                        + "Requires restart."
        );

        enableAvatarModule = config.getBoolean(
                "EnableAvatarModule",
                "avatar",
                true,
                "Master switch for the Avatar module (Appa and gliders). Requires restart."
        );

        gliderDyeRecipes = config.getBoolean(
                "GliderDyeRecipes",
                "avatar",
                true,
                "If true, gliders can be recolored with dyes in a crafting grid."
        );
        gliderStackable = config.getBoolean(
                "GliderStackable",
                "avatar",
                false,
                "If true, Avatar gliders can stack. If false, each glider is unstackable."
        );

        gliderUseItemInHand = config.getBoolean(
                "GliderUseItemInHand",
                "avatar",
                false,
                "If true, use the normal item model in-hand instead of the 3D staff model when holding a glider."
        );
        enableGliderHoldAltitude = config.getBoolean(
                "enableGliderHoldAltitude",
                "avatar",
                true,
                "If true, holding the jump key while gliding prevents downward motion and consumes stamina at sprint rate. Works best with DSS enabled."
        );

        blockEtFuturumElytraWhileAvatarGliding = config.getBoolean(
                "BlockEtFuturumElytraWhileAvatarGliding",
                "avatar",
                true,
                "If true, Et Futurum Requiem Elytra cannot be activated while Avatar glider mode is active."
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
                        "EntityBison",
                        "EntityNimatin",
                        "EntitySootSprite"
                },
                "Entity IDs/class names used to filter which mobs can ride Appa.\n" +
                        "Matches entity ID, class simple name, or full class name.\n" +
                        "Default blocks Appa from mounting itself, Nimatins from riding Appa, and Soot Sprites from riding Appa."
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

        enableTerraModule = config.getBoolean(
                "EnableTerraModule",
                "terraria",
                true,
                "Master switch for the Terraria module (Demon Eye, Eye of Cthulhu, Destroyer, Suspicious Looking Eye, lenses, and Ice Rod)."
        );

        demonEyeHealth = config.getFloat(
                "DemonEyeHealth",
                "terraria",
                14.0F,
                1.0F,
                2048.0F,
                "Max health for Demon Eye."
        );

        eyeOfCthulhuHealth = config.getFloat(
                "EyeOfCthulhuHealth",
                "terraria",
                400.0F,
                1.0F,
                2048.0F,
                "Max health for Eye of Cthulhu."
        );

        eyeOfCthulhuExperience = config.getInt(
                "EyeOfCthulhuExperience",
                "terraria",
                3600,
                0,
                1000000,
                "Total experience dropped by Eye of Cthulhu during its death sequence."
        );

        lensDropChance = config.getFloat(
                "LensDropChance",
                "terraria",
                0.23F,
                0.0F,
                1.0F,
                "Chance (0.0-1.0) for a Demon Eye to drop a Lens."
        );

        blackLensDropChance = config.getFloat(
                "BlackLensDropChance",
                "terraria",
                0.01F,
                0.0F,
                1.0F,
                "Chance (0.0-1.0) for a Demon Eye to drop a Black Lens."
        );

        eyeOfCthulhuDrops = config.getStringList(
                "EyeOfCthulhuDrops",
                "terraria",
                new String[]{
                        "riftflux:glider_red*1|1.0",
                        "riftflux:whoopie_cushion*1|1.0",
                        "riftflux:ice_rod*1|1.0",
                        "riftflux:respecBook@1*1|1.0"
                },
                "Drops for Eye of Cthulhu.\n" +
                        "Format: modid:item[@meta][*count]|chance (chance can be 0-1 or percent)."
        );

        eyeOfCthulhuMusicEnabled = config.getBoolean(
                "EyeOfCthulhuMusicEnabled",
                "terraria",
                true,
                "If true, plays riftflux:eyeofcthulu while an Eye of Cthulhu is within render distance."
        );

        eyeOfCthulhuDemonEyeWaveCount = config.getInt(
                "EyeOfCthulhuDemonEyeWaveCount",
                "terraria",
                3,
                0,
                128,
                "How many Demon Eyes Eye of Cthulhu summons per wave. Set to 0 to disable wave summoning."
        );

        eyeOfCthulhuDemonEyeWaveIntervalSeconds = config.getInt(
                "EyeOfCthulhuDemonEyeWaveIntervalSeconds",
                "terraria",
                12,
                1,
                3600,
                "Seconds between Eye of Cthulhu Demon Eye summon waves."
        );

        eyeOfCthulhuDemonEyeCap = config.getInt(
                "EyeOfCthulhuDemonEyeCap",
                "terraria",
                18,
                0,
                512,
                "Maximum number of Eye of Cthulhu-summoned Demon Eyes alive at once. Set to 0 to disable wave summoning."
        );

        eyeOfCthulhuDespawnNoPlayerDelaySeconds = config.getInt(
                "EyeOfCthulhuDespawnNoPlayerDelaySeconds",
                "terraria",
                6,
                0,
                120,
                "Delay in seconds before Eye of Cthulhu despawns after no alive player remains in range. Set to 0 for instant despawn."
        );

        eyeOfCthulhuDespawnNoPlayerChunkRadius = config.getInt(
                "EyeOfCthulhuDespawnNoPlayerChunkRadius",
                "terraria",
                8,
                1,
                64,
                "Eye of Cthulhu despawns if no alive player is within this many chunks. 10 chunks = 160 blocks."
        );

        eyeOfCthulhuDespawnNoPlayerRadius = config.getInt(
                "EyeOfCthulhuDespawnNoPlayerRadius",
                "terraria",
                160,
                16,
                512,
                "Deprecated fallback block radius for Eye of Cthulhu despawn distance when chunk radius is not set."
        );

        destroyerEnabled = config.getBoolean(
                "DestroyerEnabled",
                "terraria",
                true,
                "If false, disables spawning and keeping The Destroyer alive while leaving the rest of the Terraria module enabled."
        );

        destroyerMusicEnabled = config.getBoolean(
                "DestroyerMusicEnabled",
                "terraria",
                true,
                "If true, plays riftflux:destroyer while The Destroyer boss bar is active."
        );

        destroyerHealth = config.getFloat(
                "DestroyerHealth",
                "terraria",
                2000.0F,
                1.0F,
                100000.0F,
                "Max health for The Destroyer. Body segment hits are forwarded to this shared health pool."
        );

        destroyerExperience = config.getInt(
                "DestroyerExperience",
                "terraria",
                6000,
                0,
                100000,
                "Total XP dropped by The Destroyer on death."
        );

        destroyerSegmentCount = config.getInt(
                "DestroyerSegmentCount",
                "terraria",
                119,
                1,
                160,
                "Number of body segments spawned behind The Destroyer head."
        );

        destroyerSegmentDistance = config.getFloat(
                "DestroyerSegmentDistance",
                "terraria",
                1.9F,
                0.5F,
                8.0F,
                "Target spacing in blocks between Destroyer segments."
        );

        destroyerDiveDepth = config.getFloat(
                "DestroyerDiveDepth",
                "terraria",
                10.0F,
                1.0F,
                80.0F,
                "Y level The Destroyer dives toward during its burrowing cycle. The original Destroyer uses Y=10."
        );

        destroyerDiveCycleTicks = config.getInt(
                "DestroyerDiveCycleTicks",
                "terraria",
                160,
                40,
                1200,
                "Length of The Destroyer dive/surface attack cycle in ticks."
        );

        destroyerArmoredHealthThreshold = config.getFloat(
                "DestroyerArmoredHealthThreshold",
                "terraria",
                400.0F,
                0.0F,
                100000.0F,
                "The Destroyer uses its gold armored texture and reduced projectile damage at or below this health."
        );

        destroyerHeadContactDamage = config.getFloat(
                "DestroyerHeadContactDamage",
                "terraria",
                100.0F,
                0.0F,
                10000.0F,
                "Contact damage dealt by The Destroyer head."
        );

        destroyerHeadContactHealthPercent = config.getFloat(
                "DestroyerHeadContactHealthPercent",
                "terraria",
                0.10F,
                0.0F,
                100.0F,
                "Extra head contact damage as a fraction of the target current health. Original value is 0.10 for 10%."
        );

        destroyerBodyContactDamage = config.getFloat(
                "DestroyerBodyContactDamage",
                "terraria",
                16.0F,
                0.0F,
                10000.0F,
                "Contact damage dealt by each Destroyer body segment."
        );

        destroyerBodyContactHealthPercent = config.getFloat(
                "DestroyerBodyContactHealthPercent",
                "terraria",
                0.04F,
                0.0F,
                100.0F,
                "Extra body segment contact damage as a fraction of the target current health. Original value is 0.04 for 4%."
        );

        destroyerProbeContactDamage = config.getFloat(
                "DestroyerProbeContactDamage",
                "terraria",
                12.0F,
                0.0F,
                10000.0F,
                "Contact damage dealt by Destroyer Probes."
        );

        destroyerProbeContactHealthPercent = config.getFloat(
                "DestroyerProbeContactHealthPercent",
                "terraria",
                0.03333334F,
                0.0F,
                100.0F,
                "Extra probe contact damage as a fraction of the target current health. Original value is 0.03333334 for 1/30."
        );

        destroyerHeadLaserDamage = config.getFloat(
                "DestroyerHeadLaserDamage",
                "terraria",
                6.0F,
                0.0F,
                10000.0F,
                "Damage dealt by The Destroyer head laser."
        );

        destroyerHeadLaserMaxHealthPercent = config.getFloat(
                "DestroyerHeadLaserMaxHealthPercent",
                "terraria",
                0.0F,
                0.0F,
                100.0F,
                "Extra head laser damage as a fraction of target max health. Original Destroyer laser damage is flat, so default is 0."
        );

        destroyerBodyLaserDamage = config.getFloat(
                "DestroyerBodyLaserDamage",
                "terraria",
                4.5F,
                0.0F,
                10000.0F,
                "Damage dealt by body segment lasers."
        );

        destroyerProbeLaserDamage = config.getFloat(
                "DestroyerProbeLaserDamage",
                "terraria",
                4.0F,
                0.0F,
                10000.0F,
                "Damage dealt by Destroyer Probe lasers."
        );

        destroyerHeadLaserCooldownTicks = config.getInt(
                "DestroyerHeadLaserCooldownTicks",
                "terraria",
                30,
                1,
                1200,
                "Ticks between head laser shots."
        );

        destroyerBodyLaserCooldownTicks = config.getInt(
                "DestroyerBodyLaserCooldownTicks",
                "terraria",
                30,
                1,
                1200,
                "Ticks between laser shots for body segments that can fire."
        );

        destroyerProbeLaserCooldownTicks = config.getInt(
                "DestroyerProbeLaserCooldownTicks",
                "terraria",
                40,
                1,
                1200,
                "Ticks between Destroyer Probe laser shots."
        );

        destroyerBodyLaserEverySegments = config.getInt(
                "DestroyerBodyLaserEverySegments",
                "terraria",
                1,
                1,
                160,
                "Only every Nth body segment can fire body lasers. The original Destroyer allows every segment to fire."
        );

        destroyerLaserLifetimeTicks = config.getInt(
                "DestroyerLaserLifetimeTicks",
                "terraria",
                100,
                20,
                600,
                "Maximum lifetime for Destroyer laser projectiles in ticks."
        );

        destroyerProbesEnabled = config.getBoolean(
                "DestroyerProbesEnabled",
                "terraria",
                true,
                "If true, The Destroyer can spawn Probe adds during the fight."
        );

        destroyerProbeHealth = config.getFloat(
                "DestroyerProbeHealth",
                "terraria",
                32.0F,
                1.0F,
                10000.0F,
                "Max health for Destroyer Probes."
        );

        destroyerProbeChance = config.getInt(
                "DestroyerProbeChance",
                "terraria",
                110,
                1,
                100000,
                "One-in-N chance for the head to spawn a Probe after firing a head laser."
        );

        destroyerProbeCooldownTicks = config.getInt(
                "DestroyerProbeCooldownTicks",
                "terraria",
                0,
                0,
                12000,
                "Minimum ticks between Probe spawns. Original behavior has no separate cooldown beyond the spawn chance."
        );

        destroyerProbeLifetimeTicks = config.getInt(
                "DestroyerProbeLifetimeTicks",
                "terraria",
                1200,
                20,
                24000,
                "Maximum lifetime for Destroyer Probes in ticks."
        );

        destroyerDespawnNoPlayerDelaySeconds = config.getInt(
                "DestroyerDespawnNoPlayerDelaySeconds",
                "terraria",
                6,
                0,
                120,
                "Delay in seconds before The Destroyer despawns after no alive player remains in range. Set to 0 for instant despawn."
        );

        destroyerDespawnNoPlayerChunkRadius = config.getInt(
                "DestroyerDespawnNoPlayerChunkRadius",
                "terraria",
                10,
                1,
                64,
                "The Destroyer despawns if no alive player is within this many chunks."
        );

        destroyerDrops = config.getStringList(
                "DestroyerDrops",
                "terraria",
                new String[]{
                        "minecraft:diamond*8|1.0",
                        "minecraft:redstone*32|1.0",
                        "minecraft:gold_ingot*16|0.75"
                },
                "Drops for The Destroyer.\n" +
                        "Format: modid:item[@meta][*count]|chance (chance can be 0-1 or percent)."
        );

        iceRodBlockLifetimeSeconds = config.getFloat(
                "IceRodBlockLifetimeSeconds",
                "terraria",
                16.0F,
                0.05F,
                300.0F,
                "How long Ice Rod summoned blocks last before disappearing."
        );

        iceRodDurability = config.getInt(
                "IceRodDurability",
                "terraria",
                1024,
                0,
                32767,
                "Max durability for Ice Rod. Set to 0 for infinite durability."
        );

        iceRodSpawnDistance = config.getFloat(
                "IceRodSpawnDistance",
                "terraria",
                3.0F,
                1.0F,
                16.0F,
                "Distance in blocks in front of the player where Ice Rod places Magic Ice when no block is targeted. Supports decimals (for example 2.5)."
        );

        iceRodPlacementPreviewEnabled = config.getBoolean(
                "IceRodPlacementPreviewEnabled",
                "terraria",
                true,
                "If true, holding the Ice Rod shows a preview box of the block position that will be placed."
        );

        iceRodUseLegendGearMana = config.getBoolean(
                "IceRodUseLegendGearMana",
                "terraria",
                true,
                "If true, Ice Rod uses LegendGear mana (shows mana HUD while held and consumes IceRodLegendGearManaCost mana units per cast)."
        );

        iceRodLegendGearManaCost = config.getFloat(
                "IceRodLegendGearManaCost",
                "terraria",
                0.25F,
                0.0F,
                40.0F,
                "LegendGear mana consumed per Ice Rod cast when IceRodUseLegendGearMana is enabled. 1.0 = half a mana star."
        );

        magicIceRequireSilkTouch = config.getBoolean(
                "MagicIceRequireSilkTouch",
                "terraria",
                true,
                "If true, temporary Magic Ice only drops permanent Magic Ice when broken with Silk Touch."
        );

        caneOfSomariaBlockLifetimeSeconds = config.getFloat(
                "CaneOfSomariaBlockLifetimeSeconds",
                "terraria",
                24.0F,
                0.05F,
                300.0F,
                "How long Cane of Somaria summoned blocks last before disappearing."
        );

        caneOfSomariaDurability = config.getInt(
                "CaneOfSomariaDurability",
                "terraria",
                1024,
                0,
                32767,
                "Max durability for Cane of Somaria. Set to 0 for infinite durability."
        );

        caneOfSomariaSpawnDistance = config.getFloat(
                "CaneOfSomariaSpawnDistance",
                "terraria",
                4.0F,
                1.0F,
                16.0F,
                "Distance in blocks in front of the player where Cane of Somaria places Somaria Blocks when no block is targeted. Supports decimals (for example 2.5)."
        );

        caneOfSomariaPlacementPreviewEnabled = config.getBoolean(
                "CaneOfSomariaPlacementPreviewEnabled",
                "terraria",
                true,
                "If true, holding the Cane of Somaria shows a preview box of the block position that will be placed."
        );

        caneOfSomariaUseLegendGearMana = config.getBoolean(
                "CaneOfSomariaUseLegendGearMana",
                "terraria",
                true,
                "If true, Cane of Somaria uses LegendGear mana (shows mana HUD while held and consumes CaneOfSomariaLegendGearManaCost mana units per cast)."
        );

        caneOfSomariaLegendGearManaCost = config.getFloat(
                "CaneOfSomariaLegendGearManaCost",
                "terraria",
                0.5F,
                0.0F,
                40.0F,
                "LegendGear mana consumed per Cane of Somaria cast when CaneOfSomariaUseLegendGearMana is enabled. 1.0 = half a mana star."
        );

        somariaBlockRequireSilkTouch = config.getBoolean(
                "SomariaBlockRequireSilkTouch",
                "terraria",
                true,
                "If true, temporary Somaria Blocks only drop Somaria Blocks when broken with Silk Touch."
        );

        somariaBlockRequirePickaxeToDrop = config.getBoolean(
                "SomariaBlockRequirePickaxeToDrop",
                "terraria",
                true,
                "If true, temporary Somaria Blocks require a pickaxe to drop as an item when mined."
        );

        whoopieCushionKnockbackEnabled = config.getBoolean(
                "WhoopieCushionKnockbackEnabled",
                "terraria",
                true,
                "If true, right-clicking Whoopie Cushion knocks nearby mobs upward and away."
        );

        whoopieCushionKnockbackRadius = config.getFloat(
                "WhoopieCushionKnockbackRadius",
                "terraria",
                2.5F,
                0.0F,
                16.0F,
                "Radius around the player to affect mobs when using Whoopie Cushion."
        );

        whoopieCushionKnockbackStrength = config.getFloat(
                "WhoopieCushionKnockbackStrength",
                "terraria",
                2.5F,
                0.0F,
                10.0F,
                "Knockback strength multiplier for Whoopie Cushion mob launch."
        );
        whoopieCushionLegendGearManaCost = config.getFloat(
                "WhoopieCushionLegendGearManaCost",
                "terraria",
                0.25F,
                0.0F,
                20.0F,
                "LegendGear mana consumed per Whoopie Cushion use when LegendGear is enabled. 2.0 = one full mana icon."
        );

        terraMushroomSpawnAttempts = config.getInt(
                "TerraMushroomSpawnAttempts",
                "terraria",
                33,
                0,
                10000,
                "1 in N chance per chunk to attempt Terra Mushroom generation (surface). 1 = always, 0 = disabled."
        );

        daybloomSpawnAttempts = config.getInt(
                "DaybloomSpawnAttempts",
                "terraria",
                33,
                0,
                10000,
                "1 in N chance per chunk to attempt Daybloom generation (surface). 1 = always, 0 = disabled."
        );

        blinkrootSpawnAttempts = config.getInt(
                "BlinkrootSpawnAttempts",
                "terraria",
                33,
                0,
                10000,
                "1 in N chance per chunk to attempt Blinkroot generation (underground). 1 = always, 0 = disabled."
        );

        waterleafSpawnAttempts = config.getInt(
                "WaterleafSpawnAttempts",
                "terraria",
                33,
                0,
                10000,
                "1 in N chance per chunk to attempt Waterleaf generation (underground). 1 = always, 0 = disabled."
        );

        deathweedSpawnAttempts = config.getInt(
                "DeathweedSpawnAttempts",
                "terraria",
                33,
                0,
                10000,
                "1 in N chance per chunk to attempt Deathweed generation (underground). 1 = always, 0 = disabled."
        );

        fireblossomSpawnAttempts = config.getInt(
                "FireblossomSpawnAttempts",
                "terraria",
                33,
                0,
                10000,
                "1 in N chance per chunk to attempt Fireblossom generation (underground). 1 = always, 0 = disabled."
        );

        jungleSporeSpawnAttempts = config.getInt(
                "JungleSporeSpawnAttempts",
                "terraria",
                33,
                0,
                10000,
                "1 in N chance per chunk to attempt Jungle Spore generation (underground jungle). 1 = always, 0 = disabled."
        );

        moonglowSpawnAttempts = config.getInt(
                "MoonglowSpawnAttempts",
                "terraria",
                33,
                0,
                10000,
                "1 in N chance per chunk to attempt Moonglow generation (underground jungle). 1 = always, 0 = disabled."
        );

        terraMushroomRequireShears = config.getBoolean(
                "TerraMushroomRequireShears",
                "terraria",
                false,
                "If true, Terra Mushroom only drops itself when harvested with shears."
        );

        daybloomRequireShears = config.getBoolean(
                "DaybloomRequireShears",
                "terraria",
                false,
                "If true, Daybloom only drops itself when harvested with shears."
        );

        blinkrootRequireShears = config.getBoolean(
                "BlinkrootRequireShears",
                "terraria",
                false,
                "If true, Blinkroot only drops itself when harvested with shears."
        );

        waterleafRequireShears = config.getBoolean(
                "WaterleafRequireShears",
                "terraria",
                false,
                "If true, Waterleaf only drops itself when harvested with shears."
        );

        deathweedRequireShears = config.getBoolean(
                "DeathweedRequireShears",
                "terraria",
                false,
                "If true, Deathweed only drops itself when harvested with shears."
        );

        fireblossomRequireShears = config.getBoolean(
                "FireblossomRequireShears",
                "terraria",
                false,
                "If true, Fireblossom only drops itself when harvested with shears."
        );

        jungleSporeRequireShears = config.getBoolean(
                "JungleSporeRequireShears",
                "terraria",
                false,
                "If true, Jungle Spore only drops itself when harvested with shears."
        );

        moonglowRequireShears = config.getBoolean(
                "MoonglowRequireShears",
                "terraria",
                false,
                "If true, Moonglow only drops itself when harvested with shears."
        );

        enableLegendGearModule = config.getBoolean(
                "EnableLegendGearModule",
                "legendgear",
                true,
                "Master switch for integrated LegendGear content (items, blocks, entities, rituals, mana, and worldgen)."
        );

        legendGearEnableLegacyLegendGear = config.getBoolean(
                "EnableLegacyLegendGear",
                "legendgear",
                true,
                "If true, restores the missing 1.5.2 LegendGear legacy items and blocks inside the integrated LegendGear module."
        );

        legendGearSoulTetherEnabled = config.getBoolean(
                "EnableSoulTether",
                "legendgear",
                true,
                "If false, disables both the legacy Soul Tether enchantment and the ritual-applied Soul Tether item effect."
        );

        legendGearLegacyFocusEnchantmentId = config.getInt(
                "legacyFocusEnchantmentId",
                "legendgear",
                246,
                0,
                255,
                "Enchantment ID for the legacy LegendGear Focus enchantment."
        );

        legendGearLegacySoulTetherEnchantmentId = config.getInt(
                "legacySoulTetherEnchantmentId",
                "legendgear",
                247,
                0,
                255,
                "Enchantment ID for the legacy LegendGear Soul Tether enchantment."
        );

        legendGearLegacyBombBagCapacity = clampInt(
                config.getInt(
                        "legacyBombBagCapacity",
                        "legendgear",
                        50,
                        0,
                        999,
                        "Maximum bomb capacity for the legacy Bomb Bag."
                ),
                0,
                999
        );

        legendGearLegacyBombMaxStackSize = clampInt(
                config.getInt(
                        "legacyBombMaxStackSize",
                        "legendgear",
                        16,
                        1,
                        64,
                        "Maximum inventory stack size for legacy Bomb items."
                ),
                1,
                64
        );

        legendGearLegacyBombDamage = config.getInt(
                "legacyBombDamage",
                "legendgear",
                5,
                0,
                1024,
                "Damage dealt by legacy bombs and bomb flowers."
        );

        legendGearLegacyBombFuseTimeTicks = clampInt(
                config.getInt(
                        "legacyBombFuseTimeTicks",
                        "legendgear",
                        120,
                        1,
                        12000,
                        "Fuse time in ticks for legacy bombs before they explode."
                ),
                1,
                12000
        );

        legendGearLegacyBombExplosionStrength = config.getFloat(
                "legacyBombExplosionStrength",
                "legendgear",
                4.0F,
                0.0F,
                128.0F,
                "Explosion strength used for legacy bomb blast radius, knockback, and bombable block reach."
        );

        legendGearLegacyBombsIgniteExplosiveBlocks = config.getBoolean(
                "legacyBombsIgniteExplosiveBlocks",
                "legendgear",
                true,
                "If true, legacy bomb blasts ignite TNT and compatible explosive blocks in their blast radius. If false, explosive blocks are not triggered by legacy bomb blasts."
        );

        legendGearLegacyBombFlowerPlaceAnywhere = config.getBoolean(
                "legacyBombFlowerPlaceAnywhere",
                "legendgear",
                true,
                "If true, legacy Bomb Flowers can be placed on nearly any non-air support block without requiring lava adjacency."
        );

        legendGearLegacyBombFlowerPlaceOnAnyBlockNearLava = config.getBoolean(
                "legacyBombFlowerPlaceOnAnyBlockNearLava",
                "legendgear",
                false,
                "If true, legacy Bomb Flowers can be placed on any non-air support block as long as they still have their lava adjacency."
        );

        legendGearLegacyBombFlowerPickupWithShears = config.getBoolean(
                "legacyBombFlowerPickupWithShears",
                "legendgear",
                true,
                "If true, legacy Bomb Flowers can be picked up intact with shears."
        );

        legendGearLegacyBombFlowerPickupWithSilkTouch = config.getBoolean(
                "legacyBombFlowerPickupWithSilkTouch",
                "legendgear",
                true,
                "If true, legacy Bomb Flowers can be picked up intact when broken with Silk Touch."
        );
        legendGearLegacyBombFlowerMobTrigger = config.getBoolean(
                "legacyBombFlowerMobTrigger",
                "legendgear",
                true,
                "If true, non-player living mobs that walk into a live legacy Bomb Flower will trigger its bomb."
        );

        legendGearLegacyClayJarItemCapacity = config.getInt(
                "legacyClayJarItemCapacity",
                "legendgear",
                512,
                1,
                32767,
                "Maximum number of one stackable item type that a legacy Clay Jar can store. Unstackable items are always limited to 1."
        );

        legendGearLegacyClayJarSneakPickupEnabled = config.getBoolean(
                "legacyClayJarSneakPickupEnabled",
                "legendgear",
                true,
                "If true, sneak-right-clicking an empty-handed legacy Clay Jar picks it up intact with all contents preserved."
        );

        legendGearLegacyClayJarUndergroundGenEnabled = config.getBoolean(
                "legacyClayJarUndergroundGenEnabled",
                "legendgear",
                true,
                "If true, legacy Clay Jars can generate underground on cave floors."
        );

        legendGearLegacyClayJarUndergroundSpawnChance = config.getInt(
                "legacyClayJarUndergroundSpawnChance",
                "legendgear",
                2,
                0,
                256,
                "Underground legacy Clay Jar spawn attempts per chunk when enabled."
        );

        legendGearLegacyClayJarUndergroundMinY = config.getInt(
                "legacyClayJarUndergroundMinY",
                "legendgear",
                16,
                1,
                255,
                "Minimum Y level for underground legacy Clay Jar generation."
        );

        legendGearLegacyClayJarUndergroundMaxY = config.getInt(
                "legacyClayJarUndergroundMaxY",
                "legendgear",
                64,
                1,
                255,
                "Maximum Y level for underground legacy Clay Jar generation."
        );

        legendGearLegacyClayJarNaturalLootEntries = config.getStringList(
                "legacyClayJarNaturalLootEntries",
                "legendgear",
                new String[]{
                        "minecraft:clay_ball*4-32",
                        "minecraft:sand*16-64",
                        "minecraft:sandstone*4-24"
                },
                "Potential contents for naturally generated legacy Clay Jars. Format: modid:item[*count or *min-max][@meta]. Example: minecraft:sandstone*1-256"
        );

        legendGearLegacyBombableBlocks = config.getStringList(
                "legacyBombableBlocks",
                "legendgear",
                new String[]{"minecraft:cobblestone", "minecraft:tnt"},
                "Block registry names that legacy bombs can break, for example minecraft:cobblestone. Specific metadata is supported with modid:block:meta or modid:block@meta. Ore dictionary entries are also supported with an ore: prefix, for example ore:stone."
        );

        legendGearLegacyMysticShrubDropEntries = config.getStringList(
                "legacyMysticShrubDropEntries",
                "legendgear",
                DEFAULT_LEGACY_MYSTIC_SHRUB_DROP_ENTRIES,
                "Independent normal legacy Mystic Shrub drop rolls.\n" +
                        "Syntax: item_or_alias[*count or *min-max][@meta]|chance\n" +
                        "Chance accepts 0.05 or 5 for 5%.\n" +
                        "Examples: riftflux:heartPickup*1|20  or  minecraft:arrow*1-3|0.5."
        );
        if (Arrays.equals(legendGearLegacyMysticShrubDropEntries, OLD_DEFAULT_LEGACY_MYSTIC_SHRUB_DROP_ENTRIES)) {
            legendGearLegacyMysticShrubDropEntries = DEFAULT_LEGACY_MYSTIC_SHRUB_DROP_ENTRIES.clone();
            config.getCategory("legendgear").get("legacyMysticShrubDropEntries").set(legendGearLegacyMysticShrubDropEntries);
        }

        legendGearLegacyMysticShrubChargedPrizeEntries = config.getStringList(
                "legacyMysticShrubChargedPrizeEntries",
                "legendgear",
                DEFAULT_LEGACY_MYSTIC_SHRUB_CHARGED_PRIZE_ENTRIES,
                "Weighted charged legacy Mystic Shrub prize table.\n" +
                        "Syntax: item_or_alias[*count or *min-max][@meta]|weight\n" +
                        "Exactly one valid entry is chosen when a charged shrub pays out.\n" +
                        "Examples: heartPickup*3|20  or  itemBomb*1-3|5."
        );

        legendGearLegacyMysticShrubGenStarChance = config.getFloat(
                "legacyMysticShrubGenStarChance",
                "legendgear",
                0.3F,
                0.0F,
                1.0F,
                "Chance for a legacy Mystic Shrub cluster to generate in a star shape."
        );

        legendGearLegacyMysticShrubJackpotChance = config.getFloat(
                "legacyMysticShrubJackpotChance",
                "legendgear",
                0.05F,
                0.0F,
                1.0F,
                "Chance for a charged legacy Mystic Shrub to trigger a jackpot prize."
        );

        legendGearLegacyMysticShrubRarity = config.getInt(
                "legacyMysticShrubRarity",
                "legendgear",
                48,
                1,
                1024,
                "Average chunk rarity for legacy Mystic Shrub generation. 1 means every chunk."
        );

        legendGearLegacyMysticShrubBiomeWhitelist = config.getStringList(
                "legacyMysticShrubBiomeWhitelist",
                "legendgear",
                new String[0],
                "Biome IDs, names, or Forge biome dictionary types where legacy Mystic Shrubs may generate. Leave empty to allow every Overworld biome unless blacklisted. Entries are resolved once at startup; generation checks numeric biome IDs only. Special token: wheatfield. Prefixes type: and biometype: are accepted but not required."
        );

        legendGearLegacyMysticShrubBiomeBlacklist = config.getStringList(
                "legacyMysticShrubBiomeBlacklist",
                "legendgear",
                DEFAULT_LEGACY_MYSTIC_SHRUB_BIOME_BLACKLIST,
                "Biome IDs, names, or Forge biome dictionary types where legacy Mystic Shrubs must not generate. This blacklist takes precedence over the whitelist. Entries are resolved once at startup; generation checks numeric biome IDs only. Special token: wheatfield. Prefixes type: and biometype: are accepted but not required."
        );
        if (Arrays.equals(legendGearLegacyMysticShrubBiomeBlacklist, OLD_DEFAULT_LEGACY_MYSTIC_SHRUB_BIOME_BLACKLIST)) {
            legendGearLegacyMysticShrubBiomeBlacklist = DEFAULT_LEGACY_MYSTIC_SHRUB_BIOME_BLACKLIST.clone();
            config.getCategory("legendgear").get("legacyMysticShrubBiomeBlacklist").set(legendGearLegacyMysticShrubBiomeBlacklist);
        }

        legendGearAzuriteBiomeWhitelist = config.getStringList(
                "azuriteBiomeWhitelist",
                "legendgear",
                new String[]{"Hot Springs"},
                "Biomes where Azurite Ore may generate. Default: Hot Springs. Leave empty to allow every Overworld biome unless blacklisted.\n" +
                        "Entries may be biome IDs, exact biome names, name:<name>, or biome dictionary types such as type:HILLS.\n" +
                        "The biome blacklist always takes precedence."
        );

        legendGearAzuriteBiomeBlacklist = config.getStringList(
                "azuriteBiomeBlacklist",
                "legendgear",
                new String[0],
                "Biomes where Azurite Ore must never generate. Uses the same entry format as azuriteBiomeWhitelist and always takes precedence."
        );

        legendGearLegacyMysticShrubDimensionWhitelist = ConfigResolver.parseIntegerList(config.getStringList(
                "legacyMysticShrubDimensionWhitelist",
                "legendgear",
                new String[]{"0", "7"},
                "Dimension IDs where legacy Mystic Shrubs may generate. Default: 0 and 7."
        ));

        legendGearLegacyBombFlowerDimensionWhitelist = ConfigResolver.parseIntegerList(config.getStringList(
                "legacyBombFlowerDimensionWhitelist",
                "legendgear",
                new String[]{"0", "-1"},
                "Dimension IDs where legacy Bomb Flowers may generate. Default: 0 and -1."
        ));

        legendGearLegacyQuiverMaxCapacity = clampInt(
                config.getInt(
                        "legacyQuiverMaxCapacity",
                        "legendgear",
                        200,
                        0,
                        999,
                        "Maximum arrow capacity for the legacy Quiver."
                ),
                0,
                999
        );

        legendGearLegacyAllowCandy = config.getBoolean(
                "legacyAllowCandy",
                "legendgear",
                true,
                "If true, legacy Rock Candy items and recipes are enabled."
        );

        legendGearLegacyBombsAllowed = config.getBoolean(
                "legacyBombsAllowed",
                "legendgear",
                true,
                "If true, legacy bombs, bomb bags, and bomb flowers remain active."
        );

        legendGearLegacyEmeraldShardsAllowed = config.getBoolean(
                "legacyEmeraldShardsAllowed",
                "legendgear",
                true,
                "If true, legacy emerald shard drops and Mystic Shrub shard rewards remain active."
        );

        legendGearLegacyHeartsAllowed = config.getBoolean(
                "legacyHeartsAllowed",
                "legendgear",
                true,
                "If true, legacy hearts can drop from monsters and Mystic Shrubs."
        );

        legendGearLegacyMagicMirrorAllowed = config.getBoolean(
                "legacyMagicMirrorAllowed",
                "legendgear",
                true,
                "If true, the legacy Magic Mirror recipe and behavior remain enabled."
        );

        legendGearLegacyMagicMirrorDurability = config.getInt(
                "legacyMagicMirrorDurability",
                "legendgear",
                64,
                0,
                32767,
                "Durability for the legacy Magic Mirror. Set to 0 for unlimited uses."
        );

        legendGearFortuneCookiePotionEffects = config.getStringList(
                "fortuneCookiePotionEffects",
                "legendgear",
                new String[0],
                "Potion effects Fortune Cookies can apply at random when eaten.\n" +
                        "Format per entry: potionNameOrId,level,durationSeconds.\n" +
                        "Examples: speed,2,30  or  regeneration,1,10.\n" +
                        "Leave empty to keep Fortune Cookies as chat-only."
        );

        legendGearSweetSnackPotionEffects = config.getStringList(
                "sweetSnackPotionEffects",
                "legendgear",
                new String[]{
                        "legendgearManaRegen,2,20",
                        "speed,3,20"
                },
                "Potion effects Star Candy and Poptarts can apply at random when eaten.\n" +
                        "Format per entry: potionNameOrId,level,durationSeconds.\n" +
                        "Examples: legendgearManaRegen,2,20  or  speed,3,20.\n" +
                        "Leave empty to disable random potion effects from those snacks."
        );

        legendGearLegacyMedallionsAllowed = config.getBoolean(
                "legacyMedallionsAllowed",
                "legendgear",
                true,
                "If true, legacy medallions, amulets, and related recipes remain enabled."
        );

        legendGearLegacyMysticShrubAllowed = config.getBoolean(
                "legacyMysticShrubAllowed",
                "legendgear",
                true,
                "If true, legacy Mystic Shrubs remain enabled and can generate in the world."
        );

        legendGearLegacyMysticShrubSuperPrizes = config.getBoolean(
                "legacyMysticShrubSuperPrizes",
                "legendgear",
                true,
                "If true, charged legacy Mystic Shrubs can drop their stronger storm prizes."
        );

        legendGearLegacyQuiverAllowed = config.getBoolean(
                "legacyQuiverAllowed",
                "legendgear",
                true,
                "If true, the legacy Quiver and its loading behavior remain enabled."
        );

        legendGearLegacyHookshotAnyBlock = config.getBoolean(
                "legacyHookshotAnyBlock",
                "legendgear",
                true,
                "If true, the legacy Hookshot can attach to any solid block instead of only wood plus the compatibility list."
        );

        legendGearLegacyHookshotBlocks = config.getStringList(
                "legacyHookshotBlocks",
                "legendgear",
                new String[0],
                "Extra blocks the legacy Hookshot can attach to when legacyHookshotAnyBlock=false. Use registry names such as minecraft:stone or modid:block_name. Wood blocks are always allowed."
        );

        legendGearLegacyHookshotDurability = config.getInt(
                "legacyHookshotDurability",
                "legendgear",
                256,
                0,
                65535,
                "Durability for the legacy Hookshot. Set to 0 for infinite durability."
        );

        legendGearLegacyAeroAmuletDurability = config.getInt(
                "legacyAeroAmuletDurability",
                "legendgear",
                500,
                0,
                65535,
                "Durability for the legacy Aero Amulet. Set to 0 for infinite durability."
        );

        legendGearLegacyGeoAmuletDurability = config.getInt(
                "legacyGeoAmuletDurability",
                "legendgear",
                500,
                0,
                65535,
                "Durability for the legacy Geo Amulet. Set to 0 for infinite durability."
        );

        legendGearLegacyGeoAmuletQuakeDamageMultiplier = config.getFloat(
                "legacyGeoAmuletQuakeDamageMultiplier",
                "legendgear",
                1.2F,
                0.0F,
                1024.0F,
                "Multiplier applied to the fall damage the legacy Geo Amulet prevented when calculating quake damage. Examples: 0.5 halves quake damage, 2.0 doubles it."
        );

        legendGearLegacyPyroAmuletDurability = config.getInt(
                "legacyPyroAmuletDurability",
                "legendgear",
                500,
                0,
                65535,
                "Durability for the legacy Pyro Amulet. Set to 0 for infinite durability."
        );

        legendGearLegacyAmuletsUseBaublesSlot = config.getBoolean(
                "legacyAmuletsUseBaublesSlot",
                "legendgear",
                true,
                "If true and Baubles is installed, legacy amulets work from the Baubles amulet slot."
        );

        legendGearLegacyMedallionEffectsAffectPlayer = config.getBoolean(
                "legacyMedallionEffectsAffectPlayer",
                "legendgear",
                true,
                "If true, legacy medallion effect entities can also affect the player who cast them."
        );

        legendGearLegacyWhirlwindBootsDashSound = config.getBoolean(
                "legacyWhirlwindBootsDashSound",
                "legendgear",
                false,
                "If true, Whirlwind Boots play their legacy dash sound while sprinting."
        );

        legendGearLegacyStarbeamRailLaunchStrength = config.getFloat(
                "legacyStarbeamRailLaunchStrength",
                "legendgear",
                1.2F,
                0.0F,
                10.0F,
                "Velocity applied when jumping off a legacy Starbeam Rail."
        );

        legendGearLegacyStarbeamRailConnectionRange = config.getInt(
                "legacyStarbeamRailConnectionRange",
                "legendgear",
                64,
                1,
                256,
                "Maximum block distance for legacy Starbeam Rails to connect to each other."
        );

        legendGearLegacyStarbeamRailNoSlowdown = config.getBoolean(
                "legacyStarbeamRailNoSlowdown",
                "legendgear",
                true,
                "If true, legacy Starbeam Rails do not reduce rider speed from drag or uphill travel."
        );

        legendGearLegacyStarbeamRailNoFallDamage = config.getBoolean(
                "legacyStarbeamRailNoFallDamage",
                "legendgear",
                true,
                "If true, jumping off a legacy Starbeam Rail prevents the fall damage from that launch."
        );

        legendGearLegacyStarbeamRailConnectAcrossTypes = config.getBoolean(
                "legacyStarbeamRailConnectAcrossTypes",
                "legendgear",
                false,
                "If true, legacy Starbeam Rails and placed Infused Star Pieces can connect directly to each other while grinding."
        );

        legendGearLegacyStarbeamRailRightClickAnyDirection = config.getBoolean(
                "legacyStarbeamRailRightClickAnyDirection",
                "legendgear",
                true,
                "If true, right-clicking a legacy Starbeam Rail or Infused Star Piece can start travel even when you are not facing one of its connected rail nodes."
        );

        legendGearLegacyTitanBandBlockBossPickup = config.getBoolean(
                "legacyTitanBandBlockBossPickup",
                "legendgear",
                false,
                "If true, the legacy Titan Band cannot pick up boss entities."
        );

        legendGearLegacyTitanBandPickupBlacklist = config.getStringList(
                "legacyTitanBandPickupBlacklist",
                "legendgear",
                new String[0],
                "Entities the legacy Titan Band cannot pick up. Accepts entity ids, display names, simple class names, or full class names. Examples: Zombie, minecraft:zombie, net.minecraft.entity.monster.EntityZombie."
        );

        legendGearLegacyTitanBandKeepPassengerOnCarrierHurt = config.getBoolean(
                "legacyTitanBandKeepPassengerOnCarrierHurt",
                "legendgear",
                true,
                "If true, taking damage will not force the legacy Titan Band to drop the mob you are carrying."
        );

        legendGearLegacyTitanBandDurability = config.getInt(
                "legacyTitanBandDurability",
                "legendgear",
                150,
                0,
                32767,
                "Durability for the legacy Titan Band. Set to 0 for unlimited durability."
        );

        legendGearLegacyHeadbandOfValorDamageBonusWithArmor = config.getBoolean(
                "legacyHeadbandOfValorDamageBonusWithArmor",
                "legendgear",
                true,
                "If true, the Headband of Valor keeps its damage bonus even while you are wearing other armor."
        );

        legendGearLegacyHeadbandOfValorHeadBaubleDamageBonus = config.getBoolean(
                "legacyHeadbandOfValorHeadBaubleDamageBonus",
                "legendgear",
                true,
                "If true, the Headband of Valor can be equipped in the Baubles head slot and still grants its damage bonus regardless of worn armor."
        );

        legendGearLegacyHeadbandOfValorDurability = config.getInt(
                "legacyHeadbandOfValorDurability",
                "legendgear",
                150,
                0,
                32767,
                "Durability for the Headband of Valor. Set to 0 for unlimited durability."
        );

        legendGearLegacyHeadbandOfValorBonusDamage = config.getFloat(
                "legacyHeadbandOfValorBonusDamage",
                "legendgear",
                3.0F,
                0.0F,
                1024.0F,
                "Flat extra damage granted by the Headband of Valor."
        );

        legendGearLegacyStarbeamRailRightClickTravel = config.getBoolean(
                "legacyStarbeamRailRightClickTravel",
                "legendgear",
                true,
                "If true, right-clicking a legacy Starbeam Rail starts rail travel without needing to jump onto it."
        );

        legendGearMagicProtectionId = config.getInt(
                "magicProtectionID",
                "legendgear",
                110,
                0,
                255,
                "Enchantment ID for Magic Protection."
        );

        legendGearSpellReachId = config.getInt(
                "spellReachID",
                "legendgear",
                111,
                0,
                255,
                "Enchantment ID for Reach."
        );

        legendGearSpellSpreadId = config.getInt(
                "spellSpreadID",
                "legendgear",
                112,
                0,
                255,
                "Enchantment ID for Spread."
        );

        legendGearSpellArmoredId = config.getInt(
                "spellArmoredID",
                "legendgear",
                113,
                0,
                255,
                "Enchantment ID for Fortitude."
        );

        legendGearAllowEmeraldDrops = config.getBoolean(
                "allowEmeraldDrops",
                "legendgear",
                false,
                "Allow emerald shard drops from mobs and grass."
        );

        legendGearAllowHeartDrops = config.getBoolean(
                "allowHeartDrops",
                "legendgear",
                true,
                "Allow heart drops from mobs and grass."
        );

        legendGearItemSoundVolume = config.getFloat(
                "itemSoundVolume",
                "legendgear",
                0.3F,
                0.0F,
                1.0F,
                "Volume of heart and emerald pickup sounds."
        );

        legendGearFancyExperience = config.getBoolean(
                "fancyExperience",
                "legendgear",
                true,
                "Render fancy rainbow experience orbs."
        );

        legendGearMaxStarwellRetries = config.getInt(
                "maxStarwellRetries",
                "legendgear",
                16,
                0,
                16,
                "Times to retry placing a starwell in a chunk if invalid."
        );

        legendGearFallingStarDamageEnabled = config.getBoolean(
                "fallingStarDamageEnabled",
                "legendgear",
                true,
                "If true, Falling Stars can deal damage on impact and while descending."
        );

        legendGearFallingStarDamage = config.getFloat(
                "fallingStarDamage",
                "legendgear",
                25.0F,
                0.0F,
                2048.0F,
                "Damage dealt by Falling Stars to entities hit mid-air and in the 3x3x3 impact area."
        );
        legendGearNightFallingStarFrequency = config.getFloat(
                "nightFallingStarFrequency",
                "legendgear",
                1.0F,
                0.0F,
                64.0F,
                "Multiplier for natural Falling Star frequency while under open sky at night. 0 disables them, 1 keeps current behavior, 2 makes them happen about twice as often."
        );
        legendGearAprilFoolsBananaFallingStars = config.getBoolean(
                "aprilFoolsBananaFallingStars",
                "legendgear",
                true,
                "If true, Falling Stars render as bananas on April 1."
        );

        legendGearMagicBoomerangInfiniteDurability = config.getBoolean(
                "magicBoomerangInfiniteDurability",
                "legendgear",
                true,
                "If true, the Magic Boomerang does not lose durability."
        );

        legendGearMagicBoomerangDamage = config.getFloat(
                "magicBoomerangDamage",
                "legendgear",
                12.0F,
                0.0F,
                1024.0F,
                "Damage dealt by a direct Magic Boomerang hit."
        );

        legendGearMagicBoomerangBreakPlants = config.getBoolean(
                "magicBoomerangBreakPlants",
                "legendgear",
                false,
                "If true, a thrown Magic Boomerang breaks plant/vine blocks it hits."
        );

        legendGearMagicBoomerangActivateLegacyPlants = config.getBoolean(
                "magicBoomerangActivateLegacyPlants",
                "legendgear",
                true,
                "If true, a thrown Magic Boomerang activates LegendGear Bomb Flowers and Mystic Shrubs even when magicBoomerangBreakPlants is false."
        );

        legendGearMagicBoomerangPickupBombFlowerBombs = config.getBoolean(
                "magicBoomerangPickupBombFlowerBombs",
                "legendgear",
                true,
                "If true, a thrown Magic Boomerang defuses grounded unowned LegendGear bombs, such as bombs spawned by Bomb Flowers, and carries the bomb item back."
        );

        legendGearEnableBadBow = config.getBoolean(
                "enableBadBow",
                "legendgear",
                true,
                "If false, disables registration and recipes for riftflux:badBow."
        );

        legendGearDashRingMaxAirJumps = config.getInt(
                "dashRingMaxAirJumps",
                "legendgear",
                2,
                0,
                8,
                "Maximum extra mid-air jumps granted by the dash ring behavior."
        );

        legendGearDashRingUseOriginalBehavior = config.getBoolean(
                "dashRingUseOriginalBehavior",
                "legendgear",
                false,
                "If true, restores the original dash ring behavior (unlimited mid-air dashes) and ignores dashRingMaxAirJumps."
        );

        legendGearDashRingAirJumpsRequireSprinting = config.getBoolean(
                "dashRingAirJumpsRequireSprinting",
                "legendgear",
                false,
                "If true, dash ring mid-air jumps only trigger while the player is sprinting."
        );

        legendGearDashRingAirJumpManaCost = config.getFloat(
                "dashRingAirJumpManaCost",
                "legendgear",
                4.0F,
                0.0F,
                1024.0F,
                "Mana fatigue cost for each Dash Ring mid-air jump. 4.0 equals 2 full mana stars on the HUD."
        );

        legendGearDescentRingManaCost = config.getFloat(
                "descentRingManaCost",
                "legendgear",
                0.75F,
                0.0F,
                1024.0F,
                "Mana cost per excess fall block prevented by the Descent Ring."
        );

        legendGearSprinkleStardustRequireSneak = config.getBoolean(
                "sprinkleStardustRequireSneak",
                "legendgear",
                true,
                "If true, Infused Stardust only sprinkles when crouch right-clicking."
        );

        legendGearSpottingScopeConsumesMana = config.getBoolean(
                "spottingScopeConsumesMana",
                "legendgear",
                false,
                "If false, the spotting scope ping does not consume mana and can be used at zero mana."
        );
        legendGearLoginSparkleEffectEnabled = config.getBoolean(
                "loginSparkleEffectEnabled",
                "legendgear",
                true,
                "If true, LegendGear sparkle burst plays when the local player logs in."
        );
        legendGearDimensionChangeSparkleEffectEnabled = config.getBoolean(
                "dimensionChangeSparkleEffectEnabled",
                "legendgear",
                true,
                "If true, LegendGear sparkle burst plays when the local player changes dimension."
        );
        legendGearTwinkleStaffDurability = config.getInt(
                "twinkleStaffDurability",
                "legendgear",
                128,
                0,
                32767,
                "Durability for the Twinkle Staff. Set to 0 for unlimited durability."
        );
        legendGearFireStaffDurability = config.getInt(
                "fireStaffDurability",
                "legendgear",
                128,
                0,
                32767,
                "Durability for the Ember Staff. Set to 0 for unlimited durability."
        );
        legendGearZapStaffDurability = config.getInt(
                "zapStaffDurability",
                "legendgear",
                128,
                0,
                32767,
                "Durability for the Zap Staff. Set to 0 for unlimited durability."
        );
        legendGearIceStaffDurability = config.getInt(
                "iceStaffDurability",
                "legendgear",
                128,
                0,
                32767,
                "Durability for the Ice Staff. Set to 0 for unlimited durability."
        );
        legendGearTwinkleStaffManaCost = config.getFloat(
                "twinkleStaffManaCost",
                "legendgear",
                4.0F,
                0.0F,
                1024.0F,
                "Mana cost per Twinkle Staff cast."
        );
        legendGearFireStaffManaCost = config.getFloat(
                "fireStaffManaCost",
                "legendgear",
                5.0F,
                0.0F,
                1024.0F,
                "Mana cost per Ember Staff cast."
        );
        legendGearZapStaffManaCost = config.getFloat(
                "zapStaffManaCost",
                "legendgear",
                5.0F,
                0.0F,
                1024.0F,
                "Mana cost per Zap Staff cast."
        );
        legendGearIceStaffManaCost = config.getFloat(
                "iceStaffManaCost",
                "legendgear",
                5.0F,
                0.0F,
                1024.0F,
                "Mana cost per Ice Staff cast."
        );
        legendGearStarPieceInfuseLevels = config.getInt(
                "starPieceInfuseLevels",
                "legendgear",
                3,
                0,
                1000,
                "XP levels required to infuse a Star Piece into an Infused Star Piece."
        );
        legendGearPlaceableStarPieces = config.getBoolean(
                "placeableStarPieces",
                "legendgear",
                true,
                "If true, Star Piece and Infused Star Piece can be placed as decorative star blocks."
        );
        legendGearInfusedStarPiecesActAsStarbeamRails = config.getBoolean(
                "infusedStarPiecesActAsStarbeamRails",
                "legendgear",
                true,
                "If true, placed Infused Star Pieces act as Starbeam Rails and connect to legacy Starbeam Rail paths."
        );
        legendGearStarInJarLightLevel = config.getInt(
                "starInJarLightLevel",
                "legendgear",
                8,
                0,
                15,
                "Light level of the Star in a Jar block while active."
        );
        legendGearPlacedStarPiecesLightLevel = config.getInt(
                "placedStarPiecesLightLevel",
                "legendgear",
                8,
                0,
                15,
                "Light level for placed Star Piece and Infused Star Piece blocks."
        );
        legendGearFulguriteRequiresSilkTouch = config.getBoolean(
                "fulguriteRequiresSilkTouch",
                "legendgear",
                true,
                "If true, harvesting lightning-struck sand with a shovel only yields Fulgurite when Silk Touch is applied."
        );

        legendGearEmberStaffFireSeconds = config.getFloat(
                "emberStaffFireSeconds",
                "legendgear",
                3.0F,
                0.0F,
                600.0F,
                "How long Ember Staff fire spells ignite entities for (in seconds, fractional allowed). Set to 0 to disable burning."
        );
        legendGearManaRegenPotionId = config.getInt(
                "manaRegenPotionId",
                "legendgear",
                24,
                0,
                255,
                "Potion ID reserved for the LegendGear mana regeneration effect. If occupied, the next free ID is used."
        );
        legendGearGroundedPotionId = config.getInt(
                "groundedPotionId",
                "legendgear",
                25,
                0,
                255,
                "Potion ID reserved for the LegendGear Grounded jump-penalty effect. If occupied, the next free ID is used."
        );
        legendGearManaRegenPotionManaPerSecond = config.getFloat(
                "manaRegenPotionManaPerSecond",
                "legendgear",
                0.5F,
                0.0F,
                100.0F,
                "Mana restored per second by the LegendGear mana regeneration potion effect at amplifier 0."
        );
        legendGearStoneskinResistancePotionId = getLegendGearPotionId(
                "stoneskinResistancePotionId",
                VANILLA_POTION_ID_RESISTANCE,
                "Potion ID used for the Stoneskin ritual."
        );
        legendGearCaltropsBreakOnTrigger = config.getBoolean(
                "caltropsBreakOnTrigger",
                "legendgear",
                false,
                "If true, caltrops break when triggered. If false, they stay placed and can trigger again after a short cooldown."
        );
        legendGearCaltropsTriggerDropEnabled = config.getBoolean(
                "caltropsTriggerDropEnabled",
                "legendgear",
                false,
                "If true, caltrops have their normal random chance to drop as an item when triggered and broken."
        );
        legendGearCaltropsMobDamageHearts = config.getFloat(
                "caltropsMobDamageHearts",
                "legendgear",
                1.5F,
                0.0F,
                1024.0F,
                "Damage dealt by caltrops to non-player living entities, measured in hearts."
        );
        legendGearCaltropsPlayerDamagePercent = config.getFloat(
                "caltropsPlayerDamagePercent",
                "legendgear",
                20.0F,
                0.0F,
                1000.0F,
                "Percent of a player's max health dealt by caltrops."
        );
        legendGearCaltropsIronBarsMiningSpeed = config.getBoolean(
                "caltropsIronBarsMiningSpeed",
                "legendgear",
                true,
                "If true, caltrops take about as long to mine as iron bars. If false, they use their original instant-break speed."
        );
        legendGearCaltropsRequirePickaxeToDrop = config.getBoolean(
                "caltropsRequirePickaxeToDrop",
                "legendgear",
                true,
                "If true, placed caltrops require a pickaxe to drop as an item when mined."
        );
        legendGearCaltropsUndergroundGenEnabled = config.getBoolean(
                "caltropsUndergroundGenEnabled",
                "legendgear",
                true,
                "If true, caltrops may generate underground in caves."
        );
        legendGearCaltropsUndergroundSpawnChance = config.getInt(
                "caltropsUndergroundSpawnChance",
                "legendgear",
                6,
                0,
                256,
                "Underground caltrops spawn attempts per chunk when enabled. Higher values try more random cave-floor placements and can place multiple caltrops in one chunk."
        );
        legendGearCaltropsUndergroundMinY = config.getInt(
                "caltropsUndergroundMinY",
                "legendgear",
                12,
                1,
                255,
                "Minimum Y level for underground caltrops generation."
        );
        legendGearCaltropsUndergroundMaxY = config.getInt(
                "caltropsUndergroundMaxY",
                "legendgear",
                48,
                1,
                255,
                "Maximum Y level for underground caltrops generation."
        );
        legendGearCaltropsSlownessPotionId = getLegendGearPotionId(
                "caltropsSlownessPotionId",
                VANILLA_POTION_ID_SLOWNESS,
                "Potion ID used by caltrops for movement slowdown."
        );
        legendGearExitConfusionPotionId = getLegendGearPotionId(
                "exitConfusionPotionId",
                VANILLA_POTION_ID_CONFUSION,
                "Potion ID used for the Exit spell side effect."
        );
        legendGearIceSpellSlownessPotionId = getLegendGearPotionId(
                "iceSpellSlownessPotionId",
                VANILLA_POTION_ID_SLOWNESS,
                "Potion ID used by critical Ice spell hits for slowdown."
        );
        legendGearPhoenixReviveResistancePotionId = getLegendGearPotionId(
                "phoenixReviveResistancePotionId",
                VANILLA_POTION_ID_RESISTANCE,
                "Potion ID used by phoenix revival effects for resistance."
        );
        legendGearPhoenixReviveRegenerationPotionId = getLegendGearPotionId(
                "phoenixReviveRegenerationPotionId",
                VANILLA_POTION_ID_REGENERATION,
                "Potion ID used by phoenix revival effects for regeneration."
        );
        legendGearPhoenixReviveFireResistancePotionId = getLegendGearPotionId(
                "phoenixReviveFireResistancePotionId",
                VANILLA_POTION_ID_FIRE_RESISTANCE,
                "Potion ID used by phoenix revival effects for fire resistance."
        );
        legendGearPhoenixFeatherReviveEnabled = config.getBoolean(
                "phoenixFeatherReviveEnabled",
                "legendgear",
                true,
                "If true, Phoenix Feathers revive the player on death. If false, they behave as crafting materials again."
        );
        legendGearThiefRingInvisibilityPotionId = getLegendGearPotionId(
                "thiefRingInvisibilityPotionId",
                VANILLA_POTION_ID_INVISIBILITY,
                "Potion ID used while the Thief Ring is active."
        );
        legendGearPhoenixEmblemFireResistancePotionId = getLegendGearPotionId(
                "phoenixEmblemFireResistancePotionId",
                VANILLA_POTION_ID_FIRE_RESISTANCE,
                "Potion ID used by Phoenix Emblem interventions for fire resistance."
        );

        enableAsgardShieldModule = config.getBoolean(
                "EnableAsgardShieldModule",
                "asgardshield",
                true,
                "Master switch for integrated Asgard Shield content (core + addon shields/swords, guard logic, and HUD)."
        );

        asgardShieldEquipmentDurabilityMultiplier = config.getInt(
                "EquipmentDurabilityMultiplier",
                "asgardshield",
                1,
                1,
                64,
                "Multiplier applied to Asgard shield and giant sword durability."
        );

        asgardShieldHarkenVitalityAugmentId = config.getInt(
                "HarkenScytheVitalityAugmentID",
                "asgardshield",
                206,
                0,
                255,
                "Enchantment ID used for Harken Scythe Vitality augment compatibility."
        );

        asgardShieldHarkenExudeAugmentId = config.getInt(
                "HarkenScytheExudeAugmentID",
                "asgardshield",
                207,
                0,
                255,
                "Enchantment ID used for Harken Scythe Exude augment compatibility."
        );

        asgardShieldHarkenWardAugmentId = config.getInt(
                "HarkenScytheWardAugmentID",
                "asgardshield",
                210,
                0,
                255,
                "Enchantment ID used for Harken Scythe Ward augment compatibility."
        );

        asgardShieldHarkenSanguinaryAugmentId = config.getInt(
                "HarkenScytheSanguinaryAugmentID",
                "asgardshield",
                211,
                0,
                255,
                "Enchantment ID used for Harken Scythe Sanguinary augment compatibility."
        );

        asgardShieldEnableVanguard = config.getBoolean(
                "EnableVanguard",
                "asgardshield",
                true,
                "If true, the Vanguard charge/activation mechanic is enabled."
        );

        asgardShieldHudVanguardYOffset = config.getInt(
                "HudVanguardYOffset",
                "asgardshield",
                0,
                -32,
                32,
                "Vertical offset for the Vanguard HUD icon. Value is in 16px steps."
        );

        asgardShieldHudGuardGaugeYOffset = config.getInt(
                "HudGuardGaugeYOffset",
                "asgardshield",
                0,
                -32,
                32,
                "Vertical offset for the Guard Gauge HUD bar. Value is in 9px steps."
        );

        asgardShieldColorWood = config.getInt(
                "ColorWood",
                "asgardshield",
                10454093,
                0,
                0xFFFFFF,
                "Default tint color for Wooden shields."
        );

        asgardShieldColorStone = config.getInt(
                "ColorStone",
                "asgardshield",
                0xCDCDCD,
                0,
                0xFFFFFF,
                "Default tint color for Stone shields."
        );

        asgardShieldColorIron = config.getInt(
                "ColorIron",
                "asgardshield",
                0xC6C6C6,
                0,
                0xFFFFFF,
                "Default tint color for Iron shields."
        );

        asgardShieldColorDiamond = config.getInt(
                "ColorDiamond",
                "asgardshield",
                3402699,
                0,
                0xFFFFFF,
                "Default tint color for Diamond shields."
        );

        asgardShieldColorNether = config.getInt(
                "ColorNether",
                "asgardshield",
                15065046,
                0,
                0xFFFFFF,
                "Default tint color for Nether Quartz shields."
        );

        asgardShieldColorEnder = config.getInt(
                "ColorEnder",
                "asgardshield",
                6314865,
                0,
                0xFFFFFF,
                "Default tint color for Ender shields."
        );

        asgardShieldColorSkull = config.getInt(
                "ColorSkull",
                "asgardshield",
                16184280,
                0,
                0xFFFFFF,
                "Default tint color for Skull shields."
        );

        asgardShieldColorPatchwork = config.getInt(
                "ColorPatchwork",
                "asgardshield",
                44975,
                0,
                0xFFFFFF,
                "Default tint color for Patchwork shields."
        );

        asgardShieldColorLivingmetal = config.getInt(
                "ColorLivingmetal",
                "asgardshield",
                12905471,
                0,
                0xFFFFFF,
                "Default tint color for Livingmetal shields."
        );

        asgardShieldColorBiomass = config.getInt(
                "ColorBiomass",
                "asgardshield",
                13136249,
                0,
                0xFFFFFF,
                "Default tint color for Biomass shields."
        );

        enableSoulHeartsModule = config.getBoolean(
                "EnableSoulHeartsModule",
                "soulhearts",
                true,
                "Master switch for integrated Soul Hearts content."
        );

        soulHeartsDamageMultiplier = config.getFloat(
                "SoulDamageMultiplier",
                "soulhearts",
                2.0F,
                1.0F,
                5.0F,
                "Legacy soul-damage multiplier for non-per-hit behavior."
        );

        soulHeartsConsumeOneHeartPerHit = config.getBoolean(
                "SoulHeartsConsumeOneHeartPerHit",
                "soulhearts",
                true,
                "If true, any incoming damage consumes exactly 1 soul heart (2 points) to absorb damage."
        );

        enableHeartCrystalModule = config.getBoolean(
                "EnableHeartCrystalModule",
                "heartcrystal",
                true,
                "Master switch for integrated Heart Crystal content."
        );

        heartCrystalHeartsPerCrystal = config.getInt(
                "HeartsPerCrystal",
                "heartcrystal",
                2,
                1,
                20,
                "How many hearts (2 HP each) one Heart Crystal grants."
        );

        heartCrystalMiningLevel = config.getInt(
                "MiningLevel",
                "heartcrystal",
                2,
                0,
                10,
                "Required pickaxe harvest level for Heart Crystals. 2 = iron pickaxe."
        );

        heartCrystalMaxHearts = config.getInt(
                "MaxHearts",
                "heartcrystal",
                10,
                0,
                Integer.MAX_VALUE,
                "How many extra hearts a player can gain from heart crystals."
        );

        heartCrystalGenHeight = config.getInt(
                "GenHeight",
                "heartcrystal",
                20,
                1,
                256,
                "Generation height ceiling for heart crystals."
        );

        heartCrystalGenCount = config.getInt(
                "GenCount",
                "heartcrystal",
                1,
                0,
                1000,
                "Generation attempts per chunk for heart crystals."
        );

        heartCrystalOldModel = config.getBoolean(
                "OldModel",
                "heartcrystal",
                false,
                "If true, use the old 3D heart crystal model."
        );

        heartCrystalHeartPetDropChance = config.getFloat(
                "HeartPetDropChance",
                "heartcrystal",
                0.01F,
                0.0F,
                1.0F,
                "Chance for a naturally generated Heart Crystal to also drop a Heart pet when broken.\n" +
                        "1.0 = 100%, 0.01 = 1%."
        );

        heartLanternAuraEnabled = config.getBoolean(
                "HeartLanternAuraEnabled",
                "heartcrystal",
                true,
                "If true, heart lanterns apply configurable potion effects to players inside the configured radius."
        );

        heartLanternAuraRadius = config.getFloat(
                "HeartLanternAuraRadius",
                "heartcrystal",
                12.0F,
                0.0F,
                64.0F,
                "Radius around a heart lantern that receives the configured aura effects."
        );

        heartLanternAuraEffects = config.getStringList(
                "HeartLanternAuraEffects",
                "heartcrystal",
                new String[]{"regeneration,2,4"},
                "Potion effects applied by heart lanterns.\n" +
                        "Format per entry: potionNameOrId,level,durationSeconds\n" +
                        "Examples: regeneration,2,4  or  moveSpeed,1,10"
        );

        starLanternAuraEnabled = config.getBoolean(
                "StarLanternAuraEnabled",
                "heartcrystal",
                true,
                "If true, star lanterns apply configurable potion effects to players inside the configured radius."
        );

        starLanternAuraRadius = config.getFloat(
                "StarLanternAuraRadius",
                "heartcrystal",
                12.0F,
                0.0F,
                64.0F,
                "Radius around a star lantern that receives the configured aura effects."
        );

        starLanternAuraEffects = config.getStringList(
                "StarLanternAuraEffects",
                "heartcrystal",
                new String[]{"legendgearManaRegen,1,4"},
                "Potion effects applied by star lanterns.\n" +
                        "Format per entry: potionNameOrId,level,durationSeconds\n" +
                        "Examples: legendgearManaRegen,1,4  or  moveSpeed,1,10"
        );

        enableWheatfieldBiome = config.getBoolean(
                "EnableWheatfieldBiome",
                "wheatfield",
                true,
                "Master switch for Wheatfield biome and its barley block."
        );

        wheatfieldBiomeId = config.getInt(
                "BiomeId",
                "wheatfield",
                192,
                0,
                255,
                "Preferred biome ID for Wheatfield. RiftFlux will fall back to a free ID if this slot is occupied."
        );

        wheatfieldBiomeWeight = config.getInt(
                "BiomeWeight",
                "wheatfield",
                8,
                0,
                1000,
                "Generation weight for Wheatfield in the warm biome list. Set to 0 to stop natural generation without disabling the biome entirely."
        );

        wheatfieldAllowVillage = config.getBoolean(
                "AllowVillages",
                "wheatfield",
                false,
                "If true, villages may generate in the Wheatfield biome."
        );

        wheatfieldTreeChunkChance = config.getInt(
                "TreeChunkChance",
                "wheatfield",
                7,
                1,
                1000,
                "Chance for Wheatfield tree generation per chunk, expressed as 1 in N chunks. Higher numbers make trees rarer. Default 7 is about 30% rarer than the old hardcoded 1 in 5 chance."
        );

        wheatfieldPumpkinChunkChance = config.getInt(
                "PumpkinChunkChance",
                "wheatfield",
                64,
                1,
                1000,
                "Chance for Wheatfield pumpkin generation per chunk, expressed as 1 in N chunks. Higher numbers make pumpkins rarer. Default 64 is half as common as the old hardcoded 1 in 32 chance."
        );

        wheatfieldBarleyFistDropChancePercent = config.getInt(
                "BarleyFistDropChancePercent",
                "wheatfield",
                20,
                0,
                100,
                "Percent chance for barley to drop when broken, unless harvested with shears or silk touch."
        );

        wheatfieldBarleyOnlyDropsWhenSheared = config.getBoolean(
                "BarleyOnlyDropsWhenSheared",
                "wheatfield",
                false,
                "If true, barley only drops when harvested with shears."
        );

        enableHotSpringsModule = config.getBoolean(
                "EnableHotSpringsModule",
                "hotsprings",
                true,
                "Master switch for the Biomes O' Plenty 1.6.4 Hot Springs biome, spring water, and bucket. Requires restart."
        );

        hotSpringsBiomeId = config.getInt(
                "BiomeId",
                "hotsprings",
                193,
                0,
                255,
                "Preferred biome ID for Hot Springs. RiftFlux will fall back to a free ID if this slot is occupied."
        );

        hotSpringsBiomeWeight = config.getInt(
                "BiomeWeight",
                "hotsprings",
                4,
                0,
                1000,
                "Generation weight for Hot Springs in the cool biome list. Set to 0 to stop natural generation without disabling the biome."
        );

        hotSpringsAllowVillages = config.getBoolean(
                "AllowVillages",
                "hotsprings",
                false,
                "If true, villages may generate in the Hot Springs biome."
        );

        hotSpringsPotionEffects = config.getStringList(
                "PotionEffects",
                "hotsprings",
                new String[]{"regeneration,3,3"},
                "Potion effects applied to living entities in spring water.\n" +
                        "Format per entry: potionNameOrId,level,durationSeconds\n" +
                        "Example: regeneration,3,3. Leave empty to disable spring-water effects."
        );

        hotSpringsSpringLakesPerChunk = config.getInt(
                "SpringLakesPerChunk",
                "hotsprings",
                32,
                0,
                32,
                "Visible surface spring-water lake attempts per Hot Springs chunk. Higher values create a much wetter biome."
        );

        hotSpringsLavaLakesPerChunk = config.getInt(
                "LavaLakesPerChunk",
                "hotsprings",
                8,
                0,
                32,
                "Underground lava-lake attempts per Hot Springs chunk."
        );

        hotSpringsLakeRarity = config.getInt(
                "LakeRarity",
                "hotsprings",
                512,
                1,
                1000000,
                "Independent hotsprings water lake generation attempt per this many eligible chunks. 1 means every eligible chunk; 512 is quite rare."
        );

        hotSpringsLakeBiomeWhitelist = config.getStringList(
                "LakeBiomeWhitelist",
                "hotsprings",
                new String[0],
                "Biomes where independent hotsprings water lakes may generate. Leave empty to allow every biome in an allowed dimension.\n" +
                        "Entries may be biome IDs, exact biome names, name:<name>, or biome dictionary types such as type:FOREST.\n" +
                        "The biome blacklist always takes precedence."
        );

        hotSpringsLakeBiomeBlacklist = config.getStringList(
                "LakeBiomeBlacklist",
                "hotsprings",
                new String[0],
                "Biomes where independent hotsprings water lake must never generate. Uses the same entry format as LakeBiomeWhitelist and takes precedence over it."
        );

        hotSpringsLakeDimensionWhitelist = ConfigResolver.parseIntegerList(config.getStringList(
                "LakeDimensionWhitelist",
                "hotsprings",
                new String[]{"0"},
                "Dimension IDs where independent spring-water lakes may generate. Leave empty to allow every dimension. Default: 0 (Overworld).\n" +
                        "The dimension blacklist always takes precedence."
        ));

        hotSpringsLakeDimensionBlacklist = ConfigResolver.parseIntegerList(config.getStringList(
                "LakeDimensionBlacklist",
                "hotsprings",
                new String[0],
                "Dimension IDs where independent spring-water lakes must never generate. Takes precedence over LakeDimensionWhitelist."
        ));

        configurableWaterLakeYLevels = config.getBoolean(
                "ConfigurableWaterLakeYLevels",
                "worldgen",
                false,
                "If true, vanilla overworld water lake attempts use the configured Y range instead of vanilla Y selection. Requires restart."
        );

        waterLakeMinY = config.getInt(
                "WaterLakeMinY",
                "worldgen",
                5,
                5,
                255,
                "Minimum Y level passed to vanilla overworld water lake generation when ConfigurableWaterLakeYLevels is enabled. Values below 5 cannot generate vanilla lakes. Requires restart."
        );

        waterLakeMaxY = config.getInt(
                "WaterLakeMaxY",
                "worldgen",
                255,
                5,
                255,
                "Maximum Y level passed to vanilla overworld water lake generation when ConfigurableWaterLakeYLevels is enabled. If this is below WaterLakeMinY, the mixin swaps them at runtime. Requires restart."
        );

        enableWitchesAndMoreModule = config.getBoolean(
                "EnableWitchesAndMoreModule",
                "witchesandmore",
                true,
                "Master switch for integrated Witches and More content, including mobs, spawn eggs, events, and structures."
        );

        enableCyclopsMob = config.getBoolean(
                "EnableCyclops",
                "witchesandmore",
                true,
                "If true, Cyclops are registered and may spawn from eggs, natural spawning, and structures."
        );

        cyclopsMaxHealth = config.getInt(
                "CyclopsMaxHealth",
                "witchesandmore",
                60,
                1,
                2048,
                "Base max health for Cyclops. The eyeless state still scales down from this value."
        );

        enableFlowerManMob = config.getBoolean(
                "EnableFlowerMan",
                "witchesandmore",
                true,
                "If true, Flower Men are registered and may spawn from eggs, natural spawning, and structures."
        );

        flowerManMaxHealth = config.getInt(
                "FlowerManMaxHealth",
                "witchesandmore",
                4,
                1,
                2048,
                "Base max health for Flower Men."
        );

        enableEnderTrollMob = config.getBoolean(
                "EnableEnderTroll",
                "witchesandmore",
                true,
                "If true, Ender Trolls are registered and may spawn from eggs, natural spawning, and structures."
        );

        enderTrollMaxHealth = config.getInt(
                "EnderTrollMaxHealth",
                "witchesandmore",
                80,
                1,
                2048,
                "Base max health for Ender Trolls."
        );

        enableJaxxMob = config.getBoolean(
                "EnableJaxx",
                "witchesandmore",
                true,
                "If true, JAXX are registered and may spawn from eggs, natural spawning, and structures."
        );

        jaxxMaxHealth = config.getInt(
                "JaxxMaxHealth",
                "witchesandmore",
                140,
                1,
                2048,
                "Base max health for JAXX."
        );

        enableBlackWidowMob = config.getBoolean(
                "EnableBlackWidow",
                "witchesandmore",
                true,
                "If true, Black Widows are registered and may spawn from eggs, natural spawning, and structures."
        );

        blackWidowMaxHealth = config.getInt(
                "BlackWidowMaxHealth",
                "witchesandmore",
                24,
                1,
                2048,
                "Base max health for Black Widows."
        );
        wamCyclopsDropEntries = config.getStringList(
                "CyclopsDropEntries",
                "witchesandmore",
                DEFAULT_WAM_CYCLOPS_DROPS,
                "Independent Cyclops drop rolls. Syntax: item_or_alias*min-max|chance. Chance accepts 0.05 or 5 for 5%."
        );
        wamFlowerManDropEntries = config.getStringList(
                "FlowerManDropEntries",
                "witchesandmore",
                DEFAULT_WAM_FLOWER_MAN_DROPS,
                "Independent Flower Man drop rolls. Syntax: item_or_alias*min-max|chance. Chance accepts 0.05 or 5 for 5%.\n" +
                        "Use flowerman_flower for the Flower Man's color-specific flower."
        );
        wamEnderTrollDropEntries = config.getStringList(
                "EnderTrollDropEntries",
                "witchesandmore",
                DEFAULT_WAM_ENDER_TROLL_DROPS,
                "Independent Ender Troll drop rolls. Syntax: item_or_alias*min-max|chance. Chance accepts 0.05 or 5 for 5%."
        );
        wamJaxxDropEntries = config.getStringList(
                "JaxxDropEntries",
                "witchesandmore",
                DEFAULT_WAM_JAXX_DROPS,
                "Independent JAXX drop rolls. Syntax: item_or_alias*min-max|chance. Chance accepts 0.05 or 5 for 5%."
        );
        wamBlackWidowDropEntries = config.getStringList(
                "BlackWidowDropEntries",
                "witchesandmore",
                DEFAULT_WAM_BLACK_WIDOW_DROPS,
                "Independent Black Widow drop rolls. Syntax: item_or_alias*min-max|chance. Chance accepts 0.05 or 5 for 5%."
        );

        enableOffLawnModule = config.getBoolean(
                "EnableOffLawnModule",
                "offlawn",
                true,
                "Master switch for integrated OffLawn content."
        );

        offLawnEnableSunflowerWorldgen = config.getBoolean(
                "EnableSunflowerWorldgen",
                "offlawn",
                true,
                "If true, sunflower bushes naturally generate in forest/plains style biomes."
        );

        offLawnSunflowerPatchChance = config.getInt(
                "SunflowerPatchChance",
                "offlawn",
                1,
                1,
                1000,
                "Base one-in-N chance for a large OffLawn sunflower patch cell to spawn a patch. Higher values make patches rarer."
        );

        offLawnSunflowerAttemptsPerChunk = config.getInt(
                "SunflowerAttemptsPerChunk",
                "offlawn",
                1,
                0,
                128,
                "Target sunflower placements per full OffLawn sunflower patch. The generator clamps this to a sane patch size to avoid carpet coverage."
        );

        offLawnSunflowerPatchRadiusBlocks = config.getInt(
                "SunflowerPatchRadiusBlocks",
                "offlawn",
                12,
                4,
                128,
                "Approximate radius in blocks for a full OffLawn sunflower spiral patch. Larger values spread patches across multiple chunks."
        );

        offLawnSunflowerBiomeTypes = config.getStringList(
                "SunflowerBiomeTypes",
                "offlawn",
                new String[] { "PLAINS", "FOREST" },
                "Biome dictionary types that OffLawn sunflower patches may spawn in.\n"
                        + "Examples: PLAINS, FOREST. Leave empty to only use SunflowerBiomeList."
        );

        offLawnSunflowerBiomeList = config.getStringList(
                "SunflowerBiomeList",
                "offlawn",
                new String[] { "Wheatfield" },
                "Specific biomes where OffLawn sunflower patches may spawn.\n"
                        + "Accepted entries: biome id (4), biome name (Wheatfield), name:Wheatfield, or biome dictionary tag (type:PLAINS)."
        );

        offLawnEnableBrightSunflowerWorldgen = config.getBoolean(
                "EnableBrightSunflowerWorldgen",
                "offlawn",
                true,
                "If true, Bright Sunflower patches naturally generate using their own OffLawn worldgen settings."
        );

        offLawnBrightSunflowerPatchChance = config.getInt(
                "BrightSunflowerPatchChance",
                "offlawn",
                1,
                1,
                1000,
                "Base one-in-N chance for a large Bright Sunflower patch cell to spawn a patch. Higher values make patches rarer."
        );

        offLawnBrightSunflowerAttemptsPerChunk = config.getInt(
                "BrightSunflowerAttemptsPerChunk",
                "offlawn",
                1,
                0,
                128,
                "Target Bright Sunflower placements per full patch. The generator clamps this to a sane patch size to avoid carpet coverage."
        );

        offLawnBrightSunflowerPatchRadiusBlocks = config.getInt(
                "BrightSunflowerPatchRadiusBlocks",
                "offlawn",
                12,
                4,
                128,
                "Approximate radius in blocks for a full Bright Sunflower spiral patch. Larger values spread patches across multiple chunks."
        );

        offLawnBrightSunflowerBiomeTypes = config.getStringList(
                "BrightSunflowerBiomeTypes",
                "offlawn",
                new String[] { "PLAINS", "FOREST" },
                "Biome dictionary types that Bright Sunflower patches may spawn in.\n"
                        + "Examples: PLAINS, FOREST. Leave empty to only use BrightSunflowerBiomeList."
        );

        offLawnBrightSunflowerBiomeList = config.getStringList(
                "BrightSunflowerBiomeList",
                "offlawn",
                new String[] { "Wheatfield" },
                "Specific biomes where Bright Sunflower patches may spawn.\n"
                        + "Accepted entries: biome id (4), biome name (Wheatfield), name:Wheatfield, or biome dictionary tag (type:PLAINS)."
        );

        offLawnEnableMixedSunflowerWorldgen = config.getBoolean(
                "EnableMixedSunflowerWorldgen",
                "offlawn",
                true,
                "If true, mixed OffLawn sunflower patches naturally generate with both sunflower types in the same patch."
        );

        offLawnMixedSunflowerPatchChance = config.getInt(
                "MixedSunflowerPatchChance",
                "offlawn",
                4,
                1,
                1000,
                "Base one-in-N chance for a large mixed sunflower patch cell to spawn. Higher values make the mixed patches rarer."
        );

        offLawnMixedSunflowerAttemptsPerChunk = config.getInt(
                "MixedSunflowerAttemptsPerChunk",
                "offlawn",
                1,
                0,
                128,
                "Target sunflower placements per full mixed patch. The generator clamps this to a sane patch size to avoid carpet coverage."
        );

        offLawnMixedSunflowerPatchRadiusBlocks = config.getInt(
                "MixedSunflowerPatchRadiusBlocks",
                "offlawn",
                12,
                4,
                128,
                "Approximate radius in blocks for a full mixed sunflower spiral patch. Larger values spread patches across multiple chunks."
        );

        offLawnMixedSunflowerBiomeTypes = config.getStringList(
                "MixedSunflowerBiomeTypes",
                "offlawn",
                new String[] { "PLAINS", "FOREST" },
                "Biome dictionary types that mixed sunflower patches may spawn in.\n"
                        + "Examples: PLAINS, FOREST. Leave empty to only use MixedSunflowerBiomeList."
        );

        offLawnMixedSunflowerBiomeList = config.getStringList(
                "MixedSunflowerBiomeList",
                "offlawn",
                new String[] { "Wheatfield" },
                "Specific biomes where mixed sunflower patches may spawn.\n"
                        + "Accepted entries: biome id (4), biome name (Wheatfield), name:Wheatfield, or biome dictionary tag (type:PLAINS)."
        );

        offLawnBeanstalkMaxGrowthLevel = config.getInt(
                "BeanstalkMaxGrowthLevel",
                "offlawn",
                256,
                1,
                256,
                "Maximum contiguous beanstalk height from the base block. 256 preserves the previous effectively-unlimited growth behavior."
        );

        offLawnSunflowerAuraEnabled = config.getBoolean(
                "SunflowerAuraEnabled",
                "offlawn",
                true,
                "If true, Sunflower Bushes apply their configured potion effects to nearby players. The default effect list is empty."
        );

        offLawnSunflowerAuraRadius = config.getFloat(
                "SunflowerAuraRadius",
                "offlawn",
                12.0F,
                0.0F,
                64.0F,
                "Radius around a Sunflower Bush that receives its configured aura effects."
        );

        offLawnSunflowerAuraEffects = config.getStringList(
                "SunflowerAuraEffects",
                "offlawn",
                new String[0],
                "Potion effects applied by Sunflower Bushes.\n"
                        + "Format per entry: potionNameOrId,level,durationSeconds\n"
                        + "Examples: regeneration,1,4  or  moveSpeed,2,10. Empty by default."
        );

        offLawnBrightSunflowerAuraEnabled = config.getBoolean(
                "BrightSunflowerAuraEnabled",
                "offlawn",
                true,
                "If true, Bright Sunflowers apply their separately configured potion effects to nearby players. The default effect list is empty."
        );

        offLawnBrightSunflowerAuraRadius = config.getFloat(
                "BrightSunflowerAuraRadius",
                "offlawn",
                12.0F,
                0.0F,
                64.0F,
                "Radius around a Bright Sunflower that receives its configured aura effects."
        );

        offLawnBrightSunflowerAuraEffects = config.getStringList(
                "BrightSunflowerAuraEffects",
                "offlawn",
                new String[0],
                "Potion effects applied by Bright Sunflowers.\n"
                        + "Format per entry: potionNameOrId,level,durationSeconds\n"
                        + "Examples: regeneration,1,4  or  moveSpeed,2,10. Empty by default."
        );

        offLawnBrightSunflowerSpeedBoostEnabled = config.getBoolean(
                "BrightSunflowerSpeedBoostEnabled",
                "offlawn",
                true,
                "If true, Bright Sunflowers grant a separate movement-speed attribute boost that stacks with potion effects."
        );

        offLawnBrightSunflowerSpeedBoostPercent = config.getFloat(
                "BrightSunflowerSpeedBoostPercent",
                "offlawn",
                15.0F,
                0.0F,
                1000.0F,
                "Stacking movement speed increase from Bright Sunflowers, as a percentage. 15 = 15%."
        );

        offLawnBrightSunflowerHappyPotionId = config.getInt(
                "BrightSunflowerHappyPotionId",
                "offlawn",
                223,
                0,
                65535,
                "Potion ID preferred by the Bright Sunflower Happy! speed effect. If occupied, the next free ID is used."
        );

        offLawnBrightSunflowerSpeedBoostRadius = config.getFloat(
                "BrightSunflowerSpeedBoostRadius",
                "offlawn",
                12.0F,
                0.0F,
                64.0F,
                "Radius around a Bright Sunflower that grants its stacking speed boost."
        );

        offLawnBrightSunflowerSpeedBoostDurationSeconds = config.getInt(
                "BrightSunflowerSpeedBoostDurationSeconds",
                "offlawn",
                4,
                1,
                60,
                "How long the Bright Sunflower stacking speed boost remains after its last refresh, in seconds."
        );

        offLawnBrightSunflowerSeedChance = config.getFloat(
                "BrightSunflowerSeedChance",
                "offlawn",
                0.10F,
                0.0F,
                1.0F,
                "Chance that planting a Sunflower Seed creates a Bright Sunflower. 0.10 = 10%; remaining chance creates a Sunflower Bush."
        );

        enablePumpkinPasturesModule = config.getBoolean(
                "EnablePumpkinPasturesModule",
                "pumpkinpastures",
                true,
                "Master switch for integrated Pumpkin Pastures content."
        );

        pumpkinPasturesCreeperExplosionStrength = config.getFloat(
                "PumpkinCreeperExplosionStrength",
                "pumpkinpastures",
                3.0F,
                0.0F,
                1024.0F,
                "Explosion strength for Pumpkin Creepers. Vanilla creepers use 3.0; powered Pumpkin Creepers double this value."
        );

        pumpkinPasturesCreeperDamageMultiplier = config.getFloat(
                "PumpkinCreeperDamageMultiplier",
                "pumpkinpastures",
                1.0F,
                0.0F,
                1024.0F,
                "Multiplier applied to Pumpkin Creeper explosion damage compared to normal explosion damage."
        );

        pumpkinPasturesCreeperKnockbackMultiplier = config.getFloat(
                "PumpkinCreeperKnockbackMultiplier",
                "pumpkinpastures",
                1.0F,
                0.0F,
                1024.0F,
                "Multiplier applied to Pumpkin Creeper explosion knockback compared to normal explosion knockback."
        );

        pumpkinPasturesCreeperExplosionDamagesEnvironment = config.getBoolean(
                "PumpkinCreeperExplosionDamagesEnvironment",
                "pumpkinpastures",
                false,
                "If false, Pumpkin Creeper explosions still damage living entities but do not destroy blocks or non-living entities such as item frames."
        );

        enableGokiStatsModule = config.getBoolean(
                "EnableGokiStatsModule",
                "gokistats",
                true,
                "Master switch for integrated GokiStats content."
        );

        enableRiftExplorerModule = config.getBoolean(
                "EnableRiftExplorerModule",
                "riftexplorer",
                true,
                "Master switch for integrated Rift Explorer content."
        );
        riftExplorerEnablePebbleRecipes = config.getBoolean(
                "EnablePebbleRecipes",
                "riftexplorer",
                true,
                "If true, enables pebble to cobblestone and cobblestone to pebble conversion recipes."
        );
        riftExplorerPebblesDropFromGrass = config.getBoolean(
                "PebblesDropFromGrass",
                "riftexplorer",
                true,
                "If true, Rift Explorer pebbles can drop when tall grass is broken."
        );
        riftExplorerSlingshotAmmoItems = config.getStringList(
                "SlingshotAmmoItems",
                "riftexplorer",
                new String[]{"minecraft:cobblestone:0", "riftflux:pebble"},
                "Exact item stacks accepted as slingshot ammo. Format: modid:item or modid:item:meta. Use minecraft:cobblestone:0 for normal cobblestone only."
        );
        riftExplorerSlingshotEnableOreDictionaryAmmo = config.getBoolean(
                "SlingshotEnableOreDictionaryAmmo",
                "riftexplorer",
                true,
                "If true, slingshot ammo also matches SlingshotAmmoOreDictionary entries in addition to SlingshotAmmoItems."
        );
        riftExplorerSlingshotAmmoOreDictionary = config.getStringList(
                "SlingshotAmmoOreDictionary",
                "riftexplorer",
                new String[]{"cobblestone", "stone"},
                "OreDictionary names accepted as additional slingshot ammo when SlingshotEnableOreDictionaryAmmo is true."
        );
        riftExplorerSlingshotSpecialAmmoEntries = config.getStringList(
                "SlingshotSpecialAmmoEntries",
                "riftexplorer",
                new String[]{
                        "minecraft:obsidian|8",
                        "minecraft:tnt|12|0|4",
                        "ore:blockIron|12",
                        "minecraft:hay_block|1|3",
                        "minecraft:ender_chest|0|0|0|capture",
                        "minecraft:ender_pearl|6|0|0|enderpearl",
                        "minecraft:ender_eye|9|0|0|teleport"
                },
                "Special slingshot ammo behavior entries. Format: match|damage|knockback|explosionStrength|action, or use \"ore:name : damage\" for damage-only ore dictionary entries. Damage is exact for special ammo before enchantment and ranged blessing bonuses. Actions: capture, teleport, enderpearl. teleport randomly teleports hit mobs; enderpearl teleports the shooter to block hits and swaps the shooter with hit mobs. A positive explosionStrength makes the ammo explode on impact and damage terrain."
        );
        riftExplorerSlingshotDisabledAmmoItems = config.getStringList(
                "SlingshotDisabledAmmoItems",
                "riftexplorer",
                new String[0],
                "Ammo entries that are blocked even if they appear in SlingshotAmmoItems, SlingshotAmmoOreDictionary, or SlingshotSpecialAmmoEntries. Format: modid:item, modid:item:meta, or ore:name."
        );
        riftExplorerSlingshotSkeletonAmmoEntries = config.getStringList(
                "SlingshotSkeletonAmmoEntries",
                "riftexplorer",
                new String[]{
                        "riftflux:pebble|70",
                        "minecraft:cobblestone|70",
                        "minecraft:stone:0|70",
                        "minecraft:iron_block|15",
                        "minecraft:ender_eye|15",
                        "minecraft:ender_pearl|15",
                        "minecraft:obsidian|15"
                },
                "Ammo entries that slingshot skeletons can spawn with. Format: match|chance. match accepts modid:item, modid:item:meta, or ore:name. chance accepts 0-1 or 0-100 values and each entry is rolled independently; one successful entry is chosen at random. Capture ammo is always excluded."
        );
        riftExplorerSlingshotAmmoIconCorner = config.getString(
                "SlingshotAmmoIconCorner",
                "riftexplorer",
                "top_right",
                "Corner used for the slingshot's selected-ammo icon in the inventory. Accepted values: top_right, bottom_right, top_left, bottom_left. Spaces and hyphens are also accepted."
        );
        riftExplorerSlingshotBlockBossCapture = config.getBoolean(
                "SlingshotBlockBossCapture",
                "riftexplorer",
                true,
                "If true, slingshot capture ammo cannot capture boss entities."
        );
        riftExplorerSlingshotCaptureMobBlacklist = config.getStringList(
                "SlingshotCaptureMobBlacklist",
                "riftexplorer",
                new String[0],
                "Entities that slingshot capture ammo cannot capture. Accepts entity ids, display names, simple class names, or full class names. Examples: Zombie, minecraft:zombie, net.minecraft.entity.monster.EntityZombie."
        );
        riftExplorerSlingshotBaseDamage = config.getFloat(
                "SlingshotBaseDamage",
                "riftexplorer",
                3.0F,
                0.0F,
                1000.0F,
                "Base damage for normal slingshot ammo before projectile speed, Power enchantment, and ranged blessing bonuses. Special ammo with configured damage uses its own damage value."
        );
        riftExplorerDisabledLongbowArrows = config.getStringList(
                "DisabledLongbowArrows",
                "riftexplorer",
                new String[] {"obsidian_arrow"},
                "Longbow special arrow tiers disabled from creative listing, crafting recipes, and longbow ammo use. Accepts names like obsidian_arrow or metadata numbers like 5."
        );
        riftExplorerLongbowDurability = config.getInt(
                "LongbowDurability",
                "riftexplorer",
                512,
                0,
                32767,
                "Durability for the Rift Explorer Longbow. Set to 0 for unlimited durability."
        );
        riftExplorerSlingshotDurability = config.getInt(
                "SlingshotDurability",
                "riftexplorer",
                192,
                0,
                32767,
                "Durability for the Rift Explorer Slingshot. Set to 0 for unlimited durability."
        );
        riftExplorerBoomerangDurability = config.getInt(
                "BoomerangDurability",
                "riftexplorer",
                256,
                0,
                32767,
                "Durability for the Rift Explorer Boomerang. Set to 0 for unlimited durability."
        );
        riftExplorerBoomerangImpactModifierItems = config.getStringList(
                "BoomerangImpactModifierItems",
                "riftexplorer",
                new String[]{"minecraft:gunpowder|5"},
                "Items that add Impact Modifier to the Rift Explorer Boomerang. Format per entry: modid:item|percent"
        );
        riftExplorerBoomerangCapacityModifierItems = config.getStringList(
                "BoomerangCapacityModifierItems",
                "riftexplorer",
                new String[]{"minecraft:slime_ball|1"},
                "Items that add Capacity Modifier to the Rift Explorer Boomerang. Format per entry: modid:item|stacks"
        );
        riftExplorerBoomerangPowerModifierItems = config.getStringList(
                "BoomerangPowerModifierItems",
                "riftexplorer",
                new String[]{"minecraft:glowstone_dust|2", "minecraft:glowstone|18"},
                "Items that add Power Modifier to the Rift Explorer Boomerang. Format per entry: modid:item|percent"
        );
        riftExplorerBoomerangReachModifierItems = config.getStringList(
                "BoomerangReachModifierItems",
                "riftexplorer",
                new String[]{"minecraft:redstone|0.25", "minecraft:redstone_block|2.25"},
                "Items that add Reach Modifier to the Rift Explorer Boomerang. Format per entry: modid:item|blocks"
        );
        riftExplorerBoomerangEnderModifierItems = config.getStringList(
                "BoomerangEnderModifierItems",
                "riftexplorer",
                new String[]{"minecraft:ender_pearl|5", "minecraft:ender_eye|15"},
                "Items that add Ender Modifier to the Rift Explorer Boomerang. Format per entry: modid:item|percent"
        );
        riftExplorerBoomerangUnbreakingModifierItems = config.getStringList(
                "BoomerangUnbreakingModifierItems",
                "riftexplorer",
                new String[]{"minecraft:obsidian|10"},
                "Items that add Unbreaking Modifier to the Rift Explorer Boomerang. Format per entry: modid:item|percent"
        );
        riftExplorerBoomerangImpactModifierMaxPercent = config.getFloat(
                "BoomerangImpactModifierMaxPercent",
                "riftexplorer",
                300.0F,
                0.0F,
                100000.0F,
                "Maximum total Impact Modifier percent on a Rift Explorer Boomerang. Set to 0 for no cap."
        );
        riftExplorerBoomerangCapacityModifierMaxStacks = config.getInt(
                "BoomerangCapacityModifierMaxStacks",
                "riftexplorer",
                8,
                0,
                32767,
                "Maximum extra stacks from Capacity Modifier on a Rift Explorer Boomerang. Set to 0 for no cap."
        );
        riftExplorerBoomerangPowerModifierMaxPercent = config.getFloat(
                "BoomerangPowerModifierMaxPercent",
                "riftexplorer",
                120.0F,
                0.0F,
                100000.0F,
                "Maximum total Power Modifier percent on a Rift Explorer Boomerang. Set to 0 for no cap."
        );
        riftExplorerBoomerangReachModifierMaxPercent = config.getFloat(
                "BoomerangReachModifierMaxPercent",
                "riftexplorer",
                300.0F,
                0.0F,
                100000.0F,
                "Maximum total Reach Modifier percent on a Rift Explorer Boomerang. Set to 0 for no cap."
        );
        riftExplorerBoomerangEnderModifierMaxPercent = config.getFloat(
                "BoomerangEnderModifierMaxPercent",
                "riftexplorer",
                100.0F,
                0.0F,
                100000.0F,
                "Maximum total Ender Modifier percent on a Rift Explorer Boomerang. Set to 0 to use the hard 100 percent cap."
        );
        riftExplorerBoomerangUnbreakingModifierMaxPercent = config.getFloat(
                "BoomerangUnbreakingModifierMaxPercent",
                "riftexplorer",
                100.0F,
                0.0F,
                100000.0F,
                "Maximum total Unbreaking Modifier percent on a Rift Explorer Boomerang. Set to 0 to use the hard 100 percent cap."
        );
        riftExplorerBoomerangMaxModifierTypes = config.getInt(
                "BoomerangMaxModifierTypes",
                "riftexplorer",
                5,
                0,
                64,
                "Maximum number of distinct modifier types allowed on a Rift Explorer Boomerang. Set to 0 for no cap."
        );
        riftExplorerBoomerangStarInfusedItems = config.getStringList(
                "BoomerangStarInfusedItems",
                "riftexplorer",
                new String[]{"minecraft:nether_star", "worldexplorer:raregem"},
                "Items that can Star-Infuse a Rift Explorer Boomerang once. Format per entry: modid:item or modid:item:meta"
        );
        riftExplorerBoomerangStarInfusedBonusModifierTypes = config.getInt(
                "BoomerangStarInfusedBonusModifierTypes",
                "riftexplorer",
                1,
                0,
                64,
                "Additional distinct modifier types allowed after Star-Infusing a Rift Explorer Boomerang once. Set to 0 to disable the bonus."
        );
        riftExplorerBoomerangStarInfusedBonusPercent = config.getFloat(
                "BoomerangStarInfusedBonusPercent",
                "riftexplorer",
                50.0F,
                0.0F,
                100000.0F,
                "Percent increase to Rift Explorer Boomerang modifier caps after Star-Infusing it once. This applies to modifier caps and the combined modifier percent cap. Ender and Unbreaking are still hard-capped at 100 percent. Set to 0 to disable the bonus."
        );
        riftExplorerBoomerangMaxCombinedModifierPercent = config.getFloat(
                "BoomerangMaxCombinedModifierPercent",
                "riftexplorer",
                450.0F,
                0.0F,
                100000.0F,
                "Maximum combined percent from Impact, Power, Reach, Ender, and Unbreaking Modifier on a Rift Explorer Boomerang. Capacity Modifier does not count toward this cap. Set to 0 for no cap."
        );
        riftExplorerBlowpipeDurability = config.getInt(
                "BlowpipeDurability",
                "riftexplorer",
                350,
                0,
                32767,
                "Durability for the Rift Explorer Blowpipe. Set to 0 for unlimited durability."
        );
        riftExplorerDartDamage = config.getFloat(
                "DartDamage",
                "riftexplorer",
                1.0F,
                0.0F,
                1000.0F,
                "Base damage dealt by Rift Explorer darts before enchantment bonuses."
        );
        riftExplorerDartMaxPotionModifiers = config.getInt(
                "DartMaxPotionModifiers",
                "riftexplorer",
                3,
                0,
                64,
                "Maximum number of extra non-poison potion modifiers a dart stack can hold. The built-in poison effect does not count toward this cap. Set to 0 to allow poison-only darts."
        );
        riftExplorerDartPotionDurationSeconds = config.getFloat(
                "DartPotionDurationSeconds",
                "riftexplorer",
                3.0F,
                0.1F,
                3600.0F,
                "Duration, in seconds, for all potion effects applied by Rift Explorer darts. Potion item durations are normalized to this value."
        );
        riftExplorerDartPotionEffectFilterWhitelistMode = config.getBoolean(
                "DartPotionEffectFilterWhitelistMode",
                "riftexplorer",
                false,
                "If false, DartPotionEffectFilter is a blacklist.\n" +
                        "If true, DartPotionEffectFilter is a whitelist."
        );
        riftExplorerDartPotionEffectFilter = config.getStringList(
                "DartPotionEffectFilter",
                "riftexplorer",
                new String[0],
                "Potion effects allowed or blocked for dart infusion.\n" +
                        "Entries can be potion IDs or names like heal, harm, moveSpeed, or Instant Health."
        );
        riftExplorerDartPotionLevelCap = config.getInt(
                "DartPotionLevelCap",
                "riftexplorer",
                2,
                0,
                64,
                "Maximum potion level allowed on extra dart effects. Set to 0 for no cap. A value of 2 allows up to level II."
        );

        enableMoreBowsModule = config.getBoolean(
                "EnableMoreBowsModule",
                "morebows",
                true,
                "Master switch for integrated More Bows content. The original steel ingot and steel crafting content is not included."
        );

        pumpkinPasturesEnderflameSwordDamage = config.getFloat(
                "EnderflameSwordDamage",
                "pumpkinpastures",
                12.0F,
                0.0F,
                1000.0F,
                "Base attack damage for Enderflame Sword."
        );
        pumpkinPasturesEnderflameSwordDurability = config.getInt(
                "EnderflameSwordDurability",
                "pumpkinpastures",
                350,
                0,
                32767,
                "Durability for Enderflame Sword. Set to 0 for unlimited durability."
        );
        pumpkinPasturesEnderflamePickaxeDurability = config.getInt(
                "EnderflamePickaxeDurability",
                "pumpkinpastures",
                350,
                0,
                32767,
                "Durability for Enderflame Pickaxe. Set to 0 for unlimited durability."
        );
        pumpkinPasturesEnderflameShaxDurability = config.getInt(
                "EnderflameShaxDurability",
                "pumpkinpastures",
                350,
                0,
                32767,
                "Durability for Enderflame Shax. Set to 0 for unlimited durability."
        );
        pumpkinPasturesEnderflameToolEfficiency = config.getFloat(
                "EnderflameToolEfficiency",
                "pumpkinpastures",
                10.64F,
                0.1F,
                1000.0F,
                "Mining speed (efficiency) for Enderflame tools. Diamond is 8.0, so 10.64 is ~33% faster."
        );
        pumpkinPasturesEnderflamePickaxeAutoSmelt = config.getBoolean(
                "EnderflamePickaxeAutoSmelt",
                "pumpkinpastures",
                true,
                "If true, Enderflame Pickaxe smelts harvested drops after fortune is applied."
        );
        pumpkinPasturesEnderflameShaxAutoSmelt = config.getBoolean(
                "EnderflameShaxAutoSmelt",
                "pumpkinpastures",
                true,
                "If true, Enderflame Shax smelts harvested drops after fortune is applied."
        );
        pumpkinPasturesCorruptedSoulDropEntries = config.getStringList(
                "CorruptedSoulDropEntries",
                "pumpkinpastures",
                new String[]{
                        "RiftFluxPumpkinZombie|0.5",
                        "RiftFluxPumpkinSkeleton|0.5",
                        "RiftFluxPumpkinCreeper|0.5"
                },
                "Corrupted soul drop table. Format: mob_id|chance_percent. Example: RiftFluxPumpkinZombie|0.5"
        );
        pumpkinPasturesPumpkinSoulDropEntries = config.getStringList(
                "PumpkinSoulDropEntries",
                "pumpkinpastures",
                new String[]{
                        "RiftFluxPumpkinZombie|2.0",
                        "RiftFluxPumpkinSkeleton|2.0",
                        "RiftFluxPumpkinCreeper|2.0"
                },
                "Pumpkin soul drop table. Format: mob_id|chance_percent. Example: RiftFluxPumpkinZombie|2.0"
        );
        pumpkinPasturesEnderflameStaffCastsSpell = config.getBoolean(
                "EnderflameStaffCastsSpell",
                "pumpkinpastures",
                true,
                "If true, Enderflame Staff right-click charges and casts the Ember Staff fire spell. If false, it behaves like a normal blocking weapon."
        );
        pumpkinPasturesEnderflameStaffManaCost = config.getFloat(
                "EnderflameStaffManaCost",
                "pumpkinpastures",
                7.0F,
                0.0F,
                1000.0F,
                "Mana cost per Enderflame Staff cast."
        );
        pumpkinPasturesEnderflameStaffDurability = config.getInt(
                "EnderflameStaffDurability",
                "pumpkinpastures",
                512,
                0,
                32767,
                "Durability for Enderflame Staff. Set to 0 for unlimited durability."
        );
        pumpkinPasturesEnderflameStaffFireSeconds = config.getFloat(
                "EnderflameStaffFireSeconds",
                "pumpkinpastures",
                4.5F,
                0.0F,
                600.0F,
                "How long Enderflame Staff fire spells ignite entities for (in seconds)."
        );
        pumpkinPasturesEnderflameStaffSpellDamage = config.getFloat(
                "EnderflameStaffSpellDamage",
                "pumpkinpastures",
                14.0F,
                0.0F,
                1000.0F,
                "Base spell damage power for Enderflame Staff casts."
        );

        enablePalariaModule = config.getBoolean(
                "EnablePalariaModule",
                PALARIA_CATEGORY,
                true,
                "If false, disables all Palaria mob, item, egg, render, and spawn registration."
        );
        enablePalariaCowasaurus = config.getBoolean(
                "EnableCowasaurus",
                PALARIA_CATEGORY,
                true,
                "If true, the Palaria Cowasaurus mob and spawn egg are registered."
        );
        palariaCowasaurusMaxHealth = config.getFloat(
                "CowasaurusMaxHealth",
                PALARIA_CATEGORY,
                140.0F,
                1.0F,
                10000.0F,
                "Base max health for Cowasaurus."
        );
        enablePalariaCreeptile = config.getBoolean(
                "EnableCreeptile",
                PALARIA_CATEGORY,
                true,
                "If true, the Palaria Creeptile mob and spawn egg are registered."
        );
        palariaCreeptileMaxHealth = config.getFloat(
                "CreeptileMaxHealth",
                PALARIA_CATEGORY,
                40.0F,
                1.0F,
                10000.0F,
                "Base max health for Creeptiles."
        );
        palariaCreeptileExplosionStrength = config.getFloat(
                "CreeptileExplosionStrength",
                PALARIA_CATEGORY,
                6.0F,
                0.0F,
                100.0F,
                "Explosion strength for Creeptiles. Vanilla creepers use 3.0; 6.0 is double vanilla."
        );
        palariaCreeptileDamageMultiplier = config.getFloat(
                "CreeptileDamageMultiplier",
                PALARIA_CATEGORY,
                1.5F,
                0.0F,
                100.0F,
                "Multiplier applied to Creeptile explosion damage compared to normal explosion damage."
        );
        palariaCreeptileKnockbackMultiplier = config.getFloat(
                "CreeptileKnockbackMultiplier",
                PALARIA_CATEGORY,
                1.5F,
                0.0F,
                100.0F,
                "Multiplier applied to Creeptile explosion knockback compared to normal explosion knockback."
        );
        palariaCreeptileExplosionDamagesEnvironment = config.getBoolean(
                "CreeptileExplosionDamagesEnvironment",
                PALARIA_CATEGORY,
                true,
                "If false, Creeptile explosions still damage living entities but do not destroy blocks or non-living entities such as item frames."
        );
        enablePalariaRaptorChicken = config.getBoolean(
                "EnableRaptorChicken",
                PALARIA_CATEGORY,
                true,
                "If true, the Palaria Raptor Chicken mob and spawn egg are registered."
        );
        palariaRaptorChickenMaxHealth = config.getFloat(
                "RaptorChickenMaxHealth",
                PALARIA_CATEGORY,
                50.0F,
                1.0F,
                10000.0F,
                "Base max health for Raptor Chickens."
        );
        enablePalariaEnderWalker = config.getBoolean(
                "EnableEnderWalker",
                PALARIA_CATEGORY,
                true,
                "If true, the Palaria Ender Walker mob and spawn egg are registered."
        );
        palariaEnderWalkerMaxHealth = config.getFloat(
                "EnderWalkerMaxHealth",
                PALARIA_CATEGORY,
                80.0F,
                1.0F,
                10000.0F,
                "Base max health for Ender Walkers."
        );
        enablePalariaNimatin = config.getBoolean(
                "EnableNimatin",
                PALARIA_CATEGORY,
                true,
                "If true, the Palaria Nimatin mob and spawn egg are registered."
        );
        palariaNimatinTameable = config.getBoolean(
                "NimatinTameable",
                PALARIA_CATEGORY,
                true,
                "If true, Nimatins can be tamed with the configured NimatinTameItems."
        );
        palariaNimatinTameChance = config.getFloat(
                "NimatinTameChance",
                PALARIA_CATEGORY,
                0.33F,
                0.0F,
                1.0F,
                "Chance for a valid tame attempt to tame a Nimatin. 0.33 is 1 in 3."
        );
        palariaNimatinMaxHealth = config.getFloat(
                "NimatinMaxHealth",
                PALARIA_CATEGORY,
                100.0F,
                1.0F,
                10000.0F,
                "Base max health for untamed Nimatins."
        );
        palariaNimatinTamedMaxHealth = config.getFloat(
                "NimatinTamedMaxHealth",
                PALARIA_CATEGORY,
                200.0F,
                1.0F,
                10000.0F,
                "Base max health for tamed Nimatins."
        );
        palariaNimatinTamedDamage = config.getFloat(
                "NimatinTamedDamage",
                PALARIA_CATEGORY,
                40.0F,
                0.0F,
                10000.0F,
                "Attack damage dealt by tamed Nimatins."
        );
        palariaNimatinRidingSpeed = config.getFloat(
                "NimatinRidingSpeed",
                PALARIA_CATEGORY,
                0.4F,
                0.0F,
                10.0F,
                "Movement speed used while directly riding a Nimatin."
        );
        palariaNimatinMaxJumpHeight = config.getFloat(
                "NimatinMaxJumpHeight",
                PALARIA_CATEGORY,
                7.0F,
                0.5F,
                100.0F,
                "Maximum jump height in blocks for a fully charged ridden Nimatin jump."
        );
        palariaNimatinDoubleJumpEnabled = config.getBoolean(
                "NimatinDoubleJumpEnabled",
                PALARIA_CATEGORY,
                true,
                "If true, a ridden Nimatin can perform one configurable mid-air double jump per airborne sequence with a quick jump-key tap."
        );
        palariaNimatinDoubleJumpHeight = config.getFloat(
                "NimatinDoubleJumpHeight",
                PALARIA_CATEGORY,
                6.0F,
                0.5F,
                100.0F,
                "Jump height in blocks for Nimatin's mid-air double jump."
        );
        palariaNimatinTeleportDistance = config.getFloat(
                "NimatinTeleportDistance",
                PALARIA_CATEGORY,
                48.0F,
                0.0F,
                256.0F,
                "Distance in blocks before a tamed Nimatin teleports to its owner while following. Set to 0 to disable follow-owner teleporting."
        );
        palariaNimatinTalkInterval = config.getInt(
                "NimatinTalkInterval",
                PALARIA_CATEGORY,
                123,
                1,
                10000,
                "Ticks between Nimatin ambient purr attempts while tamed. Higher values make purring less frequent."
        );
        palariaNimatinKillHealAmount = config.getFloat(
                "NimatinKillHealAmount",
                PALARIA_CATEGORY,
                4.0F,
                0.0F,
                10000.0F,
                "Health a Nimatin restores after killing a target. Set to 0 to disable kill healing."
        );
        palariaNimatinOwnerKillHealMultiplier = config.getFloat(
                "NimatinOwnerKillHealMultiplier",
                PALARIA_CATEGORY,
                0.5F,
                0.0F,
                100.0F,
                "Multiplier applied to NimatinKillHealAmount and healed to the owner when the Nimatin gets a kill. 0 disables owner healing; 0.5 gives half."
        );
        palariaNimatinTameItems = config.getStringList(
                "NimatinTameItems",
                PALARIA_CATEGORY,
                DEFAULT_PALARIA_NIMATIN_TAME_ITEMS,
                "Items that can tame Nimatins. Use registry ids, optionally with @meta, for example riftflux:creeptile_eye."
        );
        palariaNimatinAllowMobPassengers = config.getBoolean(
                "NimatinAllowMobPassengers",
                PALARIA_CATEGORY,
                true,
                "If true, tamed Nimatins can auto-seat nearby allowed non-player mobs in the rear passenger seat while standing still. If false, only players can use the passenger seat."
        );
        palariaNimatinMobPassengerBlacklist = config.getStringList(
                "NimatinMobPassengerBlacklist",
                PALARIA_CATEGORY,
                DEFAULT_PALARIA_NIMATIN_PASSENGER_BLACKLIST,
                "Entity IDs/class names used to block which mobs can ride Nimatin's passenger seat.\n" +
                        "Matches entity ID, class simple name, or full class name.\n" +
                        "Default blocks Nimatin from mounting itself."
        );
        palariaNimatinMobPassengerBlacklist = sanitizeNimatinPassengerFilter(palariaNimatinMobPassengerBlacklist);
        config.getCategory(PALARIA_CATEGORY)
                .get("NimatinMobPassengerBlacklist")
                .set(palariaNimatinMobPassengerBlacklist);
        enablePalariaEnderRaptorChicken = config.getBoolean(
                "EnableEnderRaptorChicken",
                PALARIA_CATEGORY,
                true,
                "If true, the Palaria Ender Raptor Chicken mob and spawn egg are registered."
        );
        palariaEnderRaptorChickenMaxHealth = config.getFloat(
                "EnderRaptorChickenMaxHealth",
                PALARIA_CATEGORY,
                100.0F,
                1.0F,
                10000.0F,
                "Base max health for Ender Raptor Chickens."
        );
        enablePalariaMagmaRaptorChicken = config.getBoolean(
                "EnableMagmaRaptorChicken",
                PALARIA_CATEGORY,
                true,
                "If true, the Palaria Magma Raptor Chicken mob and spawn egg are registered."
        );
        palariaMagmaRaptorChickenMaxHealth = config.getFloat(
                "MagmaRaptorChickenMaxHealth",
                PALARIA_CATEGORY,
                80.0F,
                1.0F,
                10000.0F,
                "Base max health for Magma Raptor Chickens."
        );
        palariaMagmaRaptorChickenPlaceFire = config.getBoolean(
                "MagmaRaptorChickenPlaceFire",
                PALARIA_CATEGORY,
                false,
                "If true, Magma Raptor Chickens place fire blocks around where they stand."
        );
        palariaNimatinDropEntries = config.getStringList(
                "NimatinDropEntries",
                PALARIA_CATEGORY,
                DEFAULT_PALARIA_NIMATIN_DROPS,
                "Independent Nimatin drop rolls. Syntax: item_or_alias*min-max|chance. Chance accepts 0.05 or 5 for 5%. Tamed Nimatins drop nothing."
        );
        palariaCowasaurusDropEntries = config.getStringList(
                "CowasaurusDropEntries",
                PALARIA_CATEGORY,
                DEFAULT_PALARIA_COWASAURUS_DROPS,
                "Independent Cowasaurus drop rolls. Syntax: item_or_alias*min-max|chance. Chance accepts 0.05 or 5 for 5%."
        );
        palariaCreeptileDropEntries = config.getStringList(
                "CreeptileDropEntries",
                PALARIA_CATEGORY,
                DEFAULT_PALARIA_CREEPTILE_DROPS,
                "Independent Creeptile drop rolls. Syntax: item_or_alias*min-max|chance. Chance accepts 0.05 or 5 for 5%."
        );

        satisforestryLizardDoggoAllowNametagRename = config.getBoolean(
                "LizardDoggoAllowNametagRename",
                "general",
                true,
                "If true, Satisforestry Lizard Doggos use their vanilla custom name tag instead of always displaying Lizard Doggo."
        );
        satisforestryLizardDoggoDisableRandomItemFinding = config.getBoolean(
                "LizardDoggoDisableRandomItemFinding",
                "general",
                true,
                "If true, Satisforestry Lizard Doggos no longer generate random found items over time."
        );

        enableWitchHouseStructure = config.getBoolean(
                "EnableWitchHouse",
                "witchesandmore",
                true,
                "If true, witch houses may generate in Wheatfield biomes."
        );

        witchHouseChunkChance = config.getInt(
                "WitchHouseChunkChance",
                "witchesandmore",
                48,
                1,
                100000,
                "One witch house generation attempt per this many chunks in Wheatfield."
        );

        witchHouseMinDistanceBlocks = config.getInt(
                "WitchHouseMinDistanceBlocks",
                "witchesandmore",
                256,
                0,
                100000,
                "Minimum distance in blocks between witch house placements. Set to 0 to disable spacing enforcement."
        );

        witchHouseInsideMobCount = config.getInt(
                "WitchHouseInsideMobCount",
                "witchesandmore",
                7,
                0,
                32,
                "How many configured mobs to spawn inside each witch house."
        );

        witchHouseOutsideMobCount = config.getInt(
                "WitchHouseOutsideMobCount",
                "witchesandmore",
                1,
                0,
                32,
                "How many configured mobs to spawn outside each witch house."
        );

        witchHouseInsideMobIds = config.getStringList(
                "WitchHouseInsideMobIds",
                "witchesandmore",
                new String[]{"black_widow"},
                "Mob ids that may spawn inside witch houses.\n" +
                        "Accepted ids: WAM aliases (cyclops, flower_man, ender_troll, jaxx, black_widow) or any registered EntityLiving name/class alias such as Witch, Zombie, EntityWitch, RiftFluxCyclops, etc."
        );

        witchHouseOutsideMobIds = config.getStringList(
                "WitchHouseOutsideMobIds",
                "witchesandmore",
                new String[]{"cyclops"},
                "Mob ids that may spawn outside witch houses.\n" +
                        "Accepted ids: WAM aliases (cyclops, flower_man, ender_troll, jaxx, black_widow) or any registered EntityLiving name/class alias such as Witch, Zombie, EntityWitch, RiftFluxCyclops, etc."
        );

        witchHouseChestLootEntries = config.getStringList(
                "WitchHouseChestLootEntries",
                "witchesandmore",
                new String[]{
                        "riftflux:as_shield_patchwork|15",
                        "riftflux:as_shield_patchwork_gilded|15",
                        "riftflux:as_giant_sword_patchwork|15",
                        "riftflux:as_shield_skull|15",
                        "riftflux:as_shield_skull_gilded|15",
                        "riftflux:as_giant_sword_skull|15"
                },
                "Potential loot entries for witch house chests.\n" +
                        "Format per entry: modid:item*count@meta|chance\n" +
                        "Examples: riftflux:as_shield_patchwork|15  or  minecraft:gold_ingot*3|0.25\n" +
                        "Chance values above 1 are treated as percentages."
        );

        specialArmorLootSlimeHelmet = config.getBoolean(
                "LootSlimeHelmet",
                "specialarmor",
                true,
                "If true, Slime Helmet can appear in dungeon chests."
        );

        specialArmorLootDoubleJumpBoots = config.getBoolean(
                "LootDoubleJumpBoots",
                "specialarmor",
                true,
                "If true, Double Jump Boots can appear in dungeon chests."
        );

        specialArmorLootSkates = config.getBoolean(
                "LootSkates",
                "specialarmor",
                true,
                "If true, Skates can appear in dungeon chests."
        );

        specialArmorLootHeavyBoots = config.getBoolean(
                "LootHeavyBoots",
                "specialarmor",
                true,
                "If true, Heavy Boots can appear in dungeon chests."
        );

        enableInventoryPetsModule = config.getBoolean(
                "EnableInventoryPetsModule",
                "inventorypets",
                true,
                "Master switch for integrated Inventory Pets content."
        );

        inventoryPetsEnabledEntries = config.getStringList(
                "EnabledPets",
                "inventorypets",
                DEFAULT_INVENTORY_PET_DUNGEON_LOOT,
                "Inventory Pets that will register in the game.\n" +
                        "Remove names from this list to stop those pets from registering at all.\n" +
                        "Valid names: " + INVENTORY_PET_DUNGEON_LOOT_COMMENT
        );

        inventoryPetsDungeonLootEntries = config.getStringList(
                "DungeonLootPets",
                "inventorypets",
                DEFAULT_INVENTORY_PET_DUNGEON_LOOT,
                "Inventory Pets that may appear in dungeon chests.\n" +
                        "Remove names from this list to stop those pets from spawning in dungeon loot.\n" +
                        "Valid names: " + INVENTORY_PET_DUNGEON_LOOT_COMMENT
        );

        inventoryPetsDungeonLootWeight = config.getInt(
                "DungeonLootWeight",
                "inventorypets",
                1,
                0,
                Integer.MAX_VALUE,
                "Dungeon chest weight for each enabled Inventory Pet.\n" +
                        "Set to 0 to disable all Inventory Pet dungeon loot."
        );

        inventoryPetsBananaDamage = config.getFloat(
                "BananaDamage",
                "inventorypets",
                6.0F,
                0.0F,
                1024.0F,
                "Damage dealt by the Banana Pet boomerang."
        );

        enableFurnitureModule = config.getBoolean(
                "EnableFurnitureModule",
                "furniture",
                true,
                "Master switch for integrated furniture content."
        );

        enableChesterModule = config.getBoolean(
                "EnableChesterModule",
                "chester",
                true,
                "Master switch for integrated Chester content."
        );

        enableChesterBaubleSlot = config.getBoolean(
                "EnableBaubleSlot",
                "chester",
                true,
                "If true, registers a dedicated Chester Staff Bauble slot and allows right-click equipping the Eyebone."
        );

        chesterTeleportDistance = config.getFloat(
                "ChesterTeleportDistance",
                "chester",
                24.0F,
                0.0F,
                256.0F,
                "Distance in blocks before Chester teleports to the player carrying his Eyebone. Set to 0 to disable follow-owner teleporting."
        );

        chesterInventoryColor = config.getString(
                "InventoryColor",
                "chester",
                "5A3825",
                "Normal Chester inventory tint. Use a six-digit RGB hex value, optional #/0x prefix, or a supported color name.\n" +
                        "Leave empty to use the default Minecraft inventory color."
        );

        shadowChesterInventoryColor = config.getString(
                "ShadowInventoryColor",
                "chester",
                "51485C",
                "Shadow Chester inventory tint. Use a six-digit RGB hex value, optional #/0x prefix, or a supported color name.\n" +
                        "Leave empty to use the default Minecraft inventory color."
        );

        enableAxolotlModule = config.getBoolean(
                "EnableAxolotlModule",
                "axolotl",
                true,
                "Master switch for integrated axolotl content."
        );

        axolotlMaxHealth = config.getFloat(
                "MaxHealth",
                "axolotl",
                14.0F,
                1.0F,
                1024.0F,
                "Base max health for axolotls."
        );

        enableDucklingModule = config.getBoolean(
                "EnableGhibliModule",
                "ghibli",
                true,
                "Master switch for integrated Ghibli content."
        );

        ghibliStarCandyRecipeEnabled = config.getBoolean(
                "EnableStarCandyRecipe",
                "ghibli",
                false,
                "If true, the Star Candy crafting recipe is registered."
        );

        ducklingAgentDNaturalVariantChancePercent = config.getFloat(
                "DuckAgentDNaturalVariantChancePercent",
                "ghibli",
                1.0F,
                0.0F,
                100.0F,
                "Percent chance for a naturally/randomly generated duck to use the Agent D variant as its real stored variant. Agent D ducks keep the skin even after renaming."
        );

        ghibliSootSpriteCoalOreSpawnChancePercent = config.getFloat(
                "SootSpriteCoalOreSpawnChancePercent",
                "ghibli",
                10.0F,
                0.0F,
                100.0F,
                "Percent chance for breaking coal ore to spawn Soot Sprites."
        );

        int[] sootSpriteCoalOreSpawns = getIntRange(
                "SootSpriteCoalOreSpawns",
                "ghibli",
                "1-4",
                0,
                64,
                "Number of Soot Sprites spawned when coal ore spawning triggers, as min-max, for example 1-3."
        );
        ghibliSootSpriteCoalOreMinSpawns = sootSpriteCoalOreSpawns[0];
        ghibliSootSpriteCoalOreMaxSpawns = sootSpriteCoalOreSpawns[1];

        ghibliSootSpriteChirpIntervalTicks = config.getInt(
                "SootSpriteChirpIntervalTicks",
                "ghibli",
                600,
                0,
                12000,
                "Base interval between Soot Sprite ambient chirps, in ticks. 20 ticks = 1 second. Set to 0 to disable ambient chirps."
        );

        ghibliSootSpriteMaxHealth = config.getFloat(
                "SootSpriteMaxHealth",
                "ghibli",
                6.0F,
                1.0F,
                1024.0F,
                "Base max health for Soot Sprites."
        );

        ghibliSootSpriteHealingItems = config.getStringList(
                "SootSpriteHealingItems",
                "ghibli",
                DEFAULT_SOOT_SPRITE_HEALING_ITEMS,
                "Items that can heal Soot Sprites when fed to them. Syntax: modid:item or modid:item@meta. If meta is omitted, all metadata values match."
        );

        ducklingQuacklingMaxHealth = config.getFloat(
                "QuacklingMaxHealth",
                "ghibli",
                40.0F,
                1.0F,
                4096.0F,
                "Base max health for Quacklings."
        );

        ducklingQuacklingTradingEnabled = config.getBoolean(
                "EnableQuacklingTrading",
                "ghibli",
                true,
                "If false, Quacklings cannot open their trade GUI."
        );

        ducklingQuacklingTradeOnlyWhileFishing = config.getBoolean(
                "QuacklingTradeOnlyWhileFishing",
                "ghibli",
                true,
                "If true, players can only trade with Quacklings while they are sitting and actively fishing."
        );

        ducklingQuacklingTrades = config.getStringList(
                "QuacklingTrades",
                "ghibli",
                DEFAULT_DUCKLING_QUACKLING_TRADES,
                "Trades Quacklings can offer. Leave this list empty to disable Quackling trading. Syntax: buy_item[*count or *min-max][@meta][ + second_buy_item[*count or *min-max][@meta]] -> sell_item[*count or *min-max][@meta][; chance=percent]. Examples: minecraft:emerald*1-4 -> minecraft:fish*1-4, minecraft:emerald*1 + minecraft:fish*2 -> riftflux:duck_egg*1, or minecraft:emerald*4 -> riftflux:duck_egg*1; chance=35. Meta defaults to 0; use 32767 for wildcard input meta. If no trades pass their chance rolls, one valid trade is still guaranteed."
        );

        ducklingQuacklingRefreshTradesDaily = config.getBoolean(
                "QuacklingRefreshTradesDaily",
                "ghibli",
                true,
                "If true, each Quackling rerolls its offers at the start of every in-game day."
        );

        ducklingQuacklingBreedItems = config.getStringList(
                "QuacklingBreedItems",
                "ghibli",
                DEFAULT_DUCKLING_QUACKLING_BREED_ITEMS,
                "Item registry names that can breed and tempt Quacklings. Syntax: modid:item or modid:item@meta. If meta is omitted, all metadata values match. Entries starting with ore: are treated as ore dictionary names."
        );

        ducklingQuacklingBreedOreDictionary = config.getStringList(
                "QuacklingBreedOreDictionary",
                "ghibli",
                DEFAULT_DUCKLING_QUACKLING_BREED_ORE_DICTIONARY,
                "Ore dictionary names that can breed and tempt Quacklings."
        );

        ducklingQuacklingFishingCatchDelayTicks = getIntRangeConfig(
                "ghibli",
                "QuacklingFishingCatchDelayTicks",
                1200,
                3600,
                20,
                72000,
                "Random delay range in ticks before a fishing Quackling pulls up a fish. Format: [minimum, maximum]. 20 ticks = 1 second."
        );

        ducklingQuacklingFishingCatchesBeforeStop = getIntRangeConfig(
                "ghibli",
                "QuacklingFishingCatchesBeforeStop",
                6,
                12,
                1,
                1024,
                "Random catch-count range before a Quackling ends one autonomous fishing session. Format: [minimum, maximum]."
        );

        ducklingQuacklingFishingSessionsPerDay = getIntRangeConfig(
                "ghibli",
                "QuacklingFishingSessionsPerDay",
                0,
                2,
                0,
                64,
                "Random daily autonomous fishing session range for each Quackling. Format: [minimum, maximum]."
        );

        ducklingQuacklingFishingBumpRecoveryTicks = config.getInt(
                "QuacklingFishingBumpRecoveryTicks",
                "ghibli",
                300,
                20,
                72000,
                "How long a bumped or displaced Quackling keeps trying to return to its fishing session before giving up. 20 ticks = 1 second."
        );

        protectCircuitryFromWater = config.getBoolean(
                "ProtectCircuitryFromWater",
                "general",
                true,
                "If true, water will not wash away redstone dust, repeaters, comparators, buttons, rails, and similar circuitry."
        );

        preventWaterGrassDecay = config.getBoolean(
                "PreventWaterGrassDecay",
                "general",
                true,
                "If true, grass blocks with water directly above them skip vanilla grass update ticks, so water does not turn them into dirt. Requires restart."
        );

        enableArmorOverlayModule = config.getBoolean(
                "EnableArmorOverlayModule",
                "armoroverlay",
                true,
                "Master switch for integrated Armor Overlay rendering."
        );

        armorOverlayLevels = config.getInt(
                "OverlayLevels",
                "armoroverlay",
                3,
                3,
                20,
                "Max armor icon tiers: 3, 5, 10, or 20."
        );
        if (armorOverlayLevels <= 3) {
            armorOverlayLevels = 3;
        } else if (armorOverlayLevels <= 5) {
            armorOverlayLevels = 5;
        } else if (armorOverlayLevels <= 10) {
            armorOverlayLevels = 10;
        } else {
            armorOverlayLevels = 20;
        }

        armorOverlayArmorPieces = config.getInt(
                "ArmorPieces",
                "armoroverlay",
                2,
                1,
                4,
                "Number of armor points to make 1 full armor icon. can be 1, 2 or 4"
        );
        if (armorOverlayArmorPieces == 3 || armorOverlayArmorPieces > 4) {
            armorOverlayArmorPieces = 4;
        }
        if (armorOverlayArmorPieces < 1) {
            armorOverlayArmorPieces = 1;
        }

        armorOverlayShowNumbers = config.getBoolean(
                "DisplayNumber",
                "armoroverlay",
                false,
                "If true, show raw armor values next to the overlay."
        );

        persistConfigIntValue("armoroverlay", "OverlayLevels", armorOverlayLevels);
        persistConfigIntValue("armoroverlay", "ArmorPieces", armorOverlayArmorPieces);

        divineRpgDisableHaliteExtraArmorPieceRender = config.getBoolean(
                "DisableHaliteExtraArmorPieceRender",
                "divinerpg",
                true,
                "If true, disables DivineRPG's extra Halite full-set half-armor HUD piece rendering."
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
                33,
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

        dualHotbarEnablePickBlockImplementation = config.getBoolean(
                "Enable Pick Block Implementation",
                "dualhotbar",
                true,
                "If true, RiftFlux replaces vanilla pick block behavior to support DualHotbar slot swapping. Disable this to leave pick block behavior vanilla."
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

        dualHotbarHeldItemTooltipAboveBars = config.getBoolean(
                "HeldItemTooltipAboveBars",
                "dualhotbar",
                true,
                "If true, the held-item tooltip is raised to stay above the armor bar and LegendGear mana bar when visible."
        );

        dualHotbarHeldItemTooltipPadding = config.getInt(
                "HeldItemTooltipPadding",
                "dualhotbar",
                2,
                0,
                32,
                "Extra pixels of vertical padding between the held-item tooltip and the top-most armor/mana bar."
        );

        dualHotbarShowMountedHealth = config.getBoolean(
                "ShowMountedHealth",
                "dualhotbar",
                true,
                "If false, hides the mounted creature health bar and keeps the normal HUD layout while riding."
        );

        dualHotbarUseCustomMountOnboardPrompt = config.getBoolean(
                "UseCustomMountOnboardPrompt",
                "dualhotbar",
                true,
                "If true, RiftFlux renders the mount dismount prompt itself so it can share the same font, fade timing, and positioning rules as the rest of the centered HUD overlays."
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
                "PickupNotifierMaxEntries", "general", 25, 1, 200,
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

        disableFalseCrashImprover = config.get("general", "DisableFalseCrashImprover", true,
                        "If true, prevents FalsePatternLib from appending the full latest FML log to crash reports.")
                .getBoolean(true);

        stackOverflowMaxDepth = config.get("general", "StackOverflowMaxDepth", 512,
                "Max recursion depth for World.getBlock before returning air.").getInt(512);

        protectItemsFromExplosions = config.get("general", "protectItemsFromExplosions", true,
                "Stops explosions from deleting items").getBoolean(true);

        enableDroppedItemRenderTweaks = config.get("client", "EnableDroppedItemRenderTweaks", true,
                        "If true, applies RiftFlux dropped-item render tweaks, including the nearest-item limiter and extra render-distance override.")
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

        allowPumpkinsOnAnyBlock = config.getBoolean(
                "AllowPumpkinsOnAnyBlock",
                "general",
                true,
                "If true, pumpkins, jack o'lanterns, and BlockPumpkin subclasses such as Haunted Pumpkins can be placed on any non-air block, including leaves. Requires restart."
        );

        allowSugarcaneOnAnyBlock = config.getBoolean(
                "AllowSugarcaneOnAnyBlock",
                "general",
                true,
                "If true, sugar cane can be placed on and stay on any non-air block, but it only grows upward when the root block has adjacent water like vanilla. Requires restart."
        );

        allowSugarcaneInWater = config.getBoolean(
                "AllowSugarcaneInWater",
                "general",
                true,
                "If true, sugar cane can be placed into water blocks and grow into water blocks. Requires restart."
        );

        sugarcaneGeneratesOnRiverFloors = config.getBoolean(
                "SugarcaneGeneratesOnRiverFloors",
                "general",
                true,
                "If true, natural sugar cane generation can start on vanilla-valid or compatible modded river/lake floor blocks underwater and grow up through the water surface. If false, natural sugar cane generation follows vanilla surface rules. Requires AllowSugarcaneInWater for underwater generation. Requires restart."
        );

        sugarcaneRiverFloorRarity = config.getInt(
                "SugarcaneRiverFloorRarity",
                "general",
                4,
                1,
                1000000,
                "1 in N chance for each underwater river/lake floor sugar cane generation attempt. 1 = every valid attempt, 4 = four times rarer than the original water generation. Requires SugarcaneGeneratesOnRiverFloors. Requires restart."
        );

        sugarcaneRiverFloorMinHeightAboveWater = config.getInt(
                "SugarcaneRiverFloorMinHeightAboveWater",
                "general",
                0,
                0,
                255,
                "Minimum number of generated sugar cane blocks above the top water block when SugarcaneGeneratesOnRiverFloors is enabled. Default is 0. Requires restart."
        );

        sugarcaneRiverFloorMaxHeightAboveWater = config.getInt(
                "SugarcaneRiverFloorMaxHeightAboveWater",
                "general",
                2,
                0,
                255,
                "Maximum number of generated sugar cane blocks above the top water block when SugarcaneGeneratesOnRiverFloors is enabled. Default is 2. Values below the minimum are clamped up at runtime. Requires restart."
        );

        sugarcaneMaxHeight = config.getInt(
                "SugarcaneMaxHeight",
                "general",
                3,
                1,
                255,
                "Maximum total height sugar cane can naturally grow to. Vanilla is 3. Requires restart."
        );

        sugarcaneMaxHeightAboveTopWaterBlock = config.getInt(
                "SugarcaneMaxHeightAboveTopWaterBlock",
                "general",
                3,
                0,
                255,
                "Maximum number of sugar cane blocks that can grow above the highest nearby water block in the cane column. 0 keeps growth at or below the water surface. Vanilla-like default is 3. Requires AllowSugarcaneInWater for water-column growth. Requires restart."
        );

        allowHangingSugarcane = config.getBoolean(
                "AllowHangingSugarcane",
                "general",
                true,
                "If true, sugar cane can hang from blocks above it. Requires restart."
        );

        hangingSugarcaneGrowsWithWaterAboveSupport = config.getBoolean(
                "HangingSugarcaneGrowsWithWaterAboveSupport",
                "general",
                true,
                "If true, hanging sugar cane can grow downward when water is one block above the block it is hanging from. Requires AllowHangingSugarcane. Requires restart."
        );

        sugarcaneGrowsWhenSupportHasBlockBelow = config.getBoolean(
                "SugarcaneGrowsWhenSupportHasBlockBelow",
                "general",
                true,
                "If true, standing sugar cane can grow upward when there is a non-air block underneath the block it is standing on, even without adjacent water. Requires restart."
        );

        directionalCrossedPlantRenderingByPlacement = config.getBoolean(
                "DirectionalCrossedPlantRenderingByPlacement",
                "client",
                true,
                "If true, crossed-plant rendering (flowers, tall grass, mushrooms, etc.) uses fixed world orientation based on player placement direction instead of the default crossed layout."
        );
        directionalCrossedPlantFacePlayerOnPlacement = config.getBoolean(
                "DirectionalCrossedPlantFacePlayerOnPlacement",
                "client",
                true,
                "If true, player-placed crossed plants store and use the player's facing direction at placement time. If false, directional crossed-plant rendering falls back to deterministic position-based orientation."
        );
        allowTorchesOnAnyBlock = config.getBoolean(
                "AllowTorchesOnAnyBlock",
                "general",
                true,
                "If true, vanilla torches and blocks extending BlockTorch can be placed on any non-air block, including leaves. Requires restart."
        );
        doubleSidedTorchRendering = config.getBoolean(
                "DoubleSidedTorchRendering",
                "client",
                true,
                "If true, torch-style renders keep their normal torch geometry but do not hide back faces when viewed from the opposite side."
        );
        billboardTorchRendering = config.getBoolean(
                "BillboardTorchRendering",
                "client",
                false,
                "If true, torch-style renders listed in BillboardTorchRenderingWhitelist or not using ModernTorchRendering render with RiftFlux's camera-facing billboard technique. This supersedes DoubleSidedTorchRendering for matching torch blocks."
        );
        billboardTorchRenderingBlacklist = config.getStringList(
                "BillboardTorchRenderingBlacklist",
                "client",
                new String[0],
                "Entries blocked from BillboardTorchRendering unless also listed in BillboardTorchRenderingWhitelist. Entries can be a mod id (example: divinerpg), a mod wildcard (example: divinerpg:*), a block id (example: divinerpg:edenTorch), a block path/name (example: starRail), or a block class name."
        );
        billboardTorchRenderingWhitelist = config.getStringList(
                "BillboardTorchRenderingWhitelist",
                "client",
                new String[0],
                "Entries forced to use BillboardTorchRendering even when ModernTorchRendering would otherwise apply."
        );
        modernTorchRendering = config.getBoolean(
                "ModernTorchRendering",
                "client",
                true,
                "If true, torch-render blocks render with RiftFlux's modern redstone-torch-inspired model unless blocked by ModernTorchRenderingBlacklist."
        );
        betterTorchTexture = config.getBoolean(
                "BetterTorchTexture",
                "client",
                true,
                "If true, vanilla torches use RiftFlux's custom torch texture.\n" +
                        "This is usually only used with ModernTorchRendering turned on as it will make it emulate the style of redstone torches"
        );
        modernTorchRenderingBlacklist = config.getStringList(
                "ModernTorchRenderingBlacklist",
                "client",
                new String[] {
                        "chisel",
                        "netherlicious",
                },
                "Entries blocked from ModernTorchRendering. Entries can be a mod id (example: divinerpg), a mod wildcard (example: divinerpg:*), a block id (example: divinerpg:edenTorch), a block path/name (example: starRail), or a block class name."
        );
        modernTorchRenderingWhitelist = config.getStringList(
                "ModernTorchRenderingWhitelist",
                "client",
                new String[0],
                "Entries forced to use ModernTorchRendering even if their mod is blocked by ModernTorchRenderingBlacklist. Use this for per-block exceptions such as divinerpg:edenTorch."
        );
        asyncWorldSelection = config.getBoolean(
                "AsyncWorldSelection",
                "client",
                true,
                "If true, the singleplayer world selection screen opens immediately and refreshes the save list on a daemon background loader."
        );
        saveWorldBeforeWindowClose = config.getBoolean(
                "SaveWorldBeforeWindowClose",
                "client",
                true,
                "If true, clicking the game window X waits for the integrated server to save and stop before the client exits."
        );
        preventBlockBreakingResetOnHeldItemChange = config.getBoolean(
                "PreventBlockBreakingResetOnHeldItemChange",
                "general",
                true,
                "If true, changing held item while mining the same block keeps the accumulated block-breaking progress on both the client and server. Requires restart."
        );
        movementSpeedFovFactorMax = config.getFloat(
                "MovementSpeedFovFactorMax",
                "client",
                1.15F,
                0.0F,
                10.0F,
                "Maximum movement-speed FOV multiplier before other FOV modifiers are reapplied. Set to 1.15 to cap speed FOV around vanilla sprinting, or 0 to disable the cap."
        );

        strictMobSpawnsZeroBlockLight = config.getBoolean(
                "StrictMobSpawnsZeroBlockLight",
                "general",
                false,
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

        teleportOwnedPetsFromUnloadedChunks = config.getBoolean(
                "teleportOwnedPetsFromUnloadedChunks",
                "general",
                true,
                "If true, tamed player-owned EntityTameable pets that are not sitting or leashed " +
                        "will snap to a safe spot near their owner before their old chunk unloads.\n" +
                        "This helps pets keep up after long-distance teleports in the same dimension."
        );

        teleportOwnedPetsMinimumDistance = config.getFloat(
                "teleportOwnedPetsMinimumDistance",
                "general",
                12.0F,
                0.0F,
                256.0F,
                "Minimum distance in blocks before unloaded-chunk pet recovery teleporting is allowed.\n" +
                        "Uses the same default 12 block threshold as vanilla follow-owner teleporting."
        );

        preventLeadsBreaking = config.getBoolean(
                "preventLeadsBreaking",
                "general",
                true,
                "If true, loads an early mixin that prevents EntityLiving leash maintenance from automatically breaking leads.\n" +
                        "Players can still remove leads through normal interaction."
        );

        preventLeashedMobFallDamage = config.getBoolean(
                "preventLeashedMobFallDamage",
                "general",
                true,
                "If true, leashed EntityLiving mobs ignore fall damage while attached to a live leash holder."
        );

        unsilenceCoveredNoteBlocks = config.getBoolean(
                "UnsilenceCoveredNoteBlocks",
                "general",
                true,
                "If true, note blocks can play even when a solid block is above them. Requires restart."
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

        netherliciousBigNetherTopY = config.getInt(
                "BigNetherTopY",
                "netherlicious",
                250,
                128,
                255,
                "Highest Y used by Netherlicious' Bigger Nether / double tall Nether option. 255 matches Netherlicious' default; 250 moves the Nether roof to Y=250."
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
                "Unicorn;32;16;0;0;riftflux:textures/painting/Unicorn.png",
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
                "DarkStorm;32;32;32;128;riftflux:textures/painting/imported/DarkStorm.png",
                "UnityKebab;16;16;0;0;riftflux:textures/painting/atlas_unity/UnityKebab.png",
                "UnityAztec;16;16;0;0;riftflux:textures/painting/atlas_unity/UnityAztec.png",
                "UnityAlban;16;16;0;0;riftflux:textures/painting/atlas_unity/UnityAlban.png",
                "UnityPool;32;16;0;0;riftflux:textures/painting/atlas_unity/UnityPool.png",
                "UnityCourbet;32;16;0;0;riftflux:textures/painting/atlas_unity/UnityCourbet.png",
                "UnitySea;32;16;0;0;riftflux:textures/painting/atlas_unity/UnitySea.png",
                "UnityWanderer;16;32;0;0;riftflux:textures/painting/atlas_unity/UnityWanderer.png",
                "UnityGraham;16;32;0;0;riftflux:textures/painting/atlas_unity/UnityGraham.png",
                "UnityFighters;64;32;0;0;riftflux:textures/painting/atlas_unity/UnityFighters.png",
                "UnityPointer;64;64;0;0;riftflux:textures/painting/atlas_unity/UnityPointer.png",
                "GalleryKebab;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryKebab.png",
                "GalleryAztec;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryAztec.png",
                "GalleryAlban;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryAlban.png",
                "GalleryAztec2;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryAztec2.png",
                "GalleryBomb;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryBomb.png",
                "GalleryPlant;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryPlant.png",
                "GalleryWasteland;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryWasteland.png",
                "GalleryPool;32;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryPool.png",
                "GalleryCourbet;32;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryCourbet.png",
                "GallerySea;32;16;0;0;riftflux:textures/painting/atlas_gallery/GallerySea.png",
                "GallerySunset;32;16;0;0;riftflux:textures/painting/atlas_gallery/GallerySunset.png",
                "GalleryCreebet;32;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryCreebet.png",
                "GalleryWanderer;16;32;0;0;riftflux:textures/painting/atlas_gallery/GalleryWanderer.png",
                "GalleryGraham;16;32;0;0;riftflux:textures/painting/atlas_gallery/GalleryGraham.png",
                "GalleryFighters;64;32;0;0;riftflux:textures/painting/atlas_gallery/GalleryFighters.png",
                "GalleryBust;32;32;0;0;riftflux:textures/painting/atlas_gallery/GalleryBust.png",
                "GalleryVoid;32;32;0;0;riftflux:textures/painting/atlas_gallery/GalleryVoid.png",
                "GalleryPointer;64;64;0;0;riftflux:textures/painting/atlas_gallery/GalleryPointer.png",
                "GalleryBurningSkull;64;64;0;0;riftflux:textures/painting/atlas_gallery/GalleryBurningSkull.png",
                "ArcaneKebab;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneKebab.png",
                "ArcaneAztec;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneAztec.png",
                "ArcaneAlban;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneAlban.png",
                "ArcaneAztec2;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneAztec2.png",
                "ArcaneBomb;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneBomb.png",
                "ArcanePlant;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcanePlant.png",
                "ArcaneWasteland;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneWasteland.png",
                "ArcanePool;32;16;0;0;riftflux:textures/painting/atlas_arcane/ArcanePool.png",
                "ArcaneCourbet;32;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneCourbet.png",
                "ArcaneSea;32;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneSea.png",
                "ArcaneSunset;32;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneSunset.png",
                "ArcaneCreebet;32;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneCreebet.png",
                "ArcaneWanderer;16;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneWanderer.png",
                "ArcaneGraham;16;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneGraham.png",
                "ArcaneFighters;64;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneFighters.png",
                "ArcaneMatch;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneMatch.png",
                "ArcaneBust;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneBust.png",
                "ArcaneStage;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneStage.png",
                "ArcaneVoid;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneVoid.png",
                "ArcaneSkullAndRoses;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneSkullAndRoses.png",
                "ArcaneWither;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneWither.png",
                "ArcanePointer;64;64;0;0;riftflux:textures/painting/atlas_arcane/ArcanePointer.png",
                "ArcanePigscene;64;64;0;0;riftflux:textures/painting/atlas_arcane/ArcanePigscene.png",
                "ArcaneBurningSkull;64;64;0;0;riftflux:textures/painting/atlas_arcane/ArcaneBurningSkull.png",
                "ArcaneSkeleton;64;48;0;0;riftflux:textures/painting/atlas_arcane/ArcaneSkeleton.png",
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
                false,
                "Enables Hanging Ladders, extend a ladder downward by right-clicking an existing ladder with another ladder also enables holding jump to climb ladders. [WIP]"
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

        enablePlaceableGunpowder = config.getBoolean(
                "EnablePlaceableGunpowder",
                "placeable_gunpowder",
                true,
                "Master switch for the integrated Placeable Gunpowder block and placement behavior."
        );

        enableGlowstoneDust = config.getBoolean(
                "EnableGlowstoneDust",
                "glowstone_dust",
                true,
                "Master switch for the integrated placeable Glowstone Dust block and placement behavior."
        );

        glowstoneDustLightLevel = config.getInt(
                "LightLevel",
                "glowstone_dust",
                8,
                0,
                15,
                "Light level emitted by placed Glowstone Dust. Vanilla torch is 14 and glowstone block is 15."
        );

        glowCarpetLightLevel = config.getInt(
                "GlowCarpetLightLevel",
                "vortex",
                6,
                0,
                15,
                "Light level emitted by Glow Carpet. Vanilla torch is 14 and glowstone block is 15."
        );

        randomizeGlowCarpetTextureOnPlacement = config.getBoolean(
                "RandomizeGlowCarpetTextureOnPlacement",
                "vortex",
                true,
                "If true, newly added Glow Carpet randomizes into one of the hidden glow_carpet block variants. If false, new placements stay glow_carpet."
        );

        randomizeGlowCarpetRotation = config.getBoolean(
                "RandomizeGlowCarpetRotation",
                "vortex",
                true,
                "If true, newly added Glow Carpet randomly chooses and stores one of four texture rotations."
        );

        voidFluxLightLevel = config.getInt(
                "VoidFluxLightLevel",
                "vortex",
                6,
                0,
                15,
                "Light level emitted by Void Flux. Vanilla torch is 14 and glowstone block is 15."
        );

        placeableGunpowderSetsFireBelow = config.getBoolean(
                "SetsFireBelow",
                "placeable_gunpowder",
                true,
                "If true, exploding placeable gunpowder can ignite flammable blocks directly below it."
        );

        placeableGunpowderEmitsRedstone = config.getBoolean(
                "EmitsRedstoneWhileLit",
                "placeable_gunpowder",
                true,
                "If true, ignited placeable gunpowder emits a weak redstone signal until it detonates."
        );

        placeableGunpowderIgnitesHbmBarrels = config.getBoolean(
                "IgniteHbmExplosiveBarrels",
                "placeable_gunpowder",
                true,
                "If true, ignited placeable gunpowder directly ignites adjacent HBM NTM explosive barrels, which normally only react to fire."
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

        butterflyKnifeStaminaBoost = config.getBoolean(
                "butterflyKnifeStaminaBoost",
                "dss",
                true,
                "If true and DSS is enabled, restore stamina when flicking shortly after a successful backstab."
        );

        butterflyKnifeStaminaBoostAmount = config.getFloat(
                "butterflyKnifeStaminaBoostAmount",
                "dss",
                2.0F,
                0.0F,
                Float.MAX_VALUE,
                "Amount of stamina (in DSS seconds) restored when flicking within the backstab window."
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
                        "Format per entry: potionNameOrId,level,durationSeconds.\n" +
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

        butterflyKnifeFlickParry = config.getBoolean(
                "butterflyKnifeFlickParry",
                "vortex",
                true,
                "If true, damage received during the configured opening window of a butterfly knife flick is parried."
        );

        butterflyKnifeFlickParryWindowTicks = config.getInt(
                "butterflyKnifeFlickParryWindowTicks",
                "vortex",
                4,
                1,
                12,
                "Number of ticks after right-clicking during which the butterfly knife can parry."
        );

        butterflyKnifeFlickCooldownTicks = config.getInt(
                "butterflyKnifeFlickCooldownTicks",
                "vortex",
                20,
                0,
                1200,
                "Cooldown between butterfly knife flicks, in ticks. 20 ticks equals 1 second; 0 disables the cooldown."
        );

        backpackStorage = config.getBoolean("backpackStorage", "vortex", true,
                "Allows the backpack to be unequipped while containing items.");

        backpackDurability = config.getBoolean("backpackDurability", "vortex", false,
                "If true, the backpack uses durability. The max durability is exactly backpackDurabilityAmount.\n" +
                        "If false, the backpack has no durability.");

        backpackDurabilityAmount = config.getInt("backpackDurabilityAmount", "vortex", 512, 1, 4096,
                "Exact max durability for the backpack when backpackDurability is true (e.g., 1234). Default: 512.");

        backpackArmorPoints = config.getInt("backpackArmorPoints", "vortex", 2, 0, 20,
                "Armor points granted by the backpack (0 = no armor).");

        poptartFoodValue = config.getInt(
                "poptartFoodValue",
                "vortex",
                6,
                0,
                20,
                "Hunger restored by the Poptart."
        );

        poptartSaturation = config.getFloat(
                "poptartSaturation",
                "vortex",
                0.6F,
                0.0F,
                20.0F,
                "Saturation modifier for the Poptart."
        );

        poptartLegendGearManaRestore = config.getFloat(
                "poptartLegendGearManaRestore",
                "vortex",
                4.0F,
                0.0F,
                20.0F,
                "LegendGear mana restored by the Poptart. 2.0 = one full mana icon (default 4.0 = two full icons)."
        );

        poptartDungeonLootWeight = config.getInt(
                "poptartDungeonLootWeight",
                "vortex",
                4,
                0,
                1000,
                "Dungeon chest weight for the Poptart. Set to 0 to disable dungeon loot."
        );


        vortexGlintRuneDungeonLoot = config.getBoolean(
                "glintRuneDungeonLoot",
                "vortex",
                true,
                "If true, all Glint Rune colors can appear in dungeon chests."
        );

        randomizeEnchantedGlintColors = config.getBoolean(
                "randomizeEnchantedGlintColors",
                "vortex",
                true,
                "If true, enchanted items without a custom glint rune color get a random Quark-style glint color when they are first enchanted or generated as loot."
        );

        GluttonyCharm = config.getBoolean("GluttonyCharm", "vortex", false,
                "Gives the gluttony charm an autofeeding functionality. Right-click to put in food items.");

        highlanderPotionEffectId = config.getInt(
                "HighlanderPotionEffectId",
                "vortex",
                222,
                0,
                65536,
                "Potion ID to use for the Highland Spirit head buff effect. If the ID is occupied, the next free ID is used."
        );

        DualHotbarConfig.syncFromModConfig();
        zelda.Config.syncFromModConfig();
        de.sanandrew.mods.claysoldiers.util.ModConfig.syncConfig();

        config.save();
    }

    private static void persistConfigIntValue(String category, String key, int value) {
        config.get(category, key, value).set(value);
    }

    private static int[] getIntRangeConfig(String category, String key, int defaultMin, int defaultMax,
            int minimumAllowed, int maximumAllowed, String comment) {
        Property property = config.get(category, key, new int[]{defaultMin, defaultMax}, comment);
        int[] range = sanitizeIntRange(property.getIntList(), defaultMin, defaultMax, minimumAllowed, maximumAllowed);
        property.set(range);
        return range;
    }

    private static int[] sanitizeIntRange(int[] values, int defaultMin, int defaultMax, int minimumAllowed,
            int maximumAllowed) {
        int min = defaultMin;
        int max = defaultMax;
        if (values != null && values.length > 0) {
            min = values[0];
            max = values.length > 1 ? values[1] : values[0];
        }
        min = clampInt(min, minimumAllowed, maximumAllowed);
        max = clampInt(max, minimumAllowed, maximumAllowed);
        if (max < min) {
            max = min;
        }
        return new int[]{min, max};
    }

    private static int clampInt(int value, int minimumAllowed, int maximumAllowed) {
        if (value < minimumAllowed) {
            return minimumAllowed;
        }
        if (value > maximumAllowed) {
            return maximumAllowed;
        }
        return value;
    }

    public static int getQuacklingFishingMinCatchDelayTicks() {
        return getRangeMin(ducklingQuacklingFishingCatchDelayTicks, 360);
    }

    public static int getQuacklingFishingMaxCatchDelayTicks() {
        return getRangeMax(ducklingQuacklingFishingCatchDelayTicks, 400);
    }

    public static int getQuacklingFishingMinCatchesBeforeStop() {
        return getRangeMin(ducklingQuacklingFishingCatchesBeforeStop, 8);
    }

    public static int getQuacklingFishingMaxCatchesBeforeStop() {
        return getRangeMax(ducklingQuacklingFishingCatchesBeforeStop, 16);
    }

    public static int getQuacklingFishingMinSessionsPerDay() {
        return getRangeMin(ducklingQuacklingFishingSessionsPerDay, 0);
    }

    public static int getQuacklingFishingMaxSessionsPerDay() {
        return getRangeMax(ducklingQuacklingFishingSessionsPerDay, 64);
    }

    public static boolean hasQuacklingTradesConfigured() {
        if (ducklingQuacklingTrades == null) {
            return false;
        }
        for (String trade : ducklingQuacklingTrades) {
            if (trade == null) {
                continue;
            }
            String trimmed = trade.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                return true;
            }
        }
        return false;
    }

    private static int getRangeMin(int[] range, int fallback) {
        return range != null && range.length > 0 ? range[0] : fallback;
    }

    private static int getRangeMax(int[] range, int fallback) {
        return range != null && range.length > 1 ? range[1] : getRangeMin(range, fallback);
    }

    private static String[] sanitizeMobSpawnRules(String[] values) {
        if (values == null || values.length == 0) {
            return new String[0];
        }

        List<String> sanitized = new ArrayList<String>();
        for (String raw : values) {
            if (raw == null) {
                continue;
            }
            String entry = raw.trim();
            if (entry.isEmpty()) {
                continue;
            }

            String[] parts = entry.split("\\|", -1);
            if (parts.length > 0) {
                parts[0] = sanitizeMobSpawnMobName(parts[0]);
            }
            if (parts.length >= 6) {
                parts[5] = sanitizeMobSpawnBiomeIdField(parts[5]);
            }

            StringBuilder rebuilt = new StringBuilder(entry.length() + 10);
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    rebuilt.append('|');
                }
                rebuilt.append(parts[i].trim());
            }
            entry = rebuilt.toString();
            sanitized.add(entry);
        }
        return sanitized.toArray(new String[sanitized.size()]);
    }

    private static String sanitizeMobSpawnMobName(String raw) {
        if (raw == null) {
            return "";
        }

        String token = raw.trim();
        if (token.isEmpty()) {
            return "";
        }

        String lowered = token.toLowerCase(Locale.ROOT);
        if (lowered.startsWith("riftflux.")) {
            return token;
        }

        String normalized = ConfigResolver.stripKnownEntityPrefixes(ConfigResolver.normalizeToken(token));
        String riftFluxId = RIFTFLUX_NATURAL_MOB_CONFIG_IDS.get(normalized);
        if (riftFluxId != null) {
            return riftFluxId;
        }
        return token;
    }

    private static String[] sanitizeBiomeNameIdList(String[] values) {
        if (values == null || values.length == 0) {
            return new String[0];
        }

        LinkedHashSet<String> tokens = new LinkedHashSet<String>();
        for (String raw : values) {
            if (raw == null) {
                continue;
            }

            String[] parts = raw.split("[,;]+");
            for (String part : parts) {
                if (part == null) {
                    continue;
                }
                String token = part.trim();
                if (!token.isEmpty()) {
                    tokens.add(token);
                }
            }
        }

        return tokens.toArray(new String[tokens.size()]);
    }

    private static String sanitizeMobSpawnBiomeIdField(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "";
        }

        LinkedHashSet<String> tokens = new LinkedHashSet<String>();
        String[] parts = raw.split("[,;]+");
        for (String part : parts) {
            if (part == null) {
                continue;
            }
            String token = part.trim();
            if (token.isEmpty()) {
                continue;
            }

            String lowered = token.toLowerCase(Locale.ROOT);
            if ("name:wheatfield".equals(lowered)) {
                tokens.add("wheatfield");
            } else if ("name:unnamed".equals(lowered) || "unnamed".equals(lowered)) {
                continue;
            } else {
                tokens.add(token);
            }
        }

        StringBuilder out = new StringBuilder(raw.length());
        for (String token : tokens) {
            if (out.length() > 0) {
                out.append(',');
            }
            out.append(token);
        }
        return out.toString();
    }

    private static String[] sanitizeAppaPassengerFilter(String[] values) {
        if (values == null || values.length == 0) {
            return new String[]{"EntityBison", "EntityNimatin", "EntitySootSprite"};
        }
        final String appaEntityClass = "EntityBison";
        final String nimatinEntityClass = "EntityNimatin";
        final String sootSpriteEntityClass = "EntitySootSprite";
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
            } else if ("EntityNimatin".equalsIgnoreCase(entry)) {
                entry = nimatinEntityClass;
            } else if ("EntitySootSprite".equalsIgnoreCase(entry)) {
                entry = sootSpriteEntityClass;
            }
            String key = entry.toLowerCase();
            if (lowered.add(key)) {
                unique.add(entry);
            }
        }
        if (unique.isEmpty() || lowered.add(appaEntityClass.toLowerCase())) {
            unique.add(appaEntityClass);
        }
        if (lowered.add(nimatinEntityClass.toLowerCase())) {
            unique.add(nimatinEntityClass);
        }
        if (lowered.add(sootSpriteEntityClass.toLowerCase())) {
            unique.add(sootSpriteEntityClass);
        }
        return unique.toArray(new String[unique.size()]);
    }

    private static String[] sanitizeNimatinPassengerFilter(String[] values) {
        if (values == null || values.length == 0) {
            return DEFAULT_PALARIA_NIMATIN_PASSENGER_BLACKLIST.clone();
        }
        final String nimatinEntityClass = "EntityNimatin";
        final String appaEntityClass = "EntityBison";
        final String sootSpriteEntityClass = "EntitySootSprite";
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
            if ("EntityNimatin".equalsIgnoreCase(entry)) {
                entry = nimatinEntityClass;
            } else if ("EntityBison".equalsIgnoreCase(entry)) {
                entry = appaEntityClass;
            } else if ("EntitySootSprite".equalsIgnoreCase(entry)) {
                entry = sootSpriteEntityClass;
            }
            String key = entry.toLowerCase();
            if (lowered.add(key)) {
                unique.add(entry);
            }
        }
        if (unique.isEmpty() || lowered.add(nimatinEntityClass.toLowerCase())) {
            unique.add(nimatinEntityClass);
        }
        if (lowered.add(appaEntityClass.toLowerCase())) {
            unique.add(appaEntityClass);
        }
        if (lowered.add(sootSpriteEntityClass.toLowerCase())) {
            unique.add(sootSpriteEntityClass);
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

    private static int parseToroHealthColor(String color, int fallback) {
        return parseRgbColor(color, fallback);
    }

    public static int parseRgbColor(String color, int fallback) {
        Integer parsed = parseRgbColorOrNull(color);
        return parsed == null ? fallback : parsed;
    }

    public static Integer parseRgbColorOrNull(String color) {
        if (color == null) {
            return null;
        }

        String value = color.trim();
        if (value.isEmpty()) {
            return null;
        }

        String hex = value;
        if (hex.charAt(0) == '#') {
            hex = hex.substring(1);
        } else if (hex.length() > 2 && (hex.startsWith("0x") || hex.startsWith("0X"))) {
            hex = hex.substring(2);
        }

        if (hex.matches("(?i)[0-9a-f]{6}")) {
            try {
                return Integer.parseInt(hex, 16);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        String key = value.toUpperCase(Locale.ROOT);
        if ("RED".equals(key)) return 0xFF0000;
        if ("GREEN".equals(key) || "LIME".equals(key)) return 0x00FF00;
        if ("BLUE".equals(key)) return 0x0000FF;
        if ("YELLOW".equals(key)) return 0xFFFF00;
        if ("ORANGE".equals(key)) return 0xFFA500;
        if ("BLACK".equals(key)) return 0x000000;
        if ("PURPLE".equals(key)) return 0x960096;
        if ("WHITE".equals(key)) return 0xFFFFFF;
        if ("GOLD".equals(key)) return 0xFFD700;
        if ("CYAN".equals(key)) return 0x00FFFF;
        if ("MAGENTA".equals(key)) return 0xFF00FF;
        if ("GRAY".equals(key)) return 0x808080;
        if ("LIGHT_GRAY".equals(key)) return 0xC0C0C0;
        return null;
    }

    public static String getNamedRgbColorDisplayName(String color) {
        if (color == null) {
            return null;
        }
        String key = color.trim().toUpperCase(Locale.ROOT);
        if ("RED".equals(key)) return "red";
        if ("GREEN".equals(key)) return "green";
        if ("LIME".equals(key)) return "lime";
        if ("BLUE".equals(key)) return "blue";
        if ("YELLOW".equals(key)) return "yellow";
        if ("ORANGE".equals(key)) return "orange";
        if ("BLACK".equals(key)) return "black";
        if ("PURPLE".equals(key)) return "purple";
        if ("WHITE".equals(key)) return "white";
        if ("GOLD".equals(key)) return "gold";
        if ("CYAN".equals(key)) return "cyan";
        if ("MAGENTA".equals(key)) return "magenta";
        if ("GRAY".equals(key)) return "gray";
        if ("LIGHT_GRAY".equals(key)) return "light gray";
        return null;
    }

    public static String describeRgbColorInput(String colorText, int color) {
        String named = getNamedRgbColorDisplayName(colorText);
        return named == null ? "#" + formatRgbColor(color) : named;
    }

    public static String formatRgbColor(int color) {
        String hex = Integer.toHexString(color & 0xFFFFFF).toUpperCase(Locale.ROOT);
        StringBuilder padded = new StringBuilder();
        for (int i = hex.length(); i < 6; i++) {
            padded.append('0');
        }
        padded.append(hex);
        return padded.toString();
    }

    public static boolean isChatBubblesConfigKey(String setting) {
        return normalizeChatBubblesConfigKey(setting) != null;
    }

    public static String normalizeChatBubblesConfigKey(String setting) {
        if (setting == null) {
            return null;
        }
        String key = setting.trim().toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "");
        if (key.isEmpty()) return null;
        if ("enabled".equals(key) || "enable".equals(key) || "module".equals(key)) return "enabled";
        if ("showown".equals(key) || "own".equals(key) || "ownmessages".equals(key) || "showownmessages".equals(key)) return "showown";
        if ("background".equals(key) || "showbackground".equals(key) || "bubblebackground".equals(key)) return "background";
        if ("photomode".equals(key) || "renderinphotomode".equals(key) || "photomoderender".equals(key) || "showinphotomode".equals(key) || "photobubbles".equals(key)) return "photomode";
        if ("color".equals(key) || "colour".equals(key) || "owncolor".equals(key) || "owncolour".equals(key) || "bubblecolor".equals(key) || "bubblecolour".equals(key)) return "color";
        if ("text".equals(key) || "textcolor".equals(key) || "textcolour".equals(key) || "owntextcolor".equals(key) || "owntextcolour".equals(key) || "chattextcolor".equals(key) || "chattextcolour".equals(key) || "bubbletextcolor".equals(key) || "bubbletextcolour".equals(key) || "whitetext".equals(key) || "textwhite".equals(key) || "textcolourwhite".equals(key) || "textcolorwhite".equals(key)) return "textcolor";
        if ("randomtextcolor".equals(key) || "randomtextcolour".equals(key) || "randomizetextcolor".equals(key) || "randomisetextcolour".equals(key) || "uuidtextcolor".equals(key) || "uuidtextcolour".equals(key)) return "randomtextcolor";
        if ("gap".equals(key) || "messagegap".equals(key) || "linegap".equals(key)) return "gap";
        if ("blackbar".equals(key) || "blackbackground".equals(key) || "blacktextbackground".equals(key) || "textbackground".equals(key)) return "blackbar";
        if ("blackbaropacity".equals(key) || "blackbackgroundopacity".equals(key) || "blacktextbackgroundopacity".equals(key) || "baropacity".equals(key) || "textbackgroundopacity".equals(key)) return "blackbaropacity";
        if ("size".equals(key) || "textsize".equals(key) || "scale".equals(key) || "textscale".equals(key) || "bubblesize".equals(key)) return "size";
        if ("lifetime".equals(key) || "messagelifetime".equals(key) || "duration".equals(key)) return "lifetime";
        if ("linelength".equals(key) || "maxlength".equals(key) || "maxlinelength".equals(key) || "wrap".equals(key) || "wraplength".equals(key)) return "linelength";
        return null;
    }

    public static String validateChatBubblesConfigValue(String setting, String rawValue) {
        return applyChatBubblesConfigValue(setting, rawValue, false, false);
    }

    public static String applyChatBubblesConfigValue(String setting, String rawValue, boolean save) {
        return applyChatBubblesConfigValue(setting, rawValue, save, true);
    }

    private static String applyChatBubblesConfigValue(String setting, String rawValue, boolean save, boolean updateMemory) {
        String key = normalizeChatBubblesConfigKey(setting);
        if (key == null) {
            throw new IllegalArgumentException("Unknown chat bubbles config: " + setting);
        }

        if ("enabled".equals(key)) {
            boolean value = parseChatBubblesBoolean(rawValue);
            if (updateMemory) {
                enableChatBubblesModule = value;
                if (save) saveChatBubblesBoolean("EnableChatBubblesModule", true, value, "If true, renders chat bubbles above players when their chat messages can be parsed.");
            }
            return "enabled = " + enabledDisabled(value);
        }
        if ("showown".equals(key)) {
            boolean value = parseChatBubblesBoolean(rawValue);
            if (updateMemory) {
                chatBubblesShowOwnMessages = value;
                if (save) saveChatBubblesBoolean("ChatBubblesShowOwnMessages", true, value, "If true, your own parsed chat messages can render above your own head in third-person, including in singleplayer.");
            }
            return "show own messages = " + enabledDisabled(value);
        }
        if ("background".equals(key)) {
            boolean value = parseChatBubblesBoolean(rawValue);
            if (updateMemory) {
                chatBubblesShowBackground = value;
                if (save) saveChatBubblesBoolean("ChatBubblesShowBackground", true, value, "If true, chat bubbles render their speech-bubble background and tail behind the text.");
            }
            return "bubble background = " + enabledDisabled(value);
        }
        if ("photomode".equals(key)) {
            boolean value = parseChatBubblesBoolean(rawValue);
            if (updateMemory) {
                chatBubblesRenderInPhotoMode = value;
                if (save) saveChatBubblesBoolean("ChatBubblesRenderInPhotoMode", true, value, "If true, chat bubbles are rendered while isometric photo mode is active.");
            }
            return "photo mode bubbles = " + enabledDisabled(value);
        }
        if ("color".equals(key)) {
            String value = rawValue == null ? "" : rawValue.trim();
            if (isAutoChatBubbleColorValue(value)) {
                if (updateMemory) {
                    chatBubblesUseCustomOwnBubbleColor = false;
                    chatBubblesOwnBubbleColor = 0xFFD700;
                    if (save) saveChatBubblesString("ChatBubblesOwnBubbleColor", "", "AUTO", "Optional custom color for your own player's chat bubble. Leave blank or set to AUTO to use local gold for yourself and UUID-based colors for others. Accepts #RRGGBB, 0xRRGGBB, or named colors; custom values are advertised to the server so other RiftFlux clients can see them.");
                }
                return "own color = automatic";
            }
            Integer color = parseRgbColorOrNull(value);
            if (color == null) {
                throw new IllegalArgumentException("Unknown chat bubble color: " + value);
            }
            if (updateMemory) {
                chatBubblesUseCustomOwnBubbleColor = true;
                chatBubblesOwnBubbleColor = color.intValue() & 0xFFFFFF;
                if (save) saveChatBubblesString("ChatBubblesOwnBubbleColor", "", value, "Optional custom color for your own player's chat bubble. Leave blank or set to AUTO to use local gold for yourself and UUID-based colors for others. Accepts #RRGGBB, 0xRRGGBB, or named colors; custom values are advertised to the server so other RiftFlux clients can see them.");
            }
            return "own color = " + describeRgbColorInput(value, color.intValue());
        }
        if ("textcolor".equals(key)) {
            String value = rawValue == null ? "" : rawValue.trim();
            if (isAutoChatBubbleColorValue(value)) {
                if (updateMemory) {
                    chatBubblesUseCustomOwnTextColor = false;
                    chatBubblesOwnTextColor = 0xFFFFFF;
                    if (save) saveChatBubblesString("ChatBubblesOwnTextColor", "AUTO", "AUTO", "Optional custom color for your own player's chat bubble text. Leave blank or set to AUTO for the default white text, or UUID-random text if ChatBubblesRandomizeTextColorByUuid is enabled. Accepts #RRGGBB, 0xRRGGBB, or named colors; custom values are advertised to the server so other RiftFlux clients can see them.");
                }
                return "own text color = automatic";
            }
            Integer color = parseRgbColorOrNull(value);
            if (color == null) {
                throw new IllegalArgumentException("Unknown chat bubble text color: " + value);
            }
            if (updateMemory) {
                chatBubblesUseCustomOwnTextColor = true;
                chatBubblesOwnTextColor = color.intValue() & 0xFFFFFF;
                if (save) saveChatBubblesString("ChatBubblesOwnTextColor", "AUTO", value, "Optional custom color for your own player's chat bubble text. Leave blank or set to AUTO for the default white text, or UUID-random text if ChatBubblesRandomizeTextColorByUuid is enabled. Accepts #RRGGBB, 0xRRGGBB, or named colors; custom values are advertised to the server so other RiftFlux clients can see them.");
            }
            return "own text color = " + describeRgbColorInput(value, color.intValue());
        }
        if ("randomtextcolor".equals(key)) {
            boolean value = parseChatBubblesBoolean(rawValue);
            if (updateMemory) {
                chatBubblesRandomizeTextColorByUuid = value;
                if (save) saveChatBubblesBoolean("ChatBubblesRandomizeTextColorByUuid", false, value, "If true, chat bubble text uses a stable UUID-based random color for players who have not set their own synced text color.");
            }
            return "random UUID text color = " + enabledDisabled(value);
        }
        if ("gap".equals(key)) {
            int value = parseChatBubblesInt(rawValue, CHAT_BUBBLES_MESSAGE_GAP_MIN, CHAT_BUBBLES_MESSAGE_GAP_MAX, "message gap");
            if (updateMemory) {
                chatBubblesMessageGap = value;
                if (save) saveChatBubblesInt("ChatBubblesMessageGap", CHAT_BUBBLES_MESSAGE_GAP_DEFAULT, value, CHAT_BUBBLES_MESSAGE_GAP_COMMENT);
            }
            return "message gap = " + value;
        }
        if ("blackbar".equals(key)) {
            boolean value = parseChatBubblesBoolean(rawValue);
            if (updateMemory) {
                chatBubblesBlackTextBackground = value;
                if (save) saveChatBubblesBoolean("ChatBubblesBlackTextBackground", false, value, "If true, draws a solid black bar behind each chat bubble text line.");
            }
            return "black text bar = " + enabledDisabled(value);
        }
        if ("blackbaropacity".equals(key)) {
            float value = parseChatBubblesFloat(rawValue, CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_MIN, CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_MAX, "black text bar opacity");
            if (updateMemory) {
                chatBubblesBlackTextBackgroundOpacity = value;
                if (save) saveChatBubblesFloat("ChatBubblesBlackTextBackgroundOpacity", CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_DEFAULT, value, CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_COMMENT);
            }
            return "black text bar opacity = " + value;
        }
        if ("size".equals(key)) {
            float value = parseChatBubblesFloat(rawValue, CHAT_BUBBLES_TEXT_SCALE_MIN, CHAT_BUBBLES_TEXT_SCALE_MAX, "text size");
            if (updateMemory) {
                chatBubblesTextScale = value;
                if (save) saveChatBubblesFloat("ChatBubblesTextScale", CHAT_BUBBLES_TEXT_SCALE_DEFAULT, value, CHAT_BUBBLES_TEXT_SCALE_COMMENT);
            }
            return "text size = " + value;
        }
        if ("lifetime".equals(key)) {
            int value = parseChatBubblesInt(rawValue, 1, 120, "message lifetime");
            if (updateMemory) {
                chatBubblesMessageLifetimeSeconds = value;
                if (save) saveChatBubblesInt("ChatBubblesMessageLifetimeSeconds", 12, value, "How long chat bubbles remain visible, in seconds.");
            }
            return "message lifetime = " + value + " seconds";
        }
        if ("linelength".equals(key)) {
            int value = parseChatBubblesInt(rawValue, 8, 120, "max line length");
            if (updateMemory) {
                chatBubblesMaxLineLength = value;
                if (save) saveChatBubblesInt("ChatBubblesMaxLineLength", 30, value, "Maximum approximate characters per rendered chat bubble line before wrapping.");
            }
            return "max line length = " + value;
        }

        throw new IllegalArgumentException("Unknown chat bubbles config: " + setting);
    }

    public static float clampChatBubblesTextScale(float scale) {
        if (Float.isNaN(scale) || Float.isInfinite(scale)) {
            return CHAT_BUBBLES_TEXT_SCALE_DEFAULT;
        }
        if (scale < CHAT_BUBBLES_TEXT_SCALE_MIN) {
            return CHAT_BUBBLES_TEXT_SCALE_MIN;
        }
        if (scale > CHAT_BUBBLES_TEXT_SCALE_MAX) {
            return CHAT_BUBBLES_TEXT_SCALE_MAX;
        }
        return scale;
    }

    public static float clampChatBubblesBlackTextBackgroundOpacity(float opacity) {
        if (Float.isNaN(opacity) || Float.isInfinite(opacity)) {
            return CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_DEFAULT;
        }
        if (opacity < CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_MIN) {
            return CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_MIN;
        }
        if (opacity > CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_MAX) {
            return CHAT_BUBBLES_BLACK_TEXT_BACKGROUND_OPACITY_MAX;
        }
        return opacity;
    }

    public static void saveChatBubblesTextScale(float scale) {
        applyChatBubblesConfigValue("size", Float.toString(clampChatBubblesTextScale(scale)), true);
    }

    private static boolean isAutoChatBubbleColorValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return "auto".equals(normalized) || "default".equals(normalized) || "clear".equals(normalized) || "random".equals(normalized);
    }

    private static boolean parseChatBubblesBoolean(String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Expected true or false.");
        }
        String value = rawValue.trim().toLowerCase(Locale.ROOT);
        if ("true".equals(value) || "on".equals(value) || "yes".equals(value) || "1".equals(value) || "enabled".equals(value) || "enable".equals(value)) {
            return true;
        }
        if ("false".equals(value) || "off".equals(value) || "no".equals(value) || "0".equals(value) || "disabled".equals(value) || "disable".equals(value)) {
            return false;
        }
        throw new IllegalArgumentException("Expected true or false, got: " + rawValue);
    }

    private static int parseChatBubblesInt(String rawValue, int min, int max, String label) {
        if (rawValue == null) {
            throw new IllegalArgumentException(label + " must be from " + min + " to " + max + ".");
        }
        try {
            int value = Integer.parseInt(rawValue.trim());
            if (value < min || value > max) {
                throw new IllegalArgumentException(label + " must be from " + min + " to " + max + ".");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a whole number from " + min + " to " + max + ".");
        }
    }

    private static float parseChatBubblesFloat(String rawValue, float min, float max, String label) {
        if (rawValue == null) {
            throw new IllegalArgumentException(label + " must be from " + min + " to " + max + ".");
        }
        try {
            float value = Float.parseFloat(rawValue.trim());
            if (Float.isNaN(value) || Float.isInfinite(value) || value < min || value > max) {
                throw new IllegalArgumentException(label + " must be from " + min + " to " + max + ".");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a number from " + min + " to " + max + ".");
        }
    }

    private static String enabledDisabled(boolean value) {
        return value ? "enabled" : "disabled";
    }

    private static void saveChatBubblesBoolean(String key, boolean defaultValue, boolean value, String comment) {
        if (config == null) {
            return;
        }
        config.get("chatbubbles", key, defaultValue, comment).set(Boolean.toString(value));
        config.save();
    }

    private static void saveChatBubblesInt(String key, int defaultValue, int value, String comment) {
        if (config == null) {
            return;
        }
        config.get("chatbubbles", key, defaultValue, comment).set(Integer.toString(value));
        config.save();
    }

    private static void saveChatBubblesFloat(String key, float defaultValue, float value, String comment) {
        if (config == null) {
            return;
        }
        config.get("chatbubbles", key, defaultValue, comment).set(Float.toString(value));
        config.save();
    }

    private static void saveChatBubblesString(String key, String defaultValue, String value, String comment) {
        if (config == null) {
            return;
        }
        config.get("chatbubbles", key, defaultValue, comment).set(value);
        config.save();
    }

    // parse the string of IDs into a set
    private static void parseDisabledPotionIds(String raw) {
        disabledPotionIdsSet.clear();
        disabledPotionIdsSet.addAll(ConfigResolver.parseIntegerSet(raw));
    }

    // helper used by the mixin
    public static boolean isPotionIdDisabled(int id) {
        return disabledPotionIdsSet.contains(id);
    }

    public static boolean isBlessingEnabled(String blessing) {
        if (blessing == null) {
            return false;
        }
        return !disabledBlessingsSet.contains(blessing.trim().toLowerCase(Locale.ROOT));
    }

    private static void parseDisabledBlessings(String[] list) {
        disabledBlessingsSet.clear();
        if (list == null) {
            return;
        }
        for (String entry : list) {
            if (entry == null) {
                continue;
            }
            String trimmed = entry.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            disabledBlessingsSet.add(trimmed.toLowerCase(Locale.ROOT));
        }
    }

    private static int[] parsePotionIdList(String[] entries) {
        return ConfigResolver.parseIntegerList(entries);
    }

    private static int[] getIntRange(String key, String category, String defaultValue, int minAllowed, int maxAllowed, String comment) {
        String raw = config.get(category, key, defaultValue, comment).getString();
        int[] parsed = parseIntRange(raw, defaultValue, minAllowed, maxAllowed);
        if (parsed[0] > parsed[1]) {
            int swap = parsed[0];
            parsed[0] = parsed[1];
            parsed[1] = swap;
        }
        return parsed;
    }

    private static int[] parseIntRange(String raw, String defaultValue, int minAllowed, int maxAllowed) {
        String value = raw == null ? defaultValue : raw.trim();
        String[] parts = value.split("\\s*-\\s*", 2);
        if (parts.length != 2) {
            parts = defaultValue.split("\\s*-\\s*", 2);
        }
        try {
            int min = clampInt(Integer.parseInt(parts[0].trim()), minAllowed, maxAllowed);
            int max = clampInt(Integer.parseInt(parts[1].trim()), minAllowed, maxAllowed);
            return new int[]{min, max};
        } catch (NumberFormatException e) {
            String[] defaults = defaultValue.split("\\s*-\\s*", 2);
            return new int[]{
                    clampInt(Integer.parseInt(defaults[0].trim()), minAllowed, maxAllowed),
                    clampInt(Integer.parseInt(defaults[1].trim()), minAllowed, maxAllowed)
            };
        }
    }

    private static int getLegendGearPotionId(String key, int defaultId, String comment) {
        return config.getInt(key, "legendgear", defaultId, 0, 255, comment);
    }

    private static boolean getChromatiCraftNetherStructureEnabled(String structureName, boolean defaultValue) {
        return config.getBoolean(
                "NetherStructure" + structureName + "Enabled",
                "chromaticraft",
                defaultValue,
                "If false, ChromatiCraft will not generate the " + structureName + " Nether structure. Requires restart."
        );
    }

    private static int getChromatiCraftNetherStructureY(String structureName, int defaultValue) {
        return config.getInt(
                "NetherStructure" + structureName + "Y",
                "chromaticraft",
                defaultValue,
                1,
                255,
                "Y level used when ChromatiCraft generates the " + structureName + " Nether structure. Requires restart."
        );
    }

    public static boolean isChromatiCraftNetherStructureEnabled(String structureName) {
        String key = normalizeConfigName(structureName);
        if ("hut".equals(key)) return chromatiCraftNetherStructureHutEnabled;
        if ("temple".equals(key)) return chromatiCraftNetherStructureTempleEnabled;
        if ("maze".equals(key)) return chromatiCraftNetherStructureMazeEnabled;
        if ("spiral".equals(key)) return chromatiCraftNetherStructureSpiralEnabled;
        if ("diorama".equals(key)) return chromatiCraftNetherStructureDioramaEnabled;
        return true;
    }

    public static int getChromatiCraftNetherStructureYLevel(String structureName) {
        String key = normalizeConfigName(structureName);
        if ("hut".equals(key)) return chromatiCraftNetherStructureHutY;
        if ("temple".equals(key)) return chromatiCraftNetherStructureTempleY;
        if ("maze".equals(key)) return chromatiCraftNetherStructureMazeY;
        if ("spiral".equals(key)) return chromatiCraftNetherStructureSpiralY;
        if ("diorama".equals(key)) return chromatiCraftNetherStructureDioramaY;
        return 128;
    }

    public static boolean isLegendGearMysticShrubDimensionAllowed(int dimensionId) {
        return containsInt(legendGearLegacyMysticShrubDimensionWhitelist, dimensionId);
    }

    public static boolean isLegendGearBombFlowerDimensionAllowed(int dimensionId) {
        return containsInt(legendGearLegacyBombFlowerDimensionWhitelist, dimensionId);
    }

    public static boolean isPostProcessingDimensionAllowed(int dimensionId) {
        if (containsInt(postProcessDimensionBlacklist, dimensionId)) {
            return false;
        }
        return postProcessDimensionWhitelist == null
                || postProcessDimensionWhitelist.length == 0
                || containsInt(postProcessDimensionWhitelist, dimensionId);
    }

    public static boolean isPostProcessCelestialBloomDimensionAllowed(int dimensionId) {
        if (containsInt(postProcessCelestialBloomDimensionBlacklist, dimensionId)) {
            return false;
        }
        return postProcessCelestialBloomDimensionWhitelist == null
                || postProcessCelestialBloomDimensionWhitelist.length == 0
                || containsInt(postProcessCelestialBloomDimensionWhitelist, dimensionId);
    }

    public static boolean isCelestialFogHorizonDimensionAllowed(int dimensionId) {
        if (containsInt(celestialFogHorizonDimensionBlacklist, dimensionId)) {
            return false;
        }
        return celestialFogHorizonDimensionWhitelist == null
                || celestialFogHorizonDimensionWhitelist.length == 0
                || containsInt(celestialFogHorizonDimensionWhitelist, dimensionId);
    }

    public static boolean isCelestialFogChanceEventDimensionAllowed(int dimensionId) {
        if (containsInt(celestialFogChanceEventDimensionBlacklist, dimensionId)) {
            return false;
        }
        return celestialFogChanceEventDimensionWhitelist == null
                || celestialFogChanceEventDimensionWhitelist.length == 0
                || containsInt(celestialFogChanceEventDimensionWhitelist, dimensionId);
    }

    private static boolean containsInt(int[] values, int needle) {
        if (values == null) {
            return false;
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] == needle) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeConfigName(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replace("_", "").replace("-", "").replace(" ", "");
    }

    public static boolean isPostProcessingActive() {
        return enablePostProcessing && (
                postProcessSaturationPercent != 0.0F
                        || postProcessGamma != 0.5F
                        || postProcessBrightness != 0.0F
                        || postProcessContrast != 0.0F
                        || postProcessExposure != 0.0F
                        || postProcessRedMultiplier != 1.0F
                        || postProcessGreenMultiplier != 1.0F
                        || postProcessBlueMultiplier != 1.0F
                        || postProcessBloomStrengthPercent > 0.0F
        );
    }

}
