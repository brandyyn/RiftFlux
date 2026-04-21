package com.voidsrift.riftflux.pumpkinpastures;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIPumpkinCreeperSwell extends EntityAIBase {
    private final EntityPumpkinCreeper creeper;
    private EntityLivingBase target;

    public EntityAIPumpkinCreeperSwell(EntityPumpkinCreeper creeper) {
        this.creeper = creeper;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase attackTarget = this.creeper.getAttackTarget();
        return this.creeper.getCreeperState() > 0
                || attackTarget != null && this.creeper.getDistanceSqToEntity(attackTarget) < 9.0D;
    }

    @Override
    public void startExecuting() {
        this.creeper.getNavigator().clearPathEntity();
        this.target = this.creeper.getAttackTarget();
    }

    @Override
    public void resetTask() {
        this.target = null;
    }

    @Override
    public void updateTask() {
        if (this.target == null) {
            this.creeper.setCreeperState(-1);
            return;
        }
        if (this.creeper.getDistanceSqToEntity(this.target) > 49.0D) {
            this.creeper.setCreeperState(-1);
            return;
        }
        if (!this.creeper.getEntitySenses().canSee(this.target)) {
            this.creeper.setCreeperState(-1);
            return;
        }
        this.creeper.setCreeperState(1);
    }

}
