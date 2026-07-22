/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.ByteBufOutputStream
 */
package de.sanandrew.core.manpack.network;

import de.sanandrew.core.manpack.util.javatuples.Tuple;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import net.minecraft.network.INetHandler;

public interface IPacket {
    public void process(ByteBufInputStream var1, ByteBuf var2, INetHandler var3) throws IOException;

    public void writeData(ByteBufOutputStream var1, Tuple var2) throws IOException;
}

