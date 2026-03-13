package com.voidsrift.riftflux.mixin.late.openblocks;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.compat.EtFuturumElytraCompat;
import com.voidsrift.riftflux.compat.OpenBlocksGliderCompat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "openblocks.common.item.ItemHangGlider", remap = false)
public abstract class MixinItemHangGlider_NoElytra {

    @Inject(
            method = "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void riftflux$clearElytraBeforeOpenBlocksGlider(ItemStack stack,
                                                            World world,
                                                            EntityPlayer player,
                                                            CallbackInfoReturnable<ItemStack> cir) {
        if (!ModConfig.blockEtFuturumElytraWhileAvatarGliding || player == null) {
            return;
        }

        if (OpenBlocksGliderCompat.isGliderActive(player)) {
            return;
        }

        if (!EtFuturumElytraCompat.isElytraFlying(player)) {
            return;
        }
        EtFuturumElytraCompat.clearElytraFlight(player);
    }
}
