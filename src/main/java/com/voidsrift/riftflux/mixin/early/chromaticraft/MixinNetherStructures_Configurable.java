package com.voidsrift.riftflux.mixin.early.chromaticraft;

import Reika.ChromatiCraft.World.Nether.NetherStructures;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = NetherStructures.class, remap = false)
public abstract class MixinNetherStructures_Configurable {

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void riftflux$skipDisabledNetherStructure(World world, int x, int z, Random rand, CallbackInfo ci) {
        NetherStructures structure = (NetherStructures) (Object) this;
        if (!ModConfig.isChromatiCraftNetherStructureEnabled(structure.name())) {
            ci.cancel();
        }
    }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 128), require = 0)
    private int riftflux$useConfiguredNetherStructureY(int original) {
        NetherStructures structure = (NetherStructures) (Object) this;
        return ModConfig.getChromatiCraftNetherStructureYLevel(structure.name());
    }
}
