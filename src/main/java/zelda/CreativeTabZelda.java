/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 */
package zelda;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import zelda.items.ZItems;

public class CreativeTabZelda
extends CreativeTabs {
    public CreativeTabZelda() {
        super(CreativeTabZelda.getNextID(), "zelda");
    }

    public Item getTabIconItem() {
        return ZItems.heartContainer;
    }
}

