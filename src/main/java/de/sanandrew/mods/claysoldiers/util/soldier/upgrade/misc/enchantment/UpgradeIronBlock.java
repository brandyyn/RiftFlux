/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableFloat
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.enchantment;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.ASoldierEffect;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffects;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.AUpgradeMisc;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeBlazePowder;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.mutable.MutableFloat;

public class UpgradeIronBlock
extends AUpgradeMisc {
    public ASoldierEffect[] blockableEffects = new ASoldierEffect[]{SoldierEffects.getEffect("slowmotion"), SoldierEffects.getEffect("slimefeet"), SoldierEffects.getEffect("magmabomb"), SoldierEffects.getEffect("slowmotion")};

    @Override
    public boolean onSoldierHurt(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, DamageSource source, MutableFloat damage) {
        damage.setValue(Math.max(0.0f, damage.getValue().floatValue() - 1.0f));
        if (SAPUtils.RNG.nextBoolean()) {
            if (source == UpgradeBlazePowder.BLAZEPOWDER_DAMAGE_SRC) {
                return true;
            }
            if (clayMan.isPotionActive(Potion.poison)) {
                clayMan.removePotionEffect(Potion.poison.getId());
            }
            for (ASoldierEffect effect : this.blockableEffects) {
                if (!clayMan.hasEffect(effect)) continue;
                clayMan.removeEffect(effect);
            }
        }
        return false;
    }

    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        return !clayMan.hasUpgrade("bowl");
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }

    @Override
    public boolean canBePickedUp(EntityClayMan clayMan, ItemStack stack, ASoldierUpgrade upgrade) {
        return clayMan.hasUpgrade("bowl");
    }
}

