package com.voidsrift.riftflux.vortex.event;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

public class ModClientEvents {
    public static void init() {
        // Tick bus (KeyEventHandler uses ClientTickEvent)
        FMLCommonHandler.instance().bus().register(new KeyEventHandler());

        // Forge bus
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
    }
}
