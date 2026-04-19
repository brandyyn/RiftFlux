/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.ClientRegistry
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.InputEvent$KeyInputEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.settings.KeyBinding
 */
package goki.stats.handlers;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import goki.stats.GokiStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;

public class GokiKeyHandler {
    public static KeyBinding statsMenu = new KeyBinding("Open Stats Menu", 21, "Goki Stats");
    public static KeyBinding compatibilityMenu = new KeyBinding("Open Stats Compatibility Helper", 35, "Goki Stats");

    public GokiKeyHandler() {
        ClientRegistry.registerKeyBinding((KeyBinding)statsMenu);
        ClientRegistry.registerKeyBinding((KeyBinding)compatibilityMenu);
    }

    @SubscribeEvent
    public void keyDown(InputEvent.KeyInputEvent event) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (statsMenu.isPressed()) {
            player.openGui((Object)GokiStats.instance, 0, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
        } else if (compatibilityMenu.isPressed()) {
            player.openGui((Object)GokiStats.instance, 1, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
        }
    }
}

