package com.voidsrift.riftflux;

import com.voidsrift.riftflux.util.ConfigResolver;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.io.File;
import makamys.mclib.config.item.BackpackConfigHelper;
import com.voidsrift.riftflux.dualhotbar.DualHotbarConfig;

public class ModConfig {

    public static Configuration config;
    public static final int DATAWATCHER_MAX_ID = 65536;
    public static final int PLAYER_DATAWATCHER_MIN_ID = 20;
    public static final int ENTITY_DATAWATCHER_MIN_ID = 16;
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
    public static boolean disableDragonAPILogging;
    public static boolean optimizeDragonAPIBlockRenderFastPaths;
    public static boolean optimizeDragonAPIEntityRenderLoopFastPaths;
    public static boolean optimizeDragonAPIParticleRenderFastPaths;
    public static boolean optimizeChromatiCraftRenderEventFastPaths;
    public static boolean optimizeChromatiCraftCliffsChunkGeneration;
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
    public static boolean enableToroHealthModule;
    public static boolean toroHealthShowDamageParticles;
    public static boolean toroHealthShowThroughWalls;
    public static float toroHealthParticleSize;
    public static int toroHealthHealColor;
    public static int toroHealthDamageColor;

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
    public static boolean enableFenceTextureModule;
    public static boolean disableFencePumpkinConnections;
    public static boolean enableJackOLanternHelmet;
    public static boolean disablePumpkinOverlay;
    public static boolean disableUnderwaterOverlay;
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
    public static boolean celestialBlackNightFog;
    public static int celestialVoidFogStartHeight;
    public static int celestialVoidParticleStartHeight;
    public static boolean betaStarsEnabled;
    public static int betaStarsCount;
    public static float betaStarsSizeMultiplier;
    public static boolean betaStarsRandomBlink;
    public static float betaStarsTwinkleSpeedMultiplier;
    public static int betaStarsSunsetFadeStartTick;
    public static int betaStarsSunsetFadeEndTick;
    public static boolean betaStarsRenderBehindSunMoon;
    public static boolean betaStarsDisableBetterSkiesStars;

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
    public static boolean levelUpEnableSneakAttackDoubleDamage;

    // Blessings
    public static boolean blessingsEnabled;
    public static boolean blessingsGrantOnFirstJoin;
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

    // Avatar 
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
    public static boolean enableDemonEyeSpawning;
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
    public static int iceRodDurability;
    public static float iceRodBlockLifetimeSeconds;
    public static float iceRodSpawnDistance;
    public static boolean iceRodPlacementPreviewEnabled;
    public static boolean iceRodUseLegendGearMana;
    public static float iceRodLegendGearManaCost;
    public static boolean magicIceRequireSilkTouch;
    public static int demonEyeSpawnWeight;
    public static boolean whoopieCushionKnockbackEnabled;
    public static float whoopieCushionKnockbackRadius;
    public static float whoopieCushionKnockbackStrength;
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
    public static int wheatfieldBarleyPerChunk;
    public static int wheatfieldBarleyFistDropChancePercent;
    public static boolean wheatfieldBarleyOnlyDropsWhenSheared;
    public static boolean wheatfieldRestrictHostileSpawns;
    public static String[] wheatfieldAllowedHostileMobIds;

    // Witches and More ports
    public static boolean enableWitchesAndMoreModule;
    public static boolean enableCyclopsMob;
    public static int cyclopsSpawnWeight;
    public static int cyclopsMaxHealth;
    public static boolean cyclopsUseBiomeWhitelist;
    public static String[] cyclopsBiomeList;
    public static boolean enableFlowerManMob;
    public static int flowerManSpawnWeight;
    public static int flowerManMaxHealth;
    public static boolean flowerManUseBiomeWhitelist;
    public static String[] flowerManBiomeList;
    public static boolean enableEnderTrollMob;
    public static int enderTrollSpawnWeight;
    public static int enderTrollMaxHealth;
    public static boolean enderTrollUseBiomeWhitelist;
    public static String[] enderTrollBiomeList;
    public static boolean enableJaxxMob;
    public static int jaxxSpawnWeight;
    public static int jaxxMaxHealth;
    public static boolean jaxxUseBiomeWhitelist;
    public static String[] jaxxBiomeList;
    public static boolean enableBlackWidowMob;
    public static int blackWidowSpawnWeight;
    public static int blackWidowMaxHealth;
    public static boolean blackWidowUseBiomeWhitelist;
    public static String[] blackWidowBiomeList;
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

    // Axolotl module
    public static boolean enableAxolotlModule;
    public static boolean enableAxolotlNaturalSpawning;
    public static int axolotlSpawnWeight;
    public static float axolotlMaxHealth;

    // LegendGear module
    public static boolean enableLegendGearModule;
    public static int legendGearMagicProtectionId;
    public static int legendGearSpellReachId;
    public static int legendGearSpellSpreadId;
    public static int legendGearSpellArmoredId;
    public static boolean legendGearAllowEmeraldDrops;
    public static boolean legendGearAllowHeartDrops;
    public static float legendGearItemSoundVolume;
    public static boolean legendGearFancyExperience;
    public static int legendGearManaDatawatcherId;
    public static int legendGearGlideDatawatcherId;
    public static int legendGearMaxStarwellRetries;
    public static boolean legendGearFallingStarDamageEnabled;
    public static float legendGearFallingStarDamage;
    public static boolean legendGearMagicBoomerangInfiniteDurability;
    public static float legendGearMagicBoomerangDamage;
    public static boolean legendGearMagicBoomerangBreakPlants;
    public static boolean legendGearEnableBadBow;
    public static int legendGearDashRingMaxAirJumps;
    public static boolean legendGearDashRingUseOriginalBehavior;
    public static boolean legendGearSprinkleStardustRequireSneak;
    public static boolean legendGearSpottingScopeConsumesMana;
    public static int legendGearTwinkleStaffDurability;
    public static int legendGearFireStaffDurability;
    public static int legendGearZapStaffDurability;
    public static int legendGearIceStaffDurability;
    public static int legendGearStarPieceInfuseLevels;
    public static boolean legendGearPlaceableStarPieces;
    public static float legendGearEmberStaffFireSeconds;
    public static int legendGearManaRegenPotionId;
    public static int legendGearGroundedPotionId;
    public static float legendGearManaRegenPotionManaPerSecond;
    public static int legendGearStoneskinResistancePotionId;
    public static boolean legendGearCaltropsBreakOnTrigger;
    public static boolean legendGearCaltropsTriggerDropEnabled;
    public static float legendGearCaltropsMobDamageHearts;
    public static float legendGearCaltropsPlayerDamagePercent;
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
    public static int healAltarSoulHeartsDatawatcherId;
    public static int appaFamiliarNameDatawatcherId;
    public static int appaBisonSeatParentDatawatcherId;
    public static int appaBisonSeatIndexDatawatcherId;
    public static int terrariaDemonEyeFlagsDatawatcherId;
    public static int axolotlStateDatawatcherId;

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
    public static int heartLanternAuraDurationSeconds;
    public static float heartLanternAuraRadius;
    public static String[] heartLanternAuraEffects;
    public static boolean starLanternAuraEnabled;
    public static int starLanternAuraDurationSeconds;
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

