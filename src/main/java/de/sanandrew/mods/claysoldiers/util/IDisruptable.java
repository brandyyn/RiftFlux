/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util;

import net.minecraft.util.DamageSource;

public interface IDisruptable {
    public static final DamageSource DISRUPT_DAMAGE = new DamageSource("claysoldiers:disrupt").setDamageBypassesArmor().setMagicDamage();

    public void disrupt();
}

