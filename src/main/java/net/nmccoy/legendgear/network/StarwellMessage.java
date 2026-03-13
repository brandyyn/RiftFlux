/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  io.netty.buffer.ByteBuf
 */
package net.nmccoy.legendgear.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class StarwellMessage
implements IMessage {
    public int chargeLevel;

    public StarwellMessage() {
    }

    public StarwellMessage(int charge) {
        this.chargeLevel = charge;
    }

    public void fromBytes(ByteBuf buf) {
        this.chargeLevel = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.chargeLevel);
    }
}

