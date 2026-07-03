package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock_TorchTextureOverride {
    @Unique
    private static IIcon riftflux$betterTorchIcon;

    @Inject(method = "registerBlockIcons(Lnet/minecraft/client/renderer/texture/IIconRegister;)V", at = @At("TAIL"))
    @SideOnly(Side.CLIENT)
    private void riftflux$registerBetterTorchTexture(IIconRegister register, CallbackInfo ci) {
        if (ModConfig.betterTorchTexture && (Object) this == Blocks.torch) {
            riftflux$betterTorchIcon = register.registerIcon(Constants.MODID + ":torch_on");
        }
    }

    @Inject(method = "getIcon(II)Lnet/minecraft/util/IIcon;", at = @At("HEAD"), cancellable = true)
    @SideOnly(Side.CLIENT)
    private void riftflux$getBetterTorchTexture(int side, int meta, CallbackInfoReturnable<IIcon> cir) {
        if (ModConfig.betterTorchTexture && riftflux$betterTorchIcon != null && (Object) this == Blocks.torch) {
            cir.setReturnValue(riftflux$betterTorchIcon);
        }
    }
}
