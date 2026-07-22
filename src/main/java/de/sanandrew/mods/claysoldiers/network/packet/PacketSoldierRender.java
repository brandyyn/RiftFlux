/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.ByteBufOutputStream
 */
package de.sanandrew.mods.claysoldiers.network.packet;

import de.sanandrew.core.manpack.network.IPacket;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.claysoldiers.util.ClaySoldiersMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import net.minecraft.network.INetHandler;

public class PacketSoldierRender
implements IPacket {
    @Override
    public void process(ByteBufInputStream stream, ByteBuf rawData, INetHandler handler) throws IOException {
        ClaySoldiersMod.proxy.applySoldierRenderFlags(stream.readInt(), stream.readLong(), stream.readLong(), stream.readLong(), stream.readLong());
    }

    @Override
    public void writeData(ByteBufOutputStream stream, Tuple dataTuple) throws IOException {
        Triplet data = (Triplet)dataTuple;
        stream.writeInt(((Integer)data.getValue0()).intValue());
        stream.writeLong(((long[])data.getValue1())[0]);
        stream.writeLong(((long[])data.getValue1())[1]);
        stream.writeLong(((long[])data.getValue2())[0]);
        stream.writeLong(((long[])data.getValue2())[1]);
    }
}

