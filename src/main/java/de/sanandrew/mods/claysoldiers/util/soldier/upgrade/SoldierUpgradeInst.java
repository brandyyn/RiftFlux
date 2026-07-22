/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade;

import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class SoldierUpgradeInst {
    private ASoldierUpgrade p_upgrade;
    private ItemStack p_storedItem;
    private NBTTagCompound p_nbt = new NBTTagCompound();

    public SoldierUpgradeInst(ASoldierUpgrade upgrade) {
        this.p_upgrade = upgrade;
    }

    public ItemStack getStoredItem() {
        return this.p_storedItem;
    }

    public void setStoredItem(ItemStack stack) {
        this.p_storedItem = stack;
    }

    public void readStoredItemFromNBT(NBTTagCompound nbt) {
        if (nbt != null) {
            this.p_storedItem = ItemStack.loadItemStackFromNBT(nbt);
        }
    }

    public NBTTagCompound saveStoredItemToNBT() {
        if (this.p_storedItem != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            this.p_storedItem.writeToNBT(nbt);
            return nbt;
        }
        return null;
    }

    public NBTTagCompound getNbtTag() {
        return this.p_nbt;
    }

    public void setNbtTag(NBTTagCompound nbt) {
        if (nbt != null) {
            this.p_nbt = nbt;
        }
    }

    public ASoldierUpgrade getUpgrade() {
        return this.p_upgrade;
    }
}

