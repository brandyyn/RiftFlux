package com.voidsrift.riftflux.duckling;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntityQuackling extends EntityAnimal implements IMerchant, IAnimatable, IEntitySyncData, IEntityAdditionalSpawnData {
    private static final float ADULT_WIDTH = 0.7F;
    private static final float ADULT_HEIGHT = 1.45F;
    private static final int DRIPPED_FLAG = 1;
    private static final int ACTIVE_FISHING_FLAG = 4;
    private static final int FISHING_START_CHECK_MIN_TICKS = 600;
    private static final int FISHING_START_CHECK_RANDOM_TICKS = 3000;
    private static final int FISHING_START_CHANCE_DENOMINATOR = 4;

    private final AnimationFactory animationFactory = new AnimationFactory(this);
    private EntityPlayer customer;
    private MerchantRecipeList recipes;
    private long tradeRecipeDay = Long.MIN_VALUE;
    private boolean fishingSessionTradeUsed;
    private long fishingDay = Long.MIN_VALUE;
    private int fishingSessionsToday;
    private int fishingSessionLimitToday = -1;
    private int nextFishingCheckTicks;
    private boolean fishingSessionActive;
    private boolean fishingSessionHasTarget;
    private int fishingSessionTargetX;
    private int fishingSessionTargetY;
    private int fishingSessionTargetZ;
    private int fishingSessionWaterX;
    private int fishingSessionWaterY;
    private int fishingSessionWaterZ;
    private int fishingSessionCatchTicks;
    private int fishingSessionTargetCatches;
    private int fishingSessionCatchesThisSession;
    private int fishingSessionLostTargetTicks;
    private int ducklingFlags;
    private transient String activeAnimationName = "";
    private transient int movingAnimationGraceTicks;

    public EntityQuackling(World world) {
        super(world);
        this.setSize(ADULT_WIDTH, ADULT_HEIGHT);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setCanSwim(true);
        this.getNavigator().setEnterDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.2D));
        this.tasks.addTask(2, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(3, new EntityAIQuacklingFishing(this));
        this.tasks.addTask(4, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(5, new EntityAIQuacklingTempt(this, 1.0D));
        this.tasks.addTask(6, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(7, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(8, new EntityAIQuacklingSwimWander(this, 1.0D));
        this.tasks.addTask(9, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F));
        this.tasks.addTask(11, new EntityAILookIdle(this));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Math.max(1.0D, ModConfig.ducklingQuacklingMaxHealth));
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        IEntityLivingData result = super.onSpawnWithEgg(data);
        this.setDripped(this.rand.nextBoolean());
        this.setHealth(this.getMaxHealth());
        return result;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack held = player.inventory.getCurrentItem();

        if (held != null && held.getItem() == Items.shears && this.isDripped()) {
            this.setDripped(false);
            if (!this.worldObj.isRemote) {
                this.entityDropItem(new ItemStack(Blocks.waterlily), 0.0F);
            }
            if (!player.capabilities.isCreativeMode) {
                held.damageItem(1, player);
            }
            return true;
        }

        if (super.interact(player)) {
            return true;
        }

        if (this.canOpenTrade(held)) {
            if (!this.worldObj.isRemote) {
                this.setCustomer(player);
                player.displayGUIMerchant(this, this.getCommandSenderName());
            }
            return true;
        }

        return false;
    }

    private boolean canOpenTrade(ItemStack held) {
        return this.isEntityAlive()
                && !this.isChild()
                && ModConfig.ducklingQuacklingTradingEnabled
                && ModConfig.hasQuacklingTradesConfigured()
                && !this.fishingSessionTradeUsed
                && (!ModConfig.ducklingQuacklingTradeOnlyWhileFishing || this.isFishing())
                && (held == null || held.getItem() != DucklingContent.quacklingSpawnEgg);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int feathers = 1 + this.rand.nextInt(2 + Math.max(0, looting));
        for (int i = 0; i < feathers; i++) {
            this.dropItem(Items.feather, 1);
        }
        if (this.isDripped()) {
            this.entityDropItem(new ItemStack(Blocks.waterlily), 0.0F);
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return DucklingItemMatcher.matchesQuacklingBreedItem(stack);
    }

    @Override
    public EntityQuackling createChild(EntityAgeable mate) {
        EntityQuackling child = new EntityQuackling(this.worldObj);
        child.setDripped(this.rand.nextBoolean());
        return child;
    }

    @Override
    protected String getLivingSound() {
        return DucklingContent.MODID + ":deep_quack";
    }

    @Override
    protected String getHurtSound() {
        return DucklingContent.MODID + ":quackling_death";
    }

    @Override
    protected String getDeathSound() {
        return DucklingContent.MODID + ":quackling_death";
    }

    @Override
    protected float getSoundVolume() {
        return 0.55F;
    }

    @Override
    public boolean getCanSpawnHere() {
        return ModConfig.enableDucklingModule
                && this.worldObj.checkNoEntityCollision(this.boundingBox)
                && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
                && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    @Override
    public boolean allowLeashing() {
        return !this.getLeashed();
    }

    @Override
    public void handleHealthUpdate(byte state) {
        if (state == 13) {
            this.spawnStatusParticles("smoke");
        } else if (state == 14) {
            this.spawnStatusParticles("happyVillager");
        } else {
            super.handleHealthUpdate(state);
        }
    }

    private void spawnStatusParticles(String particle) {
        for (int i = 0; i < 5; i++) {
            double vx = this.rand.nextGaussian() * 0.02D;
            double vy = this.rand.nextGaussian() * 0.02D;
            double vz = this.rand.nextGaussian() * 0.02D;
            this.worldObj.spawnParticle(particle, this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.posY + 1.0D + this.rand.nextFloat() * this.height,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    vx, vy, vz);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean("Dripped", this.isDripped());
        tag.setLong("FishingDay", this.fishingDay);
        tag.setInteger("FishingSessionsToday", this.fishingSessionsToday);
        tag.setInteger("FishingSessionLimitToday", this.fishingSessionLimitToday);
        tag.setInteger("NextFishingCheckTicks", this.nextFishingCheckTicks);
        tag.setBoolean("FishingSessionActive", this.fishingSessionActive);
        tag.setBoolean("FishingSessionHasTarget", this.fishingSessionHasTarget);
        tag.setInteger("FishingSessionTargetX", this.fishingSessionTargetX);
        tag.setInteger("FishingSessionTargetY", this.fishingSessionTargetY);
        tag.setInteger("FishingSessionTargetZ", this.fishingSessionTargetZ);
        tag.setInteger("FishingSessionWaterX", this.fishingSessionWaterX);
        tag.setInteger("FishingSessionWaterY", this.fishingSessionWaterY);
        tag.setInteger("FishingSessionWaterZ", this.fishingSessionWaterZ);
        tag.setInteger("FishingSessionCatchTicks", this.fishingSessionCatchTicks);
        tag.setInteger("FishingSessionTargetCatches", this.fishingSessionTargetCatches);
        tag.setInteger("FishingSessionCatchesThisSession", this.fishingSessionCatchesThisSession);
        tag.setInteger("FishingSessionLostTargetTicks", this.fishingSessionLostTargetTicks);
        tag.setBoolean("FishingSessionTradeUsed", this.fishingSessionTradeUsed);
        tag.setLong("TradeRecipeDay", this.tradeRecipeDay);
        if (this.recipes != null) {
            tag.setTag("Offers", this.recipes.getRecipiesAsTags());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setDripped(tag.getBoolean("Dripped"));
        this.fishingDay = tag.hasKey("FishingDay") ? tag.getLong("FishingDay") : Long.MIN_VALUE;
        this.fishingSessionsToday = tag.hasKey("FishingSessionsToday") ? Math.max(0, tag.getInteger("FishingSessionsToday")) : 0;
        this.fishingSessionLimitToday = tag.hasKey("FishingSessionLimitToday") ? Math.max(0, tag.getInteger("FishingSessionLimitToday")) : -1;
        this.nextFishingCheckTicks = tag.hasKey("NextFishingCheckTicks") ? Math.max(0, tag.getInteger("NextFishingCheckTicks")) : 0;
        this.fishingSessionActive = tag.getBoolean("FishingSessionActive");
        this.fishingSessionHasTarget = tag.getBoolean("FishingSessionHasTarget");
        this.fishingSessionTargetX = tag.getInteger("FishingSessionTargetX");
        this.fishingSessionTargetY = tag.getInteger("FishingSessionTargetY");
        this.fishingSessionTargetZ = tag.getInteger("FishingSessionTargetZ");
        this.fishingSessionWaterX = tag.getInteger("FishingSessionWaterX");
        this.fishingSessionWaterY = tag.getInteger("FishingSessionWaterY");
        this.fishingSessionWaterZ = tag.getInteger("FishingSessionWaterZ");
        this.fishingSessionCatchTicks = Math.max(1, tag.getInteger("FishingSessionCatchTicks"));
        this.fishingSessionTargetCatches = Math.max(0, tag.getInteger("FishingSessionTargetCatches"));
        this.fishingSessionCatchesThisSession = Math.max(0, tag.getInteger("FishingSessionCatchesThisSession"));
        this.fishingSessionLostTargetTicks = Math.max(0, tag.getInteger("FishingSessionLostTargetTicks"));
        this.fishingSessionTradeUsed = tag.getBoolean("FishingSessionTradeUsed");
        this.tradeRecipeDay = tag.hasKey("TradeRecipeDay") ? tag.getLong("TradeRecipeDay") : Long.MIN_VALUE;
        if (this.fishingSessionTargetCatches <= this.fishingSessionCatchesThisSession) {
            this.clearFishingSession();
        }
        this.setFishingActive(false);
        if (tag.hasKey("Offers", 10)) {
            this.recipes = new MerchantRecipeList(tag.getCompoundTag("Offers"));
        }
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeByte(this.ducklingFlags);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        this.ducklingFlags = data.readUnsignedByte();
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setInteger("DucklingFlags", this.ducklingFlags);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        if (tag.hasKey("DucklingFlags")) {
            this.ducklingFlags = tag.getInteger("DucklingFlags");
        }
    }

    public boolean isDripped() {
        return (this.ducklingFlags & DRIPPED_FLAG) != 0;
    }

    public void setDripped(boolean dripped) {
        this.setDucklingFlag(DRIPPED_FLAG, dripped);
    }

    public boolean isFishing() {
        return (this.ducklingFlags & ACTIVE_FISHING_FLAG) != 0;
    }

    public void setFishingActive(boolean fishing) {
        this.setDucklingFlag(ACTIVE_FISHING_FLAG, fishing);
    }

    public boolean shouldStartFishingSessionNow() {
        this.updateFishingDayState();
        if (this.worldObj == null
                || this.worldObj.isRemote
                || !ModConfig.enableDucklingModule
                || !this.isEntityAlive()
                || this.isChild()
                || this.customer != null
                || this.isFishing()
                || this.fishingSessionsToday >= this.fishingSessionLimitToday) {
            return false;
        }
        if (this.nextFishingCheckTicks > 0) {
            --this.nextFishingCheckTicks;
            return false;
        }
        this.nextFishingCheckTicks = this.nextFishingStartDelay();
        return this.rand.nextInt(FISHING_START_CHANCE_DENOMINATOR) == 0;
    }

    public void beginFishingSession() {
        this.updateFishingDayState();
        if (!this.worldObj.isRemote && this.fishingSessionsToday < this.fishingSessionLimitToday) {
            ++this.fishingSessionsToday;
        }
        this.fishingSessionTradeUsed = false;
        this.setFishingActive(false);
    }

    public void deferFishingStartCheck() {
        if (!this.worldObj.isRemote) {
            this.nextFishingCheckTicks = this.nextFishingStartDelay();
        }
    }

    public boolean shouldResumeFishingSession() {
        return !this.worldObj.isRemote
                && ModConfig.enableDucklingModule
                && this.fishingSessionActive
                && this.isEntityAlive()
                && !this.isChild()
                && this.fishingSessionTargetCatches > this.fishingSessionCatchesThisSession;
    }

    public void saveFishingSession(boolean hasTarget, int targetX, int targetY, int targetZ,
            int waterX, int waterY, int waterZ, int catchTicks, int targetCatches,
            int catchesThisSession, int lostTargetTicks) {
        this.fishingSessionActive = true;
        this.fishingSessionHasTarget = hasTarget;
        this.fishingSessionTargetX = targetX;
        this.fishingSessionTargetY = targetY;
        this.fishingSessionTargetZ = targetZ;
        this.fishingSessionWaterX = waterX;
        this.fishingSessionWaterY = waterY;
        this.fishingSessionWaterZ = waterZ;
        this.fishingSessionCatchTicks = Math.max(1, catchTicks);
        this.fishingSessionTargetCatches = Math.max(1, targetCatches);
        this.fishingSessionCatchesThisSession = Math.max(0, catchesThisSession);
        this.fishingSessionLostTargetTicks = Math.max(0, lostTargetTicks);
    }

    public void clearFishingSession() {
        this.fishingSessionActive = false;
        this.fishingSessionHasTarget = false;
        this.fishingSessionCatchTicks = 0;
        this.fishingSessionTargetCatches = 0;
        this.fishingSessionCatchesThisSession = 0;
        this.fishingSessionLostTargetTicks = 0;
    }

    public boolean savedFishingSessionHasTarget() {
        return this.fishingSessionHasTarget;
    }

    public int getSavedFishingTargetX() {
        return this.fishingSessionTargetX;
    }

    public int getSavedFishingTargetY() {
        return this.fishingSessionTargetY;
    }

    public int getSavedFishingTargetZ() {
        return this.fishingSessionTargetZ;
    }

    public int getSavedFishingWaterX() {
        return this.fishingSessionWaterX;
    }

    public int getSavedFishingWaterY() {
        return this.fishingSessionWaterY;
    }

    public int getSavedFishingWaterZ() {
        return this.fishingSessionWaterZ;
    }

    public int getSavedFishingCatchTicks() {
        return this.fishingSessionCatchTicks;
    }

    public int getSavedFishingTargetCatches() {
        return this.fishingSessionTargetCatches;
    }

    public int getSavedFishingCatchesThisSession() {
        return this.fishingSessionCatchesThisSession;
    }

    public int getSavedFishingLostTargetTicks() {
        return this.fishingSessionLostTargetTicks;
    }

    public int getRandomFishingCatchTarget() {
        int min = Math.max(1, ModConfig.getQuacklingFishingMinCatchesBeforeStop());
        int max = Math.max(min, ModConfig.getQuacklingFishingMaxCatchesBeforeStop());
        return min + this.rand.nextInt(max - min + 1);
    }

    private void updateFishingDayState() {
        if (this.worldObj == null) {
            return;
        }
        long day = this.worldObj.getWorldTime() / 24000L;
        if (day != this.fishingDay || this.fishingSessionLimitToday < 0) {
            this.fishingDay = day;
            this.fishingSessionsToday = 0;
            this.fishingSessionLimitToday = this.getRandomFishingSessionLimit();
            this.nextFishingCheckTicks = this.nextFishingStartDelay();
            this.fishingSessionTradeUsed = false;
        }
    }

    private int getRandomFishingSessionLimit() {
        int min = Math.max(0, ModConfig.getQuacklingFishingMinSessionsPerDay());
        int max = Math.max(min, ModConfig.getQuacklingFishingMaxSessionsPerDay());
        return min + this.rand.nextInt(max - min + 1);
    }

    private int nextFishingStartDelay() {
        return FISHING_START_CHECK_MIN_TICKS + this.rand.nextInt(FISHING_START_CHECK_RANDOM_TICKS + 1);
    }

    private void setDucklingFlag(int flag, boolean enabled) {
        int flags = this.ducklingFlags & 255;
        int updated = flags;
        if (enabled) {
            updated |= flag;
        } else {
            updated &= ~flag;
        }
        if (updated != flags) {
            this.ducklingFlags = updated;
            if (this.worldObj != null && !this.worldObj.isRemote) {
                EntitySyncHelper.sync(this);
            }
        }
    }

    @Override
    public void setCustomer(EntityPlayer player) {
        this.customer = player;
    }

    @Override
    public EntityPlayer getCustomer() {
        return this.customer;
    }

    @Override
    public MerchantRecipeList getRecipes(EntityPlayer player) {
        if (!ModConfig.ducklingQuacklingTradingEnabled || !ModConfig.hasQuacklingTradesConfigured()) {
            return new MerchantRecipeList();
        }
        if (this.fishingSessionTradeUsed) {
            return new MerchantRecipeList();
        }
        if (this.worldObj != null && this.worldObj.isRemote) {
            return this.recipes == null ? new MerchantRecipeList() : this.recipes;
        }
        this.refreshTradesIfNeeded();
        if (this.recipes == null) {
            this.recipes = DucklingTradeParser.buildRecipes(ModConfig.ducklingQuacklingTrades, this.rand);
            this.tradeRecipeDay = this.getCurrentWorldDay();
        }
        return this.recipes;
    }

    @Override
    public void setRecipes(MerchantRecipeList recipes) {
        this.recipes = recipes;
        if (recipes != null) {
            this.tradeRecipeDay = this.getCurrentWorldDay();
        }
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        if (recipe != null) {
            recipe.incrementToolUses();
        }
        if (!this.worldObj.isRemote) {
            this.fishingSessionTradeUsed = true;
            this.setFishingActive(false);
            this.clearFishingSession();
            if (this.customer != null) {
                this.customer.closeScreen();
                this.setCustomer(null);
            }
            this.worldObj.setEntityState(this, (byte)14);
        }
    }

    @Override
    public void func_110297_a_(ItemStack stack) {
    }

    private void refreshTradesIfNeeded() {
        if (!ModConfig.ducklingQuacklingRefreshTradesDaily || this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        long currentDay = this.getCurrentWorldDay();
        if (currentDay == Long.MIN_VALUE) {
            return;
        }
        if (this.tradeRecipeDay != currentDay) {
            this.tradeRecipeDay = currentDay;
            this.recipes = null;
        }
    }

    private long getCurrentWorldDay() {
        return this.worldObj == null ? Long.MIN_VALUE : this.worldObj.getWorldTime() / 24000L;
    }

    private boolean isAnimationMoving(AnimationEvent<?> event) {
        boolean moving = (event != null && event.isMoving())
                || this.limbSwingAmount > 0.015F
                || this.motionX * this.motionX + this.motionZ * this.motionZ > 1.0E-4D
                || this.getHorizontalTravelSq() > 1.0E-4D;
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

    private double getHorizontalTravelSq() {
        double dx = this.posX - this.prevPosX;
        double dz = this.posZ - this.prevPosZ;
        return dx * dx + dz * dz;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        String animationName;
        if (this.isFishing()) {
            animationName = "fishing";
        } else if (this.isAnimationMoving(event)) {
            animationName = "walking";
        } else {
            animationName = "idle";
        }
        this.activeAnimationName = animationName;
        AnimationBuilder builder = new AnimationBuilder();
        builder.addAnimation(animationName, Boolean.TRUE);
        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<EntityQuackling>(this, "controller", 10.0F, new AnimationController.IAnimationPredicate<EntityQuackling>() {
            @Override
            public PlayState test(AnimationEvent<EntityQuackling> event) {
                return EntityQuackling.this.predicate(event);
            }
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.animationFactory;
    }
}
