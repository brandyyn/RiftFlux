package com.voidsrift.riftflux.mixin.early.backhand;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import xonin.backhand.client.utils.BackhandRenderHelper;

@Mixin(GuiInventory.class)
public abstract class MixinGuiInventory_BackhandSlot extends GuiContainer {

    protected MixinGuiInventory_BackhandSlot(Container container) {
        super(container);
    }

    @Redirect(
            method = "backhand$drawOffhandSlot",
            at = @At(value = "INVOKE", target = "Lxonin/backhand/client/utils/BackhandRenderHelper;drawItemStackSlot(II)V"),
            remap = false
    )
    private void rf$drawBackhandSlotAligned(int x, int y) {
        int drawX = x;
        int drawY = y;
        Slot backhandSlot = findBackhandSlot();
        if (backhandSlot != null) {
            drawX = this.guiLeft + backhandSlot.xDisplayPosition - 2;
            drawY = this.guiTop + backhandSlot.yDisplayPosition - 2;
        }
        BackhandRenderHelper.drawItemStackSlot(drawX, drawY);
    }

    private Slot findBackhandSlot() {
        if (this.inventorySlots == null) {
            return null;
        }
        List slots = this.inventorySlots.inventorySlots;
        if (slots == null) {
            return null;
        }
        for (Object obj : slots) {
            if (obj instanceof Slot && "xonin.backhand.api.core.BackhandSlot".equals(obj.getClass().getName())) {
                return (Slot) obj;
            }
        }
        return null;
    }
}
