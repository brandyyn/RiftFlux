/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util;

import de.sanandrew.mods.claysoldiers.item.ItemClayManDoll;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabClaySoldiers
extends CreativeTabs {
    private ItemStack tabIcon;

    public CreativeTabClaySoldiers() {
        super("claysoldiers:csm_tab");
    }

    @Override
    public ItemStack getIconItemStack() {
        if (this.tabIcon == null) {
            this.tabIcon = super.getIconItemStack();
            ItemClayManDoll.setTeamForItem("clay", this.tabIcon);
        }
        return this.tabIcon;
    }

    @Override
    public Item getTabIconItem() {
        return RegistryItems.dollSoldier;
    }

    @Override
    public String getBackgroundImageName() {
        return "claysoldiers.png";
    }

    public boolean hasSearchBar() {
        return true;
    }
}

