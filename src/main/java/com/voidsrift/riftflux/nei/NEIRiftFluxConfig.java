package com.voidsrift.riftflux.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.FMLLog;

/**
 * NEI discovers plugins in 1.7.10 by scanning for classes named NEI*Config.
 */
@Optional.Interface(iface = "codechicken.nei.api.IConfigureNEI", modid = "NotEnoughItems")
public class NEIRiftFluxConfig implements IConfigureNEI {

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public void loadConfig() {
        if (Loader.isModLoaded("claysoldiers")) {
            try {
                ClaySoldiersRecipeHandler handler = new ClaySoldiersRecipeHandler();
                API.registerRecipeHandler(handler);
                API.registerUsageHandler(handler);
                FMLLog.info("[RiftFlux] NEI plugin loaded: Clay Soldiers handler registered.");
            } catch (Throwable t) {
                FMLLog.severe("[RiftFlux] Failed to register Clay Soldiers NEI handler: %s", t);
            }
        }
    }

    @Override
    public String getName() {
        return "RiftFlux NEI Plugin";
    }

    @Override
    public String getVersion() {
        return com.voidsrift.riftflux.Constants.VERSION;
    }
}
