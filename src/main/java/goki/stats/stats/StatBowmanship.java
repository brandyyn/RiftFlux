/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 */
package goki.stats.stats;

import goki.stats.ToolSpecificStat;
import goki.stats.lib.Helper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class StatBowmanship
extends ToolSpecificStat {
    public StatBowmanship(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public String getConfigurationKey() {
        return "Bowmanship Tools";
    }

    @Override
    public float getBonus(int level) {
        return StatBowmanship.getFinalBonus((float)Math.pow(level, 1.0895) * 0.03f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return "Deal " + Helper.trimDecimals(this.getBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% more damage with a bow.";
    }

    @Override
    public String[] getDefaultSupportedItems() {
        return new String[]{Item.getIdFromItem((Item)Items.bow) + ":0"};
    }
}

