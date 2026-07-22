/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.behavior;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.soldier.EnumMethodState;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.behavior.AUpgradeBehavior;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class UpgradeNetherwart
extends AUpgradeBehavior {
    @Override
    public EnumMethodState onTargeting(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target) {
        return EnumMethodState.ALLOW;
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        if (!clayMan.worldObj.isRemote && clayMan.ticksExisted % 20 == 0 && SAPUtils.RNG.nextInt(10) == 0) {
            ParticlePacketSender.sendBreakFx(clayMan.posX, clayMan.posY, clayMan.posZ, clayMan.dimension, Items.nether_wart);
        }
        return super.onUpdate(clayMan, upgradeInst);
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }
}

