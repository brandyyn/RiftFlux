/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class UpgradeWheatSeeds
extends AUpgradeMisc {
    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        if (clayMan.hasUpgrade("enderpearl")) {
            clayMan.attackEntityFrom(DamageSource.magic, 10000.0f);
        }
        return false;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }
}

