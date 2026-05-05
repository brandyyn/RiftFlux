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
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GliderEvents {
    private static final String PERSISTED_GLIDER_TAG = "RiftFluxGliderActiveOnLogout";
    private static final String GLIDER_SAVE_SUFFIX = "riftflux-glider";
    private static final Set<String> PENDING_GLIDE_RESTORE =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private static final Map<String, File> GLIDER_SAVE_FILES =
            new ConcurrentHashMap<String, File>();

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
        boolean serverSide = player.worldObj != null && !player.worldObj.isRemote;
        boolean currentlyGliding = GliderState.isPlayerGliding(playerName);
        if (serverSide) {
            if (currentlyGliding) {
                this.setPersistedGliding(player, true);
                this.writeGliderSave(player, true);
            } else if (!this.hasPendingRestore(player)) {
                this.setPersistedGliding(player, false);
                this.writeGliderSave(player, false);
            }
        }
        if (this.tryRestorePendingGlide(player, playerName, holdingGlider)) {
            holdingGlider = true;
            currentlyGliding = true;
        }

        if (!holdingGlider) {
            if (currentlyGliding) {
                GliderState.removeGlidingPlayerName(playerName);
            }
            return;
        }

        if (!currentlyGliding) {
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
            boolean restoreGliding = GliderState.isPlayerGliding(playerName)
                    || this.hasPendingRestore(player)
                    || this.peekPersistedGliding(player);
            this.setPersistedGliding(player, restoreGliding);
            if (restoreGliding) {
                this.markPendingRestore(player);
            } else {
                this.clearPendingRestore(player);
            }
            this.writeGliderSave(player, restoreGliding);
        }
        GliderState.removeGlidingPlayerName(playerName, !serverSide);
        ItemGlider.clearLastToggle(playerName);
    }

    @SubscribeEvent
    public void onPlayerSaveToFile(SaveToFile event) {
        if (event == null || event.entityPlayer == null) {
            return;
        }
        this.rememberGliderSaveFile(event.playerUUID, event.getPlayerFile(GLIDER_SAVE_SUFFIX));
        boolean restoreGliding = this.shouldRestoreGliding(event.entityPlayer);
        this.writeGliderSave(event.getPlayerFile(GLIDER_SAVE_SUFFIX), restoreGliding);
    }

    @SubscribeEvent
    public void onPlayerLoadFromFile(LoadFromFile event) {
        if (event == null || event.entityPlayer == null) {
            return;
        }
        this.rememberGliderSaveFile(event.playerUUID, event.getPlayerFile(GLIDER_SAVE_SUFFIX));
        boolean restoreGliding = this.readGliderSave(event.getPlayerFile(GLIDER_SAVE_SUFFIX));
        this.setPersistedGliding(event.entityPlayer, restoreGliding);
        if (restoreGliding) {
            this.markPendingRestore(event.playerUUID);
        } else {
            this.clearPendingRestore(event.playerUUID);
        }
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

        if (this.peekPersistedGliding(player)) {
            this.markPendingRestore(player);
        }
        this.tryRestorePendingGlide(player, playerName, GliderItemHelper.isHoldingGlider(player));

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

    private void markPendingRestore(EntityPlayer player) {
        this.markPendingRestore(this.playerId(player));
    }

    private void markPendingRestore(String playerId) {
        String normalizedId = this.normalizePlayerId(playerId);
        if (normalizedId != null) {
            PENDING_GLIDE_RESTORE.add(normalizedId);
        }
    }

    private boolean hasPendingRestore(EntityPlayer player) {
        return this.hasPendingRestore(this.playerId(player));
    }

    private boolean hasPendingRestore(String playerId) {
        String normalizedId = this.normalizePlayerId(playerId);
        return normalizedId != null && PENDING_GLIDE_RESTORE.contains(normalizedId);
    }

    private void clearPendingRestore(EntityPlayer player) {
        this.clearPendingRestore(this.playerId(player));
    }

    private void clearPendingRestore(String playerId) {
        String normalizedId = this.normalizePlayerId(playerId);
        if (normalizedId != null) {
            PENDING_GLIDE_RESTORE.remove(normalizedId);
        }
    }

    private String playerId(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        return player.getUniqueID() == null ? null : player.getUniqueID().toString();
    }

    private boolean tryRestorePendingGlide(EntityPlayer player, String playerName, boolean holdingGlider) {
        if (player == null || playerName == null || !this.hasPendingRestore(player)) {
            return false;
        }
        if (!holdingGlider || player.isInWater()) {
            return false;
        }
        if (!GliderState.isPlayerGliding(playerName)) {
            GliderState.addGlidingPlayerName(playerName);
        }
        this.clearPendingRestore(player);
        this.setPersistedGliding(player, false);
        return true;
    }

    private boolean shouldRestoreGliding(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        String playerName = player.getDisplayName();
        if (playerName != null && GliderState.isPlayerGliding(playerName)) {
            return true;
        }
        if (this.hasPendingRestore(player)) {
            return true;
        }
        return this.peekPersistedGliding(player);
    }

    private void rememberGliderSaveFile(String playerId, File file) {
        String normalizedId = this.normalizePlayerId(playerId);
        if (normalizedId != null && file != null) {
            GLIDER_SAVE_FILES.put(normalizedId, file);
        }
    }

    private void writeGliderSave(EntityPlayer player, boolean gliding) {
        String playerId = this.normalizePlayerId(this.playerId(player));
        if (playerId == null) {
            return;
        }
        File file = GLIDER_SAVE_FILES.get(playerId);
        if (file != null) {
            this.writeGliderSave(file, gliding);
        }
    }

    private String normalizePlayerId(String playerId) {
        return playerId == null ? null : playerId.toLowerCase();
    }

    private void writeGliderSave(File file, boolean gliding) {
        if (file == null) {
            return;
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(PERSISTED_GLIDER_TAG, gliding);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            CompressedStreamTools.writeCompressed(tag, output);
        } catch (IOException ignored) {
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private boolean readGliderSave(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            NBTTagCompound tag = CompressedStreamTools.readCompressed(input);
            return tag != null && tag.getBoolean(PERSISTED_GLIDER_TAG);
        } catch (IOException ignored) {
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                }
            }
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

    private boolean peekPersistedGliding(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)) {
            return false;
        }
        return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean(PERSISTED_GLIDER_TAG);
    }
}
