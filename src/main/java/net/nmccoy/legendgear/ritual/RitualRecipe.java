/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 */
package net.nmccoy.legendgear.ritual;

import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.init.Blocks;
import net.nmccoy.legendgear.ritual.RecipeComponent;

public class RitualRecipe {
    public HashMap<RecipeComponent, Integer> ingredients = new HashMap();

    public boolean accepts(RitualRecipe other) {
        HashSet<RecipeComponent> requirements = new HashSet<RecipeComponent>();
        requirements.addAll(this.ingredients.keySet());
        HashSet<RecipeComponent> supply = new HashSet<RecipeComponent>();
        supply.addAll(other.ingredients.keySet());
        if (requirements.size() != supply.size()) {
            return false;
        }
        for (RecipeComponent check : this.ingredients.keySet()) {
            boolean fulfilled = false;
            for (RecipeComponent input : other.ingredients.keySet()) {
                if (!check.isMetBy(input)) continue;
                requirements.remove(check);
                supply.remove(input);
                fulfilled = true;
                break;
            }
            if (fulfilled) continue;
            return false;
        }
        return requirements.isEmpty() && supply.isEmpty();
    }

    public RitualRecipe add(RecipeComponent toAdd) {
        if (this.ingredients.containsKey(toAdd)) {
            this.ingredients.put(toAdd, this.ingredients.get(toAdd) + 1);
        } else {
            this.ingredients.put(toAdd, 1);
        }
        return this;
    }

    public int count(RecipeComponent toCount) {
        if (!this.ingredients.containsKey(toCount)) {
            return 0;
        }
        return this.ingredients.get(toCount);
    }

    public int getCharacteristicColor() {
        if (this.count(new RecipeComponent(Blocks.redstone_block)) > 0) {
            return 0xFF2211;
        }
        if (this.count(new RecipeComponent(Blocks.emerald_block)) > 0) {
            return 0x22FF11;
        }
        if (this.count(new RecipeComponent(Blocks.gold_block)) > 0) {
            return 0xFFEE11;
        }
        if (this.count(new RecipeComponent(Blocks.lapis_block)) > 0) {
            return 0x1122FF;
        }
        if (this.count(new RecipeComponent(Blocks.diamond_block)) > 0) {
            return 0x11EEFF;
        }
        return 0xFFFFFF;
    }

    public RitualRecipe generic() {
        RitualRecipe flattened = new RitualRecipe();
        for (RecipeComponent ingredient : this.ingredients.keySet()) {
            RecipeComponent generic = ingredient.generic();
            for (int i = 0; i < this.ingredients.get(ingredient); ++i) {
                flattened.add(generic);
            }
        }
        return flattened;
    }

    public String toString() {
        String output = "Recipe:\n";
        for (RecipeComponent comp : this.ingredients.keySet()) {
            output = output + comp + " x" + this.ingredients.get(comp) + "\n";
        }
        if (this.ingredients.size() == 0) {
            output = output + "(nothing)\n";
        }
        return output;
    }
}
