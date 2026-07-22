/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.effect;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.EnumMethodState;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffectInst;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.mutable.MutableFloat;

public abstract class ASoldierEffect {
    public void onConstruct(EntityClayMan clayMan, SoldierEffectInst effectInst) {
    }

    public boolean onUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        return false;
    }

    public EnumMethodState onTargeting(EntityClayMan clayMan, SoldierEffectInst effectInst, EntityClayMan target) {
        return EnumMethodState.SKIP;
    }

    public void getAiMoveSpeed(EntityClayMan clayMan, SoldierEffectInst effectInst, MutableFloat speed) {
    }

    public void onClientUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
    }

    public boolean shouldNbtSyncToClient(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        return false;
    }

    public void onSoldierDeath(EntityClayMan clayMan, SoldierEffectInst effectInst, DamageSource source) {
    }

    public boolean isCompatibleWith(EntityClayMan clayMan, SoldierEffectInst effectInst, SoldierEffectInst checkEffect) {
        return true;
    }
}

