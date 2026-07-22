/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.effect;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.ASoldierEffect;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffectInst;
import org.apache.commons.lang3.mutable.MutableFloat;

public class EffectSlowMotion
extends ASoldierEffect {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        effectInst.getNbtTag().setShort("ticksRemaining", (short)60);
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        if (clayMan.ticksExisted % 10 == 0) {
            ParticlePacketSender.sendSpellFx(clayMan.posX, clayMan.posY, clayMan.posZ, clayMan.dimension, 1.0, 1.0, 1.0);
        }
        short remaining = (short)(effectInst.getNbtTag().getShort("ticksRemaining") - 1);
        effectInst.getNbtTag().setShort("ticksRemaining", remaining);
        return remaining == 0;
    }

    @Override
    public void getAiMoveSpeed(EntityClayMan clayMan, SoldierEffectInst effectInst, MutableFloat speed) {
        speed.setValue(speed.getValue().floatValue() / 2.0f);
    }
}

