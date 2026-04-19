/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 */
package goki.stats.lib;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class TreasureFinderEntry {
    public Block block;
    public Item item;
    public int blockMD = 0;
    public int itemMD = 0;
    public int miniumLevel = 0;
    public int chance = 0;

    public TreasureFinderEntry(Block block, int bMD, Item item, int iMD, int mL, int c) {
        this.block = block;
        this.item = item;
        this.blockMD = bMD;
        this.itemMD = iMD;
        this.miniumLevel = mL;
        this.chance = c;
    }

    public TreasureFinderEntry(String configString) {
        this.fromConfigurationString(configString);
    }

    public String toConfigurationString() {
        return Block.getIdFromBlock((Block)this.block) + "_" + this.blockMD + "_" + Item.getIdFromItem((Item)this.item) + "_" + this.itemMD + "_" + this.miniumLevel + "_" + this.chance;
    }

    public boolean fromConfigurationString(String configString) {
        boolean successful = false;
        try {
            String[] values = configString.split("_");
            int blockID = Integer.parseInt(values[0]);
            this.block = Block.getBlockById((int)blockID);
            this.blockMD = Integer.parseInt(values[1]);
            int itemID = Integer.parseInt(values[2]);
            this.item = Item.getItemById((int)itemID);
            this.itemMD = Integer.parseInt(values[3]);
            this.miniumLevel = Integer.parseInt(values[4]);
            this.chance = Integer.parseInt(values[5]);
        }
        catch (Exception e) {
            successful = false;
        }
        return successful;
    }
}

