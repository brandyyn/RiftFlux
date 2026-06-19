package com.voidsrift.riftflux.mixin.late.geostrata;

import Reika.GeoStrata.Blocks.BlockDecoGen;
import com.voidsrift.riftflux.geostrata.client.DecoGenItemRenderer;
import com.voidsrift.riftflux.geostrata.client.GeoStrataDecoGenParticle;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockDecoGen.class, remap = false)
public abstract class MixinBlockDecoGen_ParticleIcons {
    private static final int riftflux$ORIENTATION_MASK = 14;

    @Inject(method = "registerBlockIcons", at = @At("TAIL"))
    private void riftflux$registerDecoGenParticleIcons(IIconRegister iconRegister, CallbackInfo ci) {
        DecoGenItemRenderer.registerBlockParticleIcons(iconRegister);
    }

    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        if (!riftflux$isIcicle(meta) || DecoGenItemRenderer.getItemTexture(meta) == null) {
            return false;
        }

        int count = 4;
        for (int ix = 0; ix < count; ix++) {
            for (int iy = 0; iy < count; iy++) {
                for (int iz = 0; iz < count; iz++) {
                    double px = x + (ix + 0.5D) / count;
                    double py = y + (iy + 0.5D) / count;
                    double pz = z + (iz + 0.5D) / count;
                    effectRenderer.addEffect(new GeoStrataDecoGenParticle(
                            world,
                            px,
                            py,
                            pz,
                            px - x - 0.5D,
                            py - y - 0.5D,
                            pz - z - 0.5D,
                            meta
                    ));
                }
            }
        }
        return true;
    }

    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        if (target == null) {
            return false;
        }

        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (block != (Object) this || !riftflux$isIcicle(meta)
                || DecoGenItemRenderer.getItemTexture(meta) == null) {
            return false;
        }

        double px = x + 0.5D;
        double py = y + 0.5D;
        double pz = z + 0.5D;
        if (target.sideHit == 0) {
            py = y - 0.02D;
        } else if (target.sideHit == 1) {
            py = y + 1.02D;
        } else if (target.sideHit == 2) {
            pz = z - 0.02D;
        } else if (target.sideHit == 3) {
            pz = z + 1.02D;
        } else if (target.sideHit == 4) {
            px = x - 0.02D;
        } else if (target.sideHit == 5) {
            px = x + 1.02D;
        }

        GeoStrataDecoGenParticle particle = new GeoStrataDecoGenParticle(
                world,
                px,
                py,
                pz,
                px - x - 0.5D,
                py - y - 0.5D,
                pz - z - 0.5D,
                meta
        );
        effectRenderer.addEffect(particle.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
        return true;
    }

    private static boolean riftflux$isIcicle(int meta) {
        return (meta & ~riftflux$ORIENTATION_MASK) == 1;
    }
}
