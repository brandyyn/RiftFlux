package com.voidsrift.riftflux.palaria;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.palaria.entity.EntityNimatin;
import com.voidsrift.riftflux.palaria.entity.EntityNimatinSeat;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class NimatinSeatEvents {
    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (!ModConfig.enablePalariaModule || !ModConfig.enablePalariaNimatin) {
            return;
        }
        if (event.entityPlayer == null || event.entityPlayer.worldObj == null || event.entityPlayer.worldObj.isRemote) {
            return;
        }
        if (!event.entityPlayer.isSneaking()) {
            return;
        }
        Entity target = event.target;
        if (target == null) {
            return;
        }
        if (target.ridingEntity instanceof EntityNimatinSeat) {
            EntityNimatinSeat seat = (EntityNimatinSeat) target.ridingEntity;
            EntityNimatin parent = seat.getParent();
            if (parent != null) {
                parent.ejectSeatPassenger(target);
            } else {
                target.mountEntity(null);
            }
            event.setCanceled(true);
            return;
        }
        if (target instanceof EntityNimatin) {
            EntityNimatin nimatin = (EntityNimatin) target;
            if (nimatin.ejectClosestSeatPassenger(event.entityPlayer)) {
                event.setCanceled(true);
            }
        }
    }
}
