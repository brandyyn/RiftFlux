package com.voidsrift.riftflux.gokistats;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import goki.stats.client.gui.GuiStats;

@SideOnly(Side.CLIENT)
public final class GokiStatsClientHooks {
    private static final String CATEGORY = "RiftFlux";
    private static final KeyBinding STATS_MENU = new KeyBinding("Open Stats Menu", 21, CATEGORY);
    private static boolean registered;

    private GokiStatsClientHooks() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        ClientRegistry.registerKeyBinding(STATS_MENU);
        FMLCommonHandler.instance().bus().register(new GokiStatsClientHooks());
    }

    @SubscribeEvent
    public void keyDown(InputEvent.KeyInputEvent event) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            return;
        }
        if (STATS_MENU.getIsKeyPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiStats(player));
        }
    }
}
