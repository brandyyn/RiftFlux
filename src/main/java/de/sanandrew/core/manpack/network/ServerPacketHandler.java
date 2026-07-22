/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.network.FMLNetworkEvent$ServerCustomPacketEvent
 */
package de.sanandrew.core.manpack.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import de.sanandrew.core.manpack.network.NetworkManager;
import net.minecraft.network.NetHandlerPlayServer;

public final class ServerPacketHandler {
    private final String channel;
    private final String modId;

    public ServerPacketHandler(String modId, String modChannel) {
        this.channel = modChannel;
        this.modId = modId;
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        NetHandlerPlayServer netHandlerPlayServer = (NetHandlerPlayServer)event.handler;
        if (event.packet.channel().equals(this.channel)) {
            NetworkManager.getPacketProcessor(this.modId).processPacket(event.packet.payload(), netHandlerPlayServer);
        }
    }
}

