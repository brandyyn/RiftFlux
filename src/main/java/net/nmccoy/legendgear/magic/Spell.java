/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.monster.EntitySnowman
 */
package net.nmccoy.legendgear.magic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySnowman;

public abstract class Spell {
    public EntityLiving owner;

    public abstract boolean affectLiving(EntityLivingBase var1);

    public abstract boolean affectInanimate(Entity var1);

    public abstract boolean affectBlock();

    public static enum Element {
        Harmless,
        Star,
        Fire,
        Ice,
        Lightning,
        Radiant,
        Void,
        Explosion,
        Wind,
        Teleport;


        public float getDamageMultiplier(EntityLivingBase target) {
            if (this == Fire && target.isImmuneToFire()) {
                return 0.0f;
            }
            if (this == Lightning && target.isWet()) {
                return 2.0f;
            }
            if (this == Ice && target instanceof EntitySnowman) {
                return 0.0f;
            }
            if (this == Ice && target.isImmuneToFire()) {
                return 2.0f;
            }
            if (this == Wind && target.isAirBorne) {
                return 2.0f;
            }
            if (this == Radiant && target.isEntityUndead()) {
                return 2.0f;
            }
            return 1.0f;
        }
    }
}

