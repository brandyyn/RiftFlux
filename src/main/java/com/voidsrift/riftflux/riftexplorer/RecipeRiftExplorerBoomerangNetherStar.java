package com.voidsrift.riftflux.riftexplorer;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import zairus.worldexplorer.archery.items.Boomerang;

public class RecipeRiftExplorerBoomerangNetherStar implements IRecipe {
    private final Boomerang boomerang;

    public RecipeRiftExplorerBoomerangNetherStar(Boomerang boomerang) {
        this.boomerang = boomerang;
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return this.getCraftingResult(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        if (inventory == null || !this.boomerang.isStarInfusionEnabled()) {
            return null;
        }

        ItemStack subject = null;
        ItemStack infusionItem = null;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (stack.getItem() == this.boomerang) {
                if (subject != null) {
                    return null;
                }
                subject = stack;
                continue;
            }
            if (this.boomerang.isStarInfusedMaterial(stack)) {
                if (infusionItem != null) {
                    return null;
                }
                infusionItem = stack;
                continue;
            }
            return null;
        }

        if (subject == null || infusionItem == null) {
            return null;
        }

        ItemStack upgraded = subject.copy();
        upgraded.stackSize = 1;
        return this.boomerang.applyStarInfusedBoost(upgraded);
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        if (!this.boomerang.isStarInfusionEnabled()) {
            return null;
        }
        return this.boomerang.applyStarInfusedBoost(new ItemStack(this.boomerang));
    }
}
