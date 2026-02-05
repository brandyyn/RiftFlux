/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.item.Item
 */
package zelda.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import zelda.Core;
import zelda.items.ItemHeartContainer;

public class ZItems {
    public static Item heartPiece;
    public static Item heartContainer;
    public static Item heart;

    public static void init() {
        heartPiece = new Item().setCreativeTab(Core.tab).setUnlocalizedName("heartPiece").setTextureName("zelda:heartPiece");
        GameRegistry.registerItem((Item)heartPiece, (String)"heartPiece");
        heartContainer = new ItemHeartContainer().setCreativeTab(Core.tab).setUnlocalizedName("heartContainer").setTextureName("zelda:heartContainer");
        GameRegistry.registerItem((Item)heartContainer, (String)"heartContainer");
        heart = new Item().setCreativeTab(Core.tab).setUnlocalizedName("heart").setTextureName("zelda:heart");
        GameRegistry.registerItem((Item)heart, (String)"heart");
    }
}

