package com.voidsrift.riftflux.client.photomode;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class IsometricPhotoModeHandler {

    private static final String CATEGORY = "RiftFlux";
    private static final KeyBinding TOGGLE_PHOTO_MODE = new KeyBinding("Isometric Photo Mode", Keyboard.KEY_P, CATEGORY);
    private static final KeyBinding TOGGLE_PLAYER_CONTROL = new KeyBinding("Photo Mode Player Control", Keyboard.KEY_C, CATEGORY);
    private static final KeyBinding ROTATE_UP = new KeyBinding("Photo Mode Rotate Up", Keyboard.KEY_UP, CATEGORY);
    private static final KeyBinding ROTATE_DOWN = new KeyBinding("Photo Mode Rotate Down", Keyboard.KEY_DOWN, CATEGORY);
    private static final int HOLD_ROTATE_DELAY_TICKS = 3;
    private static boolean registered;

    private final Minecraft mc = Minecraft.getMinecraft();
    private int horizontalRotateHoldDirection;
    private int horizontalRotateHoldTicks;
    private int verticalRotateDirection;
    private int verticalRotateHoldTicks;
    private boolean jumpCenterHeld;

    public static void bootstrap() {
        if (registered || !ModConfig.enableIsometricPhotoMode) {
            return;
        }

        ClientRegistry.registerKeyBinding(TOGGLE_PHOTO_MODE);
        ClientRegistry.registerKeyBinding(TOGGLE_PLAYER_CONTROL);
        ClientRegistry.registerKeyBinding(ROTATE_UP);
        ClientRegistry.registerKeyBinding(ROTATE_DOWN);

        IsometricPhotoModeHandler handler = new IsometricPhotoModeHandler();
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);
        registered = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        boolean capturingInput = controller.isCapturingInput() && this.mc.currentScreen == null;
        controller.setRotationInputActive(capturingInput && this.isAnyRotationInputDown());
        if (capturingInput) {
            this.tickCenterOnPlayer(controller);
        } else {
            this.jumpCenterHeld = false;
        }
        controller.tick();

        if (!controller.isCapturingInput() || this.mc.currentScreen != null) {
            controller.setRotationInputActive(false);
            this.resetHorizontalRotationHold();
            this.resetVerticalRotationHold();
            return;
        }

        this.tickVerticalRotation(controller);
        int horizontalDirection = this.getHorizontalRotationDirection();
        if (horizontalDirection == 0) {
            this.flushHorizontalRotationHold(controller);
            return;
        }

        if (this.horizontalRotateHoldDirection != horizontalDirection) {
            this.flushHorizontalRotationHold(controller);
            this.horizontalRotateHoldDirection = horizontalDirection;
            this.horizontalRotateHoldTicks = 0;
        }

        this.horizontalRotateHoldTicks++;
        if (this.horizontalRotateHoldTicks > HOLD_ROTATE_DELAY_TICKS) {
            controller.rotateHorizontalContinuous(horizontalDirection, ModConfig.isometricPhotoModeHoldRotateDegreesPerTick);
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (this.mc.currentScreen != null) {
            return;
        }

        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();

        while (TOGGLE_PHOTO_MODE.isPressed()) {
            controller.toggle();
        }

        if (!controller.isActive()) {
            this.resetHorizontalRotationHold();
            this.resetVerticalRotationHold();
            return;
        }

        while (TOGGLE_PLAYER_CONTROL.isPressed()) {
            controller.togglePlayerControl();
        }
        while (this.mc.gameSettings.keyBindJump.isPressed()) {
            if (controller.isCapturingInput()) {
                controller.centerOnPlayer();
            }
        }

        this.drainCameraControlConflictingKeyPresses(controller);
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        if (!controller.isActive() || this.mc.currentScreen != null) {
            return;
        }

        if (event.button == 2 && event.buttonstate) {
            controller.centerOnPlayer();
            event.setCanceled(true);
            return;
        }

        if (event.dwheel != 0) {
            controller.adjustZoom(event.dwheel);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        IsometricPhotoModeController.instance().reset();
    }

    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        IsometricPhotoModeController.instance().onDisconnect();
    }

    private void flushHorizontalRotationHold(IsometricPhotoModeController controller) {
        if (this.horizontalRotateHoldDirection != 0 && this.horizontalRotateHoldTicks > 0) {
            if (this.horizontalRotateHoldTicks <= HOLD_ROTATE_DELAY_TICKS) {
                controller.rotateHorizontal(this.horizontalRotateHoldDirection);
            } else {
                controller.snapHorizontalToNearestDiagonal(this.horizontalRotateHoldDirection);
            }
        }

        this.resetHorizontalRotationHold();
    }

    private void resetHorizontalRotationHold() {
        this.horizontalRotateHoldDirection = 0;
        this.horizontalRotateHoldTicks = 0;
    }

    private void resetVerticalRotationHold() {
        this.verticalRotateDirection = 0;
        this.verticalRotateHoldTicks = 0;
    }

    private void tickCenterOnPlayer(IsometricPhotoModeController controller) {
        boolean jumpDown = this.isKeyBindingDown(this.mc.gameSettings.keyBindJump);
        if (jumpDown && !this.jumpCenterHeld) {
            controller.centerOnPlayer();
        }
        this.jumpCenterHeld = jumpDown;
    }

    private int getHorizontalRotationDirection() {
        boolean rotateLeftDown = Keyboard.isKeyDown(Keyboard.KEY_Q)
                || Keyboard.isKeyDown(Keyboard.KEY_LEFT)
                || this.isSneakMovementComboDown(this.mc.gameSettings.keyBindLeft);
        boolean rotateRightDown = Keyboard.isKeyDown(Keyboard.KEY_E)
                || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)
                || this.isSneakMovementComboDown(this.mc.gameSettings.keyBindRight);

        if (rotateLeftDown == rotateRightDown) {
            return 0;
        }

        return rotateLeftDown ? 1 : -1;
    }

    private void tickVerticalRotation(IsometricPhotoModeController controller) {
        int direction = this.getVerticalRotationDirection();
        if (direction == 0) {
            this.resetVerticalRotationHold();
            return;
        }

        if (direction != this.verticalRotateDirection) {
            controller.adjustVerticalRotation(direction);
            this.verticalRotateDirection = direction;
            this.verticalRotateHoldTicks = 1;
            return;
        }

        this.verticalRotateHoldTicks++;
        if (this.verticalRotateHoldTicks > HOLD_ROTATE_DELAY_TICKS) {
            controller.adjustVerticalRotation(direction);
        }
    }

    private int getVerticalRotationDirection() {
        boolean rotateUpDown = this.isKeyBindingDown(ROTATE_DOWN)
                || this.isSneakMovementComboDown(this.mc.gameSettings.keyBindBack);
        boolean rotateDownDown = this.isKeyBindingDown(ROTATE_UP)
                || this.isSneakMovementComboDown(this.mc.gameSettings.keyBindForward);

        if (rotateUpDown == rotateDownDown) {
            return 0;
        }

        return rotateUpDown ? -1 : 1;
    }

    private boolean isAnyRotationInputDown() {
        return this.getHorizontalRotationDirection() != 0 || this.getVerticalRotationDirection() != 0;
    }

    private boolean isSneakMovementComboDown(KeyBinding movementKeyBinding) {
        GameSettings settings = this.mc.gameSettings;
        return this.isKeyBindingDown(settings.keyBindSneak) && this.isKeyBindingDown(movementKeyBinding);
    }

    private void drainCameraControlConflictingKeyPresses(IsometricPhotoModeController controller) {
        if (!controller.isCapturingInput()) {
            return;
        }

        while (this.mc.gameSettings.keyBindInventory.isPressed()) {
            // Q/E are photo-camera controls here, not normal inventory/drop actions.
        }
        while (this.mc.gameSettings.keyBindDrop.isPressed()) {
            // Keep default Q from dropping the held item while it is used to rotate.
        }
        while (this.mc.gameSettings.keyBindJump.isPressed()) {
            // Space recenters the photo camera instead of jumping the player.
        }
    }

    private boolean isKeyBindingDown(KeyBinding keyBinding) {
        int keyCode = keyBinding.getKeyCode();
        if (keyCode < 0) {
            return Mouse.isButtonDown(keyCode + 100);
        }

        return keyCode > 0 && Keyboard.isKeyDown(keyCode);
    }
}
