package com.zyin.zyinhud;

import com.zyin.zyinhud.mods.ItemSelector;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * This class is in charge of rendering the remaining Zyin HUD overlay.
 */
public class ZyinHUDRenderer
{
    public static ZyinHUDRenderer instance = new ZyinHUDRenderer();

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event)
    {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT)
        {
            ItemSelector.RenderOntoHUD(event.partialTicks);
        }
    }
}
