package com.voidsrift.riftflux.avatar.appa;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class AppaSeatEvents {
    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (!ModConfig.appaAllowMobPassengers) {
            return;
        }
        if (event.entityPlayer == null || event.entityPlayer.worldObj == null || event.entityPlayer.worldObj.isRemote) {
            return;
        }
        Entity target = event.target;
        if (target == null) {
            return;
        }
        if (!event.entityPlayer.isSneaking()) {
            return;
        }
        if (target.ridingEntity instanceof EntityBisonSeat) {
            EntityBisonSeat seat = (EntityBisonSeat) target.ridingEntity;
            EntityBison parent = seat.getParent();
            if (parent != null) {
                parent.ejectSeatPassenger(target);
            } else {
                target.mountEntity(null);
            }
            event.setCanceled(true);
            return;
        }
        if (target instanceof EntityBison) {
            EntityBison appa = (EntityBison) target;
            if (appa.ejectClosestSeatPassenger(event.entityPlayer)) {
                event.setCanceled(true);
            }
        }
    }
}
