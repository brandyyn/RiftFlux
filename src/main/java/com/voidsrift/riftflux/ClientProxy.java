package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.CommonProxy;

public class ClientProxy extends CommonProxy {
    @Override
    public void initClientFeatures() {
        // HUD (item pickup notifications)
        if (ModConfig.enablePickupNotifier) {
            PickupNotifierHud.bootstrap();
        }
        // Stars (tag new items so the GUI mixin can draw)
        if (ModConfig.enableItemPickupStar) {
            PickupStarClientTracker.bootstrap();
        }
    }
}
