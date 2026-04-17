package com.voidsrift.riftflux.offlawn.client;

import com.voidsrift.riftflux.offlawn.OffLawnRenderIds;
import cpw.mods.fml.client.registry.RenderingRegistry;

public final class OffLawnClientContent {
    private OffLawnClientContent() {
    }

    public static void initClient() {
        if (OffLawnRenderIds.sunflowerBushRenderId < 0) {
            OffLawnRenderIds.sunflowerBushRenderId = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new RenderOffLawnSunflowerBush());
        }
    }
}
