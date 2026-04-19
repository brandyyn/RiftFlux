/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 */
package goki.stats.stats;

import goki.stats.DamageSourceProtectionStat;
import goki.stats.lib.Helper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class StatFeatherFall
extends DamageSourceProtectionStat {
    public StatFeatherFall(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public float getBonus(int level) {
        return StatFeatherFall.getFinalBonus((float)level * 0.026f);
    }

    @Override
    public float getSecondaryBonus(int level) {
        return StatFeatherFall.getFinalBonus((float)level * 0.1f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        float height = Helper.getFallResistance((EntityLivingBase)player) + Helper.trimDecimals(this.getSecondaryBonus(this.getPlayerStatLevel(player)), 1);
        return "Take " + Helper.trimDecimals(this.getBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% less damage from falling more than " + height + " blocks.";
    }

    @Override
    public String[] getDefaultDamageSources() {
        return new String[]{"fall"};
    }
}

