package com.voidsrift.riftflux.placeablegunpowder;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.tweaks.ladder.client.RFRenderIds;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;

public final class PlaceableGunpowderContent {

    public static BlockGunpowder gunpowderBlock;

    private PlaceableGunpowderContent() {
    }

    public static void preInit() {
        if (!ModConfig.enablePlaceableGunpowder || gunpowderBlock != null) {
            return;
        }

        gunpowderBlock = (BlockGunpowder) new BlockGunpowder()
                .setHardness(0.0F)
                .setStepSound(Block.soundTypeStone)
                .setBlockName("gunpowder_block")
                .setBlockTextureName("redstone_dust");
        GameRegistry.registerBlock(gunpowderBlock, "gunpowder_block");
        Blocks.fire.setFireInfo(gunpowderBlock, 15, 20);
        MinecraftForge.EVENT_BUS.register(new GunpowderEventHandler());
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        if (!ModConfig.enablePlaceableGunpowder || gunpowderBlock == null || RFRenderIds.placeableGunpowderRenderId >= 0) {
            return;
        }

        RFRenderIds.placeableGunpowderRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderGunpowder());
    }
}
