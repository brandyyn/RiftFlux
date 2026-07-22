/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.soldier.EnumMethodState;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand.AUpgradeRightHanded;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableFloat;

public class UpgradeGoldMelon
extends AUpgradeRightHanded {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        upgradeInst.getNbtTag().setShort("uses", (short)25);
    }

    @Override
    public EnumMethodState onTargeting(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target) {
        return target.getClayTeam().equals(clayMan.getClayTeam()) && target.getHealth() < target.getMaxHealth() * 0.25f ? EnumMethodState.ALLOW : EnumMethodState.DENY;
    }

    @Override
    public void onSoldierAttack(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target, MutableFloat damage) {
        damage.setValue(0.0f);
        clayMan.heal(15.0f);
        ParticlePacketSender.sendSpellFx(target.posX, target.posY, target.posZ, target.dimension, 1.0, 0.0, 0.0);
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }

    @Override
    public void onItemDrop(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ArrayList<ItemStack> droppedItems) {
        if (upgradeInst.getNbtTag().getShort("uses") == 25) {
            droppedItems.add(upgradeInst.getStoredItem());
        }
    }
}

