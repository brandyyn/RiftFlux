package com.voidsrift.riftflux.duckling;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;

public class EntityAISootScavenge extends EntityAIBase {
    private static final double SEARCH_HORIZONTAL = 24.0D;
    private static final double SEARCH_VERTICAL = 8.0D;
    private static final double PICKUP_DISTANCE_SQ = 2.0D;
    private static final int MIN_SCAN_COOLDOWN = 3;
    private static final int RANDOM_SCAN_COOLDOWN = 5;
    private static final int RETARGET_COOLDOWN = 4;
    private static final int PATH_UPDATE_COOLDOWN = 5;

    private final EntitySootSprite sprite;
    private final double speed;
    private EntityItem targetItem;
    private int graceTimer;
    private int scanCooldown;
    private int retargetCooldown;
    private int pathUpdateCooldown;

    public EntityAISootScavenge(EntitySootSprite sprite, double speed) {
        this.sprite = sprite;
        this.speed = speed;
        this.scanCooldown = sprite.getRNG().nextInt(MIN_SCAN_COOLDOWN + RANDOM_SCAN_COOLDOWN);
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.sprite.isHiding() || !this.sprite.canCarryMoreItems()) {
            return false;
        }
        if (this.scanCooldown > 0) {
            --this.scanCooldown;
            return false;
        }
        this.targetItem = this.findTargetItem();
        this.graceTimer = 0;
        this.retargetCooldown = 0;
        this.pathUpdateCooldown = 0;
        this.scanCooldown = MIN_SCAN_COOLDOWN + this.sprite.getRNG().nextInt(RANDOM_SCAN_COOLDOWN + 1);
        return this.targetItem != null;
    }

    @Override
    public boolean continueExecuting() {
        if (this.targetItem == null) {
            return this.sprite.getHeldItem() != null && this.graceTimer < 40;
        }
        if (!this.targetItem.isEntityAlive() || !this.sprite.canAcceptItem(this.targetItem.getEntityItem())) {
            return false;
        }
        return !this.sprite.getNavigator().noPath()
                || this.sprite.getDistanceSqToEntity(this.targetItem) < PICKUP_DISTANCE_SQ * 2.0D;
    }

    @Override
    public void startExecuting() {
        this.sprite.getNavigator().clearPathEntity();
        this.sprite.mountEntity(null);
        this.updatePathToTarget();
    }

    @Override
    public void resetTask() {
        this.targetItem = null;
        this.sprite.getNavigator().clearPathEntity();
        this.graceTimer = 0;
        this.retargetCooldown = 0;
        this.pathUpdateCooldown = 0;
    }

    @Override
    public void updateTask() {
        if (this.targetItem == null
                || !this.targetItem.isEntityAlive()
                || !this.sprite.canAcceptItem(this.targetItem.getEntityItem())) {
            ++this.graceTimer;
            if (this.retargetCooldown > 0) {
                --this.retargetCooldown;
            } else {
                this.targetItem = this.findTargetItem();
                this.retargetCooldown = RETARGET_COOLDOWN;
            }
            if (this.targetItem == null) {
                this.sprite.getNavigator().clearPathEntity();
                return;
            }
        }

        if (this.pathUpdateCooldown > 0) {
            --this.pathUpdateCooldown;
        }
        if (this.pathUpdateCooldown <= 0 || this.sprite.getNavigator().noPath()) {
            this.updatePathToTarget();
        }
        this.graceTimer = 0;
        if (this.sprite.getDistanceSqToEntity(this.targetItem) < PICKUP_DISTANCE_SQ) {
            this.sprite.performPickup(this.targetItem);
        }
    }

    private EntityItem findTargetItem() {
        return SootSpriteTargetCache.findNearestItem(this.sprite, SEARCH_HORIZONTAL, SEARCH_VERTICAL);
    }

    private void updatePathToTarget() {
        if (this.targetItem != null) {
            this.sprite.getNavigator().tryMoveToXYZ(this.targetItem.posX, this.targetItem.posY, this.targetItem.posZ, this.speed);
            this.pathUpdateCooldown = PATH_UPDATE_COOLDOWN;
        }
    }
}
