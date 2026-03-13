package com.voidsrift.riftflux.blessings;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

public final class BlessingContent {
    public static Block blessingPillar;
    private static boolean preInited;
    private static boolean initialized;

    private BlessingContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!ModConfig.blessingsEnabled) {
            blessingPillar = null;
            return;
        }

        blessingPillar = new BlockBlessingPillar();
        GameRegistry.registerBlock(blessingPillar, "blessing_pillar");
        GameRegistry.registerTileEntity(TileEntityBlessingPillar.class, Constants.MODID + ":blessing_pillar");
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        if (!ModConfig.blessingsEnabled) {
            return;
        }

        BlessingEvents events = new BlessingEvents();
        MinecraftForge.EVENT_BUS.register(events);
        FMLCommonHandler.instance().bus().register(events);

        if (ModConfig.blessingsPillarGenEnabled) {
            GameRegistry.registerWorldGenerator(new BlessingPillarGen(), 0);
        }
    }

    public static void initClient() {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        if (BlessingRenderIds.blessingPillarRenderId < 0) {
            BlessingRenderIds.blessingPillarRenderId = cpw.mods.fml.client.registry.RenderingRegistry.getNextAvailableRenderId();
            cpw.mods.fml.client.registry.RenderingRegistry.registerBlockHandler(
                    new com.voidsrift.riftflux.blessings.client.BlessingPillarBlockRenderer(
                            BlessingRenderIds.blessingPillarRenderId
                    )
            );
        }
        cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityBlessingPillar.class,
                new com.voidsrift.riftflux.blessings.client.RenderBlessingPillar()
        );
        if (blessingPillar != null) {
            net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
                    net.minecraft.item.Item.getItemFromBlock(blessingPillar),
                    new com.voidsrift.riftflux.blessings.client.BlessingPillarItemRenderer()
            );
        }
        BlessingKeyHandler.bootstrap();
    }

    public static void serverStarting(FMLServerStartingEvent event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        event.registerServerCommand(new CommandCurrentBlessing());
        event.registerServerCommand(new CommandSetBlessing());
    }
}
