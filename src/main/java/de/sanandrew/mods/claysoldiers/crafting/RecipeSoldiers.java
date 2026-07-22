/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package de.sanandrew.mods.claysoldiers.crafting;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import de.sanandrew.mods.claysoldiers.item.ItemClayManDoll;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.soldier.ClaymanTeam;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

public class RecipeSoldiers
implements IRecipe {
    private final List<ItemStack> p_dollMaterials = new ArrayList<ItemStack>();

    public void addDollMaterial(ItemStack stack) {
        this.p_dollMaterials.add(stack);
    }

    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        if (invCrafting.getSizeInventory() < 9) {
            return false;
        }
        boolean hasMaterial = false;
        boolean hasDoll = false;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = invCrafting.getStackInSlot(i);
            if (stack == null) continue;
            if (SAPUtils.isItemInStackArray(stack, this.p_dollMaterials)) {
                if (!hasMaterial) {
                    hasMaterial = true;
                    continue;
                }
                return false;
            }
            if (stack.getItem() instanceof ItemClayManDoll) {
                hasDoll = true;
                continue;
            }
            return false;
        }
        return hasDoll && hasMaterial;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        ItemStack material = null;
        Pair<ItemStack, MutableInt> doll = null;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = invCrafting.getStackInSlot(i);
            if (stack == null) continue;
            if (SAPUtils.isItemInStackArray(stack, this.p_dollMaterials)) {
                material = stack;
                continue;
            }
            if (!(stack.getItem() instanceof ItemClayManDoll)) continue;
            if (doll == null) {
                doll = Pair.with(stack, new MutableInt(1));
                continue;
            }
            ((MutableInt)doll.getValue1()).increment();
        }
        if (doll != null && material != null) {
            ItemStack result = new ItemStack(RegistryItems.dollSoldier, (int)((MutableInt)doll.getValue1()).getValue());
            if (((MutableInt)doll.getValue1()).getValue() == 1 && ((ItemStack)doll.getValue0()).hasTagCompound()) {
                result.setTagCompound((NBTTagCompound)doll.getValue0().getTagCompound().copy());
            }
            ItemClayManDoll.setTeamForItem(ClaymanTeam.getTeam(material).getTeamName(), result);
            return result;
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}

