/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.item.Item
 */
package tk.nukeduck.hearts.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import tk.nukeduck.hearts.item.ItemHeartShard;

public class HeartsItems {
    public static Item shard = new ItemHeartShard();

    public static void init() {
        shard = new ItemHeartShard().setUnlocalizedName("heartShard");
        GameRegistry.registerItem((Item)shard, (String)"heart_shard");
    }
}

