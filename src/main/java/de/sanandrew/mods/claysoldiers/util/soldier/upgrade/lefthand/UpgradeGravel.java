/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.lefthand;

import de.sanandrew.core.manpack.util.client.helpers.ItemRenderHelper;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.projectile.EntityGravelChunk;
import de.sanandrew.mods.claysoldiers.entity.projectile.ISoldierProjectile;
import de.sanandrew.mods.claysoldiers.tileentity.TileEntityClayNexus;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.IThrowableUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.lefthand.AUpgradeLeftHanded;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableFloat;

public class UpgradeGravel
extends AUpgradeLeftHanded
implements IThrowableUpgrade {
    private ItemStack p_nexusItem = new ItemStack(Blocks.gravel);

    @Override
    public void onConstruct(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        upgradeInst.getNbtTag().setShort("uses", (short)15);
    }

    @Override
    public void getAttackRange(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, Entity target, MutableFloat attackRange) {
        boolean isInRange;
        boolean bl = isInRange = target.getDistanceSqToEntity(clayMan) <= 64.0;
        if (target instanceof EntityLivingBase && !target.isDead && clayMan.canEntityBeSeen(target) && ((EntityLivingBase)target).getHealth() > 0.0f && isInRange) {
            clayMan.throwSomethingAtEnemy((EntityLivingBase)target, EntityGravelChunk.class, clayMan.hasUpgrade("sugarcane"));
            clayMan.attackTime = 30;
            upgradeInst.getNbtTag().setShort("uses", (short)(upgradeInst.getNbtTag().getShort("uses") - 1));
        }
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        return upgradeInst.getNbtTag().getShort("uses") == 0;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }

    @Override
    public void getAiMoveSpeed(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, MutableFloat speed) {
        Entity target = clayMan.getEntityToAttack();
        if (target instanceof EntityLivingBase && !target.isDead && clayMan.canEntityBeSeen(target) && ((EntityLivingBase)target).getHealth() > 0.0f) {
            float multiplier = Math.min(1.0f, Math.max(-1.0f, (float)clayMan.getDistanceSqToEntity(target) - 16.0f));
            speed.setValue(speed.getValue().floatValue() * multiplier);
        }
    }

    @Override
    public Class<? extends ISoldierProjectile<? extends EntityThrowable>> getThrowableClass() {
        return EntityGravelChunk.class;
    }

    @Override
    public void renderNexusThrowable(TileEntityClayNexus nexus, float partTicks) {
        ItemRenderHelper.renderItemIn3D(this.p_nexusItem);
    }

    @Override
    public void onItemDrop(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ArrayList<ItemStack> droppedItems) {
        if (upgradeInst.getNbtTag().getShort("uses") == 15) {
            droppedItems.add(upgradeInst.getStoredItem());
        }
    }
}

