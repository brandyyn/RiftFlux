package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Block.class, priority = 100)
public abstract class MixinBlock_NetherHideBlocks_PhotoMode {

    private static final String RIFTFLUX_NETHERLICIOUS_BRITTLE_BEDROCK_CLASS =
            "DelirusCrux.Netherlicious.Common.Blocks.BrittleBedrock";

    @Inject(method = "canRenderInPass", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private void riftflux$hideNetherNetherrackAndBedrockInPhotoMode(int pass, CallbackInfoReturnable<Boolean> cir) {
        if (!this.riftflux$isNetherPhotoModeActive()) {
            return;
        }

        Block block = (Block) (Object) this;
        if (this.riftflux$isHiddenBedrockBlock(block)) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    private boolean riftflux$isNetherPhotoModeActive() {
        if (!ModConfig.isometricPhotoModeHideNetherNetherrackAndBedrock
                || !IsometricPhotoModeController.instance().isActive()) {
            return false;
        }

        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc == null ? null : mc.theWorld;
        return world != null
                && world.provider != null
                && (world.provider.isHellWorld || world.provider.dimensionId == -1);
    }

    private boolean riftflux$isHiddenBedrockBlock(Block block) {
        return block == Blocks.bedrock || RIFTFLUX_NETHERLICIOUS_BRITTLE_BEDROCK_CLASS.equals(block.getClass().getName());
    }
}
