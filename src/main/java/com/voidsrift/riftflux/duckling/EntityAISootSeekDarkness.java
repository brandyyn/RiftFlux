package com.voidsrift.riftflux.duckling;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class EntityAISootSeekDarkness extends EntityAIBase {
    private final EntitySootSprite sprite;
    private final double speed;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int searchCooldown;

    public EntityAISootSeekDarkness(EntitySootSprite sprite, double speed) {
        this.sprite = sprite;
        this.speed = speed;
        this.searchCooldown = sprite.getRNG().nextInt(100);
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.sprite.isTamed() || this.sprite.isHiding()) {
            return false;
        }
        if (this.searchCooldown > 0) {
            --this.searchCooldown;
            return false;
        }
        this.searchCooldown = 100 + this.sprite.getRNG().nextInt(101);
        int x = MathHelper.floor_double(this.sprite.posX);
        int y = MathHelper.floor_double(this.sprite.boundingBox.minY);
        int z = MathHelper.floor_double(this.sprite.posZ);
        if (this.sprite.worldObj.getBlockLightValue(x, y, z) <= 7) {
            return false;
        }

        Vec3 darkSpot = this.findDarkSpot(x, y, z);
        if (darkSpot == null) {
            return false;
        }
        this.targetX = darkSpot.xCoord;
        this.targetY = darkSpot.yCoord;
        this.targetZ = darkSpot.zCoord;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        int x = MathHelper.floor_double(this.sprite.posX);
        int y = MathHelper.floor_double(this.sprite.boundingBox.minY);
        int z = MathHelper.floor_double(this.sprite.posZ);
        return !this.sprite.isTamed()
                && !this.sprite.getNavigator().noPath()
                && this.sprite.worldObj.getBlockLightValue(x, y, z) > 7;
    }

    @Override
    public void startExecuting() {
        this.sprite.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.getPathSpeed());
    }

    private double getPathSpeed() {
        return this.sprite.getHeldItem() == null ? this.speed : Math.max(this.speed, 0.825D);
    }

    private Vec3 findDarkSpot(int baseX, int baseY, int baseZ) {
        for (int i = 0; i < 12; i++) {
            int x = baseX + this.sprite.getRNG().nextInt(20) - 10;
            int y = baseY + this.sprite.getRNG().nextInt(6) - 3;
            int z = baseZ + this.sprite.getRNG().nextInt(20) - 10;
            if (this.sprite.worldObj.getBlockLightValue(x, y, z) < 5 && this.sprite.worldObj.isAirBlock(x, y, z)) {
                return Vec3.createVectorHelper(x + 0.5D, y, z + 0.5D);
            }
        }
        return null;
    }
}
