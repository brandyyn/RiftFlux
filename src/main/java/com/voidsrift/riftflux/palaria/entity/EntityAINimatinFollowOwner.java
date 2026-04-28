package com.voidsrift.riftflux.palaria.entity;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAINimatinFollowOwner extends EntityAIBase {
    private final EntityNimatin nimatin;
    private final World world;
    private final double speed;
    private final float minDistance;
    private final float maxDistance;
    private EntityLivingBase owner;
    private int repathTicks;

    public EntityAINimatinFollowOwner(EntityNimatin nimatin, double speed, float minDistance, float maxDistance) {
        this.nimatin = nimatin;
        this.world = nimatin.worldObj;
        this.speed = speed;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase owner = nimatin.getOwner();
        if (owner == null || nimatin.isSitting()) {
            return false;
        }
        if (nimatin.getDistanceSqToEntity(owner) < (double) (minDistance * minDistance)) {
            return false;
        }
        this.owner = owner;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return owner != null
                && !nimatin.isSitting()
                && !nimatin.getNavigator().noPath()
                && nimatin.getDistanceSqToEntity(owner) > (double) (maxDistance * maxDistance);
    }

    @Override
    public void startExecuting() {
        repathTicks = 0;
    }

    @Override
    public void resetTask() {
        owner = null;
        nimatin.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        if (owner == null || nimatin.isSitting()) {
            this.resetTask();
            return;
        }

        nimatin.getLookHelper().setLookPositionWithEntity(owner, 10.0F, nimatin.getVerticalFaceSpeed());
        if (--repathTicks > 0) {
            return;
        }
        repathTicks = 10;

        double teleportDistance = Math.max(0.0D, (double) ModConfig.palariaNimatinTeleportDistance);
        double distanceSq = nimatin.getDistanceSqToEntity(owner);
        if (teleportDistance > 0.0D
                && distanceSq >= teleportDistance * teleportDistance
                && !nimatin.getLeashed()
                && nimatin.riddenByEntity == null
                && teleportNearOwner()) {
            nimatin.getNavigator().clearPathEntity();
            return;
        }

        nimatin.getNavigator().tryMoveToEntityLiving(owner, speed);
    }

    private boolean teleportNearOwner() {
        if (nimatin.isSitting()) {
            return false;
        }
        int ownerX = MathHelper.floor_double(owner.posX) - 2;
        int ownerY = MathHelper.floor_double(owner.boundingBox.minY);
        int ownerZ = MathHelper.floor_double(owner.posZ) - 2;

        for (int dx = 0; dx <= 4; dx++) {
            for (int dz = 0; dz <= 4; dz++) {
                if ((dx < 1 || dz < 1 || dx > 3 || dz > 3)
                        && canTeleportTo(ownerX + dx, ownerY, ownerZ + dz)) {
                    nimatin.setLocationAndAngles(ownerX + dx + 0.5D, ownerY, ownerZ + dz + 0.5D, nimatin.rotationYaw, nimatin.rotationPitch);
                    nimatin.fallDistance = 0.0F;
                    nimatin.motionX = 0.0D;
                    nimatin.motionY = 0.0D;
                    nimatin.motionZ = 0.0D;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canTeleportTo(int x, int y, int z) {
        if (!world.blockExists(x, y, z)) {
            return false;
        }

        Block floor = world.getBlock(x, y - 1, z);
        if (floor == null || floor.getMaterial() == Material.air || !floor.getMaterial().blocksMovement()) {
            return false;
        }

        return world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z);
    }
}
