package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockFluidClassic.class, remap = false)
public abstract class MixinBlockFluidClassic_LiquidloggedQuanta extends BlockFluidBase {
    protected MixinBlockFluidClassic_LiquidloggedQuanta(Fluid fluid, Material material) {
        super(fluid, material);
    }

    @Inject(method = "getQuantaValue", at = @At("HEAD"), cancellable = true)
    private void riftflux$useFullQuantaForLiquidloggedHost(IBlockAccess world, int x, int y, int z,
                                                           CallbackInfoReturnable<Integer> cir) {
        Block fluid = RiftFluxFluidloggedLookup.getSupportedFluidBlock(world, x, y, z);
        if (RiftFluxFluidloggedLookup.isSameFluid((Block) (Object) this, fluid)) {
            cir.setReturnValue(this.quantaPerBlock);
        }
    }
}
