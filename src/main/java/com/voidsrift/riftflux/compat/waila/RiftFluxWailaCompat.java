package com.voidsrift.riftflux.compat.waila;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.nmccoy.legendgear.legacy.blocks.TileEntityJar;

public final class RiftFluxWailaCompat {
    private static final ClayJarWailaProvider CLAY_JAR_PROVIDER = new ClayJarWailaProvider();

    private RiftFluxWailaCompat() {
    }

    public static void register() {
        if (!Loader.isModLoaded("Waila")) {
            return;
        }
        FMLInterModComms.sendMessage("Waila", "register", RiftFluxWailaCompat.class.getName() + ".callbackRegister");
    }

    public static void callbackRegister(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(CLAY_JAR_PROVIDER, TileEntityJar.class);
        registrar.registerNBTProvider(CLAY_JAR_PROVIDER, TileEntityJar.class);
    }
}
