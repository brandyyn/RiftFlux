/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.DamageSource
 *  net.minecraftforge.common.config.Configuration
 */
package goki.stats;

import goki.stats.lib.Reference;
import goki.stats.stats.Stat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;

public abstract class DamageSourceProtectionStat
extends Stat {
    public List<String> damageSources = new ArrayList<String>();

    public DamageSourceProtectionStat(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public int isAffectedByStat(Object object) {
        if (object != null && object instanceof DamageSource) {
            DamageSource source = (DamageSource)object;
            for (int i = 0; i < this.damageSources.size(); ++i) {
                if (!source.damageType.equals(this.damageSources.get(i))) continue;
                return 1;
            }
        }
        return 0;
    }

    @Override
    public void loadFromConfigurationFile(Configuration config) {
        this.damageSources.clear();
        String[] sources = Reference.configuration.get("Support", this.name + " Sources", this.getDefaultDamageSources()).getStringList();
        for (int i = 0; i < sources.length; ++i) {
            this.damageSources.add(sources[i]);
        }
    }

    @Override
    public String toConfigurationString() {
        String configString = "";
        for (String s : this.damageSources) {
            configString = configString + "," + s;
        }
        return configString.substring(1);
    }

    @Override
    public void saveToConfigurationFile(Configuration config) {
        String[] sources = new String[this.damageSources.size()];
        for (int i = 0; i < sources.length; ++i) {
            sources[i] = this.damageSources.get(i);
        }
        Reference.configuration.get("Support", this.name + " Sources", this.getDefaultDamageSources()).set(sources);
    }

    @Override
    public void fromConfigurationString(String configString) {
        String[] configStringSplit;
        this.damageSources.clear();
        for (String s : configStringSplit = configString.split(",")) {
            this.damageSources.add(s);
        }
    }

    public abstract String[] getDefaultDamageSources();
}

