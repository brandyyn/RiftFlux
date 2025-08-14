package com.excsi.riftfixes.client;

import com.excsi.riftfixes.ModConfig;
import com.excsi.riftfixes.CommonProxy;

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
