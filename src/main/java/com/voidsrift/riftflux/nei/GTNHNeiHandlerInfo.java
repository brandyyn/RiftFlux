package com.voidsrift.riftflux.nei;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.nbt.NBTTagCompound;

/**
 * GTNH NEI (NotEnoughItems fork) renders recipe-tab icons from HandlerInfo.
 * Those are normally loaded from NEI's CSV, but mods can register them via IMC.
 */
public final class GTNHNeiHandlerInfo {
    private static boolean registered;

    private GTNHNeiHandlerInfo() {}

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        if (!Loader.isModLoaded("NotEnoughItems")) {
            return;
        }
        // Only add the icon if Clay Soldiers is present, otherwise NEI will log missing item.
        if (!Loader.isModLoaded("claysoldiers")) {
            return;
        }

        try {
            final NBTTagCompound tag = new NBTTagCompound();
            tag.setString("handler", ClaySoldiersRecipeHandler.class.getName());
            tag.setString("modName", "Clay Soldiers");
            tag.setString("modId", "claysoldiers");
            tag.setBoolean("modRequired", true);

            tag.setString("itemName", "claysoldiers:clayman_doll");
            tag.setString("nbtInfo", "{team:\"red\"}");

            tag.setInteger("handlerHeight", 65);
            tag.setInteger("handlerWidth", 166);
            tag.setBoolean("multipleWidgetsAllowed", true);
            tag.setBoolean("showFavoritesButton", true);
            tag.setBoolean("showOverlayButton", true);

            FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", tag);
        } catch (Throwable t) {
            FMLLog.severe("[RiftFlux] Failed to register GTNH NEI handler info for Clay Soldiers: %s", t);
        }
    }
}
