/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package goki.stats.stats;

import goki.stats.lib.Helper;
import goki.stats.stats.Stat;
import goki.stats.stats.StatLeaper;
import net.minecraft.entity.player.EntityPlayer;

public class StatStealth
extends StatLeaper {
    public StatStealth(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public float getBonus(int level) {
        return StatStealth.getFinalBonus((float)Math.pow(level, 1.3416));
    }

    @Override
    public int isAffectedByStat(Object object) {
        if (object instanceof EntityPlayer && ((EntityPlayer)object).isSneaking()) {
            return 1;
        }
        return 0;
    }

    @Override
    public float getSecondaryBonus(int level) {
        return StatStealth.getFinalBonus((float)Math.pow(level, 1.4307));
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        float speed = Helper.trimDecimals(this.getBonus(player), 1);
        float reapBonus = Helper.trimDecimals(this.getSecondaryBonus(player), 1);
        float reap = Stat.STAT_REAPER.getBonus(player) * 100.0f;
        float newReap = Helper.trimDecimals(reap + reap * reapBonus / 100.0f, 1);
        return "Move " + speed + "% faster and reap " + reapBonus + "% more often (" + newReap + "%) when sneaking.";
    }
}

