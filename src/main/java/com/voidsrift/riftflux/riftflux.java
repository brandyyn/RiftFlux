package com.voidsrift.riftflux;

import com.voidsrift.riftflux.combat.StickTooltipHandler;
import com.voidsrift.riftflux.chatbubbles.ChatBubbleColorManager;
import com.voidsrift.riftflux.command.CommandRiftFlux;
import com.voidsrift.riftflux.compat.thaumcraft.ThaumcraftWarpSyncCompat;
import com.voidsrift.riftflux.compat.waila.RiftFluxWailaCompat;
import com.voidsrift.riftflux.combat.torohealth.ToroHealthContent;
import com.voidsrift.riftflux.dualhotbar.DualHotbarState;
import com.voidsrift.riftflux.duckling.DucklingContent;
import com.voidsrift.riftflux.inventorypets.InventoryPetsContent;
import com.voidsrift.riftflux.legendgear.LegendGearAdditionsContent;
import com.voidsrift.riftflux.legendgear.LegendGearContent;
import com.voidsrift.riftflux.legendgear.LegendGearLegacyContent;
import com.voidsrift.riftflux.net.sync.SyncEventHandler;
import com.voidsrift.riftflux.specialarmor.SpecialArmorContent;
import com.voidsrift.riftflux.asgardshield.AsgardShieldContent;
import com.voidsrift.riftflux.soulhearts.SoulHeartsContent;
import com.voidsrift.riftflux.heartcrystal.HeartCrystalContent;
import com.voidsrift.riftflux.armoroverlay.ArmorOverlayContent;
import com.voidsrift.riftflux.axolotl.AxolotlContent;
import com.voidsrift.riftflux.levelup.LevelUpContent;
import com.voidsrift.riftflux.morebows.MoreBowsContent;
import com.voidsrift.riftflux.painting.PaintingTooltipHandler;
import com.voidsrift.riftflux.palaria.PalariaMobContent;
import com.voidsrift.riftflux.placeablegunpowder.PlaceableGunpowderContent;
import com.voidsrift.riftflux.pumpkinpastures.PumpkinPasturesContent;
import com.voidsrift.riftflux.offlawn.OffLawnContent;
import com.voidsrift.riftflux.riftexplorer.RiftExplorerContent;
import com.voidsrift.riftflux.server.CelestialFogEventServerEvents;
import com.voidsrift.riftflux.spawning.ConfiguredMobSpawns;
import com.voidsrift.riftflux.spawning.SpawnTypeMobCapHandler;
import com.voidsrift.riftflux.terramine.TerrariaContent;
import com.voidsrift.riftflux.vortex.vortexContent;
import com.voidsrift.riftflux.blessings.BlessingContent;
import com.voidsrift.riftflux.furniture.FurnitureContent;
import com.voidsrift.riftflux.glowstonedust.GlowstoneDustContent;
import com.voidsrift.riftflux.gokistats.GokiStatsContent;
import com.voidsrift.riftflux.wam.WAMContent;
import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
import com.voidsrift.riftflux.util.LegacyRegistryAliasHelper;
import de.rinonline.korinrpg.Springmain;
import zelda.Core;
import com.zyin.zyinhud.ZyinHUD;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.Loader;
import makamys.satchels.SatchelsItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.nmccoy.legendgear.LegendGear2;
import net.minecraftforge.common.MinecraftForge;
import java.util.Map;
import java.util.Locale;

@Mod(modid = Constants.MODID, version = Constants.VERSION)
public class riftflux {
    private static boolean legacyRegistryAliasesRegistered;

    @Instance(Constants.MODID)
    public static riftflux instance;

