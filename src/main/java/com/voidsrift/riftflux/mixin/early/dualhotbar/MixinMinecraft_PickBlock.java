package com.voidsrift.riftflux.mixin.early.dualhotbar;

import com.voidsrift.riftflux.dualhotbar.DualHotbarPickBlockHelper;
import com.voidsrift.riftflux.mixin.accessor.PlayerControllerMPAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_PickBlock {
    @Inject(method = "func_147112_ai", at = @At("HEAD"), cancellable = true)
    private void riftflux$extendPickBlock(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        DualHotbarPickBlockHelper.clearPendingPickBlock();
        if (mc.objectMouseOver == null || mc.thePlayer == null || mc.theWorld == null || mc.playerController == null) {
            ci.cancel();
            return;
        }

        boolean creative = mc.thePlayer.capabilities.isCreativeMode;
        if (!ForgeHooks.onPickBlock(mc.objectMouseOver, mc.thePlayer, mc.theWorld)) {
            ci.cancel();
            return;
        }

        ItemStack result = DualHotbarPickBlockHelper.getPendingPickBlockResult();
        int sourceSlot = DualHotbarPickBlockHelper.getPendingPickBlockSourceSlot();
        int selectedSlot = mc.thePlayer.inventory.currentItem;

        if (result != null) {
            if (sourceSlot >= 0 && creative) {
                int sourceContainerSlot = DualHotbarPickBlockHelper.getInventoryContainerSlot(mc.thePlayer, sourceSlot);
                int selectedContainerSlot = DualHotbarPickBlockHelper.getInventoryContainerSlot(mc.thePlayer, selectedSlot);
                ItemStack sourceStack = mc.thePlayer.inventory.getStackInSlot(sourceSlot);
                ItemStack selectedStack = mc.thePlayer.inventory.getStackInSlot(selectedSlot);
                if (sourceStack != null && sourceContainerSlot >= 0 && selectedContainerSlot >= 0
                        && sourceContainerSlot != selectedContainerSlot) {
                    ItemStack sourceCopy = sourceStack.copy();
                    ItemStack selectedCopy = selectedStack == null ? null : selectedStack.copy();
                    mc.thePlayer.inventory.setInventorySlotContents(selectedSlot, sourceCopy.copy());
                    mc.thePlayer.inventory.setInventorySlotContents(sourceSlot, selectedCopy == null ? null : selectedCopy.copy());
                    mc.playerController.sendSlotPacket(sourceCopy, selectedContainerSlot);
                    mc.playerController.sendSlotPacket(selectedCopy, sourceContainerSlot);
                    mc.thePlayer.inventory.markDirty();
                }
            } else if (sourceSlot >= 0) {
                int sourceContainerSlot = DualHotbarPickBlockHelper.getInventoryContainerSlot(mc.thePlayer, sourceSlot);
                int selectedContainerSlot = DualHotbarPickBlockHelper.getInventoryContainerSlot(mc.thePlayer, selectedSlot);
                if (sourceContainerSlot >= 0 && selectedContainerSlot >= 0 && sourceContainerSlot != selectedContainerSlot) {
                    int windowId = mc.thePlayer.inventoryContainer.windowId;
                    mc.playerController.windowClick(windowId, sourceContainerSlot, 0, 0, mc.thePlayer);
                    mc.playerController.windowClick(windowId, selectedContainerSlot, 0, 0, mc.thePlayer);
                    mc.playerController.windowClick(windowId, sourceContainerSlot, 0, 0, mc.thePlayer);
                }
            } else if (creative) {
                int selectedContainerSlot = DualHotbarPickBlockHelper.getInventoryContainerSlot(mc.thePlayer, selectedSlot);
                ItemStack resultCopy = result.copy();
                mc.thePlayer.inventory.setInventorySlotContents(selectedSlot, resultCopy.copy());
                mc.thePlayer.inventory.markDirty();
                if (selectedContainerSlot >= 0) {
                    mc.playerController.sendSlotPacket(resultCopy, selectedContainerSlot);
                }
            }
        }

        if (mc.playerController instanceof PlayerControllerMPAccessor) {
            ((PlayerControllerMPAccessor)mc.playerController).riftflux$syncCurrentPlayItem();
        }
        DualHotbarPickBlockHelper.clearPendingPickBlock();
        ci.cancel();
    }
}
