package com.voidsrift.riftflux.duckling;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

public class EntityAISootDepositFuel extends EntityAIBase {
    private static final int FUEL_SLOT = 1;

    private final EntitySootSprite sprite;
    private final double speed;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int cooldown;
    private boolean hasTarget;

    public EntityAISootDepositFuel(EntitySootSprite sprite, double speed) {
        this.sprite = sprite;
        this.speed = speed;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        ItemStack held = this.sprite.getHeldItem();
        if (held == null || !TileEntityFurnace.isItemFuel(held) || this.sprite.isHiding()) {
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
        ItemStack held = this.sprite.getHeldItem();
        return held != null
                && this.hasTarget
                && TileEntityFurnace.isItemFuel(held)
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

    private void tryDeposit() {
        TileEntity tile = this.sprite.worldObj.getTileEntity(this.targetX, this.targetY, this.targetZ);
        if (tile instanceof IInventory && this.insertIntoSlot((IInventory)tile, FUEL_SLOT)) {
            this.sprite.playSound("random.pop", 0.5F, 1.5F);
        }
    }

    private boolean insertIntoSlot(IInventory inventory, int slot) {
        ItemStack held = this.sprite.getHeldItem();
        if (held == null || !inventory.isItemValidForSlot(slot, held)) {
            return false;
        }

        ItemStack existing = inventory.getStackInSlot(slot);
        if (existing == null) {
            inventory.setInventorySlotContents(slot, held.copy());
            this.sprite.setCurrentItemOrArmor(0, null);
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
}
