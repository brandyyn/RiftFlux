package com.voidsrift.riftflux.asgardshield;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class AsgardShieldContent {
    public static Item asgardShieldWood;
    public static Item asgardShieldStone;
    public static Item asgardShieldIron;
    public static Item asgardShieldDiamond;
    public static Item asgardShieldWoodGilded;
    public static Item asgardShieldStoneGilded;
    public static Item asgardShieldIronGilded;
    public static Item asgardShieldDiamondGilded;

    public static Item zedSwordWood;
    public static Item zedSwordStone;
    public static Item zedSwordIron;
    public static Item zedSwordGold;
    public static Item zedSwordDiamond;

    public static Item asgardShieldNether;
    public static Item asgardShieldEnder;
    public static Item asgardShieldNetherGilded;
    public static Item asgardShieldEnderGilded;
    public static Item zedSwordNether;
    public static Item zedSwordEnder;

    public static Item asgardShieldSkull;
    public static Item asgardShieldPatchwork;
    public static Item asgardShieldSkullGilded;
    public static Item asgardShieldPatchworkGilded;
    public static Item zedSwordSkull;
    public static Item zedSwordPatchwork;

    public static Item asgardShieldLivingmetal;
    public static Item asgardShieldBiomass;
    public static Item asgardShieldLivingmetalGilded;
    public static Item asgardShieldBiomassGilded;
    public static Item zedSwordLivingmetal;
    public static Item zedSwordBiomass;

    private static Item.ToolMaterial toolNether;
    private static Item.ToolMaterial toolEnder;
    private static Item.ToolMaterial toolSkull;
    private static Item.ToolMaterial toolPatchwork;
    private static Item.ToolMaterial toolLivingmetal;
    private static Item.ToolMaterial toolBiomass;

    private static final Map<String, Integer> SHIELD_COLORS = new HashMap<String, Integer>();
    private static final Map<Item, Item> GILDED_VARIANTS = new HashMap<Item, Item>();

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;
    private static boolean enabled;

    private AsgardShieldContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        refreshShieldColors();

        if (!ModConfig.enableAsgardShieldModule) {
            return;
        }
        enabled = true;

        initToolMaterials();
        createItems();
        registerItems();
        registerRecipes();
        registerGildedVariants();
    }

    public static void init(FMLInitializationEvent event) {
        if (!enabled || initialized) {
            return;
        }
        initialized = true;
        MinecraftForge.EVENT_BUS.register(new AsgardShieldEventHandler());
    }

    public static void initClient() {
        if (!enabled || clientInited) {
            return;
        }
        clientInited = true;
        AsgardShieldHud.bootstrap();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static int getShieldColor(String shieldType) {
        if (shieldType == null || shieldType.isEmpty()) {
            return ModConfig.asgardShieldColorWood;
        }
        Integer value = SHIELD_COLORS.get(shieldType.toLowerCase(Locale.ROOT));
        return value == null ? ModConfig.asgardShieldColorWood : value;
    }

    static Item getGildedVariant(Item baseShield) {
        return baseShield == null ? null : GILDED_VARIANTS.get(baseShield);
    }

    static boolean isGildedShield(Item item) {
        return item != null && GILDED_VARIANTS.containsValue(item);
    }

    private static void refreshShieldColors() {
        SHIELD_COLORS.clear();
        SHIELD_COLORS.put("wood", ModConfig.asgardShieldColorWood);
        SHIELD_COLORS.put("stone", ModConfig.asgardShieldColorStone);
        SHIELD_COLORS.put("iron", ModConfig.asgardShieldColorIron);
        SHIELD_COLORS.put("diamond", ModConfig.asgardShieldColorDiamond);
        SHIELD_COLORS.put("nether", ModConfig.asgardShieldColorNether);
        SHIELD_COLORS.put("ender", ModConfig.asgardShieldColorEnder);
        SHIELD_COLORS.put("skull", ModConfig.asgardShieldColorSkull);
        SHIELD_COLORS.put("patchwork", ModConfig.asgardShieldColorPatchwork);
        SHIELD_COLORS.put("livingmetal", ModConfig.asgardShieldColorLivingmetal);
        SHIELD_COLORS.put("biomass", ModConfig.asgardShieldColorBiomass);
    }

    private static void initToolMaterials() {
        toolNether = EnumHelper.addToolMaterial("RIFTFLUX_NETHER_QUARTZ", 2, 350, 6.0F, 2.0F, 16);
        toolEnder = EnumHelper.addToolMaterial("RIFTFLUX_ENDER", 2, 900, 7.0F, 3.0F, 15);
        toolSkull = EnumHelper.addToolMaterial("RIFTFLUX_SKULL", 1, 160, 5.0F, 1.0F, 11);
        toolPatchwork = EnumHelper.addToolMaterial("RIFTFLUX_PATCHWORK", 0, 30, 2.0F, 0.0F, 5);
        toolLivingmetal = EnumHelper.addToolMaterial("RIFTFLUX_LIVINGMETAL", 2, 250, 6.0F, 2.0F, 20);
        toolBiomass = EnumHelper.addToolMaterial("RIFTFLUX_BIOMASS", 2, 150, 6.0F, 2.0F, 20);
    }

    private static void createItems() {
        ItemStack planksRepair = new ItemStack(Item.getItemFromBlock(Blocks.planks), 1, 32767);
        ItemStack cobbleRepair = new ItemStack(Item.getItemFromBlock(Blocks.cobblestone));
        ItemStack ironRepair = new ItemStack(Items.iron_ingot);
        ItemStack diamondRepair = new ItemStack(Items.diamond);
        ItemStack goldRepair = new ItemStack(Items.gold_ingot);
        ItemStack quartzRepair = new ItemStack(Items.quartz);
        ItemStack obsidianRepair = new ItemStack(Item.getItemFromBlock(Blocks.obsidian));
        ItemStack boneRepair = new ItemStack(Items.bone);
        ItemStack rottenFleshRepair = new ItemStack(Items.rotten_flesh);

        ItemStack livingmetalRepair = resolveHarkenIngredient(
                "HSIngotLivingmetal",
                "ingotLivingmetal",
                "livingmetalIngot",
                "livingmetal_ingot"
        );
        if (livingmetalRepair == null) {
            livingmetalRepair = ironRepair.copy();
        }

        ItemStack biomassRepair = resolveHarkenIngredient(
                "HSBiomass",
                "biomass",
                "biomassChunk",
                "biomass_chunk"
        );
        if (biomassRepair == null) {
            biomassRepair = rottenFleshRepair.copy();
        }

        asgardShieldWood = new ItemAsgardShield("ASShieldWood", Item.ToolMaterial.WOOD, false, 1, "wood", "wood", planksRepair)
                .setUnlocalizedName("ASShieldWood");
        asgardShieldStone = new ItemAsgardShield("ASShieldStone", Item.ToolMaterial.STONE, false, 2, "stone", "stone", cobbleRepair)
                .setUnlocalizedName("ASShieldStone");
        asgardShieldIron = new ItemAsgardShield("ASShieldIron", Item.ToolMaterial.IRON, false, 3, "metal", "iron", ironRepair)
                .setUnlocalizedName("ASShieldIron");
        asgardShieldDiamond = new ItemAsgardShield("ASShieldDiamond", Item.ToolMaterial.EMERALD, false, 4, "crystal", "diamond", diamondRepair)
                .setUnlocalizedName("ASShieldDiamond");

        asgardShieldWoodGilded = new ItemAsgardShield("ASShieldWoodGilded", Item.ToolMaterial.WOOD, true, 1, "wood", "wood", planksRepair)
                .setUnlocalizedName("ASShieldWoodGilded");
        asgardShieldStoneGilded = new ItemAsgardShield("ASShieldStoneGilded", Item.ToolMaterial.STONE, true, 2, "stone", "stone", cobbleRepair)
                .setUnlocalizedName("ASShieldStoneGilded");
        asgardShieldIronGilded = new ItemAsgardShield("ASShieldIronGilded", Item.ToolMaterial.IRON, true, 3, "metal", "iron", ironRepair)
                .setUnlocalizedName("ASShieldIronGilded");
        asgardShieldDiamondGilded = new ItemAsgardShield("ASShieldDiamondGilded", Item.ToolMaterial.EMERALD, true, 4, "crystal", "diamond", diamondRepair)
                .setUnlocalizedName("ASShieldDiamondGilded");

        zedSwordWood = new ItemAsgardGreatsword("ASGiantSwordWood", Item.ToolMaterial.WOOD, "wood", "wood", planksRepair)
                .setUnlocalizedName("ASGiantSwordWood");
        zedSwordStone = new ItemAsgardGreatsword("ASGiantSwordStone", Item.ToolMaterial.STONE, "stone", "stone", cobbleRepair)
                .setUnlocalizedName("ASGiantSwordStone");
        zedSwordIron = new ItemAsgardGreatsword("ASGiantSwordIron", Item.ToolMaterial.IRON, "metal", "iron", ironRepair)
                .setUnlocalizedName("ASGiantSwordIron");
        zedSwordGold = new ItemAsgardGreatsword("ASGiantSwordGold", Item.ToolMaterial.GOLD, "metal", "gold", goldRepair)
                .setUnlocalizedName("ASGiantSwordGold");
        zedSwordDiamond = new ItemAsgardGreatsword("ASGiantSwordDiamond", Item.ToolMaterial.EMERALD, "crystal", "diamond", diamondRepair)
                .setUnlocalizedName("ASGiantSwordDiamond");

        asgardShieldNether = new ItemAsgardShield("ASShieldNether", toolNether, false, 5, "crystal", "nether", quartzRepair)
                .setUnlocalizedName("ASShieldNether");
        asgardShieldEnder = new ItemAsgardShield("ASShieldEnder", toolEnder, false, 6, "stone", "ender", obsidianRepair)
                .setUnlocalizedName("ASShieldEnder");
        asgardShieldNetherGilded = new ItemAsgardShield("ASShieldNetherGilded", toolNether, true, 5, "crystal", "nether", quartzRepair)
                .setUnlocalizedName("ASShieldNetherGilded");
        asgardShieldEnderGilded = new ItemAsgardShield("ASShieldEnderGilded", toolEnder, true, 6, "stone", "ender", obsidianRepair)
                .setUnlocalizedName("ASShieldEnderGilded");
        zedSwordNether = new ItemAsgardGreatsword("ASGiantSwordNether", toolNether, "crystal", "nether", quartzRepair)
                .setUnlocalizedName("ASGiantSwordNether");
        zedSwordEnder = new ItemAsgardGreatsword("ASGiantSwordEnder", toolEnder, "stone", "ender", obsidianRepair)
                .setUnlocalizedName("ASGiantSwordEnder");

        asgardShieldSkull = new ItemAsgardShield("ASShieldSkull", toolSkull, false, 7, "bone", "skull", boneRepair)
                .setUnlocalizedName("ASShieldSkull");
        asgardShieldPatchwork = new ItemAsgardShield("ASShieldPatchwork", toolPatchwork, false, 8, "flesh", "patchwork", rottenFleshRepair)
                .setUnlocalizedName("ASShieldPatchwork");
        asgardShieldSkullGilded = new ItemAsgardShield("ASShieldSkullGilded", toolSkull, true, 7, "bone", "skull", boneRepair)
                .setUnlocalizedName("ASShieldSkullGilded");
        asgardShieldPatchworkGilded = new ItemAsgardShield("ASShieldPatchworkGilded", toolPatchwork, true, 8, "flesh", "patchwork", rottenFleshRepair)
                .setUnlocalizedName("ASShieldPatchworkGilded");
        zedSwordSkull = new ItemAsgardGreatsword("ASGiantSwordSkull", toolSkull, "bone", "skull", boneRepair)
                .setUnlocalizedName("ASGiantSwordSkull");
        zedSwordPatchwork = new ItemAsgardGreatsword("ASGiantSwordPatchwork", toolPatchwork, "flesh", "patchwork", rottenFleshRepair)
                .setUnlocalizedName("ASGiantSwordPatchwork");

        asgardShieldLivingmetal = new ItemAsgardShield("ASShieldLivingmetal", toolLivingmetal, false, 9, "metal", "livingmetal", livingmetalRepair)
                .setUnlocalizedName("ASShieldLivingmetal");
        asgardShieldBiomass = new ItemAsgardShield("ASShieldBiomass", toolBiomass, false, 10, "flesh", "biomass", biomassRepair)
                .setUnlocalizedName("ASShieldBiomass");
        asgardShieldLivingmetalGilded = new ItemAsgardShield("ASShieldLivingmetalGilded", toolLivingmetal, true, 9, "metal", "livingmetal", livingmetalRepair)
                .setUnlocalizedName("ASShieldLivingmetalGilded");
        asgardShieldBiomassGilded = new ItemAsgardShield("ASShieldBiomassGilded", toolBiomass, true, 10, "flesh", "biomass", biomassRepair)
                .setUnlocalizedName("ASShieldBiomassGilded");
        zedSwordLivingmetal = new ItemAsgardGreatsword("ASGiantSwordLivingmetal", toolLivingmetal, "metal", "livingmetal", livingmetalRepair)
                .setUnlocalizedName("ASGiantSwordLivingmetal");
        zedSwordBiomass = new ItemAsgardGreatsword("ASGiantSwordBiomass", toolBiomass, "flesh", "biomass", biomassRepair)
                .setUnlocalizedName("ASGiantSwordBiomass");
    }

    private static void registerItems() {
        GameRegistry.registerItem(asgardShieldWood, "as_shield_wood");
        GameRegistry.registerItem(asgardShieldStone, "as_shield_stone");
        GameRegistry.registerItem(asgardShieldIron, "as_shield_iron");
        GameRegistry.registerItem(asgardShieldDiamond, "as_shield_diamond");
        GameRegistry.registerItem(asgardShieldWoodGilded, "as_shield_wood_gilded");
        GameRegistry.registerItem(asgardShieldStoneGilded, "as_shield_stone_gilded");
        GameRegistry.registerItem(asgardShieldIronGilded, "as_shield_iron_gilded");
        GameRegistry.registerItem(asgardShieldDiamondGilded, "as_shield_diamond_gilded");

        GameRegistry.registerItem(zedSwordWood, "as_giant_sword_wood");
        GameRegistry.registerItem(zedSwordStone, "as_giant_sword_stone");
        GameRegistry.registerItem(zedSwordIron, "as_giant_sword_iron");
        GameRegistry.registerItem(zedSwordGold, "as_giant_sword_gold");
        GameRegistry.registerItem(zedSwordDiamond, "as_giant_sword_diamond");

        GameRegistry.registerItem(asgardShieldNether, "as_shield_nether");
        GameRegistry.registerItem(asgardShieldEnder, "as_shield_ender");
        GameRegistry.registerItem(asgardShieldNetherGilded, "as_shield_nether_gilded");
        GameRegistry.registerItem(asgardShieldEnderGilded, "as_shield_ender_gilded");
        GameRegistry.registerItem(zedSwordNether, "as_giant_sword_nether");
        GameRegistry.registerItem(zedSwordEnder, "as_giant_sword_ender");

        GameRegistry.registerItem(asgardShieldSkull, "as_shield_skull");
        GameRegistry.registerItem(asgardShieldPatchwork, "as_shield_patchwork");
        GameRegistry.registerItem(asgardShieldSkullGilded, "as_shield_skull_gilded");
        GameRegistry.registerItem(asgardShieldPatchworkGilded, "as_shield_patchwork_gilded");
        GameRegistry.registerItem(zedSwordSkull, "as_giant_sword_skull");
        GameRegistry.registerItem(zedSwordPatchwork, "as_giant_sword_patchwork");

        GameRegistry.registerItem(asgardShieldLivingmetal, "as_shield_livingmetal");
        GameRegistry.registerItem(asgardShieldBiomass, "as_shield_biomass");
        GameRegistry.registerItem(asgardShieldLivingmetalGilded, "as_shield_livingmetal_gilded");
        GameRegistry.registerItem(asgardShieldBiomassGilded, "as_shield_biomass_gilded");
        GameRegistry.registerItem(zedSwordLivingmetal, "as_giant_sword_livingmetal");
        GameRegistry.registerItem(zedSwordBiomass, "as_giant_sword_biomass");
    }

    private static void registerRecipes() {
        ItemStack skull = new ItemStack(Items.skull, 1, 0);
        ItemStack livingmetalIngredient = resolveHarkenIngredient(
                "HSIngotLivingmetal",
                "ingotLivingmetal",
                "livingmetalIngot",
                "livingmetal_ingot"
        );
        ItemStack biomassIngredient = resolveHarkenIngredient(
                "HSBiomass",
                "biomass",
                "biomassChunk",
                "biomass_chunk"
        );

        GameRegistry.addRecipe(new ItemStack(asgardShieldWood), "XXX", "X*X", " X ", 'X', new ItemStack(Blocks.planks, 1, 32767), '*', Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(asgardShieldStone), "XXX", "X*X", " X ", 'X', Blocks.cobblestone, '*', Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(asgardShieldIron), "XXX", "X*X", " X ", 'X', Items.iron_ingot, '*', Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(asgardShieldDiamond), "XXX", "X*X", " X ", 'X', Items.diamond, '*', Items.iron_ingot);

        GameRegistry.addRecipe(new ItemStack(zedSwordWood), " XX", "XXX", "*X ", 'X', new ItemStack(Blocks.planks, 1, 32767), '*', Items.stick);
        GameRegistry.addRecipe(new ItemStack(zedSwordStone), " XX", "XXX", "*X ", 'X', Blocks.cobblestone, '*', Items.stick);
        GameRegistry.addRecipe(new ItemStack(zedSwordIron), " XX", "XXX", "*X ", 'X', Items.iron_ingot, '*', Items.stick);
        GameRegistry.addRecipe(new ItemStack(zedSwordGold), " XX", "XXX", "*X ", 'X', Items.gold_ingot, '*', Items.stick);
        GameRegistry.addRecipe(new ItemStack(zedSwordDiamond), " XX", "XXX", "*X ", 'X', Items.diamond, '*', Items.stick);

        GameRegistry.addRecipe(new ItemStack(asgardShieldNether), "XXX", "X*X", " X ", 'X', Items.quartz, '*', Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(asgardShieldEnder), "X^X", "X*X", " X ", 'X', Blocks.obsidian, '*', Items.iron_ingot, '^', Items.ender_pearl);
        GameRegistry.addRecipe(new ItemStack(zedSwordNether), " XX", "XXX", "*X ", 'X', Items.quartz, '*', Items.blaze_rod);
        GameRegistry.addRecipe(new ItemStack(zedSwordEnder), " XX", "X^X", "*X ", 'X', Blocks.obsidian, '*', Items.blaze_rod, '^', Items.ender_pearl);

        GameRegistry.addRecipe(new ItemStack(asgardShieldSkull), "X^X", "X*X", " X ", 'X', Items.bone, '*', Items.iron_ingot, '^', skull);
        GameRegistry.addRecipe(new ItemStack(asgardShieldPatchwork), "XXX", "XXX", " X ", 'X', Items.rotten_flesh);
        GameRegistry.addRecipe(new ItemStack(zedSwordSkull), " XX", "X^X", "*X ", 'X', Items.bone, '*', Items.stick, '^', skull);
        GameRegistry.addRecipe(new ItemStack(zedSwordPatchwork), " XX", "XXX", "XX ", 'X', Items.rotten_flesh);

        if (livingmetalIngredient != null) {
            GameRegistry.addRecipe(new ItemStack(asgardShieldLivingmetal), "XXX", "X*X", " X ", 'X', livingmetalIngredient.copy(), '*', Items.iron_ingot);
            GameRegistry.addRecipe(new ItemStack(zedSwordLivingmetal), " XX", "XXX", "*X ", 'X', livingmetalIngredient.copy(), '*', Items.stick);
        }
        if (biomassIngredient != null) {
            GameRegistry.addRecipe(new ItemStack(asgardShieldBiomass), "XXX", "X*X", " X ", 'X', biomassIngredient.copy(), '*', Items.iron_ingot);
            GameRegistry.addRecipe(new ItemStack(zedSwordBiomass), " XX", "XXX", "*X ", 'X', biomassIngredient.copy(), '*', Items.stick);
        }

        GameRegistry.addRecipe(new RecipeAsgardShieldGilding());
    }

    private static void registerGildedVariants() {
        GILDED_VARIANTS.clear();
        GILDED_VARIANTS.put(asgardShieldWood, asgardShieldWoodGilded);
        GILDED_VARIANTS.put(asgardShieldStone, asgardShieldStoneGilded);
        GILDED_VARIANTS.put(asgardShieldIron, asgardShieldIronGilded);
        GILDED_VARIANTS.put(asgardShieldDiamond, asgardShieldDiamondGilded);
        GILDED_VARIANTS.put(asgardShieldNether, asgardShieldNetherGilded);
        GILDED_VARIANTS.put(asgardShieldEnder, asgardShieldEnderGilded);
        GILDED_VARIANTS.put(asgardShieldSkull, asgardShieldSkullGilded);
        GILDED_VARIANTS.put(asgardShieldPatchwork, asgardShieldPatchworkGilded);
        GILDED_VARIANTS.put(asgardShieldLivingmetal, asgardShieldLivingmetalGilded);
        GILDED_VARIANTS.put(asgardShieldBiomass, asgardShieldBiomassGilded);
    }

    private static ItemStack resolveHarkenIngredient(String... candidates) {
        Item item = resolveHarkenItem(candidates);
        return item == null ? null : new ItemStack(item);
    }

    private static Item resolveHarkenItem(String... candidates) {
        if (candidates == null || candidates.length == 0) {
            return null;
        }
        for (String candidate : candidates) {
            if (candidate == null || candidate.isEmpty()) {
                continue;
            }

            Item item = GameRegistry.findItem("harkenscythe", candidate);
            if (item != null) {
                return item;
            }

            Object direct = Item.itemRegistry.getObject("harkenscythe:" + candidate);
            if (direct instanceof Item) {
                return (Item) direct;
            }

            direct = Item.itemRegistry.getObject(candidate);
            if (direct instanceof Item) {
                return (Item) direct;
            }
        }
        return null;
    }
}
