package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.Block;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import com.voidsrift.riftflux.ModConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * MC 1.7.10
 * Forces full block drops for ALL explosions when ModConfig.enableFullExplosionDrops is true.
 * - Redirects Block.canDropFromExplosion(...) to return true.
 * - Redirects Block.dropBlockAsItemWithChance(...) to use chance=1.0F.
 */
@Mixin(Explosion.class)
public abstract class MixinTNT {

    // Ensure blocks are considered "droppable" by explosions.
    @Redirect(
            method = "doExplosionB",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;canDropFromExplosion(Lnet/minecraft/world/Explosion;)Z"
            )
    )
    private boolean riftflux$alwaysAllowExplosionDrops(Block block, Explosion explosion) {
        if (ModConfig.enableFullExplosionDrops) {
            return true;
        }
        return block.canDropFromExplosion(explosion);
    }

    // Force the actual drop chance to 100%.
    @Redirect(
            method = "doExplosionB",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;dropBlockAsItemWithChance(Lnet/minecraft/world/World;IIIIFI)V"
            )
    )
    private void riftflux$forceFullDropChance(Block block, World world, int x, int y, int z,
                                               int meta, float chance, int fortune) {
        if (ModConfig.enableFullExplosionDrops) {
            chance = 1.0F;
        }
        block.dropBlockAsItemWithChance(world, x, y, z, meta, chance, fortune);
    }
}