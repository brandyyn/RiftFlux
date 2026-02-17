package com.voidsrift.riftflux.mixin.late.battlegear;

import com.voidsrift.riftflux.dualhotbar.IBattlegearClientEventsBridge;
import mods.battlegear2.client.BattlegearClientEvents;
import mods.battlegear2.client.gui.BattlegearInGameGUI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(value = BattlegearClientEvents.class, remap = false)
public abstract class MixinBattlegearClientEventsBridge implements IBattlegearClientEventsBridge {

    @Shadow
    @Final
    private BattlegearInGameGUI inGameGUI;

    @Override
    public void riftflux$renderGameOverlay(float partialTicks, int mouseX, int mouseY) {
        this.inGameGUI.renderGameOverlay(partialTicks, mouseX, mouseY);
    }
}
