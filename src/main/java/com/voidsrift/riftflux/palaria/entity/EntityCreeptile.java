package com.voidsrift.riftflux.palaria.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.palaria.PalariaMobDrops;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCreeptile extends EntityMob {
    private int lastActiveTime;
    private int timeSinceIgnited;
    private int fuseTime = 25;
    private int explosionRadius = 4;

    public EntityCreeptile(World world) {
        super(world);
        setSize(1.0F, 1.2F);
        getNavigator().setAvoidsWater(true);
        getNavigator().setBreakDoors(true);
        experienceValue = 15;
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAICreeptileSwell(this));
        tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, false));
        tasks.addTask(5, new EntityAIWander(this, 0.8D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(6, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(16, Byte.valueOf((byte) -1));
        dataWatcher.addObject(17, Byte.valueOf((byte) 0));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModConfig.palariaCreeptileMaxHealth);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
    }

    @Override
    protected void fall(float distance) {
        super.fall(distance);
        timeSinceIgnited = (int) ((float) timeSinceIgnited + distance * 1.5F);
        if (timeSinceIgnited > fuseTime - 5) {
            timeSinceIgnited = fuseTime - 5;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if (getPowered()) {
            tag.setBoolean("powered", true);
        }
        tag.setShort("Fuse", (short) fuseTime);
        tag.setByte("ExplosionRadius", (byte) explosionRadius);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        dataWatcher.updateObject(17, Byte.valueOf((byte) (tag.getBoolean("powered") ? 1 : 0)));
        if (tag.hasKey("Fuse")) {
            fuseTime = tag.getShort("Fuse");
        }
        if (tag.hasKey("ExplosionRadius")) {
            explosionRadius = tag.getByte("ExplosionRadius");
        }
    }

    @Override
    public void onUpdate() {
        if (isEntityAlive()) {
            lastActiveTime = timeSinceIgnited;
            int state = getCreeptileState();
            if (state > 0 && timeSinceIgnited == 0) {
                playSound("random.fuse", 1.0F, 0.5F);
            }
            timeSinceIgnited += state;
            if (timeSinceIgnited < 0) {
                timeSinceIgnited = 0;
            }
            if (timeSinceIgnited >= fuseTime) {
                timeSinceIgnited = fuseTime;
                if (!worldObj.isRemote) {
                    explodeCreeptile();
                    setDead();
                }
            }
        }
        super.onUpdate();
    }

    @Override
    protected String getHurtSound() {
        return "mob.creeper.say";
    }

    @Override
    protected String getDeathSound() {
        return "mob.creeper.death";
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (source.getEntity() instanceof net.minecraft.entity.monster.EntitySkeleton) {
            int record = Item.getIdFromItem(Items.record_13) + rand.nextInt(Item.getIdFromItem(Items.record_wait) - Item.getIdFromItem(Items.record_13) + 1);
            dropItem(Item.getItemById(record), 1);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public float getCreeptileFlashIntensity(float partialTicks) {
        return ((float) lastActiveTime + (float) (timeSinceIgnited - lastActiveTime) * partialTicks) / (float) (fuseTime - 2);
    }

    public boolean getPowered() {
        return dataWatcher.getWatchableObjectByte(17) == 1;
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    public int getCreeptileState() {
        return dataWatcher.getWatchableObjectByte(16);
    }

    public void setCreeptileState(int state) {
        dataWatcher.updateObject(16, Byte.valueOf((byte) state));
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightning) {
        super.onStruckByLightning(lightning);
        dataWatcher.updateObject(17, Byte.valueOf((byte) 1));
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        PalariaMobDrops.dropConfigured(this, ModConfig.palariaCreeptileDropEntries);
    }

    private void explodeCreeptile() {
        float strength = getExplosionStrength();
        if (strength <= 0.0F) {
            return;
        }

        boolean mobGriefing = worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
        CreeptileExplosion explosion = new CreeptileExplosion(
                worldObj,
                this,
                posX,
                posY,
                posZ,
                strength,
                ModConfig.palariaCreeptileExplosionDamagesEnvironment && mobGriefing,
                ModConfig.palariaCreeptileDamageMultiplier,
                ModConfig.palariaCreeptileKnockbackMultiplier
        );
        explosion.doExplosion();
    }

    private float getExplosionStrength() {
        float strength = Math.max(0.0F, ModConfig.palariaCreeptileExplosionStrength);
        if (getPowered()) {
            strength *= 2.0F;
        }
        return strength;
    }
}
