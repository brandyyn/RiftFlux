package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.placeditem.BlockPlacedItem;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderer.class)
public abstract class MixinEffectRenderer_PlacedItemDestroyParticles {

    @Shadow
    protected World worldObj;

    @Inject(method = "addBlockDestroyEffects", at = @At("HEAD"), cancellable = true)
    private void rf$limitPlacedItemDestroyParticles(int x, int y, int z, Block block, int meta, CallbackInfo ci) {
        if (!(block instanceof BlockPlacedItem)) {
            return;
        }
        if (this.worldObj != null) {
            block.addDestroyEffects(this.worldObj, x, y, z, meta, (EffectRenderer) (Object) this);
        }
        ci.cancel();
    }
}
