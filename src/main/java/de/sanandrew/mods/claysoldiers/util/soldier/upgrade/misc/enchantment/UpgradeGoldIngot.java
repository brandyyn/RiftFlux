/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.enchantment;

import de.sanandrew.core.manpack.util.EnumNbtTypes;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.mutable.MutableFloat;

public class UpgradeGoldIngot
extends AUpgradeMisc {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        ASoldierUpgrade[] upgrades;
        for (ASoldierUpgrade upgrade : upgrades = clayMan.getAvailableUpgrades()) {
            NBTTagCompound upgNbt = clayMan.getUpgrade(upgrade).getNbtTag();
            if (!upgNbt.hasKey("uses", EnumNbtTypes.NBT_SHORT.ordinal())) continue;
            upgNbt.setShort("uses", (short)(upgNbt.getShort("uses") * 2));
        }
    }

    @Override
    public void onSoldierAttack(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target, MutableFloat damage) {
        damage.add(SAPUtils.RNG.nextFloat() + 1.0f);
    }

    @Override
    public boolean onSoldierHurt(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, DamageSource source, MutableFloat damage) {
        damage.setValue(Math.max(0.25f, damage.getValue().floatValue() - 1.0f));
        return false;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }

    @Override
    public void onUpgradeAdded(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, SoldierUpgradeInst appliedUpgradeInst) {
        NBTTagCompound upgNbt = appliedUpgradeInst.getNbtTag();
        if (upgNbt.hasKey("uses", EnumNbtTypes.NBT_SHORT.ordinal())) {
            upgNbt.setShort("uses", (short)(upgNbt.getShort("uses") * 2));
        }
    }

    @Override
    public boolean canBePickedUp(EntityClayMan clayMan, ItemStack stack, ASoldierUpgrade upgrade) {
        return clayMan.hasUpgrade("gold_nugget");
    }
}

