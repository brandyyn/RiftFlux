package com.voidsrift.riftflux.palaria.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAICreeptileSwell extends EntityAIBase {
    private final EntityCreeptile creeptile;
    private EntityLivingBase target;

    public EntityAICreeptileSwell(EntityCreeptile creeptile) {
        this.creeptile = creeptile;
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase attackTarget = creeptile.getAttackTarget();
        return creeptile.getCreeptileState() > 0
                || attackTarget != null && creeptile.getDistanceSqToEntity(attackTarget) < 9.0D;
    }

    @Override
    public void startExecuting() {
        creeptile.getNavigator().clearPathEntity();
        target = creeptile.getAttackTarget();
    }

    @Override
    public void resetTask() {
        target = null;
    }

    @Override
    public void updateTask() {
        if (target == null
                || creeptile.getDistanceSqToEntity(target) > 49.0D
                || !creeptile.getEntitySenses().canSee(target)) {
            creeptile.setCreeptileState(-1);
        } else {
            creeptile.setCreeptileState(1);
        }
    }
}
