package com.voidsrift.riftflux.avatar.appa;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityBison extends EntityFamiliar {
    public float tailAngle = 0.0f;
    private final float maxSpeed;
    private float currentSpeed;
    private double moveSpeedAir = 1.5;
    public float moveSpeedAirVert = 0.0f;
    private float yawAdd;
    private int yawSpeed = 30;
    private boolean controlUp;
    private boolean controlDown;

    public EntityBison(World world) {
        super(world);
        this.getNavigator().setAvoidsWater(true);
        this.setSize(3.0f, 4.5f);
        this.moveSpeedAir = 0.08;
        this.moveSpeedAirVert = 0.25f;
        this.maxSpeed = (float) this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue();
        this.currentSpeed = 0.0f;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
    }

    @Override
    public void moveEntityWithHeading(float strafeMovement, float forwardMovement) {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
            EntityLivingBase rider = (EntityLivingBase) this.riddenByEntity;
            this.prevRotationYaw = this.rotationYaw = rider.rotationYaw;
            this.rotationPitch = rider.rotationPitch * 0.5f;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            strafeMovement = rider.moveStrafing * 0.5f;
            forwardMovement = rider.moveForward;
            if (forwardMovement <= 0.0f) {
                forwardMovement *= 0.25f;
            }
            this.stepHeight = 1.0f;
            if (!this.worldObj.isRemote) {
                boolean wantsVerticalMove = this.controlUp || this.controlDown;
                if (wantsVerticalMove || !this.onGround) {
                    this.moveFlying(strafeMovement, forwardMovement, (float) this.moveSpeedAir);
                    float vertical = 0.0f;
                    float speed = this.moveSpeedAirVert > 0.0f ? this.moveSpeedAirVert : 0.25f;
                    if (this.controlUp) {
                        vertical += speed;
                    }
                    if (this.controlDown) {
                        vertical -= speed;
                    }
                    this.motionY = vertical;
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    this.motionX *= 0.9;
                    this.motionY *= 0.9;
                    this.motionZ *= 0.9;
                    this.fallDistance = 0.0f;
                    this.isAirBorne = true;
                } else {
                    this.setAIMoveSpeed(0.25f);
                    super.moveEntityWithHeading(strafeMovement, forwardMovement);
                }
            }
            return;
        }
        super.moveEntityWithHeading(strafeMovement, forwardMovement);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!(this.riddenByEntity instanceof EntityPlayer)) {
            this.controlUp = false;
            this.controlDown = false;
        }
        if (this.riddenByEntity == null && !this.onGround && !this.isInWater()) {
            this.motionY = Math.max(this.motionY, -0.1D);
            this.fallDistance = 0.0f;
        }
    }

    public void setGlideControls(boolean up, boolean down) {
        this.controlUp = up;
        this.controlDown = down;
    }

    public void setMoveSpeedAir(double moveSpeedAir) {
        this.moveSpeedAir = moveSpeedAir;
    }

    public void setMoveSpeedAirVertical(float moveSpeedAirVert) {
        this.moveSpeedAirVert = moveSpeedAirVert;
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public Item getTameItem() {
        return Items.apple;
    }

    @Override
    public int getTotalArmorValue() {
        return 8;
    }

    public int getAttackStrength(Entity par1Entity) {
        return 10;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    @Override
    protected String getLivingSound() {
        return "avatarMobs.BisonRoar";
    }

    @Override
    protected String getHurtSound() {
        return "mob.cow.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "avatarMobs.BisonDeath";
    }

    @Override
    protected void func_145780_a(int par1, int par2, int par3, Block par4) {
        this.worldObj.playSoundAtEntity(this, "mob.cow.step", 0.4f, 1.0f);
    }

    @Override
    protected Item getDropItem() {
        return this.isBurning() ? Items.cooked_beef : Items.beef;
    }

    @Override
    protected void dropRareDrop(int par1) {
        this.dropItem(Items.beef, 27);
    }

    @Override
    protected void dropFewItems(boolean par1, int par2) {
        this.dropItem(Items.beef, 27);
    }

    @Override
    protected void fall(float distance) {
        this.fallDistance = 0.0f;
    }

    @Override
    public void receivedMessage(String message) {
    }
}
