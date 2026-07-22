/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.item;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class AItemHorseArmor
extends Item {
    public abstract int getArmorValue(EntityHorse var1, ItemStack var2);

    public abstract String getArmorTexture(EntityHorse var1, ItemStack var2);
}

