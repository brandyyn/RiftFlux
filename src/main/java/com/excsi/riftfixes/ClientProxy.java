package com.excsi.riftfixes;

import com.excsi.riftfixes.client.PickupStarClientTracker;

public class ClientProxy extends CommonProxy {
    @Override public void initClientFeatures() {
        com.excsi.riftfixes.client.PickupStarClientTracker.bootstrap();
        com.excsi.riftfixes.client.PickupNotifierHud.bootstrap();
    }
}