    @SidedProxy(clientSide = "com.voidsrift.riftflux.client.ClientProxy",
            serverSide = "com.voidsrift.riftflux.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        vortexContent.preInit(event);
        com.voidsrift.riftflux.placeditem.PlacedItemContent.init();
        PlaceableGunpowderContent.preInit();
        GlowstoneDustContent.preInit();
        com.voidsrift.riftflux.avatar.AvatarTLBContent.preInit();
        makamys.satchels.Satchels.preInit(event);
        Core.preInit(event);
        if (ModConfig.dssEnabled) {
            Springmain.preInit(event);
        }
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ZyinHUD.preInit(event);
            proxy.preInitClientFeatures();
        }
        BlessingContent.preInit(event);
        FurnitureContent.preInit(event);
        AxolotlContent.preInit(event);
        DucklingContent.preInit(event);
        TerrariaContent.preInit(event);
        WheatfieldContent.preInit(event);
        WAMContent.preInit(event);
        OffLawnContent.preInit(event);
        PumpkinPasturesContent.preInit(event);
        GokiStatsContent.preInit(event);
        RiftExplorerContent.preInit(event);
        MoreBowsContent.preInit(event);
        PalariaMobContent.preInit(event);
        SpecialArmorContent.preInit(event);
        InventoryPetsContent.preInit(event);
        LegendGearContent.preInit(event);
        AsgardShieldContent.preInit(event);
        SoulHeartsContent.preInit(event);
        HeartCrystalContent.preInit(event);
        ArmorOverlayContent.preInit(event);
        LevelUpContent.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        com.voidsrift.riftflux.net.RFNetwork.init();
        if (Loader.isModLoaded("Waila")) {
            RiftFluxWailaCompat.register();
        }
        ToroHealthContent.init(event);
        BlessingContent.init();
        FurnitureContent.init();
        AxolotlContent.init(event);
        DucklingContent.init(event);

        com.voidsrift.riftflux.tweaks.ladder.RiftFluxLadderContent.init();

