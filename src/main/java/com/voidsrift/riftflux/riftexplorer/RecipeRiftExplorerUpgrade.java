package com.voidsrift.riftflux.riftexplorer;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import zairus.worldexplorer.archery.items.Boomerang;

import java.util.Arrays;

public class RecipeRiftExplorerUpgrade extends ShapelessRecipes {
    private final Item subjectItem;
    private final Item materialItem;
    private final String nbtKey;
    private final float valuePerCraft;
    private final float maxValue;

    public RecipeRiftExplorerUpgrade(Item subjectItem, Item materialItem, String nbtKey, float valuePerCraft, float maxValue) {
        super(
                new ItemStack(subjectItem),
                Arrays.asList(new ItemStack(subjectItem, 1, 32767), new ItemStack(materialItem, 1, 32767))
        );
        this.subjectItem = subjectItem;
        this.materialItem = materialItem;
        this.nbtKey = nbtKey;
        this.valuePerCraft = valuePerCraft;
        this.maxValue = maxValue;
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return super.matches(inventory, world) && getCraftingResult(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack subject = findSubjectStack(inventory);
        if (subject == null) {
            return null;
        }
        ItemStack upgraded = subject.copy();
        upgraded.stackSize = 1;
        if (upgraded.getItem() instanceof Boomerang) {
            return ((Boomerang)upgraded.getItem()).applyImprovement(
                    upgraded,
                    ((Boomerang)upgraded.getItem()).getImprovementFromMaterial(this.materialItem)
            );
        }
        if (upgraded.getTagCompound() == null) {
            upgraded.setTagCompound(new NBTTagCompound());
        }
        float current = upgraded.getTagCompound().getFloat(this.nbtKey);
        if (current >= this.maxValue) {
            return null;
        }
        current += this.valuePerCraft;
        if (current > this.maxValue) {
            current = this.maxValue;
        }
        upgraded.getTagCompound().setFloat(this.nbtKey, current);
        return upgraded;
    }

    @Override
    public ItemStack getRecipeOutput() {
        ItemStack preview = new ItemStack(this.subjectItem);
        if (preview.getItem() instanceof Boomerang) {
            return ((Boomerang)preview.getItem()).applyImprovement(
                    preview,
                    ((Boomerang)preview.getItem()).getImprovementFromMaterial(this.materialItem)
            );
        }
        preview.setTagCompound(new NBTTagCompound());
        float value = this.valuePerCraft;
        if (value > this.maxValue) {
            value = this.maxValue;
        }
        preview.getTagCompound().setFloat(this.nbtKey, value);
        return preview;
    }

    private ItemStack findSubjectStack(InventoryCrafting inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (stack.getItem() == this.subjectItem) {
                return stack;
            }
        }
        return null;
    }

    private ItemStack findMaterialStack(InventoryCrafting inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (stack.getItem() == this.materialItem) {
                return stack;
            }
        }
        return null;
    }
}
