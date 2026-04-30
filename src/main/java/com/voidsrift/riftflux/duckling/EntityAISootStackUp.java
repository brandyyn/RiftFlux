package com.voidsrift.riftflux.duckling;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISootStackUp extends EntityAIBase {
    private final EntitySootSprite sprite;
    private Entity mountTarget;
    private int checkCooldown;

    public EntityAISootStackUp(EntitySootSprite sprite) {
        this.sprite = sprite;
        this.checkCooldown = sprite.getRNG().nextInt(20);
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.checkCooldown > 0) {
            --this.checkCooldown;
            return false;
        }
        this.checkCooldown = 20 + this.sprite.getRNG().nextInt(21);
        if (!this.sprite.canStartStacking()
                || this.sprite.isRiding()
                || this.sprite.riddenByEntity != null
                || this.sprite.motionX * this.sprite.motionX + this.sprite.motionZ * this.sprite.motionZ > 0.01D) {
            return false;
        }
        this.mountTarget = this.findMountTarget();
        return this.mountTarget != null;
    }

    @Override
    public void startExecuting() {
        if (this.mountTarget != null) {
            this.sprite.mountEntity(this.mountTarget);
            this.sprite.beginStackRide();
        }
        this.mountTarget = null;
    }

    private Entity findMountTarget() {
        List neighbors = this.sprite.worldObj.getEntitiesWithinAABB(EntitySootSprite.class, this.sprite.boundingBox.expand(0.5D, 0.0D, 0.5D));
        for (int i = 0; i < neighbors.size(); i++) {
            EntitySootSprite neighbor = (EntitySootSprite)neighbors.get(i);
            if (neighbor == this.sprite || neighbor.isRiding() || neighbor.isTamed()) {
                continue;
            }
            Entity top = getTopOfStack(neighbor);
            if (top instanceof EntitySootSprite
                    && !((EntitySootSprite) top).isTamed()
                    && !stackContainsHeldItems(neighbor)
                    && getStackHeight(neighbor) < 3) {
                return top;
            }
        }
        return null;
    }

    private static Entity getTopOfStack(Entity entity) {
        Entity current = entity;
        while (current.riddenByEntity instanceof EntitySootSprite) {
            current = current.riddenByEntity;
        }
        return current;
    }

    private static int getStackHeight(Entity entity) {
        int height = 1;
        Entity current = entity;
        while (current.riddenByEntity instanceof EntitySootSprite) {
            current = current.riddenByEntity;
            ++height;
        }
        return height;
    }

    private static boolean stackContainsHeldItems(Entity entity) {
        Entity current = entity;
        while (current instanceof EntitySootSprite) {
            EntitySootSprite sprite = (EntitySootSprite) current;
            if (sprite.getHeldItem() != null) {
                return true;
            }
            current = sprite.riddenByEntity;
        }
        return false;
    }
}
