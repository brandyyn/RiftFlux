package com.voidsrift.riftflux.duckling;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISootPressButton extends EntityAIBase {
    private final EntitySootSprite sprite;
    private final double speed;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int cooldown;
    private boolean hasTarget;

    public EntityAISootPressButton(EntitySootSprite sprite, double speed) {
        this.sprite = sprite;
        this.speed = speed;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.sprite.isHiding()) {
            return false;
        }
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.cooldown = 40;
        return this.findTarget();
    }

    @Override
    public boolean continueExecuting() {
        return this.hasTarget
                && this.isUnpressedButton(this.targetX, this.targetY, this.targetZ)
                && !this.sprite.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.sprite.getNavigator().tryMoveToXYZ(this.targetX + 0.5D, this.targetY, this.targetZ + 0.5D, this.speed);
    }

    @Override
    public void resetTask() {
        this.hasTarget = false;
        this.sprite.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        if (!this.hasTarget) {
            return;
        }
        this.sprite.getNavigator().tryMoveToXYZ(this.targetX + 0.5D, this.targetY, this.targetZ + 0.5D, this.speed);
        if (this.sprite.getDistanceSq(this.targetX + 0.5D, this.targetY + 0.5D, this.targetZ + 0.5D) <= 2.2D) {
            Block block = this.sprite.worldObj.getBlock(this.targetX, this.targetY, this.targetZ);
            if (block instanceof BlockButton && (this.sprite.worldObj.getBlockMetadata(this.targetX, this.targetY, this.targetZ) & 8) == 0) {
                block.onBlockActivated(this.sprite.worldObj, this.targetX, this.targetY, this.targetZ, null, 0, 0.5F, 0.5F, 0.5F);
                this.sprite.playSound("random.click", 0.3F, 0.6F);
            }
            this.hasTarget = false;
        }
    }

    private boolean findTarget() {
        int baseX = (int)Math.floor(this.sprite.posX);
        int baseY = (int)Math.floor(this.sprite.posY);
        int baseZ = (int)Math.floor(this.sprite.posZ);
        double bestDistance = Double.MAX_VALUE;
        boolean found = false;
        for (int x = baseX - 16; x <= baseX + 16; x++) {
            for (int y = baseY - 4; y <= baseY + 4; y++) {
                for (int z = baseZ - 16; z <= baseZ + 16; z++) {
                    if (!this.isUnpressedButton(x, y, z)) {
                        continue;
                    }
                    double distance = this.sprite.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        this.targetX = x;
                        this.targetY = y;
                        this.targetZ = z;
                        found = true;
                    }
                }
            }
        }
        this.hasTarget = found;
        return found;
    }

    private boolean isUnpressedButton(int x, int y, int z) {
        return this.sprite.worldObj.getBlock(x, y, z) instanceof BlockButton
                && (this.sprite.worldObj.getBlockMetadata(x, y, z) & 8) == 0;
    }
}
