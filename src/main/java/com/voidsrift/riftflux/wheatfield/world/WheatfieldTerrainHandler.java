package com.voidsrift.riftflux.wheatfield.world;

import com.voidsrift.riftflux.wheatfield.BiomeGenWheatfield;
import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
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
        int wheatfieldBiomeId = WheatfieldTerrainUtil.getWheatfieldBiomeId();
        for (long seed = 6100L; seed <= 6106L; seed++) {
            smoothed = new GenLayerWheatfieldRound(seed, smoothed, wheatfieldBiomeId);
        }
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

    @SubscribeEvent
    public void onPopulatePost(PopulateChunkEvent.Post event) {
        if (event.world == null || event.world.isRemote || !WheatfieldTerrainUtil.chunkHasWheatfield(event.world, event.chunkX, event.chunkZ)) {
            return;
        }

        if (WheatfieldContent.wheatfieldBiome instanceof BiomeGenWheatfield) {
            ((BiomeGenWheatfield) WheatfieldContent.wheatfieldBiome).populateBarleyForChunk(event.world, event.chunkX << 4, event.chunkZ << 4);
        }
    }

}
