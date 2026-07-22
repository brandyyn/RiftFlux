/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.effect;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.EnumMethodState;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.ASoldierEffect;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffectInst;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffects;

public class EffectBlindingRedstone
extends ASoldierEffect {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        effectInst.getNbtTag().setShort("ticksRemain", (short)60);
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        short ticksRemain = (short)(effectInst.getNbtTag().getShort("ticksRemain") - 1);
        effectInst.getNbtTag().setShort("ticksRemain", ticksRemain);
        return ticksRemain == 0;
    }

    @Override
    public EnumMethodState onTargeting(EntityClayMan clayMan, SoldierEffectInst effectInst, EntityClayMan target) {
        return EnumMethodState.DENY;
    }

    @Override
    public void onClientUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        clayMan.worldObj.spawnParticle("reddust", clayMan.posX, clayMan.posY, clayMan.posZ, 1.0, 0.0, 0.0);
    }

    @Override
    public boolean isCompatibleWith(EntityClayMan clayMan, SoldierEffectInst effectInst, SoldierEffectInst checkEffect) {
        return checkEffect.getEffect() != SoldierEffects.getEffect("slimefeet");
    }
}

