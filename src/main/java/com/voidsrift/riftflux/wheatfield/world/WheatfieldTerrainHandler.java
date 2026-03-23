package com.voidsrift.riftflux.wheatfield.world;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.WorldTypeEvent;

public final class WheatfieldTerrainHandler {
    @SubscribeEvent
    public void onInitBiomeGens(WorldTypeEvent.InitBiomeGens event) {
        if (!WheatfieldTerrainUtil.isActive() || event == null || event.newBiomeGens == null || event.newBiomeGens.length < 2) {
            return;
        }

        GenLayer smoothed = event.newBiomeGens[0];
        smoothed = new GenLayerWheatfieldRound(6100L, smoothed, WheatfieldTerrainUtil.getWheatfieldBiomeId());
        smoothed.initWorldGenSeed(event.seed);

        GenLayerVoronoiZoom voronoi = new GenLayerVoronoiZoom(10L, smoothed);
        voronoi.initWorldGenSeed(event.seed);

        event.newBiomeGens[0] = smoothed;
        event.newBiomeGens[1] = voronoi;
        if (event.newBiomeGens.length > 2) {
            event.newBiomeGens[2] = smoothed;
        }
    }

    @SubscribeEvent
    public void onPopulate(PopulateChunkEvent.Populate event) {
        if (!WheatfieldTerrainUtil.chunkIsPredominantlyWheatfield(event.world, event.chunkX, event.chunkZ)) {
            return;
        }

        if (event.type == PopulateChunkEvent.Populate.EventType.LAKE
                || event.type == PopulateChunkEvent.Populate.EventType.LAVA) {
            event.setResult(Event.Result.DENY);
        }
    }
}
