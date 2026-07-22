/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.effect;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.soldier.EnumMethodState;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.ASoldierEffect;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffectInst;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;

public class EffectMagmaBomb
extends ASoldierEffect {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        effectInst.getNbtTag().setByte("ticksRemain", (byte)40);
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        byte ticksRemain = effectInst.getNbtTag().getByte("ticksRemain");
        ticksRemain = (byte)(ticksRemain - 1);
        effectInst.getNbtTag().setByte("ticksRemain", ticksRemain);
        if (ticksRemain == 0) {
            Explosion explosion = clayMan.worldObj.createExplosion(clayMan, clayMan.posX, clayMan.posY, clayMan.posZ, 1.0f, false);
            clayMan.attackEntityFrom(DamageSource.setExplosionSource(explosion), 10000.0f);
        }
        return false;
    }

    @Override
    public EnumMethodState onTargeting(EntityClayMan clayMan, SoldierEffectInst effectInst, EntityClayMan target) {
        return EnumMethodState.DENY;
    }

    @Override
    public void onClientUpdate(EntityClayMan clayMan, SoldierEffectInst effectInst) {
        ParticlePacketSender.sendMagmafuseFx(clayMan.posX, clayMan.posY + 0.2, clayMan.posZ, clayMan.dimension);
    }

    @Override
    public void onSoldierDeath(EntityClayMan clayMan, SoldierEffectInst effectInst, DamageSource source) {
        if (!(source.getEntity() instanceof EntityPlayer)) {
            clayMan.worldObj.createExplosion(clayMan, clayMan.posX, clayMan.posY, clayMan.posZ, 1.0f, false);
        }
    }
}

