package com.voidsrift.riftflux.dualhotbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import com.voidsrift.riftflux.mixin.accessor.PlayerControllerMPAccessor;

public class InventoryChangeHandler {
    public static KeyBinding swapkey;
    public int mousePrev = -1;
    public int slot = -1;

    public int selectedItem;

    public static KeyBinding selectKey;
    public boolean swapKeyDown;
    public long[] keyTimes = new long[9];
    public int lastKey = -1;
    public int clickCount = 0;

    public boolean[] keyWasDown = new boolean[9];
    public boolean[] changeInv = new boolean[9];

    @SubscribeEvent
    public void postTickEvent(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return;
        }

        if (event.phase == TickEvent.Phase.START) {
            if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer) {
                swapKeyDown = false;
                return;
            }
            for (int j = 0; j < 9; ++j) {
                if (Keyboard.isKeyDown(mc.gameSettings.keyBindsHotbar[j].getKeyCode())) {
                    selectedItem = mc.thePlayer.inventory.currentItem;
                }
            }
            mousePrev = Mouse.getDWheel();

            if (swapkey != null && Keyboard.isKeyDown(swapkey.getKeyCode()) && Math.abs(mousePrev - Mouse.getDWheel()) > 0) {
                if (swapKeyDown == false) {
                    swapKeyDown = true;
                    System.out.println(Mouse.getX() + " " + Mouse.getY());
                    PlayerControllerMP controller = mc.playerController;
                    EntityClientPlayerMP player = mc.thePlayer;

                    int window = player.inventoryContainer.windowId;

                    controller.updateController();

                    System.out.println(mousePrev);
                    if (mousePrev < 0) {
                        if (DualHotbarConfig.twoLayerRendering) {
                            for (int i = 9; i < 18; i++) {
                                controller.windowClick(window, i, 0, 0, player);
                                if (DualHotbarConfig.numHotbars > 1) {
                                    controller.windowClick(window, i + 27, 0, 0, player);
                                }
                                if (DualHotbarConfig.numHotbars > 2) {
                                    controller.windowClick(window, i + 18, 0, 0, player);
                                }
                                if (DualHotbarConfig.numHotbars > 3) {
                                    controller.windowClick(window, i + 9, 0, 0, player);
                                }
                                controller.windowClick(window, i, 0, 0, player);
                            }
                        } else if (DualHotbarConfig.numHotbars == 4) {
                            for (int i = 9; i < 27; i++) {
                                controller.windowClick(window, i, 0, 0, player);
                                controller.windowClick(window, i + 18, 0, 0, player);
                                controller.windowClick(window, i, 0, 0, player);
                            }
                        }
                    } else {
                        if (DualHotbarConfig.twoLayerRendering) {
                            System.out.println("test");
                            for (int i = 9; i < 18; i++) {
                                controller.windowClick(window, i, 0, 0, player);
                                if (DualHotbarConfig.numHotbars > 3) {
                                    controller.windowClick(window, i + 27, 0, 0, player);
                                }
                                if (DualHotbarConfig.numHotbars > 2) {
                                    controller.windowClick(window, i + 18, 0, 0, player);
                                }
                                if (DualHotbarConfig.numHotbars > 1) {
                                    controller.windowClick(window, i + 9, 0, 0, player);
                                }
                                controller.windowClick(window, i, 0, 0, player);
                            }
                        }
                        if (DualHotbarConfig.numHotbars == 4) {
                            for (int i = 9; i < 27; i++) {
                                controller.windowClick(window, i, 0, 0, player);
                                controller.windowClick(window, i + 18, 0, 0, player);
                                controller.windowClick(window, i, 0, 0, player);
                            }
                        }
                    }
                    slot = player.inventory.currentItem;
                }
            } else {
                swapKeyDown = false;
            }
        }

        if (event.phase == TickEvent.Phase.END) {
            if (slot != -1) {
                mc.thePlayer.inventory.currentItem = slot;
                slot = -1;
            }

            if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer) {
                return;
            }

            if (!DualHotbarConfig.enable || !DualHotbarState.installedOnServer) {
                return;
            }

            long time = System.currentTimeMillis();
            for (int j = 0; j < 9; ++j) {
                if (Keyboard.isKeyDown(mc.gameSettings.keyBindsHotbar[j].getKeyCode())) {
                    if (Keyboard.isKeyDown(selectKey.getKeyCode())) {
                        mc.thePlayer.inventory.currentItem = j + 9;
                        if (mc.playerController != null) {
                            mc.playerController.updateController();
                            syncCurrentPlayItem(mc.playerController);
                        }
                        continue;
                    }

                    if (keyWasDown[j]) {
                        continue;
                    }

                    for (int i = 0; i < DualHotbarConfig.numHotbars; i++) {
                        if (selectedItem == j + i * 9) {
                            mc.thePlayer.inventory.currentItem =
                                    (j + 9 * (i + 1)) % (DualHotbarConfig.numHotbars * 9);
                            if (mc.playerController != null) {
                                mc.playerController.updateController();
                                syncCurrentPlayItem(mc.playerController);
                            }
                        }
                    }

                    if (lastKey == j && DualHotbarConfig.doubleTap
                            && time - keyTimes[j] < DualHotbarConfig.doubleTapTime) {
                        clickCount++;
                    } else {
                        clickCount = 0;
                    }

                    if (clickCount > 0) {
                        // handled by the hotbar cycling logic above
                    }

                    lastKey = j;
                    keyTimes[j] = time;
                    keyWasDown[j] = true;
                } else {
                    keyWasDown[j] = false;
                }
            }
        }
    }

    private static void syncCurrentPlayItem(PlayerControllerMP controller) {
        if (controller instanceof PlayerControllerMPAccessor) {
            ((PlayerControllerMPAccessor) controller).riftflux$syncCurrentPlayItem();
        }
    }

}
