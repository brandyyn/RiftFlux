/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package goki.stats.stats;

import goki.stats.stats.Stat;
import net.minecraft.entity.player.EntityPlayer;

public class StatFurnaceFinesse
extends Stat {
    public StatFurnaceFinesse(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public float getBonus(int level) {
        return Math.min(StatFurnaceFinesse.getFinalBonus((float)level / 5.0f), 199.0f) + 1.0f;
    }

    @Override
    public float getSecondaryBonus(int level) {
        return Math.min(StatFurnaceFinesse.getFinalBonus(level), 100.0f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        int level = this.getPlayerStatLevel(player);
        return "Furnaces around you smelt " + this.getBonus(level) + " ticks faster " + this.getSecondaryBonus(level) + "% of the time.";
    }
}

