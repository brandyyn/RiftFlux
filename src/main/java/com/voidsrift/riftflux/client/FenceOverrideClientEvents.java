package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.fence.FenceOverrideClientState;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.event.world.WorldEvent;

@SideOnly(Side.CLIENT)
public class FenceOverrideClientEvents {

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event != null && event.world != null && event.world.isRemote) {
            FenceOverrideClientState.clearAll();
        }
    }
}
