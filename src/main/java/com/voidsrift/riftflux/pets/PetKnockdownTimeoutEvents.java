package com.voidsrift.riftflux.pets;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PetKnockdownTimeoutEvents {

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event == null
                || !(event.entityPlayer instanceof EntityPlayerMP)
                || !(event.target instanceof EntityLivingBase)) {
            return;
        }
        PetKnockdownTimeout.syncTo(
                (EntityLivingBase) event.target,
                (EntityPlayerMP) event.entityPlayer
        );
    }
}
