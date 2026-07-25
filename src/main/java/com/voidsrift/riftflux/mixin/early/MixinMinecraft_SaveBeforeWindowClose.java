package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_SaveBeforeWindowClose {
    private static final Logger riftflux$LOGGER = LogManager.getLogger("RiftFlux");
    private static boolean riftflux$savingBeforeClose;

    @Inject(
            method = "runGameLoop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;shutdown()V"
            )
    )
    private void riftflux$saveBeforeWindowClose(CallbackInfo ci) {
        IsometricPhotoModeController.instance().onShutdown();

        Minecraft minecraft = (Minecraft)(Object)this;
        if (riftflux$savingBeforeClose || !minecraft.isIntegratedServerRunning()) {
            return;
        }

        IntegratedServer server = minecraft.getIntegratedServer();
        if (server == null || server.isServerStopped()) {
            return;
        }

        riftflux$savingBeforeClose = true;
        try {
            riftflux$LOGGER.info("Window close requested; waiting for integrated server to save before exiting.");
            if (minecraft.loadingScreen != null) {
                minecraft.loadingScreen.resetProgresAndWorkingMessage("Saving world before exit...");
            }

            server.initiateShutdown();
            while (!server.isServerStopped()) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (Throwable throwable) {
            riftflux$LOGGER.warn("Failed while waiting for integrated server to save before window close", throwable);
        }
    }
}
