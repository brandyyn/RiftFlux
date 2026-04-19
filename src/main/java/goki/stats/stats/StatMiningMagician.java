/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 */
package goki.stats.stats;

import goki.stats.lib.Helper;
import goki.stats.lib.IDMDTuple;
import goki.stats.stats.Stat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class StatMiningMagician
extends Stat {
    public static List<IDMDTuple> blockEntries = new ArrayList<IDMDTuple>();
    private static IDMDTuple[] defaultBlockEntries = new IDMDTuple[]{new IDMDTuple(Blocks.coal_ore, 0), new IDMDTuple(Blocks.diamond_ore, 0), new IDMDTuple(Blocks.emerald_ore, 0), new IDMDTuple(Blocks.gold_ore, 0), new IDMDTuple(Blocks.iron_ore, 0), new IDMDTuple(Blocks.lapis_ore, 0), new IDMDTuple(Blocks.quartz_ore, 0), new IDMDTuple(Blocks.redstone_ore, 0)};
    public static List<IDMDTuple> itemEntries = new ArrayList<IDMDTuple>();
    private static IDMDTuple[] defaultItemEntries = new IDMDTuple[]{new IDMDTuple(Items.coal, 0), new IDMDTuple(Items.diamond, 0), new IDMDTuple(Items.emerald, 0), new IDMDTuple(Items.gold_ingot, 0), new IDMDTuple(Items.iron_ingot, 0), new IDMDTuple(Items.dye, 4), new IDMDTuple(Items.quartz, 0), new IDMDTuple(Items.redstone, 0)};

    public StatMiningMagician(int id, String key, String name, int limit) {
        super(id, key, name, limit);
        for (IDMDTuple mme : defaultBlockEntries) {
            blockEntries.add(mme);
        }
        for (IDMDTuple mme : defaultItemEntries) {
            itemEntries.add(mme);
        }
    }

    @Override
    public float getBonus(int level) {
        return StatMiningMagician.getFinalBonus((float)level * 0.3f);
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return Helper.trimDecimals(this.getBonus(player), 1) + "% chance to transform an ore when harvesting.";
    }

    @Override
    public int isAffectedByStat(Object object) {
        if (object instanceof IDMDTuple) {
            IDMDTuple idmd = (IDMDTuple)object;
            for (IDMDTuple entry : blockEntries) {
                if (!idmd.equals(entry)) continue;
                return 1;
            }
            for (IDMDTuple entry : itemEntries) {
                if (!idmd.equals(entry)) continue;
                return 1;
            }
        }
        return super.isAffectedByStat(object);
    }
}

