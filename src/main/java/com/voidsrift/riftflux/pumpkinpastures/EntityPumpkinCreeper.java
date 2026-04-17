package com.voidsrift.riftflux.pumpkinpastures;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityPumpkinCreeper extends EntityCreeper {
    public EntityPumpkinCreeper(World world) {
        super(world);
        setSize(1.6F, 1.6F);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35D);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (worldObj.isRemote || ticksExisted % 20 != 0) {
            return;
        }
        if (worldObj.getClosestPlayerToEntity(this, 51.0D) != null) {
            return;
        }

        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(posY);
        int z = MathHelper.floor_double(posZ);
        net.minecraft.block.Block target = worldObj.getBlock(x, y, z);
        if (target == null || worldObj.isAirBlock(x, y, z) || target.isReplaceable(worldObj, x, y, z)) {
            worldObj.setBlock(x, y, z, PumpkinPasturesContent.suspiciousPumpkinBlock, 0, 3);
        }
        setDead();
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        super.dropFewItems(recentlyHit, looting);
        PumpkinPasturesContent.dropSoulItems(this, looting);
    }
}
