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

public class StatMining
extends ToolSpecificStat {
    public StatMining(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public String getConfigurationKey() {
        return "Mining Tools";
    }

    @Override
    public float getBonus(int level) {
        return StatMining.getFinalBonus((float)Math.pow(level, 1.3) * 0.01523f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return "Mine " + Helper.trimDecimals(this.getBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% faster.";
    }

    @Override
    public String[] getDefaultSupportedItems() {
        return new String[]{Item.getIdFromItem((Item)Items.wooden_pickaxe) + ":0", Item.getIdFromItem((Item)Items.stone_pickaxe) + ":0", Item.getIdFromItem((Item)Items.iron_pickaxe) + ":0", Item.getIdFromItem((Item)Items.golden_pickaxe) + ":0", Item.getIdFromItem((Item)Items.diamond_pickaxe) + ":0"};
    }
}

