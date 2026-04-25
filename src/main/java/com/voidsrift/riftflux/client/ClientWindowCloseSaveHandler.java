package com.voidsrift.riftflux.client;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ClientWindowCloseSaveHandler {
    private static final Logger LOGGER = LogManager.getLogger("RiftFlux");
    private static boolean savingBeforeClose;

    private ClientWindowCloseSaveHandler() {
    }

    public static void saveIntegratedServerBeforeWindowClose(Minecraft minecraft) {
        if (minecraft == null || savingBeforeClose || !minecraft.isIntegratedServerRunning()) {
            return;
        }

        IntegratedServer server = minecraft.getIntegratedServer();
        if (server == null || server.isServerStopped()) {
            return;
        }

        savingBeforeClose = true;
        try {
            LOGGER.info("Window close requested; waiting for integrated server to save before exiting.");
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
            LOGGER.warn("Failed while waiting for integrated server to save before window close", throwable);
        }
    }
}
