package com.voidsrift.riftflux.duckling;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class EntityAISootStoreItem extends EntityAIBase implements SootSpriteTargetCache.TileValidator {
    private static final int SEARCH_HORIZONTAL = 16;
    private static final int SEARCH_VERTICAL = 8;
    private static final int MIN_COOLDOWN = 20;
    private static final int RANDOM_COOLDOWN = 20;
    private static final int PATH_UPDATE_COOLDOWN = 20;
    private static final double TARGET_DISTANCE_SQ = 5.0D;

    private final EntitySootSprite sprite;
    private final double speed;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int cooldown;
    private int pathUpdateCooldown;
    private boolean hasTarget;
    private boolean hasKnownTarget;

    public EntityAISootStoreItem(EntitySootSprite sprite, double speed) {
        this.sprite = sprite;
        this.speed = speed;
        this.cooldown = sprite.getRNG().nextInt(MIN_COOLDOWN + RANDOM_COOLDOWN);
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.sprite.isTamed() || this.sprite.getHeldItem() == null || this.sprite.isHiding()) {
            return false;
        }
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.cooldown = MIN_COOLDOWN + this.sprite.getRNG().nextInt(RANDOM_COOLDOWN + 1);
        return this.findTarget();
    }

    @Override
    public boolean continueExecuting() {
        return this.sprite.getHeldItem() != null
                && this.hasTarget
                && this.isLiveTarget(this.targetX, this.targetY, this.targetZ)
                && (!this.sprite.getNavigator().noPath() || this.isNearTarget());
    }

    @Override
    public void startExecuting() {
        this.pathUpdateCooldown = 0;
        this.updatePathToTarget();
    }

    @Override
    public void resetTask() {
        this.hasTarget = false;
        this.sprite.getNavigator().clearPathEntity();
        this.pathUpdateCooldown = 0;
    }

    @Override
    public void updateTask() {
        if (!this.hasTarget) {
            return;
        }
        if (this.pathUpdateCooldown > 0) {
            --this.pathUpdateCooldown;
        }
        if (this.pathUpdateCooldown <= 0 && !this.isNearTarget()) {
            this.updatePathToTarget();
        }
        if (this.isNearTarget() && !this.tryDeposit()) {
            this.hasTarget = false;
            this.sprite.getNavigator().clearPathEntity();
        }
    }

    private boolean findTarget() {
        if (this.hasKnownTarget && this.isValid(this.sprite.worldObj.getTileEntity(this.targetX, this.targetY, this.targetZ))) {
            this.hasTarget = true;
            return true;
        }

        TileEntity target = SootSpriteTargetCache.findNearestTile(this.sprite, SEARCH_HORIZONTAL, SEARCH_VERTICAL, this);
        if (target == null) {
            this.hasTarget = false;
            return false;
        }
        this.targetX = target.xCoord;
        this.targetY = target.yCoord;
        this.targetZ = target.zCoord;
        this.hasTarget = true;
        this.hasKnownTarget = true;
        return true;
    }

    private boolean isLiveTarget(int x, int y, int z) {
        TileEntity tile = this.sprite.worldObj.getTileEntity(x, y, z);
        return tile instanceof IInventory && !tile.isInvalid();
    }

    @Override
    public boolean isValid(TileEntity tile) {
        if (!(tile instanceof IInventory)) {
            return false;
        }
        IInventory inventory = (IInventory)tile;
        ItemStack held = this.sprite.getHeldItem();
        if (held == null) {
            return false;
        }
        boolean hasSameItem = false;
        boolean hasEmptySlot = false;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack existing = inventory.getStackInSlot(i);
            if (existing == null) {
                if (inventory.isItemValidForSlot(i, held)) {
                    hasEmptySlot = true;
                }
                continue;
            }
            if (this.isSameItemType(existing, held)) {
                hasSameItem = true;
            }
            if (existing != null
                    && EntitySootSprite.areStacksMergeable(existing, held)
                    && existing.stackSize < Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit())
                    && inventory.isItemValidForSlot(i, held)) {
                return true;
            }
        }
        return hasSameItem && hasEmptySlot;
    }

    private boolean tryDeposit() {
        TileEntity tile = this.sprite.worldObj.getTileEntity(this.targetX, this.targetY, this.targetZ);
        if (tile instanceof IInventory && this.insertIntoInventory(tile)) {
            this.sprite.playSound("random.pop", 0.5F, 1.5F);
            return true;
        }
        this.hasKnownTarget = false;
        return false;
    }

    private boolean insertIntoInventory(TileEntity tile) {
        IInventory inventory = (IInventory)tile;
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

        if (!this.inventoryHasSameItemType(inventory, held)) {
            return false;
        }

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i) != null || !inventory.isItemValidForSlot(i, held)) {
                continue;
            }
            int toMove = Math.min(held.stackSize, Math.min(held.getMaxStackSize(), inventory.getInventoryStackLimit()));
            if (toMove <= 0) {
                return false;
            }
            ItemStack inserted = held.copy();
            inserted.stackSize = toMove;
            held.stackSize -= toMove;
            inventory.setInventorySlotContents(i, inserted);
            this.sprite.setCurrentItemOrArmor(0, held.stackSize <= 0 ? null : held);
            inventory.markDirty();
            return true;
        }
        return false;
    }

    private boolean inventoryHasSameItemType(IInventory inventory, ItemStack held) {
        if (inventory == null || held == null) {
            return false;
        }
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (this.isSameItemType(inventory.getStackInSlot(i), held)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSameItemType(ItemStack existing, ItemStack held) {
        return existing != null && held != null && existing.getItem() == held.getItem();
    }

    private boolean isNearTarget() {
        return this.sprite.getDistanceSq(this.targetX + 0.5D, this.targetY + 0.5D, this.targetZ + 0.5D) < TARGET_DISTANCE_SQ;
    }

    private void updatePathToTarget() {
        this.sprite.getNavigator().tryMoveToXYZ(this.targetX + 0.5D, this.targetY, this.targetZ + 0.5D, this.speed);
        this.pathUpdateCooldown = PATH_UPDATE_COOLDOWN;
    }
}
