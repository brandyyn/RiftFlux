package com.excsi.riftfixes.mixin.late;

import net.minecraft.block.Block;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import com.excsi.riftfixes.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * MC 1.7.10
 * Forces 100% block drop chance for ALL explosions when enabled in config.
 * Works for TNT, creepers, beds, crystals, etc. (anything using net.minecraft.world.Explosion#doExplosionB).
 */
@Mixin(Explosion.class)
public abstract class MixinTNT {

    /**
     * Inside Explosion#doExplosionB(boolean), vanilla calls:
     *   Block.dropBlockAsItemWithChance(World, x, y, z, meta, chance, fortune)
     * We redirect that call to clamp 'chance' to 1.0F when enabled.
     */
    @Redirect(
            method = "doExplosionB",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;dropBlockAsItemWithChance(Lnet/minecraft/world/World;IIIIFI)V"
            )
    )
    private void riftfixes$fullDropsForAllExplosions(Block block, World world, int x, int y, int z,
                                                     int meta, float chance, int fortune) {
        if (ModConfig.enableFullExplosionDrops) {
            chance = 1.0F;
        }
        block.dropBlockAsItemWithChance(world, x, y, z, meta, chance, fortune);
    }
}