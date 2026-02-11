package com.voidsrift.riftflux.mixin.early.backhand;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;

@Mixin(ContainerPlayer.class)
public abstract class MixinContainerPlayer_BackhandSlot {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void rf$moveBackhandSlotUp(InventoryPlayer inventory, boolean localWorld, EntityPlayer player, CallbackInfo ci) {
        ContainerPlayer self = (ContainerPlayer) (Object) this;
        List slots = self.inventorySlots;
        if (slots == null) {
            return;
        }
        for (Object obj : slots) {
            if (obj instanceof Slot && "xonin.backhand.api.core.BackhandSlot".equals(obj.getClass().getName())) {
                Slot slot = (Slot) obj;
                slot.yDisplayPosition = slot.yDisplayPosition - 1;
            }
        }
    }
}
