/*
 * Decompiled with CFR 0.152.
 */
package zairus.worldexplorer.equipment.items;

import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;

public class Hookshot
extends WEItem {
    public Hookshot() {
        this.setUnlocalizedName("hookshot");
        this.setTextureName("worldexplorer:hookshot");
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabTools);
        this.maxStackSize = 1;
        this.bFull3D = true;
    }
}
