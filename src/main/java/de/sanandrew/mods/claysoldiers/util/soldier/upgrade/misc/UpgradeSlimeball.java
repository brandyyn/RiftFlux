/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc;

import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.projectile.ISoldierProjectile;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffects;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import java.util.ArrayList;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

public class UpgradeSlimeball
extends AUpgradeMisc {
    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        upgradeInst.getNbtTag().setShort("uses", (short)5);
    }

    @Override
    public void onSoldierDamage(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, EntityClayMan target) {
        if (target.addEffect(SoldierEffects.getEffect("slimefeet")) != null) {
            target.playSound("mob.slime.attack", 1.0f, 1.0f);
            upgradeInst.getNbtTag().setShort("uses", (short)(upgradeInst.getNbtTag().getShort("uses") - 1));
        }
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        return upgradeInst.getNbtTag().getShort("uses") == 0;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        if (stack.getItem() == Items.slime_ball) {
            this.consumeItem(stack, upgradeInst);
            clayMan.playSound("random.pop", 1.0f, 1.0f);
        } else if (stack.getItem() == Item.getItemFromBlock(Blocks.command_block)) {
            clayMan.playSound("mob.slime.small", 1.0f, 1.0f);
        }
    }

    @Override
    public void onProjectileHit(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, MovingObjectPosition target, ISoldierProjectile<? extends EntityThrowable> projectile) {
        EntityClayMan caddicarus;
        if (target.entityHit instanceof EntityClayMan && (caddicarus = (EntityClayMan)target.entityHit).addEffect(SoldierEffects.getEffect("slimefeet")) != null) {
            caddicarus.playSound("mob.slime.attack", 1.0f, 1.0f);
            upgradeInst.getNbtTag().setShort("uses", (short)(upgradeInst.getNbtTag().getShort("uses") - 1));
        }
    }

    @Override
    public void onItemDrop(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ArrayList<ItemStack> droppedItems) {
        if (upgradeInst.getNbtTag().getShort("uses") == 5) {
            droppedItems.add(upgradeInst.getStoredItem());
        }
    }
}

