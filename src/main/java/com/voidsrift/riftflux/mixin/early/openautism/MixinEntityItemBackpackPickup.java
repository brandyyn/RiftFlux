package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.container.ContainerBackpack;
import com.voidsrift.riftflux.vortex.lib.container.InventoryBackpack;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;
import makamys.satchels.inventory.ContainerSatchels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public abstract class MixinEntityItemBackpackPickup {

    @Inject(
            method = "onCollideWithPlayer(Lnet/minecraft/entity/player/EntityPlayer;)V",
            at = @At("RETURN")
    )
    private void rf$storeInBackpack(EntityPlayer player, CallbackInfo ci) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) return;
        ItemStack equipped = com.voidsrift.riftflux.vortex.item.ItemBackpack.getEquippedBackpack(player);
        if (equipped == null || equipped.getItem() != ModItems.backpack) return;

        EntityItem self = (EntityItem) (Object) this;
        if (self.isDead || self.delayBeforeCanPickup > 0) return;
        ItemStack stack = self.getEntityItem();
        if (stack == null || stack.stackSize <= 0) return;

        InventoryBackpack backpack;
        if (player.openContainer instanceof ContainerBackpack) {
            backpack = ((ContainerBackpack) player.openContainer).inventoryBackpack;
        } else if (player.openContainer instanceof ContainerSatchels) {
            backpack = ((ContainerSatchels) player.openContainer).inventoryBackpack;
            if (backpack == null) {
                backpack = ContainerHelper.getBackpackInventory(equipped);
            }
        } else {
            backpack = ContainerHelper.getBackpackInventory(equipped);
        }
        if (backpack == null) return;

        int before = stack.stackSize;
        boolean moved = mergeIntoBackpack(backpack, stack);
        if (moved) {
            player.inventory.markDirty();
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP mp = (EntityPlayerMP) player;
                mp.inventoryContainer.detectAndSendChanges();
                mp.openContainer.detectAndSendChanges();
            }
            int taken = before - stack.stackSize;
            if (taken > 0) {
                player.onItemPickup(self, taken);
            }
            if (stack.stackSize <= 0) {
                self.setDead();
            }
        }
    }

    private static boolean mergeIntoBackpack(InventoryBackpack backpack, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return false;
        boolean moved = false;
        int size = backpack.getSizeInventory();
        int invLimit = backpack.getInventoryStackLimit();

        if (stack.isStackable()) {
            for (int i = 0; i < size && stack.stackSize > 0; ++i) {
                ItemStack slot = backpack.getStackInSlot(i);
                if (slot == null) continue;
                if (slot.getItem() != stack.getItem()) continue;
                if (slot.getItemDamage() != stack.getItemDamage()) continue;
                if (!tagsEqualIgnoringMarkers(slot, stack)) continue;

                int max = Math.min(stack.getMaxStackSize(), invLimit);
                if (slot.stackSize < max) {
                    int can = Math.min(max - slot.stackSize, stack.stackSize);
                    slot.stackSize += can;
                    stack.stackSize -= can;
                    moved = true;
                }
            }
        }

        for (int i = 0; i < size && stack.stackSize > 0; ++i) {
            if (backpack.getStackInSlot(i) != null) continue;
            if (!backpack.isItemValidForSlot(i, stack)) continue;

            int max = Math.min(stack.getMaxStackSize(), invLimit);
            int count = Math.min(max, stack.stackSize);
            ItemStack copy = stack.copy();
            copy.stackSize = count;
            backpack.setInventorySlotContents(i, copy);
            stack.stackSize -= count;
            moved = true;
        }

        if (moved) {
            backpack.markDirty();
        }
        return moved;
    }

    private static boolean tagsEqualIgnoringMarkers(ItemStack a, ItemStack b) {
        if (ItemStack.areItemStackTagsEqual(a, b)) return true;
        NBTTagCompound ta = a != null ? a.getTagCompound() : null;
        NBTTagCompound tb = b != null ? b.getTagCompound() : null;
        if (ta == null && tb == null) return true;
        if (ta == null || tb == null) return false;

        NBTTagCompound ca = (NBTTagCompound) ta.copy();
        NBTTagCompound cb = (NBTTagCompound) tb.copy();
        ca.removeTag("riftflux_new");
        ca.removeTag("riftflux_seen");
        cb.removeTag("riftflux_new");
        cb.removeTag("riftflux_seen");
        if (ca.hasNoTags()) ca = null;
        if (cb.hasNoTags()) cb = null;
        if (ca == null && cb == null) return true;
        if (ca == null || cb == null) return false;
        return ca.equals(cb);
    }
}
