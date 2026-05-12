package com.voidsrift.riftflux.mixin.early.tweakimo;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.dualhotbar.RenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiIngameForge.class, remap = false)
public abstract class MixinGuiIngameForge_HideMountPromptOverlay {
    @Inject(method = "renderRecordOverlay", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideTweakimoMountPromptOverlay(int width, int height, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (ModConfig.dualHotbarUseCustomMountOnboardPrompt
                && mc != null
                && mc.thePlayer != null
                && RenderHandler.isMountOnboardOverlayVisible(mc, mc.thePlayer)) {
            ci.cancel();
        }
    }
}
