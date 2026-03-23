package com.voidsrift.riftflux.server;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.sound.PlayerHurtSoundResolver;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class PlayerHurtSoundEventHandler {
    private static final String TAG_LAST_HURT_SOUND_TICK = "RiftFluxLastHurtSoundTick";
    private static final String TAG_LAST_HURT_SOUND_SOURCE = "RiftFluxLastHurtSoundSource";
    private static final long DROWN_SOUND_DEDUPE_TICKS = 15L;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (!ModConfig.playerOnlyHurtSound || event == null || !(event.entityLiving instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (player.worldObj == null || player.worldObj.isRemote || event.ammount <= 0.0F) {
            return;
        }
        if (isDuplicateSoundEvent(player, event)) {
            return;
        }

        Entity attacker = event.source == null ? null : event.source.getEntity();
        boolean pvpHit = attacker instanceof EntityPlayer && attacker != player;
        String sound = PlayerHurtSoundResolver.resolvePlayerHurtSoundKey(!pvpHit || ModConfig.otherPlayersOOF);
        float pitch = (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2F + 1.0F;
        player.worldObj.playSoundAtEntity(player, sound, 1.0F, pitch);
    }

    private static boolean isDuplicateSoundEvent(EntityPlayer player, LivingHurtEvent event) {
        if (player == null || event == null || player.getEntityData() == null) {
            return false;
        }

        long worldTick = player.worldObj.getTotalWorldTime();
        String source = event.source == null ? "" : event.source.damageType;

        long lastTick = player.getEntityData().getLong(TAG_LAST_HURT_SOUND_TICK);
        String lastSource = player.getEntityData().getString(TAG_LAST_HURT_SOUND_SOURCE);

        player.getEntityData().setLong(TAG_LAST_HURT_SOUND_TICK, worldTick);
        player.getEntityData().setString(TAG_LAST_HURT_SOUND_SOURCE, source);

        if (source.equals(lastSource) && lastTick == worldTick) {
            return true;
        }

        return "drown".equals(source) && source.equals(lastSource) && worldTick - lastTick <= DROWN_SOUND_DEDUPE_TICKS;
    }
}
