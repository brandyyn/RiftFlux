package com.voidsrift.riftflux.legendgear;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.nmccoy.legendgear.entity.EntityMagicBoomerang;
import net.nmccoy.legendgear.LegendGear2;

public final class LegendGearContent {
    private static LegendGear2 module;
    private static boolean preInited;
    private static boolean initialized;
    private static boolean postInited;
    private static boolean activated;

    private LegendGearContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!ModConfig.enableLegendGearModule) {
            return;
        }

        if (Loader.isModLoaded(LegendGear2.MODID)) {
            activated = true;
            return;
        }

        applyConfig();
        ensureProxy();

        if (LegendGear2.instance == null) {
            LegendGear2.instance = new LegendGear2();
        }

        module = LegendGear2.instance;
        module.preInit(event);
        LegendGearAdditionsContent.preInit(event);
        activated = true;
    }

    public static void init(FMLInitializationEvent event) {
        if (!activated || initialized || module == null) {
            return;
        }
        initialized = true;
        module.init(event);
        LegendGearAdditionsContent.init(event);
    }

    public static void postInit(FMLPostInitializationEvent event) {
        if (!activated || postInited || module == null) {
            return;
        }
        postInited = true;
        module.postInit(event);
    }

    public static boolean isEnabled() {
        return activated;
    }

    private static void ensureProxy() {
        if (LegendGear2.proxy != null) {
            return;
        }
        Side side = FMLCommonHandler.instance().getSide();
        if (side == Side.CLIENT) {
            LegendGear2.proxy = new net.nmccoy.legendgear.client.ClientProxy();
        } else {
            LegendGear2.proxy = new net.nmccoy.legendgear.CommonProxy();
        }
    }

    private static void applyConfig() {
        LegendGear2.config = ModConfig.config;

        LegendGear2.enchMagicProtectionID = ModConfig.legendGearMagicProtectionId;
        LegendGear2.enchSpellReachID = ModConfig.legendGearSpellReachId;
        LegendGear2.enchSpellSpreadID = ModConfig.legendGearSpellSpreadId;
        LegendGear2.enchSpellArmoredID = ModConfig.legendGearSpellArmoredId;
        LegendGear2.CONFIG_ALLOW_EMERALD_DROPS = ModConfig.legendGearAllowEmeraldDrops;
        LegendGear2.CONFIG_ALLOW_HEART_DROPS = ModConfig.legendGearAllowHeartDrops;
        LegendGear2.CONFIG_PICKUP_SOUND_VOLUME = ModConfig.legendGearItemSoundVolume;
        LegendGear2.CONFIG_FANCY_XP = ModConfig.legendGearFancyExperience;
        LegendGear2.CONFIG_MAX_STARWELL_ATTEMPTS = ModConfig.legendGearMaxStarwellRetries;
        LegendGear2.CONFIG_FALLING_STAR_DAMAGE_ENABLED = ModConfig.legendGearFallingStarDamageEnabled;
        LegendGear2.CONFIG_FALLING_STAR_DAMAGE = ModConfig.legendGearFallingStarDamage;
        LegendGear2.CONFIG_MAGIC_BOOMERANG_INFINITE_DURABILITY = ModConfig.legendGearMagicBoomerangInfiniteDurability;
        LegendGear2.CONFIG_MAGIC_BOOMERANG_DAMAGE = ModConfig.legendGearMagicBoomerangDamage;
        LegendGear2.CONFIG_ENABLE_BAD_BOW = ModConfig.legendGearEnableBadBow;
        LegendGear2.CONFIG_DASH_RING_MAX_AIR_JUMPS = ModConfig.legendGearDashRingMaxAirJumps;
        LegendGear2.CONFIG_DASH_RING_USE_ORIGINAL_BEHAVIOR = ModConfig.legendGearDashRingUseOriginalBehavior;
        LegendGear2.CONFIG_SPRINKLE_STARDUST_REQUIRE_SNEAK = ModConfig.legendGearSprinkleStardustRequireSneak;
        LegendGear2.CONFIG_SPOTTING_SCOPE_CONSUMES_MANA = ModConfig.legendGearSpottingScopeConsumesMana;
        LegendGear2.CONFIG_MANA_REGEN_POTION_ID = ModConfig.legendGearManaRegenPotionId;
        LegendGear2.CONFIG_GROUNDED_POTION_ID = ModConfig.legendGearGroundedPotionId;
        LegendGear2.CONFIG_MANA_REGEN_POTION_PER_SECOND = ModConfig.legendGearManaRegenPotionManaPerSecond;
        LegendGear2.CONFIG_STONESKIN_RESISTANCE_POTION_ID = ModConfig.legendGearStoneskinResistancePotionId;
        LegendGear2.CONFIG_CALTROPS_BREAK_ON_TRIGGER = ModConfig.legendGearCaltropsBreakOnTrigger;
        LegendGear2.CONFIG_CALTROPS_TRIGGER_DROP_ENABLED = ModConfig.legendGearCaltropsTriggerDropEnabled;
        LegendGear2.CONFIG_CALTROPS_MOB_DAMAGE = ModConfig.legendGearCaltropsMobDamageHearts * 2.0F;
        LegendGear2.CONFIG_CALTROPS_PLAYER_DAMAGE_PERCENT = ModConfig.legendGearCaltropsPlayerDamagePercent;
        LegendGear2.CONFIG_CALTROPS_IRON_BARS_MINING_SPEED = ModConfig.legendGearCaltropsIronBarsMiningSpeed;
        LegendGear2.CONFIG_CALTROPS_REQUIRE_PICKAXE_TO_DROP = ModConfig.legendGearCaltropsRequirePickaxeToDrop;
        LegendGear2.CONFIG_CALTROPS_UNDERGROUND_GEN_ENABLED = ModConfig.legendGearCaltropsUndergroundGenEnabled;
        LegendGear2.CONFIG_CALTROPS_UNDERGROUND_SPAWN_CHANCE = ModConfig.legendGearCaltropsUndergroundSpawnChance;
        LegendGear2.CONFIG_CALTROPS_UNDERGROUND_MIN_Y = ModConfig.legendGearCaltropsUndergroundMinY;
        LegendGear2.CONFIG_CALTROPS_UNDERGROUND_MAX_Y = ModConfig.legendGearCaltropsUndergroundMaxY;
        LegendGear2.CONFIG_CALTROPS_SLOWNESS_POTION_ID = ModConfig.legendGearCaltropsSlownessPotionId;
        LegendGear2.CONFIG_EXIT_CONFUSION_POTION_ID = ModConfig.legendGearExitConfusionPotionId;
        LegendGear2.CONFIG_ICE_SPELL_SLOWNESS_POTION_ID = ModConfig.legendGearIceSpellSlownessPotionId;
        LegendGear2.CONFIG_PHOENIX_REVIVE_RESISTANCE_POTION_ID = ModConfig.legendGearPhoenixReviveResistancePotionId;
        LegendGear2.CONFIG_PHOENIX_REVIVE_REGENERATION_POTION_ID = ModConfig.legendGearPhoenixReviveRegenerationPotionId;
        LegendGear2.CONFIG_PHOENIX_REVIVE_FIRE_RESISTANCE_POTION_ID = ModConfig.legendGearPhoenixReviveFireResistancePotionId;
        LegendGear2.CONFIG_THIEF_RING_INVISIBILITY_POTION_ID = ModConfig.legendGearThiefRingInvisibilityPotionId;
        LegendGear2.CONFIG_PHOENIX_EMBLEM_FIRE_RESISTANCE_POTION_ID = ModConfig.legendGearPhoenixEmblemFireResistancePotionId;
        EntityMagicBoomerang.BOOMERANG_DAMAGE = LegendGear2.CONFIG_MAGIC_BOOMERANG_DAMAGE;
    }
}
