/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  cpw.mods.fml.common.network.FMLEventChannel
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  cpw.mods.fml.common.network.NetworkRegistry$TargetPoint
 *  cpw.mods.fml.common.network.internal.FMLProxyPacket
 *  io.netty.buffer.ByteBufOutputStream
 *  io.netty.buffer.Unpooled
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.core.manpack.network;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import de.sanandrew.core.manpack.init.ManPackLoadingPlugin;
import de.sanandrew.core.manpack.mod.CommonProxy;
import de.sanandrew.core.manpack.network.IPacket;
import de.sanandrew.core.manpack.network.PacketProcessor;
import de.sanandrew.core.manpack.util.javatuples.Quintet;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.core.manpack.util.javatuples.Unit;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.Level;

public final class NetworkManager {
    private static final Map<String, Triplet<String, PacketProcessor, FMLEventChannel>> PROCESSORS = Maps.newHashMap();

    public static void registerModHandler(String modId, String modChannel) {
        PROCESSORS.put(modId, Triplet.with(modChannel, new PacketProcessor(modId), NetworkRegistry.INSTANCE.newEventDrivenChannel(modChannel)));
    }

    public static void registerModPacketCls(String modId, int packetId, Class<? extends IPacket> packetCls) {
        PROCESSORS.get(modId).getValue1().addPacketCls(packetId, packetCls);
    }

    public static void initPacketHandler(CommonProxy proxy) {
        for (Map.Entry<String, Triplet<String, PacketProcessor, FMLEventChannel>> processor : PROCESSORS.entrySet()) {
            proxy.registerPacketHandler(processor.getKey(), processor.getValue().getValue0(), processor.getValue().getValue1());
        }
    }

    public static void sendToServer(String modId, short packet, Tuple data) {
        NetworkManager.sendPacketTo(modId, packet, EnumPacketDirections.TO_SERVER, null, data);
    }

    public static void sendToAll(String modId, short packed, Tuple data) {
        NetworkManager.sendPacketTo(modId, packed, EnumPacketDirections.TO_ALL, null, data);
    }

    public static void sendToPlayer(String modId, short packed, EntityPlayerMP player, Tuple data) {
        NetworkManager.sendPacketTo(modId, packed, EnumPacketDirections.TO_PLAYER, Unit.with(player), data);
    }

    public static void sendToAllInDimension(String modId, short packed, int dimensionId, Tuple data) {
        NetworkManager.sendPacketTo(modId, packed, EnumPacketDirections.TO_ALL_IN_DIMENSION, Unit.with(dimensionId), data);
    }

    public static void sendToAllAround(String modId, short packed, int dimensionId, double x, double y, double z, double range, Tuple data) {
        NetworkManager.sendPacketTo(modId, packed, EnumPacketDirections.TO_ALL_IN_RANGE, Quintet.with(dimensionId, x, y, z, range), data);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static void sendPacketTo(String modId, short packet, EnumPacketDirections direction, Tuple dirData, Tuple packetData) {
        try (ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());){
            bbos.writeShort((int)packet);
            IPacket pktInst = NetworkManager.getPacketProcessor(modId).getPacket(packet);
            if (pktInst == null) {
                throw new IllegalArgumentException("Unknown packet ID " + packet + " for " + modId);
            }
            FMLEventChannel channel = NetworkManager.getPacketChannel(modId);
            pktInst.writeData(bbos, packetData);
            FMLProxyPacket proxyPacket = new FMLProxyPacket(bbos.buffer(), NetworkManager.getPacketChannelName(modId));
            switch (direction) {
                case TO_SERVER: {
                    channel.sendToServer(proxyPacket);
                    return;
                }
                case TO_ALL: {
                    channel.sendToAll(proxyPacket);
                    return;
                }
                case TO_PLAYER: {
                    channel.sendTo(proxyPacket, (EntityPlayerMP)dirData.getValue(0));
                    return;
                }
                case TO_ALL_IN_RANGE: {
                    channel.sendToAllAround(proxyPacket, new NetworkRegistry.TargetPoint(((Integer)dirData.getValue(0)).intValue(), ((Double)dirData.getValue(1)).doubleValue(), ((Double)dirData.getValue(2)).doubleValue(), ((Double)dirData.getValue(3)).doubleValue(), ((Double)dirData.getValue(4)).doubleValue()));
                    return;
                }
                case TO_ALL_IN_DIMENSION: {
                    channel.sendToDimension(proxyPacket, ((Integer)dirData.getValue(0)).intValue());
                    return;
                }
            }
            return;
        }
        catch (IOException ioe) {
            ManPackLoadingPlugin.MOD_LOG.log(Level.ERROR, "The packet ID %d from %s cannot be processed!", new Object[]{packet, modId});
            ioe.printStackTrace();
            return;
        }
    }

    public static String getPacketChannelName(String modId) {
        return PROCESSORS.get(modId).getValue0();
    }

    public static PacketProcessor getPacketProcessor(String modId) {
        return PROCESSORS.get(modId).getValue1();
    }

    public static FMLEventChannel getPacketChannel(String modId) {
        return PROCESSORS.get(modId).getValue2();
    }

    private static enum EnumPacketDirections {
        TO_SERVER,
        TO_PLAYER,
        TO_ALL,
        TO_ALL_IN_RANGE,
        TO_ALL_IN_DIMENSION;

    }
}
