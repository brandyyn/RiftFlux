/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 */
package goki.stats.handlers;

import goki.stats.GokiStats;
import goki.stats.handlers.AbstractPacket;
import goki.stats.handlers.PacketStatSync;
import goki.stats.handlers.PacketSyncXP;
import goki.stats.lib.Helper;
import goki.stats.stats.Stat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketStatAlter
extends AbstractPacket {
    int stat;
    int amount;

    public PacketStatAlter() {
    }

    public PacketStatAlter(int stat, int amount) {
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeInt(this.stat);
        buffer.writeInt(this.amount);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        this.stat = buffer.readInt();
        this.amount = buffer.readInt();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        if (player != null) {
            if (this.amount > 0) {
                Stat stat = Stat.stats.get(this.stat);
                int level = Helper.getPlayerStatLevel(player, stat);
                int cost = stat.getCost(level + this.amount - 1);
                int currentXP = Helper.getXPTotal(player.experienceLevel, player.experience);
                if (stat.enabled) {
                    if (level + this.amount > stat.getLimit()) {
                        this.amount = 0;
                    }
                    if (currentXP >= cost && this.amount != 0) {
                        Helper.setPlayerStatLevel(player, stat, level + this.amount);
                        if (this.amount > 0) {
                            Helper.setPlayersExpTo(player, currentXP - cost);
                        }
                    }
                }
            } else if (this.amount < 0) {
                // empty if block
            }
            GokiStats.packetPipeline.sendTo(new PacketStatSync(player), (EntityPlayerMP)player);
            GokiStats.packetPipeline.sendTo(new PacketSyncXP(player), (EntityPlayerMP)player);
        }
    }
}

