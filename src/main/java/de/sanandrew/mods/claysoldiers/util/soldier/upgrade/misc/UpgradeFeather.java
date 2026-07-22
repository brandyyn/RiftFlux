/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.EnumMethodState;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableFloat;

public class UpgradeFeather
extends AUpgradeMisc {
    @Override
    public EnumMethodState onTargeting(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target) {
        if (clayMan.ridingEntity != null || clayMan.hasUpgrade("iron_ingot")) {
            return EnumMethodState.SKIP;
        }
        return !clayMan.onGround && clayMan.motionY < -0.2 && clayMan.fallDistance >= 1.4f ? EnumMethodState.DENY : EnumMethodState.SKIP;
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        if (clayMan.ridingEntity == null && !clayMan.hasUpgrade("iron_ingot") && clayMan.motionY < -0.2 && clayMan.fallDistance >= 1.4f) {
            clayMan.motionY *= 0.2;
            clayMan.fallDistance = 1.5f;
        }
        return false;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }

    @Override
    public void getAiMoveSpeed(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, MutableFloat speed) {
        if (clayMan.ridingEntity == null && !clayMan.hasUpgrade("iron_ingot") && clayMan.motionY < -0.3 && clayMan.fallDistance >= 1.4f) {
            speed.setValue(speed.getValue().floatValue() * 0.25f);
        }
    }
}