        final com.voidsrift.riftflux.server.PickupStarServerEvents serverEvents =
                new com.voidsrift.riftflux.server.PickupStarServerEvents();
        MinecraftForge.EVENT_BUS.register(serverEvents);
        FMLCommonHandler.instance().bus().register(serverEvents);
        if (ModConfig.enableFenceTextureModule) {
            final com.voidsrift.riftflux.fence.FenceOverrideEvents fenceOverrideEvents =
                    new com.voidsrift.riftflux.fence.FenceOverrideEvents();
            MinecraftForge.EVENT_BUS.register(fenceOverrideEvents);
            FMLCommonHandler.instance().bus().register(fenceOverrideEvents);
        }
        final CelestialFogEventServerEvents celestialFogEvents = new CelestialFogEventServerEvents();
        MinecraftForge.EVENT_BUS.register(celestialFogEvents);
        FMLCommonHandler.instance().bus().register(celestialFogEvents);
        final SyncEventHandler syncEventHandler = new SyncEventHandler();
        MinecraftForge.EVENT_BUS.register(syncEventHandler);
        FMLCommonHandler.instance().bus().register(syncEventHandler);
        ChatBubbleColorManager.bootstrapServer();
        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.util.RFPlantContextEvents());
        final com.voidsrift.riftflux.server.ChestLaunchEvents chestLaunchEvents =
                new com.voidsrift.riftflux.server.ChestLaunchEvents();
        MinecraftForge.EVENT_BUS.register(chestLaunchEvents);
        FMLCommonHandler.instance().bus().register(chestLaunchEvents);
        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.server.PlayerHurtSoundEventHandler());
        if (Loader.isModLoaded("Thaumcraft")) {
            final ThaumcraftWarpSyncCompat thaumcraftWarpSyncCompat = new ThaumcraftWarpSyncCompat();
            MinecraftForge.EVENT_BUS.register(thaumcraftWarpSyncCompat);
            FMLCommonHandler.instance().bus().register(thaumcraftWarpSyncCompat);
        }

        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.tweaks.ladder.FloatingLadderEvents());
        final com.voidsrift.riftflux.avatar.glider.GliderEvents gliderEvents =
                new com.voidsrift.riftflux.avatar.glider.GliderEvents();
        MinecraftForge.EVENT_BUS.register(gliderEvents);
        FMLCommonHandler.instance().bus().register(gliderEvents);

        MinecraftForge.EVENT_BUS.register(new StickTooltipHandler());
        MinecraftForge.EVENT_BUS.register(new PaintingTooltipHandler());

        vortexContent.init(event);
        makamys.satchels.Satchels.init(event);
        Core.init(event);
        if (ModConfig.dssEnabled) {
            Springmain.init(event);
        }
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ZyinHUD.init(event);
        }

        proxy.initClientFeatures();
        TerrariaContent.init(event);
        WheatfieldContent.init(event);
        WAMContent.init(event);
        OffLawnContent.init(event);
        PumpkinPasturesContent.init(event);
        GokiStatsContent.init(event);
        RiftExplorerContent.init(event);
        PalariaMobContent.init(event);
        SpecialArmorContent.init(event);
        InventoryPetsContent.init(event);
        LegendGearContent.init(event);
        AsgardShieldContent.init(event);
        SoulHeartsContent.init(event);
        HeartCrystalContent.init(event);
        ArmorOverlayContent.init(event);
        LevelUpContent.init(event);
        ConfiguredMobSpawns.init();
        registerLegacyRegistryAliases();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        vortexContent.postInit(event);
        makamys.satchels.Satchels.postInit(event);
        Core.postInit(event);
        if (ModConfig.dssEnabled) {
            Springmain.postInit(event);
        }
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ZyinHUD.postInit(event);
        }
        LegendGearContent.postInit(event);
        SoulHeartsContent.postInit(event);
        HeartCrystalContent.postInit(event);
        AxolotlContent.postInit(event);
        GokiStatsContent.postInit(event);
        RiftExplorerContent.postInit(event);
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        if (ModConfig.useSpawnTypeForMobCap) {
            SpawnTypeMobCapHandler.prewarm();
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ZyinHUD.serverStarting(event);
        }
        if (ModConfig.dssEnabled) {
            Springmain.serverLoad(event);
        }
        event.registerServerCommand(new CommandRiftFlux());
        event.registerServerCommand(CommandRiftFlux.chatBubbleCommand());
        event.registerServerCommand(CommandRiftFlux.chatBubbleTextCommand());
        event.registerServerCommand(CommandRiftFlux.chatBubbleSizeCommand());
        BlessingContent.serverStarting(event);
        GokiStatsContent.serverStarting(event);
    }

    @EventHandler
    public void onMissingMappings(FMLMissingMappingsEvent event) {
        boolean legendGearEnabled = LegendGearContent.isEnabled();

        for (FMLMissingMappingsEvent.MissingMapping mapping : event.getAll()) {
            if (mapping == null || mapping.name == null) {
                continue;
            }

            if (mapping.type == GameRegistry.Type.ITEM) {
                Item target = null;
                target = resolvePlaceableGunpowderItemAlias(mapping.name);
                if (target == null) {
                    target = resolveGlowstoneDustItemAlias(mapping.name);
                }
                if (target == null) {
                    target = LevelUpContent.resolveLegacyItemAlias(mapping.name);
                }
                if (target == null && legendGearEnabled && isLegendGearNamespace(mapping.name)) {
                    target = resolveLegendGearItemAlias(mapping.name);
                }
                if (target == null) {
                    target = resolveSatchelsItemAlias(mapping.name);
                }
                if (target == null) {
                    target = resolveSpecialArmorItemAlias(mapping.name);
                }
                if (target == null) {
                    target = resolveInventoryPetsItemAlias(mapping.name);
                }
                if (target == null) {
                    target = resolveGhibliItemAlias(mapping.name);
                }
                if (target != null) {
                    mapping.remap(target);
                }
            } else if (mapping.type == GameRegistry.Type.BLOCK) {
                Block target = resolvePlaceableGunpowderBlockAlias(mapping.name);
                if (target == null) {
                    target = resolveGlowstoneDustBlockAlias(mapping.name);
                }
                if (target == null && legendGearEnabled && isLegendGearNamespace(mapping.name)) {
                    target = resolveLegendGearBlockAlias(mapping.name);
                }
                if (target != null) {
                    mapping.remap(target);
                }
            }
        }
    }

    private static boolean isLegendGearNamespace(String fullName) {
        if (fullName == null) {
            return false;
        }
        String lower = fullName.toLowerCase(Locale.ROOT);
        return lower.startsWith("legendgear:")
                || lower.startsWith("legendgear2:")
                || lower.startsWith("legendgearreturns:");
    }

    private static String legendGearAliasKey(String fullName) {
        int split = fullName.indexOf(':');
        String path = split >= 0 ? fullName.substring(split + 1) : fullName;
        return path.toLowerCase(Locale.ROOT);
    }

    private static Item resolveLegendGearItemAlias(String fullName) {
        Item additionsItem = LegendGearAdditionsContent.resolveLegacyItemAlias(fullName);
        if (additionsItem != null) {
            return additionsItem;
        }
        String key = legendGearAliasKey(fullName);
        switch (key) {
            case "emeraldshard":
                return LegendGear2.emeraldShard;
            case "stardust":
                return LegendGear2.starDust;
            case "ingotstarglass":
                return LegendGear2.starglassIngot;
            case "ingotstarsteel":
                return LegendGear2.starsteelIngot;
            case "blankspellbook":
                return LegendGear2.blankSpellbook;
            case "duststarsteel":
                return LegendGear2.starsteelDust;
            case "fulgurite":
                return LegendGear2.fulgurite;
            case "sunfirediamond":
                return LegendGear2.sunfireDiamond;
            case "emptyorb":
                return LegendGear2.emptyOrb;
            case "dimensionalcatalyst":
                return LegendGear2.dimensionalCatalyst;
            case "twinklestaff":
                return LegendGear2.twinkleStaff;
            case "firestaff":
                return LegendGear2.fireStaff;
            case "zapstaff":
                return LegendGear2.zapStaff;
            case "icestaff":
                return LegendGear2.iceStaff;
            case "tomescythewind":
                return LegendGear2.tomeScythewind;
            case "tomerayfire":
                return LegendGear2.tomeRayfire;
            case "tomeexit":
                return LegendGear2.tomeExit;
            case "charmpendant":
                return LegendGear2.charmPendant;
            case "magicboomerang":
                return LegendGear2.magicBoomerang;
            case "nucleus":
                return LegendGear2.elementNucleus;
            case "azurite":
                return LegendGear2.azurite;
            case "abstractiongel":
                return LegendGear2.abstractionGel;
            case "tuningfork":
                return LegendGear2.tuningFork;
            case "record_dragondot":
                return LegendGear2.recordDragondot;
            case "fortunecookie":
                return LegendGear2.fortuneCookie;
            case "phoenixfeather":
                return LegendGear2.phoenixFeather;
            case "azurefeather":
                return LegendGear2.azureFeather;
            case "spiritemblem":
                return LegendGear2.spiritEmblem;
            case "spottingscope":
                return LegendGear2.spottingScope;
            case "magicring":
                return LegendGear2.magicRing;
            case "milkchocolate":
                return LegendGear2.milkChocolate;
            case "reedpipes":
                return LegendGear2.reedPipes;
            case "badbow":
                return LegendGear2.badBow;
            default:
                Block block = resolveLegendGearBlockAlias(fullName);
                return block == null ? null : Item.getItemFromBlock(block);
        }
    }

    private static Block resolveLegendGearBlockAlias(String fullName) {
        Block additionsBlock = LegendGearAdditionsContent.resolveLegacyBlockAlias(fullName);
        if (additionsBlock != null) {
            return additionsBlock;
        }
        String key = legendGearAliasKey(fullName);
        switch (key) {
            case "starstoneblock":
                return LegendGear2.starstoneBlock;
            case "infusedstarstoneblock":
                return LegendGear2.infusedStarstoneBlock;
            case "thawingice":
                return LegendGear2.thawingIceBlock;
            case "starsand":
                return LegendGear2.starSandBlock;
            case "struckground":
                return LegendGear2.struckGroundBlock;
            case "staraltar":
            case "staraltarblock":
                return LegendGear2.starAltarBlock;
            case "starwellframe":
                return LegendGear2.starwellFrameBlock;
            case "starwellcore":
            case "starwellblock":
                return LegendGear2.starwellBlock;
            case "ritualblock":
                return LegendGear2.ritualBlock;
            case "skylensblock":
                return LegendGear2.skylensBlock;
            case "azuriteore":
                return LegendGear2.azuriteOreBlock;
            case "caltrops":
                return LegendGear2.caltropsBlock;
            default:
                return null;
        }
    }

    private static String satchelsAliasKey(String fullName) {
        int split = fullName.indexOf(':');
        String path = split >= 0 ? fullName.substring(split + 1) : fullName;
        return path.toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "")
                .replace(".", "");
    }

    private static Item resolveSatchelsItemAlias(String fullName) {
        String lower = fullName.toLowerCase(Locale.ROOT);
        if (!(lower.startsWith("satchels:") || lower.startsWith("riftflux:"))) {
            return null;
        }

        String key = satchelsAliasKey(lower);
        switch (key) {
            case "satchel":
            case "satchelitem":
            case "itemsatchel":
            case "itemsatchelitem":
            case "itemsatchelsatchel":
            case "satchelssatchel":
                return SatchelsItems.satchel;
            case "pouch":
            case "pouchitem":
            case "itempouch":
            case "itemsatchelspouch":
            case "satchelspouch":
                return SatchelsItems.pouch;
            case "pouchupgrade":
            case "satchelupgrade":
            case "itempouchupgrade":
            case "upgradepouch":
            case "pouchexpansion":
            case "itemsatchelspouchupgrade":
            case "satchelspouchupgrade":
                return SatchelsItems.pouch_upgrade;
            default:
                return null;
        }
    }

    private static String placeableGunpowderAliasKey(String fullName) {
        int split = fullName.indexOf(':');
        String path = split >= 0 ? fullName.substring(split + 1) : fullName;
        return path.toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "")
                .replace(".", "");
    }

    private static Item resolvePlaceableGunpowderItemAlias(String fullName) {
        Block block = resolvePlaceableGunpowderBlockAlias(fullName);
        return block == null ? null : Item.getItemFromBlock(block);
    }

    private static Block resolvePlaceableGunpowderBlockAlias(String fullName) {
        if (PlaceableGunpowderContent.gunpowderBlock == null || fullName == null) {
            return null;
        }

        String lower = fullName.toLowerCase(Locale.ROOT);
        if (!(lower.startsWith("gunpowder:") || "gunpowder_block".equals(lower) || "gunpowderblock".equals(lower))) {
            return null;
        }

        if ("gunpowderblock".equals(placeableGunpowderAliasKey(fullName))) {
            return PlaceableGunpowderContent.gunpowderBlock;
        }
        return null;
    }

    private static String glowstoneDustAliasKey(String fullName) {
        int split = fullName.indexOf(':');
        String path = split >= 0 ? fullName.substring(split + 1) : fullName;
        return path.toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "")
                .replace(".", "");
    }

    private static Item resolveGlowstoneDustItemAlias(String fullName) {
        Block block = resolveGlowstoneDustBlockAlias(fullName);
        return block == null ? null : Item.getItemFromBlock(block);
    }

    private static Block resolveGlowstoneDustBlockAlias(String fullName) {
        if (GlowstoneDustContent.glowstoneDustBlock == null || fullName == null) {
            return null;
        }

        String lower = fullName.toLowerCase(Locale.ROOT);
        if (!(lower.startsWith("grygrflzr_glowstonewire:")
                || lower.startsWith("glowstonedust:")
                || lower.startsWith("glowstonewire:")
                || "glowstone_wire".equals(lower)
                || "glowstone_dust".equals(lower)
                || "glowstonewire".equals(lower)
                || "glowstonedust".equals(lower))) {
            return null;
        }

        String key = glowstoneDustAliasKey(fullName);
        if ("glowstonewire".equals(key) || "glowstonedust".equals(key)) {
            return GlowstoneDustContent.glowstoneDustBlock;
        }
        return null;
    }

    private static Item resolveSpecialArmorItemAlias(String fullName) {
        if (fullName == null) {
            return null;
        }

        String lower = fullName.toLowerCase(Locale.ROOT);
        if (!(lower.startsWith("specialarmor:") || lower.startsWith("tlspecialarmor:") || lower.startsWith("riftflux:"))) {
            return null;
        }

        String key = satchelsAliasKey(fullName);
        if ("slimehelmet".equals(key) || "saslimehelmet".equals(key)) {
            return SpecialArmorContent.slimeHelmet;
        }
        if ("doublejumpboots".equals(key) || "sadoublejumpboots".equals(key)) {
            return SpecialArmorContent.doubleJumpBoots;
        }
        if ("skates".equals(key) || "saskates".equals(key)) {
            return SpecialArmorContent.skates;
        }
        if ("heavyboots".equals(key) || "saheavyboots".equals(key)) {
            return SpecialArmorContent.heavyBoots;
        }
        return null;
    }

    private static Item resolveInventoryPetsItemAlias(String fullName) {
        return InventoryPetsContent.resolveLegacyItemAlias(fullName);
    }

    private static Item resolveGhibliItemAlias(String fullName) {
        if (fullName == null) {
            return null;
        }
        String lower = fullName.toLowerCase(Locale.ROOT);
        if (!(lower.startsWith("duckling:") || lower.startsWith("sootspritecraft:"))) {
            return null;
        }
        String key = lower.substring(lower.indexOf(':') + 1);
        if ("raw_duck".equals(key)) {
            return DucklingContent.rawDuck;
        }
        if ("cooked_duck".equals(key)) {
            return DucklingContent.cookedDuck;
        }
        if ("duck_egg".equals(key)) {
            return DucklingContent.duckEgg;
        }
        if ("duck_spawn_egg".equals(key)) {
            return DucklingContent.duckSpawnEgg;
        }
        if ("quackling_spawn_egg".equals(key)) {
            return DucklingContent.quacklingSpawnEgg;
        }
        if ("star_candy".equals(key)) {
            return DucklingContent.starCandy;
        }
        if ("soot_jar".equals(key)) {
            return DucklingContent.sootJar;
        }
        if ("soot_sprite_spawn_egg".equals(key)) {
            return DucklingContent.sootSpriteSpawnEgg;
        }
        return null;
    }

    private static void registerLegacyRegistryAliases() {
        if (legacyRegistryAliasesRegistered) {
            return;
        }
        legacyRegistryAliasesRegistered = true;

        registerLevelUpLegacyAliases();
        registerLegendGearLegacyAliases();
        registerSatchelsLegacyAliases();
        registerSpecialArmorLegacyAliases();
        registerPlaceableGunpowderLegacyAliases();
        registerGlowstoneDustLegacyAliases();
        InventoryPetsContent.registerLegacyItemAliases();
        DucklingContent.registerLegacyAliases();
    }

    private static void registerLevelUpLegacyAliases() {
        registerItemAliasVariants(LevelUpContent.resolveLegacyItemAlias("levelup:xpTalisman"),
                "levelup:xpTalisman",
                "levelup:Talisman of Wonder");
        registerItemAliasVariants(LevelUpContent.resolveLegacyItemAlias("levelup:respecBook"),
                "levelup:respecBook",
                "levelup:Book of Unlearning");
    }

    private static void registerLegendGearLegacyAliases() {
        String[] itemAliases = {
                "emeraldShard",
                "starDust",
                "ingotStarglass",
                "ingotStarsteel",
                "blankSpellbook",
                "dustStarsteel",
                "fulgurite",
                "sunfireDiamond",
                "emptyOrb",
                "dimensionalCatalyst",
                "twinkleStaff",
                "fireStaff",
                "zapStaff",
                "iceStaff",
                "tomeScythewind",
                "tomeRayfire",
                "tomeExit",
                "charmPendant",
                "magicBoomerang",
                "nucleus",
                "azurite",
                "abstractionGel",
                "tuningFork",
                "record_dragondot",
                "fortuneCookie",
                "phoenixFeather",
                "azureFeather",
                "spiritEmblem",
                "spottingScope",
                "magicRing",
                "milkChocolate",
                "reedPipes",
                "badBow"
        };
        String[] blockAliases = {
                "starstoneBlock",
                "infusedStarstoneBlock",
                "thawingIce",
                "starSand",
                "struckGround",
                "starAltar",
                "starwellFrame",
                "starwellCore",
                "ritualBlock",
                "skylensBlock",
                "azuriteOre",
                "caltrops"
        };
        for (String namespace : new String[]{"legendgear", "legendgear2", "legendgearreturns"}) {
            for (String itemAlias : itemAliases) {
                registerItemAliasVariants(resolveLegendGearItemAlias(namespace + ":" + itemAlias), namespace + ":" + itemAlias);
            }
            for (String blockAlias : blockAliases) {
                Block block = resolveLegendGearBlockAlias(namespace + ":" + blockAlias);
                registerBlockAliasVariants(block, namespace + ":" + blockAlias);
                registerItemAliasVariants(block == null ? null : Item.getItemFromBlock(block), namespace + ":" + blockAlias);
            }
        }
        LegendGearAdditionsContent.registerLegacyAliases();
        LegendGearLegacyContent.registerLegacyAliases();
    }

    private static void registerSatchelsLegacyAliases() {
        registerItemAliasVariants(SatchelsItems.satchel,
                "satchels:satchel",
                "satchels:satchelitem");
        registerItemAliasVariants(SatchelsItems.pouch,
                "satchels:pouch",
                "satchels:pouchitem");
        registerItemAliasVariants(SatchelsItems.pouch_upgrade,
                "satchels:pouch_upgrade",
                "satchels:pouchUpgrade",
                "satchels:pouchupgrade",
                "satchels:satchelupgrade");
    }

    private static void registerSpecialArmorLegacyAliases() {
        for (String namespace : new String[]{"specialarmor", "tlspecialarmor"}) {
            registerItemAliasVariants(SpecialArmorContent.slimeHelmet, namespace + ":SAslimehelmet", namespace + ":slimehelmet");
            registerItemAliasVariants(SpecialArmorContent.doubleJumpBoots, namespace + ":SAdoublejumpboots", namespace + ":doublejumpboots");
            registerItemAliasVariants(SpecialArmorContent.skates, namespace + ":SAskates", namespace + ":skates");
            registerItemAliasVariants(SpecialArmorContent.heavyBoots, namespace + ":SAheavyboots", namespace + ":heavyboots");
        }
    }

    private static void registerPlaceableGunpowderLegacyAliases() {
        Block block = resolvePlaceableGunpowderBlockAlias("gunpowder:gunpowderBlock");
        Item item = block == null ? null : Item.getItemFromBlock(block);
        registerBlockAliasVariants(block,
                "gunpowder:gunpowderBlock",
                "gunpowder:gunpowder_block");
        registerItemAliasVariants(item,
                "gunpowder:gunpowderBlock",
                "gunpowder:gunpowder_block");
    }

    private static void registerGlowstoneDustLegacyAliases() {
        Block block = resolveGlowstoneDustBlockAlias("glowstonewire:glowstone_wire");
        Item item = block == null ? null : Item.getItemFromBlock(block);
        registerBlockAliasVariants(block,
                "grygrflzr_glowstonewire:glowstone_wire",
                "glowstonewire:glowstone_wire",
                "glowstonedust:glowstoneDust");
        registerItemAliasVariants(item,
                "grygrflzr_glowstonewire:glowstone_wire",
                "glowstonewire:glowstone_wire",
                "glowstonedust:glowstoneDust");
    }

    private static void registerItemAliasVariants(Item item, String... aliases) {
        if (item == null || aliases == null) {
            return;
        }
        for (String alias : aliases) {
            if (alias == null || alias.isEmpty()) {
                continue;
            }
            LegacyRegistryAliasHelper.registerItemAliases(item, alias, alias.toLowerCase(Locale.ROOT));
        }
    }

    private static void registerBlockAliasVariants(Block block, String... aliases) {
        if (block == null || aliases == null) {
            return;
        }
        for (String alias : aliases) {
            if (alias == null || alias.isEmpty()) {
                continue;
            }
            LegacyRegistryAliasHelper.registerBlockAliases(block, alias, alias.toLowerCase(Locale.ROOT));
        }
    }

    @NetworkCheckHandler
    public boolean checkRemote(Map<String, String> modList, Side side) {
        if (side == Side.CLIENT) {
            DualHotbarState.installedOnServer = modList != null && modList.containsKey(Constants.MODID);
        }
        return true;
    }
    
}
