package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.CommonProxy;
import com.voidsrift.riftflux.compat.hats.HatsKeybinds;
import com.voidsrift.riftflux.dualhotbar.DualHotbarClient;
import com.voidsrift.riftflux.painting.GuiPaintingSelector;
import com.voidsrift.riftflux.blessings.BlessingContent;
import com.voidsrift.riftflux.tweaks.ladder.client.DoubleSidedLadderRenderer;
import com.voidsrift.riftflux.tweaks.ladder.client.RFRenderIds;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy {

    @Override
    public boolean isJumpKeyDown() {
        // Client-side: read the jump keybinding directly (no reflection).
        return Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed();
    }

    @Override
    public void openPaintingSelectorScreen() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) {
            return;
        }
        mc.displayGuiScreen(new GuiPaintingSelector());
    }

    @Override
    public void applyBlessingSync(String blessing, boolean hasSource, int x, int y, int z, int dim) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) {
            return;
        }
        if (blessing != null && !blessing.isEmpty()) {
            com.voidsrift.riftflux.blessings.BlessingHelper.setBlessing(mc.thePlayer, blessing);
        } else {
            com.voidsrift.riftflux.blessings.BlessingHelper.clearBlessing(mc.thePlayer);
        }
        if (hasSource) {
            mc.thePlayer.getEntityData().setInteger(com.voidsrift.riftflux.blessings.BlessingHelper.NBT_BLESSING_PILLAR_X, x);
            mc.thePlayer.getEntityData().setInteger(com.voidsrift.riftflux.blessings.BlessingHelper.NBT_BLESSING_PILLAR_Y, y);
            mc.thePlayer.getEntityData().setInteger(com.voidsrift.riftflux.blessings.BlessingHelper.NBT_BLESSING_PILLAR_Z, z);
            mc.thePlayer.getEntityData().setInteger(com.voidsrift.riftflux.blessings.BlessingHelper.NBT_BLESSING_PILLAR_DIM, dim);
        } else {
            com.voidsrift.riftflux.blessings.BlessingHelper.clearBlessingSource(mc.thePlayer);
        }
    }

    @Override
    public void initClientFeatures() {
        // Register NEI handler tab icon
        com.voidsrift.riftflux.nei.GTNHNeiHandlerInfo.register();

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
            if (ModConfig.itemPickupStarShowHotbarHud) {
                PickupStarHotbarHud.bootstrap();
            }
        }
        com.voidsrift.riftflux.placeditem.PlacedItemContent.initClient();
        DualHotbarClient.init();
        com.voidsrift.riftflux.vortex.vortexContent.initClient();
        com.voidsrift.riftflux.avatar.AvatarTLBContent.initClient();
        BlessingContent.initClient();
        if (Loader.isModLoaded("Hats")) {
            HatsKeybinds.ensureRegistered();
        }

    }
}
