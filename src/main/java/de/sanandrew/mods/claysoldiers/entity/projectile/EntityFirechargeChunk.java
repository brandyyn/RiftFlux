/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.sanandrew.core.manpack.util.UsedByReflection
 */
package de.sanandrew.mods.claysoldiers.entity.projectile;

import de.sanandrew.core.manpack.util.annotation.UsedByReflection;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.projectile.EntityGravelChunk;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityFirechargeChunk
extends EntityGravelChunk {
    @UsedByReflection
    public EntityFirechargeChunk(World world) {
        super(world);
    }

    @UsedByReflection
    public EntityFirechargeChunk(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    @UsedByReflection
    public EntityFirechargeChunk(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void onImpact(MovingObjectPosition movObjPos) {
        if (!this.worldObj.isRemote) {
            if (movObjPos.entityHit != null) {
                float attackDmg = 1.0f + this.rand.nextFloat();
                boolean isEnemy = movObjPos.entityHit instanceof EntityClayMan && this.target instanceof EntityClayMan && ((EntityClayMan)movObjPos.entityHit).getClayTeam().equals(((EntityClayMan)this.target).getClayTeam());
                DamageSource dmgSrc = DamageSource.causeThrownDamage(this, this.getThrower());
                if (this.getThrower() == null) {
                    dmgSrc = DamageSource.causeThrownDamage(this, this);
                }
                if ((movObjPos.entityHit == this.target || isEnemy) && movObjPos.entityHit.attackEntityFrom(dmgSrc, attackDmg)) {
                    if (this.getThrower() instanceof EntityClayMan) {
                        ((EntityClayMan)this.getThrower()).onProjectileHit(this, movObjPos);
                    }
                    if (movObjPos.entityHit instanceof EntityClayMan) {
                        movObjPos.entityHit.setFire(3);
                        movObjPos.entityHit.playSound("random.fizz", 1.0f, 1.0f);
                    }
                } else {
                    return;
                }
            }
            if (movObjPos.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || this.getBlockCollisionBox(this.worldObj, movObjPos.blockX, movObjPos.blockY, movObjPos.blockZ) != null) {
                ParticlePacketSender.sendDiggingFx(this.posX, this.posY, this.posZ, this.dimension, Blocks.obsidian);
                this.setDead();
            }
            this.dataWatcher.updateObject(7, (byte)(this.isDead ? 1 : 0));
        }
    }
}
