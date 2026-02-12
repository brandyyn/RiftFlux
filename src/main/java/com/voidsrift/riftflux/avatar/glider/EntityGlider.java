package com.voidsrift.riftflux.avatar.glider;

import com.voidsrift.riftflux.avatar.util.FileLocation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityGlider extends Entity {
    public double prevRotationRoll = 0.0;
    public double rotationRoll = 0.0;
    public double prevRotationYAW = 0.0;
    public double rotationYAW = 0.0;
    public double prevRotationPitch = 0.0;
    public double rotationPitch = 0.0;
    private double lastHeight;
    public boolean isRising;
    private boolean wasOnGround = true;
    private int special;
    private int color;

    public EntityGlider(World world) {
        this(world, 1);
    }

    public EntityGlider(World world, int color) {
        super(world);
        this.special = this.worldObj.rand.nextInt(10000);
        this.color = color;
        this.ignoreFrustumCheck = true;
    }

    public void initOnSpawn() {
        this.lastHeight = this.posY;
        this.isRising = false;
        this.rotationPitch = 0.0;
        this.prevRotationPitch = 0.0;
        this.rotationRoll = 0.0;
        this.prevRotationRoll = 0.0;
        this.prevRotationYAW = this.rotationYAW;
        if (this.ridingEntity != null) {
            this.wasOnGround = this.ridingEntity.onGround;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ridingEntity == null) {
            this.setDead();
            return;
        }
        if (this.ridingEntity instanceof EntityPlayer
                && !GliderItemHelper.isHoldingGlider((EntityPlayer) this.ridingEntity)) {
            this.ridingEntity.riddenByEntity = null;
            this.ridingEntity = null;
            this.setDead();
            return;
        }
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == Side.SERVER) {
            this.ridingEntity.riddenByEntity = null;
            this.ridingEntity = null;
            this.setDead();
            return;
        }
        this.setPosition(
                this.ridingEntity.posX,
                this.ridingEntity.posY,
                this.ridingEntity.posZ
        );
        boolean nowOnGround = this.ridingEntity.onGround;
        if (this.wasOnGround && !nowOnGround) {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;
        }
        this.wasOnGround = nowOnGround;
        this.isRising = this.posY > this.lastHeight + 0.05;
        this.lastHeight = this.posY;
        this.updateRotations();
    }

    public void updateRotations() {
        this.prevRotationPitch = this.rotationPitch;
        if (this.ridingEntity.isSneaking()) {
            this.rotationPitch += 1.0;
            if (this.rotationPitch > 30.0) {
                this.rotationPitch = 30.0;
            }
        }
        if (!this.ridingEntity.isSneaking() && this.isRising) {
            this.rotationPitch -= 1.0;
            if (this.rotationPitch < -80.0) {
                this.rotationPitch = -80.0;
            }
        }
        if (!this.ridingEntity.isSneaking() && !this.isRising && this.rotationPitch != 0.0) {
            if (this.rotationPitch < 0.0) {
                this.rotationPitch += 2.0;
            } else if (this.rotationPitch > 0.0) {
                this.rotationPitch -= 1.0;
            }
        }
        if (Math.abs(this.ridingEntity.motionX) < 0.01 && Math.abs(this.ridingEntity.motionZ) < 0.01) {
            this.prevRotationYAW = this.rotationYAW = this.ridingEntity.rotationYaw;
        } else {
            this.prevRotationYAW = this.rotationYAW;
            this.rotationYAW = Math.toDegrees(-Math.atan2(this.ridingEntity.motionX, this.ridingEntity.motionZ));
            this.rotationYAW = this.interpolateRotation((float) this.prevRotationYAW, (float) this.rotationYAW, 0.5f);
            this.prevRotationRoll = this.rotationRoll;
            this.rotationRoll = this.rotationYAW - this.ridingEntity.rotationYaw;
            this.rotationRoll = this.interpolateRotation((float) this.prevRotationRoll, (float) this.rotationRoll, 0.5f);
        }
    }

    private float interpolateRotation(float angle1, float angle2, float centerPoint) {
        float f3;
        for (f3 = angle2 - angle1; f3 < -180.0f; f3 += 360.0f) {
        }
        while (f3 >= 180.0f) {
            f3 -= 360.0f;
        }
        return angle1 + centerPoint * f3;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
    }

    public String getTextureFile() {
        return EntityGlider.getOpenTexture(this.color);
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static String getOpenTexture(int color) {
        switch (color) {
            case 0:
                return FileLocation.ENTITYTEXTURE + "WhiteStaff.png";
            case 1:
                return FileLocation.ENTITYTEXTURE + "OrangeStaff.png";
            case 2:
                return FileLocation.ENTITYTEXTURE + "MagentaStaff.png";
            case 3:
                return FileLocation.ENTITYTEXTURE + "LightBlueStaff.png";
            case 4:
                return FileLocation.ENTITYTEXTURE + "YellowStaff.png";
            case 5:
                return FileLocation.ENTITYTEXTURE + "LightGreenStaff.png";
            case 6:
                return FileLocation.ENTITYTEXTURE + "PinkStaff.png";
            case 7:
                return FileLocation.ENTITYTEXTURE + "GreyStaff.png";
            case 8:
                return FileLocation.ENTITYTEXTURE + "LightGreyStaff.png";
            case 9:
                return FileLocation.ENTITYTEXTURE + "CyanStaff.png";
            case 10:
                return FileLocation.ENTITYTEXTURE + "PurpleStaff.png";
            case 11:
                return FileLocation.ENTITYTEXTURE + "BlueStaff.png";
            case 12:
                return FileLocation.ENTITYTEXTURE + "BrownStaff.png";
            case 13:
                return FileLocation.ENTITYTEXTURE + "GreenStaff.png";
            case 14:
                return FileLocation.ENTITYTEXTURE + "RedStaff.png";
            case 15:
                return FileLocation.ENTITYTEXTURE + "BlackStaff.png";
            default:
                return FileLocation.ENTITYTEXTURE + "OrangeStaff.png";
        }
    }
}
