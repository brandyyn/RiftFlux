package com.voidsrift.riftflux.vortex.event;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

public class ModClientEvents {
    public static void init() {
        // Tick bus (KeyEventHandler uses ClientTickEvent)
        FMLCommonHandler.instance().bus().register(new KeyEventHandler());

        // Forge bus
        GuiEventHandler guiEventHandler = new GuiEventHandler();
        MinecraftForge.EVENT_BUS.register(guiEventHandler);
        FMLCommonHandler.instance().bus().register(guiEventHandler);
    }
}
