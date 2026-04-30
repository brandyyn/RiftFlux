package com.voidsrift.riftflux.duckling;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

public class EntityAISootDepositFuel extends EntityAIBase implements SootSpriteTargetCache.TileValidator {
    private static final int SEARCH_HORIZONTAL = 16;
    private static final int SEARCH_VERTICAL = 8;
    private static final int MIN_COOLDOWN = 20;
    private static final int RANDOM_COOLDOWN = 20;
    private static final int PATH_UPDATE_COOLDOWN = 20;
    private static final double TARGET_DISTANCE_SQ = 5.0D;
    private static final int FUEL_SLOT = 1;

    private final EntitySootSprite sprite;
    private final double speed;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int cooldown;
    private int pathUpdateCooldown;
    private boolean hasTarget;
    private boolean hasKnownTarget;

    public EntityAISootDepositFuel(EntitySootSprite sprite, double speed) {
        this.sprite = sprite;
        this.speed = speed;
        this.cooldown = sprite.getRNG().nextInt(MIN_COOLDOWN + RANDOM_COOLDOWN);
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        ItemStack held = this.sprite.getHeldItem();
        if (!this.sprite.isTamed() || held == null || !TileEntityFurnace.isItemFuel(held) || this.sprite.isHiding()) {
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
        ItemStack held = this.sprite.getHeldItem();
        return held != null
                && this.hasTarget
                && TileEntityFurnace.isItemFuel(held)
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
        return tile instanceof TileEntityFurnace && !tile.isInvalid();
    }

    @Override
    public boolean isValid(TileEntity tile) {
        if (!(tile instanceof TileEntityFurnace)) {
            return false;
        }
        return this.canInsertIntoSlot((IInventory)tile, FUEL_SLOT);
    }

    private boolean canInsertIntoSlot(IInventory inventory, int slot) {
        ItemStack held = this.sprite.getHeldItem();
        if (held == null || !inventory.isItemValidForSlot(slot, held)) {
            return false;
        }
        ItemStack existing = inventory.getStackInSlot(slot);
        if (existing == null) {
            return true;
        }
        return EntitySootSprite.areStacksMergeable(existing, held)
                && existing.stackSize < Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
    }

    private boolean tryDeposit() {
        TileEntity tile = this.sprite.worldObj.getTileEntity(this.targetX, this.targetY, this.targetZ);
        if (tile instanceof IInventory && this.insertIntoSlot((IInventory)tile, FUEL_SLOT)) {
            this.sprite.playSound("random.pop", 0.5F, 1.5F);
            return true;
        }
        this.hasKnownTarget = false;
        return false;
    }

    private boolean insertIntoSlot(IInventory inventory, int slot) {
        ItemStack held = this.sprite.getHeldItem();
        if (held == null || !inventory.isItemValidForSlot(slot, held)) {
            return false;
        }

        ItemStack existing = inventory.getStackInSlot(slot);
        if (existing == null) {
            int toMove = Math.min(held.stackSize, Math.min(held.getMaxStackSize(), inventory.getInventoryStackLimit()));
            if (toMove <= 0) {
                return false;
            }
            ItemStack inserted = held.copy();
            inserted.stackSize = toMove;
            held.stackSize -= toMove;
            inventory.setInventorySlotContents(slot, inserted);
            this.sprite.setCurrentItemOrArmor(0, held.stackSize <= 0 ? null : held);
            inventory.markDirty();
            return true;
        }

        if (!EntitySootSprite.areStacksMergeable(existing, held)) {
            return false;
        }

        int limit = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
        int toMove = Math.min(limit - existing.stackSize, held.stackSize);
        if (toMove <= 0) {
            return false;
        }
        existing.stackSize += toMove;
        held.stackSize -= toMove;
        inventory.setInventorySlotContents(slot, existing);
        this.sprite.setCurrentItemOrArmor(0, held.stackSize <= 0 ? null : held);
        inventory.markDirty();
        return true;
    }

    private boolean isNearTarget() {
        return this.sprite.getDistanceSq(this.targetX + 0.5D, this.targetY + 0.5D, this.targetZ + 0.5D) < TARGET_DISTANCE_SQ;
    }

    private void updatePathToTarget() {
        this.sprite.getNavigator().tryMoveToXYZ(this.targetX + 0.5D, this.targetY, this.targetZ + 0.5D, this.speed);
        this.pathUpdateCooldown = PATH_UPDATE_COOLDOWN;
    }
}