    public static boolean strictMobSpawnsZeroBlockLight;

    public static boolean wrongUseSingleDurability;

    public static boolean invincibleOwnedMobs;

    public static boolean invincibleOwnedAllMobs;

    public static boolean invincibleRideableEntities;

    public static boolean teleportOwnedPetsFromUnloadedChunks;
    public static float teleportOwnedPetsMinimumDistance;

    public static boolean disableTintedSugarcane;

    public static boolean enableNetherrackTweak;
    public static int netherliciousBigNetherTopY;
    public static boolean enableDoorAirPlacement;
    public static boolean protectCircuitryFromWater;
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

        disableUnderwaterOverlay = config.getBoolean(
                "DisableUnderwaterOverlay",
                "client",
                true,
                "If true, hides the underwater distortion overlay while submerged."
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
        String chatBubblesOwnTextColorDefault = "AUTO";
        if (!config.hasKey("chatbubbles", "ChatBubblesOwnTextColor")
                && config.hasKey("chatbubbles", "ChatBubblesWhiteText")
                && !config.get("chatbubbles", "ChatBubblesWhiteText", true).getBoolean(true)) {
            chatBubblesOwnTextColorDefault = "black";
        }
        String chatBubblesOwnTextColorRaw = config.getString(
                "ChatBubblesOwnTextColor",
                "chatbubbles",
                chatBubblesOwnTextColorDefault,
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
        boolean legacyRemoveMessageGap = config.hasKey("chatbubbles", "ChatBubblesRemoveMessageGap")
                && config.get("chatbubbles", "ChatBubblesRemoveMessageGap", false).getBoolean(false);
        chatBubblesMessageGap = config.getInt(
                "ChatBubblesMessageGap",
                "chatbubbles",
                legacyRemoveMessageGap ? 0 : CHAT_BUBBLES_MESSAGE_GAP_DEFAULT,
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

        float worldTooltipsTextScaleDefault = 1.0F;
        if (!config.hasKey("client", "WorldTooltipsTextScale") && config.hasKey("client", "WorldTooltipsTextSize")) {
            int legacyTextSize = config.getInt(
                    "WorldTooltipsTextSize",
                    "client",
                    8,
                    2,
                    32,
                    "Legacy key for WorldTooltipsTextScale."
            );
            worldTooltipsTextScaleDefault = Math.max(0.25F, Math.min(4.0F, legacyTextSize / 8.0F));
        }

        worldTooltipsTextScale = config.getFloat(
                "WorldTooltipsTextScale",
                "client",
                worldTooltipsTextScaleDefault,
                0.25F,
                4.0F,
                "Scale multiplier for world tooltip text and its background. 1.0 is the default size."
        );
        removeConfigProperty("client", "WorldTooltipsTextSize");

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

        migrateClientConfigKeys();
        migrateCelestialConfigCategory();

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
                "If true, overworld fog keeps a desaturated beta-style grey and only tints toward non-default biome sky colors. Takes precedence over CelestialFogMatchesSky. [WIP]"
        );
        celestialBlackNightFog = config.getBoolean(
                "CelestialBlackNightFog",
                "celestial",
                true,
                "If true, night-time fog is forced fully black after sunrise and sunset tint have finished. Ignored while CelestialFogMatchesSky or CelestialBetaStyleFogBiomeTint is enabled."
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

        betaStarsRenderBehindSunMoon = config.getBoolean(
                "BetaStarsRenderBehindSunMoon",
                "celestial",
                true,
                "If true, renders RiftFlux beta stars behind the vanilla/custom sun and moon where the sky pipeline allows it. If false, fallback star rendering can draw after sun/moon for compatibility."
        );

        betaStarsDisableBetterSkiesStars = config.getBoolean(
                "BetaStarsDisableBetterSkiesStars",
                "celestial",
                true,
                "If true, suppresses MCPatcherForge/BetterSkies star layers while beta stars are enabled so RiftFlux beta stars replace them."
        );

        migrateZeldaConfigKeys();

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

        levelUpEnableSneakAttackDoubleDamage = config.getBoolean(
                "EnableSneakAttackDoubleDamage",
                "LevelUp",
                false,
                "If true, LevelUp!'s melee sneak attack from behind still deals 2x damage."
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

        enableTerraModule = config.getBoolean(
                "EnableTerraModule",
                "terraria",
                true,
                "Master switch for the Terraria module (Demon Eye, Eye of Cthulhu, Suspicious Looking Eye, lenses, and Ice Rod)."
        );

        enableDemonEyeSpawning = config.getBoolean(
                "EnableDemonEyeSpawning",
                "terraria",
                true,
                "If true, Demon Eyes can spawn naturally at night."
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
                        "levelup:respecBook*1|1.0"
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

        demonEyeSpawnWeight = config.getInt(
                "DemonEyeSpawnWeight",
                "terraria",
                2,
                0,
                200,
                "Spawn weight for Demon Eye. Lower is rarer. Set to 0 to disable natural spawning without disabling the module."
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

        terraMushroomSpawnAttempts = config.getInt(
                "TerraMushroomSpawnAttempts",
                "terraria",
                1,
                0,
                64,
                "Generation attempts per chunk for Terra Mushroom (surface). 0 disables spawning."
        );

        daybloomSpawnAttempts = config.getInt(
                "DaybloomSpawnAttempts",
                "terraria",
                1,
                0,
                64,
                "Generation attempts per chunk for Daybloom (surface). 0 disables spawning."
        );

        blinkrootSpawnAttempts = config.getInt(
                "BlinkrootSpawnAttempts",
                "terraria",
                1,
                0,
                64,
                "Generation attempts per chunk for Blinkroot (underground). 0 disables spawning."
        );

        waterleafSpawnAttempts = config.getInt(
                "WaterleafSpawnAttempts",
                "terraria",
                1,
                0,
                64,
                "Generation attempts per chunk for Waterleaf (underground). 0 disables spawning."
        );

        deathweedSpawnAttempts = config.getInt(
                "DeathweedSpawnAttempts",
                "terraria",
                1,
                0,
                64,
                "Generation attempts per chunk for Deathweed (underground). 0 disables spawning."
        );

        fireblossomSpawnAttempts = config.getInt(
                "FireblossomSpawnAttempts",
                "terraria",
                1,
                0,
                64,
                "Generation attempts per chunk for Fireblossom (underground). 0 disables spawning."
        );

        jungleSporeSpawnAttempts = config.getInt(
                "JungleSporeSpawnAttempts",
                "terraria",
                1,
                0,
                64,
                "Generation attempts per chunk for Jungle Spore (underground jungle). 0 disables spawning."
        );

        moonglowSpawnAttempts = config.getInt(
                "MoonglowSpawnAttempts",
                "terraria",
                1,
                0,
                64,
                "Generation attempts per chunk for Moonglow (underground jungle). 0 disables spawning."
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

        int legacyLegendGearManaDatawatcherId = config.getInt(
                "manaDatawatcherID",
                "legendgear",
                20,
                PLAYER_DATAWATCHER_MIN_ID,
                DATAWATCHER_MAX_ID,
                "Legacy key for player mana datawatcher ID. Kept for compatibility."
        );

        legendGearManaDatawatcherId = config.getInt(
                "LegendGearManaDatawatcherID",
                "datawatchers",
                legacyLegendGearManaDatawatcherId,
                PLAYER_DATAWATCHER_MIN_ID,
                DATAWATCHER_MAX_ID,
                "DataWatcher ID for LegendGear mana on players."
        );

        legendGearGlideDatawatcherId = config.getInt(
                "LegendGearGlideDatawatcherID",
                "datawatchers",
                Math.min(DATAWATCHER_MAX_ID, legendGearManaDatawatcherId + 1),
                PLAYER_DATAWATCHER_MIN_ID,
                DATAWATCHER_MAX_ID,
                "DataWatcher ID for LegendGear glide state on players."
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
                true,
                "If true, a thrown Magic Boomerang breaks plant/vine blocks it hits."
        );

        legendGearEnableBadBow = config.getBoolean(
                "enableBadBow",
                "legendgear",
                true,
                "If false, disables registration and recipes for legendgear:badBow."
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
        legendGearStoneskinResistancePotionId = resolveConfiguredPotionIdList(
                getLegendGearPotionIdList(
                        "stoneskinResistancePotionIds",
                        "stoneskinResistancePotionId",
                        VANILLA_POTION_ID_RESISTANCE,
                        "Potion IDs considered for the Stoneskin ritual. The first ID in the list is used."
                ),
                VANILLA_POTION_ID_RESISTANCE
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
        legendGearCaltropsUndergroundGenEnabled = config.getBoolean(
                "caltropsUndergroundGenEnabled",
                "legendgear",
                true,
                "If true, caltrops may generate underground in caves."
        );
        legendGearCaltropsUndergroundSpawnChance = config.getInt(
                "caltropsUndergroundSpawnChance",
                "legendgear",
                3,
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
        legendGearCaltropsSlownessPotionId = resolveConfiguredPotionIdList(
                getLegendGearPotionIdList(
                        "caltropsSlownessPotionIds",
                        "caltropsSlownessPotionId",
                        VANILLA_POTION_ID_SLOWNESS,
                        "Potion IDs considered by caltrops for movement slowdown. The first ID in the list is used."
                ),
                VANILLA_POTION_ID_SLOWNESS
        );
        legendGearExitConfusionPotionId = resolveConfiguredPotionIdList(
                getLegendGearPotionIdList(
                        "exitConfusionPotionIds",
                        "exitConfusionPotionId",
                        VANILLA_POTION_ID_CONFUSION,
                        "Potion IDs considered for the Exit spell side effect. The first ID in the list is used."
                ),
                VANILLA_POTION_ID_CONFUSION
        );
        legendGearIceSpellSlownessPotionId = resolveConfiguredPotionIdList(
                getLegendGearPotionIdList(
                        "iceSpellSlownessPotionIds",
                        "iceSpellSlownessPotionId",
                        VANILLA_POTION_ID_SLOWNESS,
                        "Potion IDs considered by critical Ice spell hits for slowdown. The first ID in the list is used."
                ),
                VANILLA_POTION_ID_SLOWNESS
        );
        legendGearPhoenixReviveResistancePotionId = resolveConfiguredPotionIdList(
                getLegendGearPotionIdList(
                        "phoenixReviveResistancePotionIds",
                        "phoenixReviveResistancePotionId",
                        VANILLA_POTION_ID_RESISTANCE,
                        "Potion IDs considered by phoenix revival effects for resistance. The first ID in the list is used."
                ),
                VANILLA_POTION_ID_RESISTANCE
        );
        legendGearPhoenixReviveRegenerationPotionId = resolveConfiguredPotionIdList(
                getLegendGearPotionIdList(
                        "phoenixReviveRegenerationPotionIds",
                        "phoenixReviveRegenerationPotionId",
                        VANILLA_POTION_ID_REGENERATION,
                        "Potion IDs considered by phoenix revival effects for regeneration. The first ID in the list is used."
                ),
                VANILLA_POTION_ID_REGENERATION
        );
        legendGearPhoenixReviveFireResistancePotionId = resolveConfiguredPotionIdList(
                getLegendGearPotionIdList(
                        "phoenixReviveFireResistancePotionIds",
                        "phoenixReviveFireResistancePotionId",
                        VANILLA_POTION_ID_FIRE_RESISTANCE,
                        "Potion IDs considered by phoenix revival effects for fire resistance. The first ID in the list is used."
                ),
                VANILLA_POTION_ID_FIRE_RESISTANCE
        );
        legendGearThiefRingInvisibilityPotionId = resolveConfiguredPotionIdList(
                getLegendGearPotionIdList(
                        "thiefRingInvisibilityPotionIds",
                        "thiefRingInvisibilityPotionId",
                        VANILLA_POTION_ID_INVISIBILITY,
                        "Potion IDs considered while the Thief Ring is active. The first ID in the list is used."
                ),
                VANILLA_POTION_ID_INVISIBILITY
        );
        legendGearPhoenixEmblemFireResistancePotionId = resolveConfiguredPotionIdList(
                getLegendGearPotionIdList(
                        "phoenixEmblemFireResistancePotionIds",
                        "phoenixEmblemFireResistancePotionId",
                        VANILLA_POTION_ID_FIRE_RESISTANCE,
                        "Potion IDs considered by Phoenix Emblem interventions for fire resistance. The first ID in the list is used."
                ),
                VANILLA_POTION_ID_FIRE_RESISTANCE
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

        healAltarSoulHeartsDatawatcherId = config.getInt(
                "HealingAltarSoulHeartsDatawatcherID",
                "datawatchers",
                Math.min(DATAWATCHER_MAX_ID, legendGearGlideDatawatcherId + 1),
                PLAYER_DATAWATCHER_MIN_ID,
                DATAWATCHER_MAX_ID,
                "DataWatcher ID for Healing Altar soul hearts on players."
        );

        appaFamiliarNameDatawatcherId = config.getInt(
                "AppaFamiliarNameDatawatcherID",
                "datawatchers",
                19,
                ENTITY_DATAWATCHER_MIN_ID,
                DATAWATCHER_MAX_ID,
                "DataWatcher ID for familiar name sync."
        );

        appaBisonSeatParentDatawatcherId = config.getInt(
                "AppaBisonSeatParentDatawatcherID",
                "datawatchers",
                20,
                ENTITY_DATAWATCHER_MIN_ID,
                DATAWATCHER_MAX_ID,
                "DataWatcher ID for bison seat parent entity ID."
        );

        appaBisonSeatIndexDatawatcherId = config.getInt(
                "AppaBisonSeatIndexDatawatcherID",
                "datawatchers",
                21,
                ENTITY_DATAWATCHER_MIN_ID,
                DATAWATCHER_MAX_ID,
                "DataWatcher ID for bison seat index."
        );

        terrariaDemonEyeFlagsDatawatcherId = config.getInt(
                "TerrariaDemonEyeFlagsDatawatcherID",
                "datawatchers",
                16,
                ENTITY_DATAWATCHER_MIN_ID,
                DATAWATCHER_MAX_ID,
                "DataWatcher ID for Demon Eye state flags."
        );

        axolotlStateDatawatcherId = config.getInt(
                "AxolotlStateDatawatcherID",
                "datawatchers",
                18,
                ENTITY_DATAWATCHER_MIN_ID,
                DATAWATCHER_MAX_ID,
                "DataWatcher ID for axolotl variant and state sync."
        );
        if (axolotlStateDatawatcherId < 18) {
            axolotlStateDatawatcherId = 18;
        }

        resolveDatawatcherConflicts();
        persistDatawatcherConfigValue("legendgear", "manaDatawatcherID", legendGearManaDatawatcherId);
        persistDatawatcherConfigValue("datawatchers", "LegendGearManaDatawatcherID", legendGearManaDatawatcherId);
        persistDatawatcherConfigValue("datawatchers", "LegendGearGlideDatawatcherID", legendGearGlideDatawatcherId);
        persistDatawatcherConfigValue("datawatchers", "HealingAltarSoulHeartsDatawatcherID", healAltarSoulHeartsDatawatcherId);
        persistDatawatcherConfigValue("datawatchers", "AppaFamiliarNameDatawatcherID", appaFamiliarNameDatawatcherId);
        persistDatawatcherConfigValue("datawatchers", "AppaBisonSeatParentDatawatcherID", appaBisonSeatParentDatawatcherId);
        persistDatawatcherConfigValue("datawatchers", "AppaBisonSeatIndexDatawatcherID", appaBisonSeatIndexDatawatcherId);
        persistDatawatcherConfigValue("datawatchers", "TerrariaDemonEyeFlagsDatawatcherID", terrariaDemonEyeFlagsDatawatcherId);
        persistDatawatcherConfigValue("datawatchers", "AxolotlStateDatawatcherID", axolotlStateDatawatcherId);

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

        heartLanternAuraDurationSeconds = config.getInt(
                "HeartLanternAuraDurationSeconds",
                "heartcrystal",
                4,
                1,
                60,
                "How long heart lantern aura potion effects last, in seconds."
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
                new String[]{"regeneration,1"},
                "Potion effects applied by heart lanterns.\n" +
                        "Format per entry: potionNameOrId,amplifier\n" +
                        "Examples: regeneration,0  or  moveSpeed,1"
        );

        starLanternAuraEnabled = config.getBoolean(
                "StarLanternAuraEnabled",
                "heartcrystal",
                true,
                "If true, star lanterns apply configurable potion effects to players inside the configured radius."
        );

        starLanternAuraDurationSeconds = config.getInt(
                "StarLanternAuraDurationSeconds",
                "heartcrystal",
                4,
                1,
                60,
                "How long star lantern aura potion effects last, in seconds."
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
                new String[]{"legendgearManaRegen,0"},
                "Potion effects applied by star lanterns.\n" +
                        "Format per entry: potionNameOrId,amplifier\n" +
                        "Examples: legendgearManaRegen,0  or  moveSpeed,1"
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
                true,
                "If true, villages may generate in the Wheatfield biome."
        );

        wheatfieldBarleyPerChunk = config.getInt(
                "BarleyPerChunk",
                "wheatfield",
                999,
                0,
                4096,
                "Pasture-style barley attempts per chunk in Wheatfield."
        );

        wheatfieldBarleyFistDropChancePercent = config.getInt(
                "BarleyFistDropChancePercent",
                "wheatfield",
                20,
                0,
                100,
                "Percent chance for barley to drop when broken by hand without shears."
        );

        wheatfieldBarleyOnlyDropsWhenSheared = config.getBoolean(
                "BarleyOnlyDropsWhenSheared",
                "wheatfield",
                false,
                "If true, barley only drops when harvested with shears."
        );

        wheatfieldRestrictHostileSpawns = config.getBoolean(
                "RestrictHostileSpawns",
                "wheatfield",
                false,
                "If true, only the configured hostile mob ids may naturally spawn in Wheatfield."
        );

        wheatfieldAllowedHostileMobIds = config.getStringList(
                "AllowedHostileMobIds",
                "wheatfield",
                new String[0],
                "Normalized hostile mob ids allowed to naturally spawn in Wheatfield when RestrictHostileSpawns is true.\n"
                        + "Examples: zombie, spider, cyclops, demoneye, blackwidow\n"
                        + "RiftFlux rebuilds Wheatfield's hostile spawn list from these ids, copying default spawn weights and group sizes from existing natural spawns."
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

        cyclopsSpawnWeight = config.getInt(
                "CyclopsSpawnWeight",
                "witchesandmore",
                1,
                0,
                1000,
                "Natural spawn weight for Cyclops in overworld biomes at night."
        );

        cyclopsMaxHealth = config.getInt(
                "CyclopsMaxHealth",
                "witchesandmore",
                60,
                1,
                2048,
                "Base max health for Cyclops. The eyeless state still scales down from this value."
        );

        cyclopsUseBiomeWhitelist = config.getBoolean(
                "CyclopsUseBiomeWhitelist",
                "witchesandmore",
                false,
                "If true, CyclopsBiomeList becomes a whitelist for natural spawning. If false, it is a blacklist."
        );

        cyclopsBiomeList = config.getStringList(
                "CyclopsBiomeList",
                "witchesandmore",
                new String[0],
                "Biome filters for natural Cyclops spawns.\n"
                        + "Accepted entries: biome id (4), biome name (Birch Forest), or biome dictionary tag (type:FOREST)."
        );

        enableFlowerManMob = config.getBoolean(
                "EnableFlowerMan",
                "witchesandmore",
                true,
                "If true, Flower Men are registered and may spawn from eggs, natural spawning, and structures."
        );

        flowerManSpawnWeight = config.getInt(
                "FlowerManSpawnWeight",
                "witchesandmore",
                4,
                0,
                1000,
                "Natural spawn weight for Flower Men in forest-type biomes."
        );

        flowerManMaxHealth = config.getInt(
                "FlowerManMaxHealth",
                "witchesandmore",
                4,
                1,
                2048,
                "Base max health for Flower Men."
        );

        flowerManUseBiomeWhitelist = config.getBoolean(
                "FlowerManUseBiomeWhitelist",
                "witchesandmore",
                false,
                "If true, FlowerManBiomeList becomes a whitelist for natural spawning. If false, it is a blacklist."
        );

        flowerManBiomeList = config.getStringList(
                "FlowerManBiomeList",
                "witchesandmore",
                new String[0],
                "Biome filters for natural Flower Man spawns.\n"
                        + "Accepted entries: biome id (4), biome name (Birch Forest), or biome dictionary tag (type:FOREST)."
        );

        enableEnderTrollMob = config.getBoolean(
                "EnableEnderTroll",
                "witchesandmore",
                true,
                "If true, Ender Trolls are registered and may spawn from eggs, natural spawning, and structures."
        );

        enderTrollSpawnWeight = config.getInt(
                "EnderTrollSpawnWeight",
                "witchesandmore",
                2,
                0,
                1000,
                "Natural spawn weight for Ender Trolls in forest-type biomes at night."
        );

        enderTrollMaxHealth = config.getInt(
                "EnderTrollMaxHealth",
                "witchesandmore",
                80,
                1,
                2048,
                "Base max health for Ender Trolls."
        );

        enderTrollUseBiomeWhitelist = config.getBoolean(
                "EnderTrollUseBiomeWhitelist",
                "witchesandmore",
                false,
                "If true, EnderTrollBiomeList becomes a whitelist for natural spawning. If false, it is a blacklist."
        );

        enderTrollBiomeList = config.getStringList(
                "EnderTrollBiomeList",
                "witchesandmore",
                new String[0],
                "Biome filters for natural Ender Troll spawns.\n"
                        + "Accepted entries: biome id (4), biome name (Birch Forest), or biome dictionary tag (type:FOREST)."
        );

        enableJaxxMob = config.getBoolean(
                "EnableJaxx",
                "witchesandmore",
                true,
                "If true, JAXX are registered and may spawn from eggs, natural spawning, and structures."
        );

        jaxxSpawnWeight = config.getInt(
                "JaxxSpawnWeight",
                "witchesandmore",
                1,
                0,
                1000,
                "Natural spawn weight for JAXX in forest-type biomes at night."
        );

        jaxxMaxHealth = config.getInt(
                "JaxxMaxHealth",
                "witchesandmore",
                140,
                1,
                2048,
                "Base max health for JAXX."
        );

        jaxxUseBiomeWhitelist = config.getBoolean(
                "JaxxUseBiomeWhitelist",
                "witchesandmore",
                false,
                "If true, JaxxBiomeList becomes a whitelist for natural spawning. If false, it is a blacklist."
        );

        jaxxBiomeList = config.getStringList(
                "JaxxBiomeList",
                "witchesandmore",
                new String[0],
                "Biome filters for natural JAXX spawns.\n"
                        + "Accepted entries: biome id (4), biome name (Birch Forest), or biome dictionary tag (type:FOREST)."
        );

        enableBlackWidowMob = config.getBoolean(
                "EnableBlackWidow",
                "witchesandmore",
                true,
                "If true, Black Widows are registered and may spawn from eggs, natural spawning, and structures."
        );

        blackWidowSpawnWeight = config.getInt(
                "BlackWidowSpawnWeight",
                "witchesandmore",
                4,
                0,
                1000,
                "Natural spawn weight for Black Widows in forest-type biomes at night."
        );

        blackWidowMaxHealth = config.getInt(
                "BlackWidowMaxHealth",
                "witchesandmore",
                24,
                1,
                2048,
                "Base max health for Black Widows."
        );

        blackWidowUseBiomeWhitelist = config.getBoolean(
                "BlackWidowUseBiomeWhitelist",
                "witchesandmore",
                false,
                "If true, BlackWidowBiomeList becomes a whitelist for natural spawning. If false, it is a blacklist."
        );

        blackWidowBiomeList = config.getStringList(
                "BlackWidowBiomeList",
                "witchesandmore",
                new String[0],
                "Biome filters for natural Black Widow spawns.\n"
                        + "Accepted entries: biome id (4), biome name (Birch Forest), or biome dictionary tag (type:FOREST)."
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
                2,
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

        inventoryPetsDungeonLootEntries = config.getStringList(
                "DungeonLootPets",
                "inventorypets",
                DEFAULT_INVENTORY_PET_DUNGEON_LOOT,
                "Inventory Pets that may appear in dungeon chests.\n" +
                        "Remove names from this list to stop those pets from spawning.\n" +
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

        enableAxolotlModule = config.getBoolean(
                "EnableAxolotlModule",
                "axolotl",
                true,
                "Master switch for integrated axolotl content."
        );

        enableAxolotlNaturalSpawning = config.getBoolean(
                "EnableNaturalSpawning",
                "axolotl",
                true,
                "If true, axolotls naturally spawn in wet overworld biomes."
        );

        axolotlSpawnWeight = config.getInt(
                "SpawnWeight",
                "axolotl",
                6,
                0,
                1000,
                "Spawn weight for natural axolotl spawning."
        );

        axolotlMaxHealth = config.getFloat(
                "MaxHealth",
                "axolotl",
                14.0F,
                1.0F,
                1024.0F,
                "Base max health for axolotls."
        );

        protectCircuitryFromWater = config.getBoolean(
                "ProtectCircuitryFromWater",
                "general",
                true,
                "If true, water will not wash away redstone dust, repeaters, comparators, buttons, rails, and similar circuitry."
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

        persistDatawatcherConfigValue("armoroverlay", "OverlayLevels", armorOverlayLevels);
        persistDatawatcherConfigValue("armoroverlay", "ArmorPieces", armorOverlayArmorPieces);

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

        boolean disableFalseCrashImproverDefault = true;
        if (config.hasKey("general", "DisableFalsePatternLibCrashLogAppend")) {
            disableFalseCrashImproverDefault = config.get("general", "DisableFalsePatternLibCrashLogAppend", true,
                    "Legacy key for DisableFalseCrashImprover.").getBoolean(true);
        }

        disableFalseCrashImprover = config.get("general", "DisableFalseCrashImprover", disableFalseCrashImproverDefault,
                        "If true, prevents FalsePatternLib from appending the full latest FML log to crash reports.")
                .getBoolean(disableFalseCrashImproverDefault);

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
                6.0F,
                0.0F,
                20.0F,
                "LegendGear mana restored by the Poptart. 2.0 = one full mana icon."
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

        config.save();
    }

    private static void resolveDatawatcherConflicts() {
        Set<Integer> used = new HashSet<Integer>();

        legendGearManaDatawatcherId = reserveUniqueDatawatcherId(legendGearManaDatawatcherId, PLAYER_DATAWATCHER_MIN_ID, DATAWATCHER_MAX_ID, used);
        legendGearGlideDatawatcherId = reserveUniqueDatawatcherId(legendGearGlideDatawatcherId, PLAYER_DATAWATCHER_MIN_ID, DATAWATCHER_MAX_ID, used);
        healAltarSoulHeartsDatawatcherId = reserveUniqueDatawatcherId(healAltarSoulHeartsDatawatcherId, PLAYER_DATAWATCHER_MIN_ID, DATAWATCHER_MAX_ID, used);
        appaFamiliarNameDatawatcherId = reserveUniqueDatawatcherId(appaFamiliarNameDatawatcherId, ENTITY_DATAWATCHER_MIN_ID, DATAWATCHER_MAX_ID, used);
        appaBisonSeatParentDatawatcherId = reserveUniqueDatawatcherId(appaBisonSeatParentDatawatcherId, ENTITY_DATAWATCHER_MIN_ID, DATAWATCHER_MAX_ID, used);
        appaBisonSeatIndexDatawatcherId = reserveUniqueDatawatcherId(appaBisonSeatIndexDatawatcherId, ENTITY_DATAWATCHER_MIN_ID, DATAWATCHER_MAX_ID, used);
        terrariaDemonEyeFlagsDatawatcherId = reserveUniqueDatawatcherId(terrariaDemonEyeFlagsDatawatcherId, ENTITY_DATAWATCHER_MIN_ID, DATAWATCHER_MAX_ID, used);
        axolotlStateDatawatcherId = reserveUniqueDatawatcherId(axolotlStateDatawatcherId, 18, DATAWATCHER_MAX_ID, used);
    }

    private static int reserveUniqueDatawatcherId(int requested, int min, int max, Set<Integer> used) {
        int candidate = Math.max(min, Math.min(max, requested));
        if (!used.contains(candidate)) {
            used.add(candidate);
            return candidate;
        }
        for (int i = min; i <= max; i++) {
            if (!used.contains(i)) {
                used.add(i);
                return i;
            }
        }
        return candidate;
    }

    private static void persistDatawatcherConfigValue(String category, String key, int value) {
        config.get(category, key, value).set(value);
    }

    public static boolean isValidPlayerDatawatcherId(int id) {
        return id >= PLAYER_DATAWATCHER_MIN_ID && id <= DATAWATCHER_MAX_ID;
    }

    public static boolean isValidEntityDatawatcherId(int id) {
        return id >= ENTITY_DATAWATCHER_MIN_ID && id <= DATAWATCHER_MAX_ID;
    }

    private static void migrateCelestialConfigCategory() {
        moveConfigPropertyIfMissing("client", "celestial", "EnableCelestialEventTextures");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialSunEventChance");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialSunEventTextures");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialSunEventTexture");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialMoonEventChance");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialMoonEventTextures");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialMoonEventTexture");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialFullSunriseSunsetTint");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialFogMatchesSky");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialBetaStyleFogBiomeTint");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialBlackNightFog");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialVoidFogStartHeight");
        moveConfigPropertyIfMissing("client", "celestial", "CelestialVoidParticleStartHeight");
        moveConfigPropertyIfMissing("client", "celestial", "EnableBetaStars");
        moveConfigPropertyIfMissing("client", "celestial", "BetaStarsCount");
        moveConfigPropertyIfMissing("client", "celestial", "BetaStarsSizeMultiplier");
        moveConfigPropertyIfMissing("client", "celestial", "BetaStarsDisableBetterSkiesStars");
        renameConfigPropertyIfMissing("celestial", "BetaStarsDisableBetterSkiesLayers", "BetaStarsDisableBetterSkiesStars");
    }

    private static void migrateClientConfigKeys() {
        renameConfigPropertyIfMissing("client", "enableItemRenderLimiter", "EnableDroppedItemRenderTweaks");
    }

    private static void migrateZeldaConfigKeys() {
        renameConfigPropertyIfMissing("zelda", "HeartContainerFirstKillPerConfiguredEntity", "HeartContainerFirstKillOnly");
    }

    private static void moveConfigPropertyIfMissing(String oldCategory, String newCategory, String key) {
        if (!config.hasKey(oldCategory, key) || config.hasKey(newCategory, key)) {
            return;
        }
        config.moveProperty(oldCategory, key, newCategory);
    }

    private static void renameConfigPropertyIfMissing(String category, String oldKey, String newKey) {
        if (!config.hasKey(category, oldKey) || config.hasKey(category, newKey)) {
            return;
        }
        if (config.getCategory(category) == null) {
            return;
        }
        net.minecraftforge.common.config.Property property = config.getCategory(category).remove(oldKey);
        if (property == null) {
            return;
        }
        property.setName(newKey);
        config.getCategory(category).put(newKey, property);
    }

    private static void removeConfigProperty(String category, String key) {
        if (!config.hasKey(category, key) || config.getCategory(category) == null) {
            return;
        }
        config.getCategory(category).remove(key);
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
            out = forceCustomPaintingEntry(out, "Unicorn", "Unicorn;32;16;0;0;riftflux:textures/painting/Unicorn.png");
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
            out = forceCustomPaintingEntry(out, "UnityKebab", "UnityKebab;16;16;0;0;riftflux:textures/painting/atlas_unity/UnityKebab.png");
            out = forceCustomPaintingEntry(out, "UnityAztec", "UnityAztec;16;16;0;0;riftflux:textures/painting/atlas_unity/UnityAztec.png");
            out = forceCustomPaintingEntry(out, "UnityAlban", "UnityAlban;16;16;0;0;riftflux:textures/painting/atlas_unity/UnityAlban.png");
            out = forceCustomPaintingEntry(out, "UnityPool", "UnityPool;32;16;0;0;riftflux:textures/painting/atlas_unity/UnityPool.png");
            out = forceCustomPaintingEntry(out, "UnityCourbet", "UnityCourbet;32;16;0;0;riftflux:textures/painting/atlas_unity/UnityCourbet.png");
            out = forceCustomPaintingEntry(out, "UnitySea", "UnitySea;32;16;0;0;riftflux:textures/painting/atlas_unity/UnitySea.png");
            out = forceCustomPaintingEntry(out, "UnityWanderer", "UnityWanderer;16;32;0;0;riftflux:textures/painting/atlas_unity/UnityWanderer.png");
            out = forceCustomPaintingEntry(out, "UnityGraham", "UnityGraham;16;32;0;0;riftflux:textures/painting/atlas_unity/UnityGraham.png");
            out = forceCustomPaintingEntry(out, "UnityFighters", "UnityFighters;64;32;0;0;riftflux:textures/painting/atlas_unity/UnityFighters.png");
            out = forceCustomPaintingEntry(out, "UnityPointer", "UnityPointer;64;64;0;0;riftflux:textures/painting/atlas_unity/UnityPointer.png");
            out = forceCustomPaintingEntry(out, "GalleryKebab", "GalleryKebab;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryKebab.png");
            out = forceCustomPaintingEntry(out, "GalleryAztec", "GalleryAztec;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryAztec.png");
            out = forceCustomPaintingEntry(out, "GalleryAlban", "GalleryAlban;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryAlban.png");
            out = forceCustomPaintingEntry(out, "GalleryAztec2", "GalleryAztec2;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryAztec2.png");
            out = forceCustomPaintingEntry(out, "GalleryBomb", "GalleryBomb;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryBomb.png");
            out = forceCustomPaintingEntry(out, "GalleryPlant", "GalleryPlant;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryPlant.png");
            out = forceCustomPaintingEntry(out, "GalleryWasteland", "GalleryWasteland;16;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryWasteland.png");
            out = forceCustomPaintingEntry(out, "GalleryPool", "GalleryPool;32;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryPool.png");
            out = forceCustomPaintingEntry(out, "GalleryCourbet", "GalleryCourbet;32;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryCourbet.png");
            out = forceCustomPaintingEntry(out, "GallerySea", "GallerySea;32;16;0;0;riftflux:textures/painting/atlas_gallery/GallerySea.png");
            out = forceCustomPaintingEntry(out, "GallerySunset", "GallerySunset;32;16;0;0;riftflux:textures/painting/atlas_gallery/GallerySunset.png");
            out = forceCustomPaintingEntry(out, "GalleryCreebet", "GalleryCreebet;32;16;0;0;riftflux:textures/painting/atlas_gallery/GalleryCreebet.png");
            out = forceCustomPaintingEntry(out, "GalleryWanderer", "GalleryWanderer;16;32;0;0;riftflux:textures/painting/atlas_gallery/GalleryWanderer.png");
            out = forceCustomPaintingEntry(out, "GalleryGraham", "GalleryGraham;16;32;0;0;riftflux:textures/painting/atlas_gallery/GalleryGraham.png");
            out = forceCustomPaintingEntry(out, "GalleryFighters", "GalleryFighters;64;32;0;0;riftflux:textures/painting/atlas_gallery/GalleryFighters.png");
            out = forceCustomPaintingEntry(out, "GalleryBust", "GalleryBust;32;32;0;0;riftflux:textures/painting/atlas_gallery/GalleryBust.png");
            out = forceCustomPaintingEntry(out, "GalleryVoid", "GalleryVoid;32;32;0;0;riftflux:textures/painting/atlas_gallery/GalleryVoid.png");
            out = forceCustomPaintingEntry(out, "GalleryPointer", "GalleryPointer;64;64;0;0;riftflux:textures/painting/atlas_gallery/GalleryPointer.png");
            out = forceCustomPaintingEntry(out, "GalleryBurningSkull", "GalleryBurningSkull;64;64;0;0;riftflux:textures/painting/atlas_gallery/GalleryBurningSkull.png");
            out = forceCustomPaintingEntry(out, "ArcaneKebab", "ArcaneKebab;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneKebab.png");
            out = forceCustomPaintingEntry(out, "ArcaneAztec", "ArcaneAztec;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneAztec.png");
            out = forceCustomPaintingEntry(out, "ArcaneAlban", "ArcaneAlban;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneAlban.png");
            out = forceCustomPaintingEntry(out, "ArcaneAztec2", "ArcaneAztec2;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneAztec2.png");
            out = forceCustomPaintingEntry(out, "ArcaneBomb", "ArcaneBomb;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneBomb.png");
            out = forceCustomPaintingEntry(out, "ArcanePlant", "ArcanePlant;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcanePlant.png");
            out = forceCustomPaintingEntry(out, "ArcaneWasteland", "ArcaneWasteland;16;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneWasteland.png");
            out = forceCustomPaintingEntry(out, "ArcanePool", "ArcanePool;32;16;0;0;riftflux:textures/painting/atlas_arcane/ArcanePool.png");
            out = forceCustomPaintingEntry(out, "ArcaneCourbet", "ArcaneCourbet;32;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneCourbet.png");
            out = forceCustomPaintingEntry(out, "ArcaneSea", "ArcaneSea;32;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneSea.png");
            out = forceCustomPaintingEntry(out, "ArcaneSunset", "ArcaneSunset;32;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneSunset.png");
            out = forceCustomPaintingEntry(out, "ArcaneCreebet", "ArcaneCreebet;32;16;0;0;riftflux:textures/painting/atlas_arcane/ArcaneCreebet.png");
            out = forceCustomPaintingEntry(out, "ArcaneWanderer", "ArcaneWanderer;16;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneWanderer.png");
            out = forceCustomPaintingEntry(out, "ArcaneGraham", "ArcaneGraham;16;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneGraham.png");
            out = forceCustomPaintingEntry(out, "ArcaneFighters", "ArcaneFighters;64;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneFighters.png");
            out = forceCustomPaintingEntry(out, "ArcaneMatch", "ArcaneMatch;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneMatch.png");
            out = forceCustomPaintingEntry(out, "ArcaneBust", "ArcaneBust;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneBust.png");
            out = forceCustomPaintingEntry(out, "ArcaneStage", "ArcaneStage;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneStage.png");
            out = forceCustomPaintingEntry(out, "ArcaneVoid", "ArcaneVoid;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneVoid.png");
            out = forceCustomPaintingEntry(out, "ArcaneSkullAndRoses", "ArcaneSkullAndRoses;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneSkullAndRoses.png");
            out = forceCustomPaintingEntry(out, "ArcaneWither", "ArcaneWither;32;32;0;0;riftflux:textures/painting/atlas_arcane/ArcaneWither.png");
            out = forceCustomPaintingEntry(out, "ArcanePointer", "ArcanePointer;64;64;0;0;riftflux:textures/painting/atlas_arcane/ArcanePointer.png");
            out = forceCustomPaintingEntry(out, "ArcanePigscene", "ArcanePigscene;64;64;0;0;riftflux:textures/painting/atlas_arcane/ArcanePigscene.png");
            out = forceCustomPaintingEntry(out, "ArcaneBurningSkull", "ArcaneBurningSkull;64;64;0;0;riftflux:textures/painting/atlas_arcane/ArcaneBurningSkull.png");
            out = forceCustomPaintingEntry(out, "ArcaneSkeleton", "ArcaneSkeleton;64;48;0;0;riftflux:textures/painting/atlas_arcane/ArcaneSkeleton.png");
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
            out = removeCustomPaintingEntry(out, "GalleryMatch");
            out = removeCustomPaintingEntry(out, "GallerySkullAndRoses");
            out = removeCustomPaintingEntry(out, "GalleryWither");
            out = removeCustomPaintingEntry(out, "UnitySkullAndRoses");
            out = removeCustomPaintingEntry(out, "UnityMatch");
            out = removeCustomPaintingEntry(out, "UnityStage");
            out = removeCustomPaintingEntry(out, "UnityVoid");
            out = removeCustomPaintingEntry(out, "UnityBust");
            out = removeCustomPaintingEntry(out, "ArcaneDonkeyKong");
            out = removeCustomPaintingEntry(out, "GallerySkeleton");
            out = removeCustomPaintingEntry(out, "GalleryDonkeyKong");
            out = removeCustomPaintingEntry(out, "UnitySkeleton");
            out = removeCustomPaintingEntry(out, "GalleryPigscene");
            out = removeCustomPaintingEntry(out, "UnityBurningSkull");
            out = removeCustomPaintingEntry(out, "UnityPigscene");
            out = removeCustomPaintingEntry(out, "UnitySunset");
            out = removeCustomPaintingEntry(out, "UnityDonkeyKong");
            out = removeCustomPaintingEntry(out, "UnityCreebet");
            out = removeCustomPaintingEntry(out, "UnityWither");
            out = removeCustomPaintingEntry(out, "UnityAztec2");
            out = removeCustomPaintingEntry(out, "UnityPlant");
            out = removeCustomPaintingEntry(out, "UnityWasteland");
            out = removeCustomPaintingEntry(out, "UnityBomb");
            return out;
        } finally {
            allowCustomPaintingInsert = previousAllow;
        }
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

    private static int[] getLegendGearPotionIdList(String key, String legacyKey, int defaultId, String comment) {
        String[] defaults = new String[]{String.valueOf(defaultId)};
        if (!config.hasKey("legendgear", key) && config.hasKey("legendgear", legacyKey)) {
            defaults = new String[]{String.valueOf(config.getInt(legacyKey, "legendgear", defaultId, 0, 255, comment))};
        }
        return parsePotionIdList(config.getStringList(key, "legendgear", defaults, comment));
    }

    private static int resolveConfiguredPotionIdList(int[] configuredIds, int fallback) {
        if (configuredIds != null) {
            for (int configuredId : configuredIds) {
                if (configuredId >= 0) {
                    return configuredId;
                }
            }
        }
        return fallback;
    }

}

