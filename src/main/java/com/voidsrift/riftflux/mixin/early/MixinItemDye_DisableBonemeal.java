package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

/**
 * Bonemeal overhaul:
 * - Completely disabled on everything except grass.
 * - When used on grass, it has a configurable chance to trigger a vanilla-style
 *   patch growth (tall grass + biome/modded foliage).
 * - Spawns bonemeal particles in a wide area over the grass patch.
 * - Plants grown by bonemeal still obey vanilla "must be on grass/etc." rules.
 *
 * In 1.7.10, ItemDye damage value 15 = bonemeal.
 */
@Mixin(ItemDye.class)
public abstract class MixinItemDye_DisableBonemeal {

    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    private void riftflux$overrideBonemeal(ItemStack stack,
                                           EntityPlayer player,
                                           World world,
                                           int x, int y, int z,
                                           int side,
                                           float hitX, float hitY, float hitZ,
                                           CallbackInfoReturnable<Boolean> cir) {
        // If the override is disabled, let vanilla handle everything
        if (!ModConfig.disableBonemeal) {
            return;
        }

        // Only intercept bonemeal (damage 15)
        if (stack == null || stack.getItemDamage() != 15) {
            return;
        }

        // Respect vanilla editing permissions
        if (!player.canPlayerEdit(x, y, z, side, stack)) {
            cir.setReturnValue(false);
            return;
        }

        // Use the block that was actually clicked.
        Block block = world.getBlock(x, y, z);

        // Completely block bonemeal on anything that isn't a grass block
        if (block != Blocks.grass) {
            cir.setReturnValue(false);
            return;
        }

        if (!world.isRemote) {
            Random rand = world.rand;

            // Wide-area bonemeal particles, all on a single Y level and never inside blocks
            riftflux$spawnWideBonemealParticles(world, x, y, z, rand);

            // 🔹 Consume bonemeal on EVERY valid grass use (except in creative),
            // regardless of RNG or whether anything actually grows.
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }

            // Only attempt growth if chance > 0
            if (ModConfig.bonemealFlowerChance > 0.0F) {
                if (rand.nextFloat() < ModConfig.bonemealFlowerChance) {
                    // During bonemeal growth, use vanilla placement rules for plants.
                    RFPlantContext.bonemealPlacementActive = true;
                    try {
                        riftflux$growFromGrass(world, x, y, z, rand);
                    } finally {
                        RFPlantContext.bonemealPlacementActive = false;
                    }
                }
            }
        }

        // We've fully handled the interaction on both sides
        cir.setReturnValue(true);
    }

    /**
     * Spawn bonemeal particles in a wider area above the grass patch so the
     * player can see the approximate growth range.
     *
     * All particles are spawned on the same Y level (y + 1), and only in air,
     * so none end up underground / inside blocks.
     */
    private void riftflux$spawnWideBonemealParticles(World world, int x, int y, int z, Random rand) {
        int py = y + 1; // single height level for all particles

        for (int i = 0; i < 16; ++i) {
            int px = x + rand.nextInt(7) - 3; // -3 .. +3
            int pz = z + rand.nextInt(7) - 3;

            // Only spawn the effect if this position is air at that Y level
            if (!world.isAirBlock(px, py, pz)) {
                continue;
            }

            world.playAuxSFX(2005, px, py, pz, 0);
        }
    }

    /**
     * Vanilla-style patch growth from a grass block:
     * - Random walk around the clicked grass.
     * - At each valid air spot above *grass only*:
     *   * ~90%: use biome.getRandomWorldGenForGrass(...) for tall grass / modded grasslike foliage.
     *   * ~10%: use biome's flower string (func_150572_a) for flowers / modded decorations.
     *
     * Because RFPlantContext.bonemealPlacementActive = true while this runs,
     * BlockBush uses vanilla canPlaceBlockOn, so foliage only grows on blocks
     * vanilla would allow (e.g. grass/dirt), not stone or floating.
     */
    private void riftflux$growFromGrass(World world, int x, int y, int z, Random rand) {
        // Similar to vanilla's 128-attempt loop
        attempts:
        for (int i = 0; i < 128; ++i) {
            int gx = x;
            int gy = y + 1;
            int gz = z;

            // Random walk that gets "wider" as i increases
            for (int j = 0; j < i / 16; ++j) {
                gx += rand.nextInt(3) - 1;
                gy += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
                gz += rand.nextInt(3) - 1;

                // BELOW MUST BE GRASS, not just anything
                if (world.getBlock(gx, gy - 1, gz) != Blocks.grass ||
                        world.getBlock(gx, gy, gz).isNormalCube()) {
                    // Skip to the next outer attempt
                    continue attempts;
                }
            }

            if (!world.isAirBlock(gx, gy, gz)) {
                continue;
            }

            // We only grow on top of grass (extra safety)
            Block below = world.getBlock(gx, gy - 1, gz);
            if (below != Blocks.grass) {
                continue;
            }

            BiomeGenBase biome = world.getBiomeGenForCoords(gx, gz);
            if (biome == null) {
                continue;
            }

            // Roughly: 9/10 grass-like, 1/10 flowers – mirrors vanilla behaviour
            if (rand.nextInt(10) != 0) {
                // Grass / foliage path -> use biome's grass generator (supports modded foliage)
                WorldGenerator grassGen = biome.getRandomWorldGenForGrass(rand);
                if (grassGen != null) {
                    grassGen.generate(world, rand, gx, gy, gz);
                }
            } else {
                // Flower/decorative path -> use biome's flower string system (supports modded flowers)
                String key = biome.func_150572_a(rand, gx, gy, gz);
                BlockFlower flowerBlock = BlockFlower.func_149857_e(key);

                if (flowerBlock != null) {
                    int meta = BlockFlower.func_149856_f(key);

                    if (flowerBlock.canBlockStay(world, gx, gy, gz)) {
                        world.setBlock(gx, gy, gz, flowerBlock, meta, 3);
                    }
                }
            }
        }
    }
}
