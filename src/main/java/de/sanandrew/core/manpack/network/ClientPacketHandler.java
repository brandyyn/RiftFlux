/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.network.FMLNetworkEvent$ClientCustomPacketEvent
 */
package de.sanandrew.core.manpack.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import de.sanandrew.core.manpack.network.NetworkManager;
import net.minecraft.client.network.NetHandlerPlayClient;

public final class ClientPacketHandler {
    private final String channel;
    private final String modId;

    public ClientPacketHandler(String modId, String modChannel) {
        this.channel = modChannel;
        this.modId = modId;
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        NetHandlerPlayClient netHandlerPlayClient = (NetHandlerPlayClient)event.handler;
        if (event.packet.channel().equals(this.channel)) {
            NetworkManager.getPacketProcessor(this.modId).processPacket(event.packet.payload(), netHandlerPlayClient);
        }
    }
}

