/*
 * Decompiled with CFR 0.152.
 */
package zairus.worldexplorer.archery.items;

import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;

public class Quiver
extends WEItem {
    public Quiver() {
        this.setUnlocalizedName("quiver");
        this.setTextureName("worldexplorer:quiver");
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);
        this.setFull3D();
        this.maxStackSize = 1;
    }

    @Override
    public boolean holdsAmmo() {
        return true;
    }
}
