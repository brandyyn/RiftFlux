/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package goki.stats.stats;

import net.minecraft.entity.player.EntityPlayer;

public interface IStat {
    public int isAffectedByStat(Object var1);

    public int isAffectedByStat(Object var1, Object var2);

    public int isAffectedByStat(Object var1, Object var2, Object var3);

    public String getSimpleDescriptionString();

    public String getAppliedDescriptionString(EntityPlayer var1);

    public float getBonus(EntityPlayer var1);

    public float getBonus(int var1);

    public float getSecondaryBonus(int var1);

    public float getSecondaryBonus(EntityPlayer var1);

    public float getAppliedBonus(EntityPlayer var1, Object var2);

    public int getCost(int var1);

    public int getLimit();
}

