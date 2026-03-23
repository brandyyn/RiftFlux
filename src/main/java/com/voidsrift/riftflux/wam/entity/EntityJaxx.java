package com.voidsrift.riftflux.wam.entity;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

public class EntityJaxx extends EntityMob {
    private static final String SOUND_JAXX = "riftflux:cyclops_growl";
    private static final String SOUND_JAXX_HURT = "riftflux:cyclops_roar";
    private static final String SOUND_JAXX_DEATH = "riftflux:cyclops_roar";
    private static final String SOUND_JAXX_STEP = "riftflux:wam_heavy_walk";
    private int attackTimer;

    public EntityJaxx(World world) {
        super(world);
        setSize(1.4F, 4.25F);
        stepHeight = 1.0F;
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, true));
        tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
        tasks.addTask(3, new EntityAIWander(this, 0.6D));
        tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(5, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Math.max(1.0D, ModConfig.jaxxMaxHealth));
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(40.0D);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        attackTimer = 10;
        worldObj.setEntityState(this, (byte) 4);
        boolean attacked = super.attackEntityAsMob(target);
        if (attacked) {
            target.motionY += 0.45D - target.height * 0.015D;
        }
        return attacked;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (attackTimer > 0) {
            attackTimer--;
        }
        if (!worldObj.isRemote && attackTimer == 0 && ticksExisted % 10 == 0) {
            stompNearbyTallTargets();
        }
    }

    @Override
    protected String getLivingSound() {
        return SOUND_JAXX;
    }

    @Override
    protected String getHurtSound() {
        return SOUND_JAXX_HURT;
    }

    @Override
    protected String getDeathSound() {
        return SOUND_JAXX_DEATH;
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {
        worldObj.playSoundAtEntity(this, SOUND_JAXX_STEP, 1.0F, 1.0F);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        if (rand.nextBoolean()) {
            entityDropItem(new ItemStack(Blocks.pumpkin), 0.0F);
        } else {
            dropItem(Items.blaze_powder, 1);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("AttackTimer", attackTimer);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        attackTimer = tag.getInteger("AttackTimer");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte state) {
        if (state == 4) {
            attackTimer = 10;
        } else {
            super.handleHealthUpdate(state);
        }
    }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer() {
        return attackTimer;
    }

    private void stompNearbyTallTargets() {
        List<EntityLivingBase> nearby = worldObj.getEntitiesWithinAABB(
                EntityLivingBase.class,
                boundingBox.expand(2.0D, 2.0D, 2.0D)
        );

        for (EntityLivingBase target : nearby) {
            if (target == null || target == this || !target.isEntityAlive() || target.height <= 3.0F || target instanceof EntityJaxx) {
                continue;
            }
            if (!canEntityBeSeen(target)) {
                continue;
            }

            attackEntityAsMob(target);
            break;
        }
    }
}
