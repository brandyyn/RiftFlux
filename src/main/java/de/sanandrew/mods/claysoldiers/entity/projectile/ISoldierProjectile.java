/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;

public interface ISoldierProjectile<T extends EntityThrowable> {
    public void initProjectile(EntityLivingBase var1, boolean var2, String var3);

    public String getTrowingTeam();

    public T getProjectileEntity();
}

