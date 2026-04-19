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
import goki.stats.stats.Stat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class StatChopping
extends ToolSpecificStat {
    public StatChopping(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public String getConfigurationKey() {
        return "Chopping Tools";
    }

    @Override
    public float getBonus(int level) {
        return Stat.getFinalBonus((float)Math.pow(level, 1.3) * 0.01523f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return "Chop " + Helper.trimDecimals(this.getBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% faster.";
    }

    @Override
    public String[] getDefaultSupportedItems() {
        return new String[]{Item.getIdFromItem((Item)Items.wooden_axe) + ":0", Item.getIdFromItem((Item)Items.stone_axe) + ":0", Item.getIdFromItem((Item)Items.iron_axe) + ":0", Item.getIdFromItem((Item)Items.golden_axe) + ":0", Item.getIdFromItem((Item)Items.diamond_axe) + ":0"};
    }
}

