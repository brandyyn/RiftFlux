/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.item.ItemClayManDoll;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

public class UpgradeGhastTear
extends AUpgradeMisc {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        upgradeInst.getNbtTag().setShort("uses", (short)2);
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        EntityItem item;
        short uses = upgradeInst.getNbtTag().getShort("uses");
        if (!clayMan.hasPath()) {
            Collection<EntityItem> items = clayMan.getItemsInRange();
            for (EntityItem item2 : items) {
                if (item2.getEntityItem() == null || item2.getEntityItem().getItem() != RegistryItems.dollBrick) continue;
                clayMan.setTargetFollowing(item2);
                break;
            }
        } else if (clayMan.getTargetFollowing() instanceof EntityItem && (item = (EntityItem)clayMan.getTargetFollowing()).getEntityItem() != null && item.getEntityItem().getItem() == RegistryItems.dollBrick && item.getDistanceSqToEntity(clayMan) < 1.0 && item.getEntityItem().stackSize > 0) {
            EntityClayMan awakened = ItemClayManDoll.spawnClayMan(clayMan.worldObj, clayMan.getClayTeam(), item.posX, item.posY, item.posZ);
            awakened.playSound("dig.gravel", 1.0f, 1.0f);
            ParticlePacketSender.sendSoldierDeathFx(awakened.posX, awakened.posY, awakened.posZ, awakened.dimension, awakened.getClayTeam());
            awakened.dollItem = new ItemStack(RegistryItems.dollSoldier, 1);
            awakened.dollItem.setTagCompound(item.getEntityItem().getTagCompound());
            ItemClayManDoll.setTeamForItem(awakened.getClayTeam(), awakened.dollItem);
            item.getEntityItem().splitStack(1);
            if (item.getEntityItem().stackSize == 0) {
                item.setDead();
            }
            uses = (short)(uses - 1);
            upgradeInst.getNbtTag().setShort("uses", uses);
            if (uses == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }

    @Override
    public void onItemDrop(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ArrayList<ItemStack> droppedItems) {
        if (upgradeInst.getNbtTag().getShort("uses") == 2) {
            droppedItems.add(upgradeInst.getStoredItem());
        }
    }
}

