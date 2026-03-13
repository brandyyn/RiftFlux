package com.voidsrift.riftflux.asgardshield;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeAsgardShieldGilding implements IRecipe {
    private static final int[] GOLD_SLOTS = new int[]{0, 1, 2, 3, 5, 7};

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (inv == null || inv.getSizeInventory() < 9) {
            return false;
        }

        for (int slot : GOLD_SLOTS) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack == null || stack.getItem() != Items.gold_ingot) {
                return false;
            }
        }

        ItemStack shieldStack = inv.getStackInSlot(4);
        if (shieldStack == null || !(shieldStack.getItem() instanceof ItemAsgardShield)) {
            return false;
        }

        Item shieldItem = shieldStack.getItem();
        if (AsgardShieldContent.isGildedShield(shieldItem)) {
            return false;
        }
        if (AsgardShieldContent.getGildedVariant(shieldItem) == null) {
            return false;
        }

        return inv.getStackInSlot(6) == null && inv.getStackInSlot(8) == null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        if (!matches(inv, null)) {
            return null;
        }

        ItemStack base = inv.getStackInSlot(4);
        if (base == null || base.getItem() == null) {
            return null;
        }

        Item gilded = AsgardShieldContent.getGildedVariant(base.getItem());
        if (gilded == null) {
            return null;
        }

        ItemStack output = new ItemStack(gilded, 1, base.getItemDamage());
        if (base.hasTagCompound()) {
            output.setTagCompound((NBTTagCompound) base.getTagCompound().copy());
        }
        return output;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
