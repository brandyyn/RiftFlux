package com.voidsrift.riftflux.hotsprings;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public final class HotSpringsContent {
    private static final String FLUID_NAME = "riftflux.spring_water";

    public static Fluid springWaterFluid;
    public static Block springWaterBlock;
    public static Item springWaterBucket;
    public static BiomeGenBase hotSpringsBiome;

    private static boolean preInited;
    private static boolean initialized;

    private HotSpringsContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableHotSpringsModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;
        if (!isEnabled()) {
            return;
        }

        springWaterFluid = FluidRegistry.getFluid(FLUID_NAME);
        if (springWaterFluid == null) {
            springWaterFluid = new Fluid(FLUID_NAME)
                    .setDensity(1000)
                    .setViscosity(1000)
                    .setTemperature(300);
            FluidRegistry.registerFluid(springWaterFluid);
        }

        springWaterBlock = new BlockSpringWater(springWaterFluid);
        springWaterFluid.setBlock(springWaterBlock);
        GameRegistry.registerBlock(springWaterBlock, "spring_water");

        springWaterBucket = new ItemBucket(springWaterBlock)
                .setContainerItem(Items.bucket)
                .setCreativeTab(CreativeTabs.tabMisc)
                .setTextureName("riftflux:spring_water_bucket")
                .setUnlocalizedName("riftflux.spring_water_bucket");
        GameRegistry.registerItem(springWaterBucket, "spring_water_bucket");
        FluidContainerRegistry.registerFluidContainer(
                new FluidStack(springWaterFluid, FluidContainerRegistry.BUCKET_VOLUME),
                new ItemStack(springWaterBucket),
                new ItemStack(Items.bucket)
        );

        int biomeId = resolveBiomeId(ModConfig.hotSpringsBiomeId);
        if (biomeId >= 0) {
            hotSpringsBiome = new BiomeGenHotSprings(biomeId);
        }

        MinecraftForge.EVENT_BUS.register(new BucketHandler());
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized) {
            return;
        }
        initialized = true;
        if (!isEnabled()) {
            return;
        }

        GameRegistry.registerWorldGenerator(new HotSpringsLakeWorldGenerator(), 0);
        if (hotSpringsBiome == null) {
            return;
        }

        if (ModConfig.hotSpringsBiomeWeight > 0) {
            BiomeManager.addBiome(
                    BiomeManager.BiomeType.COOL,
                    new BiomeManager.BiomeEntry(hotSpringsBiome, ModConfig.hotSpringsBiomeWeight)
            );
        }
        BiomeDictionary.registerBiomeType(
                hotSpringsBiome,
                BiomeDictionary.Type.HILLS,
                BiomeDictionary.Type.FOREST,
                BiomeDictionary.Type.WATER
        );
        BiomeManager.addSpawnBiome(hotSpringsBiome);
        BiomeManager.addStrongholdBiome(hotSpringsBiome);
        if (ModConfig.hotSpringsAllowVillages) {
            BiomeManager.addVillageBiome(hotSpringsBiome, true);
        }
    }

    private static int resolveBiomeId(int configuredId) {
        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        if (biomes == null || biomes.length == 0) {
            FMLLog.severe("[RiftFlux] Unable to register Hot Springs biome: biome array is unavailable.");
            return -1;
        }
        if (isFreeBiomeId(biomes, configuredId)) {
            return configuredId;
        }
        for (int i = Math.max(0, configuredId + 1); i < biomes.length; i++) {
            if (biomes[i] == null) {
                logBiomeFallback(configuredId, biomes, i);
                return i;
            }
        }
        for (int i = 0; i < biomes.length; i++) {
            if (biomes[i] == null) {
                logBiomeFallback(configuredId, biomes, i);
                return i;
            }
        }
        FMLLog.severe("[RiftFlux] Unable to register Hot Springs biome: no free biome IDs remain.");
        return -1;
    }

    private static void logBiomeFallback(int configuredId, BiomeGenBase[] biomes, int fallbackId) {
        String occupiedBy = "unknown biome";
        if (configuredId >= 0
                && configuredId < biomes.length
                && biomes[configuredId] != null
                && biomes[configuredId].biomeName != null) {
            occupiedBy = biomes[configuredId].biomeName;
        }
        FMLLog.warning(
                "[RiftFlux] Hot Springs biome ID %d is occupied by %s; using free biome ID %d instead.",
                configuredId,
                occupiedBy,
                fallbackId
        );
    }

    private static boolean isFreeBiomeId(BiomeGenBase[] biomes, int id) {
        return id >= 0 && id < biomes.length && biomes[id] == null;
    }

    public static final class BucketHandler {
        @SubscribeEvent
        public void onBucketFill(FillBucketEvent event) {
            MovingObjectPosition target = event.target;
            if (event.current == null
                    || event.current.getItem() != Items.bucket
                    || target == null
                    || event.world.getBlock(target.blockX, target.blockY, target.blockZ) != springWaterBlock
                    || event.world.getBlockMetadata(target.blockX, target.blockY, target.blockZ) != 0) {
                return;
            }
            if (!event.entityPlayer.canPlayerEdit(
                    target.blockX,
                    target.blockY,
                    target.blockZ,
                    target.sideHit,
                    event.current)) {
                return;
            }

            event.world.setBlockToAir(target.blockX, target.blockY, target.blockZ);
            event.result = new ItemStack(springWaterBucket);
            event.setResult(Result.ALLOW);
        }
    }
}
