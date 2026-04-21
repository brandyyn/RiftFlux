package com.voidsrift.riftflux.pumpkinpastures;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import com.voidsrift.riftflux.palaria.entity.CreeptileExplosion;
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
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityPumpkinCreeper extends EntityMob implements IEntitySyncData {
    private int lastActiveTime;
    private int timeSinceIgnited;
    private int fuseTime = 30;
    private int creeperState = -1;
    private boolean powered;
    private boolean ignited;

    public EntityPumpkinCreeper(World world) {
        super(world);
        setSize(1.6F, 1.6F);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIPumpkinCreeperSwell(this));
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
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35D);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
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
        tag.setBoolean("ignited", isIgnited());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.powered = tag.getBoolean("powered");
        if (tag.hasKey("Fuse")) {
            fuseTime = tag.getShort("Fuse");
        }
        if (tag.getBoolean("ignited")) {
            this.ignited = true;
        }
    }

    @Override
    public void onUpdate() {
        if (isEntityAlive()) {
            lastActiveTime = timeSinceIgnited;
            if (isIgnited()) {
                setCreeperState(1);
            }
            int state = getCreeperState();
            if (state > 0 && timeSinceIgnited == 0) {
                playSound("creeper.primed", 1.0F, 0.5F);
            }
            timeSinceIgnited += state;
            if (timeSinceIgnited < 0) {
                timeSinceIgnited = 0;
            }
            if (timeSinceIgnited >= fuseTime) {
                timeSinceIgnited = fuseTime;
                if (!worldObj.isRemote) {
                    explodePumpkinCreeper();
                    setDead();
                }
            }
        }
        super.onUpdate();
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
        if (source.getEntity() instanceof EntitySkeleton) {
            int record = Item.getIdFromItem(Items.record_13) + rand.nextInt(Item.getIdFromItem(Items.record_wait) - Item.getIdFromItem(Items.record_13) + 1);
            dropItem(Item.getItemById(record), 1);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public float getCreeperFlashIntensity(float partialTicks) {
        return ((float) lastActiveTime + (float) (timeSinceIgnited - lastActiveTime) * partialTicks) / (float) (fuseTime - 2);
    }

    public boolean getPowered() {
        return this.powered;
    }

    public int getCreeperState() {
        return this.creeperState;
    }

    public void setCreeperState(int state) {
        if (this.creeperState == state) {
            return;
        }
        this.creeperState = state;
        EntitySyncHelper.sync(this);
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightning) {
        super.onStruckByLightning(lightning);
        if (!this.powered) {
            this.powered = true;
            EntitySyncHelper.sync(this);
        }
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        ItemStack stack = player == null ? null : player.inventory.getCurrentItem();
        if (stack != null && stack.getItem() == Items.flint_and_steel) {
            worldObj.playSoundEffect(posX + 0.5D, posY + 0.5D, posZ + 0.5D, "fire.ignite", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
            player.swingItem();
            if (!worldObj.isRemote) {
                ignite();
                stack.damageItem(1, player);
                return true;
            }
        }
        return super.interact(player);
    }

    @Override
    protected Item getDropItem() {
        return Items.gunpowder;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int count = rand.nextInt(3) + rand.nextInt(1 + looting);
        for (int i = 0; i < count; i++) {
            dropItem(Items.gunpowder, 1);
        }
        PumpkinPasturesContent.dropSoulItems(this, looting);
    }

    private void explodePumpkinCreeper() {
        float strength = Math.max(0.0F, ModConfig.pumpkinPasturesCreeperExplosionStrength);
        if (getPowered()) {
            strength *= 2.0F;
        }
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
                ModConfig.pumpkinPasturesCreeperExplosionDamagesEnvironment && mobGriefing,
                ModConfig.pumpkinPasturesCreeperDamageMultiplier,
                ModConfig.pumpkinPasturesCreeperKnockbackMultiplier
        );
        explosion.doExplosion();
    }

    public boolean isIgnited() {
        return this.ignited;
    }

    public void ignite() {
        if (this.ignited) {
            return;
        }
        this.ignited = true;
        EntitySyncHelper.sync(this);
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setInteger("State", this.creeperState);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Ignited", this.ignited);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        this.creeperState = tag.getInteger("State");
        this.powered = tag.getBoolean("Powered");
        this.ignited = tag.getBoolean("Ignited");
    }
}
