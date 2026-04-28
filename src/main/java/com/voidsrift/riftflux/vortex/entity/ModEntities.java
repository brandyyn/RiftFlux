package com.voidsrift.riftflux.vortex.entity;

import com.voidsrift.riftflux.entity.RiftFluxEntityRegistry;
import com.voidsrift.riftflux.riftflux;

public class ModEntities {
   public static final void init() {
      RiftFluxEntityRegistry.registerModEntity(EntityDeathRune.class, "RuneThanatos", riftflux.instance, 160, Integer.MAX_VALUE, false);
      RiftFluxEntityRegistry.registerModEntity(EntityItemBackpack.class, "ItemBackpack", riftflux.instance, 64, 20, true);
   }
}
