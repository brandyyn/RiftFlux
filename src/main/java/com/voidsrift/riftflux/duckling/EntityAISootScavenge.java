package com.voidsrift.riftflux.duckling;

import java.util.List;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

public class EntityAISootScavenge extends EntityAIBase {
    private final EntitySootSprite sprite;
    private final double speed;
    private EntityItem targetItem;
    private int graceTimer;

    public EntityAISootScavenge(EntitySootSprite sprite, double speed) {
        this.sprite = sprite;
        this.speed = speed;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.sprite.isHiding()) {
            return false;
        }
        this.targetItem = this.findTargetItem();
        this.graceTimer = 0;
        return this.targetItem != null;
    }

    @Override
    public boolean continueExecuting() {
        if (this.targetItem == null) {
            return this.sprite.getHeldItem() != null && this.graceTimer < 40;
        }
        return !this.sprite.getNavigator().noPath()
                && this.targetItem.isEntityAlive()
                && this.sprite.canAcceptItem(this.targetItem.getEntityItem());
    }

    @Override
    public void startExecuting() {
        this.sprite.getNavigator().clearPathEntity();
        this.sprite.mountEntity(null);
        if (this.targetItem != null) {
            this.sprite.getNavigator().tryMoveToXYZ(this.targetItem.posX, this.targetItem.posY, this.targetItem.posZ, this.speed);
        }
    }

    @Override
    public void resetTask() {
        this.targetItem = null;
        this.sprite.getNavigator().clearPathEntity();
        this.graceTimer = 0;
    }

    @Override
    public void updateTask() {
        if (this.targetItem == null || !this.targetItem.isEntityAlive()) {
            ++this.graceTimer;
            this.targetItem = this.findTargetItem();
            if (this.targetItem == null) {
                this.sprite.getNavigator().clearPathEntity();
                return;
            }
        }

        this.sprite.getNavigator().tryMoveToXYZ(this.targetItem.posX, this.targetItem.posY, this.targetItem.posZ, this.speed);
        this.graceTimer = 0;
        if (this.sprite.getDistanceSqToEntity(this.targetItem) < 2.0D) {
            this.sprite.performPickup(this.targetItem);
        }
    }

    private EntityItem findTargetItem() {
        List items = this.sprite.worldObj.getEntitiesWithinAABB(EntityItem.class, this.sprite.boundingBox.expand(24.0D, 8.0D, 24.0D));
        EntityItem best = null;
        double bestDistance = Double.MAX_VALUE;
        for (int i = 0; i < items.size(); i++) {
            EntityItem item = (EntityItem)items.get(i);
            ItemStack stack = item.getEntityItem();
            if (item.isDead || stack == null || !this.sprite.canAcceptItem(stack)) {
                continue;
            }
            double distance = this.sprite.getDistanceSqToEntity(item);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = item;
            }
        }
        return best;
    }
}
