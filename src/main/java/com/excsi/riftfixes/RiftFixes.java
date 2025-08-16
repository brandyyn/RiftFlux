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

        final com.excsi.riftfixes.server.PickupStarServerEvents serverEvents =
                new com.excsi.riftfixes.server.PickupStarServerEvents();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(serverEvents);
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(serverEvents);

        // Client-only bits via proxy (HUD + star tracker)
        proxy.initClientFeatures();
    }
}
