/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.ByteBufUtils
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.ByteBufOutputStream
 */
package de.sanandrew.mods.claysoldiers.network.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import de.sanandrew.core.manpack.network.IPacket;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.claysoldiers.util.ClaySoldiersMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;

public class PacketSendUpgradeNBT
implements IPacket {
    @Override
    public void process(ByteBufInputStream stream, ByteBuf rawData, INetHandler handler) throws IOException {
        ClaySoldiersMod.proxy.applyUpgradeNbt(stream.readInt(), stream.readByte(), ByteBufUtils.readTag((ByteBuf)rawData));
    }

    @Override
    public void writeData(ByteBufOutputStream stream, Tuple dataTuple) throws IOException {
        Triplet data = (Triplet)dataTuple;
        stream.writeInt(((Integer)data.getValue0()).intValue());
        stream.writeByte((int)((Byte)data.getValue1()).byteValue());
        ByteBufUtils.writeTag((ByteBuf)stream.buffer(), (NBTTagCompound)((NBTTagCompound)data.getValue2()));
    }
}

