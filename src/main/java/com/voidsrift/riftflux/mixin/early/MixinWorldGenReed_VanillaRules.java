package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;
import java.util.Random;

@Mixin(WorldGenReed.class)
public abstract class MixinWorldGenReed_VanillaRules {
    @Unique
    private static final boolean riftflux$GENERATE_RIVER_FLOOR_CANE =
            ModConfig.allowSugarcaneInWater && ModConfig.sugarcaneGeneratesOnRiverFloors;
    @Unique
    private static final int riftflux$RIVER_FLOOR_RARITY = Math.max(1, ModConfig.sugarcaneRiverFloorRarity);
    @Unique
    private static final int riftflux$RIVER_FLOOR_MIN_HEIGHT_ABOVE_WATER =
            Math.max(0, ModConfig.sugarcaneRiverFloorMinHeightAboveWater);
    @Unique
    private static final int riftflux$RIVER_FLOOR_MAX_HEIGHT_ABOVE_WATER =
            Math.max(riftflux$RIVER_FLOOR_MIN_HEIGHT_ABOVE_WATER, ModConfig.sugarcaneRiverFloorMaxHeightAboveWater);

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void riftflux$generateWithVanillaSupportRules(World world, Random random, int x, int y, int z,
                                                          CallbackInfoReturnable<Boolean> cir) {
        boolean generateRiverFloorCaneThisCluster = this.riftflux$shouldGenerateRiverFloorCaneCluster(random);
        for (int attempt = 0; attempt < 20; ++attempt) {
            int reedX = x + random.nextInt(4) - random.nextInt(4);
            int reedY = y;
            int reedZ = z + random.nextInt(4) - random.nextInt(4);
            Block target = world.getBlock(reedX, reedY, reedZ);

            if (target == Blocks.air) {
                if (this.riftflux$hasAdjacentWater(world, reedX, reedY - 1, reedZ)
                        && this.riftflux$canVanillaGeneratedReedStayAt(world, reedX, reedY, reedZ)) {
                    this.riftflux$generateVanillaSurfaceCane(world, random, reedX, reedY, reedZ);
                } else if (generateRiverFloorCaneThisCluster) {
                    this.riftflux$tryGenerateRiverFloorCane(world, random, reedX, reedY, reedZ);
                }
            } else if (generateRiverFloorCaneThisCluster && this.riftflux$isWater(target)) {
                this.riftflux$tryGenerateRiverFloorCane(world, random, reedX, reedY, reedZ);
            }
        }

        cir.setReturnValue(true);
    }

    @Unique
    private void riftflux$generateVanillaSurfaceCane(World world, Random random, int x, int y, int z) {
        int height = this.riftflux$getVanillaGeneratedHeight(random);
        for (int offset = 0; offset < height; ++offset) {
            if (this.riftflux$canVanillaGeneratedReedStayAt(world, x, y + offset, z)) {
                world.setBlock(x, y + offset, z, Blocks.reeds, 0, 2);
            }
        }
    }

    @Unique
    private boolean riftflux$shouldGenerateRiverFloorCaneCluster(Random random) {
        if (!riftflux$GENERATE_RIVER_FLOOR_CANE) {
            return false;
        }
        if (riftflux$RIVER_FLOOR_RARITY > 1 && random.nextInt(riftflux$RIVER_FLOOR_RARITY) != 0) {
            return false;
        }
        return true;
    }

