package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.combat.torohealth.net.MsgToroHealthDamage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

import java.lang.reflect.Method;

/**
 * Network bootstrap.
 * Keeps existing MsgPickup (via reflection) and registers MsgClearPickupTag.
 */
public final class RFNetwork {
    public static SimpleNetworkWrapper CH;

    public static void init() {
        if (CH != null) return;
        CH = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID);

        // Try to register existing MsgPickup (ID 0) on CLIENT
        try {
            ClassLoader cl = RFNetwork.class.getClassLoader();
            Class<?> msgPickup = Class.forName("com.voidsrift.riftflux.net.MsgPickup", false, cl);
            Class<?> handler = null;
            try { handler = Class.forName("com.voidsrift.riftflux.net.MsgPickup$ClientHandler", false, cl); }
            catch (Throwable t) {
                try { handler = Class.forName("com.voidsrift.riftflux.net.MsgPickup$Handler", false, cl); }
                catch (Throwable t2) { handler = null; }
            }
            if (handler != null) {
                Method reg = SimpleNetworkWrapper.class.getMethod("registerMessage", Class.class, Class.class, int.class, Side.class);
                reg.invoke(CH, handler, msgPickup, 0, Side.CLIENT);
            }
        } catch (Throwable ignored) { }

        // Register clear-tag packet (ID 1) on SERVER
        CH.registerMessage(MsgClearPickupTag.Handler.class, MsgClearPickupTag.class, 1, Side.SERVER);

        // Glider sync (IDs 2-3)
        CH.registerMessage(MsgGliderToggle.Handler.class, MsgGliderToggle.class, 2, Side.SERVER);
        CH.registerMessage(MsgGliderState.Handler.class, MsgGliderState.class, 3, Side.CLIENT);

        // Appa control (ID 4)
        CH.registerMessage(MsgAppaControl.Handler.class, MsgAppaControl.class, 4, Side.SERVER);

        // Painting selection (ID 5)
        CH.registerMessage(MsgSetPaintingSelection.Handler.class, MsgSetPaintingSelection.class, 5, Side.SERVER);

        // Glider hover (ID 6)
        CH.registerMessage(MsgGliderHover.Handler.class, MsgGliderHover.class, 6, Side.SERVER);

        // Blessing activate (ID 7)
        CH.registerMessage(MsgActivateBlessing.Handler.class, MsgActivateBlessing.class, 7, Side.SERVER);

        // Blessing sync (ID 8)
        CH.registerMessage(MsgSyncBlessing.Handler.class, MsgSyncBlessing.class, 8, Side.CLIENT);

        // ToroHealth damage sync (ID 9)
        CH.registerMessage(MsgToroHealthDamage.Handler.class, MsgToroHealthDamage.class, 9, Side.CLIENT);

        // Respawn delay sync (ID 10)
        CH.registerMessage(MsgSyncRespawnDelay.Handler.class, MsgSyncRespawnDelay.class, 10, Side.CLIENT);
    }
}
