package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockMycelium;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockMycelium.class)
public abstract class MixinBlockMycelium_BottomDirtTexture {

    @Unique
    @SideOnly(Side.CLIENT)
    private IIcon rf$myceliumDirtIcon;

    @Inject(method = "registerBlockIcons(Lnet/minecraft/client/renderer/texture/IIconRegister;)V", at = @At("TAIL"))
    private void rf$registerMyceliumDirtIcon(IIconRegister reg, CallbackInfo ci) {
        if (!ModConfig.enablePodzolDirtTexture) return;
        this.rf$myceliumDirtIcon = reg.registerIcon(Constants.MODID + ":dirt");
    }

    @Inject(method = "getIcon(II)Lnet/minecraft/util/IIcon;", at = @At("HEAD"), cancellable = true)
    private void rf$getIcon(int side, int meta, CallbackInfoReturnable<IIcon> cir) {
        if (!ModConfig.enablePodzolDirtTexture) return;
        if (this.rf$myceliumDirtIcon == null) return;
        if (side == 0) {
            cir.setReturnValue(this.rf$myceliumDirtIcon);
        }
    }

    @Inject(
            method = "getIcon(Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rf$getIcon(IBlockAccess world, int x, int y, int z, int side, CallbackInfoReturnable<IIcon> cir) {
        if (!ModConfig.enablePodzolDirtTexture) return;
        if (this.rf$myceliumDirtIcon == null) return;
        if (side == 0) {
            cir.setReturnValue(this.rf$myceliumDirtIcon);
        }
    }
}
