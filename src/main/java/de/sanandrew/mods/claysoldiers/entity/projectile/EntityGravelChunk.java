/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.entity.projectile;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.projectile.ISoldierProjectile;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.soldier.ClaymanTeam;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityGravelChunk
extends EntityThrowable
implements ISoldierProjectile<EntityGravelChunk> {
    protected static final int DW_CLAYMANTEAM = 5;
    protected static final int DW_HOMING = 6;
    protected static final int DW_DEAD = 7;
    protected EntityLivingBase target;

    public EntityGravelChunk(World world) {
        super(world);
        this.setSize(0.1f, 0.1f);
        this.renderDistanceWeight = 5.0;
    }

    public EntityGravelChunk(World world, EntityLivingBase thrower) {
        super(world, thrower);
        this.setSize(0.1f, 0.1f);
        this.renderDistanceWeight = 5.0;
    }

    public EntityGravelChunk(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.setSize(0.1f, 0.1f);
        this.renderDistanceWeight = 5.0;
    }

    @Override
    public void initProjectile(EntityLivingBase target, boolean homing, String clayTeam) {
        this.target = target;
        this.dataWatcher.updateObject(6, (byte)(homing ? 1 : 0));
        this.dataWatcher.updateObject(5, clayTeam);
    }

    @Override
    public String getTrowingTeam() {
        return this.dataWatcher.getWatchableObjectString(5);
    }

    @Override
    public EntityGravelChunk getProjectileEntity() {
        return this;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(5, ClaymanTeam.NULL_TEAM.getTeamName());
        this.dataWatcher.addObject(6, (byte)0);
        this.dataWatcher.addObject(7, (byte)0);
    }

    @Override
    public void setThrowableHeading(double motX, double motY, double motZ, float motMulti, float rndMulti) {
        super.setThrowableHeading(motX, motY, motZ, motMulti, rndMulti);
        float motionNorm = MathHelper.sqrt_double(motX * motX + motY * motY + motZ * motZ);
        motX /= (double)motionNorm;
        motY /= (double)motionNorm;
        motZ /= (double)motionNorm;
        if (!this.isHoming()) {
            motX += this.rand.nextGaussian() * 0.0075 * (double)rndMulti;
            motY += this.rand.nextGaussian() * 0.0075 * (double)rndMulti;
            motZ += this.rand.nextGaussian() * 0.0075 * (double)rndMulti;
        }
        this.motionX = motX *= (double)motMulti;
        this.motionY = motY *= (double)motMulti;
        this.motionZ = motZ *= (double)motMulti;
        float f3 = MathHelper.sqrt_double(motX * motX + motZ * motZ);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(motX, motZ) * 180.0 / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(motY, f3) * 180.0 / Math.PI);
    }

    @Override
    public void onUpdate() {
        if (this.dataWatcher.getWatchableObjectByte(7) == 1) {
            this.setDead();
            return;
        }
        if (this.isHoming() && !this.worldObj.isRemote) {
            double d = this.target.posX - this.posX;
            double d1 = this.target.posZ - this.posZ;
            double d2 = this.target.posY + (double)this.target.getEyeHeight() - 0.10000000298023223 - this.posY;
            float f1 = MathHelper.sqrt_double(d * d + d1 * d1) * 0.2f;
            this.setThrowableHeading(d, d2 + (double)f1, d1, 0.6f, 12.0f);
        }
        super.onUpdate();
    }

    @Override
    protected void onImpact(MovingObjectPosition movObjPos) {
        if (movObjPos.entityHit != null) {
            float attackDmg = 2.0f + this.rand.nextFloat() * 2.0f;
            boolean isEnemy = movObjPos.entityHit instanceof EntityClayMan && this.target instanceof EntityClayMan && ((EntityClayMan)movObjPos.entityHit).getClayTeam().equals(((EntityClayMan)this.target).getClayTeam());
            DamageSource dmgSrc = DamageSource.causeThrownDamage(this, this.getThrower());
            if (this.getThrower() == null) {
                dmgSrc = DamageSource.causeThrownDamage(this, this);
            }
            if ((movObjPos.entityHit == this.target || isEnemy) && movObjPos.entityHit.attackEntityFrom(dmgSrc, attackDmg)) {
                if (this.getThrower() instanceof EntityClayMan) {
                    ((EntityClayMan)this.getThrower()).onProjectileHit(this, movObjPos);
                }
            } else {
                return;
            }
        }
        if (!this.worldObj.isRemote) {
            if (movObjPos.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || this.getBlockCollisionBox(this.worldObj, movObjPos.blockX, movObjPos.blockY, movObjPos.blockZ) != null) {
                ParticlePacketSender.sendDiggingFx(this.posX, this.posY, this.posZ, this.dimension, Blocks.gravel);
                this.setDead();
            }
            this.dataWatcher.updateObject(7, (byte)(this.isDead ? 1 : 0));
        }
    }

    protected AxisAlignedBB getBlockCollisionBox(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) != null ? world.getBlock(x, y, z).getCollisionBoundingBoxFromPool(world, x, y, z) : null;
    }

    private boolean isHoming() {
        return this.dataWatcher.getWatchableObjectByte(6) == 1 && this.target != null && !this.target.isDead && this.target.getHealth() > 0.0f;
    }
}

