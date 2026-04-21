package com.voidsrift.riftflux.duckling;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityAIQuacklingTempt extends EntityAIBase {
    private final EntityQuackling quackling;
    private final double speed;
    private EntityPlayer temptingPlayer;
    private int delayTemptCounter;
    private boolean running;
    private boolean previousAvoidsWater;

    public EntityAIQuacklingTempt(EntityQuackling quackling, double speed) {
        this.quackling = quackling;
        this.speed = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        }

        this.temptingPlayer = this.quackling.worldObj.getClosestPlayerToEntity(this.quackling, 10.0D);
        return this.temptingPlayer != null && this.isTempting(this.temptingPlayer.getCurrentEquippedItem());
    }

    @Override
    public boolean continueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void startExecuting() {
        this.running = true;
        this.previousAvoidsWater = this.quackling.getNavigator().getAvoidsWater();
        this.quackling.getNavigator().setAvoidsWater(false);
    }

    @Override
    public void resetTask() {
        this.temptingPlayer = null;
        this.quackling.getNavigator().clearPathEntity();
        this.delayTemptCounter = 100;
        this.running = false;
        this.quackling.getNavigator().setAvoidsWater(this.previousAvoidsWater);
    }

    @Override
    public void updateTask() {
        this.quackling.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, 30.0F, (float)this.quackling.getVerticalFaceSpeed());
        if (this.quackling.getDistanceSqToEntity(this.temptingPlayer) < 6.25D) {
            this.quackling.getNavigator().clearPathEntity();
        } else {
            this.quackling.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.speed);
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    private boolean isTempting(ItemStack stack) {
        return DucklingItemMatcher.matchesQuacklingBreedItem(stack);
    }
}
