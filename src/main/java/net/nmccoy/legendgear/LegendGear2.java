/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.IWorldGenerator
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
 *  cpw.mods.fml.common.registry.EntityRegistry
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.block.Block
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.stats.Achievement
 *  net.minecraftforge.common.AchievementPage
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.config.Configuration
 *  net.minecraftforge.common.util.EnumHelper
 *  net.minecraftforge.oredict.OreDictionary
 *  net.minecraftforge.oredict.ShapelessOreRecipe
 */
package net.nmccoy.legendgear;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfigResolver;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.nmccoy.legendgear.AzuriteGenerator;
import net.nmccoy.legendgear.CommonProxy;
import net.nmccoy.legendgear.CustomAttributes;
import net.nmccoy.legendgear.Fortunes;
import net.nmccoy.legendgear.PlayerEventHandler;
import net.nmccoy.legendgear.StarSpirit;
import net.nmccoy.legendgear.StarwellGenerator;
import net.nmccoy.legendgear.block.AzuriteOreBlock;
import net.nmccoy.legendgear.block.CaltropsBlock;
import net.nmccoy.legendgear.block.FragstoneBlock;
import net.nmccoy.legendgear.block.InfusedStarstoneBlock;
import net.nmccoy.legendgear.block.InfusedStarPieceBlock;
import net.nmccoy.legendgear.block.LgItemBlockMeta;
import net.nmccoy.legendgear.block.PhoenixAltar;
import net.nmccoy.legendgear.block.RitualBlock;
import net.nmccoy.legendgear.block.SkylensBlock;
import net.nmccoy.legendgear.block.StarSandBlock;
import net.nmccoy.legendgear.block.StarPieceBlock;
import net.nmccoy.legendgear.block.StarstoneBlock;
import net.nmccoy.legendgear.block.StarwellBlock;
import net.nmccoy.legendgear.block.StarwellFrameBlock;
import net.nmccoy.legendgear.block.StruckGroundBlock;
import net.nmccoy.legendgear.block.ThawingIceBlock;
import net.nmccoy.legendgear.block.TileEntityAltar;
import net.nmccoy.legendgear.block.TileEntityPlacedStar;
import net.nmccoy.legendgear.block.TileEntityRitual;
import net.nmccoy.legendgear.block.TileEntityStarstone;
import net.nmccoy.legendgear.block.TileEntityStarwell;
import net.nmccoy.legendgear.enchantment.EnchantmentMagicProtection;
import net.nmccoy.legendgear.enchantment.EnchantmentSpellArmored;
import net.nmccoy.legendgear.enchantment.EnchantmentSpellReach;
import net.nmccoy.legendgear.enchantment.EnchantmentSpellSpread;
import net.nmccoy.legendgear.entity.EntityFallingStar;
import net.nmccoy.legendgear.entity.EntityHeart;
import net.nmccoy.legendgear.entity.EntityMagicBoomerang;
import net.nmccoy.legendgear.entity.EntityPing;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.entity.EntityThrownOrb;
import net.nmccoy.legendgear.entity.SpellDecorator;
import net.nmccoy.legendgear.item.AbstractionGel;
import net.nmccoy.legendgear.item.AzureFeather;
import net.nmccoy.legendgear.item.BadBow;
import net.nmccoy.legendgear.item.CharmPendant;
import net.nmccoy.legendgear.item.DimensionalCatalyst;
import net.nmccoy.legendgear.item.EmeraldShard;
import net.nmccoy.legendgear.item.FortuneCookie;
import net.nmccoy.legendgear.item.ItemAzurite;
import net.nmccoy.legendgear.item.ItemCaltropsBlock;
import net.nmccoy.legendgear.item.ItemMagicBoomerang;
import net.nmccoy.legendgear.item.ItemNucleus;
import net.nmccoy.legendgear.item.LGItem;
import net.nmccoy.legendgear.item.LGRecord;
import net.nmccoy.legendgear.item.MagicRing;
import net.nmccoy.legendgear.item.MilkChocolate;
import net.nmccoy.legendgear.item.PhoenixFeather;
import net.nmccoy.legendgear.item.ReedPipes;
import net.nmccoy.legendgear.item.SpiritEmblem;
import net.nmccoy.legendgear.item.SpottingScope;
import net.nmccoy.legendgear.item.StarDust;
import net.nmccoy.legendgear.item.StarglassOrb;
import net.nmccoy.legendgear.item.TuningFork;
import net.nmccoy.legendgear.item.spell.StaffFire;
import net.nmccoy.legendgear.item.spell.StaffIce;
import net.nmccoy.legendgear.item.spell.StaffTwinkle;
import net.nmccoy.legendgear.item.spell.StaffZap;
import net.nmccoy.legendgear.item.spell.Tome;
import net.nmccoy.legendgear.network.StarwellMessage;
import net.nmccoy.legendgear.network.StarwellMessageHandler;
import net.nmccoy.legendgear.potion.LegendGearPotions;
import net.nmccoy.legendgear.ritual.RitualManager;

