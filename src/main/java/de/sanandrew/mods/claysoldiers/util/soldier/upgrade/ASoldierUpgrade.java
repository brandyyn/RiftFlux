/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableDouble
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.projectile.ISoldierProjectile;
import de.sanandrew.mods.claysoldiers.util.soldier.EnumMethodState;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableFloat;

public abstract class ASoldierUpgrade {
    public static final String NBT_USES = "uses";

    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
    }

    public EnumMethodState onTargeting(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target) {
        return EnumMethodState.SKIP;
    }

    public EnumMethodState onBeingTargeted(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan attacker) {
        return EnumMethodState.SKIP;
    }

    public void onSoldierAttack(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target, MutableFloat damage) {
    }

    public void getAttackRange(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, Entity target, MutableFloat attackRange) {
    }

    public void onSoldierDamage(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target) {
    }

    public void onSoldierDeath(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, DamageSource source) {
    }

    public boolean onSoldierHurt(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, DamageSource source, MutableFloat damage) {
        return false;
    }

    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        return false;
    }

    public void onClientUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
    }

    public abstract boolean canBePickedUp(EntityClayMan var1, ItemStack var2, ASoldierUpgrade var3);

    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
    }

    public void getAiMoveSpeed(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, MutableFloat speed) {
    }

    public void onProjectileHit(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, MovingObjectPosition target, ISoldierProjectile<? extends EntityThrowable> projectile) {
    }

    public void getLookRange(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, MutableDouble radius) {
    }

    public boolean shouldNbtSyncToClient(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        return false;
    }

    public void onUpgradeAdded(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, SoldierUpgradeInst appliedUpgradeInst) {
    }

    public void consumeItem(ItemStack stack, SoldierUpgradeInst upgradeInst) {
        upgradeInst.setStoredItem(stack.splitStack(1));
    }

    public abstract void onItemDrop(EntityClayMan var1, SoldierUpgradeInst var2, ArrayList<ItemStack> var3);
}

