package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockDirt.class)
public abstract class MixinBlockDirt_PodzolTextures {

    @Unique
    @SideOnly(Side.CLIENT)
    private IIcon rf$podzolDirtIcon;

    @Inject(method = "registerBlockIcons(Lnet/minecraft/client/renderer/texture/IIconRegister;)V", at = @At("TAIL"))
    private void rf$registerPodzolDirtIcon(IIconRegister reg, CallbackInfo ci) {
        if (!ModConfig.enablePodzolDirtTexture) return;
        this.rf$podzolDirtIcon = reg.registerIcon(Constants.MODID + ":dirt");
    }

    @Inject(method = "getIcon(II)Lnet/minecraft/util/IIcon;", at = @At("HEAD"), cancellable = true)
    private void rf$getIcon(int side, int meta, CallbackInfoReturnable<IIcon> cir) {
        if (!ModConfig.enablePodzolDirtTexture) return;
        if (this.rf$podzolDirtIcon == null) return;
        if (meta == 2 && side == 0) {
            cir.setReturnValue(this.rf$podzolDirtIcon);
        }
    }

    @Inject(
            method = "getIcon(Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rf$getIcon(IBlockAccess world, int x, int y, int z, int side, CallbackInfoReturnable<IIcon> cir) {
        if (!ModConfig.enablePodzolDirtTexture) return;
        if (this.rf$podzolDirtIcon == null) return;
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != 2) return;

        if (rf$isPodzolAbove(world, x, y, z)) {
            cir.setReturnValue(this.rf$podzolDirtIcon);
            return;
        }

        if (side == 0) {
            cir.setReturnValue(this.rf$podzolDirtIcon);
        }
    }

    @Unique
    private static boolean rf$isPodzolAbove(IBlockAccess world, int x, int y, int z) {
        Block above = world.getBlock(x, y + 1, z);
        return above == Blocks.dirt && world.getBlockMetadata(x, y + 1, z) == 2;
    }
}
