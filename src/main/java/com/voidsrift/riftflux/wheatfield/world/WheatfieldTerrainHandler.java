package com.voidsrift.riftflux.wheatfield.world;

import com.voidsrift.riftflux.wheatfield.BiomeGenWheatfield;
import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
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
        smoothed = new GenLayerWheatfieldRoundStacked(smoothed, wheatfieldBiomeId);
        smoothed.initWorldGenSeed(event.seed);

        GenLayerVoronoiZoom voronoi = new GenLayerVoronoiZoom(10L, smoothed);
        voronoi.initWorldGenSeed(event.seed);

        event.newBiomeGens[0] = smoothed;
        event.newBiomeGens[1] = voronoi;
        if (event.newBiomeGens.length > 2) {
            event.newBiomeGens[2] = smoothed;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPopulate(PopulateChunkEvent.Populate event) {
        if (event == null || event.world == null || !WheatfieldTerrainUtil.chunkIsPredominantlyWheatfield(event.world, event.chunkX, event.chunkZ)) {
            return;
        }

        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDecorate(DecorateBiomeEvent.Decorate event) {
        if (event == null || event.world == null || !WheatfieldTerrainUtil.isOverworld(event.world)) {
            return;
        }

        int chunkX = event.chunkX >> 4;
        int chunkZ = event.chunkZ >> 4;
        if (WheatfieldTerrainUtil.chunkIsPredominantlyWheatfield(event.world, chunkX, chunkZ)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onPopulatePost(PopulateChunkEvent.Post event) {
        if (event.world == null || event.world.isRemote || !WheatfieldTerrainUtil.isOverworld(event.world)) {
            return;
        }

        if (WheatfieldContent.wheatfieldBiome instanceof BiomeGenWheatfield) {
            BiomeGenWheatfield wheatfieldBiome = (BiomeGenWheatfield) WheatfieldContent.wheatfieldBiome;
            int chunkBlockX = event.chunkX << 4;
            int chunkBlockZ = event.chunkZ << 4;
            if (!WheatfieldTerrainUtil.chunkHasWheatfield(event.world, event.chunkX, event.chunkZ)) {
                return;
            }

            WheatfieldBiomeSampler sampler = WheatfieldBiomeSampler.forChunk(
                    event.world,
                    event.chunkX,
                    event.chunkZ,
                    BiomeGenWheatfield.getBiomeScanRadius());
            if (sampler == null) {
                return;
            }

            wheatfieldBiome.populateBarleyForChunk(event.world, chunkBlockX, chunkBlockZ, sampler);
        }
    }

}
