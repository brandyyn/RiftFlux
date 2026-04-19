/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.Loader
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.event.FMLServerStartingEvent
 *  cpw.mods.fml.common.network.IGuiHandler
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandManager
 *  net.minecraft.command.ServerCommandManager
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.config.Configuration
 */
package goki.stats;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import goki.stats.CommonProxy;
import goki.stats.Events;
import goki.stats.StatsCommand;
import goki.stats.client.gui.GuiHandler;
import goki.stats.handlers.PacketPipeline;
import goki.stats.handlers.PacketStatAlter;
import goki.stats.handlers.PacketStatSync;
import goki.stats.handlers.PacketSyncStatConfig;
import goki.stats.handlers.PacketSyncXP;
import goki.stats.handlers.TickHandler;
import goki.stats.lib.Reference;
import goki.stats.stats.Stat;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class GokiStats {
    public static final PacketPipeline packetPipeline = new PacketPipeline();
    public static GokiStats instance;
    public static CommonProxy proxy;

    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        Reference.configuration = new Configuration(event.getSuggestedConfigurationFile());
        Reference.configuration.load();
        if (!Reference.configuration.get("Version", "Configuration Version", "v1").getString().equals("v1")) {
            System.err.println("gokiStats configuration file has changed! May cause errors! Delete the configuration file and relaunch.");
        }
        Stat.loadOptions(Reference.configuration);
        Stat.loadAllStatsFromConfiguration(Reference.configuration);
        proxy.registerKeybinding();
        proxy.registerHandlers();
    }

    public void init(FMLInitializationEvent event) {
        packetPipeline.initialise();
        packetPipeline.registerPacket(PacketStatSync.class);
        packetPipeline.registerPacket(PacketStatAlter.class);
        packetPipeline.registerPacket(PacketSyncXP.class);
        packetPipeline.registerPacket(PacketSyncStatConfig.class);
        MinecraftForge.EVENT_BUS.register((Object)new Events());
        NetworkRegistry.INSTANCE.registerGuiHandler((Object)this, (IGuiHandler)new GuiHandler());
        FMLCommonHandler.instance().bus().register((Object)new TickHandler());
    }

    public void postInit(FMLPostInitializationEvent event) {
        packetPipeline.postInitialise();
        if (Loader.isModLoaded((String)"NOT-PlayerAPI-sjkdghdsjkghghdkghdgjk")) {
            Reference.isPlayerAPILoaded = true;
            Stat.STAT_ATHLETICISM.enabled = false;
            Stat.STAT_CLIMBING.enabled = false;
        }
        Stat.saveAllStatsToConfiguration(Reference.configuration);
        Stat.saveGlobalMultipliers(Reference.configuration);
        Reference.configuration.save();
    }

    public void serverStart(FMLServerStartingEvent event) {
        MinecraftServer server = MinecraftServer.getServer();
        ICommandManager command = server.getCommandManager();
        ServerCommandManager serverCommand = (ServerCommandManager)command;
        serverCommand.registerCommand((ICommand)new StatsCommand());
    }
}
