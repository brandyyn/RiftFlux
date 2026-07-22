/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class UpgradePaper
extends AUpgradeMisc {
    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        if (stack.getItem() == Items.paper) {
            this.consumeItem(stack, upgradeInst);
            clayMan.playSound("random.pop", 1.0f, 1.0f);
        } else if (stack.getItem() == Items.book) {
            clayMan.playSound("random.pop", 1.0f, 1.0f);
        }
    }
}

