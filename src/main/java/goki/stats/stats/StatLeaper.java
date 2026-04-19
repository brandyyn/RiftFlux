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

public abstract class StatLeaper
extends Stat {
    public StatLeaper(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public float getBonus(int level) {
        return StatLeaper.getFinalBonus((float)Math.pow(level, 1.065) * 0.0195f);
    }

    @Override
    public float getSecondaryBonus(int level) {
        return StatLeaper.getFinalBonus((float)Math.pow(level, 1.1) * 0.0203f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return "Jump " + Helper.trimDecimals(this.getBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% higher and " + Helper.trimDecimals(this.getSecondaryBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% farther when sprinting.";
    }
}

