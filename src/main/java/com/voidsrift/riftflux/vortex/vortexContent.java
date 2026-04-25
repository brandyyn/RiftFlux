package com.voidsrift.riftflux.vortex;

import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.vortex.event.EntityEventHandler;
import com.voidsrift.riftflux.vortex.event.KeyEventHandler;
import com.voidsrift.riftflux.vortex.event.ModEvents;
import com.voidsrift.riftflux.vortex.event.RespawnDelayEventHandler;
import com.voidsrift.riftflux.vortex.event.WorldEventHandler;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.crafting.ModRecipes;
import com.voidsrift.riftflux.vortex.lib.helper.ModHelpers;
import com.voidsrift.riftflux.vortex.network.ModPackets;
import com.voidsrift.riftflux.vortex.proxy.GuiProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;


public final class vortexContent {
    private static boolean clientHooksRegistered = false;

    private vortexContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        // Potions are referenced by several items (e.g. Highland Spirit). If this isn't
        // initialized, those items will NPE on the server.
        com.voidsrift.riftflux.vortex.potion.ModPotions.init();
        ModItems.init();
        ModPackets.init();
        ModHelpers.init();
    }

    public static void init(FMLInitializationEvent event) {
        ModRecipes.init();
        registerDungeonLoot();

        // Events
        MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.vortex.event.CraftingEventHandler());
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
        RespawnDelayEventHandler respawnDelayEventHandler = new RespawnDelayEventHandler();
        MinecraftForge.EVENT_BUS.register(respawnDelayEventHandler);
        FMLCommonHandler.instance().bus().register(respawnDelayEventHandler);
        if (com.voidsrift.riftflux.ModConfig.enableUnloader) {
            WorldEventHandler worldEventHandler = new WorldEventHandler();
            MinecraftForge.EVENT_BUS.register(worldEventHandler);
            FMLCommonHandler.instance().bus().register(worldEventHandler);
        }

        // GUI
        NetworkRegistry.INSTANCE.registerGuiHandler(riftflux.instance, new GuiProxy());

        // Client-only
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            initClient();
        }
    }

    public static void initClient() {
        if (clientHooksRegistered) {
            return;
        }
        clientHooksRegistered = true;

        try {
            FMLCommonHandler.instance().bus().register(new KeyEventHandler());
            // Key bindings are client-only and must be registered explicitly.
            KeyEventHandler.registerKeyBindings();
        } catch (Throwable ignored) {
        }
        try {
            com.voidsrift.riftflux.vortex.client.render.ModRenderers.init();
        } catch (Throwable ignored) {
        }

        // Client-only GUI hooks (e.g. Backpack inventory button).
        try {
            com.voidsrift.riftflux.vortex.event.GuiEventHandler guiEventHandler =
                    new com.voidsrift.riftflux.vortex.event.GuiEventHandler();
            MinecraftForge.EVENT_BUS.register(guiEventHandler);
            FMLCommonHandler.instance().bus().register(guiEventHandler);
            MinecraftForge.EVENT_BUS.register(new com.voidsrift.riftflux.vortex.event.RenderEventHandler());
        } catch (Throwable ignored) {
        }
    }

    public static void postInit(FMLPostInitializationEvent event) {
        // Preserve optional compat hooks inside their own modules.
        ModItems.postInitCompat();
    }

    private static void registerDungeonLoot() {
        if (ModItems.poptart != null) {
            int poptartWeight = Math.max(0, com.voidsrift.riftflux.ModConfig.poptartDungeonLootWeight);
            if (poptartWeight > 0) {
                ChestGenHooks.addItem(
                        ChestGenHooks.DUNGEON_CHEST,
                        new WeightedRandomChestContent(new ItemStack(ModItems.poptart), 1, 2, poptartWeight)
                );
            }
        }

        if (!com.voidsrift.riftflux.ModConfig.vortexGlintRuneDungeonLoot || ModItems.glintRune == null) {
            return;
        }

        for (int meta = 0; meta < 17; ++meta) {
            ChestGenHooks.addItem(
                    ChestGenHooks.DUNGEON_CHEST,
                    new WeightedRandomChestContent(new ItemStack(ModItems.glintRune, 1, meta), 1, 1, 1)
            );
        }
    }
}
