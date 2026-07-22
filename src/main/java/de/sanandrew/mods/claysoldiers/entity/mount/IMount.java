/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.entity.mount;

import net.minecraft.entity.EntityLivingBase;

public interface IMount<T extends EntityLivingBase> {
    public IMount setSpawnedFromNexus();

    public int getType();

    public void setSpecial();

    public boolean isSpecial();
}

