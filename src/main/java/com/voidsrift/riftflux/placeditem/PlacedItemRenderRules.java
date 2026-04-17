package com.voidsrift.riftflux.placeditem;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

final class PlacedItemRenderRules {

    private PlacedItemRenderRules() {
    }

    /**
     * Treat only full-cube ItemBlocks as block-like placed items.
     * Everything else (plants, crops, crossed blocks, non-cube blocks, icon-style items)
     * uses flat item placement bounds.
     */
    static boolean isBlockLikePlacedItem(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemBlock)) {
            return false;
        }
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == null) {
            return false;
        }
        return block.getRenderType() == 0
                && block.renderAsNormalBlock()
                && block.isOpaqueCube();
    }
}
