/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.common.config.Configuration
 */
package goki.stats;

import goki.stats.ItemIdMetadataTuple;
import goki.stats.ItemIdMetadataTupleComparator;
import goki.stats.lib.Reference;
import goki.stats.stats.Stat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public abstract class ToolSpecificStat
extends Stat {
    public List<ItemIdMetadataTuple> supportedItems = new ArrayList<ItemIdMetadataTuple>();

    public ToolSpecificStat(int id, String key, String name, int limit) {
        super(id, key, name, limit);
    }

    public abstract String getConfigurationKey();

    public abstract String[] getDefaultSupportedItems();

    public void addSupportForItem(ItemStack item) {
        ItemIdMetadataTuple iimt;
        this.loadFromConfigurationFile(Reference.configuration);
        if (item == null) {
            return;
        }
        boolean hasSubtypes = item.getHasSubtypes();
        int id = Item.getIdFromItem((Item)item.getItem());
        int meta = 0;
        if (hasSubtypes) {
            meta = item.getItemDamage();
        }
        if (!this.supportedItems.contains(iimt = new ItemIdMetadataTuple(id, meta))) {
            this.supportedItems.add(iimt);
        }
        this.saveToConfigurationFile(Reference.configuration);
    }

    public void removeSupportForItem(ItemStack item) {
        if (item != null) {
            ItemIdMetadataTupleComparator iimtc = new ItemIdMetadataTupleComparator();
            this.loadFromConfigurationFile(Reference.configuration);
            ItemIdMetadataTuple iimt = new ItemIdMetadataTuple(Item.getIdFromItem((Item)item.getItem()), 0);
            if (item.getHasSubtypes()) {
                iimt.metadata = item.getItemDamage();
            }
            for (int i = 0; i < this.supportedItems.size(); ++i) {
                ItemIdMetadataTuple ii = this.supportedItems.get(i);
                if (iimtc.compare(iimt, ii) != 1) continue;
                this.supportedItems.remove(ii);
                --i;
            }
            this.saveToConfigurationFile(Reference.configuration);
        }
    }

    @Override
    public void loadFromConfigurationFile(Configuration config) {
        this.supportedItems.clear();
        String[] configStrings = Reference.configuration.get("Support", this.getConfigurationKey(), this.getDefaultSupportedItems()).getStringList();
        for (int i = 0; i < configStrings.length; ++i) {
            this.supportedItems.add(new ItemIdMetadataTuple(configStrings[i]));
        }
    }

    @Override
    public String toConfigurationString() {
        String configString = "";
        for (int i = 0; i < this.supportedItems.size(); ++i) {
            configString = configString + "," + this.supportedItems.get(i).toConfigString();
        }
        return configString.substring(1);
    }

    @Override
    public void saveToConfigurationFile(Configuration config) {
        String[] toolIDs = new String[this.supportedItems.size()];
        for (int i = 0; i < toolIDs.length; ++i) {
            toolIDs[i] = this.supportedItems.get(i).toConfigString();
        }
        Reference.configuration.get("Support", this.getConfigurationKey(), this.getDefaultSupportedItems()).set(toolIDs);
    }

    @Override
    public void fromConfigurationString(String configString) {
        this.supportedItems.clear();
        String[] configStringSplit = configString.split(",");
        for (int i = 0; i < configStringSplit.length; ++i) {
            this.supportedItems.add(new ItemIdMetadataTuple(configStringSplit[i]));
        }
    }

    public boolean isItemSupported(ItemStack item) {
        for (int i = 0; i < this.supportedItems.size(); ++i) {
            ItemIdMetadataTuple iimt = this.supportedItems.get(i);
            if (Item.getIdFromItem((Item)item.getItem()) != iimt.id) continue;
            if (item.getHasSubtypes() && item.getItemDamage() == iimt.metadata) {
                return true;
            }
            if (item.getHasSubtypes()) continue;
            return true;
        }
        return false;
    }

    @Override
    public int isAffectedByStat(Object object) {
        ItemStack item;
        if (object != null && object instanceof ItemStack && this.isItemSupported(item = (ItemStack)object)) {
            return 1;
        }
        return 0;
    }

    @Override
    public float getAppliedBonus(EntityPlayer player, Object object) {
        return this.getBonus(player) * (float)this.isAffectedByStat(object);
    }
}

