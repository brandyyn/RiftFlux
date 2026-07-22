/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import de.sanandrew.core.manpack.util.EnumNbtTypes;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgrades;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class UpgradeDiamond
extends AUpgradeMisc {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        ASoldierUpgrade[] upgrades;
        IAttributeInstance attrib = clayMan.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        attrib.setBaseValue(attrib.getBaseValue() * 10.0);
        clayMan.heal(clayMan.getMaxHealth());
        for (ASoldierUpgrade upgrade : upgrades = clayMan.getAvailableUpgrades()) {
            NBTTagCompound upgNbt = clayMan.getUpgrade(upgrade).getNbtTag();
            if (!upgNbt.hasKey("uses", EnumNbtTypes.NBT_SHORT.ordinal())) continue;
            upgNbt.setShort("uses", (short)(upgNbt.getShort("uses") * 2));
        }
        clayMan.addUpgrade(SoldierUpgrades.getUpgrade("sugar"));
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
        return !clayMan.hasUpgrade("diamond_block");
    }
}

