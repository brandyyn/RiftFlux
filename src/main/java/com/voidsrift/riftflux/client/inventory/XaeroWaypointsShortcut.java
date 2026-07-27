package com.voidsrift.riftflux.client.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import xaero.common.XaeroMinimapSession;
import xaero.common.gui.GuiWaypoints;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.minimap.XaeroMinimap;

final class XaeroWaypointsShortcut {
    private XaeroWaypointsShortcut() {
    }

    static void open(GuiScreen parent) {
        XaeroMinimap mod = XaeroMinimap.instance;
        XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
        if (mod == null || session == null || mod.getSettings() == null) {
            return;
        }

        WaypointsManager waypointsManager = session.getWaypointsManager();
        if (waypointsManager == null || !mod.getSettings().waypointsGUI(waypointsManager)) {
            return;
        }

        Minecraft.getMinecraft().displayGuiScreen(new GuiWaypoints(mod, session, parent, null));
    }
}
