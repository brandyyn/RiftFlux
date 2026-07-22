/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufInputStream
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.core.manpack.network;

import com.google.common.collect.Maps;
import de.sanandrew.core.manpack.init.ManPackLoadingPlugin;
import de.sanandrew.core.manpack.network.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import java.io.IOException;
import java.util.Map;
import net.minecraft.network.INetHandler;
import org.apache.logging.log4j.Level;

public final class PacketProcessor {
    private final String modId;
    private final Map<Short, IPacket> packetMap = Maps.newHashMap();

    public PacketProcessor(String modId) {
        this.modId = modId;
    }

    public void processPacket(ByteBuf data, INetHandler handler) {
        short packetId = -1;
        try (ByteBufInputStream bbis = new ByteBufInputStream(data);){
            packetId = bbis.readShort();
            IPacket packet = this.packetMap.get(packetId);
            if (packet != null) {
                packet.process(bbis, data, handler);
            }
        }
        catch (IOException ioe) {
            ManPackLoadingPlugin.MOD_LOG.log(Level.ERROR, "The packet with the ID %d from %s cannot be processed!", new Object[]{packetId, this.modId});
            ioe.printStackTrace();
        }
    }

    public IPacket getPacket(short packetId) {
        return this.packetMap.get(packetId);
    }

    public void addPacketCls(int packetId, Class<? extends IPacket> packetCls) {
        try {
            this.packetMap.put((short)packetId, packetCls.getConstructor().newInstance());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException("Cannot instantiate packet " + packetCls.getName(), exception);
        }
    }
}
