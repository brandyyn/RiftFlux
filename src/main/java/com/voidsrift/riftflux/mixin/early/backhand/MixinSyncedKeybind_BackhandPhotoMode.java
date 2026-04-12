package com.voidsrift.riftflux.mixin.early.backhand;

import com.voidsrift.riftflux.client.photomode.PhotoModeExternalKeySuppressions;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.gtnewhorizon.gtnhlib.keybind.SyncedKeybind", remap = false)
public abstract class MixinSyncedKeybind_BackhandPhotoMode {

    @Unique
    private static Object riftflux$backhandSwapKey;

    @Unique
    private static Method riftflux$getKeybindingMethod;

    @Unique
    private static boolean riftflux$lookupAttempted;

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    private void riftflux$suppressBackhandSwapInPhotoCamera(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (riftflux$shouldSuppress(this)) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    @Inject(method = "onClientTick", at = @At("HEAD"), require = 0)
    private static void riftflux$suppressBackhandSwapBeforeClientTick(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        riftflux$releaseBackhandSwapKey();
    }

    @Unique
    private static boolean riftflux$shouldSuppress(Object keybind) {
        if (!PhotoModeExternalKeySuppressions.shouldSuppressBackhandSwapKey()
                || keybind == null
                || keybind != riftflux$getBackhandSwapKey()) {
            return false;
        }

        KeyBinding keyBinding = riftflux$getKeybinding(keybind);
        if (keyBinding != null) {
            KeyBinding.setKeyBindState(keyBinding.getKeyCode(), false);
        }
        return true;
    }

    @Unique
    private static void riftflux$releaseBackhandSwapKey() {
        if (!PhotoModeExternalKeySuppressions.shouldSuppressBackhandSwapKey()) {
            return;
        }

        KeyBinding keyBinding = riftflux$getKeybinding(riftflux$getBackhandSwapKey());
        if (keyBinding != null) {
            KeyBinding.setKeyBindState(keyBinding.getKeyCode(), false);
        }
    }

    @Unique
    private static Object riftflux$getBackhandSwapKey() {
        if (riftflux$lookupAttempted) {
            return riftflux$backhandSwapKey;
        }

        riftflux$lookupAttempted = true;
        try {
            Class<?> proxyClass = Class.forName("xonin.backhand.CommonProxy", false, MixinSyncedKeybind_BackhandPhotoMode.class.getClassLoader());
            Field swapKey = proxyClass.getField("SWAP_KEY");
            riftflux$backhandSwapKey = swapKey.get(null);
        } catch (Throwable ignored) {
            riftflux$backhandSwapKey = null;
        }
        return riftflux$backhandSwapKey;
    }

    @Unique
    private static KeyBinding riftflux$getKeybinding(Object keybind) {
        try {
            if (riftflux$getKeybindingMethod == null) {
                riftflux$getKeybindingMethod = keybind.getClass().getMethod("getKeybinding");
            }
            Object value = riftflux$getKeybindingMethod.invoke(keybind);
            return value instanceof KeyBinding ? (KeyBinding) value : null;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
