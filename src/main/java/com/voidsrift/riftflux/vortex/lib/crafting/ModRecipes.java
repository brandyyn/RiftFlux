package com.voidsrift.riftflux.vortex.lib.crafting;

import net.minecraft.item.crafting.CraftingManager;

public class ModRecipes {
   public static final void init() {
      CraftingManager.getInstance().getRecipeList().add(new RecipeBackpackDyes());
   }
}
