package com.voidsrift.riftflux.duckling;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import com.voidsrift.riftflux.util.ConfiguredNameMatcher;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntityDuck extends EntityAnimal implements IAnimatable, IEntitySyncData, IEntityAdditionalSpawnData {
    private final AnimationFactory animationFactory = new AnimationFactory(this);
    private int variantId = DuckVariant.PEKIN.getId();

    public int eggLayTime;

    public EntityDuck(World world) {
        super(world);
        this.setSize(0.6F, 0.8F);
        this.getNavigator().setAvoidsWater(false);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.4D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.wheat_seeds, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.eggLayTime = this.rand.nextInt(6000) + 6000;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        IEntityLivingData result = super.onSpawnWithEgg(data);
        this.setVariant(DuckVariant.randomNatural(this.rand));
        return result;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.onGround && this.motionY < 0.0D) {
            this.motionY *= 0.8D;
        }

        if (!this.worldObj.isRemote && this.isEntityAlive() && !this.isChild()) {
            --this.eggLayTime;
            if (this.eggLayTime <= 0) {
                this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                this.dropItem(DucklingContent.duckEgg, 1);
                this.eggLayTime = this.rand.nextInt(6000) + 6000;
            }
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack held = player.inventory.getCurrentItem();
        if (held == null && this.isEntityAlive()) {
            if (!this.worldObj.isRemote) {
                this.worldObj.setEntityState(this, (byte)7);
            }
            return true;
        }

        return super.interact(player);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack != null && stack.getItem() == Items.wheat_seeds;
    }

    @Override
    public EntityDuck createChild(EntityAgeable mate) {
        EntityDuck child = new EntityDuck(this.worldObj);
        DuckVariant inherited = null;
        if (mate instanceof EntityDuck) {
            DuckVariant other = ((EntityDuck) mate).getTextureVariant();
            DuckVariant current = this.getTextureVariant();
            if (other == current && current != DuckVariant.AGENTD) {
                inherited = current;
            }
        }
        child.setVariant(inherited != null ? inherited : DuckVariant.randomNatural(this.rand));
        return child;
    }

    @Override
    protected String getLivingSound() {
        return DucklingContent.MODID + ":quack";
    }

    @Override
    protected String getHurtSound() {
        return DucklingContent.MODID + ":duck_death";
    }

    @Override
    protected String getDeathSound() {
        return DucklingContent.MODID + ":duck_death";
    }

    @Override
    protected float getSoundVolume() {
        return 0.45F;
    }

    @Override
    protected void fall(float distance) {
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int feathers = 1 + this.rand.nextInt(2) + this.rand.nextInt(Math.max(1, looting + 1));
        for (int i = 0; i < feathers; i++) {
            this.dropItem(Items.feather, 1);
        }

        int meat = this.rand.nextInt(3 + Math.max(0, looting));
        for (int i = 0; i < meat; i++) {
            this.dropItem(this.isBurning() ? DucklingContent.cookedDuck : DucklingContent.rawDuck, 1);
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        return ModConfig.enableDucklingModule
                && this.worldObj.checkNoEntityCollision(this.boundingBox)
                && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
                && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        return this.worldObj.getBlock(x, y - 1, z).getMaterial().isLiquid() ? 12.0F : this.worldObj.getLightBrightness(x, y, z) - 0.5F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setString("Variant", this.getVariant().name());
        tag.setInteger("EggLayTime", this.eggLayTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setVariant(DuckVariant.byName(tag.getString("Variant")));
        if (tag.hasKey("EggLayTime")) {
            this.eggLayTime = Math.max(1, tag.getInteger("EggLayTime"));
        }
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeByte(this.variantId);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        this.variantId = data.readUnsignedByte();
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setInteger("VariantId", this.variantId);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        if (tag.hasKey("VariantId")) {
            this.variantId = tag.getInteger("VariantId");
        }
    }

    public DuckVariant getVariant() {
        return DuckVariant.byId(this.variantId);
    }

    public void setVariant(DuckVariant variant) {
        int nextVariantId = variant == null ? DuckVariant.PEKIN.getId() : variant.getId();
        if (this.variantId == nextVariantId) {
            return;
        }
        this.variantId = nextVariantId;
        if (this.worldObj != null && !this.worldObj.isRemote) {
            EntitySyncHelper.sync(this);
        }
    }

    public DuckVariant getTextureVariant() {
        return this.hasAgentDName() ? DuckVariant.AGENTD : this.getVariant();
    }

    private boolean hasAgentDName() {
        return ConfiguredNameMatcher.matches(this.getCommandSenderName(), ModConfig.ducklingAgentDNames);
    }

    public float getHeadRotationOffset(float partialTicks) {
        return MathHelper.sin((float)this.ticksExisted * 0.18F + partialTicks) * 0.08F;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationBuilder builder = new AnimationBuilder();
        if (this.isInWater()) {
            builder.addAnimation("walking", Boolean.TRUE);
        } else if (!this.onGround) {
            builder.addAnimation("falling", Boolean.TRUE);
        } else if (event.isMoving()) {
            builder.addAnimation("walking", Boolean.TRUE);
        } else {
            builder.addAnimation("idle", Boolean.TRUE);
        }
        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<EntityDuck>(this, "controller", 10.0F, new AnimationController.IAnimationPredicate<EntityDuck>() {
            @Override
            public PlayState test(AnimationEvent<EntityDuck> event) {
                return EntityDuck.this.predicate(event);
            }
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.animationFactory;
    }
}
