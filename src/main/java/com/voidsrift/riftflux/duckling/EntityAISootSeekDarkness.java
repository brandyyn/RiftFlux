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

    public EntityAISootSeekDarkness(EntitySootSprite sprite, double speed) {
        this.sprite = sprite;
        this.speed = speed;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        int x = MathHelper.floor_double(this.sprite.posX);
        int y = MathHelper.floor_double(this.sprite.boundingBox.minY);
        int z = MathHelper.floor_double(this.sprite.posZ);
        if (this.sprite.worldObj.getBlockLightValue(x, y, z) == 0 || this.sprite.getRNG().nextInt(5) != 0) {
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
        return !this.sprite.getNavigator().noPath() && this.sprite.worldObj.getBlockLightValue(x, y, z) > 0;
    }

    @Override
    public void startExecuting() {
        this.sprite.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    private Vec3 findDarkSpot(int baseX, int baseY, int baseZ) {
        for (int i = 0; i < 40; i++) {
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
