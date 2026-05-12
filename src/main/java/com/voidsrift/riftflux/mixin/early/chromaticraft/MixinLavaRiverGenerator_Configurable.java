package com.voidsrift.riftflux.mixin.early.chromaticraft;

import Reika.ChromatiCraft.World.Nether.LavaRiverGenerator;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LavaRiverGenerator.class, remap = false)
public abstract class MixinLavaRiverGenerator_Configurable {

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void riftflux$skipDisabledNetherLavaRivers(World world, int chunkX, int chunkZ, CallbackInfo ci) {
        if (!ModConfig.chromatiCraftNetherLavaRiversEnabled) {
            ci.cancel();
        }
    }

    @ModifyConstant(method = "generate", constant = @Constant(doubleValue = 127.0D), require = 0)
    private double riftflux$useConfiguredNetherLavaRiverMinY(double original) {
        return ModConfig.chromatiCraftNetherLavaRiverMinY;
    }

    @ModifyConstant(method = "generate", constant = @Constant(doubleValue = 240.0D), require = 0)
    private double riftflux$useConfiguredNetherLavaRiverMaxY(double original) {
        return ModConfig.chromatiCraftNetherLavaRiverMaxY;
    }

    @ModifyArg(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z"), index = 4)
    private int riftflux$stripProtectedShieldMetadataForBreakableNetherRivers(int meta) {
        return ModConfig.chromatiCraftNetherStructureShieldBreakableLikeObsidian && meta >= 8 ? meta % 8 : meta;
    }
}
