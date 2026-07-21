package com.voidsrift.riftflux.mixin.late.xaero.minimap;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "xaero.common.minimap.waypoints.WaypointsManager", remap = false)
public interface XaeroWaypointsManagerInvoker {
    @Invoker("createDeathpoint")
    void riftflux$createDeathpoint(EntityPlayer player);
}
