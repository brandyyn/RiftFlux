package com.voidsrift.riftflux.gokistats;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.PrefixedConfiguration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.command.ICommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public final class GokiStatsContent {
    private static Object gokiStats;
    private static Object proxy;
    private static Object packetPipeline;
    private static boolean preInited;
    private static boolean initialized;
    private static boolean postInited;
    private static boolean serverCommandRegistered;
    private static boolean externalWarned;

    private GokiStatsContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableGokiStatsModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (!isEnabled() || preInited || shouldSkipForExternalMod()) {
            return;
        }
        preInited = true;

        try {
            Class<?> gokiStatsClass = Class.forName("goki.stats.GokiStats");
            gokiStats = gokiStatsClass.newInstance();
            proxy = createProxy();
            setStaticField(gokiStatsClass, "instance", gokiStats);
            setStaticField(gokiStatsClass, "proxy", proxy);
            packetPipeline = getStaticField(gokiStatsClass, "packetPipeline");

            loadConfig(event);
            invokeNoArg(proxy, "registerHandlers");
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                registerClientKeybindings();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to pre-initialize integrated GokiStats module", e);
        }
    }

    public static void init(FMLInitializationEvent event) {
        if (!isEnabled() || !preInited || initialized || shouldSkipForExternalMod()) {
            return;
        }
        initialized = true;

        try {
            invokeNoArg(packetPipeline, "initialise");
            registerPacket("goki.stats.handlers.PacketStatSync");
            registerPacket("goki.stats.handlers.PacketStatAlter");
            registerPacket("goki.stats.handlers.PacketSyncXP");
            registerPacket("goki.stats.handlers.PacketSyncStatConfig");
            MinecraftForge.EVENT_BUS.register(Class.forName("goki.stats.Events").newInstance());
            FMLCommonHandler.instance().bus().register(Class.forName("goki.stats.handlers.TickHandler").newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize integrated GokiStats module", e);
        }
    }

    public static void postInit(FMLPostInitializationEvent event) {
        if (!isEnabled() || !initialized || postInited || shouldSkipForExternalMod()) {
            return;
        }
        postInited = true;

        try {
            invokeNoArg(packetPipeline, "postInitialise");
            forcePlayerApiDisabled();
            Class<?> statClass = Class.forName("goki.stats.stats.Stat");
            Configuration configuration = getGokiConfiguration();
            invokeStatic(statClass, "saveAllStatsToConfiguration", Configuration.class, configuration);
            invokeStatic(statClass, "saveGlobalMultipliers", Configuration.class, configuration);
            configuration.save();
        } catch (Exception e) {
            throw new RuntimeException("Failed to post-initialize integrated GokiStats module", e);
        }
    }

    public static void serverStarting(FMLServerStartingEvent event) {
        if (!isEnabled() || !initialized || serverCommandRegistered || shouldSkipForExternalMod()) {
            return;
        }
        serverCommandRegistered = true;

        try {
            event.registerServerCommand((ICommand) Class.forName("goki.stats.StatsCommand").newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Failed to register integrated GokiStats command", e);
        }
    }

    private static boolean shouldSkipForExternalMod() {
        if (!Loader.isModLoaded("gokiStats")) {
            return false;
        }
        if (!externalWarned) {
            externalWarned = true;
            FMLLog.warning("[RiftFlux] Skipping integrated GokiStats because an external gokiStats mod is already loaded.");
        }
        return true;
    }

    private static Object createProxy() throws Exception {
        String className = FMLCommonHandler.instance().getSide() == Side.CLIENT
                ? "goki.stats.client.ClientProxy"
                : "goki.stats.CommonProxy";
        return Class.forName(className).newInstance();
    }

    private static void loadConfig(FMLPreInitializationEvent event) throws Exception {
        Configuration backingConfiguration = ModConfig.config;
        if (backingConfiguration == null) {
            throw new IllegalStateException("RiftFlux config is not initialized before GokiStats integration");
        }
        Configuration configuration = new PrefixedConfiguration(backingConfiguration, "gokistats");
        Class<?> referenceClass = Class.forName("goki.stats.lib.Reference");
        setStaticField(referenceClass, "configuration", configuration);
        configuration.load();

        Class<?> statClass = Class.forName("goki.stats.stats.Stat");
        invokeStatic(statClass, "loadOptions", Configuration.class, configuration);
        invokeStatic(statClass, "loadAllStatsFromConfiguration", Configuration.class, configuration);
        forcePlayerApiDisabled();
    }

    private static void registerPacket(String className) throws Exception {
        Method method = packetPipeline.getClass().getMethod("registerPacket", Class.class);
        method.invoke(packetPipeline, Class.forName(className));
    }

    private static void registerClientKeybindings() throws Exception {
        Class.forName("com.voidsrift.riftflux.gokistats.GokiStatsClientHooks")
                .getMethod("register")
                .invoke(null);
    }

    private static void forcePlayerApiDisabled() throws Exception {
        setStaticField(Class.forName("goki.stats.lib.Reference"), "isPlayerAPILoaded", false);
    }

    private static Configuration getGokiConfiguration() throws Exception {
        return (Configuration) getStaticField(Class.forName("goki.stats.lib.Reference"), "configuration");
    }

    private static void invokeNoArg(Object target, String methodName) throws Exception {
        Method method = target.getClass().getMethod(methodName);
        method.invoke(target);
    }

    private static void invokeStatic(Class<?> owner, String methodName, Class<?> parameterType, Object argument) throws Exception {
        Method method = owner.getMethod(methodName, parameterType);
        method.invoke(null, argument);
    }

    private static void setStaticField(Class<?> owner, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static Object getStaticField(Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(null);
    }
}
