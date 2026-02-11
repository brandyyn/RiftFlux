package com.voidsrift.riftflux.dualhotbar;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public final class DualHotbarClient {
    private DualHotbarClient() {
    }

    public static void init() {
        RenderHandler renderHandler = new RenderHandler();
        InventoryChangeHandler inventoryChangeHandler = new InventoryChangeHandler();

        inventoryChangeHandler.selectKey = new KeyBinding(
                "Hold + Number for second hotbar",
                Keyboard.KEY_NONE,
                "RiftFlux"
        );
        ClientRegistry.registerKeyBinding(inventoryChangeHandler.selectKey);

        MinecraftForge.EVENT_BUS.register(renderHandler);
        FMLCommonHandler.instance().bus().register(inventoryChangeHandler);
        FMLCommonHandler.instance().bus().register(new DualHotbarClientEvents());
    }
}
