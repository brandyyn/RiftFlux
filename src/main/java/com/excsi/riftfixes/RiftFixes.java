package com.excsi.riftfixes;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Constants.MODID, version = Constants.VERSION)
public class RiftFixes {

    @SidedProxy(
            clientSide = "com.excsi.riftfixes.ClientProxy",
            serverSide = "com.excsi.riftfixes.CommonProxy"
    )
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLInitializationEvent e) {
        com.excsi.riftfixes.net.RFNetwork.init();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new com.excsi.riftfixes.server.PickupStarServerEvents());

        if (cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            com.excsi.riftfixes.client.PickupStarClientTracker.bootstrap();
            com.excsi.riftfixes.client.PickupNotifierHud.bootstrap();
        }
    }
}
