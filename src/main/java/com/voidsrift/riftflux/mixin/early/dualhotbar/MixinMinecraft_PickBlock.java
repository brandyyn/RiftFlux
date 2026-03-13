package com.voidsrift.riftflux.mixin.early.dualhotbar;

import com.voidsrift.riftflux.dualhotbar.DualHotbarPickBlockHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_PickBlock {
    @Shadow
    public MovingObjectPosition objectMouseOver;

    @Shadow
    public EntityClientPlayerMP thePlayer;

    @Shadow
    public WorldClient theWorld;

    @Shadow
    public PlayerControllerMP playerController;

    @Inject(method = "func_147112_ai", at = @At("HEAD"), cancellable = true)
    private void riftflux$extendPickBlock(CallbackInfo ci) {
        if (this.objectMouseOver == null || this.thePlayer == null || this.theWorld == null || this.playerController == null) {
            ci.cancel();
            return;
        }

        boolean creative = this.thePlayer.capabilities.isCreativeMode;
        if (!ForgeHooks.onPickBlock(this.objectMouseOver, this.thePlayer, this.theWorld)) {
            ci.cancel();
            return;
        }

        if (creative) {
            int hotbarSlot = this.thePlayer.inventory.currentItem;
            int containerSlot = DualHotbarPickBlockHelper.getCreativeContainerSlot(this.thePlayer, hotbarSlot);
            ItemStack stack = this.thePlayer.inventory.getStackInSlot(hotbarSlot);
            this.playerController.sendSlotPacket(stack, containerSlot);
        }

        ci.cancel();
    }
}
