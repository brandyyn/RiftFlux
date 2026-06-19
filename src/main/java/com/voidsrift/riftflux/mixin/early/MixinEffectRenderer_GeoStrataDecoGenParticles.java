package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.geostrata.client.DecoGenItemRenderer;
import com.voidsrift.riftflux.geostrata.client.GeoStrataDecoGenParticle;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EffectRenderer.class, priority = 2000)
public abstract class MixinEffectRenderer_GeoStrataDecoGenParticles {
    private static final int riftflux$ORIENTATION_MASK = 14;

    @Shadow
    protected World worldObj;

    @Inject(method = "addBlockDestroyEffects", at = @At("HEAD"), cancellable = true)
    private void riftflux$addDecoGenDestroyParticles(int x, int y, int z, Block block, int meta, CallbackInfo ci) {
        if (!riftflux$isDecoGen(block) || !riftflux$isIcicle(meta)
                || DecoGenItemRenderer.getItemTexture(meta) == null || this.worldObj == null) {
            return;
        }

        int count = 4;
        for (int ix = 0; ix < count; ix++) {
            for (int iy = 0; iy < count; iy++) {
                for (int iz = 0; iz < count; iz++) {
                    double px = x + (ix + 0.5D) / count;
                    double py = y + (iy + 0.5D) / count;
                    double pz = z + (iz + 0.5D) / count;
                    GeoStrataDecoGenParticle particle = new GeoStrataDecoGenParticle(
                            this.worldObj,
                            px,
                            py,
                            pz,
                            px - x - 0.5D,
                            py - y - 0.5D,
                            pz - z - 0.5D,
                            meta
                    );
                    ((EffectRenderer) (Object) this).addEffect(particle);
                }
            }
        }
        ci.cancel();
    }

    @Inject(method = "addBlockHitEffects(IIII)V", at = @At("HEAD"), cancellable = true)
    private void riftflux$addDecoGenHitParticle(int x, int y, int z, int side, CallbackInfo ci) {
        if (this.worldObj == null) {
            return;
        }

        Block block = this.worldObj.getBlock(x, y, z);
        int meta = this.worldObj.getBlockMetadata(x, y, z);
        if (!riftflux$isDecoGen(block) || !riftflux$isIcicle(meta)
                || DecoGenItemRenderer.getItemTexture(meta) == null) {
            return;
        }

        double px = x + 0.5D;
        double py = y + 0.5D;
        double pz = z + 0.5D;
        double offset = 0.62D;
        if (side == 0) {
            py = y - 0.02D;
        } else if (side == 1) {
            py = y + 1.02D;
        } else if (side == 2) {
            pz = z - 0.02D;
        } else if (side == 3) {
            pz = z + 1.02D;
        } else if (side == 4) {
            px = x - 0.02D;
        } else if (side == 5) {
            px = x + 1.02D;
        }

        GeoStrataDecoGenParticle particle = new GeoStrataDecoGenParticle(
                this.worldObj,
                px,
                py,
                pz,
                (px - x - 0.5D) * offset,
                (py - y - 0.5D) * offset,
                (pz - z - 0.5D) * offset,
                meta
        );
        ((EffectRenderer) (Object) this).addEffect(particle.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
        ci.cancel();
    }

    private static boolean riftflux$isDecoGen(Block block) {
        return block != null && "Reika.GeoStrata.Blocks.BlockDecoGen".equals(block.getClass().getName());
    }

    private static boolean riftflux$isIcicle(int meta) {
        return (meta & ~riftflux$ORIENTATION_MASK) == 1;
    }
}
