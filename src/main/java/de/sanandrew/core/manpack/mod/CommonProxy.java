/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.mod;

import de.sanandrew.core.manpack.network.NetworkManager;
import de.sanandrew.core.manpack.network.PacketProcessor;
import de.sanandrew.core.manpack.network.ServerPacketHandler;

public class CommonProxy {
    public void registerRenderStuff() {
    }

    public void registerPacketHandler(String modId, String modChannel, PacketProcessor packetProcessor) {
        NetworkManager.getPacketChannel(modId).register((Object)new ServerPacketHandler(modId, modChannel));
    }
}

