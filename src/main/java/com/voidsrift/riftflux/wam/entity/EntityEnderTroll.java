package com.voidsrift.riftflux.wam.entity;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityEnderTroll extends EntityMob {
    private static final String SOUND_ENDER_TROLL = "riftflux:jaxx_idle";
    private static final String SOUND_ENDER_TROLL_HURT = "riftflux:jaxx_hurt";
    private static final String SOUND_ENDER_TROLL_DEATH = "riftflux:jaxx_death";
    private static final String SOUND_ENDER_TROLL_STEP = "riftflux:jaxx_step";
    private int attackTimer;

    public EntityEnderTroll(World world) {
        super(world);
        setSize(0.8F, 2.9F);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIRestrictSun(this));
        tasks.addTask(2, new EntityAIFleeSun(this, 1.0D));
        tasks.addTask(3, new EntityAIAttackOnCollide(this, 1.0D, true));
        tasks.addTask(4, new EntityAIWander(this, 0.7D));
        tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(6, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityEnderman.class, 0, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Math.max(1.0D, ModConfig.enderTrollMaxHealth));
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(12.0D);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean attacked = super.attackEntityFrom(source, amount);
        Entity attacker = source == null ? null : source.getEntity();
        if (attacked && attacker instanceof EntityPlayer) {
            setAttackTarget((EntityPlayer) attacker);
            setRevengeTarget((EntityLivingBase) attacker);
        }
        return attacked;
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
        if (!worldObj.isRemote && ticksExisted % 20 == 0) {
            EntityLivingBase currentTarget = getAttackTarget();
            if (currentTarget instanceof EntityPlayer && currentTarget.isEntityAlive()) {
                return;
            }

            EntityEnderman nearbyEnderman = findNearestEnderman(24.0D);
            if (nearbyEnderman != null) {
                setAttackTarget(nearbyEnderman);
                setRevengeTarget(nearbyEnderman);
            } else if (currentTarget == null || !currentTarget.isEntityAlive()) {
                seekNearbyPortal();
            }
        }
        if (worldObj.isRemote && motionX * motionX + motionZ * motionZ > 2.5E-7D && rand.nextInt(5) == 0) {
            worldObj.spawnParticle(
                    "portal",
                    posX + (rand.nextDouble() - 0.5D) * width,
                    posY + rand.nextDouble() * height - 0.25D,
                    posZ + (rand.nextDouble() - 0.5D) * width,
                    (rand.nextDouble() - 0.5D) * 2.0D,
                    -rand.nextDouble(),
                    (rand.nextDouble() - 0.5D) * 2.0D
            );
        }
    }

    @Override
    protected String getLivingSound() {
        return SOUND_ENDER_TROLL;
    }

    @Override
    protected String getHurtSound() {
        return SOUND_ENDER_TROLL_HURT;
    }

    @Override
    protected String getDeathSound() {
        return SOUND_ENDER_TROLL_DEATH;
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {
        worldObj.playSoundAtEntity(this, SOUND_ENDER_TROLL_STEP, 1.0F, 1.0F);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        WAMMobDrops.dropConfigured(this, ModConfig.wamEnderTrollDropEntries);
    }

    @Override
    public boolean getCanSpawnHere() {
        return !isSpawnBlockEndStoneOrObsidian() && super.getCanSpawnHere();
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

    private boolean isSpawnBlockEndStoneOrObsidian() {
        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(posY);
        int z = MathHelper.floor_double(posZ);

        return worldObj.getBlock(x, y - 1, z) == Blocks.end_stone
                || worldObj.getBlock(x, y, z) == Blocks.end_stone
                || worldObj.getBlock(x, y - 1, z) == Blocks.obsidian
                || worldObj.getBlock(x, y, z) == Blocks.obsidian;
    }

    private EntityEnderman findNearestEnderman(double range) {
        List<EntityEnderman> nearby = worldObj.getEntitiesWithinAABB(
                EntityEnderman.class,
                boundingBox.expand(range, 8.0D, range)
        );
        EntityEnderman closest = null;
        double bestDistance = Double.MAX_VALUE;

        for (EntityEnderman enderman : nearby) {
            if (enderman == null || !enderman.isEntityAlive()) {
                continue;
            }

            double distance = getDistanceSqToEntity(enderman);
            if (distance < bestDistance) {
                bestDistance = distance;
                closest = enderman;
            }
        }

        return closest;
    }

    private void seekNearbyPortal() {
        if (worldObj.provider.dimensionId == 1) {
            return;
        }

        int[] portalPos = findNearestPortalBlock(16, 8);
        if (portalPos != null) {
            getNavigator().tryMoveToXYZ(portalPos[0] + 0.5D, portalPos[1], portalPos[2] + 0.5D, 0.23D);
        }
    }

    private int[] findNearestPortalBlock(int horizontalRange, int verticalRange) {
        int centerX = MathHelper.floor_double(posX);
        int centerY = MathHelper.floor_double(posY);
        int centerZ = MathHelper.floor_double(posZ);
        int[] closest = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = centerX - horizontalRange; x <= centerX + horizontalRange; x++) {
            for (int y = centerY - verticalRange; y <= centerY + verticalRange; y++) {
                for (int z = centerZ - horizontalRange; z <= centerZ + horizontalRange; z++) {
                    if (worldObj.getBlock(x, y, z) != Blocks.end_portal && worldObj.getBlock(x, y, z) != Blocks.end_portal_frame) {
                        continue;
                    }

                    double dx = x + 0.5D - posX;
                    double dy = y - posY;
                    double dz = z + 0.5D - posZ;
                    double distance = dx * dx + dy * dy + dz * dz;
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        closest = new int[]{x, y, z};
                    }
                }
            }
        }

        return closest;
    }
}
