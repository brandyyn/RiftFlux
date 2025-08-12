package com.excsi.riftfixes.net;

import com.excsi.riftfixes.Constants;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class RFNetwork {
    public static SimpleNetworkWrapper CH;

    public static void init() {
        if (CH != null) return;
        CH = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID);
        CH.registerMessage(MsgPickup.ClientHandler.class, MsgPickup.class, 0, Side.CLIENT);
    }
}