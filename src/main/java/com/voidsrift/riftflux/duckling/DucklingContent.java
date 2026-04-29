package com.voidsrift.riftflux.duckling;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.entity.RiftFluxEntityRegistry;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.util.LegacyRegistryAliasHelper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import java.io.File;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.particles.BedrockLibrary;

public final class DucklingContent {
    public static final String MODID = "ghibli";

    public static Item rawDuck;
    public static Item cookedDuck;
    public static Item duckEgg;
    public static Item duckSpawnEgg;
    public static Item quacklingSpawnEgg;
    public static Item starCandy;
    public static Item sootJar;
    public static Item sootSpriteSpawnEgg;

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;
    private static boolean eventHandlersRegistered;

    private DucklingContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableDucklingModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!isEnabled()) {
            return;
        }

        rawDuck = new ItemFood(2, 0.3F, true)
                .setTextureName(MODID + ":raw_duck")
                .setUnlocalizedName("raw_duck")
                .setCreativeTab(CreativeTabs.tabFood);
        cookedDuck = new ItemFood(6, 0.6F, true)
                .setTextureName(MODID + ":cooked_duck")
                .setUnlocalizedName("cooked_duck")
                .setCreativeTab(CreativeTabs.tabFood);
        duckEgg = new ItemDuckEgg();
        duckSpawnEgg = new ItemDucklingSpawnEgg("duck", "duck_spawn_egg", 0xF5EFE0, 0x6D8E3C);
        quacklingSpawnEgg = new ItemDucklingSpawnEgg("quackling", "quackling_spawn_egg", 0xD9C17A, 0x35564B);
        starCandy = new ItemStarCandy();
        sootJar = new ItemSootJar();
        sootSpriteSpawnEgg = new ItemDucklingSpawnEgg("soot_sprite", "soot_sprite_spawn_egg", 0x000000, 0xFFFFFF);

        GameRegistry.registerItem(rawDuck, "raw_duck", MODID);
        GameRegistry.registerItem(cookedDuck, "cooked_duck", MODID);
        GameRegistry.registerItem(duckEgg, "duck_egg", MODID);
        GameRegistry.registerItem(duckSpawnEgg, "duck_spawn_egg", MODID);
        GameRegistry.registerItem(quacklingSpawnEgg, "quackling_spawn_egg", MODID);
        GameRegistry.registerItem(starCandy, "star_candy", MODID);
        GameRegistry.registerItem(sootJar, "soot_jar", MODID);
        GameRegistry.registerItem(sootSpriteSpawnEgg, "soot_sprite_spawn_egg", MODID);

        registerEntities();
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized) {
            return;
        }
        initialized = true;

        if (!isEnabled()) {
            return;
        }

        registerRecipes();
        registerEventHandlers();
    }

    public static void initClient() {
        if (clientInited) {
            return;
        }
        clientInited = true;

        if (!isEnabled()) {
            return;
        }

        initializeGeckoLibClient();
        RenderingRegistry.registerEntityRenderingHandler(EntityDuck.class, new RenderDuck());
        RenderingRegistry.registerEntityRenderingHandler(EntityQuackling.class, new RenderQuackling());
        RenderingRegistry.registerEntityRenderingHandler(EntitySootSprite.class, new RenderSootSprite());
        RenderingRegistry.registerEntityRenderingHandler(EntityDuckEgg.class, new net.minecraft.client.renderer.entity.RenderSnowball(duckEgg));
    }

    public static void registerLegacyAliases() {
        LegacyRegistryAliasHelper.registerItemAliases(rawDuck, "duckling:raw_duck");
        LegacyRegistryAliasHelper.registerItemAliases(cookedDuck, "duckling:cooked_duck");
        LegacyRegistryAliasHelper.registerItemAliases(duckEgg, "duckling:duck_egg");
        LegacyRegistryAliasHelper.registerItemAliases(duckSpawnEgg, "duckling:duck_spawn_egg");
        LegacyRegistryAliasHelper.registerItemAliases(quacklingSpawnEgg, "duckling:quackling_spawn_egg");
        LegacyRegistryAliasHelper.registerItemAliases(starCandy, "sootspritecraft:star_candy");
        LegacyRegistryAliasHelper.registerItemAliases(sootJar, "sootspritecraft:soot_jar");
        LegacyRegistryAliasHelper.registerItemAliases(sootSpriteSpawnEgg, "sootspritecraft:soot_sprite_spawn_egg");
    }

    private static void registerEntities() {
        RiftFluxEntityRegistry.registerModEntity(EntityDuck.class, "duck", riftflux.instance, 80, 3, true);
        RiftFluxEntityRegistry.registerModEntity(EntityQuackling.class, "quackling", riftflux.instance, 80, 3, true);
        RiftFluxEntityRegistry.registerModEntity(EntityDuckEgg.class, "duck_egg", riftflux.instance, 64, 10, true);
        RiftFluxEntityRegistry.registerModEntity(EntitySootSprite.class, "soot_sprite", riftflux.instance, 80, 1, true);
    }

    private static void registerRecipes() {
        GameRegistry.addSmelting(rawDuck, new ItemStack(cookedDuck), 0.35F);
        if (ModConfig.ghibliStarCandyRecipeEnabled) {
            GameRegistry.addShapelessRecipe(new ItemStack(starCandy, 4),
                    Items.sugar,
                    new ItemStack(Items.dye, 1, 11),
                    new ItemStack(Items.dye, 1, 2));
        }

        GameRegistry.addRecipe(new ItemStack(Items.cake),
                "AAA",
                "BEB",
                "CCC",
                'A', Items.milk_bucket,
                'B', Items.sugar,
                'C', Items.wheat,
                'E', duckEgg);

        GameRegistry.addShapelessRecipe(new ItemStack(Items.pumpkin_pie), Blocks.pumpkin, Items.sugar, duckEgg);
    }

    private static void registerEventHandlers() {
        if (eventHandlersRegistered) {
            return;
        }
        eventHandlersRegistered = true;
        MinecraftForge.EVENT_BUS.register(new SootSpriteEvents());
    }

    private static void initializeGeckoLibClient() {
        if (BedrockLibrary.instance == null) {
            new BedrockLibrary(new File("./particle"));
        }
        GeckoLib.initialize();
    }
}
