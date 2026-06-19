package com.voidsrift.riftflux.geostrata.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

public final class GeoStrataDecoGenTextureEvents {
    private static boolean registered;

    private GeoStrataDecoGenTextureEvents() {
    }

    public static void bootstrap() {
        if (!registered) {
            registered = true;
            MinecraftForge.EVENT_BUS.register(new GeoStrataDecoGenTextureEvents());
        }
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0) {
            DecoGenItemRenderer.registerBlockParticleIcons(event.map);
        }
    }
}
