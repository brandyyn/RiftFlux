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
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.minecraftforge.client.event.MouseEvent;

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
    /** Minimum ticks to consider it a hold (radial menu). */
    private static final int TOOLBELT_HOLD_TICKS = 4;
    /** Maximum ticks to consider it a quick tap. */
    private static final int TOOLBELT_TAP_MAX_TICKS = TOOLBELT_HOLD_TICKS - 1;

    /** Set when the radial menu performed an action to suppress tap-swap on release. */
    private static boolean toolbeltRadialAction = false;
    /** Set when the mouse was pressed while holding the toolbelt key (radial interaction). */
    private static boolean toolbeltMouseDownWhileHeld = false;
    /** Tracks a key press that could become a quick tap. */
    private static boolean toolbeltTapQueued = false;

    private static int lastCycleIndex = -1;
    private static boolean registered = false;

    public static void registerKeyBindings() {
        if (registered) return;
        try {
            ClientRegistry.registerKeyBinding(keyX);
            registered = true;
        } catch (Throwable ignored) {
        }
    }

    public static void markToolbeltRadialAction() {
        toolbeltRadialAction = true;
    }

    public static boolean isToolbeltKeyDown() {
        return isKeyDown(keyX);
    }

    private static boolean isKeyDown(KeyBinding binding) {
        int code = binding.getKeyCode();
        if (code < 0) {
            return Mouse.isButtonDown(100 + code);
        }
        return Keyboard.isKeyDown(code);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        final Minecraft mc = Minecraft.getMinecraft();
        final boolean down = isKeyDown(keyX);
        final boolean pressed = keyX.isPressed();

        if (mc.thePlayer == null || mc.currentScreen != null) {
            // reset state when not in-game
            TRMactive = false;
            lastToolbeltKeyDown = down;
            pressTicks = 0;
            toolbeltRadialAction = false;
            toolbeltMouseDownWhileHeld = false;
            toolbeltTapQueued = false;
            return;
        }

        if (pressed && !lastToolbeltKeyDown) {
            toolbeltTapQueued = true;
        }

        // Track hold duration (ticks held down)
        if (down) {
            if (!lastToolbeltKeyDown) {
                pressTicks = 0;
            } else {
                pressTicks++;
            }
            if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
                toolbeltMouseDownWhileHeld = true;
            }
        } else if (!lastToolbeltKeyDown && toolbeltTapQueued) {
            // Catch ultra-fast taps that start/end between ticks.
            if (!toolbeltRadialAction && !toolbeltMouseDownWhileHeld) {
                doQuickTapSwap(mc);
            }
            toolbeltTapQueued = false;
        }

        // IMPORTANT: Do NOT gate TRMactive on inGameHasFocus.
        // REHToolbeltHelper intentionally toggles inGameHasFocus while the radial menu is open.
        // If we depend on it here, TRMactive will rapidly flip on/off and the menu will flicker.
        TRMactive = down && pressTicks >= TOOLBELT_HOLD_TICKS;
        if (TRMactive) {
            toolbeltTapQueued = false;
        }

        // On key release: if it was a short tap, cycle. If it was a hold, the radial menu handled selection.
        if (!down && lastToolbeltKeyDown) {
            final int held = pressTicks;
            pressTicks = 0;

            // tap threshold: <= TOOLBELT_TAP_MAX_TICKS
            if (toolbeltRadialAction || toolbeltMouseDownWhileHeld) {
                toolbeltRadialAction = false;
                toolbeltMouseDownWhileHeld = false;
            } else if (toolbeltTapQueued && held <= TOOLBELT_TAP_MAX_TICKS) {
                doQuickTapSwap(mc);
            }
            toolbeltTapQueued = false;
        }

        lastToolbeltKeyDown = down;
    }

    @SubscribeEvent
    public void onMouseInput(MouseEvent event) {
        if (event.buttonstate && isToolbeltKeyDown()) {
            toolbeltMouseDownWhileHeld = true;
        }
    }

    private static void doQuickTapSwap(Minecraft mc) {
        if (ModItems.toolbelt != null && ItemHelper.hasBauble(mc.thePlayer, ModItems.toolbelt)) {
            try {
                int toolbeltSlot = ItemHelper.findBaubleSlot(mc.thePlayer, ModItems.toolbelt);
                if (toolbeltSlot < 0) {
                    return;
                }
                ItemStack toolbeltStack = BaublesApi.getBaubles(mc.thePlayer).getStackInSlot(toolbeltSlot);
                if (toolbeltStack == null) {
                    return;
                }
                InventoryToolbelt tbInv = ContainerHelper.getToolbeltInventory(toolbeltStack);
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
