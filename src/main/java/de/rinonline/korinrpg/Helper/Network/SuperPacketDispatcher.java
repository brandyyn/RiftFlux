/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  cpw.mods.fml.common.network.NetworkRegistry$TargetPoint
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 */
package de.rinonline.korinrpg.Helper.Network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import de.rinonline.korinrpg.Helper.Network.AbstractMessage;
import de.rinonline.korinrpg.Helper.Network.SyncNewPlayerPropsMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class SuperPacketDispatcher {
    private static byte packetId = 0;
    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel("dss");

    public static final void registerPackets() {
        SuperPacketDispatcher.registerMessage(SyncNewPlayerPropsMessage.class, SyncNewPlayerPropsMessage.class, Side.CLIENT);
    }

    private static final void registerMessage(Class handlerClass, Class messageClass, Side side) {
        byte by = packetId;
        packetId = (byte)(by + 1);
        dispatcher.registerMessage(handlerClass, messageClass, (int)by, side);
    }

    private static final <T extends AbstractMessage<T>> void registerMessage(Class<T> clazz) {
        if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
            byte by = packetId;
            packetId = (byte)(by + 1);
            dispatcher.registerMessage(clazz, clazz, (int)by, Side.CLIENT);
        } else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
            byte by = packetId;
            packetId = (byte)(by + 1);
            dispatcher.registerMessage(clazz, clazz, (int)by, Side.SERVER);
        } else {
            dispatcher.registerMessage(clazz, clazz, (int)packetId, Side.CLIENT);
            byte by = packetId;
            packetId = (byte)(by + 1);
            dispatcher.registerMessage(clazz, clazz, (int)by, Side.SERVER);
        }
    }

    public static final void sendTo(IMessage message, EntityPlayerMP player) {
        dispatcher.sendTo(message, player);
    }

    public static final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        dispatcher.sendToAllAround(message, point);
    }

    public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
        SuperPacketDispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
    }

    public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
        SuperPacketDispatcher.sendToAllAround(message, player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, range);
    }

    public static final void sendToDimension(IMessage message, int dimensionId) {
        dispatcher.sendToDimension(message, dimensionId);
    }

    public static final void sendToServer(IMessage message) {
        dispatcher.sendToServer(message);
    }
}

