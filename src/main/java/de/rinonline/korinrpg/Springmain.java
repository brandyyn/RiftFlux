/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.event.FMLServerStartingEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.command.ICommand
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.config.Configuration
 */
package de.rinonline.korinrpg;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import de.rinonline.korinrpg.BasisClientProxy;
import de.rinonline.korinrpg.BasisCommonProxy;
import de.rinonline.korinrpg.Commands.CommandRefillStamina;
import de.rinonline.korinrpg.Commands.CommandSubtracStamina;
import de.rinonline.korinrpg.ConfigurationMoD2;
import de.rinonline.korinrpg.EnchRegistry;
import de.rinonline.korinrpg.Helper.Gui.InterfaceGUI2;
import de.rinonline.korinrpg.Helper.Network.SuperPacketDispatcher;
import de.rinonline.korinrpg.Helper.RINEventHandler2;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class Springmain {
    public static final String MODID = "dss";
    public static final String name = "Darwin Sprinting";
    public static final String VERSION = "a57";
    public static Springmain instance;
    public static Configuration config;
    public static BasisCommonProxy proxy;
    public static BasisClientProxy proxyclient;

    public Springmain() {
        instance = this;
    }

    public static void preInit(FMLPreInitializationEvent preEvent) {
        ensureInitialized();
        config = new Configuration(preEvent.getSuggestedConfigurationFile());
        ConfigurationMoD2.loadConfig();
        if (ConfigurationMoD2.EnableEnchantment) {
            new EnchRegistry();
        }
    }

    public static void init(FMLInitializationEvent event) {
        ensureInitialized();
        RINEventHandler2 events = new RINEventHandler2();
        MinecraftForge.EVENT_BUS.register((Object)events);
        SuperPacketDispatcher.registerPackets();
    }

    public static void postInit(FMLPostInitializationEvent postEvent) {
        ensureInitialized();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            MinecraftForge.EVENT_BUS.register((Object)new InterfaceGUI2(Minecraft.getMinecraft()));
        }
    }

    public static void serverLoad(FMLServerStartingEvent event) {
        ensureInitialized();
        event.registerServerCommand(new CommandRefillStamina());
        event.registerServerCommand(new CommandSubtracStamina());
    }

    private static void ensureInitialized() {
        if (instance == null) {
            instance = new Springmain();
        }
        if (proxy == null) {
            proxy = createProxy();
            if (proxy instanceof BasisClientProxy) {
                proxyclient = (BasisClientProxy)proxy;
            }
        }
    }

    private static BasisCommonProxy createProxy() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            return new BasisClientProxy();
        }
        return new BasisCommonProxy();
    }
}
