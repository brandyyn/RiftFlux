package com.voidsrift.riftflux.terramine;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

public final class TerraLifecycleEvents {
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event != null && event.world != null && !event.world.isRemote) {
            BlockIceRodIce.clearCrackStatesForWorld(event.world);
        }
    }
}
