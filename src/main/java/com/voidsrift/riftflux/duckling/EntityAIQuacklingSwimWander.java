package com.voidsrift.riftflux.duckling;

import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;

public class EntityAIQuacklingSwimWander extends EntityAIBase {
    private static final int START_CHANCE = 5000;
    private static final int SEARCH_RADIUS = 6;

    private final EntityQuackling quackling;
    private final double speed;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int swimTicks;
    private int repathTicks;
    private boolean previousAvoidsWater;

    public EntityAIQuacklingSwimWander(EntityQuackling quackling, double speed) {
        this.quackling = quackling;
        this.speed = speed;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.quackling.isFishing()
                || this.quackling.getCustomer() != null
                || !this.quackling.onGround
                || this.isInWater()
                || this.quackling.getRNG().nextInt(START_CHANCE) != 0) {
            return false;
        }
        return this.findWaterTarget();
    }

    @Override
    public boolean continueExecuting() {
        return this.swimTicks > 0
                && this.quackling.isEntityAlive()
                && !this.quackling.isFishing()
                && (this.isInWater() || this.distanceToTargetSq() > 2.0D);
    }

    @Override
    public void startExecuting() {
        this.previousAvoidsWater = this.quackling.getNavigator().getAvoidsWater();
        this.quackling.getNavigator().setAvoidsWater(false);
        this.swimTicks = 60 + this.quackling.getRNG().nextInt(81);
        this.repathTicks = 0;
        this.moveToTarget();
    }

    @Override
    public void resetTask() {
        this.quackling.getNavigator().setAvoidsWater(this.previousAvoidsWater);
        this.quackling.getNavigator().clearPathEntity();
        this.swimTicks = 0;
    }

    @Override
    public void updateTask() {
        --this.swimTicks;
        if (!this.isInWater() && this.distanceToTargetSq() <= 2.0D) {
            this.swimTicks = 0;
            return;
        }
        if (--this.repathTicks <= 0) {
            this.repathTicks = 30 + this.quackling.getRNG().nextInt(30);
            if (this.isInWater() && this.quackling.getRNG().nextInt(3) == 0) {
                this.findWaterTarget();
            }
            this.moveToTarget();
        }
    }

    private void moveToTarget() {
        this.quackling.getNavigator().tryMoveToXYZ((double)this.targetX + 0.5D, (double)this.targetY + 0.1D,
                (double)this.targetZ + 0.5D, this.speed);
    }

    private boolean findWaterTarget() {
        int baseX = MathHelper.floor_double(this.quackling.posX);
        int baseY = MathHelper.floor_double(this.quackling.boundingBox.minY);
        int baseZ = MathHelper.floor_double(this.quackling.posZ);

        for (int i = 0; i < 24; i++) {
            int x = baseX + this.quackling.getRNG().nextInt(SEARCH_RADIUS * 2 + 1) - SEARCH_RADIUS;
            int y = baseY + this.quackling.getRNG().nextInt(5) - 2;
            int z = baseZ + this.quackling.getRNG().nextInt(SEARCH_RADIUS * 2 + 1) - SEARCH_RADIUS;
            if (this.isSwimmableWater(x, y, z)) {
                this.targetX = x;
                this.targetY = y;
                this.targetZ = z;
                return true;
            }
        }
        return false;
    }

    private boolean isSwimmableWater(int x, int y, int z) {
        Material water = this.quackling.worldObj.getBlock(x, y, z).getMaterial();
        Material head = this.quackling.worldObj.getBlock(x, y + 1, z).getMaterial();
        return water == Material.water && !head.blocksMovement();
    }

    private boolean isInWater() {
        return this.quackling.worldObj.isMaterialInBB(this.quackling.boundingBox.expand(0.0D, -0.4D, 0.0D)
                .contract(0.001D, 0.001D, 0.001D), Material.water);
    }

    private double distanceToTargetSq() {
        double dx = this.quackling.posX - ((double)this.targetX + 0.5D);
        double dy = this.quackling.posY - (double)this.targetY;
        double dz = this.quackling.posZ - ((double)this.targetZ + 0.5D);
        return dx * dx + dy * dy + dz * dz;
    }
}