public class LegendGear2 {
    private static final int ENTITY_ID_BASE = 700;
    public static final String MODID = "legendgear";
    public static final String VERSION = "2.b.2.1";
    private static final int VANILLA_POTION_ID_SLOWNESS = 2;
    private static final int VANILLA_POTION_ID_CONFUSION = 9;
    private static final int VANILLA_POTION_ID_REGENERATION = 10;
    private static final int VANILLA_POTION_ID_RESISTANCE = 11;
    private static final int VANILLA_POTION_ID_FIRE_RESISTANCE = 12;
    private static final int VANILLA_POTION_ID_INVISIBILITY = 14;
    public static float manaRechargeRate = 4.0f;
    public static float fatiguedRechargeRate = 0.75f;
    public static float manaRechargeDelay = 2.0f;
    public static float fatiguedRechargeDelay = 6.0f;
    public static float exhaustedRechargeDelay = 20.0f;
    public static int emeraldExchangeRate = 8;
    public static int starFadeTime = 440;
    public static int starCooldown = 600;
    public static int starCooldownFuzz = 600;
    public static int starKarmaCost = 5000;
    public static int huntingKarmaBonus = 300;
    public static int starKarmaCap = 20000;
    public static int maxEmeraldDropsBanked = 6;
    public static int maxEmeraldGrassDropsBanked = 6;
    public static float emeraldAccumulationDistance = 16.0f;
    public static int minXpForEmeralds = 3;
    public static float emeraldsPerXP = 0.5f;
    public static float guaranteedEmeraldRatio = 0.75f;
    public static boolean CONFIG_ALLOW_TRINKETS = true;
    public static boolean CONFIG_ALLOW_EMERALD_DROPS = true;
    public static boolean CONFIG_ALLOW_HEART_DROPS = true;
    public static float CONFIG_PICKUP_SOUND_VOLUME = 0.3f;
    public static boolean CONFIG_FANCY_XP;
    public static int CONFIG_MAX_STARWELL_ATTEMPTS;
    public static boolean CONFIG_FALLING_STAR_DAMAGE_ENABLED = true;
    public static float CONFIG_FALLING_STAR_DAMAGE = 25.0f;
    public static float CONFIG_NIGHT_FALLING_STAR_FREQUENCY = 1.0f;
    public static boolean CONFIG_MAGIC_BOOMERANG_INFINITE_DURABILITY = false;
    public static float CONFIG_MAGIC_BOOMERANG_DAMAGE = 6.0f;
    public static boolean CONFIG_ENABLE_BAD_BOW = true;
    public static int CONFIG_DASH_RING_MAX_AIR_JUMPS = 2;
    public static boolean CONFIG_DASH_RING_USE_ORIGINAL_BEHAVIOR = false;
    public static boolean CONFIG_SPRINKLE_STARDUST_REQUIRE_SNEAK = false;
    public static boolean CONFIG_SPOTTING_SCOPE_CONSUMES_MANA = true;
    public static int CONFIG_MANA_REGEN_POTION_ID = 24;
    public static int CONFIG_GROUNDED_POTION_ID = 25;
    public static float CONFIG_MANA_REGEN_POTION_PER_SECOND = 1.0f;
    public static int CONFIG_STONESKIN_RESISTANCE_POTION_ID = VANILLA_POTION_ID_RESISTANCE;
    public static boolean CONFIG_CALTROPS_BREAK_ON_TRIGGER = true;
    public static boolean CONFIG_CALTROPS_TRIGGER_DROP_ENABLED = true;
    public static float CONFIG_CALTROPS_MOB_DAMAGE = 3.0f;
    public static float CONFIG_CALTROPS_PLAYER_DAMAGE_PERCENT = 20.0f;
    public static boolean CONFIG_CALTROPS_UNDERGROUND_GEN_ENABLED = true;
    public static int CONFIG_CALTROPS_UNDERGROUND_SPAWN_CHANCE = 3;
    public static int CONFIG_CALTROPS_UNDERGROUND_MIN_Y = 12;
    public static int CONFIG_CALTROPS_UNDERGROUND_MAX_Y = 48;
    public static int CONFIG_CALTROPS_SLOWNESS_POTION_ID = VANILLA_POTION_ID_SLOWNESS;
    public static int CONFIG_EXIT_CONFUSION_POTION_ID = VANILLA_POTION_ID_CONFUSION;
    public static int CONFIG_ICE_SPELL_SLOWNESS_POTION_ID = VANILLA_POTION_ID_SLOWNESS;
    public static int CONFIG_PHOENIX_REVIVE_RESISTANCE_POTION_ID = VANILLA_POTION_ID_RESISTANCE;
    public static int CONFIG_PHOENIX_REVIVE_REGENERATION_POTION_ID = VANILLA_POTION_ID_REGENERATION;
    public static int CONFIG_PHOENIX_REVIVE_FIRE_RESISTANCE_POTION_ID = VANILLA_POTION_ID_FIRE_RESISTANCE;
    public static int CONFIG_THIEF_RING_INVISIBILITY_POTION_ID = VANILLA_POTION_ID_INVISIBILITY;
    public static int CONFIG_PHOENIX_EMBLEM_FIRE_RESISTANCE_POTION_ID = VANILLA_POTION_ID_FIRE_RESISTANCE;
    public static Potion manaRegenPotion;
    public static Potion jumpPenaltyPotion;
    public static Item.ToolMaterial starglassMaterial;
    public static Item.ToolMaterial starsteelMaterial;
    public static RitualManager ritualManager;
    public static int RITUAL_MODE;
    public static int MANA_DATAWATCHER_ID;
    public static int GLIDE_DATAWATCHER_ID;
    public static SimpleNetworkWrapper snw;
    public static LegendGear2 instance;
    public static CommonProxy proxy;
    public static CreativeTabs legendgearTab;
    public static EmeraldShard emeraldShard;
    public static StarDust starDust;
    public static MilkChocolate milkChocolate;
    public static ReedPipes reedPipes;
    public static FortuneCookie fortuneCookie;
    public static Item starglassIngot;
    public static Item starsteelIngot;
    public static Item starsteelDust;
    public static Item fulgurite;
    public static Item blankSpellbook;
    public static Item sunfireDiamond;
    public static ItemNucleus elementNucleus;
    public static AbstractionGel abstractionGel;
    public static ItemAzurite azurite;
    public static SpiritEmblem spiritEmblem;
    public static CharmPendant charmPendant;
    public static StarglassOrb emptyOrb;
    public static DimensionalCatalyst dimensionalCatalyst;
    public static StaffTwinkle twinkleStaff;
    public static StaffFire fireStaff;
    public static StaffZap zapStaff;
    public static StaffIce iceStaff;
    public static Tome tomeScythewind;
    public static Tome tomeRayfire;
    public static Tome tomeExit;
    public static ItemMagicBoomerang magicBoomerang;
    public static BadBow badBow;
    public static TuningFork tuningFork;
    public static PhoenixFeather phoenixFeather;
    public static MagicRing magicRing;
    public static AzureFeather azureFeather;
    public static SpottingScope spottingScope;
    public static LGRecord recordDragondot;
    public static StarstoneBlock starstoneBlock;
    public static InfusedStarstoneBlock infusedStarstoneBlock;
    public static StarPieceBlock starPieceBlock;
    public static InfusedStarPieceBlock infusedStarPieceBlock;
    public static StarSandBlock starSandBlock;
    public static AzuriteOreBlock azuriteOreBlock;
    public static PhoenixAltar starAltarBlock;
    public static FragstoneBlock fragstoneBlock;
    public static StruckGroundBlock struckGroundBlock;
    public static StarwellFrameBlock starwellFrameBlock;
    public static StarwellBlock starwellBlock;
    public static RitualBlock ritualBlock;
    public static SkylensBlock skylensBlock;
    public static ThawingIceBlock thawingIceBlock;
    public static CaltropsBlock caltropsBlock;
    public static EnchantmentMagicProtection enchMagicProtection;
    public static EnchantmentSpellReach enchSpellReach;
    public static EnchantmentSpellSpread enchSpellSpread;
    public static EnchantmentSpellArmored enchSpellArmored;
    public static int enchMagicProtectionID;
    public static int enchSpellReachID;
    public static int enchSpellSpreadID;
    public static int enchSpellArmoredID;
    public static Configuration config;
    public static Achievement achievementStarGet;
    public static Achievement achievementRitualist;
    public static Achievement achievementMage;
    public static Achievement achievementReadSpellbook;
    public static Achievement achievementPhoenixBoon;
    public static Achievement achievementFortunate;
    public static Achievement achievementBoomerang;
    public static Achievement achievementDayRitual;
    public static Achievement achievementStarsteel;
    public static Achievement achievementAzurite;
    public static Achievement achievementFlight;
    public static Achievement achievementFlyingKill;
    public static Achievement achievementSunfireDiamond;
    public static Fortunes fortunes;

