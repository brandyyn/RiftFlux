package gravestone.core.compatibility.forestry;

import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.core.ForestryAPI;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import gravestone.core.GSItem;
import gravestone.core.GSRecipes;
import gravestone.core.GSTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

final class GSCompatibilityForestryBackpacks {
   private GSCompatibilityForestryBackpacks() {
   }

   static int getApicultureVillagerID() {
      return ForestryAPI.forestryConstants == null
            ? GSCompatibilityForestry.DEFAULT_BEEKEEPER_ID
            : ForestryAPI.forestryConstants.getApicultureVillagerID();
   }

   static int getArboricultureVillagerID() {
      return ForestryAPI.forestryConstants == null
            ? GSCompatibilityForestry.DEFAULT_LUMBERJACK_ID
            : ForestryAPI.forestryConstants.getArboricultureVillagerID();
   }

   static void addBackpack() {
      if (BackpackManager.backpackInterface == null) {
         return;
      }

      GSCompatibilityForestry.backpackItemT1 = BackpackManager.backpackInterface.addBackpack(
            UndertakerBackpack.getInstance(), EnumBackpackType.T1);
      GSCompatibilityForestry.backpackItemT1.setCreativeTab(GSTabs.otherItemsTab);
      GSCompatibilityForestry.backpackItemT1.setUnlocalizedName("backpack.undertaker.t1");
      GSItem.registryExternalItems(GSCompatibilityForestry.backpackItemT1, "GSUndertakerBackpackT1");
      ItemStack backpackStackT1 = new ItemStack(GSCompatibilityForestry.backpackItemT1);
      GSRecipes.addForestryBackpack(backpackStackT1, GSItem.chisel);

      GSCompatibilityForestry.backpackItemT2 = BackpackManager.backpackInterface.addBackpack(
            UndertakerBackpack.getInstance(), EnumBackpackType.T2);
      GSCompatibilityForestry.backpackItemT2.setCreativeTab(GSTabs.otherItemsTab);
      GSCompatibilityForestry.backpackItemT2.setUnlocalizedName("backpack.undertaker.t2");
      GSItem.registryExternalItems(GSCompatibilityForestry.backpackItemT2, "GSUndertakerBackpackT2");

      Item itemSilk = GameRegistry.findItem("Forestry", "craftingMaterial");
      if (itemSilk != null) {
         ItemStack wovenSilk = new ItemStack(itemSilk, 1, 3);
         ItemStack backpackStackT2 = new ItemStack(GSCompatibilityForestry.backpackItemT2);
         RecipeManagers.carpenterManager.addRecipe(200, FluidRegistry.getFluidStack("water", 1000), null,
               backpackStackT2, "sds", "sbs", "sss", 'd', Items.diamond, 'b',
               GSCompatibilityForestry.backpackItemT1, 's', wovenSilk);
      }
   }
}
