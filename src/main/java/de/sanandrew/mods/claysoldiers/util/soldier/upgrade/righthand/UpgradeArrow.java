/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgrades;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand.AUpgradeRightHanded;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;

public class UpgradeArrow
extends AUpgradeRightHanded {
    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        return !clayMan.hasUpgrade("stick");
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        clayMan.playSound("random.pop", 1.0f, 1.0f);
        clayMan.addUpgrade(SoldierUpgrades.getUpgrade("stick"));
        clayMan.addUpgrade(SoldierUpgrades.getUpgrade("flint"));
        clayMan.addUpgrade(SoldierUpgrades.getUpgrade("feather"));
        this.consumeItem(stack, upgradeInst);
    }

    @Override
    public boolean canBePickedUp(EntityClayMan clayMan, ItemStack stack, ASoldierUpgrade upgrade) {
        return super.canBePickedUp(clayMan, stack, upgrade) && upgrade != SoldierUpgrades.getUpgrade("feather");
    }

    @Override
    public void onItemDrop(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ArrayList<ItemStack> droppedItems) {
    }
}

