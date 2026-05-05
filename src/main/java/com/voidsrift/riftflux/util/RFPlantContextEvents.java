package com.voidsrift.riftflux.util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

public final class RFPlantContextEvents {
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event != null) {
            RFPlantContext.clearWorld(event.world);
        }
    }
}