    public static int scanEnchantmentID(int base) {
        int scan = base;
        int watchdog = 0;
        while (Enchantment.enchantmentsList[scan] != null) {
            ++watchdog;
            if (++scan >= 255) {
                scan = 0;
            }
            if (watchdog < 256) continue;
            throw new RuntimeException("Out of enchantment IDs!");
        }
        return scan;
    }

    public static Potion resolveConfiguredPotion(int configuredId, Potion fallback) {
        if (configuredId >= 0 && configuredId < Potion.potionTypes.length) {
            Potion configured = Potion.potionTypes[configuredId];
            if (configured != null) {
                return configured;
            }
        }
        return fallback;
    }

    public static void applyJumpPenalty(EntityLivingBase target, int duration, int reductionSteps, boolean ambient) {
        if (target == null || duration <= 0 || reductionSteps <= 0) {
            return;
        }

        target.removePotionEffect(Potion.jump.id);

        if (LegendGear2.jumpPenaltyPotion != null) {
            target.removePotionEffect(LegendGear2.jumpPenaltyPotion.id);
            target.addPotionEffect(new PotionEffect(LegendGear2.jumpPenaltyPotion.id, duration, reductionSteps - 1, ambient));
            return;
        }

        target.addPotionEffect(new PotionEffect(Potion.jump.id, duration, -(reductionSteps + 1), ambient));
    }

    public static void addConfiguredPotionEffect(EntityLivingBase target, int configuredId, Potion fallback, int duration, int amplifier, boolean ambient) {
        if (target == null) {
            return;
        }
        Potion potion = LegendGear2.resolveConfiguredPotion(configuredId, fallback);
        if (potion == null) {
            return;
        }
        target.addPotionEffect(new PotionEffect(potion.id, duration, amplifier, ambient));
    }

    public static void replaceConfiguredPotionEffect(EntityLivingBase target, int configuredId, Potion fallback, int duration, int amplifier, boolean ambient) {
        if (target == null) {
            return;
        }
        Potion potion = LegendGear2.resolveConfiguredPotion(configuredId, fallback);
        if (potion == null) {
            return;
        }
        target.removePotionEffect(potion.id);
        target.addPotionEffect(new PotionEffect(potion.id, duration, amplifier, ambient));
    }

    public static void addConfiguredPotionEffect(EntityLivingBase target, int configuredId, Potion fallback, int duration) {
        if (target == null) {
            return;
        }
        Potion potion = LegendGear2.resolveConfiguredPotion(configuredId, fallback);
        if (potion == null) {
            return;
        }
        target.addPotionEffect(new PotionEffect(potion.id, duration));
    }

