/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.network.packet;

public enum EnumParticleFx {
    FX_BREAK,
    FX_CRIT,
    FX_SOLDIER_DEATH,
    FX_HORSE_DEATH,
    FX_DIGGING,
    FX_SPELL,
    FX_NEXUS,
    FX_SHOCKWAVE,
    FX_MAGMAFUSE,
    FX_BUNNY_DEATH,
    FX_TURTLE_DEATH;

    public static final EnumParticleFx[] VALUES;

    public final byte ordinalByte() {
        return (byte)this.ordinal();
    }

    static {
        VALUES = EnumParticleFx.values();
    }
}

