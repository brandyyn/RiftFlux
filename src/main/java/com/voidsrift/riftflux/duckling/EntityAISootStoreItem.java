package com.voidsrift.riftflux.duckling;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class EntityAISootStoreItem extends EntityAIBase {
    private final EntitySootSprite sprite;
    private final double speed;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int cooldown;
    private boolean hasTarget;

    public EntityAISootStoreItem(EntitySootSprite sprite, double speed) {
        this.sprite = sprite;
        this.speed = speed;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.sprite.getHeldItem() == null || this.sprite.isHiding()) {
            return false;
        }
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.cooldown = 20;
        return this.findTarget();
    }

    @Override
    public boolean continueExecuting() {
        return this.sprite.getHeldItem() != null
                && this.hasTarget
                && this.isValidTarget(this.targetX, this.targetY, this.targetZ)
                && !this.sprite.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.sprite.getNavigator().tryMoveToXYZ(this.targetX + 0.5D, this.targetY, this.targetZ + 0.5D, this.speed);
    }

    @Override
    public void resetTask() {
        this.hasTarget = false;
        this.sprite.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        if (!this.hasTarget) {
            return;
        }
        this.sprite.getNavigator().tryMoveToXYZ(this.targetX + 0.5D, this.targetY, this.targetZ + 0.5D, this.speed);
        if (this.sprite.getDistanceSq(this.targetX + 0.5D, this.targetY + 0.5D, this.targetZ + 0.5D) < 5.0D) {
            this.tryDeposit();
        }
    }

    private boolean findTarget() {
        int baseX = (int)Math.floor(this.sprite.posX);
        int baseY = (int)Math.floor(this.sprite.posY);
        int baseZ = (int)Math.floor(this.sprite.posZ);
        double bestDistance = Double.MAX_VALUE;
        boolean found = false;
        for (int x = baseX - 16; x <= baseX + 16; x++) {
            for (int y = baseY - 8; y <= baseY + 8; y++) {
                for (int z = baseZ - 16; z <= baseZ + 16; z++) {
                    if (!this.isValidTarget(x, y, z)) {
                        continue;
                    }
                    double distance = this.sprite.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        this.targetX = x;
                        this.targetY = y;
                        this.targetZ = z;
                        found = true;
                    }
                }
            }
        }
        this.hasTarget = found;
        return found;
    }

    private boolean isValidTarget(int x, int y, int z) {
        TileEntity tile = this.sprite.worldObj.getTileEntity(x, y, z);
        if (!(tile instanceof IInventory)) {
            return false;
        }
        IInventory inventory = (IInventory)tile;
        ItemStack held = this.sprite.getHeldItem();
        if (held == null) {
            return false;
        }
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack existing = inventory.getStackInSlot(i);
            if (existing != null
                    && EntitySootSprite.areStacksMergeable(existing, held)
                    && existing.stackSize < Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit())
                    && inventory.isItemValidForSlot(i, held)) {
                return true;
            }
        }
        return false;
    }

    private void tryDeposit() {
        TileEntity tile = this.sprite.worldObj.getTileEntity(this.targetX, this.targetY, this.targetZ);
        if (tile instanceof IInventory && this.insertIntoInventory((IInventory)tile)) {
            this.sprite.playSound("random.pop", 0.5F, 1.5F);
        }
    }

    private boolean insertIntoInventory(IInventory inventory) {
        ItemStack held = this.sprite.getHeldItem();
        if (held == null) {
            return false;
        }

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack existing = inventory.getStackInSlot(i);
            if (existing == null || !EntitySootSprite.areStacksMergeable(existing, held) || !inventory.isItemValidForSlot(i, held)) {
                continue;
            }
            int limit = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
            int toMove = Math.min(limit - existing.stackSize, held.stackSize);
            if (toMove <= 0) {
                continue;
            }
            existing.stackSize += toMove;
            held.stackSize -= toMove;
            inventory.setInventorySlotContents(i, existing);
            this.sprite.setCurrentItemOrArmor(0, held.stackSize <= 0 ? null : held);
            inventory.markDirty();
            return true;
        }
        return false;
    }
}
