package com.voidsrift.riftflux.vortex.event;

import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import baubles.api.BaublesApi;
import com.voidsrift.riftflux.vortex.lib.container.InventoryToolbelt;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;
import com.voidsrift.riftflux.vortex.network.ModPackets;
import com.voidsrift.riftflux.vortex.network.PacketToolbeltSwap;
import net.minecraft.item.ItemStack;

/**
 * Client-side key handler for vortex content.
 *
 * TRMactive is consumed by REHToolbeltHelper (radial/tool overlay logic).
 */
public class KeyEventHandler {

    public static final KeyBinding keyX = new KeyBinding("Cycle Toolbelt", 45, "key.categories.gameplay");

    /** Toolbelt Radial Menu active flag (used by REHToolbeltHelper). */
    public static boolean TRMactive = false;

    private static boolean lastToolbeltKeyDown = false;

    /** How many client ticks the toolbelt key has been held for. */
    private static int pressTicks = 0;

    private static int lastCycleIndex = -1;
    private static boolean registered = false;

    /** Registers OA keybindings. Safe to call multiple times. */
    public static void registerKeyBindings() {
        if (registered) return;
        try {
            ClientRegistry.registerKeyBinding(keyX);
            registered = true;
        } catch (Throwable ignored) {
        }
    }

    
@SubscribeEvent
public void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase != TickEvent.Phase.END) return;

    final Minecraft mc = Minecraft.getMinecraft();
    final boolean down = keyX.getIsKeyPressed();

    // IMPORTANT: Do NOT gate TRMactive on inGameHasFocus.
    // REHToolbeltHelper intentionally toggles inGameHasFocus while the radial menu is open.
    // If we depend on it here, TRMactive will rapidly flip on/off and the menu will flicker.
    TRMactive = down && mc.thePlayer != null && mc.currentScreen == null;

    if (mc.thePlayer == null || mc.currentScreen != null) {
        // reset state when not in-game
        lastToolbeltKeyDown = down;
        pressTicks = 0;
        return;
    }

    // Track hold duration (ticks held down)
    if (down) {
        if (!lastToolbeltKeyDown) {
            pressTicks = 0;
        } else {
            pressTicks++;
        }
    }

    // On key release: if it was a short tap, cycle. If it was a hold, the radial menu handled selection.
    if (!down && lastToolbeltKeyDown) {
        final int held = pressTicks;
        pressTicks = 0;

        // tap threshold: <= 3 ticks
        if (held <= 3) {
            if (ModItems.toolbelt != null && ItemHelper.hasBauble(mc.thePlayer, ModItems.toolbelt)) {
                try {
                    InventoryToolbelt tbInv = ContainerHelper.getToolbeltInventory(BaublesApi.getBaubles(mc.thePlayer).getStackInSlot(3));
                    if (tbInv != null) {
                        int size = tbInv.getSizeInventory();
                        if (size > 0) {
                            int start = (lastCycleIndex + 1) % size;
                            int found = -1;
                            for (int off = 0; off < size; off++) {
                                int idx = (start + off) % size;
                                ItemStack st = tbInv.getStackInSlot(idx);
                                if (st != null) {
                                    found = idx;
                                    break;
                                }
                            }
                            if (found >= 0) {
                                lastCycleIndex = found;
                                // mode 2: server swaps current hand item with toolbelt slot item (server authoritative)
                                ModPackets.instance.sendToServer(new PacketToolbeltSwap(mc.thePlayer, 2, found, null));
                            }
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    lastToolbeltKeyDown = down;
}


}
