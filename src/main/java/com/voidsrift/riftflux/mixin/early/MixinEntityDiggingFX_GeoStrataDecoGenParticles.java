package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.geostrata.client.DecoGenItemRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityDiggingFX.class)
public abstract class MixinEntityDiggingFX_GeoStrataDecoGenParticles {
    @Inject(
            method = "<init>(Lnet/minecraft/world/World;DDDDDDLnet/minecraft/block/Block;II)V",
            at = @At("TAIL")
    )
    private void riftflux$useDecoGenParticleSprite(World world, double x, double y, double z, double motionX,
                                                   double motionY, double motionZ, Block block, int meta, int side,
                                                   CallbackInfo ci) {
        riftflux$useDecoGenParticleSprite(block, meta);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/World;DDDDDDLnet/minecraft/block/Block;I)V",
            at = @At("TAIL")
    )
    private void riftflux$useDecoGenParticleSprite(World world, double x, double y, double z, double motionX,
                                                   double motionY, double motionZ, Block block, int meta,
                                                   CallbackInfo ci) {
        riftflux$useDecoGenParticleSprite(block, meta);
    }

    private void riftflux$useDecoGenParticleSprite(Block block, int meta) {
        IIcon icon = DecoGenItemRenderer.getParticleIcon(block, meta);
        if (icon != null) {
            ((EntityFX) (Object) this).setParticleIcon(icon);
        }
    }
}
