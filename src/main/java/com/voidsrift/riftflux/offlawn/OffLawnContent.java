package com.voidsrift.riftflux.offlawn;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public final class OffLawnContent {
    public static Block lawnBlock;
    public static Block sunflowerBush;
    public static Block brightSunflower;
    public static Block beanstalk;
    public static Item sunSeed;

    private static boolean preInited;
    private static boolean initialized;

    private OffLawnContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableOffLawnModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited || !isEnabled()) {
            return;
        }
        preInited = true;

        lawnBlock = new BlockOffLawnBlock();
        sunflowerBush = new BlockOffLawnSunflowerBush();
        brightSunflower = new BlockOffLawnSunflowerBush(
                "bright_sunflower",
                "riftflux:offlawn/bright_sunflower_bottom",
                "riftflux:offlawn/bright_sunflower_top"
        );
        beanstalk = new BlockOffLawnBeanstalk();
        sunSeed = new ItemOffLawnSunSeed();

        GameRegistry.registerBlock(lawnBlock, ItemBlock.class, "lawn_block");
        GameRegistry.registerBlock(sunflowerBush, ItemBlock.class, "sunflower_bush");
        GameRegistry.registerBlock(brightSunflower, ItemOffLawnBrightSunflower.class, "bright_sunflower");
        GameRegistry.registerBlock(beanstalk, ItemBlock.class, "beanstalk");
        GameRegistry.registerItem(sunSeed, "sun_seed");
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized || !isEnabled()) {
            return;
        }
        initialized = true;

        registerRecipes();
        if (ModConfig.offLawnEnableSunflowerWorldgen) {
            GameRegistry.registerWorldGenerator(new OffLawnWorldGenerator(), 0);
        }
    }

    public static void initClient() {
    }

    private static void registerRecipes() {
        if (lawnBlock != null) {
            GameRegistry.addShapelessRecipe(
                    new ItemStack(lawnBlock, 1),
                    new ItemStack(Blocks.dirt, 1),
                    new ItemStack(Items.wheat_seeds, 1)
            );
            GameRegistry.addShapelessRecipe(
                    new ItemStack(lawnBlock, 1),
                    new ItemStack(Blocks.dirt, 1),
                    new ItemStack(Blocks.yellow_flower, 1, 0)
            );
            if (sunSeed != null) {
                GameRegistry.addShapelessRecipe(
                        new ItemStack(lawnBlock, 1),
                        new ItemStack(Blocks.dirt, 1),
                        new ItemStack(sunSeed, 1)
                );
            }
        }

        if (beanstalk != null && sunSeed != null) {
            GameRegistry.addShapelessRecipe(
                    new ItemStack(beanstalk, 4),
                    new ItemStack(Blocks.vine, 1),
                    new ItemStack(sunSeed, 1)
            );
        }

        if (sunflowerBush != null) {
            registerSunflowerRecipes(sunflowerBush);
        }
        if (brightSunflower != null) {
            registerSunflowerRecipes(brightSunflower);
        }

        if (sunSeed != null) {
            GameRegistry.addRecipe(
                    new ItemStack(sunSeed, 1),
                    "#",
                    '#', new ItemStack(Blocks.double_plant, 1, 0)
            );
        }
    }

    public static Block getMixedSunflowerVariant(java.util.Random random) {
        if (sunflowerBush != null && brightSunflower != null) {
            return random != null && random.nextBoolean() ? brightSunflower : sunflowerBush;
        }
        return sunflowerBush != null ? sunflowerBush : brightSunflower;
    }

    public static boolean hasSunflowerBushes() {
        return sunflowerBush != null || brightSunflower != null;
    }

    private static void registerSunflowerRecipes(Block block) {
        GameRegistry.addShapelessRecipe(
                new ItemStack(block, 1, 1),
                new ItemStack(Blocks.double_plant, 1, 0),
                new ItemStack(Blocks.tallgrass, 1, 1)
        );
        if (lawnBlock != null) {
            GameRegistry.addShapelessRecipe(
                    new ItemStack(block, 1, 1),
                    new ItemStack(Blocks.double_plant, 1, 0),
                    new ItemStack(lawnBlock, 1)
            );
        }
    }
}
