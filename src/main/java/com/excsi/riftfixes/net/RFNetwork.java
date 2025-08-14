package com.excsi.riftfixes.net;

import com.excsi.riftfixes.Constants;
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
            Class<?> msgPickup = Class.forName("com.excsi.riftfixes.net.MsgPickup", false, cl);
            Class<?> handler = null;
            try { handler = Class.forName("com.excsi.riftfixes.net.MsgPickup$ClientHandler", false, cl); }
            catch (Throwable t) {
                try { handler = Class.forName("com.excsi.riftfixes.net.MsgPickup$Handler", false, cl); }
                catch (Throwable t2) { handler = null; }
            }
            if (handler != null) {
                Method reg = SimpleNetworkWrapper.class.getMethod("registerMessage", Class.class, Class.class, int.class, Side.class);
                reg.invoke(CH, handler, msgPickup, 0, Side.CLIENT);
            }
        } catch (Throwable ignored) { }

        // Register clear-tag packet (ID 1) on SERVER
        CH.registerMessage(MsgClearPickupTag.Handler.class, MsgClearPickupTag.class, 1, Side.SERVER);
    }
}
