package com.voidsrift.riftflux.legendgear;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.LegacyRegistryAliasHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.legacy.BombFlowerGenerator;
import net.nmccoy.legendgear.legacy.CommonProxy;
import net.nmccoy.legendgear.legacy.LegendGear;
import net.nmccoy.legendgear.legacy.ShrubGenerator;
import net.nmccoy.legendgear.legacy.blocks.BlockBombFlower;
import net.nmccoy.legendgear.legacy.blocks.BlockChained;
import net.nmccoy.legendgear.legacy.blocks.BlockGrindNode;
import net.nmccoy.legendgear.legacy.blocks.BlockJar;
import net.nmccoy.legendgear.legacy.blocks.BlockSkybeam;
import net.nmccoy.legendgear.legacy.blocks.BlockSugarCube;
import net.nmccoy.legendgear.legacy.blocks.BlockSwordPedestal;
import net.nmccoy.legendgear.legacy.blocks.BlockSwordPedestalTechnical;
import net.nmccoy.legendgear.legacy.blocks.MysticShrub;
import net.nmccoy.legendgear.legacy.blocks.ShrubItemBlock;
import net.nmccoy.legendgear.legacy.blocks.TileEntityJar;
import net.nmccoy.legendgear.legacy.blocks.TileEntityPedestal;
import net.nmccoy.legendgear.legacy.blocks.TileEntitySkybeam;
import net.nmccoy.legendgear.legacy.entities.EntityArrowStorm;
import net.nmccoy.legendgear.legacy.entities.EntityBomb;
import net.nmccoy.legendgear.legacy.entities.EntityBombBlast;
import net.nmccoy.legendgear.legacy.entities.EntityEarthMedallion;
import net.nmccoy.legendgear.legacy.entities.EntityEnderBomb;
import net.nmccoy.legendgear.legacy.entities.EntityEnderMedallion;
import net.nmccoy.legendgear.legacy.entities.EntityFireMedallion;
import net.nmccoy.legendgear.legacy.entities.EntityFireblast;
import net.nmccoy.legendgear.legacy.entities.EntityGrindStar;
import net.nmccoy.legendgear.legacy.entities.EntityQuake;
import net.nmccoy.legendgear.legacy.entities.EntityShotHook;
import net.nmccoy.legendgear.legacy.entities.EntityWhirlwind;
import net.nmccoy.legendgear.legacy.entities.EntityWindMedallion;
import net.nmccoy.legendgear.legacy.events.AugmentedSwordHandler;
import net.nmccoy.legendgear.legacy.events.ForgeEventHooksHandler;
import net.nmccoy.legendgear.legacy.events.JumpNoticeHandler;
import net.nmccoy.legendgear.legacy.items.AeroAmulet;
import net.nmccoy.legendgear.legacy.items.EarthMedallion;
import net.nmccoy.legendgear.legacy.items.FireMedallion;
import net.nmccoy.legendgear.legacy.items.GeoAmulet;
import net.nmccoy.legendgear.legacy.items.HeartPickup;
import net.nmccoy.legendgear.legacy.items.ItemBlockChained;
import net.nmccoy.legendgear.legacy.items.ItemBlockJar;
import net.nmccoy.legendgear.legacy.items.ItemBomb;
import net.nmccoy.legendgear.legacy.items.ItemBombBag;
import net.nmccoy.legendgear.legacy.items.ItemEnderMedallion;
import net.nmccoy.legendgear.legacy.items.ItemHeadband;
import net.nmccoy.legendgear.legacy.items.ItemHookshot;
import net.nmccoy.legendgear.legacy.items.ItemKey;
import net.nmccoy.legendgear.legacy.items.ItemMagicPowder;
import net.nmccoy.legendgear.legacy.items.ItemRockCandy;
import net.nmccoy.legendgear.legacy.items.ItemSlimeSword;
import net.nmccoy.legendgear.legacy.items.ItemTitanBand;
import net.nmccoy.legendgear.legacy.items.ItemWindBoots;
import net.nmccoy.legendgear.legacy.items.MagicMirror;
import net.nmccoy.legendgear.legacy.items.MysticSeed;
import net.nmccoy.legendgear.legacy.items.PyroAmulet;
import net.nmccoy.legendgear.legacy.items.Quiver;
import net.nmccoy.legendgear.legacy.items.WindMedallion;
import net.nmccoy.legendgear.legacy.recipes.MedallionSwordRecipe;
import net.nmccoy.legendgear.legacy.recipes.QuiverEmptyingRecipe;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class LegendGearLegacyContent {
    private static final String[] LEGENDGEAR_NAMESPACES = {"legendgear", "legendgear2", "legendgearreturns"};
    private static final int ENTITY_ID_BASE = 10720;
    private static boolean preInited;
    private static boolean initialized;
    private static boolean postInited;
    private static boolean activated;

    private LegendGearLegacyContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!LegendGear2.CONFIG_ENABLE_LEGACY_LEGENDGEAR) {
            return;
        }

        seedDefaults();
        ensureProxy();
        bindSharedReferences();
        registerItems();
        registerBlocks();
        registerEnchantments();
        registerRecipes();
        registerEventHandlers();
        activated = true;
    }

    public static void init(FMLInitializationEvent event) {
        if (!activated || initialized) {
            return;
        }
        initialized = true;
        registerEntities();
        registerWorldGenerators();
    }

    public static void postInit(FMLPostInitializationEvent event) {
        if (!activated || postInited) {
            return;
        }
        postInited = true;
        LegendGear.proxy.registerRenderers();
    }

    public static Item resolveLegacyItemAlias(String fullName) {
        if (!activated || fullName == null) {
            return null;
        }
        String key = aliasKey(fullName);
        if ("magicmirror".equals(key)) {
            return LegendGear.magicMirror;
        }
        if ("quiver".equals(key)) {
            return LegendGear.quiver;
        }
        if ("heartpickup".equals(key) || "heart".equals(key)) {
            return LegendGear.heartPickup;
        }
        if ("mysticseed".equals(key)) {
            return LegendGear.mysticSeed;
        }
        if ("earthmedallion".equals(key)) {
            return LegendGear.earthMedallion;
        }
        if ("windmedallion".equals(key) || "aeromedallion".equals(key)) {
            return LegendGear.windMedallion;
        }
        if ("firemedallion".equals(key)) {
            return LegendGear.fireMedallion;
        }
        if ("endermedallion".equals(key) || "itemmedallionender".equals(key)) {
            return LegendGear.enderMedallion;
        }
        if ("aeroamulet".equals(key)) {
            return LegendGear.aeroAmulet;
        }
        if ("geoamulet".equals(key)) {
            return LegendGear.geoAmulet;
        }
        if ("pyroamulet".equals(key)) {
            return LegendGear.pyroAmulet;
        }
        if ("bomb".equals(key) || "itembomb".equals(key)) {
            return LegendGear.bombItem;
        }
        if ("bombbag".equals(key)) {
            return LegendGear.bombBag;
        }
        if ("titanband".equals(key) || "itemtitanband".equals(key)) {
            return LegendGear.titanBand;
        }
        if ("rockcandy0".equals(key) || "rockcandy".equals(key)) {
            return LegendGear.rockCandy;
        }
        if ("hookshot".equals(key)) {
            return LegendGear.itemHookshot;
        }
        if ("ironkey".equals(key) || "goldkey".equals(key) || "diamondkey".equals(key)) {
            return LegendGear.itemKey;
        }
        if ("itemmagicpowder".equals(key) || "magicpowder".equals(key)) {
            return LegendGear.itemMagicPowder;
        }
        if ("slimesword".equals(key)) {
            return LegendGear.itemSlimeSword;
        }
        if ("itemheadband".equals(key) || "headbandofvalor".equals(key)) {
            return LegendGear.itemHeadband;
        }
        if ("itemwindboots".equals(key) || "whirlwindboots".equals(key)) {
            return LegendGear.itemWindBoots;
        }
        Block block = resolveLegacyBlockAlias(fullName);
        return block == null ? null : Item.getItemFromBlock(block);
    }

    public static Block resolveLegacyBlockAlias(String fullName) {
        if (!activated || fullName == null) {
            return null;
        }
        String key = aliasKey(fullName);
        if ("mysticshrub".equals(key) || "mysticshrubblock".equals(key)) {
            return LegendGear.mysticShrub;
        }
        if ("bombflower".equals(key) || "blockbombflower".equals(key)) {
            return LegendGear.bombFlower;
        }
        if ("sugarcube".equals(key) || "blocksugarcube".equals(key)) {
            return LegendGear.sugarCube;
        }
        if ("clayjar".equals(key) || "blockclayjar".equals(key)) {
            return LegendGear.jarBlock;
        }
        if ("swordpedestal".equals(key) || "blockpedestal".equals(key) || "blockswordpedestal".equals(key)) {
            return LegendGear.blockPedestal;
        }
        if ("chainedblock".equals(key) || "blockchained".equals(key)) {
            return LegendGear.blockChained;
        }
        if ("grindnode".equals(key) || "starbeamrail".equals(key) || "starrail".equals(key)) {
            return LegendGear.blockGrindNode;
        }
        if ("skybeam".equals(key) || "blockskybeam".equals(key)) {
            return LegendGear.blockSkybeam;
        }
        return null;
    }

    public static void registerLegacyAliases() {
        if (!activated) {
            return;
        }

        for (String namespace : LEGENDGEAR_NAMESPACES) {
            registerItemAlias(LegendGear.magicMirror, namespace + ":magicMirror");
            registerItemAlias(LegendGear.quiver, namespace + ":quiver");
            registerItemAlias(LegendGear.heartPickup, namespace + ":heartPickup", namespace + ":heart");
            registerItemAlias(LegendGear.mysticSeed, namespace + ":mysticSeed");
            registerItemAlias(LegendGear.earthMedallion, namespace + ":earthMedallion");
            registerItemAlias(LegendGear.windMedallion, namespace + ":windMedallion", namespace + ":aeroMedallion");
            registerItemAlias(LegendGear.fireMedallion, namespace + ":fireMedallion");
            registerItemAlias(LegendGear.enderMedallion, namespace + ":enderMedallion", namespace + ":itemMedallionEnder");
            registerItemAlias(LegendGear.aeroAmulet, namespace + ":aeroAmulet");
            registerItemAlias(LegendGear.geoAmulet, namespace + ":geoAmulet");
            registerItemAlias(LegendGear.pyroAmulet, namespace + ":pyroAmulet");
            registerItemAlias(LegendGear.bombItem, namespace + ":itemBomb", namespace + ":bomb");
            registerItemAlias(LegendGear.bombBag, namespace + ":bombBag");
            registerItemAlias(LegendGear.titanBand, namespace + ":itemTitanBand", namespace + ":titanBand");
            registerItemAlias(LegendGear.rockCandy, namespace + ":rockCandy");
            registerItemAlias(LegendGear.itemHookshot, namespace + ":hookshot");
            registerItemAlias(LegendGear.itemKey, namespace + ":itemKey", namespace + ":ironKey", namespace + ":goldKey", namespace + ":diamondKey");
            registerItemAlias(LegendGear.itemMagicPowder, namespace + ":itemMagicPowder", namespace + ":magicPowder");
            registerItemAlias(LegendGear.itemSlimeSword, namespace + ":slimeSword");
            registerItemAlias(LegendGear.itemHeadband, namespace + ":itemHeadband", namespace + ":headbandOfValor");
            registerItemAlias(LegendGear.itemWindBoots, namespace + ":itemWindBoots", namespace + ":whirlwindBoots");
            registerBlockAlias(LegendGear.mysticShrub, namespace + ":mysticShrubBlock", namespace + ":mysticShrub");
            registerBlockAlias(LegendGear.bombFlower, namespace + ":blockBombFlower", namespace + ":bombFlower");
            registerBlockAlias(LegendGear.sugarCube, namespace + ":blockSugarCube", namespace + ":sugarCube");
            registerBlockAlias(LegendGear.jarBlock, namespace + ":blockClayJar", namespace + ":clayJar");
            registerBlockAlias(LegendGear.blockPedestal, namespace + ":blockPedestal", namespace + ":swordPedestal");
            registerBlockAlias(LegendGear.blockChained, namespace + ":blockChained", namespace + ":chainedBlock");
            registerBlockAlias(LegendGear.blockGrindNode, namespace + ":grindNode", namespace + ":starbeamRail", namespace + ":starRail");
            registerBlockAlias(LegendGear.blockSkybeam, namespace + ":blockSkybeam", namespace + ":skybeam");
        }
    }

    private static void seedDefaults() {
        LegendGear.creativeTab = LegendGear2.legendgearTab;
        LegendGear.prismaticXP = LegendGear2.CONFIG_FANCY_XP;
        LegendGear.magicMirrorAllowed = ModConfig.legendGearLegacyMagicMirrorAllowed;
        LegendGear.emeraldShardsAllowed = ModConfig.legendGearLegacyEmeraldShardsAllowed;
        LegendGear.quiverAllowed = ModConfig.legendGearLegacyQuiverAllowed;
        LegendGear.heartsAllowed = ModConfig.legendGearLegacyHeartsAllowed;
        LegendGear.shrubsAllowed = ModConfig.legendGearLegacyMysticShrubAllowed;
        LegendGear.shrubSuperPrizes = ModConfig.legendGearLegacyMysticShrubSuperPrizes;
        LegendGear.allowPFeather = true;
        LegendGear.allowCaptureEgg = false;
        LegendGear.allowMedallions = ModConfig.legendGearLegacyMedallionsAllowed;
        LegendGear.allowBombs = ModConfig.legendGearLegacyBombsAllowed;
        LegendGear.allowCandy = ModConfig.legendGearLegacyAllowCandy;
        LegendGear.enableCraftyLoading = false;
        LegendGear.enableBombArrows = true;
        LegendGear.subtleShrubs = false;
        LegendGear.enableStarfall = true;
        LegendGear.hookshotAnyBlock = ModConfig.legendGearLegacyHookshotAnyBlock;
        LegendGear.amuletsWorkFromInventory = ModConfig.legendGearLegacyAmuletsWorkFromInventory;
        LegendGear.amuletsUseBaublesSlot = ModConfig.legendGearLegacyAmuletsUseBaublesSlot;
        LegendGear.medallionEffectsAffectPlayer = ModConfig.legendGearLegacyMedallionEffectsAffectPlayer;
        LegendGear.whirlwindBootsDashSound = ModConfig.legendGearLegacyWhirlwindBootsDashSound;
        LegendGear.starbeamRailLaunchStrength = ModConfig.legendGearLegacyStarbeamRailLaunchStrength;
        LegendGear.starbeamRailConnectionRange = ModConfig.legendGearLegacyStarbeamRailConnectionRange;
        LegendGear.starbeamRailNoSlowdown = ModConfig.legendGearLegacyStarbeamRailNoSlowdown;
        LegendGear.starbeamRailNoFallDamage = ModConfig.legendGearLegacyStarbeamRailNoFallDamage;
        LegendGear.starbeamRailRightClickTravel = ModConfig.legendGearLegacyStarbeamRailRightClickTravel;
        LegendGear.maxBombBagCapacity = ModConfig.legendGearLegacyBombBagCapacity;
        LegendGear.maxQuiverCapacity = ModConfig.legendGearLegacyQuiverMaxCapacity;
        LegendGear.bombDamage = ModConfig.legendGearLegacyBombDamage;
        LegendGear.magicBoomerangDamage = 6;
        LegendGear.emeraldShardExchangeRate = 8;
        LegendGear.emeraldDropMinMult = 0.4D;
        LegendGear.emeraldDropScale = 0.6D;
        LegendGear.heartMinMult = 0.4D;
        LegendGear.heartDropScale = 0.6D;
        LegendGear.maxHeartDrop = 10;
        LegendGear.mysticShrubHeartChance = LegendGear.heartsAllowed ? ModConfig.legendGearLegacyMysticShrubHeartChance : 0.0D;
        LegendGear.mysticShrubShardChance = LegendGear.emeraldShardsAllowed ? ModConfig.legendGearLegacyMysticShrubShardChance : 0.0D;
        LegendGear.mysticShrubArrowChance = ModConfig.legendGearLegacyMysticShrubArrowChance;
        LegendGear.shrubJackpotChance = ModConfig.legendGearLegacyMysticShrubJackpotChance;
        LegendGear.shrubGenStarChance = ModConfig.legendGearLegacyMysticShrubGenStarChance;
        LegendGear.shrubRarity = ModConfig.legendGearLegacyMysticShrubRarity;
        LegendGear.starFallRarity = 4200;
        LegendGear.fallenStarLifetime = 440;
        LegendGear.shrubDisabledBiomes = ModConfig.legendGearLegacyShrubDisabledBiomes;
        LegendGear.bombableBlocks = resolveLegacyBombableBlockIds(ModConfig.legendGearLegacyBombableBlocks);
        LegendGear.quiverLeakBows = new int[0];
        LegendGear.alsoCountAsSwords = new int[0];
        LegendGear.extraHookshotBlocks = new int[0];
        LegendGear.enchantmentFocusID = LegendGear2.scanEnchantmentID(ModConfig.legendGearLegacyFocusEnchantmentId);
        LegendGear.enchantmentSoulTetherID = LegendGear2.scanEnchantmentID(ModConfig.legendGearLegacySoulTetherEnchantmentId);
        LegendGear.normalizeCompatibilityLists();
    }

    private static void bindSharedReferences() {
        LegendGear.emeraldShard = LegendGear2.emeraldShard;
        LegendGear.phoenixFeather = LegendGear2.phoenixFeather;
        LegendGear.itemStardust = LegendGear2.starDust;
        LegendGear.magicBoomerang = LegendGear2.magicBoomerang;
        LegendGear.itemSimplePipes = LegendGear2.reedPipes;
        LegendGear.itemMilkChocolate = LegendGear2.milkChocolate;
        LegendGear.blockCaltrops = LegendGear2.caltropsBlock;
        LegendGear.blockStarstone = LegendGear2.starstoneBlock;
    }

    private static void ensureProxy() {
        if (LegendGear.proxy != null) {
            return;
        }
        Side side = FMLCommonHandler.instance().getSide();
        if (side == Side.CLIENT) {
            LegendGear.proxy = new net.nmccoy.legendgear.legacy.client.ClientProxy();
        } else {
            LegendGear.proxy = new CommonProxy();
        }
    }

    private static void registerItems() {
        LegendGear.itemHeadband = registerItem(new ItemHeadband(0), "itemHeadband");
        LegendGear.itemWindBoots = registerItem(new ItemWindBoots(), "itemWindBoots");
        LegendGear.magicMirror = registerItem(new MagicMirror(), "magicMirror");
        LegendGear.quiver = registerItem(new Quiver(), "quiver");
        LegendGear.heartPickup = registerItem(new HeartPickup(), "heartPickup");
        LegendGear.mysticSeed = registerItem(new MysticSeed(), "mysticSeed");
        LegendGear.earthMedallion = registerItem(new EarthMedallion(), "earthMedallion");
        LegendGear.windMedallion = registerItem(new WindMedallion(), "windMedallion");
        LegendGear.fireMedallion = registerItem(new FireMedallion(), "fireMedallion");
        LegendGear.enderMedallion = registerItem(new ItemEnderMedallion(), "enderMedallion");
        LegendGear.aeroAmulet = registerItem(new AeroAmulet(), "aeroAmulet");
        LegendGear.geoAmulet = registerItem(new GeoAmulet(), "geoAmulet");
        LegendGear.pyroAmulet = registerItem(new PyroAmulet(), "pyroAmulet");
        LegendGear.titanBand = registerItem(new ItemTitanBand(), "titanBand");
        LegendGear.bombItem = registerItem(new ItemBomb(), "itemBomb");
        LegendGear.bombBag = registerItem(new ItemBombBag(), "bombBag");
        LegendGear.rockCandy = registerItem(new ItemRockCandy(), "rockCandy");
        LegendGear.itemHookshot = registerItem(new ItemHookshot(), "hookshot");
        LegendGear.itemKey = registerItem(new ItemKey(), "itemKey");
        LegendGear.itemMagicPowder = registerItem(new ItemMagicPowder(), "itemMagicPowder");
        LegendGear.itemSlimeSword = registerItem(new ItemSlimeSword(), "slimeSword");

        BlockDispenser.dispenseBehaviorRegistry.putObject(LegendGear.bombItem, LegendGear.bombItem);
    }

    private static void registerBlocks() {
        LegendGear.mysticShrub = registerBlock(new MysticShrub(0, net.minecraft.block.material.Material.plants), ShrubItemBlock.class, "mysticShrubBlock");
        LegendGear.bombFlower = registerBlock(new BlockBombFlower(0, net.minecraft.block.material.Material.plants), ItemBlock.class, "blockBombFlower");
        LegendGear.sugarCube = registerBlock(new BlockSugarCube(0, net.minecraft.block.material.Material.cake), ItemBlock.class, "blockSugarCube");
        LegendGear.jarBlock = registerBlock(new BlockJar(0), ItemBlockJar.class, "blockClayJar");
        LegendGear.blockPedestal = registerBlock(new BlockSwordPedestal(0), ItemBlock.class, "blockPedestal");
        LegendGear.blockPedestalTech = registerBlock(new BlockSwordPedestalTechnical(0), ItemBlock.class, "blockPedestalTechnical");
        LegendGear.blockChained = registerBlock(new BlockChained(0), ItemBlockChained.class, "blockChained");
        LegendGear.blockGrindNode = registerBlock(new BlockGrindNode(0), ItemBlock.class, "starRail");
        LegendGear.blockSkybeam = registerBlock(new BlockSkybeam(0), ItemBlock.class, "blockSkybeam");

        GameRegistry.registerTileEntity(TileEntityJar.class, "tileEntityJar");
        GameRegistry.registerTileEntity(TileEntityPedestal.class, "tileEntityPedestal");
        GameRegistry.registerTileEntity(TileEntitySkybeam.class, "tileEntitySkybeam");

        LegendGear.bombableBlocks = withAdditionalIds(
                LegendGear.bombableBlocks,
                Block.getIdFromBlock(LegendGear.jarBlock)
        );
        LegendGear.normalizeCompatibilityLists();
    }

    private static void registerEnchantments() {
        LegendGear.enchantmentFocus = createLegacyEnchantment(
                "net.nmccoy.legendgear.legacy.enchantments.EnchantmentFocus",
                LegendGear.enchantmentFocusID,
                5,
                "Focus"
        );
        LegendGear.enchantmentSoulTether = createLegacyEnchantment(
                "net.nmccoy.legendgear.legacy.enchantments.EnchantmentSoulTether",
                LegendGear.enchantmentSoulTetherID,
                1,
                "Soul Tether"
        );
    }

    private static net.minecraft.enchantment.Enchantment createLegacyEnchantment(
            String className,
            int enchantmentId,
            int rarity,
            String displayName
    ) {
        try {
            Class<?> enchantmentClass = Class.forName(
                    className,
                    true,
                    LegendGearLegacyContent.class.getClassLoader()
            );
            Object instance = enchantmentClass
                    .getConstructor(Integer.TYPE, Integer.TYPE)
                    .newInstance(enchantmentId, rarity);
            if (instance instanceof net.minecraft.enchantment.Enchantment) {
                return (net.minecraft.enchantment.Enchantment) instance;
            }
            FMLLog.warning("[RiftFlux] Skipping legacy %s enchantment: constructed class is not an enchantment.", displayName);
        } catch (Throwable t) {
            FMLLog.warning("[RiftFlux] Skipping legacy %s enchantment because it could not be loaded: %s", displayName, t.toString());
        }
        return null;
    }

    private static void registerRecipes() {
        if (LegendGear.magicMirrorAllowed) {
            GameRegistry.addRecipe(new ItemStack(LegendGear.magicMirror),
                    "ege",
                    "gdg",
                    "ege",
                    'e', Items.ender_pearl,
                    'g', Items.gold_ingot,
                    'd', Items.diamond);
        }

        if (LegendGear.quiverAllowed) {
            GameRegistry.addRecipe(new ItemStack(LegendGear.quiver, 1, 1),
                    "l l",
                    "lal",
                    " l ",
                    'l', Items.leather,
                    'a', Items.arrow);
            GameRegistry.addRecipe(new QuiverEmptyingRecipe(LegendGear.quiver, Items.arrow));
        }

        if (LegendGear.allowMedallions) {
            GameRegistry.addRecipe(new ItemStack(LegendGear.earthMedallion, 1, 50),
                    "gbg",
                    "bdb",
                    "gbg",
                    'g', Items.gold_nugget,
                    'b', Items.brick,
                    'd', Blocks.dirt);

            GameRegistry.addRecipe(new ItemStack(LegendGear.windMedallion, 1, 50),
                    "gbg",
                    "bab",
                    "gbg",
                    'g', Items.gold_nugget,
                    'b', Items.brick,
                    'a', Items.arrow);

            GameRegistry.addRecipe(new ItemStack(LegendGear.fireMedallion, 1, 50),
                    "gbg",
                    "bcb",
                    "gbg",
                    'g', Items.gold_nugget,
                    'b', Items.brick,
                    'c', new ItemStack(Items.coal, 1, 0));

            GameRegistry.addRecipe(new ItemStack(LegendGear.fireMedallion, 1, 50),
                    "gbg",
                    "bcb",
                    "gbg",
                    'g', Items.gold_nugget,
                    'b', Items.brick,
                    'c', new ItemStack(Items.coal, 1, 1));

            GameRegistry.addRecipe(new ItemStack(LegendGear.enderMedallion, 1, 50),
                    "gbg",
                    "bpb",
                    "gbg",
                    'g', Items.gold_nugget,
                    'b', Items.brick,
                    'p', Items.ender_pearl);

            GameRegistry.addRecipe(new ItemStack(LegendGear.aeroAmulet),
                    " g ",
                    "g g",
                    " m ",
                    'g', Items.gold_ingot,
                    'm', LegendGear.windMedallion);

            GameRegistry.addRecipe(new ItemStack(LegendGear.geoAmulet),
                    " g ",
                    "g g",
                    " m ",
                    'g', Items.gold_ingot,
                    'm', LegendGear.earthMedallion);

            GameRegistry.addRecipe(new ItemStack(LegendGear.pyroAmulet),
                    " g ",
                    "g g",
                    " m ",
                    'g', Items.gold_ingot,
                    'm', LegendGear.fireMedallion);

            GameRegistry.addRecipe(new ItemStack(LegendGear.titanBand),
                    "glg",
                    "lml",
                    "glg",
                    'g', Items.gold_ingot,
                    'l', Items.leather,
                    'm', LegendGear.earthMedallion);
            GameRegistry.addRecipe(new MedallionSwordRecipe());

            GameRegistry.addRecipe(new ItemStack(LegendGear.itemHeadband),
                    "www",
                    "wmw",
                    'w', Blocks.wool,
                    'm', LegendGear.fireMedallion);

            GameRegistry.addRecipe(new ItemStack(LegendGear.itemWindBoots),
                    "g g",
                    "wmw",
                    'g', Items.gold_ingot,
                    'w', Blocks.wool,
                    'm', LegendGear.windMedallion);
        }

        GameRegistry.addRecipe(new ItemStack(LegendGear.itemHookshot),
                " ii",
                " fi",
                "d  ",
                'i', Items.iron_ingot,
                'f', Items.fishing_rod,
                'd', Blocks.dispenser);

        if (LegendGear.allowBombs) {
            GameRegistry.addRecipe(new ItemStack(LegendGear.bombBag, 1, 1),
                    "l l",
                    "lbl",
                    "lll",
                    'l', Items.leather,
                    'b', LegendGear.bombItem);
            GameRegistry.addRecipe(new QuiverEmptyingRecipe(LegendGear.bombBag, LegendGear.bombItem));
        }

        GameRegistry.addRecipe(new ItemStack(LegendGear.sugarCube),
                "sss",
                "sss",
                "sss",
                's', Items.sugar);

        GameRegistry.addShapelessRecipe(new ItemStack(Items.sugar, 9), LegendGear.sugarCube);
        if (LegendGear.allowCandy) {
            GameRegistry.addShapelessRecipe(new ItemStack(LegendGear.rockCandy, 1, 1),
                    LegendGear.sugarCube, LegendGear.sugarCube, LegendGear.sugarCube,
                    LegendGear.sugarCube, LegendGear.sugarCube, LegendGear.sugarCube,
                    Items.stick, Items.redstone, Items.water_bucket);
            GameRegistry.addShapelessRecipe(new ItemStack(LegendGear.rockCandy, 1, 3),
                    LegendGear.sugarCube, LegendGear.sugarCube, LegendGear.sugarCube,
                    LegendGear.sugarCube, LegendGear.sugarCube, LegendGear.sugarCube,
                    Items.stick, Items.emerald, Items.water_bucket);
            GameRegistry.addShapelessRecipe(new ItemStack(LegendGear.rockCandy, 1, 2),
                    LegendGear.sugarCube, LegendGear.sugarCube, LegendGear.sugarCube,
                    LegendGear.sugarCube, LegendGear.sugarCube, LegendGear.sugarCube,
                    Items.stick, new ItemStack(Items.dye, 1, 4), Items.water_bucket);
            GameRegistry.addShapelessRecipe(new ItemStack(LegendGear.rockCandy, 1, 4),
                    LegendGear.sugarCube, LegendGear.sugarCube, LegendGear.sugarCube,
                    LegendGear.sugarCube, LegendGear.sugarCube, LegendGear.sugarCube,
                    Items.stick, Items.diamond, Items.water_bucket);
        }

        GameRegistry.addRecipe(new ItemStack(LegendGear.jarBlock, 16),
                "b b",
                "b b",
                "bbb",
                'b', Items.brick);

        GameRegistry.addRecipe(new ItemStack(LegendGear.blockPedestal),
                " s ",
                "sss",
                's', Blocks.stone);

        GameRegistry.addShapelessRecipe(new ItemStack(LegendGear.itemMagicPowder),
                new ItemStack(LegendGear.itemStardust, 1, 0), Blocks.red_mushroom);
        GameRegistry.addShapelessRecipe(new ItemStack(LegendGear.itemMagicPowder),
                new ItemStack(LegendGear.itemStardust, 1, 0), Blocks.brown_mushroom);

        GameRegistry.addRecipe(new ItemStack(LegendGear.blockSkybeam),
                "ooo",
                "oso",
                "ooo",
                'o', Blocks.obsidian,
                's', new ItemStack(LegendGear.itemStardust, 1, 2));

        GameRegistry.addRecipe(new ItemStack(LegendGear.itemSlimeSword),
                "b",
                "b",
                "s",
                'b', Items.slime_ball,
                's', Items.stick);

        GameRegistry.addRecipe(new ItemStack(LegendGear.blockGrindNode, 8),
                " g ",
                "gdg",
                " i ",
                'g', LegendGear.itemStardust,
                'd', Items.diamond,
                'i', Items.gold_ingot);
    }

    private static void registerEventHandlers() {
        MinecraftForge.EVENT_BUS.register(new ForgeEventHooksHandler());
        MinecraftForge.EVENT_BUS.register(new AugmentedSwordHandler());
        MinecraftForge.EVENT_BUS.register(new JumpNoticeHandler());
        if (LegendGear.enchantmentSoulTether != null) {
            MinecraftForge.EVENT_BUS.register(LegendGear.enchantmentSoulTether);
            FMLCommonHandler.instance().bus().register(LegendGear.enchantmentSoulTether);
        }
        MinecraftForge.EVENT_BUS.register(LegendGear.itemHeadband);
        MinecraftForge.EVENT_BUS.register(LegendGear.enderMedallion);
        MinecraftForge.EVENT_BUS.register(LegendGear.titanBand);
        MinecraftForge.EVENT_BUS.register(LegendGear.itemMagicPowder);
    }

    private static void registerEntities() {
        Object owner = modEntityOwner();
        int id = ENTITY_ID_BASE;
        EntityRegistry.registerModEntity(EntityEarthMedallion.class, "earthMedallion", id++, owner, 64, 10, true);
        EntityRegistry.registerModEntity(EntityQuake.class, "earthMedallionQuake", id++, owner, 64, 10, true);
        EntityRegistry.registerModEntity(EntityWindMedallion.class, "windMedallion", id++, owner, 64, 10, true);
        EntityRegistry.registerModEntity(EntityArrowStorm.class, "windMedallionStorm", id++, owner, 64, 10, true);
        EntityRegistry.registerModEntity(EntityBomb.class, "throwableBomb", id++, owner, 64, 60, true);
        EntityRegistry.registerModEntity(EntityBombBlast.class, "bombExplosion", id++, owner, 64, 10, true);
        EntityRegistry.registerModEntity(EntityFireMedallion.class, "fireMedallion", id++, owner, 64, 10, true);
        EntityRegistry.registerModEntity(EntityFireblast.class, "fireMedallionBlast", id++, owner, 64, 10, true);
        EntityRegistry.registerModEntity(EntityWhirlwind.class, "whirlwind", id++, owner, 64, 10, true);
        EntityRegistry.registerModEntity(EntityShotHook.class, "hookshotHook", id++, owner, 64, 5, true);
        EntityRegistry.registerModEntity(EntityGrindStar.class, "grindStar", id++, owner, 64, 5, true);
        EntityRegistry.registerModEntity(EntityEnderMedallion.class, "enderMedallion", id++, owner, 64, 10, true);
        EntityRegistry.registerModEntity(EntityEnderBomb.class, "enderBomb", id++, owner, 64, 10, true);
    }

    private static void registerWorldGenerators() {
        if (LegendGear.shrubsAllowed) {
            GameRegistry.registerWorldGenerator((IWorldGenerator) new ShrubGenerator(), 0);
        }
        if (LegendGear.allowBombs) {
            GameRegistry.registerWorldGenerator((IWorldGenerator) new BombFlowerGenerator(), 0);
        }
    }

    private static Object modEntityOwner() {
        return com.voidsrift.riftflux.riftflux.instance != null
                ? com.voidsrift.riftflux.riftflux.instance
                : LegendGear2.instance;
    }

    private static <T extends Item> T registerItem(T item, String name) {
        GameRegistry.registerItem(item, name);
        return item;
    }

    private static <T extends Block> T registerBlock(T block, Class<? extends ItemBlock> itemClass, String name) {
        GameRegistry.registerBlock(block, itemClass, name);
        return block;
    }

    private static String aliasKey(String fullName) {
        int split = fullName.indexOf(':');
        String path = split >= 0 ? fullName.substring(split + 1) : fullName;
        return path.toLowerCase(Locale.ROOT).replace("_", "").replace("-", "").replace(".", "");
    }

    private static void registerItemAlias(Item item, String... aliases) {
        if (item == null || aliases == null) {
            return;
        }
        for (String alias : aliases) {
            LegacyRegistryAliasHelper.registerItemAliases(item, alias, alias.toLowerCase(Locale.ROOT));
        }
    }

    private static void registerBlockAlias(Block block, String... aliases) {
        if (block == null || aliases == null) {
            return;
        }
        for (String alias : aliases) {
            LegacyRegistryAliasHelper.registerBlockAliases(block, alias, alias.toLowerCase(Locale.ROOT));
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                LegacyRegistryAliasHelper.registerItemAliases(item, alias, alias.toLowerCase(Locale.ROOT));
            }
        }
    }

    private static int[] withAdditionalIds(int[] values, int... additionalIds) {
        Set<Integer> merged = new LinkedHashSet<Integer>();
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                merged.add(values[i]);
            }
        }
        if (additionalIds != null) {
            for (int i = 0; i < additionalIds.length; i++) {
                merged.add(additionalIds[i]);
            }
        }
        int[] result = new int[merged.size()];
        int index = 0;
        for (Integer value : merged) {
            result[index++] = value.intValue();
        }
        return result;
    }

    private static int[] resolveLegacyBombableBlockIds(String[] configuredEntries) {
        Set<Integer> resolved = new LinkedHashSet<Integer>();
        if (configuredEntries == null) {
            return new int[0];
        }

        for (String entry : configuredEntries) {
            if (entry == null) {
                continue;
            }

            String trimmed = entry.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] tokens = trimmed.split("[,; ]+");
            for (String token : tokens) {
                int resolvedId = resolveLegacyBombableBlockId(token);
                if (resolvedId >= 0) {
                    resolved.add(resolvedId);
                }
            }
        }

        int[] ids = new int[resolved.size()];
        int index = 0;
        for (Integer value : resolved) {
            ids[index++] = value.intValue();
        }
        return ids;
    }

    private static int resolveLegacyBombableBlockId(String rawToken) {
        if (rawToken == null) {
            return -1;
        }

        String token = rawToken.trim();
        if (token.isEmpty()) {
            return -1;
        }

        String registryName = token.indexOf(':') >= 0 ? token : "minecraft:" + token;
        Object rawBlock = Block.blockRegistry.getObject(registryName);
        return rawBlock instanceof Block ? Block.getIdFromBlock((Block) rawBlock) : -1;
    }
}