    @Unique
    private boolean riftflux$tryGenerateRiverFloorCane(World world, Random random, int x, int y, int z) {
        int topWaterY = this.riftflux$getTopWaterY(world, x, y, z);
        if (topWaterY == Integer.MIN_VALUE || !world.isAirBlock(x, topWaterY + 1, z)) {
            return false;
        }

        int bottomWaterY = topWaterY;
        while (bottomWaterY > 0 && this.riftflux$isWater(world.getBlock(x, bottomWaterY - 1, z))) {
            --bottomWaterY;
        }

        int supportY = bottomWaterY - 1;
        if (supportY < 0 || !this.riftflux$isVanillaOrCompatibleReedFloorBlock(world, x, supportY, z)) {
            return false;
        }

        int aboveSurfaceHeight = this.riftflux$getRiverFloorHeightAboveWater(random);
        int topReedY = topWaterY + aboveSurfaceHeight;
        for (int placeY = bottomWaterY; placeY <= topReedY; ++placeY) {
            Block target = world.getBlock(x, placeY, z);
            if (!world.isAirBlock(x, placeY, z) && !this.riftflux$isWater(target)) {
                return false;
            }
        }

        for (int placeY = bottomWaterY; placeY <= topReedY; ++placeY) {
            world.setBlock(x, placeY, z, Blocks.reeds, 0, 2);
        }
        return true;
    }

    @Unique
    private int riftflux$getRiverFloorHeightAboveWater(Random random) {
        return riftflux$RIVER_FLOOR_MIN_HEIGHT_ABOVE_WATER
                + random.nextInt(riftflux$RIVER_FLOOR_MAX_HEIGHT_ABOVE_WATER
                - riftflux$RIVER_FLOOR_MIN_HEIGHT_ABOVE_WATER + 1);
    }

    @Unique
    private int riftflux$getTopWaterY(World world, int x, int y, int z) {
        if (world.isAirBlock(x, y, z) && this.riftflux$isWater(world.getBlock(x, y - 1, z))) {
            return y - 1;
        }
        if (!this.riftflux$isWater(world.getBlock(x, y, z))) {
            return Integer.MIN_VALUE;
        }

        int topWaterY = y;
        while (topWaterY < 255 && this.riftflux$isWater(world.getBlock(x, topWaterY + 1, z))) {
            ++topWaterY;
        }
        return topWaterY;
    }

    @Unique
    private int riftflux$getVanillaGeneratedHeight(Random random) {
        return 2 + random.nextInt(random.nextInt(3) + 1);
    }

    @Unique
    private boolean riftflux$canVanillaGeneratedReedStayAt(World world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        if (below == Blocks.reeds) {
            return true;
        }
        return this.riftflux$hasAdjacentWater(world, x, y - 1, z)
                && below != null
                && below.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (IPlantable) Blocks.reeds);
    }

    @Unique
    private boolean riftflux$isVanillaOrCompatibleReedFloorBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block == null) {
            return false;
        }
        if (block == Blocks.grass || block == Blocks.dirt || block == Blocks.sand) {
            return true;
        }
        if (block instanceof BlockGrass || block instanceof BlockDirt || block instanceof BlockSand) {
            return true;
        }
        if (block.canSustainPlant(world, x, y, z, ForgeDirection.UP, (IPlantable) Blocks.reeds)) {
            return true;
        }

        Material material = block.getMaterial();
        String blockId = this.riftflux$getBlockId(block);
        if (material == Material.sand) {
            return blockId.contains("sand");
        }
        if (material == Material.grass) {
            return blockId.contains("grass");
        }
        if (material == Material.ground) {
            return blockId.contains("dirt") || blockId.contains("grass") || blockId.contains("podzol") || blockId.contains("soil");
        }
        return false;
    }

    @Unique
    private String riftflux$getBlockId(Block block) {
        String registryName = String.valueOf(Block.blockRegistry.getNameForObject(block));
        String unlocalizedName = String.valueOf(block.getUnlocalizedName());
        String className = block.getClass().getName();
        return (registryName + " " + unlocalizedName + " " + className).toLowerCase(Locale.ROOT);
    }

    @Unique
    private boolean riftflux$hasAdjacentWater(World world, int x, int y, int z) {
        return this.riftflux$isWater(world.getBlock(x - 1, y, z))
                || this.riftflux$isWater(world.getBlock(x + 1, y, z))
                || this.riftflux$isWater(world.getBlock(x, y, z - 1))
                || this.riftflux$isWater(world.getBlock(x, y, z + 1));
    }

    @Unique
    private boolean riftflux$isWater(Block block) {
        return block != null && block.getMaterial() == Material.water;
    }
}
