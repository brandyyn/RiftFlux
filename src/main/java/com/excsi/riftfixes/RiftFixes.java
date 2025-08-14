package com.excsi.riftfixes;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = Constants.MODID, version = Constants.VERSION)
public class RiftFixes {

    @SidedProxy(clientSide = "com.excsi.riftfixes.client.ClientProxy",
            serverSide = "com.excsi.riftfixes.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        com.excsi.riftfixes.net.RFNetwork.init();

        // IMPORTANT: register ONLY ONCE, and only on the Forge event bus
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
                new com.excsi.riftfixes.server.PickupStarServerEvents()
        );

        // Client-only bits via proxy (HUD + star tracker)
        proxy.initClientFeatures();
    }
}
