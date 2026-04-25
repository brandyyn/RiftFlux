package com.voidsrift.riftflux.riftexplorer;

import java.util.ArrayList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import zairus.worldexplorer.archery.items.DartEffectHelper;
import zairus.worldexplorer.archery.items.WEArcheryItems;

public class DartInfusionDisplayRecipe extends ShapedOreRecipe {
    private final Object[] displayInput;
    private final ArrayList<ItemStack> displayOutputs;

    public DartInfusionDisplayRecipe() {
        super(createDisplayOutput(), "DDD", "DPD", "DDD",
                'D', new ItemStack(WEArcheryItems.dart, 1, 0),
                'P', Items.potionitem);
        this.displayInput = createDisplayInput();
        this.displayOutputs = createDisplayOutputs();
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return false;
    }

    @Override
    public Object[] getInput() {
        return this.displayInput;
    }

    public ArrayList<ItemStack> getDisplayOutputs() {
        ArrayList<ItemStack> outputs = new ArrayList<ItemStack>();
        for (int i = 0; i < this.displayOutputs.size(); i++) {
            outputs.add(this.displayOutputs.get(i).copy());
        }
        return outputs;
    }

    private static Object[] createDisplayInput() {
        ItemStack dart = new ItemStack(WEArcheryItems.dart, 1, 0);
        ArrayList<ItemStack> validPotions = findValidPotionStacks();
        return new Object[] {
                dart.copy(), dart.copy(), dart.copy(),
                dart.copy(), validPotions, dart.copy(),
                dart.copy(), dart.copy(), dart.copy()
        };
    }

    private static ItemStack createDisplayOutput() {
        ArrayList<ItemStack> outputs = createDisplayOutputs();
        if (!outputs.isEmpty()) {
            return outputs.get(0).copy();
        }
        ItemStack baseDart = new ItemStack(WEArcheryItems.dart, 1, 0);
        baseDart.stackSize = 8;
        return baseDart;
    }

    private static ArrayList<ItemStack> createDisplayOutputs() {
        ArrayList<ItemStack> outputs = new ArrayList<ItemStack>();
        ItemStack baseDart = new ItemStack(WEArcheryItems.dart, 1, 0);
        ArrayList<ItemStack> validPotions = findValidPotionStacks();
        for (int i = 0; i < validPotions.size(); i++) {
            ItemStack output = DartEffectHelper.applyPotion(baseDart, validPotions.get(i));
            if (output == null) {
                continue;
            }
            output.stackSize = 8;
            if (!containsEquivalentOutput(outputs, output)) {
                outputs.add(output);
            }
        }
        return outputs;
    }

    private static ArrayList<ItemStack> findValidPotionStacks() {
        ArrayList<ItemStack> creativePotions = new ArrayList<ItemStack>();
        ArrayList<ItemStack> validPotions = new ArrayList<ItemStack>();
        Items.potionitem.getSubItems(Items.potionitem, CreativeTabs.tabBrewing, creativePotions);

        ItemStack baseDart = new ItemStack(WEArcheryItems.dart, 1, 0);
        for (int i = 0; i < creativePotions.size(); i++) {
            ItemStack potion = creativePotions.get(i);
            if (potion == null || potion.getItem() != Items.potionitem) {
                continue;
            }
            if (DartEffectHelper.applyPotion(baseDart, potion) == null) {
                continue;
            }
            ItemStack displayPotion = potion.copy();
            displayPotion.stackSize = 1;
            validPotions.add(displayPotion);
        }
        return validPotions;
    }

    private static boolean containsEquivalentOutput(ArrayList<ItemStack> outputs, ItemStack candidate) {
        for (int i = 0; i < outputs.size(); i++) {
            ItemStack existing = outputs.get(i);
            if (existing == null || candidate == null) {
                continue;
            }
            if (existing.getItem() != candidate.getItem()) {
                continue;
            }
            if (existing.getItemDamage() != candidate.getItemDamage()) {
                continue;
            }
            if (ItemStack.areItemStackTagsEqual(existing, candidate)) {
                return true;
            }
        }
        return false;
    }
}
