package com.voidsrift.riftflux.palaria.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import com.voidsrift.riftflux.palaria.PalariaMobDrops;
import com.voidsrift.riftflux.util.ConfigResolver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EntityNimatin extends EntityTameable implements IEntitySyncData {
    private float headRotationCourse;
    private float headRotationCourseOld;
    private boolean isShaking;
    private boolean isWetShaking;
    private float timeWolfIsShaking;
    private float prevTimeWolfIsShaking;
    private static final int PASSENGER_SEATS = 1;
    private static final float DRIVER_FORWARD_OFFSET = 0.5F;
    private static final float PASSENGER_BACK_OFFSET = 0.75F;
    private static final float RIDER_UP_OFFSET = 0.67F;
    private static final float PASSENGER_UP_OFFSET = RIDER_UP_OFFSET - 0.64F;
    private static final double POUNCE_FORWARD_BOOST = 1.25D;
    private static final double POUNCE_EXTRA_GRAVITY = 0.14D;
    private static final int EJECT_NO_REMOUNT_TICKS = 100;
    private float jumpPower;
    private boolean nimatinJumping;
    private boolean usedDoubleJump;
    private int mountedJumpTicks;
    private final EntityNimatinSeat[] passengerSeats = new EntityNimatinSeat[PASSENGER_SEATS];
    private final Map<Integer, Integer> seatNoRemountTicks = new HashMap<Integer, Integer>();
    private boolean angry;
    private boolean begging;

    public EntityNimatin(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(2.0F, 2.0F);
        getNavigator().setAvoidsWater(true);
        getNavigator().setBreakDoors(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, aiSit);
        tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
        tasks.addTask(3, new EntityAIAttackOnCollide(this, 0.4D, true));
        tasks.addTask(4, new EntityAINimatinTempt(this, 0.4D, 8.0F));
        tasks.addTask(5, new EntityAINimatinFollowOwner(this, 0.4D, 10.0F, 4.0F));
        tasks.addTask(6, new EntityAIMate(this, 0.4D));
        tasks.addTask(7, new EntityAIWander(this, 0.4D));
        tasks.addTask(8, new EntityAINimatinBeg(this, 8.0F));
        tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(10, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntitySheep.class, 200, false));
        targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, EntityRaptorChicken.class, 200, false));
        experienceValue = 50;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4D);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(isTamed() ? ModConfig.palariaNimatinTamedMaxHealth : ModConfig.palariaNimatinMaxHealth);
        getAttackDamageAttribute().setBaseValue(isTamed() ? ModConfig.palariaNimatinTamedDamage : 20.0D);
    }

    @Override
    public void setTamed(boolean tamed) {
        super.setTamed(tamed);
        if (getEntityAttribute(SharedMonsterAttributes.maxHealth) != null) {
            getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(tamed ? ModConfig.palariaNimatinTamedMaxHealth : ModConfig.palariaNimatinMaxHealth);
            getAttackDamageAttribute().setBaseValue(tamed ? ModConfig.palariaNimatinTamedDamage : 20.0D);
        }
    }

    private IAttributeInstance getAttackDamageAttribute() {
        IAttributeInstance attribute = getEntityAttribute(SharedMonsterAttributes.attackDamage);
        return attribute != null ? attribute : getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
    }

    @Override
    public void setAttackTarget(EntityLivingBase target) {
        super.setAttackTarget(target);
        if (target instanceof EntityPlayer) {
            setAngry(true);
        }
    }

    @Override
    protected void updateAITick() {
        super.updateAITick();
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public void moveEntityWithHeading(float strafeMovement, float forwardMovement) {
        if (riddenByEntity instanceof EntityLivingBase) {
            EntityLivingBase rider = (EntityLivingBase) riddenByEntity;
            prevRotationYaw = rotationYaw = rider.rotationYaw;
            rotationPitch = rider.rotationPitch * 0.5F;
            setRotation(rotationYaw, rotationPitch);
            rotationYawHead = renderYawOffset = rotationYaw;
            strafeMovement = rider.moveStrafing * 0.5F;
            forwardMovement = rider.moveForward;
            if (forwardMovement <= 0.0F) {
                forwardMovement *= 0.25F;
            }
            if (jumpPower > 0.0F && !nimatinJumping && canStartMountedJump()) {
                performMountedJump(getConfiguredJumpVelocity() * (double) jumpPower, forwardMovement, jumpPower);
                jumpPower = 0.0F;
            }
            stepHeight = 1.0F;
            jumpMovementFactor = getAIMoveSpeed() * 0.1F;
            if (!worldObj.isRemote) {
                setAIMoveSpeed(ModConfig.palariaNimatinRidingSpeed);
                super.moveEntityWithHeading(strafeMovement, forwardMovement);
            }
            if (onGround && mountedJumpTicks == 0) {
                jumpPower = 0.0F;
                nimatinJumping = false;
                usedDoubleJump = false;
            }
            prevLimbSwingAmount = limbSwingAmount;
            double dx = posX - prevPosX;
            double dz = posZ - prevPosZ;
            float movement = MathHelper.sqrt_double(dx * dx + dz * dz) * 4.0F;
            if (movement > 1.0F) {
                movement = 1.0F;
            }
            limbSwingAmount += (movement - limbSwingAmount) * 0.4F;
            limbSwing += limbSwingAmount;
            return;
        }
        super.moveEntityWithHeading(strafeMovement, forwardMovement);
    }

    public void setJumpPower(int jumpCharge) {
        if (jumpCharge < 0 || !canStartMountedJump()) {
            return;
        }
        float power;
        if (jumpCharge > 90) {
            power = 1.0F;
        } else {
            power = 0.4F + 0.4F * (float) jumpCharge / 90.0F;
        }
        if (riddenByEntity instanceof EntityLivingBase) {
            performMountedJump(getConfiguredJumpVelocity() * (double) power, ((EntityLivingBase) riddenByEntity).moveForward, power);
            jumpPower = 0.0F;
        } else {
            jumpPower = power;
        }
    }

    public void tryDoubleJump(EntityPlayer player) {
        if (player == null
                || !ModConfig.palariaNimatinDoubleJumpEnabled
                || !canDoubleJumpNow()
                || isInWater()
                || handleLavaMovement()) {
            return;
        }
        double velocity = getConfiguredDoubleJumpVelocity();
        motionY = Math.min(velocity * 1.6D, Math.max(velocity * 1.2D, Math.max(0.0D, motionY) + velocity * 0.9D));
        isAirBorne = true;
        nimatinJumping = true;
        usedDoubleJump = true;
        mountedJumpTicks = Math.max(mountedJumpTicks, 1);
        fallDistance = 0.0F;
        onGround = false;
        isCollidedVertically = false;
        velocityChanged = true;
        if (player.moveForward > 0.0F) {
            float sin = MathHelper.sin(rotationYaw * (float) Math.PI / 180.0F);
            float cos = MathHelper.cos(rotationYaw * (float) Math.PI / 180.0F);
            motionX += -POUNCE_FORWARD_BOOST * 0.55D * (double) sin;
            motionZ += POUNCE_FORWARD_BOOST * 0.55D * (double) cos;
        }
        ForgeHooks.onLivingJump(this);
    }

    @Override
    protected String getLivingSound() {
        return isTamed() ? "mob.cat.purr" : null;
    }

    @Override
    protected String getHurtSound() {
        return super.getHurtSound();
    }

    @Override
    protected String getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    protected float getSoundVolume() {
        return 1.4F;
    }

    @Override
    public int getTalkInterval() {
        return isTamed() ? Math.max(1, ModConfig.palariaNimatinTalkInterval) : super.getTalkInterval();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        headRotationCourseOld = headRotationCourse;
        headRotationCourse += (isBegging() ? 1.0F - headRotationCourse : -headRotationCourse) * 0.4F;
        if (isBegging()) {
            numTicksToChaseTarget = 10;
        }
        if (mountedJumpTicks > 0) {
            ++mountedJumpTicks;
            if ((onGround && mountedJumpTicks > 5) || isMountedJumpSettled() || mountedJumpTicks > 100) {
                mountedJumpTicks = 0;
                nimatinJumping = false;
                usedDoubleJump = false;
            }
        }
        if (nimatinJumping && !onGround) {
            motionY -= POUNCE_EXTRA_GRAVITY;
        } else if (onGround && mountedJumpTicks == 0) {
            nimatinJumping = false;
            usedDoubleJump = false;
        }
        if (isWet()) {
            isShaking = true;
            isWetShaking = false;
            timeWolfIsShaking = 0.0F;
            prevTimeWolfIsShaking = 0.0F;
        } else if ((isShaking || isWetShaking) && isWetShaking) {
            prevTimeWolfIsShaking = timeWolfIsShaking;
            timeWolfIsShaking += 0.05F;
            if (prevTimeWolfIsShaking >= 2.0F) {
                isShaking = false;
                isWetShaking = false;
                prevTimeWolfIsShaking = 0.0F;
                timeWolfIsShaking = 0.0F;
            }
            if (timeWolfIsShaking > 0.4F) {
                float y = (float) boundingBox.minY;
                int count = (int) (MathHelper.sin((timeWolfIsShaking - 0.4F) * (float) Math.PI) * 7.0F);
                for (int i = 0; i < count; ++i) {
                    float xOffset = (rand.nextFloat() * 2.0F - 1.0F) * width * 0.5F;
                    float zOffset = (rand.nextFloat() * 2.0F - 1.0F) * width * 0.5F;
                    worldObj.spawnParticle("splash", posX + (double) xOffset, y + 0.8F, posZ + (double) zOffset, motionX, motionY, motionZ);
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!worldObj.isRemote && isTamed()) {
            ensureSeats();
            tickSeatNoRemounts();
            if (isSitting()) {
                ejectMobPassengers();
                return;
            }
            if (ModConfig.palariaNimatinAllowMobPassengers) {
                tryMountNearbyMob();
            } else {
                ejectMobPassengers();
            }
        }
        if (!worldObj.isRemote && isShaking && !isWetShaking && !hasPath() && onGround) {
            isWetShaking = true;
            timeWolfIsShaking = 0.0F;
            prevTimeWolfIsShaking = 0.0F;
            worldObj.setEntityState(this, (byte) 8);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.fall) {
            return false;
        }
        if (isRiderProjectileOrDirectHit(source)) {
            return false;
        }
        if (isEntityInvulnerable()) {
            return false;
        }
        Entity attacker = source.getEntity();
        aiSit.setSitting(false);
        if (attacker != null && !(attacker instanceof EntityPlayer) && !(attacker instanceof net.minecraft.entity.projectile.EntityArrow)) {
            amount = (amount + 1.0F) / 2.0F;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float damage = isTamed() ? ModConfig.palariaNimatinTamedDamage : 20.0F;
        return target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack held = player.inventory.getCurrentItem();
        if (isTamed()) {
            if (held != null && getHealth() < getMaxHealth() && canHealWithItem(held)) {
                if (!player.capabilities.isCreativeMode) {
                    --held.stackSize;
                }
                heal(getNimatinHealAmount(held));
                if (held.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
                return true;
            }
            if (!isBreedingItem(held)) {
                if (func_152114_e(player) && player.isSneaking() && !worldObj.isRemote) {
                    boolean sitting = !isSitting();
                    aiSit.setSitting(sitting);
                    setSitting(sitting);
                    if (sitting) {
                        ejectMobPassengers();
                    }
                    isJumping = false;
                    setPathToEntity((PathEntity) null);
                    setAttackTarget(null);
                    return true;
                }
                if (player.ridingEntity != null) {
                    return true;
                }
                if (!worldObj.isRemote) {
                    if (riddenByEntity == null && func_152114_e(player)) {
                        aiSit.setSitting(false);
                        setSitting(false);
                        setPathToEntity((PathEntity) null);
                        setAttackTarget(null);
                        player.mountEntity(this);
                        return true;
                    }
                    EntityNimatinSeat seat = getAvailableSeat();
                    if (seat != null && riddenByEntity != null) {
                        player.mountEntity(seat);
                        return true;
                    }
                }
                return true;
            }
        } else if (isConfiguredTameItem(held) && !isAngry()) {
            if (!player.capabilities.isCreativeMode) {
                --held.stackSize;
            }
            if (held.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
            if (!worldObj.isRemote) {
                if (rand.nextFloat() < ModConfig.palariaNimatinTameChance) {
                    setTamed(true);
                    func_152115_b(player.getUniqueID().toString());
                    setHealth(getMaxHealth());
                    setPathToEntity((PathEntity) null);
                    setAttackTarget(null);
                    aiSit.setSitting(true);
                    setSitting(true);
                    playTameEffect(true);
                    worldObj.setEntityState(this, (byte) 7);
                } else {
                    playTameEffect(false);
                    worldObj.setEntityState(this, (byte) 6);
                }
            }
            return true;
        }
        return super.interact(player);
    }

    @Override
    public void updateRiderPosition() {
        if (riddenByEntity != null) {
            updatePassenger(riddenByEntity);
        }
    }

    public void updatePassenger(Entity passenger) {
        if (passenger == null) {
            return;
        }
        float yawRad = (float) Math.toRadians(getSeatYaw());
        float sin = MathHelper.sin(yawRad);
        float cos = MathHelper.cos(yawRad);
        double x = posX;
        double z = posZ;
        if (passenger == this.riddenByEntity) {
            x -= sin * DRIVER_FORWARD_OFFSET;
            z += cos * DRIVER_FORWARD_OFFSET;
        } else if (passenger.ridingEntity instanceof EntityNimatinSeat) {
            x += sin * PASSENGER_BACK_OFFSET;
            z -= cos * PASSENGER_BACK_OFFSET;
        }
        float yOffset = passenger == this.riddenByEntity ? RIDER_UP_OFFSET : PASSENGER_UP_OFFSET;
        double y = posY + getMountedYOffset() + passenger.getYOffset() + yOffset;
        passenger.setPosition(x, y, z);
        passenger.fallDistance = 0.0F;
        if (passenger instanceof EntityLivingBase && !(passenger instanceof EntityPlayer)) {
            EntityLivingBase living = (EntityLivingBase) passenger;
            living.moveStrafing = 0.0F;
            living.moveForward = 0.0F;
            living.motionX = motionX;
            living.motionY = motionY;
            living.motionZ = motionZ;
        }
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (entity == this.riddenByEntity
                || isSeatPassenger(entity)
                || isSeatEntity(entity)
                || hasSeatPassenger()) {
            return;
        }
        super.applyEntityCollision(entity);
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        if (hasSeatPassenger()) {
            return;
        }
        super.addVelocity(x, y, z);
    }

    @Override
    public double getMountedYOffset() {
        return (double) height * 0.45D;
    }

    @Override
    protected void fall(float distance) {
        this.fallDistance = 0.0F;
    }

    @Override
    public void onKillEntity(EntityLivingBase target) {
        super.onKillEntity(target);
        float healAmount = Math.max(0.0F, ModConfig.palariaNimatinKillHealAmount);
        if (healAmount > 0.0F && isEntityAlive()) {
            heal(healAmount);
            EntityLivingBase owner = getOwner();
            float ownerHeal = healAmount * Math.max(0.0F, ModConfig.palariaNimatinOwnerKillHealMultiplier);
            if (ownerHeal > 0.0F && owner != null && owner.isEntityAlive()) {
                owner.heal(ownerHeal);
            }
        }
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        if (isTamed()) {
            return;
        }
        PalariaMobDrops.dropConfigured(this, ModConfig.palariaNimatinDropEntries);
    }

    @Override
    public void setDead() {
        super.setDead();
        if (!worldObj.isRemote) {
            clearSeats();
        }
    }

    private void ensureSeats() {
        if (worldObj == null || worldObj.isRemote || !isTamed()) {
            return;
        }
        for (int i = 0; i < passengerSeats.length; i++) {
            EntityNimatinSeat seat = passengerSeats[i];
            if (seat != null && (seat.isDead || seat.worldObj != worldObj)) {
                passengerSeats[i] = null;
                seat = null;
            }
            if (seat == null) {
                seat = findExistingSeat(i);
                if (seat != null) {
                    seat.setParent(this);
                    seat.setSeatIndex(i);
                    passengerSeats[i] = seat;
                } else {
                    seat = new EntityNimatinSeat(worldObj, this, i);
                    seat.setPosition(posX, posY, posZ);
                    if (worldObj.spawnEntityInWorld(seat)) {
                        passengerSeats[i] = seat;
                    }
                }
            } else {
                seat.setParent(this);
                seat.setSeatIndex(i);
                if (worldObj.getEntityByID(seat.getEntityId()) != seat) {
                    seat.setPosition(posX, posY, posZ);
                    if (!worldObj.spawnEntityInWorld(seat)) {
                        passengerSeats[i] = null;
                    }
                }
            }
        }
    }

    private EntityNimatinSeat findExistingSeat(int seatIndex) {
        if (worldObj == null || worldObj.loadedEntityList == null) {
            return null;
        }
        for (Object obj : worldObj.loadedEntityList) {
            if (!(obj instanceof EntityNimatinSeat)) {
                continue;
            }
            EntityNimatinSeat seat = (EntityNimatinSeat) obj;
            if (!seat.isDead && seat.worldObj == worldObj && seat.getSeatIndex() == seatIndex && seat.getParent() == this) {
                return seat;
            }
        }
        return null;
    }

    private void clearSeats() {
        for (int i = 0; i < passengerSeats.length; i++) {
            EntityNimatinSeat seat = passengerSeats[i];
            if (seat != null) {
                seat.setDead();
                passengerSeats[i] = null;
            }
        }
    }

    private float getSeatYaw() {
        return riddenByEntity instanceof EntityLivingBase ? rotationYaw : renderYawOffset;
    }

    private void repairDirectRiderLink() {
        if (riddenByEntity instanceof EntityPlayer && riddenByEntity.ridingEntity == this) {
            return;
        }
        if (worldObj != null && worldObj.isRemote) {
            return;
        }
        if (worldObj == null || worldObj.playerEntities == null) {
            return;
        }
        for (Object obj : worldObj.playerEntities) {
            if (!(obj instanceof EntityPlayer)) {
                continue;
            }
            EntityPlayer player = (EntityPlayer) obj;
            if (player.ridingEntity == this) {
                forceDirectRider(player);
                return;
            }
        }
    }

    public boolean forceDirectRider(EntityPlayer player) {
        if (player == null || player.isDead || player.worldObj != worldObj) {
            return false;
        }
        if (riddenByEntity != null && (riddenByEntity.isDead || riddenByEntity.worldObj != worldObj || isSamePlayer(riddenByEntity, player))) {
            riddenByEntity = null;
        }
        if (riddenByEntity != null && riddenByEntity != player) {
            return false;
        }
        if (player.ridingEntity == this) {
            riddenByEntity = player;
            return true;
        }
        if (player.ridingEntity != null && (player.ridingEntity.isDead || player.ridingEntity.worldObj != worldObj || player.ridingEntity.getDistanceSqToEntity(this) <= 4.0D)) {
            player.ridingEntity = null;
        }
        if (player.ridingEntity != null || player.getDistanceSqToEntity(this) > 64.0D) {
            return false;
        }
        player.mountEntity(this);
        player.ridingEntity = this;
        riddenByEntity = player;
        player.fallDistance = 0.0F;
        return true;
    }

    private boolean isSamePlayer(Entity entity, EntityPlayer player) {
        return entity instanceof EntityPlayer
                && player != null
                && ((EntityPlayer) entity).getUniqueID().equals(player.getUniqueID());
    }

    private boolean isRiderProjectileOrDirectHit(DamageSource source) {
        if (!(riddenByEntity instanceof EntityPlayer) || source == null) {
            return false;
        }
        Entity rider = riddenByEntity;
        Entity attacker = source.getEntity();
        if (attacker == rider) {
            return true;
        }
        Entity direct = source.getSourceOfDamage();
        return direct instanceof EntityArrow && ((EntityArrow) direct).shootingEntity == rider;
    }

    private EntityNimatinSeat getAvailableSeat() {
        ensureSeats();
        for (EntityNimatinSeat seat : passengerSeats) {
            if (seat != null && !seat.isDead && seat.worldObj == worldObj && seat.riddenByEntity == null) {
                return seat;
            }
        }
        return null;
    }

    private void tryMountNearbyMob() {
        if (!isTamed() || !isStandingStillForMobMount()) {
            return;
        }
        EntityNimatinSeat seat = getAvailableSeat();
        if (seat == null) {
            return;
        }
        List entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.8D, 0.6D, 0.8D));
        for (Object obj : entities) {
            if (!(obj instanceof EntityLivingBase) || obj instanceof EntityPlayer) {
                continue;
            }
            EntityLivingBase candidate = (EntityLivingBase) obj;
            if (candidate.isDead || candidate.ridingEntity != null || candidate.riddenByEntity != null) {
                continue;
            }
            if (!isMobPassengerAllowed(candidate)) {
                continue;
            }
            if (isInNoRemountCooldown(candidate)) {
                continue;
            }
            candidate.mountEntity(seat);
            if (candidate.ridingEntity == seat) {
                updatePassenger(candidate);
                return;
            }
        }
    }

    private boolean isStandingStillForMobMount() {
        double motionSq = motionX * motionX + motionZ * motionZ;
        double deltaX = posX - prevPosX;
        double deltaZ = posZ - prevPosZ;
        double deltaSq = deltaX * deltaX + deltaZ * deltaZ;
        return motionSq < 9.0E-4D && deltaSq < 9.0E-4D;
    }

    private boolean isMobPassengerAllowed(EntityLivingBase entity) {
        if (entity == null
                || entity == this
                || entity instanceof EntityNimatin
                || entity instanceof com.voidsrift.riftflux.avatar.appa.EntityBison) {
            return false;
        }
        if (entity instanceof IBossDisplayData) {
            return false;
        }
        String[] filter = ModConfig.palariaNimatinMobPassengerBlacklist;
        return filter == null || filter.length == 0 || !ConfigResolver.matchesConfiguredEntity(entity, filter);
    }

    private void ejectMobPassengers() {
        for (EntityNimatinSeat seat : passengerSeats) {
            if (seat == null || seat.isDead || seat.worldObj != worldObj) {
                continue;
            }
            if (seat.riddenByEntity instanceof EntityLivingBase && !(seat.riddenByEntity instanceof EntityPlayer)) {
                seat.riddenByEntity.mountEntity(null);
            }
        }
    }

    public boolean ejectSeatPassenger(Entity passenger) {
        if (passenger == null || !(passenger.ridingEntity instanceof EntityNimatinSeat)) {
            return false;
        }
        EntityNimatinSeat seat = (EntityNimatinSeat) passenger.ridingEntity;
        if (seat.getParent() != this) {
            return false;
        }
        passenger.mountEntity(null);
        setNoRemountCooldown(passenger, EJECT_NO_REMOUNT_TICKS);
        if (!worldObj.isRemote) {
            float yawRad = (float) Math.toRadians(rotationYaw);
            double dropX = posX - MathHelper.sin(yawRad) * 1.2D;
            double dropZ = posZ + MathHelper.cos(yawRad) * 1.2D;
            double dropY = posY + getMountedYOffset() + passenger.getYOffset() + 0.2D;
            passenger.setPosition(dropX, dropY, dropZ);
            passenger.motionX = motionX;
            passenger.motionY = 0.08D;
            passenger.motionZ = motionZ;
            passenger.velocityChanged = true;
        }
        return true;
    }

    public boolean ejectClosestSeatPassenger(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (EntityNimatinSeat seat : passengerSeats) {
            if (!isSeatLive(seat) || seat.riddenByEntity == null) {
                continue;
            }
            Entity candidate = seat.riddenByEntity;
            if (candidate instanceof EntityPlayer) {
                continue;
            }
            double dist = candidate.getDistanceSqToEntity(player);
            if (dist < closestDist) {
                closestDist = dist;
                closest = candidate;
            }
        }
        return closest != null && ejectSeatPassenger(closest);
    }

    private boolean isSeatLive(EntityNimatinSeat seat) {
        return seat != null
                && !seat.isDead
                && seat.worldObj == worldObj
                && worldObj.getEntityByID(seat.getEntityId()) == seat;
    }

    private void tickSeatNoRemounts() {
        if (seatNoRemountTicks.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Integer, Integer>> iterator = seatNoRemountTicks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            int next = entry.getValue().intValue() - 1;
            if (next <= 0) {
                iterator.remove();
            } else {
                entry.setValue(Integer.valueOf(next));
            }
        }
    }

    private boolean isInNoRemountCooldown(Entity entity) {
        if (entity == null) {
            return false;
        }
        Integer ticks = seatNoRemountTicks.get(Integer.valueOf(entity.getEntityId()));
        return ticks != null && ticks.intValue() > 0;
    }

    private void setNoRemountCooldown(Entity entity, int ticks) {
        if (entity != null && ticks > 0) {
            seatNoRemountTicks.put(Integer.valueOf(entity.getEntityId()), Integer.valueOf(ticks));
        }
    }

    private boolean isSeatPassenger(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (!(entity.ridingEntity instanceof EntityNimatinSeat)) {
            return false;
        }
        EntityNimatinSeat seat = (EntityNimatinSeat) entity.ridingEntity;
        return seat.getParent() == this;
    }

    private boolean isSeatEntity(Entity entity) {
        if (!(entity instanceof EntityNimatinSeat)) {
            return false;
        }
        EntityNimatinSeat seat = (EntityNimatinSeat) entity;
        return seat.getParent() == this;
    }

    private boolean hasMobSeatPassenger() {
        for (EntityNimatinSeat seat : passengerSeats) {
            if (seat == null || seat.isDead || seat.worldObj != worldObj) {
                continue;
            }
            if (seat.riddenByEntity instanceof EntityLivingBase && !(seat.riddenByEntity instanceof EntityPlayer)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSeatPassenger() {
        for (EntityNimatinSeat seat : passengerSeats) {
            if (seat == null || seat.isDead || seat.worldObj != worldObj) {
                continue;
            }
            if (seat.riddenByEntity != null) {
                return true;
            }
        }
        return false;
    }

    private boolean isNimatinMeat(ItemStack stack, ItemFood food) {
        if (stack == null || food == null) {
            return false;
        }
        if (food.isWolfsFavoriteMeat()) {
            return true;
        }
        String name = (stack.getUnlocalizedName() + " " + stack.getDisplayName()).toLowerCase(java.util.Locale.ROOT);
        return name.contains("meat")
                || name.contains("beef")
                || name.contains("steak")
                || name.contains("pork")
                || name.contains("bacon")
                || name.contains("chicken")
                || name.contains("mutton")
                || name.contains("rabbit")
                || name.contains("venison")
                || name.contains("turkey")
                || name.contains("duck")
                || name.contains("fish");
    }

    private boolean canHealWithItem(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (isConfiguredTameItem(stack)) {
            return true;
        }
        return stack.getItem() instanceof ItemFood && isNimatinMeat(stack, (ItemFood) stack.getItem());
    }

    private float getNimatinHealAmount(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemFood) {
            return Math.max(1.0F, (float) ((ItemFood) stack.getItem()).func_150905_g(stack));
        }
        return 4.0F;
    }

    private boolean isConfiguredTameItem(ItemStack stack) {
        return ModConfig.palariaNimatinTameable
                && stack != null
                && PalariaMobDrops.matchesConfiguredItem(stack, ModConfig.palariaNimatinTameItems);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleHealthUpdate(byte state) {
        if (state == 8) {
            isWetShaking = true;
            timeWolfIsShaking = 0.0F;
            prevTimeWolfIsShaking = 0.0F;
        } else {
            super.handleHealthUpdate(state);
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemFood && ((ItemFood) stack.getItem()).isWolfsFavoriteMeat();
    }

    @Override
    public EntityAgeable createChild(EntityAgeable mate) {
        EntityNimatin child = new EntityNimatin(worldObj);
        String owner = func_152113_b();
        if (owner != null && owner.trim().length() > 0) {
            child.func_152115_b(owner);
            child.setTamed(true);
        }
        return child;
    }

    @Override
    public boolean canMateWith(EntityAnimal animal) {
        if (animal == this || !isTamed() || !(animal instanceof EntityNimatin)) {
            return false;
        }
        EntityNimatin other = (EntityNimatin) animal;
        return other.isTamed() && !other.isSitting() && isInLove() && other.isInLove();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean("Angry", isAngry());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        setAngry(tag.getBoolean("Angry"));
    }

    @SideOnly(Side.CLIENT)
    public boolean getWolfShaking() {
        return isShaking;
    }

    @SideOnly(Side.CLIENT)
    public float getShadingWhileShaking(float partialTicks) {
        return 1.75F + (prevTimeWolfIsShaking + (timeWolfIsShaking - prevTimeWolfIsShaking) * partialTicks) / 2.0F * 0.25F;
    }

    @SideOnly(Side.CLIENT)
    public float getShakeAngle(float partialTicks, float offset) {
        float value = (prevTimeWolfIsShaking + (timeWolfIsShaking - prevTimeWolfIsShaking) * partialTicks + offset) / 1.8F;
        if (value < 0.0F) value = 0.0F;
        if (value > 1.0F) value = 1.0F;
        return MathHelper.sin(value * (float) Math.PI) * MathHelper.sin(value * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
    }

    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float partialTicks) {
        return (headRotationCourseOld + (headRotationCourse - headRotationCourseOld) * partialTicks) * 0.1F * (float) Math.PI;
    }

    public boolean isAngry() {
        return this.angry;
    }

    public void setAngry(boolean angry) {
        if (this.angry == angry) {
            return;
        }
        this.angry = angry;
        EntitySyncHelper.sync(this);
    }

    public boolean isBegging() {
        return this.begging;
    }

    public void setBegging(boolean begging) {
        if (this.begging == begging) {
            return;
        }
        this.begging = begging;
        EntitySyncHelper.sync(this);
    }

    private double getConfiguredJumpVelocity() {
        return solveJumpVelocityForHeight(Math.max(0.5D, ModConfig.palariaNimatinMaxJumpHeight));
    }

    private double getConfiguredDoubleJumpVelocity() {
        return solveJumpVelocityForHeight(Math.max(0.5D, ModConfig.palariaNimatinDoubleJumpHeight));
    }

    private boolean canStartMountedJump() {
        if (onGround
                || (isCollidedVertically && Math.abs(motionY) < 0.12D)
                || (fallDistance <= 0.0F && Math.abs(motionY) < 0.03D)) {
            return true;
        }
        return riddenByEntity instanceof EntityPlayer
                && mountedJumpTicks == 0
                && fallDistance < 1.5F
                && Math.abs(motionY) < 0.2D;
    }

    private boolean canDoubleJumpNow() {
        if (usedDoubleJump) {
            return false;
        }
        boolean recentlyMountedJumped = mountedJumpTicks > 2 && mountedJumpTicks < 100;
        boolean naturallyAirborne = !onGround || fallDistance > 0.0F || Math.abs(motionY) > 0.08D;
        return recentlyMountedJumped || naturallyAirborne;
    }

    private boolean isMountedJumpSettled() {
        return mountedJumpTicks > 8
                && Math.abs(motionY) < 0.08D
                && Math.abs(posY - prevPosY) < 0.03D;
    }

    private void performMountedJump(double velocity, float forwardMovement, float jumpStrength) {
        motionY = velocity;
        nimatinJumping = true;
        isAirBorne = true;
        usedDoubleJump = false;
        mountedJumpTicks = 1;
        fallDistance = 0.0F;
        onGround = false;
        isCollidedVertically = false;
        velocityChanged = true;
        if (forwardMovement > 0.0F) {
            float sin = MathHelper.sin(rotationYaw * (float) Math.PI / 180.0F);
            float cos = MathHelper.cos(rotationYaw * (float) Math.PI / 180.0F);
            motionX += -POUNCE_FORWARD_BOOST * (double) sin * (double) jumpStrength;
            motionZ += POUNCE_FORWARD_BOOST * (double) cos * (double) jumpStrength;
        }
        ForgeHooks.onLivingJump(this);
    }

    private static double solveJumpVelocityForHeight(double targetHeight) {
        double low = 0.0D;
        double high = 3.0D;
        for (int i = 0; i < 32; i++) {
            double mid = (low + high) * 0.5D;
            if (simulateJumpHeight(mid) >= targetHeight) {
                high = mid;
            } else {
                low = mid;
            }
        }
        return high;
    }

    private static double simulateJumpHeight(double velocity) {
        double currentVelocity = velocity;
        double heightTravelled = 0.0D;
        while (currentVelocity > 0.0D) {
            heightTravelled += currentVelocity;
            currentVelocity = (currentVelocity - 0.08D - POUNCE_EXTRA_GRAVITY) * 0.98D;
        }
        return heightTravelled;
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setBoolean("Angry", this.angry);
        tag.setBoolean("Begging", this.begging);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        this.angry = tag.getBoolean("Angry");
        this.begging = tag.getBoolean("Begging");
    }

}
