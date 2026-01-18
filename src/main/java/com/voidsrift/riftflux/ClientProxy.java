package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.CommonProxy;
import com.voidsrift.riftflux.tweaks.ladder.client.DoubleSidedLadderRenderer;
import com.voidsrift.riftflux.tweaks.ladder.client.RFRenderIds;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy {

    @Override
    public boolean isJumpKeyDown() {
        // Client-side: read the jump keybinding directly (no reflection).
        return Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed();
    }
    @Override
    public void initClientFeatures() {
        // Hanging ladders: double-sided renderer (used by Hanging Ladder block)
        if (ModConfig.enableHangingLadders && RFRenderIds.doubleSidedLadderRenderId < 0) {
            RFRenderIds.doubleSidedLadderRenderId = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new DoubleSidedLadderRenderer());
        }

        // HUD (item pickup notifications)
        if (ModConfig.enablePickupNotifier) {
            PickupNotifierHud.bootstrap();
        }
        // Stars (tag new items so the GUI mixin can draw)
        if (ModConfig.enableItemPickupStar) {
            PickupStarClientTracker.bootstrap();
        }
    }
}
