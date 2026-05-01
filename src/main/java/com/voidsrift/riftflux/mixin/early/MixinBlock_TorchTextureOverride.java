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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlock_TorchTextureOverride {
    @Shadow
    protected IIcon blockIcon;

    @Inject(method = "registerBlockIcons(Lnet/minecraft/client/renderer/texture/IIconRegister;)V", at = @At("TAIL"))
    @SideOnly(Side.CLIENT)
    private void riftflux$registerBetterTorchTexture(IIconRegister register, CallbackInfo ci) {
        if (ModConfig.betterTorchTexture && (Object) this == Blocks.torch) {
            this.blockIcon = register.registerIcon(Constants.MODID + ":torch_on");
        }
    }
}
