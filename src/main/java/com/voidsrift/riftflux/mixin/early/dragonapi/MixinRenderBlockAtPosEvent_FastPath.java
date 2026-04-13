package com.voidsrift.riftflux.mixin.early.dragonapi;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "Reika.DragonAPI.Instantiable.Event.Client.RenderBlockAtPosEvent", remap = false)
public abstract class MixinRenderBlockAtPosEvent_FastPath {

    @Inject(method = "fire", at = @At("HEAD"), cancellable = true, require = 0)
    private static void riftflux$skipDragonAPIBlockRenderBusForNormalBlocks(RenderBlocks rb, Block b, int x, int y, int z, WorldRenderer wr, int pass, CallbackInfoReturnable<Boolean> cir) {
        if (!(b instanceof Reika.DragonAPI.Interfaces.Block.Submergeable)) {
            cir.setReturnValue(rb.renderBlockByRenderType(b, x, y, z));
        }
    }
}
