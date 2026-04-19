package com.voidsrift.riftflux.client.sky;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public final class CelestialFogEventClientState {
    private static final Map<Integer, State> STATES = new HashMap<Integer, State>();
    private static boolean registered;

    private CelestialFogEventClientState() {
    }

    public static void bootstrap() {
        if (registered) {
            return;
        }
        registered = true;
        ClientEvents events = new ClientEvents();
        FMLCommonHandler.instance().bus().register(events);
        MinecraftForge.EVENT_BUS.register(events);
    }

    public static void applySync(int dimensionId, boolean dayFog, boolean nightFog, boolean weatherFog) {
        synchronized (STATES) {
            State state = STATES.get(dimensionId);
            if (state == null) {
                state = new State();
                STATES.put(dimensionId, state);
            }
            state.dayFog = dayFog;
            state.nightFog = nightFog;
            state.weatherFog = weatherFog;
        }
    }

    public static boolean hasSync(WorldClient world) {
        return getState(world) != null;
    }

    public static boolean isDayFogActive(WorldClient world) {
        State state = getState(world);
        return state != null && state.dayFog;
    }

    public static boolean isNightFogActive(WorldClient world) {
        State state = getState(world);
        return state != null && state.nightFog;
    }

    public static boolean isWeatherFogActive(WorldClient world) {
        State state = getState(world);
        return state != null && state.weatherFog;
    }

    public static void clearAll() {
        synchronized (STATES) {
            STATES.clear();
        }
    }

    private static void clearDimension(int dimensionId) {
        synchronized (STATES) {
            STATES.remove(dimensionId);
        }
    }

    private static State getState(WorldClient world) {
        if (world == null || world.provider == null) {
            return null;
        }
        synchronized (STATES) {
            return STATES.get(world.provider.dimensionId);
        }
    }

    public static final class ClientEvents {
        @SubscribeEvent
        public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
            clearAll();
        }

        @SubscribeEvent
        public void onWorldUnload(WorldEvent.Unload event) {
            if (event == null || event.world == null || !event.world.isRemote || event.world.provider == null) {
                return;
            }
            clearDimension(event.world.provider.dimensionId);
            if (event.world instanceof WorldClient) {
                FogDistanceGradientState.invalidate((WorldClient) event.world);
            }
        }
    }

    private static final class State {
        private boolean dayFog;
        private boolean nightFog;
        private boolean weatherFog;
    }
}
