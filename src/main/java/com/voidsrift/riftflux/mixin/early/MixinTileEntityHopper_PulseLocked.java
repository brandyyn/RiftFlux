package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.hopper.PulseLockedHopper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TileEntityHopper.class)
public abstract class MixinTileEntityHopper_PulseLocked implements PulseLockedHopper {
    @Inject(method = "canUpdate", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$disableTicking(CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.pulseLockedHoppers) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true)
    private void riftflux$blockNormalTick(CallbackInfo ci) {
        if (ModConfig.pulseLockedHoppers) {
            ci.cancel();
        }
    }

    @Inject(method = "func_145887_i", at = @At("HEAD"), cancellable = true)
    private void riftflux$blockNormalTransferAttempts(CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.pulseLockedHoppers) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public void riftflux$runPulseTransfer() {
        boolean moved = false;
        if (!this.riftflux$isInventoryFull()) {
            moved = this.riftflux$pullItemsIntoHopper();
        }
        if (!this.riftflux$isInventoryEmpty()) {
            moved = this.riftflux$transferItemsOut() || moved;
        }
        if (moved) {
            ((TileEntityHopper) (Object) this).markDirty();
        }
    }

    @Unique
    private boolean riftflux$pullItemsIntoHopper() {
        IHopper hopper = (IHopper) (Object) this;
        if (TileEntityHopper.func_145884_b(hopper) != null && TileEntityHopper.func_145891_a(hopper)) {
            return true;
        }

        World world = hopper.getWorldObj();
        if (world == null) {
            return false;
        }

        double x = hopper.getXPos();
        double y = hopper.getYPos();
        double z = hopper.getZPos();
        AxisAlignedBB itemSearchBox = AxisAlignedBB.getBoundingBox(x, y + 0.5D, z, x + 1.0D, y + 2.0D, z + 1.0D);
        List items = world.selectEntitiesWithinAABB(EntityItem.class, itemSearchBox, IEntitySelector.selectAnything);
        for (int i = 0; i < items.size(); i++) {
            EntityItem item = (EntityItem) items.get(i);
            if (TileEntityHopper.func_145898_a((IInventory) (Object) this, item)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean riftflux$isInventoryEmpty() {
        IInventory inventory = (IInventory) (Object) this;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack != null && stack.stackSize > 0) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private boolean riftflux$isInventoryFull() {
        IInventory inventory = (IInventory) (Object) this;
        int inventoryLimit = inventory.getInventoryStackLimit();
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null || stack.stackSize <= 0) {
                return false;
            }
            int stackLimit = Math.min(stack.getMaxStackSize(), inventoryLimit);
            if (stack.stackSize < stackLimit) {
                return false;
            }
        }
        return true;
    }

    @Invoker("func_145883_k")
    public abstract boolean riftflux$transferItemsOut();
}