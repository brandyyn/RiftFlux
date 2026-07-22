/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.mutable.MutableFloat;

public class UpgradeFood
extends AUpgradeMisc {
    private static List<ItemFood> s_excludedFood = new ArrayList<ItemFood>();

    public static boolean isFoodExcluded(ItemFood food) {
        return s_excludedFood.contains(food);
    }

    public static void excludeFood(ItemFood food) {
        s_excludedFood.add(food);
    }

    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        upgradeInst.getNbtTag().setShort("uses", (short)4);
    }

    @Override
    public boolean onSoldierHurt(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, DamageSource source, MutableFloat damage) {
        if (clayMan.getHealth() + upgradeInst.getNbtTag().getFloat("healAmount") - damage.getValue().floatValue() <= 0.0f) {
            upgradeInst.getNbtTag().setBoolean("killed", true);
        }
        return false;
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        if (clayMan.getHealth() < clayMan.getMaxHealth() * 0.25f && !upgradeInst.getNbtTag().getBoolean("killed")) {
            upgradeInst.getNbtTag().setShort("uses", (short)(upgradeInst.getNbtTag().getShort("uses") - 1));
            clayMan.heal(upgradeInst.getNbtTag().getFloat("healAmount"));
            this.spawnParticles(clayMan, upgradeInst);
            clayMan.playSound("random.eat", 1.0f, 0.9f + SAPUtils.RNG.nextFloat() * 0.2f);
        }
        if (upgradeInst.getNbtTag().getShort("uses") == 0) {
            if (upgradeInst.getStoredItem().getItem() instanceof ItemSoup) {
                clayMan.entityDropItem(new ItemStack(Items.bowl, 1), 0.0f);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
        upgradeInst.getNbtTag().setFloat("healAmount", ((ItemFood)stack.getItem()).func_150905_g(stack));
    }

    protected void spawnParticles(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        ParticlePacketSender.sendBreakFx(clayMan.posX, clayMan.posY, clayMan.posZ, clayMan.dimension, upgradeInst.getStoredItem().getItem());
    }

    @Override
    public void onItemDrop(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ArrayList<ItemStack> droppedItems) {
        if (upgradeInst.getNbtTag().getShort("uses") == 4) {
            droppedItems.add(upgradeInst.getStoredItem());
        }
    }
}

