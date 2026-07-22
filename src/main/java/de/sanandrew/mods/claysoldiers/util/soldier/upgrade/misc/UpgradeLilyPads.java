/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

public class UpgradeLilyPads
extends AUpgradeMisc {
    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        if (clayMan.isInWater() && !clayMan.hasUpgrade("iron_ingot")) {
            clayMan.setJumping(false);
            clayMan.motionY = clayMan.isCollidedHorizontally ? 0.2 : Math.abs(clayMan.motionY * 0.1);
        }
        return false;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
        if (clayMan.worldObj.getBlock((int)clayMan.posX, (int)clayMan.posY, (int)clayMan.posZ).getMaterial() == Material.water) {
            clayMan.setJumping(true);
        }
    }
}

