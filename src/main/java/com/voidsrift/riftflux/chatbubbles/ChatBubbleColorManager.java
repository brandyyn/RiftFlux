package com.voidsrift.riftflux.chatbubbles;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgSyncChatBubbleColor;
import com.voidsrift.riftflux.net.MsgSyncChatBubbleTextColor;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ChatBubbleColorManager {
    public static final int LOCAL_DEFAULT_COLOR = 0xFFD700;
    public static final int TEXT_DEFAULT_COLOR = 0xFFFFFF;

    private static final String NBT_HAS_COLOR = "RiftFluxChatBubbleColorSet";
    private static final String NBT_COLOR = "RiftFluxChatBubbleColor";
    private static final String NBT_HAS_TEXT_COLOR = "RiftFluxChatBubbleTextColorSet";
    private static final String NBT_TEXT_COLOR = "RiftFluxChatBubbleTextColor";
    private static final Map<UUID, Integer> CLIENT_COLORS = new HashMap<UUID, Integer>();
    private static final Map<UUID, Integer> CLIENT_TEXT_COLORS = new HashMap<UUID, Integer>();
    private static final Map<UUID, Integer> CLIENT_UUID_COLORS = new HashMap<UUID, Integer>();
    private static final ServerEvents SERVER_EVENTS = new ServerEvents();

    private static boolean serverEventsRegistered;

    private ChatBubbleColorManager() {
    }

    public static void bootstrapServer() {
        if (serverEventsRegistered) {
            return;
        }
        serverEventsRegistered = true;
        FMLCommonHandler.instance().bus().register(SERVER_EVENTS);
        MinecraftForge.EVENT_BUS.register(SERVER_EVENTS);
    }

    public static int resolveRenderColor(EntityPlayer player, boolean localPlayer) {
        if (player == null) {
            return LOCAL_DEFAULT_COLOR;
        }
        if (localPlayer && ModConfig.chatBubblesUseCustomOwnBubbleColor) {
            return ModConfig.chatBubblesOwnBubbleColor & 0xFFFFFF;
        }

        UUID uuid = player.getUniqueID();
        Integer synced = getClientColor(uuid);
        if (synced != null) {
            return synced.intValue();
        }

        if (localPlayer) {
            return LOCAL_DEFAULT_COLOR;
        }
        return cachedUuidColor(uuid, player.getCommandSenderName());
    }

    public static int resolveRenderTextColor(EntityPlayer player, boolean localPlayer) {
        if (player == null) {
            return TEXT_DEFAULT_COLOR;
        }
        if (localPlayer && ModConfig.chatBubblesUseCustomOwnTextColor) {
            return ModConfig.chatBubblesOwnTextColor & 0xFFFFFF;
        }

        UUID uuid = player.getUniqueID();
        Integer synced = getClientTextColor(uuid);
        if (synced != null) {
            return synced.intValue();
        }

        if (ModConfig.chatBubblesRandomizeTextColorByUuid) {
            return cachedUuidColor(uuid, player.getCommandSenderName());
        }
        return TEXT_DEFAULT_COLOR;
    }

    public static void clearClientColors() {
        CLIENT_COLORS.clear();
        CLIENT_TEXT_COLORS.clear();
        CLIENT_UUID_COLORS.clear();
    }

    public static void applyClientColor(String uuid, boolean hasColor, int color) {
        UUID parsedUuid = parseUuid(uuid);
        if (parsedUuid == null) {
            return;
        }
        if (hasColor) {
            CLIENT_COLORS.put(parsedUuid, color & 0xFFFFFF);
        } else {
            CLIENT_COLORS.remove(parsedUuid);
        }
    }

    public static Integer getClientColor(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return CLIENT_COLORS.get(uuid);
    }

    public static void applyClientTextColor(String uuid, boolean hasColor, int color) {
        UUID parsedUuid = parseUuid(uuid);
        if (parsedUuid == null) {
            return;
        }
        if (hasColor) {
            CLIENT_TEXT_COLORS.put(parsedUuid, color & 0xFFFFFF);
        } else {
            CLIENT_TEXT_COLORS.remove(parsedUuid);
        }
    }

    public static Integer getClientTextColor(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return CLIENT_TEXT_COLORS.get(uuid);
    }

    public static Integer getServerColor(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        NBTTagCompound data = player.getEntityData();
        if (data == null || !data.getBoolean(NBT_HAS_COLOR)) {
            return null;
        }
        return data.getInteger(NBT_COLOR) & 0xFFFFFF;
    }

    public static Integer getServerTextColor(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        NBTTagCompound data = player.getEntityData();
        if (data == null || !data.getBoolean(NBT_HAS_TEXT_COLOR)) {
            return null;
        }
        return data.getInteger(NBT_TEXT_COLOR) & 0xFFFFFF;
    }

    public static void setServerColor(EntityPlayer player, int color) {
        if (player == null) {
            return;
        }
        NBTTagCompound data = player.getEntityData();
        data.setBoolean(NBT_HAS_COLOR, true);
        data.setInteger(NBT_COLOR, color & 0xFFFFFF);
        syncPlayerColorToAll(player);
    }

    public static void setServerTextColor(EntityPlayer player, int color) {
        if (player == null) {
            return;
        }
        NBTTagCompound data = player.getEntityData();
        data.setBoolean(NBT_HAS_TEXT_COLOR, true);
        data.setInteger(NBT_TEXT_COLOR, color & 0xFFFFFF);
        syncPlayerTextColorToAll(player);
    }

    public static void clearServerColor(EntityPlayer player) {
        if (player == null) {
            return;
        }
        NBTTagCompound data = player.getEntityData();
        data.setBoolean(NBT_HAS_COLOR, false);
        data.removeTag(NBT_COLOR);
        syncPlayerColorToAll(player);
    }

    public static void clearServerTextColor(EntityPlayer player) {
        if (player == null) {
            return;
        }
        NBTTagCompound data = player.getEntityData();
        data.setBoolean(NBT_HAS_TEXT_COLOR, false);
        data.removeTag(NBT_TEXT_COLOR);
        syncPlayerTextColorToAll(player);
    }

    public static void syncPlayerColorToAll(EntityPlayer player) {
        if (player == null || RFNetwork.CH == null) {
            return;
        }
        UUID uuid = player.getUniqueID();
        if (uuid == null) {
            return;
        }
        Integer color = getServerColor(player);
        RFNetwork.CH.sendToAll(new MsgSyncChatBubbleColor(uuid.toString(), color != null, color == null ? 0 : color.intValue()));
    }

    public static void syncPlayerTextColorToAll(EntityPlayer player) {
        if (player == null || RFNetwork.CH == null) {
            return;
        }
        UUID uuid = player.getUniqueID();
        if (uuid == null) {
            return;
        }
        Integer color = getServerTextColor(player);
        RFNetwork.CH.sendToAll(new MsgSyncChatBubbleTextColor(uuid.toString(), color != null, color == null ? 0 : color.intValue()));
    }

    private static void syncKnownColorsTo(EntityPlayerMP target) {
        if (target == null || RFNetwork.CH == null) {
            return;
        }
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null || server.getConfigurationManager() == null) {
            return;
        }
        for (Object entry : server.getConfigurationManager().playerEntityList) {
            if (!(entry instanceof EntityPlayer)) {
                continue;
            }
            EntityPlayer player = (EntityPlayer) entry;
            UUID uuid = player.getUniqueID();
            if (uuid == null) {
                continue;
            }
            Integer color = getServerColor(player);
            if (color != null) {
                RFNetwork.CH.sendTo(new MsgSyncChatBubbleColor(uuid.toString(), true, color.intValue()), target);
            }
            Integer textColor = getServerTextColor(player);
            if (textColor != null) {
                RFNetwork.CH.sendTo(new MsgSyncChatBubbleTextColor(uuid.toString(), true, textColor.intValue()), target);
            }
        }
    }

    private static UUID parseUuid(String uuid) {
        if (uuid == null) {
            return null;
        }
        String value = uuid.trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static int cachedUuidColor(UUID uuid, String fallbackName) {
        if (uuid == null) {
            return uuidColor(null, fallbackName);
        }
        Integer cached = CLIENT_UUID_COLORS.get(uuid);
        if (cached == null) {
            cached = Integer.valueOf(uuidColor(uuid, fallbackName));
            CLIENT_UUID_COLORS.put(uuid, cached);
        }
        return cached.intValue();
    }

    private static int uuidColor(UUID uuid, String fallbackName) {
        int hash;
        if (uuid != null) {
            hash = uuid.hashCode();
        } else {
            hash = fallbackName == null ? 0 : fallbackName.hashCode();
        }

        float hue = ((hash & 0x7FFFFFFF) % 360) / 360.0F;
        return Color.HSBtoRGB(hue, 0.62F, 1.0F) & 0xFFFFFF;
    }

    public static final class ServerEvents {
        @SubscribeEvent
        public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (!(event.player instanceof EntityPlayerMP)) {
                return;
            }
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            syncKnownColorsTo(player);
            syncPlayerColorToAll(player);
            syncPlayerTextColorToAll(player);
        }

        @SubscribeEvent
        public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
            if (event == null || event.original == null || event.entityPlayer == null) {
                return;
            }
            Integer color = getServerColor(event.original);
            if (color != null) {
                setServerColor(event.entityPlayer, color.intValue());
            }
            Integer textColor = getServerTextColor(event.original);
            if (textColor != null) {
                setServerTextColor(event.entityPlayer, textColor.intValue());
            }
        }
    }
}
