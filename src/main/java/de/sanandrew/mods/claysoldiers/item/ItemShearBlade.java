/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class ItemShearBlade
extends Item {
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("claysoldiers:shear_blade");
    }
}

