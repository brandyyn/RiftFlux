package com.voidsrift.riftflux.riftexplorer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class RiftChestRandomMobStateTracker {
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event == null || event.world == null || event.world.isRemote || event.entity == null) {
            return;
        }
        RiftChestRandomMobStateHelper.ensureStoredState(event.entity);
    }
}
