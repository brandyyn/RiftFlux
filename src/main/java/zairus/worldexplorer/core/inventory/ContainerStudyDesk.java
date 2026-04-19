/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.ICrafting
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.InventoryCraftResult
 *  net.minecraft.inventory.InventoryCrafting
 *  net.minecraft.inventory.Slot
 *  net.minecraft.inventory.SlotCrafting
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package zairus.worldexplorer.core.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import zairus.worldexplorer.core.inventory.ContainerBase;
import zairus.worldexplorer.core.items.WEItem;
import zairus.worldexplorer.core.tileentity.TileEntityDesk;

public class ContainerStudyDesk
extends ContainerBase {
    public InventoryCrafting craftMatrix = new InventoryCrafting((Container)this, 3, 3);
    public InventoryCraftResult craftResult = new InventoryCraftResult();
    public InventoryCraftResult craftAdditional = new InventoryCraftResult();
    private final TileEntityDesk inventory;
    private World worldObj;

    public ContainerStudyDesk(InventoryPlayer playerinv, TileEntityDesk inv, World world) {
        this.inventory = inv;
        this.worldObj = world;
        inv.openInventory();
        int iIndex = 0;
        int gridX = 82;
        int gridY = 214;
        int gridCols = 9;
        int gridRows = 1;
        iIndex = this.placeSlotGrid((IInventory)playerinv, iIndex, gridX, gridY, gridCols, gridRows);
        gridX = 82;
        gridY = 156;
        gridCols = 9;
        gridRows = 3;
        iIndex = this.placeSlotGrid((IInventory)playerinv, iIndex, gridX, gridY, gridCols, gridRows);
        iIndex = 0;
        gridX = 15;
        gridY = 30;
        gridCols = 3;
        gridRows = 9;
        iIndex = this.placeSlotGrid(inv, iIndex, gridX, gridY, gridCols, gridRows);
        gridX = 85;
        gridY = 49;
        this.addSlotToContainer(new SlotImprovement(inv, iIndex, gridX, gridY));
        ++iIndex;
        gridX = 136;
        gridY = 30;
        gridCols = 3;
        gridRows = 3;
        iIndex = this.placeSlotGrid(inv, iIndex, gridX * 2048, gridY * 2048, gridCols, gridRows);
        gridX = 155;
        gridY = 121;
        this.addSlotToContainer((Slot)new SlotCraftingAdditional(playerinv.player, inv, 27, (IInventory)this.craftMatrix, (IInventory)this.craftAdditional, 0, gridX, gridY));
        iIndex = 0;
        gridX = 136;
        gridY = 30;
        gridCols = 3;
        gridRows = 3;
        iIndex = this.placeSlotGrid((IInventory)this.craftMatrix, iIndex, gridX, gridY, gridCols, gridRows);
        gridX = 221;
        gridY = 49;
        this.addSlotToContainer((Slot)new SlotCrafting(playerinv.player, (IInventory)this.craftMatrix, (IInventory)this.craftResult, 0, gridX, gridY));
        this.onCraftMatrixChanged((IInventory)this.craftMatrix);
        for (int i = 0; i < 9; ++i) {
            this.craftMatrix.setInventorySlotContents(i, this.inventory.getStackInSlot(i + 28));
        }
    }

    public void onCraftMatrixChanged(IInventory inventory) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
        this.craftAdditional.setInventorySlotContents(63, this.findImprovementResult());
    }

    private ItemStack findImprovementResult() {
        ItemStack improved = null;
        ItemStack subject = null;
        ItemStack material = this.inventory.getStackInSlot(27);
        Item subjectItem = null;
        for (int i = 0; i < 9; ++i) {
            if (subject == null) {
                subject = this.craftMatrix.getStackInSlot(i);
                continue;
            }
            if (this.craftMatrix.getStackInSlot(i) == null) continue;
            subject = null;
            i = 9;
        }
        if (material != null && subject != null && (subjectItem = subject.getItem()) instanceof WEItem && ((WEItem)subjectItem).hasImprovements()) {
            improved = subject.copy();
            WEItem.Improvement imp = ((WEItem)subjectItem).getImprovementFromMaterial(material.getItem());
            if (imp != null) {
                this.addImprovement(improved, imp.improvementType.getKey(), imp.valuePerUnit, imp.improvementType.getMaxValue());
            }
        }
        return improved;
    }

    private ItemStack addImprovement(ItemStack improved, String improvement, float valueAdded, float valueMax) {
        if (improved.getTagCompound() == null) {
            improved.setTagCompound(new NBTTagCompound());
        }
        if (!improved.getTagCompound().hasKey(improvement)) {
            improved.getTagCompound().setFloat(improvement, 0.0f);
        }
        float curValue = improved.getTagCompound().getFloat(improvement);
        if ((curValue += valueAdded) > valueMax) {
            curValue = valueMax;
        }
        improved.getTagCompound().setFloat(improvement, curValue);
        return improved;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.inventory.isUseableByPlayer(player);
    }

    @Override
    public ItemStack slotClick(int slot, int slotX, int slotY, EntityPlayer player) {
        ItemStack stack = super.slotClick(slot, slotX, slotY, player);
        this.onCraftMatrixChanged((IInventory)this.craftMatrix);
        return stack;
    }

    @Override
    protected void retrySlotClick(int slotNumber, int p_75133_2_, boolean p_75133_3_, EntityPlayer player) {
        if (slotNumber < 63) {
            this.slotClick(slotNumber, p_75133_2_, 1, player);
        }
    }

    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotNumber);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (slotNumber < 36 && !this.mergeItemStack(itemstack1, this.inventory.getSizeInventory() - 10, 63, true)) {
                return null;
            }
            if (slotNumber > 35 && slotNumber < 63 && !this.mergeItemStack(itemstack1, 0, this.inventory.getSizeInventory() - 10, false)) {
                return null;
            }
            if (slotNumber == 63 && !this.mergeItemStack(itemstack1, 0, 63, false)) {
                return null;
            }
            if (slotNumber > 73 && slotNumber < 83 && !this.mergeItemStack(itemstack1, 0, 63, false)) {
                return null;
            }
            if (slotNumber == 83) {
                while (slot.getStack() != null && slot.getStack().stackSize > 0) {
                    itemstack1 = slot.getStack();
                    itemstack = itemstack1.copy();
                    if (!this.mergeItemStack(itemstack1, 0, 63, false)) {
                        return null;
                    }
                    slot.onSlotChange(itemstack1, itemstack);
                    if (itemstack1.stackSize == itemstack.stackSize) {
                        return null;
                    }
                    slot.onPickupFromSlot(player, itemstack1);
                    this.onCraftMatrixChanged((IInventory)this.craftMatrix);
                }
            }
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
        super.onContainerClosed(player);
        for (int i = 0; i < 9; ++i) {
            this.inventory.setInventorySlotContents(i + 28, this.craftMatrix.getStackInSlot(i));
        }
        this.inventory.closeInventory();
    }

    private class SlotCraftingAdditional
    extends SlotCrafting {
        private IInventory improvementInv;
        private int improvementInvSlot;

        public SlotCraftingAdditional(EntityPlayer player, IInventory inventoryImprovement, int slotNumberImprovement, IInventory inventoryMatrix, IInventory inventoryResult, int index, int x, int y) {
            super(player, inventoryMatrix, inventoryResult, index, x, y);
            this.improvementInv = inventoryImprovement;
            this.improvementInvSlot = slotNumberImprovement;
        }

        public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
            super.onPickupFromSlot(player, stack);
            this.improvementInv.decrStackSize(this.improvementInvSlot, 1);
        }
    }

    private class SlotImprovement
    extends Slot {
        public SlotImprovement(IInventory inventory, int slotNumber, int x, int y) {
            super(inventory, slotNumber, x, y);
        }

        public boolean isItemValid(ItemStack stack) {
            return stack.getItem() == Items.redstone || stack.getItem() == Item.getItemFromBlock((Block)Blocks.redstone_block) || stack.getItem() == Items.glowstone_dust || stack.getItem() == Item.getItemFromBlock((Block)Blocks.glowstone) || stack.getItem() == Items.gunpowder || stack.getItem() == Items.slime_ball || stack.getItem() == Items.ender_eye || stack.getItem() == Items.ender_pearl || stack.getItem() == Items.blaze_powder;
        }
    }
}

