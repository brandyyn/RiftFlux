package com.voidsrift.riftflux.vortex.event;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

public class ModEvents {
   public static final void init() {
      EntityEventHandler eventHandlerEntity = new EntityEventHandler();
      MinecraftForge.EVENT_BUS.register(eventHandlerEntity);
      FMLCommonHandler.instance().bus().register(eventHandlerEntity);
      if (ModConfig.enableUnloader) {
         FMLCommonHandler.instance().bus().register(new WorldEventHandler());
      }
      MinecraftForge.EVENT_BUS.register(new CraftingEventHandler());
   }
}
