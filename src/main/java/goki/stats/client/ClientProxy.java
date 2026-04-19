/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 */
package goki.stats.client;

import cpw.mods.fml.common.FMLCommonHandler;
import goki.stats.CommonProxy;
import goki.stats.handlers.GokiKeyHandler;

public class ClientProxy
extends CommonProxy {
    @Override
    public void registerKeybinding() {
        GokiKeyHandler keyHandler = new GokiKeyHandler();
        FMLCommonHandler.instance().bus().register((Object)keyHandler);
    }

    @Override
    public void registerSounds() {
    }
}

