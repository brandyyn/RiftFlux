package com.voidsrift.riftflux.palaria.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class EntityAbstractRaptorChicken extends EntityMob {
    public boolean field_70885_d = false;
    public float field_70886_e = 0.0F;
    public float destPos = 0.0F;
    public float field_70884_g;
    public float field_70888_h;
    public float field_70889_i = 1.0F;

    protected EntityAbstractRaptorChicken(World world) {
        super(world);
        setSize(0.5F, 1.2F);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIAttackOnCollide(this, EntitySquid.class, 0.5D, true));
        tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityCowasaurus.class, 0.55D, true));
        tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.5D, false));
        tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 0.5D, false));
        tasks.addTask(5, new EntityAIAttackOnCollide(this, EntityAnimal.class, 0.5D, false));
        tasks.addTask(6, new EntityAIWander(this, 0.5D));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, true));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityAnimal.class, 0, true));
        experienceValue = 10;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        updateWingMotion();
    }

    protected void updateWingMotion() {
        field_70888_h = field_70886_e;
        field_70884_g = destPos;
        destPos = (float) ((double) destPos + (double) (onGround ? -1 : 4) * 0.3D);
        if (destPos < 0.0F) destPos = 0.0F;
        if (destPos > 1.0F) destPos = 1.0F;
        if (!onGround && field_70889_i < 1.0F) field_70889_i = 1.0F;
        field_70889_i = (float) ((double) field_70889_i * 0.9D);
        field_70886_e += field_70889_i * 2.0F;
    }

    @Override
    protected String getLivingSound() {
        return "riftflux:palaria_raptor_chicken_say";
    }

    @Override
    protected String getHurtSound() {
        return "riftflux:palaria_raptor_chicken_hurt";
    }

    @Override
    protected String getDeathSound() {
        return "riftflux:palaria_raptor_chicken_hurt";
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {
        playSound("mob.chicken.step", 0.15F, 1.0F);
    }
}
