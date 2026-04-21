package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import zairus.worldexplorer.archery.items.SlingshotAmmoHelper;

public class RiftExplorerSlingshotAmmoTooltipHandler {
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (event == null || event.itemStack == null || event.toolTip == null || !ModConfig.enableRiftExplorerModule) {
            return;
        }
        SlingshotAmmoHelper.addSlingshotAmmoTooltip(event.itemStack, event.toolTip);
    }
}
