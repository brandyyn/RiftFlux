/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package goki.stats.stats;

import goki.stats.lib.Helper;
import goki.stats.lib.Reference;
import goki.stats.stats.Stat;
import net.minecraft.entity.player.EntityPlayer;

public class StatAthleticism
extends Stat {
    public StatAthleticism(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public float getBonus(int level) {
        return StatAthleticism.getFinalBonus((float)Math.pow(level, 1.1) * 0.029f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        if (Reference.isPlayerAPILoaded) {
            return "I do nothing when PlayerAPI is loaded!";
        }
        return "Swim " + Helper.trimDecimals(this.getBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% faster.";
    }
}

