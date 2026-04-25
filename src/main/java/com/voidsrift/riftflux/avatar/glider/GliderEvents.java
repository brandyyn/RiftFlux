package com.voidsrift.riftflux.avatar.glider;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import de.rinonline.korinrpg.Helper.NBT.RINPlayer2;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.compat.EtFuturumElytraCompat;
import com.voidsrift.riftflux.net.MsgGliderState;
import com.voidsrift.riftflux.net.RFNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GliderEvents {
    private static final String PERSISTED_GLIDER_TAG = "RiftFluxGliderActiveOnLogout";
    private static final Set<String> PENDING_GLIDE_RESTORE =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {
        if (!(evt.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) evt.entityLiving;
        ItemStack gliderStack = GliderItemHelper.getGliderStack(player);
        boolean holdingGlider = gliderStack != null;
        String playerName = player.getDisplayName();
        if (playerName == null) {
            return;
        }

        if (!holdingGlider) {
            if (GliderState.isPlayerGliding(playerName)) {
                GliderState.removeGlidingPlayerName(playerName);
            }
            return;
        }

        if (!GliderState.isPlayerGliding(playerName)) {
            return;
        }

        if (ModConfig.blockEtFuturumElytraWhileAvatarGliding && EtFuturumElytraCompat.isElytraFlying(player)) {
            EtFuturumElytraCompat.clearElytraFlight(player);
        }

        if (player.isInWater()) {
            GliderState.removeGlidingPlayerName(playerName);
            return;
        }
        if (player.onGround) {
            return;
        }

        boolean hovering = ModConfig.enableGliderHoldAltitude && GliderState.isPlayerHovering(playerName);
        boolean canHover = hovering;
        if (hovering && ModConfig.dssEnabled && !player.capabilities.isCreativeMode) {
            RINPlayer2 props = RINPlayer2.get(player);
            if (props != null && props.isOvercharged()) {
                canHover = false;
            }
        }

        if (canHover) {
            if (player.motionY < 0.0) {
                player.motionY = 0.0;
            }
        } else if (player.isSneaking()) {
            player.motionY = -0.2;
            player.motionX *= 1.05;
            player.motionZ *= 1.05;
        } else if (player.motionY < -0.07) {
            player.motionY = -0.07;
        }

        if (Math.abs(player.motionX) + Math.abs(player.motionZ) < 2.0) {
            player.motionX *= 1.05;
            player.motionZ *= 1.05;
        }

        player.fallDistance = 0.0f;
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent evt) {
        if (evt.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) evt.entityLiving;
            if (GliderItemHelper.isGliderEnabled(player)) {
                evt.distance = 1.1f;
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        if (event == null || event.player == null) {
            return;
        }
        EntityPlayer player = event.player;
        String playerName = player.getDisplayName();
        if (playerName == null) {
            return;
        }
        boolean serverSide = player.worldObj != null && !player.worldObj.isRemote;
        if (serverSide) {
            boolean restoreGliding = GliderState.isPlayerGliding(playerName) && GliderItemHelper.isHoldingGlider(player);
            this.setPersistedGliding(player, restoreGliding);
            if (restoreGliding) {
                PENDING_GLIDE_RESTORE.add(playerName);
            } else {
                PENDING_GLIDE_RESTORE.remove(playerName);
            }
        }
        GliderState.removeGlidingPlayerName(playerName, !serverSide);
        ItemGlider.clearLastToggle(playerName);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        String playerName = player.getDisplayName();
        if (playerName == null) {
            return;
        }

        boolean restoreGliding = PENDING_GLIDE_RESTORE.remove(playerName) || this.consumePersistedGliding(player);
        if (restoreGliding && GliderItemHelper.isHoldingGlider(player) && !player.isInWater()) {
            GliderState.addGlidingPlayerName(playerName);
        }

        if (RFNetwork.CH == null) {
            return;
        }
        for (String glidingName : GliderState.getGlidingPlayerNamesSnapshot()) {
            if (glidingName == null || glidingName.isEmpty()) {
                continue;
            }
            RFNetwork.CH.sendTo(new MsgGliderState(true, glidingName), player);
        }
    }

    private void setPersistedGliding(EntityPlayer player, boolean gliding) {
        if (player == null) {
            return;
        }
        NBTTagCompound entityData = player.getEntityData();
        NBTTagCompound persisted = entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)
                ? entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
                : new NBTTagCompound();
        if (gliding) {
            persisted.setBoolean(PERSISTED_GLIDER_TAG, true);
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
            return;
        }
        persisted.removeTag(PERSISTED_GLIDER_TAG);
        if (persisted.hasNoTags()) {
            entityData.removeTag(EntityPlayer.PERSISTED_NBT_TAG);
        } else {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
        }
    }

    private boolean consumePersistedGliding(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)) {
            return false;
        }
        NBTTagCompound persisted = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        boolean restore = persisted.getBoolean(PERSISTED_GLIDER_TAG);
        persisted.removeTag(PERSISTED_GLIDER_TAG);
        if (persisted.hasNoTags()) {
            entityData.removeTag(EntityPlayer.PERSISTED_NBT_TAG);
        } else {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
        }
        return restore;
    }
}
