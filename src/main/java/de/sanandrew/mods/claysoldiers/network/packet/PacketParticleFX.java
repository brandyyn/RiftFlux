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
import de.sanandrew.core.manpack.util.javatuples.Quartet;
import de.sanandrew.core.manpack.util.javatuples.Sextet;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.claysoldiers.network.packet.EnumParticleFx;
import de.sanandrew.mods.claysoldiers.util.ClaySoldiersMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import net.minecraft.network.INetHandler;

public class PacketParticleFX
implements IPacket {
    @Override
    public void process(ByteBufInputStream stream, ByteBuf rawData, INetHandler handler) throws IOException {
        EnumParticleFx fxType = EnumParticleFx.VALUES[stream.readByte()];
        switch (fxType) {
            case FX_DIGGING: 
            case FX_BREAK: 
            case FX_SOLDIER_DEATH: {
                ClaySoldiersMod.proxy.spawnParticles(fxType, Quartet.with(stream.readDouble(), stream.readDouble() + 0.5, stream.readDouble(), stream.readUTF()));
                break;
            }
            case FX_CRIT: {
                ClaySoldiersMod.proxy.spawnParticles(fxType, Triplet.with(stream.readDouble(), stream.readDouble() + 0.1, stream.readDouble()));
                break;
            }
            case FX_HORSE_DEATH: 
            case FX_BUNNY_DEATH: 
            case FX_TURTLE_DEATH: {
                ClaySoldiersMod.proxy.spawnParticles(fxType, Quartet.with(stream.readDouble(), stream.readDouble() + 0.5, stream.readDouble(), stream.readByte()));
                break;
            }
            case FX_SPELL: {
                ClaySoldiersMod.proxy.spawnParticles(fxType, Sextet.with(stream.readDouble(), stream.readDouble() + 0.5, stream.readDouble(), stream.readDouble(), stream.readDouble(), stream.readDouble()));
                break;
            }
            case FX_SHOCKWAVE: 
            case FX_MAGMAFUSE: {
                ClaySoldiersMod.proxy.spawnParticles(fxType, Triplet.with(stream.readDouble(), stream.readDouble(), stream.readDouble()));
            }
        }
    }

    @Override
    public void writeData(ByteBufOutputStream stream, Tuple dataTuple) throws IOException {
        EnumParticleFx fxType = (EnumParticleFx)((Object)dataTuple.getValue(0));
        stream.writeByte((int)fxType.ordinalByte());
        switch (fxType) {
            case FX_DIGGING: 
            case FX_BREAK: 
            case FX_SOLDIER_DEATH: {
                stream.writeDouble(((Double)dataTuple.getValue(1)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(2)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(3)).doubleValue());
                stream.writeUTF((String)dataTuple.getValue(4));
                break;
            }
            case FX_CRIT: 
            case FX_SHOCKWAVE: 
            case FX_MAGMAFUSE: {
                stream.writeDouble(((Double)dataTuple.getValue(1)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(2)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(3)).doubleValue());
                break;
            }
            case FX_HORSE_DEATH: 
            case FX_BUNNY_DEATH: 
            case FX_TURTLE_DEATH: {
                stream.writeDouble(((Double)dataTuple.getValue(1)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(2)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(3)).doubleValue());
                stream.writeByte((int)((Byte)dataTuple.getValue(4)).byteValue());
                break;
            }
            case FX_SPELL: {
                stream.writeDouble(((Double)dataTuple.getValue(1)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(2)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(3)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(4)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(5)).doubleValue());
                stream.writeDouble(((Double)dataTuple.getValue(6)).doubleValue());
            }
        }
    }
}

