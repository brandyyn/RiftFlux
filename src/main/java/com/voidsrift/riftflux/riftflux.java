package com.voidsrift.riftflux;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Constants.MODID, version = Constants.VERSION)
public class riftflux {

    @SidedProxy(clientSide = "com.voidsrift.riftflux.client.ClientProxy",
            serverSide = "com.voidsrift.riftflux.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        com.voidsrift.riftflux.net.RFNetwork.init();

        final com.voidsrift.riftflux.server.PickupStarServerEvents serverEvents =
                new com.voidsrift.riftflux.server.PickupStarServerEvents();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(serverEvents);
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(serverEvents);
        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.server.ChestLaunchEvents());


        // Client-only bits via proxy (HUD + star tracker)
        proxy.initClientFeatures();
    }
}
