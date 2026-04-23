package net.nmccoy.legendgear.legacy.recipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.inventory.InventoryCrafting;
import net.nmccoy.legendgear.legacy.LegendGear;

import java.util.Arrays;
import java.util.List;

public class MedallionSwordRecipe implements IRecipe {
    private final List<Item> medallions = Arrays.asList(
            LegendGear.fireMedallion,
            LegendGear.earthMedallion,
            LegendGear.windMedallion
    );

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        int numMedallions = 0;
        int numSwords = 0;
        boolean hasNothingElse = true;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }

            if (this.isChargedMedallion(stack)) {
                numMedallions++;
            } else if (this.isAugmentableSword(stack)) {
                numSwords++;
            } else {
                hasNothingElse = false;
            }
        }

        return numMedallions == 1 && numSwords == 1 && hasNothingElse;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack medallion = null;
        ItemStack sword = null;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }

            if (this.isChargedMedallion(stack)) {
                medallion = stack;
            } else if (this.isAugmentableSword(stack)) {
                sword = stack;
            }
        }

        if (sword == null || medallion == null) {
            return null;
        }

        ItemStack modifiedSword = sword.copy();
        if (!modifiedSword.hasTagCompound()) {
            modifiedSword.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = modifiedSword.getTagCompound();
        Item medallionItem = medallion.getItem();
        nbt.setInteger("medallion", Item.getIdFromItem(medallionItem));
        nbt.setString("medallionName", String.valueOf(Item.itemRegistry.getNameForObject(medallionItem)));

        NBTTagCompound display = nbt.getCompoundTag("display");
        NBTTagList lore = display.getTagList("Lore", 8);
        String augmentName = StatCollector.translateToLocal(medallionItem.getUnlocalizedName() + ".name").trim();
        lore.appendTag(new NBTTagString("\u00a76Augment: " + augmentName));
        display.setTag("Lore", lore);
        nbt.setTag("display", display);

        return modifiedSword;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    private boolean isChargedMedallion(ItemStack stack) {
        return stack != null && this.medallions.contains(stack.getItem()) && stack.getItemDamage() == 0;
    }

    private boolean isAugmentableSword(ItemStack stack) {
        return stack != null
                && stack.getItem() instanceof ItemSword
                && (!stack.hasTagCompound()
                || (!stack.getTagCompound().hasKey("medallion") && !stack.getTagCompound().hasKey("medallionName")))
                && stack.getItem().getItemEnchantability() > 0
                && stack.getMaxDamage() > 0;
    }
}
