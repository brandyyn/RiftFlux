/*
 * Decompiled with CFR 0.152.
 */
package zairus.worldexplorer.equipment.items;

import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;

public class Rope
extends WEItem {
    public Rope() {
        this.setUnlocalizedName("rope");
        this.setTextureName("worldexplorer:rope");
        this.setCreativeTab(WorldExplorer.tabWorldExplorer);
        this.maxStackSize = 1;
    }
}

