package com.voidsrift.riftflux.wam.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

public class EntityCyclops extends EntityMob implements IEntitySyncData {
    private static final double SCAVENGE_RANGE = 10.0D;
    private static final String SOUND_CYCLOPS_IDLE = "riftflux:ender_troll_idle";
    private static final String SOUND_CYCLOPS_HURT = "riftflux:ender_troll_hurt";
    private static final String SOUND_CYCLOPS_DEATH = "riftflux:ender_troll_death";
    private static final String SOUND_HEAVY_WALK = "riftflux:wam_heavy_walk";
    private int attackTimer;
    private boolean hasEye = true;

    public EntityCyclops(World world) {
        super(world);
        setSize(1.2F, 2.9F);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIRestrictSun(this));
        tasks.addTask(2, new EntityAIFleeSun(this, 1.0D));
        tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, true));
        tasks.addTask(4, new EntityAIWander(this, 0.6D));
        tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(6, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(getConfiguredMaxHealth(true));
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(12.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return worldObj != null && !worldObj.isDaytime() && super.getCanSpawnHere();
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        attackTimer = 10;
        worldObj.setEntityState(this, (byte) 4);
        boolean attacked = super.attackEntityAsMob(target);
        if (attacked) {
            target.motionY += 0.4D - target.height * 0.015D;
        }
        return attacked;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (attackTimer > 0) {
            attackTimer--;
        }
        if (worldObj.isRemote) {
            return;
        }

        syncEyeState();

        if (hasEye() && rand.nextInt(10000) == 0) {
            setHasEye(false);
            playSound(SOUND_CYCLOPS_HURT, 0.3F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
            entityDropItem(new ItemStack(Items.spider_eye), 0.0F);
        }

        if (ticksExisted % 20 == 0) {
            scavengeNearbyItems();
        }
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int drops = 1 + rand.nextInt(2 + looting);
        for (int i = 0; i < drops; i++) {
            dropItem(Items.beef, 1);
        }
    }

    @Override
    protected String getLivingSound() {
        return SOUND_CYCLOPS_IDLE;
    }

    @Override
    protected String getHurtSound() {
        return SOUND_CYCLOPS_HURT;
    }

    @Override
    protected String getDeathSound() {
        return SOUND_CYCLOPS_DEATH;
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {
        worldObj.playSoundAtEntity(this, SOUND_HEAVY_WALK, 1.0F, 1.0F);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("AttackTimer", attackTimer);
        tag.setBoolean("HasEye", hasEye());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        attackTimer = tag.getInteger("AttackTimer");
        if (tag.hasKey("HasEye")) {
            setHasEye(tag.getBoolean("HasEye"));
        }
        syncEyeState();
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

    public boolean hasEye() {
        return this.hasEye;
    }

    public void setHasEye(boolean hasEye) {
        if (this.hasEye == hasEye) {
            return;
        }
        this.hasEye = hasEye;
        EntitySyncHelper.sync(this);
        this.syncEyeState();
    }

    @Override
    public int getTotalArmorValue() {
        return Math.min(20, super.getTotalArmorValue() + 5);
    }

    private void syncEyeState() {
        double maxHealth = getConfiguredMaxHealth(hasEye());
        double attackDamage = hasEye() ? 12.0D : 6.0D;

        if (getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() != maxHealth) {
            getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth);
            if (getHealth() > maxHealth) {
                setHealth((float) maxHealth);
            }
        }

        if (getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() != attackDamage) {
            getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(attackDamage);
        }
    }

    private void scavengeNearbyItems() {
        EntityItem targetItem = getClosestScavengableItem(SCAVENGE_RANGE);
        if (targetItem == null) {
            return;
        }

        if (getDistanceSqToEntity(targetItem) < 4.0D) {
            ItemStack stack = targetItem.getEntityItem();
            if (stack == null) {
                return;
            }

            Item item = stack.getItem();
            stack.stackSize--;
            if (stack.stackSize <= 0) {
                targetItem.setDead();
            }

            if (item == Items.spider_eye) {
                setHasEye(true);
            } else {
                heal(2.0F);
            }
            playSound(SOUND_CYCLOPS_IDLE, 0.3F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
            syncEyeState();
            return;
        }

        getNavigator().tryMoveToXYZ(targetItem.posX, targetItem.posY, targetItem.posZ, 0.9D);
    }

    private EntityItem getClosestScavengableItem(double range) {
        List<EntityItem> items = worldObj.getEntitiesWithinAABB(
                EntityItem.class,
                boundingBox.expand(range, range, range)
        );
        EntityItem closest = null;
        double bestDistance = Double.MAX_VALUE;

        for (EntityItem candidate : items) {
            if (candidate == null || !candidate.isEntityAlive()) {
                continue;
            }

            ItemStack stack = candidate.getEntityItem();
            if (stack == null || !isScavengable(stack.getItem())) {
                continue;
            }

            double distance = getDistanceSqToEntity(candidate);
            if (distance < bestDistance) {
                bestDistance = distance;
                closest = candidate;
            }
        }

        return closest;
    }

    private boolean isScavengable(Item item) {
        if (item == null) {
            return false;
        }
        return item == Items.beef || item == Items.cooked_beef || (!hasEye() && item == Items.spider_eye);
    }

    private static double getConfiguredMaxHealth(boolean hasEye) {
        double configured = Math.max(1.0D, ModConfig.cyclopsMaxHealth);
        return hasEye ? configured : Math.max(1.0D, configured * (2.0D / 3.0D));
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setBoolean("HasEye", this.hasEye);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        this.hasEye = tag.getBoolean("HasEye");
        this.syncEyeState();
    }
}
