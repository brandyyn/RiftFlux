/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package goki.stats.stats;

import goki.stats.DamageSourceProtectionStat;
import goki.stats.lib.Helper;
import net.minecraft.entity.player.EntityPlayer;

public class StatToughSkin
extends DamageSourceProtectionStat {
    public StatToughSkin(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public float getBonus(int level) {
        return StatToughSkin.getFinalBonus((float)level * 0.026f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return "Take " + Helper.trimDecimals(this.getBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% less damage from explosions.";
    }

    @Override
    public String[] getDefaultDamageSources() {
        return new String[]{"explosion", "explosion.player"};
    }
}

