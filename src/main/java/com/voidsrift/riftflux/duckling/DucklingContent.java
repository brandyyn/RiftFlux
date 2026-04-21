package com.voidsrift.riftflux.duckling;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.particles.BedrockLibrary;

public final class DucklingContent {
    public static final String MODID = "duckling";

    public static Item rawDuck;
    public static Item cookedDuck;
    public static Item duckEgg;
    public static Item duckSpawnEgg;
    public static Item quacklingSpawnEgg;

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;

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

        GameRegistry.registerItem(rawDuck, "raw_duck", MODID);
        GameRegistry.registerItem(cookedDuck, "cooked_duck", MODID);
        GameRegistry.registerItem(duckEgg, "duck_egg", MODID);
        GameRegistry.registerItem(duckSpawnEgg, "duck_spawn_egg", MODID);
        GameRegistry.registerItem(quacklingSpawnEgg, "quackling_spawn_egg", MODID);

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
        registerNaturalSpawns();
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
        RenderingRegistry.registerEntityRenderingHandler(EntityDuckEgg.class, new net.minecraft.client.renderer.entity.RenderSnowball(duckEgg));
    }

    private static void registerEntities() {
        // Keep Duckling discriminators away from WAM's 230+ range in this merged mod.
        int id = 720;
        EntityRegistry.registerModEntity(EntityDuck.class, "duck", id++, riftflux.instance, 80, 3, true);
        EntityRegistry.registerModEntity(EntityQuackling.class, "quackling", id++, riftflux.instance, 80, 3, true);
        EntityRegistry.registerModEntity(EntityDuckEgg.class, "duck_egg", id, riftflux.instance, 64, 10, true);
    }

    private static void registerRecipes() {
        GameRegistry.addSmelting(rawDuck, new ItemStack(cookedDuck), 0.35F);

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

    private static void registerNaturalSpawns() {
        if (!ModConfig.enableDucklingNaturalSpawning) {
            return;
        }
        if (ModConfig.ducklingDuckSpawnWeight > 0) {
            BiomeGenBase[] rivers = collectBiomes("river");
            if (rivers.length > 0) {
                EntityRegistry.addSpawn(EntityDuck.class, ModConfig.ducklingDuckSpawnWeight, 3, 4, EnumCreatureType.creature, rivers);
            }
        }
        if (ModConfig.ducklingQuacklingSpawnWeight > 0) {
            BiomeGenBase[] swamps = collectBiomes("swamp", "marsh", "bog", "wetland", "mangrove");
            if (swamps.length > 0) {
                EntityRegistry.addSpawn(EntityQuackling.class, ModConfig.ducklingQuacklingSpawnWeight, 1, 1, EnumCreatureType.creature, swamps);
            }
        }
    }

    private static BiomeGenBase[] collectBiomes(String... nameFragments) {
        List<BiomeGenBase> out = new ArrayList<BiomeGenBase>();
        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        if (biomes == null || nameFragments == null) {
            return new BiomeGenBase[0];
        }
        for (int i = 0; i < biomes.length; i++) {
            BiomeGenBase biome = biomes[i];
            if (biome == null || biome.biomeName == null) {
                continue;
            }
            String name = biome.biomeName.toLowerCase(Locale.ROOT);
            for (int j = 0; j < nameFragments.length; j++) {
                if (name.contains(nameFragments[j])) {
                    out.add(biome);
                    break;
                }
            }
        }
        return out.toArray(new BiomeGenBase[out.size()]);
    }

    private static void initializeGeckoLibClient() {
        if (BedrockLibrary.instance == null) {
            new BedrockLibrary(new File("./particle"));
        }
        GeckoLib.initialize();
    }
}
