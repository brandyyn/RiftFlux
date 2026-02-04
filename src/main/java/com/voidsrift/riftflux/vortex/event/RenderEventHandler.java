package com.voidsrift.riftflux.vortex.event;

import com.voidsrift.riftflux.vortex.lib.helper.REHToolbeltHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;

/**
 * Client-only render hooks for vortex content.
 * Currently used for the Toolbelt radial menu overlay.
 */
@SideOnly(Side.CLIENT)
public class RenderEventHandler {

    private final REHToolbeltHelper toolbeltHelper = new REHToolbeltHelper();
    Minecraft mc = Minecraft.getMinecraft();
    
@SubscribeEvent
public void onHudPre(RenderGameOverlayEvent.Pre event) {
    // Hide the crosshair while the Toolbelt radial menu is open
    if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && KeyEventHandler.TRMactive
            && mc.thePlayer != null && ItemHelper.hasBauble(mc.thePlayer, ModItems.toolbelt)) {
        event.setCanceled(true);
    }
}

@SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        // Only draw once per frame; ALL is the typical place for custom overlays.
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) {
            return;
        }
        toolbeltHelper.handleToolbeltRadialMenu(mc, event);
    }
}