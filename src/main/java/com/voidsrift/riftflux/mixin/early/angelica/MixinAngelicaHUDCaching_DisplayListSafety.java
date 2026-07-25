package com.voidsrift.riftflux.mixin.early.angelica;

import com.gtnewhorizons.angelica.glsm.DisplayListManager;
import com.gtnewhorizons.angelica.glsm.recording.CommandRecorder;
import com.voidsrift.riftflux.mixin.accessor.angelica.DisplayListManagerInvoker;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.gtnewhorizons.angelica.hudcaching.HUDCaching", remap = false)
public abstract class MixinAngelicaHUDCaching_DisplayListSafety {

    @Redirect(
            method = "renderCachedHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(FZII)V",
                    remap = true
            ),
            require = 2
    )
    private static void riftflux$renderHudOutsideDisplayList(
            GuiIngame gui,
            float partialTicks,
            boolean hasScreen,
            int mouseX,
            int mouseY
    ) {
        if (!DisplayListManager.isRecording()) {
            gui.renderGameOverlay(partialTicks, hasScreen, mouseX, mouseY);
            return;
        }

        DisplayListManagerInvoker.riftflux$flushAll();
        CommandRecorder recorder = DisplayListManager.pauseRecording();
        try {
            gui.renderGameOverlay(partialTicks, hasScreen, mouseX, mouseY);
        } finally {
            DisplayListManager.resumeRecording(recorder);
        }
    }
}
