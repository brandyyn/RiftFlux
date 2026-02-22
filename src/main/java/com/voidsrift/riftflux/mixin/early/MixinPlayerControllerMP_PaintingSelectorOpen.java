package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.riftflux;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP_PaintingSelectorOpen {

    @Inject(method = "onPlayerRightClick", at = @At("HEAD"), cancellable = true)
    private void riftflux$openSelectorBeforePacket(EntityPlayer player,
                                                   World world,
                                                   ItemStack stack,
                                                   int x, int y, int z,
                                                   int side,
                                                   Vec3 hitVec,
                                                   CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.enablePaintingSelection) {
            return;
        }
        if (player == null || !player.isSneaking()) {
            return;
        }
        if (stack == null || stack.getItem() != Items.painting) {
            return;
        }

        if (riftflux.proxy != null) {
            riftflux.proxy.openPaintingSelectorScreen();
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "sendUseItem", at = @At("HEAD"), cancellable = true)
    private void riftflux$openSelectorInAir(EntityPlayer player,
                                            World world,
                                            ItemStack stack,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.enablePaintingSelection) {
            return;
        }
        if (player == null || !player.isSneaking()) {
            return;
        }
        if (stack == null || stack.getItem() != Items.painting) {
            return;
        }

        if (riftflux.proxy != null) {
            riftflux.proxy.openPaintingSelectorScreen();
        }
        cir.setReturnValue(true);
    }
}
