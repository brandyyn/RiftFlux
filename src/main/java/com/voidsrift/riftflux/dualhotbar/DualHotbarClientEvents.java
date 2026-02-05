package com.voidsrift.riftflux.dualhotbar;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class DualHotbarClientEvents {
    @SubscribeEvent
    public void onConnectedToServerEvent(ClientConnectedToServerEvent event) {
        DualHotbarState.applyServerConfig();
    }

    @SubscribeEvent
    public void onClientDisconnectionFromServerEvent(ClientDisconnectionFromServerEvent event) {
        DualHotbarState.installedOnServer = false;
    }
}
