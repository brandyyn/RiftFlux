package com.voidsrift.riftflux;

import com.voidsrift.riftflux.combat.StickTooltipHandler;
import com.voidsrift.riftflux.vortex.vortexContent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Constants.MODID, version = Constants.VERSION)
public class riftflux {

    @Instance(Constants.MODID)
    public static riftflux instance;

    @SidedProxy(clientSide = "com.voidsrift.riftflux.client.ClientProxy",
            serverSide = "com.voidsrift.riftflux.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        vortexContent.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        com.voidsrift.riftflux.net.RFNetwork.init();

        com.voidsrift.riftflux.tweaks.ladder.RiftFluxLadderContent.init();

        final com.voidsrift.riftflux.server.PickupStarServerEvents serverEvents =
                new com.voidsrift.riftflux.server.PickupStarServerEvents();
        MinecraftForge.EVENT_BUS.register(serverEvents);
        FMLCommonHandler.instance().bus().register(serverEvents);
        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.server.ChestLaunchEvents());

        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.tweaks.ladder.FloatingLadderEvents());

        MinecraftForge.EVENT_BUS.register(new StickTooltipHandler());

        vortexContent.init(event);

        proxy.initClientFeatures();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        vortexContent.postInit(event);
    }
    
}
