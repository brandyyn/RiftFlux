package com.voidsrift.riftflux.placeditem;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import cpw.mods.fml.client.registry.ClientRegistry;

public final class PlacedItemContent {
    public static Block placedItemBlock;

    private PlacedItemContent() {
    }

    public static void init() {
        if (placedItemBlock != null) {
            return;
        }
        placedItemBlock = new BlockPlacedItem();
        GameRegistry.registerBlock(placedItemBlock, "riftflux_placed_item");
        GameRegistry.registerTileEntity(TilePlacedItem.class, "riftflux_placed_item");
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        ClientRegistry.bindTileEntitySpecialRenderer(TilePlacedItem.class, new RenderTilePlacedItem());
    }
}
