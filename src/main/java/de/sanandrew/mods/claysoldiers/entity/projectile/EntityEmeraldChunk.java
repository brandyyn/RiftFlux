/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.sanandrew.core.manpack.util.UsedByReflection
 */
package de.sanandrew.mods.claysoldiers.entity.projectile;

import de.sanandrew.core.manpack.util.annotation.UsedByReflection;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.projectile.EntityGravelChunk;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffectInst;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEmeraldChunk
extends EntityGravelChunk {
    public Triplet<Double, Double, Double> origin;

    @UsedByReflection
    public EntityEmeraldChunk(World world) {
        super(world);
    }

    @UsedByReflection
    public EntityEmeraldChunk(World world, EntityLivingBase thrower) {
        super(world, thrower);
        this.origin = Triplet.with(thrower.posX, thrower.posY + 0.5, thrower.posZ);
    }

    @UsedByReflection
    public EntityEmeraldChunk(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.origin = Triplet.with(x, y, z);
    }

    @Override
    public void setThrowableHeading(double motX, double motY, double motZ, float motMulti, float rndMulti) {
        super.setThrowableHeading(motX, motY, motZ, motMulti, rndMulti);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.target != null) {
            this.setLocationAndAngles(this.target.posX, this.target.posY, this.target.posZ, 0.0f, 0.0f);
            this.onImpact(new MovingObjectPosition(this.target));
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition movObjPos) {
        if (movObjPos.entityHit != null) {
            boolean isEnemy = movObjPos.entityHit instanceof EntityClayMan && this.target instanceof EntityClayMan && !((EntityClayMan)movObjPos.entityHit).getClayTeam().equals(((EntityClayMan)this.target).getClayTeam());
            DamageSource dmgSrc = DamageSource.causeThrownDamage(this, this.getThrower());
            if (this.getThrower() == null) {
                dmgSrc = DamageSource.causeThrownDamage(this, this);
            }
            dmgSrc.setDamageBypassesArmor();
            if (movObjPos.entityHit == this.target || isEnemy) {
                float attackDmg = 3.0f + this.rand.nextFloat();
                if (movObjPos.entityHit.isWet()) {
                    attackDmg *= 2.0f;
                }
                if (movObjPos.entityHit.attackEntityFrom(dmgSrc, attackDmg)) {
                    if (this.getThrower() instanceof EntityClayMan) {
                        ((EntityClayMan)this.getThrower()).onProjectileHit(this, movObjPos);
                    }
                    if (movObjPos.entityHit instanceof EntityClayMan) {
                        EntityClayMan iChun = (EntityClayMan)movObjPos.entityHit;
                        movObjPos.entityHit.playSound("ambient.weather.thunder", 1.0f, 8.0f);
                        SoldierEffectInst effect = iChun.addEffect(SoldierEffects.getEffect("thunder"));
                        if (effect != null && this.origin != null) {
                            effect.getNbtTag().setDouble("originX", this.origin.getValue0());
                            effect.getNbtTag().setDouble("originY", this.origin.getValue1());
                            effect.getNbtTag().setDouble("originZ", this.origin.getValue2());
                        }
                        iChun.updateUpgradeEffectRenders();
                    }
                }
            } else {
                return;
            }
        }
        if (!this.worldObj.isRemote) {
            if (movObjPos.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || this.getBlockCollisionBox(this.worldObj, movObjPos.blockX, movObjPos.blockY, movObjPos.blockZ) != null) {
                ParticlePacketSender.sendDiggingFx(this.posX, this.posY, this.posZ, this.dimension, Blocks.snow);
                this.setDead();
            }
            this.dataWatcher.updateObject(7, (byte)(this.isDead ? 1 : 0));
        }
    }
}
