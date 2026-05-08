package com.voidsrift.riftflux.mixin.early.chromaticraft;

import Reika.ChromatiCraft.World.Nether.NetherStructureGenerator;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = NetherStructureGenerator.class, remap = false)
public abstract class MixinNetherStructureGenerator_HolesConfig {

    @Inject(method = "tryGenerateBypass", at = @At("HEAD"), cancellable = true)
    private void riftflux$skipDisabledNetherHoles(World world, int x, int z, Random rand, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.chromatiCraftNetherHolesEnabled) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    @ModifyConstant(method = "tryGenerateBypass", constant = @Constant(intValue = 100), require = 0)
    private int riftflux$useConfiguredNetherHoleAirScanTop(int original) {
        return Math.max(1, ModConfig.chromatiCraftNetherHolesY - 27);
    }

    @ModifyConstant(method = "tryGenerateBypass", constant = @Constant(intValue = 50), require = 0)
    private int riftflux$useConfiguredNetherHoleAirScanBottom(int original) {
        return Math.max(1, ModConfig.chromatiCraftNetherHolesY - 77);
    }

    @ModifyConstant(method = "generateBedrockBypass", constant = @Constant(intValue = 127), require = 0)
    private int riftflux$useConfiguredNetherHoleY(int original) {
        return ModConfig.chromatiCraftNetherHolesY;
    }

    @ModifyConstant(method = "generateBedrockBypass", constant = @Constant(intValue = 128), require = 0)
    private int riftflux$useConfiguredNetherHoleCapY(int original) {
        return ModConfig.chromatiCraftNetherHolesY + 1;
    }
}
