package com.voidsrift.riftflux.duckling;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntitySootSprite extends EntityTameable implements IAnimatable, IEntityAdditionalSpawnData {
    private static final int HIDING_WATCHER = 18;
    private static final int SCARED_WATCHER = 19;
    private static final int HIDING_COOLDOWN_TICKS = 60;
    private static final int NEIGHBOR_HIDING_COOLDOWN_TICKS = 40;

    private final AnimationFactory animationFactory = new AnimationFactory(this);
    private final EntityAISootAvoidPlayer avoidPlayerTask;
    private int idleVariant;
    private int idleTimer;
    private int hidingCooldown;
    private int scaredCooldown;
    private int hideIntentTicks;
    private int stackRideTicks;
    private int restackCooldownTicks;
    private transient String activeAnimationName = "";
    private transient String pendingAnimationName = "";
    private transient int pendingAnimationTicks;
    private transient int lastAnimationDecisionTick = Integer.MIN_VALUE;
    private transient int movingAnimationGraceTicks;

    public EntitySootSprite(World world) {
        super(world);
        this.setSize(0.45F, 0.6F);
        this.stepHeight = 1.0F;
        this.getNavigator().setAvoidsWater(true);
        this.setEquipmentDropChance(0, 0.0F);

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.avoidPlayerTask = new EntityAISootAvoidPlayer(this, 10.0F, 0.66D, 0.88D);
        this.tasks.addTask(1, this.avoidPlayerTask);
        this.tasks.addTask(2, new EntityAITempt(this, 0.715D, DucklingContent.starCandy, false));
        this.tasks.addTask(2, new EntityAISootScavenge(this, 0.825D));
        this.tasks.addTask(2, new EntityAISootDepositFuel(this, 0.55D));
        this.tasks.addTask(2, new EntityAISootStoreItem(this, 0.55D));
        this.tasks.addTask(2, new EntityAISootPressButton(this, 0.55D));
        this.tasks.addTask(3, new EntityAISootSeekDarkness(this, 0.275D));
        this.tasks.addTask(4, new EntityAIWander(this, 0.198D));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.tasks.addTask(7, new EntityAISootStackUp(this));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(HIDING_WATCHER, (byte)0);
        this.dataWatcher.addObject(SCARED_WATCHER, (byte)0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Math.max(1.0D, ModConfig.ghibliSootSpriteMaxHealth));
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.198D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(20.0D);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public EntityAgeable createChild(EntityAgeable mate) {
        return null;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        IEntityLivingData result = super.onSpawnWithEgg(data);
        this.setHealth(this.getMaxHealth());
        return result;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.worldObj.isRemote) {
            this.updateClientIdleVariant();
            return;
        }

        if (this.restackCooldownTicks > 0) {
            --this.restackCooldownTicks;
        }
        this.updateStackingState();
        this.updateHidingState();
        if (this.isHiding()) {
            this.getNavigator().clearPathEntity();
            this.motionX = 0.0D;
            this.motionZ = 0.0D;
        }

        boolean scared = this.avoidPlayerTask.isRunning();
        if (scared) {
            this.scaredCooldown = 10;
        } else if (this.isScared() && this.scaredCooldown > 0) {
            --this.scaredCooldown;
            scared = true;
        }
        if (this.isScared() != scared) {
            this.setScared(scared);
        }

        ItemStack held = this.getHeldItem();
        if (this.shouldConsumeHeldHealingItem(held)) {
            this.consumeHeldHealingItem(held);
        }

        if (this.isHiding() && this.ticksExisted % 5 == 0) {
            this.hideNearbySprites();
        }

        if (this.getHeldItem() != null && this.isMovingHorizontally(1.0E-4D)) {
            this.worldObj.spawnParticle("smoke",
                    this.posX + (this.rand.nextDouble() - 0.5D) * 0.5D,
                    this.posY + 0.5D,
                    this.posZ + (this.rand.nextDouble() - 0.5D) * 0.5D,
                    0.0D, 0.0D, 0.0D);
        }

        if (this.isScared() && this.ticksExisted % 2 == 0) {
            this.worldObj.spawnParticle("blockcrack_" + net.minecraft.block.Block.getIdFromBlock(Blocks.coal_block) + "_0",
                    this.posX,
                    this.posY + 0.1D,
                    this.posZ,
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack held = player.inventory.getCurrentItem();
        ItemStack spriteHeld = this.getHeldItem();
        if (player.isSneaking() && spriteHeld != null) {
            if (!this.worldObj.isRemote) {
                ItemStack returned = spriteHeld.copy();
                this.setCurrentItemOrArmor(0, null);
                this.giveOrDrop(player, returned);
                this.playSound("random.pop", 0.5F, 1.5F);
                this.setScared(false);
            }
            return true;
        }

        if (held != null && held.getItem() == DucklingContent.starCandy && !this.isTamed()) {
            if (!player.capabilities.isCreativeMode) {
                --held.stackSize;
                if (held.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }

            this.playSound("random.eat", 1.0F, 1.0F);
            if (!this.worldObj.isRemote) {
                this.setTamed(true);
                this.func_152115_b(player.getUniqueID().toString());
                this.getNavigator().clearPathEntity();
                this.setHiding(false);
                this.setScared(false);
                this.worldObj.setEntityState(this, (byte)7);
                this.playSound("random.levelup", 0.5F, 2.0F);
            }
            return true;
        }

        if (held != null && this.getHealth() < this.getMaxHealth() && SootSpriteHealingItemMatcher.matches(held)) {
            if (!player.capabilities.isCreativeMode) {
                --held.stackSize;
                if (held.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
            this.playSound("random.eat", 1.0F, 1.0F);
            if (!this.worldObj.isRemote) {
                this.heal(this.getHealingAmount(held));
                this.setHiding(false);
                this.setScared(false);
                this.worldObj.setEntityState(this, (byte)18);
            }
            return true;
        }

        if (held != null && held.getItem() == Items.glass_bottle) {
            if (!this.worldObj.isRemote) {
                ItemStack jar = ItemSootJar.createFilledJar(this);
                if (!player.capabilities.isCreativeMode) {
                    --held.stackSize;
                    if (held.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }
                }
                this.giveOrDrop(player, jar);
                this.playSound("random.pop", 1.0F, 1.0F);
                this.setDead();
            }
            return true;
        }

        return super.interact(player);
    }

    @Override
    public void handleHealthUpdate(byte state) {
        if (state == 18) {
            this.spawnStatusParticles("happyVillager");
        } else {
            super.handleHealthUpdate(state);
        }
    }

    @Override
    protected String getLivingSound() {
        if (ModConfig.ghibliSootSpriteChirpIntervalTicks <= 0) {
            return null;
        }
        return "mob.bat.idle";
    }

    @Override
    public int getTalkInterval() {
        return ModConfig.ghibliSootSpriteChirpIntervalTicks <= 0 ? Integer.MAX_VALUE : ModConfig.ghibliSootSpriteChirpIntervalTicks;
    }

    @Override
    protected String getHurtSound() {
        return "mob.bat.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.bat.death";
    }

    @Override
    protected float getSoundVolume() {
        return 0.35F;
    }

    @Override
    public boolean getCanSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return ModConfig.enableDucklingModule
                && ModConfig.enableDucklingNaturalSpawning
                && ModConfig.ghibliSootSpriteNaturalSpawning
                && this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL
                && this.worldObj.getBlockLightValue(x, y, z) <= 7
                && super.getCanSpawnHere();
    }

    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        return 1.0F - this.worldObj.getLightBrightness(x, y, z);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 8;
    }

    @Override
    public boolean allowLeashing() {
        return !this.getLeashed();
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (entity instanceof EntitySootSprite) {
            return;
        }
        super.applyEntityCollision(entity);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int coalCount = this.rand.nextInt(2);
        if (looting > 0) {
            coalCount += this.rand.nextInt(looting + 1);
        }
        for (int i = 0; i < coalCount; i++) {
            this.dropItem(Items.coal, 1);
        }
        if (this.rand.nextInt(20) == 0) {
            this.dropItem(DucklingContent.starCandy, 1);
        }
        ItemStack held = this.getHeldItem();
        if (held != null) {
            this.entityDropItem(held.copy(), 0.0F);
            this.setCurrentItemOrArmor(0, null);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean("Hiding", this.isHiding());
        tag.setBoolean("Scared", this.isScared());
        tag.setInteger("HideIntentTicks", this.hideIntentTicks);
        tag.setInteger("StackRideTicks", this.stackRideTicks);
        tag.setInteger("RestackCooldown", this.restackCooldownTicks);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setHiding(tag.getBoolean("Hiding"));
        this.setScared(tag.getBoolean("Scared"));
        this.hideIntentTicks = Math.max(0, tag.getInteger("HideIntentTicks"));
        this.stackRideTicks = Math.max(0, tag.getInteger("StackRideTicks"));
        this.restackCooldownTicks = Math.max(0, tag.getInteger("RestackCooldown"));
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeBoolean(this.isHiding());
        data.writeBoolean(this.isScared());
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        this.setHiding(data.readBoolean());
        this.setScared(data.readBoolean());
    }

    public void setHiding(boolean hiding) {
        this.dataWatcher.updateObject(HIDING_WATCHER, (byte)(hiding ? 1 : 0));
    }

    public boolean isHiding() {
        return this.dataWatcher.getWatchableObjectByte(HIDING_WATCHER) != 0;
    }

    public void setScared(boolean scared) {
        this.dataWatcher.updateObject(SCARED_WATCHER, (byte)(scared ? 1 : 0));
    }

    public boolean isScared() {
        return this.dataWatcher.getWatchableObjectByte(SCARED_WATCHER) != 0;
    }

    public int getIdleVariant() {
        return this.idleVariant;
    }

    public boolean canStartStacking() {
        return !this.isTamed()
                && this.restackCooldownTicks <= 0
                && this.getHeldItem() == null
                && !this.isSupportingAnotherSprite()
                && !this.isRiding();
    }

    public void beginStackRide() {
        this.stackRideTicks = this.getRandomStackRideDuration();
    }

    public boolean isSupportingAnotherSprite() {
        return this.riddenByEntity instanceof EntitySootSprite;
    }

    public boolean canAcceptItem(ItemStack stack) {
        if (stack == null || this.isSupportingAnotherSprite()) {
            return false;
        }
        ItemStack held = this.getHeldItem();
        if (held == null) {
            return true;
        }
        return areStacksMergeable(held, stack) && held.stackSize < held.getMaxStackSize();
    }

    public void performPickup(EntityItem itemEntity) {
        if (itemEntity == null || itemEntity.isDead) {
            return;
        }
        ItemStack itemStack = itemEntity.getEntityItem();
        if (itemStack == null || itemStack.stackSize <= 0 || !this.canAcceptItem(itemStack)) {
            return;
        }

        ItemStack held = this.getHeldItem();
        int pickedUp;
        if (held == null) {
            pickedUp = itemStack.stackSize;
            this.setCurrentItemOrArmor(0, itemStack.copy());
            itemEntity.setDead();
        } else {
            int space = Math.min(held.getMaxStackSize(), held.getItem().getItemStackLimit(held)) - held.stackSize;
            pickedUp = Math.min(space, itemStack.stackSize);
            if (pickedUp <= 0) {
                return;
            }
            held.stackSize += pickedUp;
            itemStack.stackSize -= pickedUp;
            this.setCurrentItemOrArmor(0, held);
            if (itemStack.stackSize <= 0) {
                itemEntity.setDead();
            } else {
                itemEntity.setEntityItemStack(itemStack);
            }
        }
        this.onItemPickup(itemEntity, pickedUp);
    }

    public static boolean areStacksMergeable(ItemStack left, ItemStack right) {
        return left != null
                && right != null
                && left.isItemEqual(right)
                && ItemStack.areItemStackTagsEqual(left, right);
    }

    private void updateClientIdleVariant() {
        boolean moving = this.isAnimationMoving(null);
        boolean canPlayIdleVariants = !moving
                && this.onGround
                && !this.isHiding()
                && !this.isScared()
                && this.getHeldItem() == null
                && !(this.riddenByEntity instanceof EntitySootSprite)
                && !(this.isRiding() && this.ridingEntity instanceof EntitySootSprite);
        if (!canPlayIdleVariants) {
            this.idleVariant = 0;
            this.idleTimer = 0;
            return;
        }

        if (this.idleTimer > 0) {
            --this.idleTimer;
            return;
        }

        if (this.rand.nextInt(3) == 0) {
            this.idleVariant = 1 + this.rand.nextInt(5);
            this.idleTimer = 30 + this.rand.nextInt(61);
        } else {
            this.idleVariant = 0;
            this.idleTimer = 80 + this.rand.nextInt(121);
        }
    }

    private void updateHidingState() {
        boolean currentlyHiding = this.isHiding();
        boolean shouldHide = currentlyHiding;
        if (!this.shouldIgnoreHiding()) {
            EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, 3.0D);
            if (player != null && !player.capabilities.isCreativeMode && !player.isSneaking()) {
                boolean canSee = player.canEntityBeSeen(this);
                Vec3 playerLook = player.getLookVec().normalize();
                Vec3 toSprite = Vec3.createVectorHelper(
                        this.posX - player.posX,
                        this.posY + this.height * 0.5D - (player.posY + player.getEyeHeight()),
                        this.posZ - player.posZ).normalize();
                double dot = playerLook.dotProduct(toSprite);
                boolean directlyObserved = canSee && dot > 0.85D;
                if (currentlyHiding) {
                    shouldHide = canSee && dot >= 0.72D;
                    this.hideIntentTicks = 0;
                } else if (directlyObserved) {
                    this.hideIntentTicks = Math.min(this.hideIntentTicks + 1, 10);
                    shouldHide = this.hideIntentTicks >= 2;
                } else {
                    this.hideIntentTicks = 0;
                }
            } else {
                this.hideIntentTicks = 0;
                shouldHide = false;
            }
        } else {
            this.hideIntentTicks = 0;
            shouldHide = false;
        }
        if (shouldHide) {
            this.hidingCooldown = HIDING_COOLDOWN_TICKS;
        } else if (currentlyHiding && this.hidingCooldown > 0) {
            --this.hidingCooldown;
            shouldHide = true;
        }
        if (currentlyHiding != shouldHide) {
            this.setHiding(shouldHide);
        }
    }

    private void hideNearbySprites() {
        java.util.List neighbors = this.worldObj.getEntitiesWithinAABB(EntitySootSprite.class, this.boundingBox.expand(0.5D, 0.5D, 0.5D));
        for (int i = 0; i < neighbors.size(); i++) {
            EntitySootSprite neighbor = (EntitySootSprite)neighbors.get(i);
            if (neighbor != this && !neighbor.shouldIgnoreHiding()) {
                boolean wasHiding = neighbor.isHiding();
                neighbor.forceHide(NEIGHBOR_HIDING_COOLDOWN_TICKS);
                if (wasHiding) {
                    continue;
                }
                neighbor.playSound("mob.bat.idle", 0.3F, 2.0F);
            }
        }
    }

    private void forceHide(int ticks) {
        if (this.shouldIgnoreHiding()) {
            this.hidingCooldown = 0;
            if (this.isHiding()) {
                this.setHiding(false);
            }
            return;
        }
        this.hidingCooldown = Math.max(this.hidingCooldown, ticks);
        if (!this.isHiding()) {
            this.setHiding(true);
        }
    }

    private boolean shouldIgnoreHiding() {
        return this.isTamed() || this.isTemptedByStarCandy();
    }

    private boolean isTemptedByStarCandy() {
        if (this.worldObj == null) {
            return false;
        }
        EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, 10.0D);
        if (player == null) {
            return false;
        }
        ItemStack held = player.getCurrentEquippedItem();
        return held != null && held.getItem() == DucklingContent.starCandy;
    }

    private void spawnStatusParticles(String particle) {
        for (int i = 0; i < 7; i++) {
            double vx = this.rand.nextGaussian() * 0.02D;
            double vy = this.rand.nextGaussian() * 0.02D;
            double vz = this.rand.nextGaussian() * 0.02D;
            this.worldObj.spawnParticle(particle,
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.posY + 0.5D + this.rand.nextFloat() * this.height,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    vx, vy, vz);
        }
    }

    private void updateStackingState() {
        if (this.isSupportingAnotherSprite() && this.getHeldItem() != null) {
            Entity rider = this.riddenByEntity;
            if (rider instanceof EntitySootSprite) {
                rider.mountEntity(null);
                ((EntitySootSprite) rider).restackCooldownTicks = this.getRandomRestackCooldown();
            }
        }

        if (!(this.ridingEntity instanceof EntitySootSprite)) {
            this.stackRideTicks = 0;
            return;
        }

        if (this.isMountedOnSpriteHoldingItems()) {
            this.mountEntity(null);
            this.restackCooldownTicks = this.getRandomRestackCooldown();
            return;
        }

        if (this.stackRideTicks <= 0) {
            this.stackRideTicks = this.getRandomStackRideDuration();
        }

        --this.stackRideTicks;
        if (this.stackRideTicks <= 0) {
            this.mountEntity(null);
            this.restackCooldownTicks = this.getRandomRestackCooldown();
        }
    }

    private int getRandomStackRideDuration() {
        return 120 + this.rand.nextInt(181);
    }

    private int getRandomRestackCooldown() {
        return 200 + this.rand.nextInt(201);
    }

    private boolean isMountedOnSpriteHoldingItems() {
        Entity current = this.ridingEntity;
        while (current instanceof EntitySootSprite) {
            EntitySootSprite sprite = (EntitySootSprite) current;
            if (sprite.getHeldItem() != null) {
                return true;
            }
            current = sprite.ridingEntity;
        }
        return false;
    }

    private boolean shouldConsumeHeldHealingItem(ItemStack held) {
        return held != null
                && this.getHealth() < this.getMaxHealth()
                && SootSpriteHealingItemMatcher.matches(held);
    }

    private void consumeHeldHealingItem(ItemStack held) {
        held.stackSize--;
        this.setCurrentItemOrArmor(0, held.stackSize > 0 ? held : null);
        this.playSound("random.eat", 1.0F, 1.5F);
        this.worldObj.setEntityState(this, (byte)18);
        this.heal(this.getHealingAmount(held));
        this.setHiding(false);
        this.setScared(false);
    }

    private float getHealingAmount(ItemStack stack) {
        if (stack != null && stack.getItem() == DucklingContent.starCandy) {
            return this.getMaxHealth();
        }
        return 2.0F;
    }

    private boolean isMovingHorizontally(double threshold) {
        return this.motionX * this.motionX + this.motionZ * this.motionZ > threshold;
    }

    private double getHorizontalTravelSq() {
        double dx = this.posX - this.prevPosX;
        double dz = this.posZ - this.prevPosZ;
        return dx * dx + dz * dz;
    }

    private <E extends IAnimatable> boolean isAnimationMoving(AnimationEvent<E> event) {
        boolean eventMoving = event != null && event.isMoving();
        boolean moving = eventMoving
                || this.limbSwingAmount > 0.02F
                || this.isMovingHorizontally(4.0E-4D)
                || this.getHorizontalTravelSq() > 4.0E-4D;
        if (moving) {
            this.movingAnimationGraceTicks = 3;
            return true;
        }
        if (this.movingAnimationGraceTicks > 0) {
            --this.movingAnimationGraceTicks;
            return true;
        }
        return false;
    }

    private String getStableAnimationName(String desiredAnimationName) {
        if (desiredAnimationName == null || desiredAnimationName.isEmpty()) {
            return this.activeAnimationName;
        }

        if (this.ticksExisted != this.lastAnimationDecisionTick) {
            this.lastAnimationDecisionTick = this.ticksExisted;
            if (!desiredAnimationName.equals(this.pendingAnimationName)) {
                this.pendingAnimationName = desiredAnimationName;
                this.pendingAnimationTicks = 1;
            } else {
                ++this.pendingAnimationTicks;
            }

            if (this.activeAnimationName.isEmpty()) {
                this.activeAnimationName = desiredAnimationName;
            } else if (!desiredAnimationName.equals(this.activeAnimationName) && this.pendingAnimationTicks >= 2) {
                this.activeAnimationName = desiredAnimationName;
            }
        }

        return this.activeAnimationName.isEmpty() ? desiredAnimationName : this.activeAnimationName;
    }

    private void giveOrDrop(EntityPlayer player, ItemStack stack) {
        if (stack == null) {
            return;
        }
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.entityDropItem(stack, 0.0F);
        }
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        String desiredAnimationName;
        boolean moving = this.isAnimationMoving(event);
        boolean hasItem = this.getHeldItem() != null;
        boolean isSupportingStack = this.riddenByEntity instanceof EntitySootSprite;
        boolean isTopOfStack = this.isRiding()
                && this.ridingEntity instanceof EntitySootSprite
                && !isSupportingStack;
        if (hasItem || isSupportingStack) {
            desiredAnimationName = moving ? "holdItemWalk" : "holdItem";
        } else if (this.isHiding()) {
            desiredAnimationName = "hide_hold";
        } else if (this.isScared() && moving) {
            desiredAnimationName = "scare";
        } else if (isTopOfStack) {
            desiredAnimationName = "idle";
        } else {
            if (moving) {
                desiredAnimationName = "walk";
            } else {
                switch (this.idleVariant) {
                    case 0:
                        desiredAnimationName = "idle";
                        break;
                    case 1:
                        desiredAnimationName = "idle2";
                        break;
                    case 2:
                        desiredAnimationName = "idle3";
                        break;
                    case 3:
                        desiredAnimationName = "idle4";
                        break;
                    case 4:
                        desiredAnimationName = "idle5";
                        break;
                    case 5:
                        desiredAnimationName = "idle6";
                        break;
                    default:
                        desiredAnimationName = "idle";
                        break;
                }
            }
        }
        String animationName = this.getStableAnimationName(desiredAnimationName);
        AnimationBuilder builder = new AnimationBuilder();
        builder.addAnimation(animationName, Boolean.TRUE);
        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<EntitySootSprite>(this, "controller", 8.0F, new AnimationController.IAnimationPredicate<EntitySootSprite>() {
            @Override
            public PlayState test(AnimationEvent<EntitySootSprite> event) {
                return EntitySootSprite.this.predicate(event);
            }
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.animationFactory;
    }
}
