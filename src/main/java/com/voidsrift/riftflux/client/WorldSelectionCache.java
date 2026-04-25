package com.voidsrift.riftflux.client;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.world.storage.ISaveFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WorldSelectionCache {
    private static final Logger LOGGER = LogManager.getLogger("RiftFlux");
    private static final Object LOCK = new Object();

    private static List cachedSaveList;
    private static List pendingSaveList;
    private static AnvilConverterException pendingError;
    private static boolean loading;
    private static int generation;

    private WorldSelectionCache() {
    }

    public static List getCachedSaveListAndRefresh(ISaveFormat saveFormat) {
        startRefresh(saveFormat);
        synchronized (LOCK) {
            return cachedSaveList == null ? new ArrayList() : new ArrayList(cachedSaveList);
        }
    }

    public static List consumePendingSaveList() {
        synchronized (LOCK) {
            if (pendingSaveList == null) {
                return null;
            }
            List saveList = new ArrayList(pendingSaveList);
            pendingSaveList = null;
            return saveList;
        }
    }

    public static AnvilConverterException consumePendingError() {
        synchronized (LOCK) {
            AnvilConverterException error = pendingError;
            pendingError = null;
            return error;
        }
    }

    public static boolean isLoading() {
        synchronized (LOCK) {
            return loading;
        }
    }

    public static void invalidate() {
        synchronized (LOCK) {
            generation++;
            cachedSaveList = null;
            pendingSaveList = null;
            pendingError = null;
            loading = false;
        }
    }

    public static void releaseScreenCache() {
        invalidate();
    }

    private static void startRefresh(final ISaveFormat saveFormat) {
        final int refreshGeneration;
        synchronized (LOCK) {
            if (loading) {
                return;
            }
            loading = true;
            refreshGeneration = generation;
        }

        Thread loaderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                List loadedSaveList = null;
                AnvilConverterException error = null;
                try {
                    loadedSaveList = saveFormat.getSaveList();
                    Collections.sort(loadedSaveList);
                    loadedSaveList = new ArrayList(loadedSaveList);
                } catch (AnvilConverterException anvilConverterException) {
                    error = anvilConverterException;
                    LOGGER.warn("Unable to refresh world selection list", anvilConverterException);
                } catch (Throwable throwable) {
                    String message = throwable.getMessage();
                    error = new AnvilConverterException(message == null ? throwable.getClass().getName() : message);
                    LOGGER.warn("Unexpected failure while refreshing world selection list", throwable);
                }

                synchronized (LOCK) {
                    if (refreshGeneration == generation) {
                        if (error == null) {
                            cachedSaveList = loadedSaveList;
                            pendingSaveList = loadedSaveList;
                            pendingError = null;
                        } else {
                            pendingError = error;
                        }
                        loading = false;
                    }
                }
            }
        }, "RiftFlux World Selection Loader");
        loaderThread.setDaemon(true);
        loaderThread.start();
    }
}
