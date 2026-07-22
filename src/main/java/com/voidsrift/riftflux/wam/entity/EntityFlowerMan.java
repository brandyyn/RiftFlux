package com.voidsrift.riftflux.wam.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityFlowerMan extends EntityAnimal implements IEntitySyncData {
    private int color = 1;

    public EntityFlowerMan(World world) {
        super(world);
        setSize(0.6F, 1.6F);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAIAvoidEntity(this, EntityMob.class, 8.0F, 0.8D, 1.2D));
        tasks.addTask(1, new EntityAIPanic(this, 1.2D));
        tasks.addTask(2, new EntityAIWander(this, 0.8D));
        tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Math.max(1.0D, ModConfig.flowerManMaxHealth));
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4D);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        IEntityLivingData out = super.onSpawnWithEgg(data);
        if (getColor() <= 0) {
            setColor(1 + rand.nextInt(10));
        }
        return out;
    }

    public int getColor() {
        return Math.max(1, this.color);
    }

    public void setColor(int color) {
        int clamped = Math.max(1, Math.min(10, color));
        if (this.color == clamped) {
            return;
        }
        this.color = clamped;
        EntitySyncHelper.sync(this);
    }

    @Override
    protected String getLivingSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return "mob.sheep.say";
    }

    @Override
    protected String getDeathSound() {
        return "mob.sheep.say";
    }

    @Override
    protected Item getDropItem() {
        return Items.wheat_seeds;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        WAMMobDrops.dropConfigured(this, ModConfig.wamFlowerManDropEntries);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("Color", getColor());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        int color = tag.getInteger("Color");
        setColor(color <= 0 ? 1 + rand.nextInt(10) : color);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable mate) {
        EntityFlowerMan child = new EntityFlowerMan(worldObj);
        child.setColor(rand.nextBoolean() ? getColor() : (mate instanceof EntityFlowerMan ? ((EntityFlowerMan) mate).getColor() : getColor()));
        return child;
    }

    public ItemStack createLifeDropStack() {
        return new ItemStack(getLifeBlock(), 1, getLifeMetadata());
    }

    private Block getLifeBlock() {
        return getColor() == 4 ? Blocks.yellow_flower : Blocks.red_flower;
    }

    private int getLifeMetadata() {
        switch (getColor()) {
            case 1:
                return 7;
            case 2:
                return 1;
            case 3:
                return 5;
            case 4:
                return 0;
            case 5:
                return 0;
            case 6:
                return 6;
            case 7:
            case 8:
                return 2;
            case 9:
                return 7;
            case 10:
                return 3;
            default:
                return 0;
        }
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setInteger("Color", this.color);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        this.color = Math.max(1, Math.min(10, tag.getInteger("Color")));
    }
}
