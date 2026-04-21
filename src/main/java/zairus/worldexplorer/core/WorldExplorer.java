/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.network.IGuiHandler
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.config.Configuration
 */
package zairus.worldexplorer.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import zairus.worldexplorer.core.CommonProxy;
import zairus.worldexplorer.core.IWEAddonMod;
import zairus.worldexplorer.core.WEConfig;
import zairus.worldexplorer.core.block.WorldExplorerBlocks;
import zairus.worldexplorer.core.event.WEEventHandler;
import zairus.worldexplorer.core.gui.GuiHandler;
import zairus.worldexplorer.core.items.WorldExplorerItems;
import zairus.worldexplorer.core.util.network.PacketPipeline;

public class WorldExplorer {
    private static List<IWEAddonMod> registeredMods = new ArrayList<IWEAddonMod>();
    public static Logger logger;
    public static Configuration configuration;
    public static PacketPipeline packetPipeline;
    public static CommonProxy proxy;
    public static WorldExplorer instance;

    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        packetPipeline.initalise();
        WEConfig.init(event.getSuggestedConfigurationFile());
        WorldExplorerItems.init();
    }

    public void init(FMLInitializationEvent event) {
        WorldExplorerItems.register();
        proxy.init(event);
        this.addRecipes();
        WEEventHandler eventHandler = new WEEventHandler();
        FMLCommonHandler.instance().bus().register((Object)eventHandler);
        MinecraftForge.EVENT_BUS.register((Object)eventHandler);
        MinecraftForge.TERRAIN_GEN_BUS.register((Object)eventHandler);
        NetworkRegistry.INSTANCE.registerGuiHandler((Object)instance, (IGuiHandler)new GuiHandler());
    }

    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        packetPipeline.postInitialise();
    }

    private void addRecipes() {
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack(WorldExplorerBlocks.studydesk), (Object[])new Object[]{" j ", "ptp", "pcp", Character.valueOf('j'), WorldExplorerItems.journal, Character.valueOf('p'), Blocks.planks, Character.valueOf('t'), Blocks.crafting_table, Character.valueOf('c'), Blocks.chest});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)WorldExplorerItems.journal), (Object[])new Object[]{Items.compass, Items.clock, Items.writable_book});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)WorldExplorerItems.needle, 6), (Object[])new Object[]{Blocks.cactus});
    }

    public static void registerWEAddonMod(IWEAddonMod mod) {
        registeredMods.add(mod);
    }

    public static List<IWEAddonMod> getRegisteredMods() {
        return registeredMods;
    }

    public static void log(String obj) {
        if (logger == null) {
            logger = Logger.getLogger("WorldExplorer");
        }
        if (obj == null) {
            obj = "null";
        }
        logger.info("[" + FMLCommonHandler.instance().getEffectiveSide() + "] " + obj);
    }

    static {
        packetPipeline = new PacketPipeline();
    }
}
