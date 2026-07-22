/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.enchantment;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.projectile.EntityFirechargeChunk;
import de.sanandrew.mods.claysoldiers.entity.projectile.ISoldierProjectile;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgrades;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.mutable.MutableFloat;

public class UpgradeCoal
extends AUpgradeMisc {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        if (clayMan.hasUpgrade("blaze_powder")) {
            SoldierUpgradeInst bpUpgInst = clayMan.getUpgrade(SoldierUpgrades.getUpgrade("blaze_powder"));
            bpUpgInst.getNbtTag().setShort("uses", (short)(bpUpgInst.getNbtTag().getShort("uses") + 1));
        }
    }

    @Override
    public void onSoldierAttack(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target, MutableFloat damage) {
        if (clayMan.hasUpgrade("blazerod")) {
            target.setFire(6);
        }
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        return !clayMan.hasUpgrade("blazerod");
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.fizz", 1.0f, 1.0f);
    }

    @Override
    public void onProjectileHit(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, MovingObjectPosition target, ISoldierProjectile<? extends EntityThrowable> projectile) {
        if (target.entityHit != null && projectile instanceof EntityFirechargeChunk) {
            target.entityHit.setFire(6);
        }
    }

    @Override
    public boolean canBePickedUp(EntityClayMan clayMan, ItemStack stack, ASoldierUpgrade upgrade) {
        return clayMan.hasUpgrade("blazerod") || clayMan.hasUpgrade("blaze_powder") || clayMan.hasUpgrade("firecharge");
    }
}

