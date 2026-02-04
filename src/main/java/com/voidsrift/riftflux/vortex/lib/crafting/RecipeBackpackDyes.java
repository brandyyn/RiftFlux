package com.voidsrift.riftflux.vortex.lib.crafting;

import java.util.ArrayList;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import com.voidsrift.riftflux.vortex.item.ItemBackpack;
import com.voidsrift.riftflux.vortex.item.ModItems;

public class RecipeBackpackDyes implements IRecipe {
   public boolean matches(InventoryCrafting inventoryCrafting, World world) {
      ItemStack backpack = null;
      ArrayList<ItemStack> dyes = new ArrayList();

      for(int i = 0; i < inventoryCrafting.getSizeInventory(); ++i) {
         ItemStack itemstack = inventoryCrafting.getStackInSlot(i);
         if (itemstack != null) {
            if (itemstack.getItem() == ModItems.backpack) {
               if (backpack != null) {
                  return false;
               }

               backpack = itemstack;
            } else {
               if (itemstack.getItem() != Items.dye) {
                  return false;
               }

               dyes.add(itemstack);
            }
         }
      }

      return backpack != null && !dyes.isEmpty();
   }

   public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
      ItemStack output = null;
      ItemBackpack backpack = null;
      int[] colorOffsets = new int[3];
      int count = 0;
      int max = 0;

      int newRed;
      int color;
      float red;
      float green;
      int blueOffset;
      for(newRed = 0; newRed < inventoryCrafting.getSizeInventory(); ++newRed) {
         ItemStack itemStack = inventoryCrafting.getStackInSlot(newRed);
         if (itemStack != null) {
            if (itemStack.getItem() == ModItems.backpack) {
               backpack = (ItemBackpack)itemStack.getItem();
               if (output != null) {
                  return null;
               }

               output = itemStack.copy();
               output.stackSize = 1;
               if (backpack.hasColor(itemStack)) {
                  color = backpack.getColor(output);
                  red = (float)(color >> 16 & 255) / 255.0F;
                  green = (float)(color >> 8 & 255) / 255.0F;
                  float blue = (float)(color & 255) / 255.0F;
                  max = (int)((float)max + Math.max(red, Math.max(green, blue)) * 255.0F);
                  colorOffsets[0] = (int)((float)colorOffsets[0] + red * 255.0F);
                  colorOffsets[1] = (int)((float)colorOffsets[1] + green * 255.0F);
                  colorOffsets[2] = (int)((float)colorOffsets[2] + blue * 255.0F);
                  ++count;
               }
            } else {
               if (itemStack.getItem() != Items.dye) {
                  return null;
               }

               float[] colorTable = EntitySheep.fleeceColorTable[BlockColored.func_150032_b(itemStack.getItemDamage())];
               int redOffset = (int)(colorTable[0] * 255.0F);
               int greenOffset = (int)(colorTable[1] * 255.0F);
               blueOffset = (int)(colorTable[2] * 255.0F);
               max += Math.max(redOffset, Math.max(greenOffset, blueOffset));
               colorOffsets[0] += redOffset;
               colorOffsets[1] += greenOffset;
               colorOffsets[2] += blueOffset;
               ++count;
            }
         }
      }

      if (backpack == null) {
         return null;
      } else {
         newRed = colorOffsets[0] / count;
         int newGreen = colorOffsets[1] / count;
         color = colorOffsets[2] / count;
         red = (float)max / (float)count;
         green = (float)Math.max(newRed, Math.max(newGreen, color));
         newRed = (int)((float)newRed * red / green);
         newGreen = (int)((float)newGreen * red / green);
         color = (int)((float)color * red / green);
         blueOffset = (newRed << 8) + newGreen;
         blueOffset = (blueOffset << 8) + color;
         backpack.func_82813_b(output, blueOffset);
         return output;
      }
   }

   public int getRecipeSize() {
      return 10;
   }

   public ItemStack getRecipeOutput() {
      return null;
   }
}
