package com.voidsrift.riftflux;

import com.voidsrift.riftflux.combat.StickTooltipHandler;
import com.voidsrift.riftflux.compat.thaumcraft.ThaumcraftWarpSyncCompat;
import com.voidsrift.riftflux.combat.torohealth.ToroHealthContent;
import com.voidsrift.riftflux.dualhotbar.DualHotbarState;
import com.voidsrift.riftflux.legendgear.LegendGearContent;
import com.voidsrift.riftflux.specialarmor.SpecialArmorContent;
import com.voidsrift.riftflux.asgardshield.AsgardShieldContent;
import com.voidsrift.riftflux.soulhearts.SoulHeartsContent;
import com.voidsrift.riftflux.heartcrystal.HeartCrystalContent;
import com.voidsrift.riftflux.armoroverlay.ArmorOverlayContent;
import com.voidsrift.riftflux.axolotl.AxolotlContent;
import com.voidsrift.riftflux.painting.PaintingTooltipHandler;
import com.voidsrift.riftflux.placeablegunpowder.PlaceableGunpowderContent;
import com.voidsrift.riftflux.terramine.TerrariaContent;
import com.voidsrift.riftflux.vortex.vortexContent;
import com.voidsrift.riftflux.blessings.BlessingContent;
import com.voidsrift.riftflux.furniture.FurnitureContent;
import com.voidsrift.riftflux.wam.WAMContent;
import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
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
        com.voidsrift.riftflux.avatar.AvatarTLBContent.preInit();
        makamys.satchels.Satchels.preInit(event);
        Core.preInit(event);
        if (ModConfig.dssEnabled) {
            Springmain.preInit(event);
        }
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ZyinHUD.preInit(event);
        }
        BlessingContent.preInit(event);
        FurnitureContent.preInit(event);
        AxolotlContent.preInit(event);
        TerrariaContent.preInit(event);
        WheatfieldContent.preInit(event);
        WAMContent.preInit(event);
        SpecialArmorContent.preInit(event);
        LegendGearContent.preInit(event);
        AsgardShieldContent.preInit(event);
        SoulHeartsContent.preInit(event);
        HeartCrystalContent.preInit(event);
        ArmorOverlayContent.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        com.voidsrift.riftflux.net.RFNetwork.init();
        ToroHealthContent.init(event);
        BlessingContent.init();
        FurnitureContent.init();
        AxolotlContent.init(event);

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
        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.server.ChestLaunchEvents());
        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.server.PlayerHurtSoundEventHandler());
        if (Loader.isModLoaded("Thaumcraft")) {
            MinecraftForge.EVENT_BUS.register(new ThaumcraftWarpSyncCompat());
        }

        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.tweaks.ladder.FloatingLadderEvents());
        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.avatar.glider.GliderEvents());

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
        SpecialArmorContent.init(event);
        LegendGearContent.init(event);
        AsgardShieldContent.init(event);
        SoulHeartsContent.init(event);
        HeartCrystalContent.init(event);
        ArmorOverlayContent.init(event);
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
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ZyinHUD.serverStarting(event);
        }
        if (ModConfig.dssEnabled) {
            Springmain.serverLoad(event);
        }
        BlessingContent.serverStarting(event);
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
                if (target == null && legendGearEnabled && isLegendGearNamespace(mapping.name)) {
                    target = resolveLegendGearItemAlias(mapping.name);
                }
                if (target == null) {
                    target = resolveSatchelsItemAlias(mapping.name);
                }
                if (target == null) {
                    target = resolveSpecialArmorItemAlias(mapping.name);
                }
                if (target != null) {
                    mapping.remap(target);
                }
            } else if (mapping.type == GameRegistry.Type.BLOCK) {
                Block target = resolvePlaceableGunpowderBlockAlias(mapping.name);
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

    @NetworkCheckHandler
    public boolean checkRemote(Map<String, String> modList, Side side) {
        if (side == Side.CLIENT) {
            DualHotbarState.installedOnServer = modList != null && modList.containsKey(Constants.MODID);
        }
        return true;
    }
    
}
