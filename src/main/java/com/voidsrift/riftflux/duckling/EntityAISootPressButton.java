package com.voidsrift.riftflux.duckling;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISootPressButton extends EntityAIBase {
    private static final int SEARCH_HORIZONTAL = 16;
    private static final int SEARCH_VERTICAL = 4;
    private static final int NEARBY_RADIUS = 2;
    private static final int RANDOM_SAMPLES = 32;
    private static final int MIN_COOLDOWN = 100;
    private static final int RANDOM_COOLDOWN = 100;
    private static final int PATH_UPDATE_COOLDOWN = 20;
    private static final double TARGET_DISTANCE_SQ = 2.2D;

    private final EntitySootSprite sprite;
    private final double speed;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int cooldown;
    private int pathUpdateCooldown;
    private boolean hasTarget;

    public EntityAISootPressButton(EntitySootSprite sprite, double speed) {
        this.sprite = sprite;
        this.speed = speed;
        this.cooldown = sprite.getRNG().nextInt(MIN_COOLDOWN + RANDOM_COOLDOWN);
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
        this.cooldown = MIN_COOLDOWN + this.sprite.getRNG().nextInt(RANDOM_COOLDOWN + 1);
        return this.findTarget();
    }

    @Override
    public boolean continueExecuting() {
        return this.hasTarget
                && this.isUnpressedButton(this.targetX, this.targetY, this.targetZ)
                && (!this.sprite.getNavigator().noPath() || this.isNearTarget());
    }

    @Override
    public void startExecuting() {
        this.pathUpdateCooldown = 0;
        this.updatePathToTarget();
    }

    @Override
    public void resetTask() {
        this.hasTarget = false;
        this.sprite.getNavigator().clearPathEntity();
        this.pathUpdateCooldown = 0;
    }

    @Override
    public void updateTask() {
        if (!this.hasTarget) {
            return;
        }
        if (this.pathUpdateCooldown > 0) {
            --this.pathUpdateCooldown;
        }
        if (this.pathUpdateCooldown <= 0 && !this.isNearTarget()) {
            this.updatePathToTarget();
        }
        if (this.isNearTarget()) {
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
        if (this.findNearbyTarget(baseX, baseY, baseZ)) {
            return true;
        }
        for (int i = 0; i < RANDOM_SAMPLES; i++) {
            int x = baseX + this.sprite.getRNG().nextInt(SEARCH_HORIZONTAL * 2 + 1) - SEARCH_HORIZONTAL;
            int y = baseY + this.sprite.getRNG().nextInt(SEARCH_VERTICAL * 2 + 1) - SEARCH_VERTICAL;
            int z = baseZ + this.sprite.getRNG().nextInt(SEARCH_HORIZONTAL * 2 + 1) - SEARCH_HORIZONTAL;
            if (this.isUnpressedButton(x, y, z)) {
                this.targetX = x;
                this.targetY = y;
                this.targetZ = z;
                this.hasTarget = true;
                return true;
            }
        }
        this.hasTarget = false;
        return false;
    }

    private boolean findNearbyTarget(int baseX, int baseY, int baseZ) {
        double bestDistance = Double.MAX_VALUE;
        boolean found = false;
        for (int x = baseX - NEARBY_RADIUS; x <= baseX + NEARBY_RADIUS; x++) {
            for (int y = baseY - 1; y <= baseY + 2; y++) {
                for (int z = baseZ - NEARBY_RADIUS; z <= baseZ + NEARBY_RADIUS; z++) {
                    if (this.isUnpressedButton(x, y, z)) {
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
        }
        this.hasTarget = found;
        return found;
    }

    private boolean isUnpressedButton(int x, int y, int z) {
        return this.sprite.worldObj.getBlock(x, y, z) instanceof BlockButton
                && (this.sprite.worldObj.getBlockMetadata(x, y, z) & 8) == 0;
    }

    private boolean isNearTarget() {
        return this.sprite.getDistanceSq(this.targetX + 0.5D, this.targetY + 0.5D, this.targetZ + 0.5D) <= TARGET_DISTANCE_SQ;
    }

    private void updatePathToTarget() {
        this.sprite.getNavigator().tryMoveToXYZ(this.targetX + 0.5D, this.targetY, this.targetZ + 0.5D, this.speed);
        this.pathUpdateCooldown = PATH_UPDATE_COOLDOWN;
    }
}
