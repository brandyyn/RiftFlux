package com.voidsrift.riftflux.duckling;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

public class EntityAISootAvoidPlayer extends EntityAIBase {
    private final EntitySootSprite sprite;
    private final float avoidDistance;
    private final double farSpeed;
    private final double nearSpeed;
    private final PathNavigate navigator;
    private EntityPlayer closestPlayer;
    private PathEntity path;
    private boolean running;
    private int checkCooldown;

    public EntityAISootAvoidPlayer(EntitySootSprite sprite, float avoidDistance, double farSpeed, double nearSpeed) {
        this.sprite = sprite;
        this.avoidDistance = avoidDistance;
        this.farSpeed = farSpeed;
        this.nearSpeed = nearSpeed;
        this.navigator = sprite.getNavigator();
        this.checkCooldown = sprite.getRNG().nextInt(8);
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.sprite.isTamed() || this.sprite.isHiding()) {
            return false;
        }
        if (this.checkCooldown > 0) {
            --this.checkCooldown;
            return false;
        }
        this.checkCooldown = 5 + this.sprite.getRNG().nextInt(8);
        this.closestPlayer = this.sprite.worldObj.getClosestPlayerToEntity(this.sprite, this.avoidDistance);
        if (!this.shouldAvoid(this.closestPlayer)) {
            return false;
        }

        Vec3 away = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.sprite, 16, 7,
                Vec3.createVectorHelper(this.closestPlayer.posX, this.closestPlayer.posY, this.closestPlayer.posZ));
        if (away == null) {
            return false;
        }
        if (this.closestPlayer.getDistanceSq(away.xCoord, away.yCoord, away.zCoord) < this.closestPlayer.getDistanceSqToEntity(this.sprite)) {
            return false;
        }

        this.path = this.navigator.getPathToXYZ(away.xCoord, away.yCoord, away.zCoord);
        return this.path != null && this.path.isDestinationSame(away);
    }

    @Override
    public boolean continueExecuting() {
        return !this.navigator.noPath() && this.shouldAvoid(this.closestPlayer);
    }

    @Override
    public void startExecuting() {
        this.running = true;
        this.sprite.setScared(true);
        this.navigator.setPath(this.path, this.farSpeed);
    }

    @Override
    public void resetTask() {
        this.running = false;
        this.closestPlayer = null;
        this.path = null;
        this.sprite.setScared(false);
    }

    @Override
    public void updateTask() {
        if (this.closestPlayer != null && this.sprite.getDistanceSqToEntity(this.closestPlayer) < 49.0D) {
            this.navigator.setSpeed(this.nearSpeed);
        } else {
            this.navigator.setSpeed(this.farSpeed);
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    private boolean shouldAvoid(EntityPlayer player) {
        if (player == null || !player.isEntityAlive() || player.capabilities.isCreativeMode || player.isSneaking()) {
            return false;
        }
        ItemStack held = player.inventory.getCurrentItem();
        return held == null || held.getItem() != DucklingContent.starCandy;
    }
}
