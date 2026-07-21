package gravestone.core;

import cpw.mods.fml.common.registry.GameRegistry;
import gravestone.item.ItemGSChisel;
import gravestone.item.ItemGSCorpse;
import gravestone.item.ItemGSMonsterPlacer;
import gravestone.core.recipe.GSChiselRegistry;
import net.minecraft.item.Item;

public class GSItem {
   public static Item chisel;
   public static Item corpse;
   public static Item spawnEgg;

   private GSItem() {
   }

   public static void registration() {
      chisel = new ItemGSChisel();
      GameRegistry.registerItem(chisel, "Chisel");
      GSChiselRegistry.registerConfiguredChisels();
      corpse = new ItemGSCorpse();
      GameRegistry.registerItem(corpse, "Corpse");
      spawnEgg = new ItemGSMonsterPlacer();
      GameRegistry.registerItem(spawnEgg, "SpawnEgg");
   }

   public static void registryExternalItems(Item item, String name) {
      GameRegistry.registerItem(item, name);
   }
}
