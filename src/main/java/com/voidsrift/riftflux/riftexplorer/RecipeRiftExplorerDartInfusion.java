package com.voidsrift.riftflux.riftexplorer;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import zairus.worldexplorer.archery.items.DartEffectHelper;
import zairus.worldexplorer.archery.items.WEArcheryItems;

public class RecipeRiftExplorerDartInfusion implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return this.getCraftingResult(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        if (inventory == null || inventory.getSizeInventory() < 9) {
            return null;
        }

        ItemStack potionStack = inventory.getStackInSlot(4);
        if (potionStack == null || potionStack.getItem() != Items.potionitem) {
            return null;
        }

        ItemStack baseDart = null;
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                continue;
            }
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null || stack.getItem() != WEArcheryItems.dart) {
                return null;
            }
            if (baseDart == null) {
                baseDart = stack;
                continue;
            }
            if (stack.getItemDamage() != baseDart.getItemDamage() || !ItemStack.areItemStackTagsEqual(stack, baseDart)) {
                return null;
            }
        }

        if (baseDart == null) {
            return null;
        }

        ItemStack ingredient = baseDart.copy();
        ingredient.stackSize = 1;
        ItemStack output = DartEffectHelper.applyPotion(ingredient, potionStack);
        if (output == null) {
            return null;
        }
        output.stackSize = 8;
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
