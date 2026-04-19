/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.common.config.Configuration
 */
package goki.stats.stats;

import goki.stats.lib.Helper;
import goki.stats.lib.Reference;
import goki.stats.stats.Stat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

public class StatReaper
extends Stat {
    public static float healthLimit = 20.0f;

    public StatReaper(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public float getBonus(int level) {
        return StatReaper.getFinalBonus((float)Math.pow(level, 1.0768) * 0.0025f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return Helper.trimDecimals(this.getBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% chance to instantly kill enemies with less than " + healthLimit + " health.";
    }

    @Override
    public int isAffectedByStat(Object object) {
        EntityLivingBase target;
        if (object != null && !(object instanceof EntityPlayer) && object instanceof EntityLivingBase && (target = (EntityLivingBase)object).getMaxHealth() <= healthLimit) {
            return 1;
        }
        return 0;
    }

    @Override
    public void loadFromConfigurationFile(Configuration config) {
        healthLimit = (float)Reference.configuration.get("Support", "Reaper Limit", (double)healthLimit).getDouble(20.0);
    }

    @Override
    public void saveToConfigurationFile(Configuration config) {
        Reference.configuration.get("Support", "Reaper Limit", (double)healthLimit).set((double)healthLimit);
    }

    @Override
    public String toConfigurationString() {
        return "" + healthLimit;
    }

    @Override
    public void fromConfigurationString(String configString) {
        try {
            healthLimit = Float.parseFloat(configString);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

