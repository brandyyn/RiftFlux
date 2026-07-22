package de.sanandrew.mods.claysoldiers.util;

import de.sanandrew.core.manpack.mod.CommonProxy;
import de.sanandrew.core.manpack.network.ClientPacketHandler;
import de.sanandrew.core.manpack.network.NetworkManager;
import de.sanandrew.core.manpack.network.PacketProcessor;

/**
 * Registers Clay Soldiers' client packet handler without initializing the
 * unrelated SAP Manager Pack rendering, updater, and keybinding lifecycle.
 */
final class ClaySoldiersNetworkClientProxy extends CommonProxy {
    @Override
    public void registerPacketHandler(String modId, String modChannel, PacketProcessor packetProcessor) {
        super.registerPacketHandler(modId, modChannel, packetProcessor);
        NetworkManager.getPacketChannel(modId).register(new ClientPacketHandler(modId, modChannel));
    }
}
