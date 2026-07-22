/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public final class ItemUtils {
    private static final Comparator<NBTTagCompound> STD_NBT_COMPARATOR = new Comparator<NBTTagCompound>(){

        @Override
        public int compare(NBTTagCompound o1, NBTTagCompound o2) {
            if (o1 != null) {
                if (!o1.equals(o2)) {
                    return 1;
                }
            } else if (o2 != null) {
                return -1;
            }
            return 0;
        }
    };

    public static ItemStack decrStackSize(ItemStack is, int amount) {
        is.stackSize -= amount;
        if (is.stackSize <= 0) {
            return null;
        }
        return is;
    }

    public static boolean areStacksEqual(ItemStack is1, ItemStack is2, boolean checkNbt) {
        return ItemUtils.areStacksEqual(is1, is2, checkNbt ? STD_NBT_COMPARATOR : null);
    }

    public static boolean areStacksEqual(ItemStack is1, ItemStack is2, Comparator<NBTTagCompound> nbtCheck) {
        if (is1 == null || is2 == null) {
            return is1 == is2;
        }
        if (is1.getItem() == null || is2.getItem() == null) {
            return is1.getItem() == is2.getItem();
        }
        if (is1.getItem() == is2.getItem()) {
            if (nbtCheck != null && nbtCheck.compare(is1.getTagCompound(), is2.getTagCompound()) != 0) {
                return false;
            }
            return is1.getItemDamage() == Short.MAX_VALUE || is2.getItemDamage() == Short.MAX_VALUE || is1.getItemDamage() == is2.getItemDamage();
        }
        return false;
    }

    public static ItemStack[] getGoodItemStacks(ItemStack is) {
        int maxStackSize = is.getMaxStackSize();
        if (is.stackSize <= maxStackSize && is.stackSize > 0) {
            return new ItemStack[]{is};
        }
        if (is.stackSize > 0) {
            ItemStack isNew;
            int maxFullStackCnt = MathHelper.floor_float((float)is.stackSize / (float)maxStackSize);
            ArrayList<ItemStack> isMap = new ArrayList<ItemStack>(MathHelper.ceiling_float_int((float)is.stackSize / (float)maxStackSize));
            for (int i = 0; i < maxFullStackCnt; ++i) {
                isNew = is.copy();
                isNew.stackSize = maxStackSize;
                isMap.add(isNew);
            }
            isNew = is.copy();
            if ((isNew.stackSize -= maxStackSize * maxFullStackCnt) > 0) {
                isMap.add(isNew);
            }
            return isMap.toArray(new ItemStack[isMap.size()]);
        }
        return new ItemStack[0];
    }

    public static boolean isItemStackInArray(ItemStack stack, boolean checkNbt, ItemStack ... stackArray) {
        for (ItemStack stackElem : stackArray) {
            if (!ItemUtils.areStacksEqual(stack, stackElem, checkNbt)) continue;
            return true;
        }
        return false;
    }

    public static boolean isItemStackInArray(ItemStack stack, Comparator<NBTTagCompound> nbtCheck, ItemStack ... stackArray) {
        for (ItemStack stackElem : stackArray) {
            if (!ItemUtils.areStacksEqual(stack, stackElem, nbtCheck)) continue;
            return true;
        }
        return false;
    }
}

