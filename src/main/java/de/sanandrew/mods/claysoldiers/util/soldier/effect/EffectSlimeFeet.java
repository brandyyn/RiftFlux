/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.effect;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.ASoldierEffect;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffectInst;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffects;

public class EffectSlimeFeet
extends ASoldierEffect {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        effectInst.getNbtTag().setShort("ticksRemain", (short)60);
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        clayMan.canMove = false;
        short ticksRemain = (short)(effectInst.getNbtTag().getShort("ticksRemain") - 1);
        if (ticksRemain == 0) {
            return true;
        }
        effectInst.getNbtTag().setShort("ticksRemain", ticksRemain);
        return false;
    }

    @Override
    public void onClientUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        clayMan.canMove = false;
    }

    @Override
    public boolean isCompatibleWith(EntityClayMan clayMan, SoldierEffectInst effectInst, SoldierEffectInst checkEffect) {
        return checkEffect.getEffect() != SoldierEffects.getEffect("redstone");
    }
}

