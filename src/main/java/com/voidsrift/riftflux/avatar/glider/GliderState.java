package com.voidsrift.riftflux.avatar.glider;

import com.voidsrift.riftflux.net.RFNetwork;
import com.voidsrift.riftflux.net.MsgGliderState;
import com.voidsrift.riftflux.net.MsgGliderToggle;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class GliderState {
    private static final Set<String> GLIDING_PLAYERS =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private static final Set<String> HOVERING_PLAYERS =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private GliderState() {
    }

    public static boolean isPlayerGliding(String name) {
        return name != null && GLIDING_PLAYERS.contains(name);
    }

    public static void addGlidingPlayerName(String name) {
        addGlidingPlayerName(name, false);
    }

    public static void addGlidingPlayerName(String name, boolean skipSync) {
        if (name == null) {
            return;
        }
        GLIDING_PLAYERS.add(name);
        if (!skipSync) {
            syncGliding(true, name);
        }
    }

    public static void removeGlidingPlayerName(String name) {
        removeGlidingPlayerName(name, false);
    }

    public static void removeGlidingPlayerName(String name, boolean skipSync) {
        if (name == null) {
            return;
        }
        boolean changed = GLIDING_PLAYERS.remove(name);
        changed = HOVERING_PLAYERS.remove(name) || changed;
        if (!skipSync && changed) {
            syncGliding(false, name);
        }
    }

    public static boolean isPlayerHovering(String name) {
        return name != null && HOVERING_PLAYERS.contains(name);
    }

    public static void setPlayerHovering(String name, boolean hovering) {
        if (name == null) {
            return;
        }
        if (hovering) {
            HOVERING_PLAYERS.add(name);
        } else {
            HOVERING_PLAYERS.remove(name);
        }
    }

    private static void syncGliding(boolean gliding, String name) {
        if (RFNetwork.CH == null) {
            return;
        }
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == Side.CLIENT) {
            RFNetwork.CH.sendToServer(new MsgGliderToggle(gliding, name));
        } else {
            RFNetwork.CH.sendToAll(new MsgGliderState(gliding, name));
        }
    }
}
