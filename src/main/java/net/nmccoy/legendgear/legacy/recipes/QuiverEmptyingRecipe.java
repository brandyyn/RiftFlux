package net.nmccoy.legendgear.legacy.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class QuiverEmptyingRecipe implements IRecipe {
    private final Item container;
    private final Item ammunition;

    public QuiverEmptyingRecipe(Item container, Item ammunition) {
        this.container = container;
        this.ammunition = ammunition;
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        int numContainers = 0;
        boolean hasNothingElse = true;
        boolean hasAmmo = false;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }

            if (stack.getItem() == this.container) {
                numContainers++;
                hasAmmo = stack.getItemDamage() > 0;
            } else {
                hasNothingElse = false;
            }
        }

        return numContainers == 1 && hasAmmo && hasNothingElse;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        int fullness = 0;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == this.container) {
                fullness = stack.getItemDamage();
            }
        }

        return new ItemStack(this.ammunition, Math.min(fullness, this.ammunition.getItemStackLimit()));
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(this.ammunition);
    }
}
