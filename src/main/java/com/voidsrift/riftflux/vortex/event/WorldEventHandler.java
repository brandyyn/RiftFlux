package com.voidsrift.riftflux.vortex.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Unload;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.vortex.lib.helper.LogHelper;

public class WorldEventHandler {
    private int serverTick;
    private boolean needsRun = false;

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
    public void onWorldLoad(net.minecraftforge.event.world.WorldEvent.Load event) {
        // Trigger on world load
        if (!event.world.isRemote && event.world.provider.dimensionId == 0) {
            needsRun = true;
            serverTick = 0;
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        // Trigger on dimension change
        needsRun = true;
        serverTick = 0;
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }

        if (!ModConfig.enableUnloader) {
            return;
        }

        if (!needsRun) {
            return;
        }

        ++serverTick;

        // Wait for configured delay after trigger
        if (serverTick < (ModConfig.unloaderSeconds * 20)) {
            return;
        }

        // Run unloader once, then clear flag
        needsRun = false;
        serverTick = 0;

        Integer[] ids = DimensionManager.getIDs();
        for (int dimension : ids) {
            // Skip blacklisted dimensions
            if (isBlacklisted(dimension)) {
                continue;
            }

            WorldServer worldServer = DimensionManager.getWorld(dimension);
            if (worldServer == null) {
                continue;
            }

            ChunkProviderServer chunkProvider = worldServer.theChunkProviderServer;
            if (!DimensionManager.shouldLoadSpawn(dimension)
                    && ForgeChunkManager.getPersistentChunksFor(worldServer).isEmpty()
                    && chunkProvider.getLoadedChunkCount() == 0
                    && worldServer.playerEntities.isEmpty()
                    && worldServer.loadedEntityList.isEmpty()
                    && worldServer.loadedTileEntityList.isEmpty()) {
                try {
                    worldServer.saveAllChunks(true, (IProgressUpdate) null);
                } catch (MinecraftException e) {
                    LogHelper.warn("Unloader was unable to save chunks at dimension " + dimension);
                } finally {
                    MinecraftForge.EVENT_BUS.post(new Unload(worldServer));
                    worldServer.flush();
                    DimensionManager.setWorld(dimension, (WorldServer) null);
                }
            }
        }
    }
}
