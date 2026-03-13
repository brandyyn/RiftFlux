/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.IWorldGenerator
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraftforge.common.MinecraftForge
 */
package tk.nukeduck.hearts;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Random;
import net.minecraftforge.common.MinecraftForge;
import tk.nukeduck.hearts.HeartsConfig;
import tk.nukeduck.hearts.block.TileEntityHeartCrystal;
import tk.nukeduck.hearts.block.TileEntityHeartLantern;
import tk.nukeduck.hearts.event.CommonEvents;
import tk.nukeduck.hearts.event.WorldGeneratorHearts;
import tk.nukeduck.hearts.network.IProxy;
import tk.nukeduck.hearts.registry.HeartsBlocks;
import tk.nukeduck.hearts.registry.HeartsCrafting;
import tk.nukeduck.hearts.registry.HeartsItems;

public class HeartCrystal {
    public static HeartCrystal instance;
    public static final String MODID = "hearts";
    public static IProxy proxy;
    public static CommonEvents events;
    public static HeartsConfig config;
    public static final Random random;

    public void preInit(FMLPreInitializationEvent e) {
        config = new HeartsConfig(e.getSuggestedConfigurationFile()).load();
        HeartsBlocks.init();
        HeartsItems.init();
        HeartsCrafting.init();
    }

    public void init(FMLInitializationEvent e) {
        GameRegistry.registerTileEntity(TileEntityHeartCrystal.class, (String)"HeartCrystal");
        GameRegistry.registerTileEntity(TileEntityHeartLantern.class, (String)"HeartLantern");
        GameRegistry.registerWorldGenerator((IWorldGenerator)new WorldGeneratorHearts(), (int)1);
        events = new CommonEvents();
        MinecraftForge.EVENT_BUS.register((Object)events);
        FMLCommonHandler.instance().bus().register((Object)events);
    }

    public void postInit(FMLPostInitializationEvent e) {
        proxy.renderInit();
    }

    static {
        random = new Random();
    }
}
