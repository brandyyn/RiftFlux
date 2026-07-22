/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class ItemClayMonitor
extends Item {
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("claysoldiers:stat_display");
    }
}

