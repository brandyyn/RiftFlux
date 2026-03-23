package com.voidsrift.riftflux.axolotl;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAxolotl extends EntityTameable {
    private static final int DEFAULT_STATE_WATCHER = 18;
    private static final int TOTAL_AIR_SUPPLY = 6000;
    private static final float AXOLOTL_WIDTH = 0.75F;
    private static final float AXOLOTL_HEIGHT = 0.42F + 0.0625F;
    private static final int VARIANT_MASK = 31;
    private static final int FROM_BUCKET_FLAG = 32;
    private static final int PLAYING_DEAD_FLAG = 64;

    private int playingDeadTicks;
    private int swimCruiseTicks;
    private int swimBurstTicks;
    private int swimBurstCooldown;
    private int swimAvoidTicks;
    private int swimCircleChainCount;
    private float swimCircleDirection;
    private float swimCruiseTurnRate;
    private double swimCruiseTargetY;
    private double swimCruiseSpeed;

    public EntityAxolotl(World world) {
        super(world);
        this.setSize(AXOLOTL_WIDTH, AXOLOTL_HEIGHT);
        this.experienceValue = 1;
        PathNavigate navigator = this.getNavigator();
        navigator.setAvoidsWater(false);
        navigator.setCanSwim(true);
        this.tasks.addTask(0, this.aiSit);
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(2, new EntityAIFollowOwner(this, 1.1D, 5.0F, 2.0F));
        this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(4, new EntityAITemptAxolotlFish(this, 1.1D, false));
        this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(6, new EntityAIAttackOnCollide(this, EntitySquid.class, 1.2D, true));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntitySquid.class, 0, true));
        this.setAir(TOTAL_AIR_SUPPLY);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(getStateWatcherId(), Integer.valueOf(AxolotlVariant.LUCY.getId()));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.setBaseAttribute(SharedMonsterAttributes.maxHealth, Math.max(1.0D, ModConfig.axolotlMaxHealth));
        this.setBaseAttribute(SharedMonsterAttributes.movementSpeed, 0.2D);
        this.setBaseAttribute(SharedMonsterAttributes.attackDamage, 2.0D);
        this.setBaseAttribute(SharedMonsterAttributes.followRange, 16.0D);
    }

    @Override
    protected String getLivingSound() {
        if (this.isPlayingDead()) {
            return null;
        }
        return this.isInWater() ? "riftflux:axolotl_idle_water" : "riftflux:axolotl_idle_air";
    }

    @Override
    protected String getHurtSound() {
        return "riftflux:axolotl_hurt";
    }

    @Override
    protected String getDeathSound() {
        return "riftflux:axolotl_death";
    }

    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    protected float getSoundPitch() {
        return this.isChild() ? 1.2F : 1.0F;
    }

    @Override
    protected Item getDropItem() {
        return null;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected boolean isMovementBlocked() {
        return super.isMovementBlocked() || (this.isSitting() && !this.isInWater()) || this.isPlayingDead();
    }

    @Override
    public void setJumping(boolean jumping) {
        if (this.isSitting() && !this.isInWater()) {
            super.setJumping(false);
            return;
        }
        super.setJumping(jumping);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected void jump() {
        if (this.isSitting() && !this.isInWater()) {
            this.isJumping = false;
            return;
        }
        super.jump();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.setAir(TOTAL_AIR_SUPPLY);

        if (this.isSitting() && this.isInWater()) {
            this.aiSit.setSitting(false);
            this.setSitting(false);
        }

        if (this.isSitting()) {
            this.isJumping = false;
            this.getNavigator().clearPathEntity();
            this.entityToAttack = null;
            this.setAttackTarget(null);
            this.swimBurstTicks = 0;
            if (this.onGround) {
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }
        }

        if (this.playingDeadTicks > 0) {
            --this.playingDeadTicks;
            if (this.playingDeadTicks <= 0) {
                this.setPlayingDead(false);
            } else {
                this.motionX *= 0.8D;
                this.motionY *= 0.8D;
                this.motionZ *= 0.8D;
            }
        }

        if (this.isInWater() && !this.isSitting() && !this.isPlayingDead() && !this.worldObj.isRemote) {
            this.updateWaterSwimming();
        } else if (!this.isInWater()) {
            this.swimBurstTicks = 0;
            this.swimAvoidTicks = 0;
        }

        if (this.worldObj.isRemote && this.isInWater() && !this.isSitting() && !this.isPlayingDead()) {
            this.spawnClientSwimTrailIfNeeded();
        }

        if (!this.isSitting() && !this.isInWater() && this.onGround && this.rand.nextInt(20) == 0) {
            this.motionX += (this.rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
            this.motionY += 0.3D;
            this.motionZ += (this.rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
            this.isAirBorne = true;
        }
    }

    @Override
    protected void updateAITick() {
        if (this.isPlayingDead()) {
            this.entityToAttack = null;
            this.setAttackTarget(null);
            return;
        }
        if (this.isSitting()) {
            this.isJumping = false;
            this.entityToAttack = null;
            this.setAttackTarget(null);
            this.getNavigator().clearPathEntity();
            return;
        }
        super.updateAITick();
    }

    @Override
    public void moveEntityWithHeading(float strafe, float forward) {
        if (this.isSitting() && this.onGround && !this.isInWater()) {
            super.moveEntityWithHeading(0.0F, 0.0F);
            return;
        }

        if (this.isInWater()) {
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.92D;
            this.motionY *= 0.88D;
            this.motionZ *= 0.92D;
        } else {
            super.moveEntityWithHeading(strafe, forward);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        this.playSound("riftflux:axolotl_attack", 0.5F, this.getSoundPitch());
        return super.attackEntityAsMob(entity);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean attacked = super.attackEntityFrom(source, amount);
        if (!attacked || this.worldObj.isRemote) {
            return attacked;
        }

        if (!this.isPlayingDead()
                && !this.isChild()
                && this.getHealth() > 0.0F
                && this.getHealth() <= this.getMaxHealth() * 0.5F
                && this.rand.nextInt(3) == 0
                && source.getEntity() instanceof EntityLivingBase) {
            this.playingDeadTicks = 100;
            this.setPlayingDead(true);
            this.entityToAttack = null;
            this.setAttackTarget(null);
        }

        return true;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack held = player.inventory.getCurrentItem();
        if (held == null && this.isTamed() && this.func_152114_e(player)) {
            if (this.worldObj.isRemote) {
                return true;
            }
            boolean shouldSit = !this.isSitting();
            this.aiSit.setSitting(shouldSit);
            this.setSitting(shouldSit);
            this.isJumping = false;
            if (shouldSit && this.onGround && !this.isInWater()) {
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }
            this.setPathToEntity(null);
            this.entityToAttack = null;
            this.setAttackTarget(null);
            return true;
        }

        if (held != null && (held.getItem() == Items.water_bucket || held.getItem() == Items.bucket)) {
            return ItemAxolotlBucket.captureAxolotl(held, player, this);
        }
        if (held != null && AxolotlFishUtil.isFish(held)) {
            if (!this.isTamed()) {
                if (!player.capabilities.isCreativeMode) {
                    --held.stackSize;
                    if (held.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }
                }
                if (!this.worldObj.isRemote) {
                    if (this.rand.nextInt(3) == 0) {
                        this.setTamed(true);
                        this.func_152115_b(player.getUniqueID().toString());
                        this.setHealth(this.getMaxHealth());
                        this.setPathToEntity(null);
                        this.setAttackTarget(null);
                        this.entityToAttack = null;
                        this.aiSit.setSitting(false);
                        this.setSitting(false);
                        this.playTameEffect(true);
                        this.worldObj.setEntityState(this, (byte) 7);
                    } else {
                        this.playTameEffect(false);
                        this.worldObj.setEntityState(this, (byte) 6);
                    }
                }
                return true;
            }

            if (this.func_152114_e(player) && this.getHealth() < this.getMaxHealth()) {
                if (!player.capabilities.isCreativeMode) {
                    --held.stackSize;
                    if (held.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }
                }
                this.heal(4.0F);
                return true;
            }
        }
        return super.interact(player);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        IEntityLivingData spawnData = super.onSpawnWithEgg(data);
        AxolotlSpawnData axolotlData;
        if (spawnData instanceof AxolotlSpawnData) {
            axolotlData = (AxolotlSpawnData) spawnData;
        } else {
            axolotlData = new AxolotlSpawnData(AxolotlVariant.getRandomVariant(this.rand));
            spawnData = axolotlData;
        }
        this.setVariant(axolotlData.variant);
        return spawnData;
    }

    @Override
    public EntityAxolotl createChild(EntityAgeable mate) {
        EntityAxolotl child = new EntityAxolotl(this.worldObj);
        EntityAxolotl other = mate instanceof EntityAxolotl ? (EntityAxolotl) mate : this;
        child.setVariant(this.rand.nextBoolean() ? this.getVariant() : other.getVariant());
        return child;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return AxolotlFishUtil.isFish(stack);
    }

    @Override
    public boolean getCanSpawnHere() {
        if (!ModConfig.enableAxolotlModule || !ModConfig.enableAxolotlNaturalSpawning) {
            return false;
        }
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);

        if (!this.isInWater()) {
            return false;
        }
        if (this.posY > 63.0D) {
            return false;
        }
        if (!AxolotlContent.isSpawnBiome(this.worldObj.getBiomeGenForCoords(x, z))) {
            return false;
        }
        return this.hasSpawnableFloor(x, y, z) && this.worldObj.checkNoEntityCollision(this.boundingBox);
    }

    private boolean hasSpawnableFloor(int x, int y, int z) {
        for (int depth = 0; depth < 8; depth++) {
            Block block = this.worldObj.getBlock(x, y - depth, z);
            Material material = block.getMaterial();
            if (material == Material.water) {
                continue;
            }
            return block == Blocks.clay || block == Blocks.stone || block == Blocks.gravel;
        }
        return false;
    }

    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        Block block = this.worldObj.getBlock(x, y, z);
        if (block.getMaterial() == Material.water) {
            Block below = this.worldObj.getBlock(x, y - 1, z);
            return below == Blocks.clay ? 20.0F : 10.0F;
        }
        return this.worldObj.getLightBrightness(x, y, z) - 0.5F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setByte("AxolotlVariant", (byte) this.getVariant().getId());
        tag.setBoolean("FromBucket", this.isFromBucket());
        tag.setInteger("PlayDeadTicks", this.playingDeadTicks);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setVariant(AxolotlVariant.byId(tag.getByte("AxolotlVariant")));
        this.setFromBucket(tag.getBoolean("FromBucket"));
        this.playingDeadTicks = Math.max(0, tag.getInteger("PlayDeadTicks"));
        this.setPlayingDead(this.playingDeadTicks > 0);
    }

    public AxolotlVariant getVariant() {
        return AxolotlVariant.byId(this.getPackedState() & VARIANT_MASK);
    }

    public void setVariant(AxolotlVariant variant) {
        int state = this.getPackedState();
        state = (state & ~VARIANT_MASK) | (variant.getId() & VARIANT_MASK);
        this.setPackedState(state);
    }

    public boolean isFromBucket() {
        return (this.getPackedState() & FROM_BUCKET_FLAG) != 0;
    }

    public void setFromBucket(boolean fromBucket) {
        int state = this.getPackedState();
        if (fromBucket) {
            state |= FROM_BUCKET_FLAG;
        } else {
            state &= ~FROM_BUCKET_FLAG;
        }
        this.setPackedState(state);
    }

    public boolean isPlayingDead() {
        return (this.getPackedState() & PLAYING_DEAD_FLAG) != 0;
    }

    public void setPlayingDead(boolean playingDead) {
        int state = this.getPackedState();
        if (playingDead) {
            state |= PLAYING_DEAD_FLAG;
        } else {
            state &= ~PLAYING_DEAD_FLAG;
        }
        this.setPackedState(state);
    }

    private int getPackedState() {
        return this.dataWatcher.getWatchableObjectInt(getStateWatcherId());
    }

    private void setPackedState(int state) {
        this.dataWatcher.updateObject(getStateWatcherId(), Integer.valueOf(state));
    }

    private static int getStateWatcherId() {
        int id = ModConfig.axolotlStateDatawatcherId;
        if (id >= DEFAULT_STATE_WATCHER && ModConfig.isValidEntityDatawatcherId(id)) {
            return id;
        }
        return DEFAULT_STATE_WATCHER;
    }

    private void updateWaterSwimming() {
        this.updateSwimBurst();
        if (this.swimAvoidTicks > 0) {
            --this.swimAvoidTicks;
        }

        boolean obstacleAhead = this.isCollidedHorizontally
                || this.isWaterBlockedAhead()
                || this.isWaterBlockedAheadOffset(0.32D)
                || this.isWaterBlockedAheadOffset(-0.32D);
        if (obstacleAhead && this.swimAvoidTicks <= 0) {
            this.avoidWaterObstacle();
        }

        Vec3 target = obstacleAhead || this.swimAvoidTicks > 0 ? null : this.getWaterSwimTarget();
        this.isJumping = false;

        if (target != null) {
            this.swimCruiseTicks = 0;
            this.glideToward(target, this.getAttackTarget() != null ? 0.11D : 0.09D, 10.0F);
        } else {
            this.updateIdleSwimming();
        }

        this.keepWithinWaterColumn();
        double horizontalSpeed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        double maxHorizontalSpeed = this.getMaxHorizontalWaterSpeed();
        if (horizontalSpeed > maxHorizontalSpeed) {
            double scale = maxHorizontalSpeed / horizontalSpeed;
            this.motionX *= scale;
            this.motionZ *= scale;
        }
        this.motionY = Math.max(-0.035D, Math.min(0.035D, this.motionY));
    }

    private Vec3 getWaterSwimTarget() {
        EntityLivingBase attackTarget = this.getAttackTarget();
        if (attackTarget != null && attackTarget.isEntityAlive()) {
            return Vec3.createVectorHelper(
                    attackTarget.posX,
                    attackTarget.posY + (double)(attackTarget.height * 0.4F),
                    attackTarget.posZ
            );
        }

        if (!this.getNavigator().noPath()) {
            PathEntity path = this.getNavigator().getPath();
            if (path != null) {
                return path.getPosition(this);
            }
        }

        return null;
    }

    private void glideToward(Vec3 target, double desiredSpeed, float maxTurn) {
        double dx = target.xCoord - this.posX;
        double dy = target.yCoord - (this.posY + (double)(this.height * 0.35F));
        double dz = target.zCoord - this.posZ;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        if (horizontalDistance < 1.0E-4D && Math.abs(dy) < 1.0E-4D) {
            return;
        }

        if (horizontalDistance > 1.0E-4D) {
            float targetYaw = (float)(Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
            this.applyWaterCruise(targetYaw, desiredSpeed, dy * 0.09D, maxTurn);
        } else {
            this.applyWaterCruise(this.rotationYaw, desiredSpeed * 0.65D, dy * 0.09D, maxTurn);
        }
    }

    private void updateIdleSwimming() {
        if (this.swimCruiseTicks-- <= 0 || this.isCollidedHorizontally) {
            this.pickNewSwimCruise();
        }

        double bodyY = this.posY + (double)(this.height * 0.35F);
        double desiredY = (this.swimCruiseTargetY - bodyY) * 0.06D;
        float targetYaw = this.rotationYaw + this.swimCruiseTurnRate;
        this.applyWaterCruise(targetYaw, this.swimCruiseSpeed, desiredY, 6.0F);
    }

    private void pickNewSwimCruise() {
        if (this.swimCircleChainCount > 0) {
            --this.swimCircleChainCount;
            this.swimCruiseTicks = 36 + this.rand.nextInt(12);
            this.swimCruiseTurnRate = this.swimCircleDirection * (6.2F + this.rand.nextFloat() * 2.0F);
            this.swimCruiseSpeed = 0.12D + this.rand.nextDouble() * 0.03D;
            this.swimCruiseTargetY = this.posY + (double)((this.rand.nextFloat() - 0.5F) * 0.8F);
            return;
        }

        if (this.rand.nextInt(3) == 0) {
            this.swimCircleDirection = this.rand.nextBoolean() ? 1.0F : -1.0F;
            this.swimCircleChainCount = 2 + this.rand.nextInt(4);
            this.swimCruiseTicks = 36 + this.rand.nextInt(12);
            this.swimCruiseTurnRate = this.swimCircleDirection * (6.2F + this.rand.nextFloat() * 2.0F);
            this.swimCruiseSpeed = 0.12D + this.rand.nextDouble() * 0.03D;
            this.swimCruiseTargetY = this.posY + (double)((this.rand.nextFloat() - 0.5F) * 0.8F);
            return;
        }

        this.swimCruiseTicks = 70 + this.rand.nextInt(90);
        this.swimCruiseTurnRate = (this.rand.nextFloat() * 1.1F + 0.8F) * (this.rand.nextBoolean() ? 1.0F : -1.0F);
        this.swimCruiseSpeed = 0.055D + this.rand.nextDouble() * 0.025D;
        this.swimCruiseTargetY = this.posY + (double)((this.rand.nextFloat() - 0.5F) * 1.5F);
    }

    private void applyWaterCruise(float targetYaw, double desiredSpeed, double desiredVerticalSpeed, float maxTurn) {
        this.rotationYaw = this.limitAngle(this.rotationYaw, targetYaw, maxTurn);
        this.renderYawOffset = this.rotationYaw;
        this.rotationYawHead = this.rotationYaw;

        desiredSpeed += this.getSwimBurstSpeedBonus();

        float yawRadians = this.rotationYaw * 0.017453292F;
        double targetMotionX = (double)(-MathHelper.sin(yawRadians)) * desiredSpeed;
        double targetMotionZ = (double)MathHelper.cos(yawRadians) * desiredSpeed;
        double targetMotionY = Math.max(-0.03D, Math.min(0.03D, desiredVerticalSpeed));

        this.motionX += (targetMotionX - this.motionX) * 0.18D;
        this.motionY += (targetMotionY - this.motionY) * 0.12D;
        this.motionZ += (targetMotionZ - this.motionZ) * 0.18D;
    }

    private void updateSwimBurst() {
        if (this.swimBurstTicks > 0) {
            --this.swimBurstTicks;
        } else if (this.swimBurstCooldown > 0) {
            --this.swimBurstCooldown;
        }

        if (this.swimBurstTicks > 0 || this.swimBurstCooldown > 0 || this.isCollidedHorizontally) {
            return;
        }

        double horizontalSpeed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (horizontalSpeed < 0.025D) {
            return;
        }

        if (this.rand.nextInt(18) == 0) {
            this.swimBurstTicks = 42 + this.rand.nextInt(22);
            this.swimBurstCooldown = 20 + this.rand.nextInt(24);
        }
    }

    private double getSwimBurstSpeedBonus() {
        return this.swimBurstTicks > 0 ? 0.14D : 0.0D;
    }

    private void spawnClientSwimTrailIfNeeded() {
        if (this.worldObj == null || !this.worldObj.isRemote) {
            return;
        }

        double horizontalSpeed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (horizontalSpeed < 0.085D) {
            return;
        }

        double dirX = this.motionX;
        double dirZ = this.motionZ;
        double dirLength = Math.sqrt(dirX * dirX + dirZ * dirZ);
        if (dirLength < 1.0E-4D) {
            return;
        }

        dirX /= dirLength;
        dirZ /= dirLength;
        this.spawnSwimBubbles(dirX, dirZ, horizontalSpeed > 0.14D ? 6 : 5);
    }

    private void spawnSwimBubbles(double forwardX, double forwardZ, int count) {
        if (this.worldObj == null) {
            return;
        }

        for (int i = 0; i < count; i++) {
            double side = (this.rand.nextDouble() - 0.5D) * 0.25D;
            double x = this.posX - forwardX * 0.35D + forwardZ * side;
            double y = this.posY + 0.25D + this.rand.nextDouble() * 0.2D;
            double z = this.posZ - forwardZ * 0.35D - forwardX * side;
            double velocityX = -forwardX * 0.035D + this.motionX * 0.15D;
            double velocityY = 0.025D + this.motionY * 0.1D;
            double velocityZ = -forwardZ * 0.035D + this.motionZ * 0.15D;
            this.worldObj.spawnParticle("bubble", x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    private boolean isWaterBlockedAhead() {
        return this.isWaterBlockedAheadOffset(0.0D);
    }

    private boolean isWaterBlockedAheadOffset(double sideOffset) {
        float yawRadians = this.rotationYaw * 0.017453292F;
        double perpX = (double)MathHelper.cos(yawRadians) * sideOffset;
        double perpZ = (double)MathHelper.sin(yawRadians) * sideOffset;
        int x = MathHelper.floor_double(this.posX - (double)(MathHelper.sin(yawRadians) * 0.85F) + perpX);
        int y = MathHelper.floor_double(this.posY + (double)(this.height * 0.35F));
        int z = MathHelper.floor_double(this.posZ + (double)(MathHelper.cos(yawRadians) * 0.85F) + perpZ);
        Material front = this.worldObj.getBlock(x, y, z).getMaterial();
        Material frontHigh = this.worldObj.getBlock(x, y + 1, z).getMaterial();
        return front != Material.water && frontHigh != Material.water;
    }

    private void avoidWaterObstacle() {
        this.swimBurstTicks = 0;
        this.swimBurstCooldown = Math.max(this.swimBurstCooldown, 18);
        this.swimAvoidTicks = 30 + this.rand.nextInt(20);
        this.getNavigator().clearPathEntity();
        this.swimCircleChainCount = 0;

        float turnDirection = this.chooseEscapeTurnDirection();
        this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw + turnDirection * (85.0F + this.rand.nextFloat() * 35.0F));
        this.renderYawOffset = this.rotationYaw;
        this.rotationYawHead = this.rotationYaw;

        float yawRadians = this.rotationYaw * 0.017453292F;
        double escapeSpeed = 0.13D + this.rand.nextDouble() * 0.035D;
        this.motionX = (double)(-MathHelper.sin(yawRadians)) * escapeSpeed;
        this.motionZ = (double)MathHelper.cos(yawRadians) * escapeSpeed;
        this.motionY = Math.max(this.motionY, 0.03D);
        this.swimCruiseTicks = 34 + this.rand.nextInt(18);
        this.swimCruiseTurnRate = turnDirection * (6.0F + this.rand.nextFloat() * 2.2F);
        this.swimCruiseSpeed = 0.12D + this.rand.nextDouble() * 0.03D;
        this.swimCruiseTargetY = this.posY + 0.3D + this.rand.nextDouble() * 0.8D;
    }

    private double getMaxHorizontalWaterSpeed() {
        if (this.swimBurstTicks > 0) {
            return 0.245D;
        }
        if (Math.abs(this.swimCruiseTurnRate) >= 6.0F || this.swimCircleChainCount > 0) {
            return 0.17D;
        }
        return 0.13D;
    }

    private float chooseEscapeTurnDirection() {
        boolean leftBlocked = this.isWaterBlockedAheadOffset(0.38D);
        boolean rightBlocked = this.isWaterBlockedAheadOffset(-0.38D);
        if (leftBlocked != rightBlocked) {
            return leftBlocked ? -1.0F : 1.0F;
        }
        return this.rand.nextBoolean() ? 1.0F : -1.0F;
    }

    private void keepWithinWaterColumn() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY + (double)(this.height * 0.35F));
        int z = MathHelper.floor_double(this.posZ);
        Material below = this.worldObj.getBlock(x, y - 1, z).getMaterial();
        Material above = this.worldObj.getBlock(x, y + 1, z).getMaterial();

        if (below != Material.water) {
            this.motionY += 0.02D;
            this.swimCruiseTargetY = Math.max(this.swimCruiseTargetY, this.posY + 0.5D);
        }
        if (above != Material.water && this.motionY > 0.0D) {
            this.motionY *= 0.65D;
            this.swimCruiseTargetY = Math.min(this.swimCruiseTargetY, this.posY);
        }
        if (this.isCollidedHorizontally) {
            this.motionY += 0.01D;
        }
    }

    private float limitAngle(float current, float target, float maxChange) {
        float delta = MathHelper.wrapAngleTo180_float(target - current);
        if (delta > maxChange) {
            delta = maxChange;
        }
        if (delta < -maxChange) {
            delta = -maxChange;
        }
        return current + delta;
    }

    private void setBaseAttribute(IAttribute attribute, double baseValue) {
        if (attribute == null) {
            return;
        }
        IAttributeInstance instance = this.getEntityAttribute(attribute);
        if (instance == null) {
            try {
                this.getAttributeMap().registerAttribute(attribute);
            } catch (IllegalArgumentException ignored) {
                // Another path may have already registered it.
            }
            instance = this.getEntityAttribute(attribute);
        }
        if (instance != null) {
            instance.setBaseValue(baseValue);
        }
    }

    private static final class AxolotlSpawnData implements IEntityLivingData {
        private final AxolotlVariant variant;

        private AxolotlSpawnData(AxolotlVariant variant) {
            this.variant = variant;
        }
    }
}
