/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade;

import de.sanandrew.mods.claysoldiers.entity.projectile.ISoldierProjectile;
import de.sanandrew.mods.claysoldiers.tileentity.TileEntityClayNexus;
import net.minecraft.entity.projectile.EntityThrowable;

public interface IThrowableUpgrade {
    public Class<? extends ISoldierProjectile<? extends EntityThrowable>> getThrowableClass();

    public void renderNexusThrowable(TileEntityClayNexus var1, float var2);
}

