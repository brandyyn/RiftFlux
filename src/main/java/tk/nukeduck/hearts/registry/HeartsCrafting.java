/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 */
package tk.nukeduck.hearts.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hearts.registry.HeartsBlocks;
import tk.nukeduck.hearts.registry.HeartsItems;

public class HeartsCrafting {
    public static void init() {
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(HeartsItems.shard, 3), (Object[])new Object[]{HeartsBlocks.crystal});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Block)HeartsBlocks.lantern), (Object[])new Object[]{"GCG", "GHG", "GGG", Character.valueOf('G'), Blocks.glass, Character.valueOf('C'), Blocks.hardened_clay, Character.valueOf('H'), HeartsBlocks.crystal});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Block)HeartsBlocks.lantern), (Object[])new Object[]{"GCG", "GHG", "GGG", Character.valueOf('G'), Blocks.glass, Character.valueOf('C'), Blocks.stained_hardened_clay, Character.valueOf('H'), HeartsBlocks.crystal});
    }
}

