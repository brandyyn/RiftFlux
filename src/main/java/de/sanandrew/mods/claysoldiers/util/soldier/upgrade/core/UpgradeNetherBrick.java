/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.core;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.IMeeleeUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.core.AUpgradeCore;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.mutable.MutableFloat;

public class UpgradeNetherBrick
extends AUpgradeCore {
    @Override
    public boolean onSoldierHurt(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, DamageSource source, MutableFloat damage) {
        EntityClayMan frodo;
        if (source.getEntity() instanceof EntityClayMan && !(frodo = (EntityClayMan)source.getEntity()).hasUpgrade(IMeeleeUpgrade.class)) {
            frodo.setFire(3);
        }
        return false;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        if (stack.getItem() == Items.netherbrick) {
            this.consumeItem(stack, upgradeInst);
        }
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }
}

