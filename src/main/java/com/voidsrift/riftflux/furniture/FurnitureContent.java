package com.voidsrift.riftflux.furniture;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.furniture.block.BlockBedsideCabinet;
import com.voidsrift.riftflux.furniture.block.BlockCabinet;
import com.voidsrift.riftflux.furniture.block.BlockDoorBell;
import com.voidsrift.riftflux.furniture.block.BlockFurnitureFence;
import com.voidsrift.riftflux.furniture.block.BlockFurnitureWindowDecoration;
import com.voidsrift.riftflux.furniture.block.BlockStonePath;
import com.voidsrift.riftflux.furniture.client.render.RenderBedsideCabinet;
import com.voidsrift.riftflux.furniture.client.render.RenderCabinet;
import com.voidsrift.riftflux.furniture.client.render.RenderDoorBell;
import com.voidsrift.riftflux.furniture.client.render.RenderFurnitureFence;
import com.voidsrift.riftflux.furniture.client.render.RenderStonePath;
import com.voidsrift.riftflux.furniture.client.render.RenderWindowDecoration;
import com.voidsrift.riftflux.furniture.tileentity.TileEntityBedsideCabinet;
import com.voidsrift.riftflux.furniture.tileentity.TileEntityCabinet;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class FurnitureContent {
    private static final String CURTAIN_ICON = "riftflux:furniture_curtains";
    private static final int DEFAULT_CURTAIN_COLOR = 14;
    private static final String[] CURTAIN_COLOR_KEYS = {
            "white", "orange", "magenta", "light_blue",
            "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue",
            "brown", "green", "red", "black"
    };
    private static final String[] CURTAIN_COLOR_NAMES = {
            "White", "Orange", "Magenta", "LightBlue",
            "Yellow", "Lime", "Pink", "Gray",
            "LightGray", "Cyan", "Purple", "Blue",
            "Brown", "Green", "Red", "Black"
    };

    public static final Block[] curtains = new Block[16];
    public static Block curtain;
    public static Block blind;
    public static Block stonePath;
    public static Block cabinet;
    public static Block bedsideCabinet;
    public static Block whiteFence;
    public static Block blackFence;
    public static Block doorBell;

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;

    private FurnitureContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableFurnitureModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!isEnabled()) {
            return;
        }

        registerCurtains();

        blind = new BlockFurnitureWindowDecoration(true, "riftflux:furniture_blinds", -1).setBlockName("furnitureBlind");
        stonePath = new BlockStonePath().setBlockName("furnitureStonePath");
        cabinet = new BlockCabinet().setBlockName("furnitureCabinet");
        bedsideCabinet = new BlockBedsideCabinet().setBlockName("furnitureBedsideCabinet");
        whiteFence = new BlockFurnitureFence(false, "riftflux:furniture_white_fence").setBlockName("furnitureWhiteFence");
        blackFence = new BlockFurnitureFence(true, "riftflux:furniture_black_fence").setBlockName("furnitureBlackFence");
        doorBell = new BlockDoorBell().setBlockName("furnitureDoorBell");

        GameRegistry.registerBlock(blind, "furniture_blind");
        GameRegistry.registerBlock(stonePath, "furniture_stone_path");
        GameRegistry.registerBlock(cabinet, "furniture_cabinet");
        GameRegistry.registerBlock(bedsideCabinet, "furniture_bedside_cabinet");
        GameRegistry.registerBlock(whiteFence, "furniture_white_fence");
        GameRegistry.registerBlock(blackFence, "furniture_black_fence");
        GameRegistry.registerBlock(doorBell, "furniture_door_bell");

        GameRegistry.registerTileEntity(TileEntityCabinet.class, Constants.MODID + ":furniture_cabinet");
        GameRegistry.registerTileEntity(TileEntityBedsideCabinet.class, Constants.MODID + ":furniture_bedside_cabinet");
    }

    public static void initClient() {
        if (clientInited) {
            return;
        }
        clientInited = true;

        if (!isEnabled()) {
            return;
        }

        if (FurnitureRenderIds.windowDecorationRenderId < 0) {
            FurnitureRenderIds.windowDecorationRenderId = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new RenderWindowDecoration(FurnitureRenderIds.windowDecorationRenderId));
        }
        if (FurnitureRenderIds.stonePathRenderId < 0) {
            FurnitureRenderIds.stonePathRenderId = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new RenderStonePath(FurnitureRenderIds.stonePathRenderId));
        }
        if (FurnitureRenderIds.cabinetRenderId < 0) {
            FurnitureRenderIds.cabinetRenderId = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new RenderCabinet(FurnitureRenderIds.cabinetRenderId));
        }
        if (FurnitureRenderIds.bedsideCabinetRenderId < 0) {
            FurnitureRenderIds.bedsideCabinetRenderId = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new RenderBedsideCabinet(FurnitureRenderIds.bedsideCabinetRenderId));
        }
        if (FurnitureRenderIds.fenceRenderId < 0) {
            FurnitureRenderIds.fenceRenderId = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new RenderFurnitureFence(FurnitureRenderIds.fenceRenderId));
        }
        if (FurnitureRenderIds.doorBellRenderId < 0) {
            FurnitureRenderIds.doorBellRenderId = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new RenderDoorBell(FurnitureRenderIds.doorBellRenderId));
        }
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        if (!isEnabled()) {
            return;
        }

        GameRegistry.addRecipe(new ItemStack(cabinet), "***", "*@*", "***", '*', Blocks.planks, '@', Blocks.trapdoor);
        GameRegistry.addRecipe(new ItemStack(bedsideCabinet), "***", "*@*", "*@*", '*', Blocks.planks, '@', Blocks.trapdoor);
        for (int color = 0; color < curtains.length; ++color) {
            GameRegistry.addRecipe(
                    new ItemStack(curtains[color], 2),
                    "@@@",
                    "* *",
                    "@ @",
                    '*',
                    Items.gold_nugget,
                    '@',
                    new ItemStack(Blocks.wool, 1, color)
            );
        }
        GameRegistry.addRecipe(new ItemStack(blind, 2), "***", "***", "***", '*', Items.stick);
        GameRegistry.addRecipe(new ItemStack(stonePath, 8), "* *", " * ", "* *", '*', Blocks.cobblestone);
        GameRegistry.addShapelessRecipe(new ItemStack(whiteFence, 2), Blocks.fence, new ItemStack(Items.dye, 1, 15));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blackFence, 2), Blocks.fence, "dyeBlack"));
        GameRegistry.addShapelessRecipe(new ItemStack(doorBell), Blocks.noteblock, Blocks.stone_button);
    }

    private static void registerCurtains() {
        for (int color = 0; color < curtains.length; ++color) {
            boolean defaultCurtain = color == DEFAULT_CURTAIN_COLOR;
            String blockName = defaultCurtain
                    ? "furnitureCurtain"
                    : "furnitureCurtain" + CURTAIN_COLOR_NAMES[color];
            String registryName = defaultCurtain
                    ? "furniture_curtain"
                    : "furniture_curtain_" + CURTAIN_COLOR_KEYS[color];

            Block block = new BlockFurnitureWindowDecoration(false, getCurtainIconName(color), color).setBlockName(blockName);
            curtains[color] = block;
            GameRegistry.registerBlock(block, registryName);

            if (defaultCurtain) {
                curtain = block;
            }
        }
    }

    private static String getCurtainIconName(int color) {
        return color == DEFAULT_CURTAIN_COLOR
                ? CURTAIN_ICON
                : "riftflux:furniture_curtains_" + CURTAIN_COLOR_KEYS[color];
    }
}
