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

public class StatPugilism
extends Stat {
    public StatPugilism(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public float getBonus(int level) {
        return StatPugilism.getFinalBonus((float)Math.pow(level, 1.03) * 0.1816f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return "Deal " + Helper.trimDecimals(this.getBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% more damage with your tree punchers.";
    }
}