    private static int[] getConfiguredPotionIdList(String key, String legacyKey, int defaultId, String comment) {
        String[] defaults = new String[]{String.valueOf(defaultId)};
        if (!config.hasKey("general", key) && config.hasKey("general", legacyKey)) {
            defaults = new String[]{String.valueOf(config.getInt(legacyKey, "general", defaultId, 0, 255, comment))};
        }
        return ConfigResolver.parseIntegerList(config.getStringList(key, "general", defaults, comment));
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

    public static void syncConfig() {
        enchMagicProtectionID = config.getInt("magicProtectionID", "general", LegendGear2.scanEnchantmentID(110), 0, 255, "Enchantment ID for Magic Protection");
        enchSpellReachID = config.getInt("spellReachID", "general", LegendGear2.scanEnchantmentID(111), 0, 255, "Enchantment ID for Reach");
        enchSpellSpreadID = config.getInt("spellSpreadID", "general", LegendGear2.scanEnchantmentID(112), 0, 255, "Enchantment ID for Spread");
        enchSpellArmoredID = config.getInt("spellArmoredID", "general", LegendGear2.scanEnchantmentID(113), 0, 255, "Enchantment ID for Fortitude");
        CONFIG_ALLOW_EMERALD_DROPS = config.getBoolean("allowEmeraldDrops", "general", true, "allow emerald shard drops from mobs and grass");
        CONFIG_ALLOW_HEART_DROPS = config.getBoolean("allowHeartDrops", "general", true, "allow heart drops from mobs and grass");
        CONFIG_PICKUP_SOUND_VOLUME = config.getFloat("itemSoundVolume", "general", 0.3f, 0.0f, 1.0f, "volume of heart/emerald sounds");
        CONFIG_FANCY_XP = config.getBoolean("fancyExperience", "general", true, "render fancy rainbow experience orbs");
        MANA_DATAWATCHER_ID = config.getInt("manaDatawatcherID", "general", 20, 0, ModConfig.DATAWATCHER_MAX_ID, "ID for player mana datawatcher, change if there's a conflict");
        GLIDE_DATAWATCHER_ID = config.getInt("glideDatawatcherID", "general", 21, 0, ModConfig.DATAWATCHER_MAX_ID, "ID for player glide datawatcher, change if there's a conflict");
        if (GLIDE_DATAWATCHER_ID == MANA_DATAWATCHER_ID) {
            GLIDE_DATAWATCHER_ID = Math.min(ModConfig.DATAWATCHER_MAX_ID, MANA_DATAWATCHER_ID + 1);
            if (GLIDE_DATAWATCHER_ID == MANA_DATAWATCHER_ID) {
                GLIDE_DATAWATCHER_ID = Math.max(0, MANA_DATAWATCHER_ID - 1);
            }
        }
        CONFIG_MAX_STARWELL_ATTEMPTS = config.getInt("maxStarwellRetries", "general", 16, 0, 16, "Times to retry placing a starwell in a chunk if invalid");
        CONFIG_FALLING_STAR_DAMAGE_ENABLED = config.getBoolean("fallingStarDamageEnabled", "general", true, "If true, Falling Stars can deal damage on impact and while descending");
        CONFIG_FALLING_STAR_DAMAGE = config.getFloat("fallingStarDamage", "general", 25.0f, 0.0f, 2048.0f, "Damage dealt by Falling Stars to entities hit mid-air and at impact");
        CONFIG_NIGHT_FALLING_STAR_FREQUENCY = config.getFloat("nightFallingStarFrequency", "general", 1.0f, 0.0f, 64.0f, "Multiplier for natural Falling Star frequency while under open sky at night. 0 disables them, 1 keeps current behavior, 2 makes them happen about twice as often");
        CONFIG_MAGIC_BOOMERANG_INFINITE_DURABILITY = config.getBoolean("magicBoomerangInfiniteDurability", "general", false, "If true, Magic Boomerang does not lose durability");
        CONFIG_MAGIC_BOOMERANG_DAMAGE = config.getFloat("magicBoomerangDamage", "general", 6.0f, 0.0f, 1024.0f, "Damage dealt by the Magic Boomerang on hit");
        CONFIG_ENABLE_BAD_BOW = config.getBoolean("enableBadBow", "general", true, "If false, legendgear:badBow is not registered");
        CONFIG_DASH_RING_MAX_AIR_JUMPS = config.getInt("dashRingMaxAirJumps", "general", 2, 0, 8, "Maximum extra air jumps granted by dash ring behavior");
        CONFIG_DASH_RING_USE_ORIGINAL_BEHAVIOR = config.getBoolean("dashRingUseOriginalBehavior", "general", false, "If true, restores original unlimited dash ring mid-air behavior and ignores dashRingMaxAirJumps");
        CONFIG_SPRINKLE_STARDUST_REQUIRE_SNEAK = config.getBoolean("sprinkleStardustRequireSneak", "general", false, "If true, infused stardust requires sneaking to sprinkle");
        CONFIG_SPOTTING_SCOPE_CONSUMES_MANA = config.getBoolean("spottingScopeConsumesMana", "general", true, "If false, spotting scope pings do not consume mana");
        CONFIG_MANA_REGEN_POTION_ID = config.getInt("manaRegenPotionId", "general", 24, 0, 255, "Potion ID reserved for the LegendGear mana regeneration effect");
        CONFIG_GROUNDED_POTION_ID = config.getInt("groundedPotionId", "general", 25, 0, 255, "Potion ID reserved for the LegendGear Grounded jump-penalty effect");
        CONFIG_MANA_REGEN_POTION_PER_SECOND = config.getFloat("manaRegenPotionManaPerSecond", "general", 1.0f, 0.0f, 100.0f, "Mana restored per second by the LegendGear mana regeneration potion effect at amplifier 0");
        CONFIG_STONESKIN_RESISTANCE_POTION_ID = resolveConfiguredPotionIdList(getConfiguredPotionIdList("stoneskinResistancePotionIds", "stoneskinResistancePotionId", VANILLA_POTION_ID_RESISTANCE, "Potion IDs considered for the Stoneskin ritual. The first ID in the list is used."), VANILLA_POTION_ID_RESISTANCE);
        CONFIG_CALTROPS_BREAK_ON_TRIGGER = config.getBoolean("caltropsBreakOnTrigger", "general", true, "If true, caltrops break when triggered. If false, they stay placed and can trigger again after a short cooldown");
        CONFIG_CALTROPS_TRIGGER_DROP_ENABLED = config.getBoolean("caltropsTriggerDropEnabled", "general", true, "If true, caltrops have their normal random chance to drop as an item when triggered");
        CONFIG_CALTROPS_MOB_DAMAGE = config.getFloat("caltropsMobDamageHearts", "general", 1.5f, 0.0f, 1024.0f, "Damage dealt by caltrops to non-player living entities, measured in hearts") * 2.0f;
        CONFIG_CALTROPS_PLAYER_DAMAGE_PERCENT = config.getFloat("caltropsPlayerDamagePercent", "general", 20.0f, 0.0f, 1000.0f, "Percent of a player's max health dealt by caltrops");
        CONFIG_CALTROPS_UNDERGROUND_GEN_ENABLED = config.getBoolean("caltropsUndergroundGenEnabled", "general", true, "If true, caltrops may generate underground in caves");
        CONFIG_CALTROPS_UNDERGROUND_SPAWN_CHANCE = config.getInt("caltropsUndergroundSpawnChance", "general", 3, 0, 256, "Underground caltrops spawn attempts per chunk when enabled");
        CONFIG_CALTROPS_UNDERGROUND_MIN_Y = config.getInt("caltropsUndergroundMinY", "general", 12, 1, 255, "Minimum Y level for underground caltrops generation");
        CONFIG_CALTROPS_UNDERGROUND_MAX_Y = config.getInt("caltropsUndergroundMaxY", "general", 48, 1, 255, "Maximum Y level for underground caltrops generation");
        CONFIG_CALTROPS_SLOWNESS_POTION_ID = resolveConfiguredPotionIdList(getConfiguredPotionIdList("caltropsSlownessPotionIds", "caltropsSlownessPotionId", VANILLA_POTION_ID_SLOWNESS, "Potion IDs considered by caltrops for movement slowdown. The first ID in the list is used."), VANILLA_POTION_ID_SLOWNESS);
        CONFIG_EXIT_CONFUSION_POTION_ID = resolveConfiguredPotionIdList(getConfiguredPotionIdList("exitConfusionPotionIds", "exitConfusionPotionId", VANILLA_POTION_ID_CONFUSION, "Potion IDs considered for the Exit spell side effect. The first ID in the list is used."), VANILLA_POTION_ID_CONFUSION);
        CONFIG_ICE_SPELL_SLOWNESS_POTION_ID = resolveConfiguredPotionIdList(getConfiguredPotionIdList("iceSpellSlownessPotionIds", "iceSpellSlownessPotionId", VANILLA_POTION_ID_SLOWNESS, "Potion IDs considered by critical Ice spell hits for slowdown. The first ID in the list is used."), VANILLA_POTION_ID_SLOWNESS);
        CONFIG_PHOENIX_REVIVE_RESISTANCE_POTION_ID = resolveConfiguredPotionIdList(getConfiguredPotionIdList("phoenixReviveResistancePotionIds", "phoenixReviveResistancePotionId", VANILLA_POTION_ID_RESISTANCE, "Potion IDs considered by phoenix revival effects for resistance. The first ID in the list is used."), VANILLA_POTION_ID_RESISTANCE);
        CONFIG_PHOENIX_REVIVE_REGENERATION_POTION_ID = resolveConfiguredPotionIdList(getConfiguredPotionIdList("phoenixReviveRegenerationPotionIds", "phoenixReviveRegenerationPotionId", VANILLA_POTION_ID_REGENERATION, "Potion IDs considered by phoenix revival effects for regeneration. The first ID in the list is used."), VANILLA_POTION_ID_REGENERATION);
        CONFIG_PHOENIX_REVIVE_FIRE_RESISTANCE_POTION_ID = resolveConfiguredPotionIdList(getConfiguredPotionIdList("phoenixReviveFireResistancePotionIds", "phoenixReviveFireResistancePotionId", VANILLA_POTION_ID_FIRE_RESISTANCE, "Potion IDs considered by phoenix revival effects for fire resistance. The first ID in the list is used."), VANILLA_POTION_ID_FIRE_RESISTANCE);
        CONFIG_THIEF_RING_INVISIBILITY_POTION_ID = resolveConfiguredPotionIdList(getConfiguredPotionIdList("thiefRingInvisibilityPotionIds", "thiefRingInvisibilityPotionId", VANILLA_POTION_ID_INVISIBILITY, "Potion IDs considered while the Thief Ring is active. The first ID in the list is used."), VANILLA_POTION_ID_INVISIBILITY);
        CONFIG_PHOENIX_EMBLEM_FIRE_RESISTANCE_POTION_ID = resolveConfiguredPotionIdList(getConfiguredPotionIdList("phoenixEmblemFireResistancePotionIds", "phoenixEmblemFireResistancePotionId", VANILLA_POTION_ID_FIRE_RESISTANCE, "Potion IDs considered by Phoenix Emblem interventions for fire resistance. The first ID in the list is used."), VANILLA_POTION_ID_FIRE_RESISTANCE);
        EntityMagicBoomerang.BOOMERANG_DAMAGE = CONFIG_MAGIC_BOOMERANG_DAMAGE;
        if (config.hasChanged()) {
            config.save();
        }
    }

    public void preInit(FMLPreInitializationEvent event) {
        if (ModConfig.config != null && !ModConfig.enableLegendGearModule) {
            return;
        }

        if (ModConfig.config != null) {
            config = ModConfig.config;
            enchMagicProtectionID = ModConfig.legendGearMagicProtectionId;
            enchSpellReachID = ModConfig.legendGearSpellReachId;
            enchSpellSpreadID = ModConfig.legendGearSpellSpreadId;
            enchSpellArmoredID = ModConfig.legendGearSpellArmoredId;
            CONFIG_ALLOW_EMERALD_DROPS = ModConfig.legendGearAllowEmeraldDrops;
            CONFIG_ALLOW_HEART_DROPS = ModConfig.legendGearAllowHeartDrops;
            CONFIG_PICKUP_SOUND_VOLUME = ModConfig.legendGearItemSoundVolume;
            CONFIG_FANCY_XP = ModConfig.legendGearFancyExperience;
            MANA_DATAWATCHER_ID = ModConfig.legendGearManaDatawatcherId;
            GLIDE_DATAWATCHER_ID = ModConfig.legendGearGlideDatawatcherId;
            CONFIG_MAX_STARWELL_ATTEMPTS = ModConfig.legendGearMaxStarwellRetries;
            CONFIG_FALLING_STAR_DAMAGE_ENABLED = ModConfig.legendGearFallingStarDamageEnabled;
            CONFIG_FALLING_STAR_DAMAGE = ModConfig.legendGearFallingStarDamage;
            CONFIG_NIGHT_FALLING_STAR_FREQUENCY = ModConfig.legendGearNightFallingStarFrequency;
            CONFIG_MAGIC_BOOMERANG_INFINITE_DURABILITY = ModConfig.legendGearMagicBoomerangInfiniteDurability;
            CONFIG_MAGIC_BOOMERANG_DAMAGE = ModConfig.legendGearMagicBoomerangDamage;
            CONFIG_ENABLE_BAD_BOW = ModConfig.legendGearEnableBadBow;
            CONFIG_DASH_RING_MAX_AIR_JUMPS = ModConfig.legendGearDashRingMaxAirJumps;
            CONFIG_DASH_RING_USE_ORIGINAL_BEHAVIOR = ModConfig.legendGearDashRingUseOriginalBehavior;
            CONFIG_SPRINKLE_STARDUST_REQUIRE_SNEAK = ModConfig.legendGearSprinkleStardustRequireSneak;
            CONFIG_SPOTTING_SCOPE_CONSUMES_MANA = ModConfig.legendGearSpottingScopeConsumesMana;
            CONFIG_MANA_REGEN_POTION_ID = ModConfig.legendGearManaRegenPotionId;
            CONFIG_GROUNDED_POTION_ID = ModConfig.legendGearGroundedPotionId;
            CONFIG_MANA_REGEN_POTION_PER_SECOND = ModConfig.legendGearManaRegenPotionManaPerSecond;
            CONFIG_STONESKIN_RESISTANCE_POTION_ID = ModConfig.legendGearStoneskinResistancePotionId;
            CONFIG_CALTROPS_SLOWNESS_POTION_ID = ModConfig.legendGearCaltropsSlownessPotionId;
            CONFIG_EXIT_CONFUSION_POTION_ID = ModConfig.legendGearExitConfusionPotionId;
            CONFIG_ICE_SPELL_SLOWNESS_POTION_ID = ModConfig.legendGearIceSpellSlownessPotionId;
            CONFIG_PHOENIX_REVIVE_RESISTANCE_POTION_ID = ModConfig.legendGearPhoenixReviveResistancePotionId;
            CONFIG_PHOENIX_REVIVE_REGENERATION_POTION_ID = ModConfig.legendGearPhoenixReviveRegenerationPotionId;
            CONFIG_PHOENIX_REVIVE_FIRE_RESISTANCE_POTION_ID = ModConfig.legendGearPhoenixReviveFireResistancePotionId;
            CONFIG_THIEF_RING_INVISIBILITY_POTION_ID = ModConfig.legendGearThiefRingInvisibilityPotionId;
            CONFIG_PHOENIX_EMBLEM_FIRE_RESISTANCE_POTION_ID = ModConfig.legendGearPhoenixEmblemFireResistancePotionId;
        }

        if (config == null) {
            config = new Configuration(event.getSuggestedConfigurationFile());
            LegendGear2.syncConfig();
        }
        LegendGearPotions.init();
        EntityMagicBoomerang.BOOMERANG_DAMAGE = CONFIG_MAGIC_BOOMERANG_DAMAGE;
        snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        if (FMLCommonHandler.instance().getSide().isClient()) {
            snw.registerMessage(StarwellMessageHandler.class, StarwellMessage.class, 0, Side.CLIENT);
        }
        enchMagicProtection = new EnchantmentMagicProtection(enchMagicProtectionID, 3);
        enchSpellArmored = new EnchantmentSpellArmored(enchSpellArmoredID, 2);
        enchSpellReach = new EnchantmentSpellReach(enchSpellReachID, 5);
        enchSpellSpread = new EnchantmentSpellSpread(enchSpellSpreadID, 5);
        fortunes = new Fortunes();
        PlayerEventHandler peh = new PlayerEventHandler();
        MinecraftForge.EVENT_BUS.register((Object)peh);
        FMLCommonHandler.instance().bus().register((Object)peh);
        MinecraftForge.EVENT_BUS.register((Object)new CustomAttributes());
        emeraldShard = new EmeraldShard();
        GameRegistry.registerItem((Item)emeraldShard, (String)"emeraldShard");
        FMLCommonHandler.instance().bus().register((Object)emeraldShard);
        MinecraftForge.EVENT_BUS.register((Object)emeraldShard);
        starstoneBlock = new StarstoneBlock();
        GameRegistry.registerBlock((Block)starstoneBlock, (String)"starstoneBlock");
        infusedStarstoneBlock = new InfusedStarstoneBlock();
        GameRegistry.registerBlock((Block)infusedStarstoneBlock, (String)"infusedStarstoneBlock");
        GameRegistry.registerTileEntity(TileEntityStarstone.class, (String)"infusedStarstoneBlock");
        starPieceBlock = new StarPieceBlock();
        GameRegistry.registerBlock((Block)starPieceBlock, (String)"starPieceBlock");
        infusedStarPieceBlock = new InfusedStarPieceBlock();
        GameRegistry.registerBlock((Block)infusedStarPieceBlock, (String)"infusedStarPieceBlock");
        GameRegistry.registerTileEntity(TileEntityPlacedStar.class, (String)"placedStarPiece");
        starDust = new StarDust();
        GameRegistry.registerItem((Item)starDust, (String)"starDust");
        thawingIceBlock = new ThawingIceBlock();
        GameRegistry.registerBlock((Block)thawingIceBlock, (String)"thawingIce");
        starSandBlock = new StarSandBlock();
        GameRegistry.registerBlock((Block)starSandBlock, (String)"starSand");
        fragstoneBlock = new FragstoneBlock();
        struckGroundBlock = new StruckGroundBlock();
        GameRegistry.registerBlock((Block)struckGroundBlock, LgItemBlockMeta.class, (String)"struckGround");
        starAltarBlock = new PhoenixAltar();
        GameRegistry.registerBlock((Block)starAltarBlock, (String)"starAltar");
        GameRegistry.registerTileEntity(TileEntityAltar.class, (String)"starAltar");
        starwellFrameBlock = new StarwellFrameBlock();
        GameRegistry.registerBlock((Block)starwellFrameBlock, (String)"starwellFrame");
        starwellBlock = new StarwellBlock();
        GameRegistry.registerBlock((Block)starwellBlock, LgItemBlockMeta.class, (String)"starwellCore");
        GameRegistry.registerTileEntity(TileEntityStarwell.class, (String)"starwell");
        ritualBlock = new RitualBlock();
        GameRegistry.registerBlock((Block)ritualBlock, (String)"ritualBlock");
        GameRegistry.registerTileEntity(TileEntityRitual.class, (String)"ritualGrid");
        skylensBlock = new SkylensBlock();
        GameRegistry.registerBlock((Block)skylensBlock, (String)"skylensBlock");
        azuriteOreBlock = new AzuriteOreBlock();
        GameRegistry.registerBlock((Block)azuriteOreBlock, (String)"azuriteOre");
        starglassIngot = new LGItem().setUnlocalizedName("ingotStarglass").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("legendgear:starglassAnim");
        GameRegistry.registerItem((Item)starglassIngot, (String)"ingotStarglass");
        starsteelIngot = new LGItem().setUnlocalizedName("ingotStarsteel").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("legendgear:starsteelAnim");
        GameRegistry.registerItem((Item)starsteelIngot, (String)"ingotStarsteel");
        blankSpellbook = new LGItem().setUnlocalizedName("blankSpellbook").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("legendgear:blankSpellbook");
        GameRegistry.registerItem((Item)blankSpellbook, (String)"blankSpellbook");
        starsteelDust = new LGItem().setUnlocalizedName("dustStarsteel").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("legendgear:starsteelDustAnim");
        GameRegistry.registerItem((Item)starsteelDust, (String)"dustStarsteel");
        fulgurite = new LGItem().setUnlocalizedName("fulgurite").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("legendgear:fulgurite");
        GameRegistry.registerItem((Item)fulgurite, (String)"fulgurite");
        sunfireDiamond = new LGItem().setShiny().setUnlocalizedName("sunfireDiamond").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("legendgear:sunfireDiamond");
        GameRegistry.registerItem((Item)sunfireDiamond, (String)"sunfireDiamond");
        LegendGear2.starglassMaterial.customCraftingMaterial = starglassIngot;
        emptyOrb = new StarglassOrb();
        GameRegistry.registerItem((Item)emptyOrb, (String)"emptyOrb");
        dimensionalCatalyst = new DimensionalCatalyst();
        GameRegistry.registerItem((Item)dimensionalCatalyst, (String)"dimensionalCatalyst");
        twinkleStaff = new StaffTwinkle();
        GameRegistry.registerItem((Item)twinkleStaff, (String)"twinkleStaff");
        fireStaff = new StaffFire();
        GameRegistry.registerItem((Item)fireStaff, (String)"fireStaff");
        zapStaff = new StaffZap();
        GameRegistry.registerItem((Item)zapStaff, (String)"zapStaff");
        iceStaff = new StaffIce();
        GameRegistry.registerItem((Item)iceStaff, (String)"iceStaff");
        tomeScythewind = new Tome(Tome.TomeType.SCYTHEWIND);
        GameRegistry.registerItem((Item)tomeScythewind, (String)"tomeScythewind");
        tomeRayfire = new Tome(Tome.TomeType.RAYFIRE);
        GameRegistry.registerItem((Item)tomeRayfire, (String)"tomeRayfire");
        tomeExit = new Tome(Tome.TomeType.EXEUNT);
        GameRegistry.registerItem((Item)tomeExit, (String)"tomeExit");
        charmPendant = new CharmPendant();
        GameRegistry.registerItem((Item)charmPendant, (String)"charmPendant");
        magicBoomerang = new ItemMagicBoomerang();
        GameRegistry.registerItem((Item)magicBoomerang, (String)"magicBoomerang");
        elementNucleus = new ItemNucleus();
        GameRegistry.registerItem((Item)elementNucleus, (String)"nucleus");
        azurite = new ItemAzurite();
        GameRegistry.registerItem((Item)azurite, (String)"azurite");
        abstractionGel = new AbstractionGel();
        GameRegistry.registerItem((Item)abstractionGel, (String)"abstractionGel");
        tuningFork = new TuningFork();
        GameRegistry.registerItem((Item)tuningFork, (String)"tuningFork");
        recordDragondot = new LGRecord("dragondot");
        GameRegistry.registerItem((Item)recordDragondot, (String)"record_dragondot");
        fortuneCookie = new FortuneCookie();
        GameRegistry.registerItem((Item)fortuneCookie, (String)"fortuneCookie");
        phoenixFeather = new PhoenixFeather();
        GameRegistry.registerItem((Item)phoenixFeather, (String)"phoenixFeather");
        azureFeather = new AzureFeather();
        GameRegistry.registerItem((Item)azureFeather, (String)"azureFeather");
        spiritEmblem = new SpiritEmblem();
        GameRegistry.registerItem((Item)spiritEmblem, (String)"spiritEmblem");
        spottingScope = new SpottingScope();
        GameRegistry.registerItem((Item)spottingScope, (String)"spottingScope");
        magicRing = new MagicRing();
        GameRegistry.registerItem((Item)magicRing, (String)"magicRing");
        if (CONFIG_ALLOW_TRINKETS) {
            milkChocolate = new MilkChocolate();
            GameRegistry.registerItem((Item)milkChocolate, (String)"milkChocolate");
            milkChocolate.addRecipes();
            reedPipes = new ReedPipes();
            GameRegistry.registerItem((Item)reedPipes, (String)"reedPipes");
            reedPipes.addRecipes();
            caltropsBlock = new CaltropsBlock();
            GameRegistry.registerBlock((Block)caltropsBlock, ItemCaltropsBlock.class, (String)"caltrops");
            caltropsBlock.addRecipes();
            if (CONFIG_ENABLE_BAD_BOW) {
                badBow = new BadBow();
                GameRegistry.registerItem((Item)badBow, (String)"badBow");
                MinecraftForge.EVENT_BUS.register((Object)badBow);
                badBow.addRecipes();
            }
        }
        starDust.addRecipes();
        starSandBlock.addRecipes();
        emeraldShard.addRecipes();
        abstractionGel.addRecipes();
        elementNucleus.addRecipes();
        tuningFork.addRecipes();
        fortuneCookie.addRecipes();
        azurite.addRecipes();
        charmPendant.addRecipes();
        magicRing.addRecipes();
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)emptyOrb, 8), (Object[])new Object[]{" X ", "X X", " X ", Character.valueOf('X'), starglassIngot});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)dimensionalCatalyst, 16), (Object[])new Object[]{new ItemStack((Item)starDust, 1, 3), Items.ender_pearl});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)magicBoomerang), (Object[])new Object[]{"GSS", "S  ", "S  ", Character.valueOf('G'), starglassIngot, Character.valueOf('S'), starsteelIngot});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)magicBoomerang), (Object[])new Object[]{"SSG", "  S", "  S", Character.valueOf('G'), starglassIngot, Character.valueOf('S'), starsteelIngot});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)magicBoomerang), (Object[])new Object[]{"S  ", "S  ", "GSS", Character.valueOf('G'), starglassIngot, Character.valueOf('S'), starsteelIngot});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)magicBoomerang), (Object[])new Object[]{"  S", "  S", "SSG", Character.valueOf('G'), starglassIngot, Character.valueOf('S'), starsteelIngot});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)twinkleStaff), (Object[])new Object[]{"  O", " S ", "S  ", Character.valueOf('O'), new ItemStack((Item)emptyOrb, 1, StarglassOrb.OrbTypes.twinkle.ordinal()), Character.valueOf('S'), Items.stick});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)fireStaff), (Object[])new Object[]{"  O", " S ", "S  ", Character.valueOf('O'), new ItemStack((Item)emptyOrb, 1, StarglassOrb.OrbTypes.fire.ordinal()), Character.valueOf('S'), Items.stick});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)iceStaff), (Object[])new Object[]{"  O", " S ", "S  ", Character.valueOf('O'), new ItemStack((Item)emptyOrb, 1, StarglassOrb.OrbTypes.ice.ordinal()), Character.valueOf('S'), Items.stick});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)zapStaff), (Object[])new Object[]{"  O", " S ", "S  ", Character.valueOf('O'), new ItemStack((Item)emptyOrb, 1, StarglassOrb.OrbTypes.zap.ordinal()), Character.valueOf('S'), Items.stick});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)spottingScope), (Object[])new Object[]{"  G", " S ", "S  ", Character.valueOf('G'), starglassIngot, Character.valueOf('S'), starsteelIngot});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)spottingScope), (Object[])new Object[]{"G  ", " S ", "  S", Character.valueOf('G'), starglassIngot, Character.valueOf('S'), starsteelIngot});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)tomeScythewind), (Object[])new Object[]{blankSpellbook, new ItemStack((Item)starDust, 1, 4), new ItemStack((Item)elementNucleus, 1, ItemNucleus.NucleusType.SKY.ordinal()), new ItemStack((Item)elementNucleus, 1, ItemNucleus.NucleusType.CUT.ordinal())});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)tomeRayfire), (Object[])new Object[]{blankSpellbook, new ItemStack((Item)starDust, 1, 4), new ItemStack((Item)elementNucleus, 1, ItemNucleus.NucleusType.FIRE.ordinal()), new ItemStack((Item)elementNucleus, 1, ItemNucleus.NucleusType.SUN.ordinal())});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)tomeExit), (Object[])new Object[]{blankSpellbook, new ItemStack((Item)starDust, 1, 4), new ItemStack((Item)elementNucleus, 1, ItemNucleus.NucleusType.SKY.ordinal()), new ItemStack((Item)elementNucleus, 1, ItemNucleus.NucleusType.NAVIGATE.ordinal())});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(starsteelIngot), (Object[])new Object[]{dimensionalCatalyst, Items.iron_ingot, new ItemStack((Item)starDust, 1, 3)});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(blankSpellbook), (Object[])new Object[]{Items.book, new ItemStack(Items.dye, 1, 0), new ItemStack((Item)starDust, 1, 3), phoenixFeather});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)azureFeather), (Object[])new Object[]{Items.feather, new ItemStack((Item)azurite, 1, 0), new ItemStack((Item)starDust, 1, 3)});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)azurite, 3, 1), (Object[])new Object[]{azuriteOreBlock, dimensionalCatalyst});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Block)ritualBlock), (Object[])new Object[]{"SNS", "NSN", "SNS", Character.valueOf('S'), starglassIngot, Character.valueOf('N'), Items.gold_nugget});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Block)skylensBlock), (Object[])new Object[]{"SSS", "SAS", "SSS", Character.valueOf('S'), starglassIngot, Character.valueOf('A'), new ItemStack((Item)azurite, 1, 2)});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)spiritEmblem), (Object[])new Object[]{" G ", "GGG", " G ", Character.valueOf('G'), Items.gold_ingot});
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Block)starAltarBlock), (Object[])new Object[]{"GEG", "GDG", Character.valueOf('G'), Blocks.gold_block, Character.valueOf('E'), new ItemStack((Item)spiritEmblem, 1, 1), Character.valueOf('D'), sunfireDiamond});
        achievementStarGet = new Achievement("achievement.starGet", "starGet", 0, 0, new ItemStack((Item)starDust, 1, 1), (Achievement)null).initIndependentStat().registerStat();
        achievementRitualist = new Achievement("achievement.ritualist", "ritualist", 3, 0, new ItemStack((Block)ritualBlock), achievementStarGet).initIndependentStat().registerStat();
        achievementMage = new Achievement("achievement.mage", "mage", -2, 2, new ItemStack((Item)twinkleStaff), achievementStarGet).initIndependentStat().registerStat();
        achievementReadSpellbook = new Achievement("achievement.literate", "literate", 0, 3, new ItemStack((Item)tomeRayfire), achievementStarGet).initIndependentStat().registerStat();
        achievementPhoenixBoon = new Achievement("achievement.phoenixBoon", "phoenixBoon", 5, -2, new ItemStack((Item)phoenixFeather, 1, 1), achievementRitualist).initIndependentStat().registerStat();
        achievementDayRitual = new Achievement("achievement.dayRitual", "dayRitual", 4, -1, new ItemStack((Item)tuningFork), achievementRitualist).initIndependentStat().registerStat();
        achievementStarsteel = new Achievement("achievement.starsteel", "starsteel", -3, 0, new ItemStack(starsteelIngot), achievementStarGet).initIndependentStat().registerStat();
        achievementFortunate = new Achievement("achievement.fortunate", "fortunate", -8, -8, new ItemStack((Item)fortuneCookie), (Achievement)null).initIndependentStat().registerStat();
        achievementAzurite = new Achievement("achievement.azurite", "achvAzurite", 0, -3, new ItemStack((Item)azurite, 1, 1), achievementStarGet).initIndependentStat().registerStat();
        achievementFlight = new Achievement("achievement.flight", "flight", 0, -5, new ItemStack((Item)azureFeather, 1, 9999), achievementAzurite).initIndependentStat().registerStat().setSpecial();
        achievementSunfireDiamond = new Achievement("achievement.sunfireDiamond", "achvSunfireDiamond", 7, -2, new ItemStack(sunfireDiamond, 1, 9999), achievementPhoenixBoon).initIndependentStat().registerStat().setSpecial();
        AchievementPage.registerAchievementPage((AchievementPage)new AchievementPage("LegendGear 2", new Achievement[]{achievementStarGet, achievementRitualist, achievementMage, achievementReadSpellbook, achievementPhoenixBoon, achievementFortunate, achievementDayRitual, achievementStarsteel, achievementAzurite, achievementFlight, achievementSunfireDiamond}));
        OreDictionary.registerOre((String)"dustStar", (ItemStack)new ItemStack((Item)starDust, 1, 0));
        OreDictionary.registerOre((String)"dustStarInfused", (ItemStack)new ItemStack((Item)starDust, 1, 3));
        OreDictionary.registerOre((String)"gemStar", (ItemStack)new ItemStack((Item)starDust, 1, 1));
        OreDictionary.registerOre((String)"gemStarInfused", (ItemStack)new ItemStack((Item)starDust, 1, 4));
        OreDictionary.registerOre((String)"blockStar", (ItemStack)new ItemStack((Item)starDust, 1, 2));
        OreDictionary.registerOre((String)"blockStarInfused", (ItemStack)new ItemStack((Item)starDust, 1, 5));
        OreDictionary.registerOre((String)"lumpStarglass", (Item)starglassIngot);
        OreDictionary.registerOre((String)"ingotStarsteel", (Item)starsteelIngot);
        OreDictionary.registerOre((String)"dustStarsteel", (Item)starsteelDust);
        GameRegistry.addRecipe((IRecipe)new ShapelessOreRecipe(starsteelDust, new Object[]{"dustStarInfused", "dustIron"}));
        GameRegistry.addSmelting((Item)starsteelDust, (ItemStack)new ItemStack(starsteelIngot), (float)0.0f);
        GameRegistry.addSmelting((Block)starSandBlock, (ItemStack)new ItemStack(starglassIngot), (float)0.0f);
        GameRegistry.addSmelting((ItemStack)new ItemStack((Item)azurite, 1, 0), (ItemStack)new ItemStack((Item)azurite, 1, 1), (float)0.0f);
        emptyOrb.addRecipes();
        StarSpirit.populateOpinions();
    }

    public void init(FMLInitializationEvent event) {
        if (ModConfig.config != null && !ModConfig.enableLegendGearModule) {
            return;
        }
        Object modEntityOwner = com.voidsrift.riftflux.riftflux.instance != null
                ? com.voidsrift.riftflux.riftflux.instance
                : this;
        int id = ENTITY_ID_BASE;
        EntityRegistry.registerModEntity(EntityFallingStar.class, (String)"fallingStar", (int)id++, modEntityOwner, (int)128, (int)10, (boolean)true);
        EntityRegistry.registerModEntity(EntitySpellEffect.class, (String)"entitySpellEffect", (int)id++, modEntityOwner, (int)128, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(SpellDecorator.class, (String)"spellDecorator", (int)id++, modEntityOwner, (int)128, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityThrownOrb.class, (String)"thrownOrb", (int)id++, modEntityOwner, (int)128, (int)5, (boolean)true);
        EntityRegistry.registerModEntity(EntityMagicBoomerang.class, (String)"magicBoomerang", (int)id++, modEntityOwner, (int)128, (int)10, (boolean)true);
        EntityRegistry.registerModEntity(EntityHeart.class, (String)"heartItem", (int)id++, modEntityOwner, (int)128, (int)10, (boolean)true);
        EntityRegistry.registerModEntity(EntityPing.class, (String)"ping", (int)id++, modEntityOwner, (int)1024, (int)10, (boolean)true);
        GameRegistry.registerWorldGenerator((IWorldGenerator)new AzuriteGenerator(), (int)3);
        GameRegistry.registerWorldGenerator((IWorldGenerator)new CaltropsUndergroundGenerator(), (int)3);
        GameRegistry.registerWorldGenerator((IWorldGenerator)new StarwellGenerator(), (int)3);
        ritualManager = new RitualManager();
    }

    public void postInit(FMLPostInitializationEvent event) {
        if (ModConfig.config != null && !ModConfig.enableLegendGearModule) {
            return;
        }
        proxy.registerRenderers();
    }

    static {
        CONFIG_MAX_STARWELL_ATTEMPTS = 16;
        starglassMaterial = EnumHelper.addToolMaterial((String)"STARGLASS", (int)2, (int)13, (float)16.0f, (float)2.0f, (int)0);
        starsteelMaterial = EnumHelper.addToolMaterial((String)"STARSTEEL", (int)2, (int)512, (float)6.0f, (float)2.0f, (int)18);
        RITUAL_MODE = 1;
        MANA_DATAWATCHER_ID = 20;
        GLIDE_DATAWATCHER_ID = 21;
        legendgearTab = new CreativeTabs("lgTab"){

            public Item getTabIconItem() {
                return starDust;
            }
        };
    }
}
