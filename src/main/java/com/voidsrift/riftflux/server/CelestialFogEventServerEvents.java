package com.voidsrift.riftflux.server;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgSyncCelestialFogEvents;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public final class CelestialFogEventServerEvents {
    private static final int DAY_EVENT_START_TICK = 23000;
    private static final int DAY_EVENT_END_TICK = 13000;
    private static final int NIGHT_EVENT_START_TICK = 12000;
    private static final int NIGHT_EVENT_END_TICK = 24000;
    private static final int DAY_EVENT_SALT = 0x47A3B21;
    private static final int NIGHT_EVENT_SALT = 0x7D13F5B;
    private static final int WEATHER_EVENT_SALT = 0x3E9C1A71;
    private static final long UPDATE_INTERVAL_TICKS = 20L;
    private static final long RESYNC_INTERVAL_TICKS = 200L;

    private final Map<Integer, DimensionState> states = new HashMap<Integer, DimensionState>();

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event == null || event.world == null || event.world.isRemote || event.phase != TickEvent.Phase.END) {
            return;
        }

        long totalTime = event.world.getTotalWorldTime();
        if (totalTime % UPDATE_INTERVAL_TICKS != 0L) {
            return;
        }

        updateAndSync(event.world, false, null);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        syncPlayer(event == null ? null : event.player);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncPlayer(event == null ? null : event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncPlayer(event == null ? null : event.player);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event == null || event.world == null || event.world.isRemote) {
            return;
        }
        states.remove(getDimensionId(event.world));
    }

    private void syncPlayer(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP) || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        updateAndSync(player.worldObj, true, (EntityPlayerMP) player);
    }

    private void updateAndSync(World world, boolean force, EntityPlayerMP target) {
        if (world == null || world.provider == null || RFNetwork.CH == null) {
            return;
        }

        int dimensionId = getDimensionId(world);
        DimensionState state = getState(dimensionId);
        boolean changed = updateState(world, state);
        long totalTime = world.getTotalWorldTime();

        if (target != null) {
            RFNetwork.CH.sendTo(new MsgSyncCelestialFogEvents(dimensionId, state.dayFog, state.nightFog, state.weatherFog), target);
            return;
        }

        if (force || changed || totalTime - state.lastSyncTick >= RESYNC_INTERVAL_TICKS) {
            state.lastSyncTick = totalTime;
            RFNetwork.CH.sendToDimension(
                    new MsgSyncCelestialFogEvents(dimensionId, state.dayFog, state.nightFog, state.weatherFog),
                    dimensionId
            );
        }
    }

    private DimensionState getState(int dimensionId) {
        DimensionState state = states.get(dimensionId);
        if (state == null) {
            state = new DimensionState();
            states.put(dimensionId, state);
        }
        return state;
    }

    private boolean updateState(World world, DimensionState state) {
        boolean hasSky = world.provider != null && !world.provider.hasNoSky;
        boolean dayFog = hasSky && isTimedEventEnabled(
                world,
                DAY_EVENT_START_TICK,
                DAY_EVENT_END_TICK,
                DAY_EVENT_SALT,
                ModConfig.celestialBetaStyleFogBiomeTintDayEventChancePercent
        );
        boolean nightFog = hasSky && isTimedEventEnabled(
                world,
                NIGHT_EVENT_START_TICK,
                NIGHT_EVENT_END_TICK,
                NIGHT_EVENT_SALT,
                ModConfig.celestialBetaStyleFogBiomeTintNightEventChancePercent
        );
        boolean weatherFog = hasSky && updateWeatherEvent(world, state);

        boolean changed = state.dayFog != dayFog || state.nightFog != nightFog || state.weatherFog != weatherFog;
        state.dayFog = dayFog;
        state.nightFog = nightFog;
        state.weatherFog = weatherFog;
        return changed;
    }

    private boolean updateWeatherEvent(World world, DimensionState state) {
        boolean weatherVisible = world.isRaining()
                || world.isThundering()
                || world.getRainStrength(1.0F) > 0.001F
                || world.getWeightedThunderStrength(1.0F) > 0.001F;
        if (!weatherVisible
                || !ModConfig.celestialBetaStyleFogBiomeTintWeatherEvent
                || ModConfig.celestialBetaStyleFogBiomeTintWeatherEventChancePercent <= 0.0F) {
            state.inWeather = false;
            state.weatherRoll = false;
            return false;
        }

        if (!state.inWeather) {
            state.inWeather = true;
            state.weatherRoll = rollChance(
                    world,
                    world.getTotalWorldTime(),
                    WEATHER_EVENT_SALT,
                    ModConfig.celestialBetaStyleFogBiomeTintWeatherEventChancePercent
            );
        }
        return state.weatherRoll;
    }

    private static boolean isTimedEventEnabled(World world, int startTick, int endTick, int salt, float chancePercent) {
        if (chancePercent <= 0.0F) {
            return false;
        }

        long worldTime = world.getWorldTime();
        long dayIndex = Math.floorDiv(worldTime, 24000L);
        long timeOfDay = worldTime % 24000L;
        if (timeOfDay < 0L) {
            timeOfDay += 24000L;
        }

        boolean wraps = startTick > endTick;
        boolean active = wraps
                ? timeOfDay >= startTick || timeOfDay < endTick
                : timeOfDay >= startTick && timeOfDay < endTick;
        if (!active) {
            return false;
        }

        long period = wraps && timeOfDay < endTick ? dayIndex - 1L : dayIndex;
        return rollChance(world, period, salt, chancePercent);
    }

    private static boolean rollChance(World world, long period, int salt, float chancePercent) {
        float chance = clamp01(chancePercent / 100.0F);
        if (chance <= 0.0F) {
            return false;
        }
        if (chance >= 1.0F) {
            return true;
        }

        int dimension = getDimensionId(world);
        long hash = period;
        hash ^= (long) dimension * 0x9E3779B97F4A7C15L;
        hash ^= (long) salt * 0xBF58476D1CE4E5B9L;
        hash ^= hash >>> 30;
        hash *= 0xBF58476D1CE4E5B9L;
        hash ^= hash >>> 27;
        hash *= 0x94D049BB133111EBL;
        hash ^= hash >>> 31;

        float roll = (float) (hash & 0xFFFFFFL) / (float) 0x1000000;
        return roll < chance;
    }

    private static int getDimensionId(World world) {
        return world != null && world.provider != null ? world.provider.dimensionId : 0;
    }

    private static float clamp01(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        if (value > 1.0F) {
            return 1.0F;
        }
        return value;
    }

    private static final class DimensionState {
        private boolean dayFog;
        private boolean nightFog;
        private boolean weatherFog;
        private boolean inWeather;
        private boolean weatherRoll;
        private long lastSyncTick = Long.MIN_VALUE / 2L;
    }
}
