package com.voidsrift.riftflux.vortex.event;

import com.voidsrift.riftflux.vortex.respawn.RespawnDelayHelper;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class RespawnDelayEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeath(LivingDeathEvent event) {
        if (event == null || !(event.entityLiving instanceof EntityPlayer) || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) {
            return;
        }
        RespawnDelayHelper.startDeathTimer((EntityPlayer) event.entityLiving);
        RespawnDelayHelper.sync((EntityPlayer) event.entityLiving);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event == null || event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote) {
            return;
        }
        RespawnDelayHelper.sync(event.player);
        RespawnDelayHelper.markPendingSync(event.player, 10);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event == null || event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote) {
            return;
        }
        RespawnDelayHelper.clearDeathTimer(event.player);
        RespawnDelayHelper.sync(event.player);
        RespawnDelayHelper.markPendingSync(event.player, 10);
        RespawnDelayHelper.markPendingRespawnStabilize(event.player, 10);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        RespawnDelayHelper.tickPendingSync(event);
        RespawnDelayHelper.tickPendingRespawnStabilize(event);
    }
}
