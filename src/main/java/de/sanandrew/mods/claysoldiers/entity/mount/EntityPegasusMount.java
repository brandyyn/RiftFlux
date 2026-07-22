/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.sanandrew.core.manpack.util.UsedByReflection
 */
package de.sanandrew.mods.claysoldiers.entity.mount;

import de.sanandrew.core.manpack.util.annotation.UsedByReflection;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityHorseMount;
import de.sanandrew.mods.claysoldiers.util.mount.EnumHorseType;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityPegasusMount
extends EntityHorseMount {
    public float wingSwing = this.rand.nextFloat() * (float)Math.PI;
    public float wingSwingStep = 0.0f;
    public float prevWingSwing = 0.0f;

    @UsedByReflection
    public EntityPegasusMount(World world) {
        super(world);
    }

    public EntityPegasusMount(World world, EnumHorseType horseType) {
        super(world, horseType);
    }

    @Override
    public void onUpdate() {
        if (this.worldObj.isRemote) {
            this.calcWingSwing();
        }
        this.jumpMovementFactor = this.getAIMoveSpeed() * 0.21600002f;
        this.fallDistance = 0.0f;
        if (this.motionY < -0.1) {
            this.motionY = -0.1;
        }
        if (this.riddenByEntity instanceof EntityClayMan && this.moveForward != 0.0f) {
            EntityClayMan rider = (EntityClayMan)this.riddenByEntity;
            double dist = Double.MAX_VALUE;
            if (rider.getEntityToAttack() != null) {
                dist = this.getDistanceSqToEntity(rider.getEntityToAttack());
            } else if (rider.getTargetFollowing() != null) {
                dist = this.getDistanceSqToEntity(rider.getTargetFollowing());
            }
            if (dist > 2.25) {
                if (this.onGround) {
                    this.motionY = 0.4;
                    this.isAirBorne = true;
                } else {
                    AxisAlignedBB aabb;
                    int[] blockPos = new int[]{(int)this.posX, (int)this.posY - 1, (int)this.posZ};
                    Block blockBelow = this.worldObj.getBlock(blockPos[0], blockPos[1], blockPos[2]);
                    if (blockBelow != null && !blockBelow.isAir(this.worldObj, blockPos[0], blockPos[1], blockPos[2]) && ((aabb = blockBelow.getCollisionBoundingBoxFromPool(this.worldObj, blockPos[0], blockPos[1], blockPos[2])) == null || aabb.maxY > this.posY - 1.0)) {
                        this.motionY = 0.2;
                        this.isAirBorne = true;
                    }
                }
            }
        }
        super.onUpdate();
    }

    @Override
    protected void fall(float fallHeight) {
    }

    private void calcWingSwing() {
        this.prevWingSwing = this.wingSwing;
        this.wingSwingStep = this.wingSwing;
        this.wingSwing = this.onGround ? (float)((double)this.wingSwing + 0.19634954084936207) : (float)((double)this.wingSwing + 0.7853981633974483);
        if ((double)this.wingSwing > Math.PI * 2) {
            this.wingSwing = 0.0f;
            this.prevWingSwing = 0.0f;
        }
    }
}
