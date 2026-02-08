package com.voidsrift.riftflux;

import com.voidsrift.riftflux.combat.StickTooltipHandler;
import com.voidsrift.riftflux.dualhotbar.DualHotbarState;
import com.voidsrift.riftflux.vortex.vortexContent;
import zelda.Core;
import com.zyin.zyinhud.ZyinHUD;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import java.util.Map;

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
        com.voidsrift.riftflux.placeditem.PlacedItemContent.init();
        makamys.satchels.Satchels.preInit(event);
        Core.preInit(event);
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ZyinHUD.preInit(event);
        }
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
        makamys.satchels.Satchels.init(event);
        Core.init(event);
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ZyinHUD.init(event);
        }

        proxy.initClientFeatures();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        vortexContent.postInit(event);
        makamys.satchels.Satchels.postInit(event);
        Core.postInit(event);
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ZyinHUD.postInit(event);
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ZyinHUD.serverStarting(event);
        }
    }

    @NetworkCheckHandler
    public boolean checkRemote(Map<String, String> modList, Side side) {
        if (side == Side.CLIENT) {
            DualHotbarState.installedOnServer = modList != null && modList.containsKey(Constants.MODID);
        }
        return true;
    }
    
}
