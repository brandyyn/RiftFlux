/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  net.minecraft.entity.player.EntityPlayer
 */
package goki.stats.handlers;

import goki.stats.handlers.AbstractPacket;
import goki.stats.lib.Helper;
import goki.stats.stats.Stat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class PacketStatSync
extends AbstractPacket {
    int[] statLevels;

    public PacketStatSync() {
    }

    public PacketStatSync(EntityPlayer player) {
        this.statLevels = new int[Stat.stats.size()];
        for (int i = 0; i < this.statLevels.length; ++i) {
            if (Stat.stats.get(i) == null) continue;
            this.statLevels[i] = Helper.getPlayerStatLevel(player, Stat.stats.get(i));
        }
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        for (int i = 0; i < this.statLevels.length; ++i) {
            buffer.writeInt(this.statLevels[i]);
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        this.statLevels = new int[Stat.stats.size()];
        for (int i = 0; i < this.statLevels.length; ++i) {
            this.statLevels[i] = buffer.readInt();
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        for (int i = 0; i < this.statLevels.length; ++i) {
            Helper.setPlayerStatLevel(player, Stat.stats.get(i), this.statLevels[i]);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
    }
}

