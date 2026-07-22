/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.network;

import de.sanandrew.core.manpack.network.NetworkManager;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.claysoldiers.network.packet.PacketParticleFX;
import de.sanandrew.mods.claysoldiers.network.packet.PacketSendEffectNBT;
import de.sanandrew.mods.claysoldiers.network.packet.PacketSendUpgradeNBT;
import de.sanandrew.mods.claysoldiers.network.packet.PacketSoldierRender;
import net.minecraft.entity.player.EntityPlayerMP;

public final class PacketManager {
    public static final short PKG_SOLDIER_RENDERS = 0;
    public static final short PKG_PARTICLES = 1;
    public static final short PKG_SOLDIER_EFFECT_NBT = 2;
    public static final short PKG_SOLDIER_UPGRADE_NBT = 3;

    public static void registerPackets() {
        NetworkManager.registerModHandler("claysoldiers", "ClaySoldiersNWCH");
        NetworkManager.registerModPacketCls("claysoldiers", 0, PacketSoldierRender.class);
        NetworkManager.registerModPacketCls("claysoldiers", 1, PacketParticleFX.class);
        NetworkManager.registerModPacketCls("claysoldiers", 2, PacketSendEffectNBT.class);
        NetworkManager.registerModPacketCls("claysoldiers", 3, PacketSendUpgradeNBT.class);
    }

    public static void sendToServer(short packet, Tuple data) {
        NetworkManager.sendToServer("claysoldiers", packet, data);
    }

    public static void sendToAll(short packed, Tuple data) {
        NetworkManager.sendToAll("claysoldiers", packed, data);
    }

    public static void sendToPlayer(short packed, EntityPlayerMP player, Tuple data) {
        NetworkManager.sendToPlayer("claysoldiers", packed, player, data);
    }

    public static void sendToAllInDimension(short packed, int dimensionId, Tuple data) {
        NetworkManager.sendToAllInDimension("claysoldiers", packed, dimensionId, data);
    }

    public static void sendToAllAround(short packed, int dimensionId, double x, double y, double z, double range, Tuple data) {
        NetworkManager.sendToAllAround("claysoldiers", packed, dimensionId, x, y, z, range, data);
    }
}

