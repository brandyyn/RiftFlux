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

        // Fence override sync (ID 11)
        CH.registerMessage(MsgSyncFenceOverrides.Handler.class, MsgSyncFenceOverrides.class, 11, Side.CLIENT);

        // Chat bubble color sync (IDs 12-13)
        CH.registerMessage(MsgSetChatBubbleColor.Handler.class, MsgSetChatBubbleColor.class, 12, Side.SERVER);
        CH.registerMessage(MsgSyncChatBubbleColor.Handler.class, MsgSyncChatBubbleColor.class, 13, Side.CLIENT);

        // Chat bubble size sync (ID 14)
        CH.registerMessage(MsgSetChatBubbleSize.Handler.class, MsgSetChatBubbleSize.class, 14, Side.CLIENT);

        // Chat bubble client config sync (ID 15)
        CH.registerMessage(MsgSetChatBubblesConfig.Handler.class, MsgSetChatBubblesConfig.class, 15, Side.CLIENT);

        // Chat bubble text color sync (IDs 16-17)
        CH.registerMessage(MsgSetChatBubbleTextColor.Handler.class, MsgSetChatBubbleTextColor.class, 16, Side.SERVER);
        CH.registerMessage(MsgSyncChatBubbleTextColor.Handler.class, MsgSyncChatBubbleTextColor.class, 17, Side.CLIENT);

        // Nimatin jump charge (ID 18)
        CH.registerMessage(MsgNimatinJump.Handler.class, MsgNimatinJump.class, 18, Side.SERVER);

        // Crossed-plant facing sync (ID 19)
        CH.registerMessage(MsgSyncCrossedPlantFacing.Handler.class, MsgSyncCrossedPlantFacing.class, 19, Side.CLIENT);

        // Server-authoritative celestial fog event sync (ID 20)
        CH.registerMessage(MsgSyncCelestialFogEvents.Handler.class, MsgSyncCelestialFogEvents.class, 20, Side.CLIENT);
    }
}
