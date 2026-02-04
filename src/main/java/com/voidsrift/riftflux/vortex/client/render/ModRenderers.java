package com.voidsrift.riftflux.vortex.client.render;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import com.voidsrift.riftflux.vortex.client.render.entity.RenderDeathRune;
import com.voidsrift.riftflux.vortex.entity.EntityDeathRune;
import com.voidsrift.riftflux.vortex.item.ModItems;

public class ModRenderers {
   public static final void init() {
      RenderingRegistry.registerEntityRenderingHandler(EntityDeathRune.class, new RenderDeathRune());
   }
}
