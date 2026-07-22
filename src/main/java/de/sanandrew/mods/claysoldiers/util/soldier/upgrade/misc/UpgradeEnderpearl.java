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
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgrades;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.mutable.MutableFloat;

public class UpgradeEnderpearl
extends AUpgradeMisc {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        IAttributeInstance attrib = clayMan.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        attrib.setBaseValue(attrib.getBaseValue() + 5.0);
        clayMan.heal(clayMan.getMaxHealth());
        upgradeInst.getNbtTag().setShort("ticksActive", (short)0);
    }

    @Override
    public EnumMethodState onTargeting(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target) {
        if (clayMan.getClayTeam().equals(target.getClayTeam()) && !target.hasUpgrade("enderpearl")) {
            return EnumMethodState.ALLOW;
        }
        return EnumMethodState.SKIP;
    }

    @Override
    public EnumMethodState onBeingTargeted(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan attacker) {
        return attacker.hasUpgrade("enderpearl") ? EnumMethodState.SKIP : EnumMethodState.ALLOW;
    }

    @Override
    public void onSoldierAttack(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target, MutableFloat damage) {
        if (target.getEntityToAttack() == null) {
            target.setTarget(clayMan);
        }
        if (damage.getValue().floatValue() >= target.getHealth()) {
            target.addUpgrade(SoldierUpgrades.getUpgrade("enderpearl"));
            clayMan.heal(clayMan.getMaxHealth());
            damage.setValue(0.0f);
        }
        upgradeInst.getNbtTag().setShort("ticksActive", (short)0);
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        short ticksAlive = upgradeInst.getNbtTag().getShort("ticksActive");
        ticksAlive = (short)(ticksAlive + 1);
        upgradeInst.getNbtTag().setShort("ticksActive", ticksAlive);
        if (!(clayMan.getEntityToAttack() instanceof EntityClayMan) || !((EntityClayMan)clayMan.getEntityToAttack()).getClayTeam().equals(clayMan.getClayTeam())) {
            EntityPlayer closestPlayer = clayMan.worldObj.getClosestPlayer(clayMan.posX, clayMan.posY, clayMan.posZ, clayMan.getLookRangeRad());
            if (!(clayMan.getEntityToAttack() instanceof EntityPlayer || closestPlayer == null || closestPlayer.isDead || closestPlayer.isEntityInvulnerable() || closestPlayer.capabilities.isCreativeMode)) {
                clayMan.setTarget(closestPlayer);
            }
        }
        if (ticksAlive == 12000) {
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

