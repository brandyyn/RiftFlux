/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.ICrafting
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package zairus.worldexplorer.core.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import zairus.worldexplorer.core.inventory.ContainerBase;
import zairus.worldexplorer.core.inventory.InventoryPlayerEquipment;
import zairus.worldexplorer.core.player.CorePlayerManager;

public class ContainerEquipment
extends ContainerBase {
    public final InventoryPlayerEquipment playerEquipment;
    private final EntityPlayer player;

    @SideOnly(value=Side.CLIENT)
    public ContainerEquipment(World world, double x, double y, double z) {
        this.player = Minecraft.getMinecraft().thePlayer;
        this.playerEquipment = CorePlayerManager.getPlayerEquipmentInventory(this.player);
        this.initContainer();
    }

    public ContainerEquipment(InventoryPlayer playerInv, World world) {
        this.player = playerInv.player;
        this.playerEquipment = CorePlayerManager.getPlayerEquipmentInventory(this.player);
        this.initContainer();
    }

    private void initContainer() {
        int iIndex = 0;
        iIndex = this.bindPlayerInventory(this.player.inventory);
        this.playerEquipment.openInventory();
        iIndex = 0;
        iIndex = this.placeSlotGrid(this.playerEquipment, iIndex, 43, 10, 1, 2);
        iIndex = this.placeSlotGrid(this.playerEquipment, iIndex, 115, 10, 1, 2);
        iIndex = this.placeSlotGrid(this.playerEquipment, iIndex, 25, 46, 2, 1);
        iIndex = this.placeSlotGrid(this.playerEquipment, iIndex, 115, 46, 2, 1);
        iIndex = this.placeSlotGrid(this.playerEquipment, iIndex, 43, 64, 1, 1);
        iIndex = this.placeSlotGrid(this.playerEquipment, iIndex, 115, 64, 1, 1);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack slotClick(int slot, int slotX, int slotY, EntityPlayer player) {
        ItemStack stack = super.slotClick(slot, slotX, slotY, player);
        CorePlayerManager.savePlayerEquipmentInventory(this.playerEquipment, this.player);
        return stack;
    }

    @Override
    protected void retrySlotClick(int slotNumber, int p_75133_2_, boolean p_75133_3_, EntityPlayer player) {
        if (slotNumber < 36) {
            this.slotClick(slotNumber, p_75133_2_, 1, player);
        }
    }

    @Override
    public void detectAndSendChanges() {
        for (int i = 0; i < this.inventorySlots.size(); ++i) {
            ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);
            if (ItemStack.areItemStacksEqual((ItemStack)itemstack1, (ItemStack)itemstack)) continue;
            itemstack1 = itemstack == null ? null : itemstack.copy();
            this.inventoryItemStacks.set(i, itemstack1);
            for (int j = 0; j < this.crafters.size(); ++j) {
                ((ICrafting)this.crafters.get(j)).sendSlotContents((Container)this, i, itemstack1);
            }
        }
        CorePlayerManager.savePlayerEquipmentInventory(this.playerEquipment, this.player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotNumber);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        this.playerEquipment.closeInventory();
    }

    @Override
    public void putStackInSlot(int slot, ItemStack stack) {
        this.getSlot(slot).putStack(stack);
    }
}

