package com.voidsrift.riftflux.combat.torohealth;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.combat.torohealth.client.event.ToroHealthEventHandler;
import com.voidsrift.riftflux.combat.torohealth.server.DamageSyncEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

public final class ToroHealthContent {
    private static boolean initialized;
    private static boolean clientInitialized;

    private ToroHealthContent() {
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized) {
            return;
        }
        initialized = true;

        if (!isEnabled()) {
            return;
        }

        MinecraftForge.EVENT_BUS.register(new DamageSyncEventHandler());

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            initClient();
        }
    }

    public static void initClient() {
        if (clientInitialized) {
            return;
        }
        clientInitialized = true;

        if (!isEnabled() || !ModConfig.toroHealthShowDamageParticles) {
            return;
        }

        MinecraftForge.EVENT_BUS.register(new ToroHealthEventHandler());
    }

    public static boolean isEnabled() {
        return ModConfig.enableToroHealthModule;
    }
}
