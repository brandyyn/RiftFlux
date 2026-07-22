/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.IMeeleeUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand.AUpgradeRightHanded;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.mutable.MutableFloat;

public class UpgradeShearBladeRight
extends AUpgradeRightHanded
implements IMeeleeUpgrade {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        NBTTagCompound nbt = upgradeInst.getNbtTag();
        nbt.setShort("uses", (short)25);
    }

    @Override
    public void onSoldierAttack(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target, MutableFloat damage) {
        float baseDmg = 1.0f;
        if (clayMan.hasUpgrade("egg") && target.getEntityToAttack() == null) {
            baseDmg = 3.0f;
            ParticlePacketSender.sendCritFx(target.posX, target.posY, target.posZ, target.dimension);
        }
        damage.add(baseDmg + clayMan.getRNG().nextFloat());
    }

    @Override
    public void onSoldierDamage(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target) {
        upgradeInst.getNbtTag().setShort("uses", (short)(upgradeInst.getNbtTag().getShort("uses") - 1));
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        if (upgradeInst.getNbtTag().getShort("uses") <= 0) {
            clayMan.playSound("random.break", 1.0f, 1.0f);
            ParticlePacketSender.sendBreakFx(clayMan.posX, clayMan.posY, clayMan.posZ, clayMan.dimension, RegistryItems.shearBlade);
            return true;
        }
        return false;
    }

    @Override
    public void onItemDrop(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ArrayList<ItemStack> droppedItems) {
        if (upgradeInst.getNbtTag().getShort("uses") == 25) {
            droppedItems.add(upgradeInst.getStoredItem());
        }
    }
}

