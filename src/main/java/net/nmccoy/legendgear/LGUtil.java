/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.S29PacketSoundEffect
 */
package net.nmccoy.legendgear;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public class LGUtil {
    public static void soundForPlayer(EntityPlayer player, String sound, float volume, float pitch) {
        if (player instanceof EntityPlayerMP && !player.worldObj.isRemote) {
            EntityPlayerMP emp = (EntityPlayerMP)player;
            S29PacketSoundEffect packet = new S29PacketSoundEffect(sound, player.posX, player.posY, player.posZ, volume, pitch);
            emp.playerNetServerHandler.sendPacket((Packet)packet);
        }
    }

    public static int rollDF(int dice, Random rand) {
        int total = 0;
        for (int i = 0; i < dice; ++i) {
            total += rand.nextInt(3) - 1;
        }
        return total;
    }
}

