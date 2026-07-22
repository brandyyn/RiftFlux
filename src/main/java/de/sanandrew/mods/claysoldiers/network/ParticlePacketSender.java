/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.network;

import de.sanandrew.core.manpack.util.javatuples.Quartet;
import de.sanandrew.core.manpack.util.javatuples.Quintet;
import de.sanandrew.core.manpack.util.javatuples.Septet;
import de.sanandrew.mods.claysoldiers.network.PacketManager;
import de.sanandrew.mods.claysoldiers.network.packet.EnumParticleFx;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public final class ParticlePacketSender {
    public static void sendSoldierDeathFx(double x, double y, double z, int dimension, String team) {
        PacketManager.sendToAllAround((short)1, dimension, x, y, z, 64.0, Quintet.with(EnumParticleFx.FX_SOLDIER_DEATH, x, y, z, team));
    }

    public static void sendBreakFx(double x, double y, double z, int dimension, Item item) {
        PacketManager.sendToAllAround((short)1, dimension, x, y, z, 64.0, Quintet.with(EnumParticleFx.FX_BREAK, x, y, z, Item.itemRegistry.getNameForObject(item)));
    }

    public static void sendDiggingFx(double x, double y, double z, int dimension, Block block) {
        PacketManager.sendToAllAround((short)1, dimension, x, y, z, 64.0, Quintet.with(EnumParticleFx.FX_DIGGING, x, y, z, Block.blockRegistry.getNameForObject(block)));
    }

    public static void sendSpellFx(double x, double y, double z, int dimension, double red, double green, double blue) {
        PacketManager.sendToAllAround((short)1, dimension, x, y, z, 64.0, Septet.with(EnumParticleFx.FX_SPELL, x, y, z, red, green, blue));
    }

    public static void sendCritFx(double x, double y, double z, int dimension) {
        PacketManager.sendToAllAround((short)1, dimension, x, y, z, 64.0, Quartet.with(EnumParticleFx.FX_CRIT, x, y, z));
    }

    public static void sendHorseDeathFx(double x, double y, double z, int dimension, byte type) {
        PacketManager.sendToAllAround((short)1, dimension, x, y, z, 64.0, Quintet.with(EnumParticleFx.FX_HORSE_DEATH, x, y, z, type));
    }

    public static void sendBunnyDeathFx(double x, double y, double z, int dimension, byte type) {
        PacketManager.sendToAllAround((short)1, dimension, x, y, z, 64.0, Quintet.with(EnumParticleFx.FX_BUNNY_DEATH, x, y, z, type));
    }

    public static void sendTurtleDeathFx(double x, double y, double z, int dimension, byte type) {
        PacketManager.sendToAllAround((short)1, dimension, x, y, z, 64.0, Quintet.with(EnumParticleFx.FX_TURTLE_DEATH, x, y, z, type));
    }

    public static void sendShockwaveFx(double x, double y, double z, float yOff, int dimension) {
        PacketManager.sendToAllAround((short)1, dimension, x, y, z, 64.0, Quartet.with(EnumParticleFx.FX_SHOCKWAVE, x, y - (double)0.2f - (double)yOff, z));
    }

    public static void sendMagmafuseFx(double x, double y, double z, int dimension) {
        PacketManager.sendToAllAround((short)1, dimension, x, y, z, 64.0, Quartet.with(EnumParticleFx.FX_MAGMAFUSE, x, y, z));
    }
}

