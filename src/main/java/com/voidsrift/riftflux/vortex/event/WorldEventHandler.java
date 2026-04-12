package com.voidsrift.riftflux.vortex.event;

import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import com.google.common.collect.ImmutableSetMultimap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.ForceChunkEvent;
import net.minecraftforge.common.ForgeChunkManager.UnforceChunkEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.vortex.lib.helper.LogHelper;

public class WorldEventHandler {
    private final Set<Integer> queuedDimensions = new HashSet<Integer>();

    public WorldEventHandler() {
        if (isEnabled()) {
            LogHelper.info("[Unloader] WorldEventHandler registered.");
        }
    }

    private static boolean isEnabled() {
        return ModConfig.enableUnloader;
    }

    private boolean isBlacklisted(int dimension) {
        if (ModConfig.unloaderBlacklistedDimensions == null) {
            return false;
        }
        for (int blacklisted : ModConfig.unloaderBlacklistedDimensions) {
            if (dimension == blacklisted) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!isEnabled()) {
            return;
        }
        if (!event.world.isRemote && event.world.provider != null) {
            queuedDimensions.remove(event.world.provider.dimensionId);
            LogHelper.info("[Unloader] Dimension " + event.world.provider.dimensionId + " unloaded.");
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!isEnabled()) {
            return;
        }
        if (!event.world.isRemote && event.world instanceof WorldServer) {
            MinecraftServer server = MinecraftServer.getServer();
            // Startup pass: if no players are online, unload preloaded dimensions immediately.
            if (server != null && server.getConfigurationManager() != null
                    && server.getConfigurationManager().playerEntityList.isEmpty()) {
                tryQueueUnload((WorldServer) event.world);
            }
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        if (!isEnabled()) {
            return;
        }
        if (!event.world.isRemote && event.world instanceof WorldServer) {
            tryQueueUnload((WorldServer) event.world);
        }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (!isEnabled()) {
            return;
        }
        if (!event.world.isRemote && event.world instanceof WorldServer) {
            tryQueueUnload((WorldServer) event.world);
        }
    }

    @SubscribeEvent
    public void onChunkForce(ForceChunkEvent event) {
        if (!isEnabled()) {
            return;
        }
        // A forced chunk was added, so this world should remain active.
    }

    @SubscribeEvent
    public void onChunkUnforce(UnforceChunkEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (event.ticket != null && event.ticket.world != null && !event.ticket.world.isRemote) {
            tryQueueUnload((WorldServer) event.ticket.world);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!isEnabled()) {
            return;
        }
        WorldServer fromWorld = DimensionManager.getWorld(event.fromDim);
        if (fromWorld != null) {
            tryQueueUnload(fromWorld);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!isEnabled()) {
            return;
        }
        unloadIdleLoadedDimensions();
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (event.player != null && event.player.worldObj instanceof WorldServer) {
            tryQueueUnload((WorldServer) event.player.worldObj);
        }
    }

    private void unloadIdleLoadedDimensions() {
        Integer[] ids = DimensionManager.getIDs();
        if (ids == null) {
            return;
        }

        for (int dim : ids) {
            WorldServer world = DimensionManager.getWorld(dim);
            if (world != null) {
                tryQueueUnload(world);
            }
        }
    }

    private void tryQueueUnload(WorldServer world) {
        if (!isEnabled() || world == null || world.provider == null) {
            return;
        }

        // Forge iterates unloadQueue in DimensionManager.unloadWorlds(); adding to it in the same call stack can throw
        // ConcurrentModificationException.
        if (isInsideDimensionUnloadPass()) {
            return;
        }

        int dimension = world.provider.dimensionId;
        if (isBlacklisted(dimension) || !canUnload(world)) {
            return;
        }

        if (!queuedDimensions.add(dimension)) {
            return;
        }

        LogHelper.info("[Unloader] Queuing dimension " + dimension + " for unload.");
        DimensionManager.unloadWorld(dimension);
    }

    private boolean isInsideDimensionUnloadPass() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            if ("net.minecraftforge.common.DimensionManager".equals(element.getClassName())
                    && "unloadWorlds".equals(element.getMethodName())) {
                return true;
            }
        }
        return false;
    }

    private boolean canUnload(WorldServer world) {
        ImmutableSetMultimap<?, ?> persistentChunks = ForgeChunkManager.getPersistentChunksFor(world);
        if (persistentChunks != null && !persistentChunks.isEmpty()) {
            return false;
        }

        if (!world.playerEntities.isEmpty()) {
            return false;
        }

        return true;
    }
}
