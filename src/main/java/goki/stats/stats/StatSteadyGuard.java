/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package goki.stats.stats;

import goki.stats.lib.Helper;
import goki.stats.stats.Stat;
import net.minecraft.entity.player.EntityPlayer;

public class StatSteadyGuard
extends Stat {
    public StatSteadyGuard(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public float getBonus(int level) {
        return Math.min(StatSteadyGuard.getFinalBonus((float)Math.pow(level, 1.3615)), 100.0f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return Helper.trimDecimals(this.getBonus(player), 1) + "% less knockback when blocking.";
    }
}

