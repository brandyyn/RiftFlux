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

public class StatSwordsmanship
extends ToolSpecificStat {
    public StatSwordsmanship(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    @Override
    public String getConfigurationKey() {
        return "Swordsmanship Tools";
    }

    @Override
    public float getBonus(int level) {
        return StatSwordsmanship.getFinalBonus((float)Math.pow(level, 1.0895) * 0.03f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return "Deal " + Helper.trimDecimals(this.getBonus(this.getPlayerStatLevel(player)) * 100.0f, 1) + "% more damage with swords.";
    }

    @Override
    public String[] getDefaultSupportedItems() {
        return new String[]{Item.getIdFromItem((Item)Items.wooden_sword) + ":0", Item.getIdFromItem((Item)Items.stone_sword) + ":0", Item.getIdFromItem((Item)Items.iron_sword) + ":0", Item.getIdFromItem((Item)Items.golden_sword) + ":0", Item.getIdFromItem((Item)Items.diamond_sword) + ":0"};
    }
}

