/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.IExplosiveUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

public class UpgradeFireworkStar
extends AUpgradeMisc
implements IExplosiveUpgrade {
    @Override
    public void onClientUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        if (clayMan.getHealth() <= 0.0f) {
            clayMan.worldObj.makeFireworks(clayMan.posX, clayMan.posY + (double)clayMan.getEyeHeight(), clayMan.posZ, 0.0, 0.0, 0.0, upgradeInst.getNbtTag());
        }
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Explosion")) {
            NBTTagList explosionList = new NBTTagList();
            explosionList.appendTag(stack.getTagCompound().getCompoundTag("Explosion"));
            upgradeInst.getNbtTag().setTag("Explosions", explosionList);
        }
    }

    @Override
    public boolean shouldNbtSyncToClient(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        return true;
    }

    @Override
    public boolean canBePickedUp(EntityClayMan clayMan, ItemStack stack, ASoldierUpgrade upgrade) {
        return !clayMan.hasUpgrade(IExplosiveUpgrade.class);
    }
}

