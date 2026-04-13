package com.voidsrift.riftflux.client.photomode;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class IsometricCameraEntity extends EntityPlayer {

    private static final GameProfile CAMERA_PROFILE = new GameProfile(null, "RiftFluxPhotoCamera");

    public IsometricCameraEntity(World world, EntityPlayer player) {
        super(world, CAMERA_PROFILE);
        this.setSize(0.4F, 0.425F);
        this.yOffset = 1.62F;
        this.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.rotationYawHead = this.rotationYaw;
        this.prevRotationYawHead = this.rotationYaw;
        this.dimension = player.dimension;
        this.noClip = true;
    }

    @Override
    public void addChatMessage(IChatComponent message) {
    }

    @Override
    public boolean canCommandSenderUseCommand(int permissionLevel, String command) {
        return false;
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates() {
        return new ChunkCoordinates(0, 0, 0);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float halfWidth = this.width / 2.0F;
        float halfHeight = this.height / 2.0F;
        this.boundingBox.setBounds(
                x - (double) halfWidth,
                y - (double) halfHeight,
                z - (double) halfWidth,
                x + (double) halfWidth,
                y + (double) halfHeight,
                z + (double) halfWidth
        );
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYawHead = this.rotationYawHead;
        this.noClip = true;
    }

    public void moveCamera(double dx, double dy, double dz) {
        this.noClip = true;
        this.setPosition(this.posX + dx, this.posY + dy, this.posZ + dz);
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public float getEyeHeight() {
        return 0.0F;
    }

    @Override
    public boolean isEntityAlive() {
        return true;
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void damageEntity(DamageSource source, float amount) {
    }

    @Override
    public ItemStack getHeldItem() {
        return null;
    }

    @Override
    public ItemStack getEquipmentInSlot(int slot) {
        return null;
    }

    @Override
    public void setCurrentItemOrArmor(int slot, ItemStack stack) {
    }

    @Override
    public ItemStack[] getLastActiveItems() {
        return new ItemStack[0];
    }
}
