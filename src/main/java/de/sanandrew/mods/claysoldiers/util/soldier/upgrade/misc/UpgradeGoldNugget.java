/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import net.minecraft.item.ItemStack;

public class UpgradeGoldNugget
extends AUpgradeMisc {
    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        for (EntityClayMan minion : clayMan.getSoldiersInRange()) {
            if (!minion.getClayTeam().equals(clayMan.getClayTeam()) || minion.hasPath() || minion.getTargetFollowing() != null) continue;
            minion.setTargetFollowing(clayMan);
        }
        return false;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }

    @Override
    public boolean canBePickedUp(EntityClayMan clayMan, ItemStack stack, ASoldierUpgrade upgrade) {
        Predicate<EntityClayMan> filterBy = new Predicate<EntityClayMan>(){

            public boolean apply(EntityClayMan input) {
                return input != null && input.hasUpgrade(UpgradeGoldNugget.class);
            }
        };
        return Collections2.filter(clayMan.getSoldiersInRange(), (Predicate)filterBy).isEmpty();
    }
}

