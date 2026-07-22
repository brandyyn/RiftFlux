/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeFood;
import java.util.ArrayList;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class UpgradeBrownMushroom
extends UpgradeFood {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        upgradeInst.getNbtTag().setShort("uses", (short)2);
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
        upgradeInst.getNbtTag().setFloat("healAmount", 10.0f);
    }

    @Override
    protected void spawnParticles(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        ParticlePacketSender.sendDiggingFx(clayMan.posX, clayMan.posY, clayMan.posZ, clayMan.dimension, Blocks.brown_mushroom);
    }

    @Override
    public void onItemDrop(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ArrayList<ItemStack> droppedItems) {
        if (upgradeInst.getNbtTag().getShort("uses") == 2) {
            droppedItems.add(upgradeInst.getStoredItem());
        }
    }
}

