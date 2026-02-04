package com.voidsrift.riftflux.vortex.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import com.voidsrift.riftflux.riftflux;

public class ModEntities {
   public static final void init() {
      int id = -1;
      int var1 = id + 1;
      EntityRegistry.registerModEntity(EntityDeathRune.class, "RuneThanatos", id, riftflux.instance, 160, Integer.MAX_VALUE, false);
      EntityRegistry.registerModEntity(EntityItemBackpack.class, "ItemBackpack", var1++, riftflux.instance, 64, 20, true);
   }
}
