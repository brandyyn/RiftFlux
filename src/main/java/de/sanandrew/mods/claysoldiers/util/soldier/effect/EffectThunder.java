/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.effect;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.ASoldierEffect;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffectInst;

public class EffectThunder
extends ASoldierEffect {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        effectInst.getNbtTag().setShort("ticksRemaining", (short)20);
        effectInst.getNbtTag().setLong("randomLightning", SAPUtils.RNG.nextLong());
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        short remaining = (short)(effectInst.getNbtTag().getShort("ticksRemaining") - 1);
        effectInst.getNbtTag().setShort("ticksRemaining", remaining);
        return remaining == 0;
    }

    @Override
    public void onClientUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        short remaining = (short)(effectInst.getNbtTag().getShort("ticksRemaining") - 1);
        effectInst.getNbtTag().setShort("ticksRemaining", remaining);
    }

    @Override
    public boolean shouldNbtSyncToClient(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        if (effectInst.getNbtTag().getBoolean("alreadySent")) {
            return false;
        }
        effectInst.getNbtTag().setBoolean("alreadySent", true);
        return true;
    }
}

