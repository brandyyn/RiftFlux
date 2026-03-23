package com.voidsrift.riftflux.wam.entity;

import com.voidsrift.riftflux.ModConfig;
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
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFlowerMan extends EntityAnimal {
    private static final int COLOR_DATA_WATCHER = 16;

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
        dataWatcher.addObject(COLOR_DATA_WATCHER, Integer.valueOf(0));
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
        try {
            return dataWatcher != null ? Math.max(1, dataWatcher.getWatchableObjectInt(COLOR_DATA_WATCHER)) : 1;
        } catch (RuntimeException ignored) {
            return 1;
        }
    }

    public void setColor(int color) {
        if (dataWatcher == null) {
            return;
        }
        try {
            dataWatcher.updateObject(COLOR_DATA_WATCHER, Integer.valueOf(Math.max(1, Math.min(10, color))));
        } catch (RuntimeException ignored) {
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!worldObj.isRemote && ticksExisted % 20 == 0 && rand.nextInt(20) == 0) {
            spreadLife();
        }
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
        Block block = getLifeBlock();
        int metadata = getLifeMetadata();
        entityDropItem(new ItemStack(block, 1, metadata), 0.0F);
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

    private void spreadLife() {
        for (int attempt = 0; attempt < 12; attempt++) {
            int x = MathHelper.floor_double(posX) + rand.nextInt(5) - 2;
            int y = MathHelper.floor_double(posY) + rand.nextInt(3) - 1;
            int z = MathHelper.floor_double(posZ) + rand.nextInt(5) - 2;

            if (!worldObj.isAirBlock(x, y, z)) {
                continue;
            }

            Block ground = worldObj.getBlock(x, y - 1, z);
            if (ground != Blocks.grass && ground != Blocks.dirt) {
                continue;
            }

            Block plant = rand.nextInt(5) == 0 ? Blocks.tallgrass : getLifeBlock();
            int metadata = plant == Blocks.tallgrass ? 1 : getLifeMetadata();
            if (plant.canPlaceBlockAt(worldObj, x, y, z)) {
                worldObj.setBlock(x, y, z, plant, metadata, 3);
                return;
            }
        }
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
}
