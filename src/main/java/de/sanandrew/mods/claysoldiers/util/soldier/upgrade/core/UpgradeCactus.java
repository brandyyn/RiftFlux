/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLLog
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade.core;

import cpw.mods.fml.common.FMLLog;
import de.sanandrew.core.manpack.util.SAPReflectionHelper;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.core.AUpgradeCore;
import java.lang.reflect.Field;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

public class UpgradeCactus
extends AUpgradeCore {
    @Override
    public boolean onUpdate(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst) {
        Field cmFireField = SAPReflectionHelper.getCachedField(Entity.class, "field_70151_c", "fire");
        try {
            int fire = cmFireField.getInt(clayMan);
            if (fire > 0 && clayMan.getRNG().nextInt(10) == 0) {
                cmFireField.setInt(clayMan, clayMan.getRNG().nextInt(4) != 0 ? 0 : fire / 2);
            }
        }
        catch (IllegalAccessException e) {
            FMLLog.log((String)"ClaySoldiers", (Level)Level.WARN, (String)"Couldn't access fire field for cactus upgrade!", (Object[])new Object[0]);
        }
        return false;
    }

    @Override
    public void onPickup(EntityClayMan clayMan, SoldierUpgradeInst upgradeInst, ItemStack stack) {
        this.consumeItem(stack, upgradeInst);
        clayMan.playSound("random.pop", 1.0f, 1.0f);
    }
}

