/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.oredict.RecipeSorter$Category
 */
package de.sanandrew.mods.claysoldiers.util;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.crafting.RecipeBunnies;
import de.sanandrew.mods.claysoldiers.crafting.RecipeGeckos;
import de.sanandrew.mods.claysoldiers.crafting.RecipeHorses;
import de.sanandrew.mods.claysoldiers.crafting.RecipeSoldiers;
import de.sanandrew.mods.claysoldiers.crafting.RecipeTurtles;
import de.sanandrew.mods.claysoldiers.item.ItemClayManDoll;
import de.sanandrew.mods.claysoldiers.util.RegistryBlocks;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.RecipeSorter;

public final class RegistryRecipes {
    public static RecipeSoldiers recipeSoldiers;

    public static void initialize() {
        CraftingManager.getInstance().addShapelessRecipe(ItemClayManDoll.setTeamForItem("clay", new ItemStack(RegistryItems.dollSoldier, 4)), new ItemStack(Blocks.soul_sand, 1), new ItemStack(Blocks.clay));
        CraftingManager.getInstance().addShapelessRecipe(new ItemStack(RegistryItems.shearBlade, 2), new ItemStack(Items.shears));
        CraftingManager.getInstance().addShapelessRecipe(new ItemStack(Items.shears), new ItemStack(RegistryItems.shearBlade), new ItemStack(RegistryItems.shearBlade));
        CraftingManager.getInstance().addRecipe(new ItemStack(RegistryItems.disruptor), "#|#", "#R#", Character.valueOf('#'), new ItemStack(Blocks.clay), Character.valueOf('|'), new ItemStack(Items.stick), Character.valueOf('R'), new ItemStack(Items.redstone));
        CraftingManager.getInstance().addRecipe(new ItemStack(RegistryItems.disruptorHardened), "#|#", "#R#", Character.valueOf('#'), new ItemStack(Blocks.hardened_clay, 1, Short.MAX_VALUE), Character.valueOf('|'), new ItemStack(Items.stick), Character.valueOf('R'), new ItemStack(Items.redstone));
        CraftingManager.getInstance().addRecipe(new ItemStack(RegistryItems.statDisplay), "#G#", "#R#", Character.valueOf('#'), new ItemStack(Blocks.clay), Character.valueOf('G'), new ItemStack(Blocks.glass), Character.valueOf('R'), new ItemStack(Items.redstone));
        CraftingManager.getInstance().addRecipe(new ItemStack(RegistryItems.statDisplay), "#G#", "#R#", Character.valueOf('#'), new ItemStack(Blocks.clay), Character.valueOf('G'), new ItemStack(Blocks.stained_glass, 1, Short.MAX_VALUE), Character.valueOf('R'), new ItemStack(Items.redstone));
        CraftingManager.getInstance().addRecipe(new ItemStack(RegistryBlocks.clayNexus), "CDC", "SOS", "OOO", Character.valueOf('C'), new ItemStack(Items.clay_ball), Character.valueOf('D'), new ItemStack(Items.diamond), Character.valueOf('S'), new ItemStack(Blocks.soul_sand), Character.valueOf('O'), new ItemStack(Blocks.obsidian));
        recipeSoldiers = new RecipeSoldiers();
        recipeSoldiers.addDollMaterial(new ItemStack(Items.dye, 1, Short.MAX_VALUE));
        recipeSoldiers.addDollMaterial(new ItemStack(Blocks.melon_block, 1, Short.MAX_VALUE));
        recipeSoldiers.addDollMaterial(new ItemStack(Blocks.pumpkin, 1, Short.MAX_VALUE));
        recipeSoldiers.addDollMaterial(new ItemStack(Blocks.torch, 1, Short.MAX_VALUE));
        recipeSoldiers.addDollMaterial(new ItemStack(Blocks.redstone_torch, 1, Short.MAX_VALUE));
        SAPUtils.registerSortedRecipe(recipeSoldiers, "claysoldiers:recipe_soldiers", RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        SAPUtils.registerSortedRecipe(new RecipeHorses(), "claysoldiers:recipe_horses", RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        SAPUtils.registerSortedRecipe(new RecipeTurtles(), "claysoldiers:recipe_turtles", RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        SAPUtils.registerSortedRecipe(new RecipeBunnies(), "claysoldiers:recipe_bunnies", RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        SAPUtils.registerSortedRecipe(new RecipeGeckos(), "claysoldiers:recipe_geckos", RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
    }
}

