package com.voidsrift.riftflux.mixin.early.backhand;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import makamys.satchels.gui.GuiSatchelsInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import xonin.backhand.client.utils.BackhandRenderHelper;

@Mixin(GuiSatchelsInventory.class)
public abstract class MixinGuiSatchelsInventory_BackhandSlot extends GuiContainer {

    protected MixinGuiSatchelsInventory_BackhandSlot(Container container) {
        super(container);
    }

    @Inject(
            method = "drawGuiContainerBackgroundLayer",
            at = @At("TAIL")
    )
    private void rf$drawBackhandSlot(float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        Slot backhandSlot = findBackhandSlot();
        if (backhandSlot == null) {
            return;
        }
        int drawX = this.guiLeft + backhandSlot.xDisplayPosition - 2;
        int drawY = this.guiTop + backhandSlot.yDisplayPosition - 2;
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
