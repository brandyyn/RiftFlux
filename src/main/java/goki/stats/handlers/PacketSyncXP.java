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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSyncXP
extends AbstractPacket {
    float experience;
    int experienceLevel;
    int experienceTotal;

    public PacketSyncXP() {
    }

    public PacketSyncXP(EntityPlayer player) {
        this.experience = player.experience;
        this.experienceLevel = player.experienceLevel;
        this.experienceTotal = player.experienceTotal;
    }

    public PacketSyncXP(float experience, int experienceLevel, int experienceTotal) {
        this.experience = experience;
        this.experienceLevel = experienceLevel;
        this.experienceTotal = experienceTotal;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeFloat(this.experience);
        buffer.writeInt(this.experienceLevel);
        buffer.writeInt(this.experienceTotal);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        this.experience = buffer.readFloat();
        this.experienceLevel = buffer.readInt();
        this.experienceTotal = buffer.readInt();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        player.experience = this.experience;
        player.experienceLevel = this.experienceLevel;
        player.experienceTotal = this.experienceTotal;
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        player.experience = this.experience;
        player.experienceLevel = this.experienceLevel;
        player.experienceTotal = this.experienceTotal;
    }
}

