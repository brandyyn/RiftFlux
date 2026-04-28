package com.voidsrift.riftflux.mixin.early.backhand;

import com.voidsrift.riftflux.avatar.glider.ItemGlider;
import com.voidsrift.riftflux.compat.BackhandCompat;
import com.voidsrift.riftflux.terramine.ItemIceRod;
import java.util.function.BooleanSupplier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xonin.backhand.api.core.BackhandUtils;

@Mixin(value = BackhandUtils.class, remap = false)
public abstract class MixinBackhandUtils_BlockUtilityOffhand {
    private static final ThreadLocal<Boolean> RIFTFLUX_BLOCK_UTILITY_OFFHAND = new ThreadLocal<Boolean>();

    @Inject(
            method = "useOffhandItem(Lnet/minecraft/entity/player/EntityPlayer;ZLjava/util/function/BooleanSupplier;)Z",
            at = @At("HEAD"),
            remap = false
    )
    private static void riftflux$captureUtilityOffhandBlock(
            EntityPlayer player,
            boolean setItemInUse,
            BooleanSupplier action,
            CallbackInfoReturnable<Boolean> cir) {
        RIFTFLUX_BLOCK_UTILITY_OFFHAND.set(Boolean.valueOf(setItemInUse && riftflux$shouldBlockUtilityOffhand(player)));
    }

    @Inject(
            method = "useOffhandItem(Lnet/minecraft/entity/player/EntityPlayer;ZLjava/util/function/BooleanSupplier;)Z",
            at = @At("RETURN"),
            remap = false
    )
    private static void riftflux$clearUtilityOffhandBlock(
            EntityPlayer player,
            boolean setItemInUse,
            BooleanSupplier action,
            CallbackInfoReturnable<Boolean> cir) {
        RIFTFLUX_BLOCK_UTILITY_OFFHAND.remove();
    }

    @Redirect(
            method = "useOffhandItem(Lnet/minecraft/entity/player/EntityPlayer;ZLjava/util/function/BooleanSupplier;)Z",
            at = @At(value = "INVOKE", target = "Ljava/util/function/BooleanSupplier;getAsBoolean()Z"),
            remap = false
    )
    private static boolean riftflux$skipBlockedUtilityOffhandAction(BooleanSupplier action) {
        Boolean blocked = RIFTFLUX_BLOCK_UTILITY_OFFHAND.get();
        if (blocked != null && blocked.booleanValue()) {
            return false;
        }
        BackhandCompat.beginOffhandAction();
        try {
            return action.getAsBoolean();
        } finally {
            BackhandCompat.endOffhandAction();
        }
    }

    private static boolean riftflux$shouldBlockUtilityOffhand(EntityPlayer player) {
        if (player == null || !BackhandCompat.isAvailable()) {
            return false;
        }
        ItemStack offhand = BackhandCompat.getOffhandItem(player);
        if (!riftflux$isIceRodOrGlider(offhand)) {
            return false;
        }
        ItemStack mainhand = BackhandCompat.getMainhandItem(player);
        return riftflux$isIceRodOrGlider(mainhand)
                || BackhandCompat.isMainhandUsingItem(player)
                || BackhandCompat.mainhandConsumesRightClick(mainhand);
    }

    private static boolean riftflux$isIceRodOrGlider(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof ItemGlider || item instanceof ItemIceRod;
    }
}
