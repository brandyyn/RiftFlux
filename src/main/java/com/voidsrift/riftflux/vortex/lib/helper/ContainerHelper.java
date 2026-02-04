package com.voidsrift.riftflux.vortex.lib.helper;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.HashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import com.voidsrift.riftflux.vortex.lib.container.InventoryBackpack;
import com.voidsrift.riftflux.vortex.lib.container.InventoryGluttonyCharm;
import com.voidsrift.riftflux.vortex.lib.container.InventoryToolbelt;

public class ContainerHelper {
   public static final int toolbeltWithdraw = 0;
   public static final int toolbeltInsert = 1;
   public static final int toolbeltSwap = 2;
   private static Item toolbelt;
   private static Item backpack;
   private static Item focusPouch;
   private static HashMap<Item, String> containers = new HashMap();

   public static final void init() {
      toolbelt = GameRegistry.findItem("riftflux", "toolbelt");
      containers.put(toolbelt, "Toolbelt");
      backpack = GameRegistry.findItem("riftflux", "backpack");
      containers.put(backpack, "Backpack");
      focusPouch = GameRegistry.findItem("Thaumcraft", "FocusPouch");
      containers.put(focusPouch, "Inventory");
   }

   public static boolean toolbeltValid(ItemStack stack) {
      return stack.getMaxStackSize() == 1 && !isContainer(stack);
   }

   public static InventoryToolbelt getToolbeltInventory(ItemStack stack) {
      return new InventoryToolbelt(stack);
   }

   public static InventoryBackpack getBackpackInventory(ItemStack stack) {
      return new InventoryBackpack(stack);
   }

   public static InventoryGluttonyCharm getGluttonyCharmInventory(ItemStack stack) {
      return new InventoryGluttonyCharm(stack);
   }

   public static boolean isContainer(ItemStack stack) {
      return containers.containsKey(stack.getItem());
   }
}
