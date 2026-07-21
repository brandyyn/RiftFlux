package com.voidsrift.riftflux.mixin.late.xaero.minimap;

import com.voidsrift.riftflux.gravestone.GravestoneWaypointTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "xaero.common.minimap.waypoints.WaypointsManager", remap = false)
public abstract class MixinWaypointsManager_GravestoneDeathpoint {
    @Inject(
            method = "createDeathpoint(Lnet/minecraft/entity/player/EntityPlayer;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void riftflux$rememberXaeroManager(EntityPlayer player, CallbackInfo ci) {
        GravestoneWaypointTracker.registerXaeroManager(this);
        if (GravestoneWaypointTracker.shouldCancelXaeroDeathpoint()) {
            ci.cancel();
        }
    }

    @Redirect(
            method = "createDeathpoint(Lnet/minecraft/entity/player/EntityPlayer;Lxaero/common/minimap/waypoints/WaypointWorld;Z)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;field_70165_t:D"),
            remap = false
    )
    private double riftflux$useGraveX(EntityPlayer player) {
        if (GravestoneWaypointTracker.hasLocation(player)) {
            GravestoneWaypointTracker.markXaeroDeathpointFinalized();
            return GravestoneWaypointTracker.getX();
        }
        return player.posX;
    }

    @Redirect(
            method = "createDeathpoint(Lnet/minecraft/entity/player/EntityPlayer;Lxaero/common/minimap/waypoints/WaypointWorld;Z)V",
            at = @At(value = "INVOKE", target = "Lxaero/common/misc/Misc;getEntityY(Lnet/minecraft/entity/Entity;)D"),
            remap = false
    )
    private double riftflux$useGraveY(Entity entity) {
        return GravestoneWaypointTracker.hasLocation(entity) ? GravestoneWaypointTracker.getY() : entity.posY;
    }

    @Redirect(
            method = "createDeathpoint(Lnet/minecraft/entity/player/EntityPlayer;Lxaero/common/minimap/waypoints/WaypointWorld;Z)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;field_70161_v:D"),
            remap = false
    )
    private double riftflux$useGraveZ(EntityPlayer player) {
        return GravestoneWaypointTracker.hasLocation(player) ? GravestoneWaypointTracker.getZ() : player.posZ;
    }

}
