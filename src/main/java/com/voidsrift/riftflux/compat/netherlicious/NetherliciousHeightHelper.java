package com.voidsrift.riftflux.compat.netherlicious;

import DelirusCrux.Netherlicious.Utility.Configuration.NetherliciousConfiguration;
import DelirusCrux.Netherlicious.Utility.Configuration.WorldgenConfiguration;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public final class NetherliciousHeightHelper {

    public static final int DEFAULT_BIG_NETHER_TOP_Y = 255;
    private static final int MIN_BIG_NETHER_TOP_Y = 128;
    private static final int MAX_WORLD_Y = 255;
    private static final String NETHERLICIOUS_MOD_BLOCKS_CLASS =
            "DelirusCrux.Netherlicious.Common.BlockItemUtility.ModBlocks";
    private static final String NETHERLICIOUS_BRITTLE_BEDROCK_FIELD = "BrittleBedrock";
    private static boolean triedResolveBrittleBedrock;
    private static Block resolvedBrittleBedrock;

    private NetherliciousHeightHelper() {
    }

    public static boolean hasCustomBigNetherTopY() {
        return getConfiguredBigNetherTopY() < DEFAULT_BIG_NETHER_TOP_Y;
    }

    public static int getConfiguredBigNetherTopY() {
        return clamp(ModConfig.netherliciousBigNetherTopY, MIN_BIG_NETHER_TOP_Y, DEFAULT_BIG_NETHER_TOP_Y);
    }

    public static int getConfiguredBigNetherActualHeight() {
        return clamp(getConfiguredBigNetherTopY() + 1, MIN_BIG_NETHER_TOP_Y + 1, MAX_WORLD_Y + 1);
    }

    public static void applyConfiguredCeilingToBlockArray(long worldSeed, int chunkX, int chunkZ, Block[] blocks) {
        if (!hasCustomBigNetherTopY() || blocks == null) {
            return;
        }

        int topY = getConfiguredBigNetherTopY();
        Block ceilingBlock = getCeilingBlock();

        for (int localZ = 0; localZ < 16; ++localZ) {
            for (int localX = 0; localX < 16; ++localX) {
                int worldX = (chunkX << 4) + localX;
                int worldZ = (chunkZ << 4) + localZ;
                int ceilingStartY = getCeilingStartY(worldSeed, worldX, worldZ, topY);

                for (int y = topY + 1; y <= MAX_WORLD_Y; ++y) {
                    blocks[toBlockArrayIndex(localX, localZ, y)] = Blocks.air;
                }

                if (!WorldgenConfiguration.BedrockCeiling) {
                    continue;
                }

                for (int y = ceilingStartY; y <= topY; ++y) {
                    blocks[toBlockArrayIndex(localX, localZ, y)] = ceilingBlock;
                }
            }
        }
    }

    public static void applyConfiguredCeilingToChunk(long worldSeed, Chunk chunk) {
        if (!hasCustomBigNetherTopY() || chunk == null) {
            return;
        }

        int topY = getConfiguredBigNetherTopY();
        Block ceilingBlock = getCeilingBlock();
        ExtendedBlockStorage[] storageArrays = chunk.getBlockStorageArray();
        int topSectionIndex = topY >> 4;
        int topLocalY = topY & 15;
        boolean changed = false;

        for (int sectionIndex = topSectionIndex + 1; sectionIndex < storageArrays.length; ++sectionIndex) {
            if (storageArrays[sectionIndex] != null) {
                storageArrays[sectionIndex] = null;
                changed = true;
            }
        }

        ExtendedBlockStorage topSection = storageArrays[topSectionIndex];
        if (topSection != null && topLocalY < 15) {
            for (int localY = topLocalY + 1; localY < 16; ++localY) {
                for (int localZ = 0; localZ < 16; ++localZ) {
                    for (int localX = 0; localX < 16; ++localX) {
                        if (topSection.getBlockByExtId(localX, localY, localZ) != Blocks.air) {
                            topSection.func_150818_a(localX, localY, localZ, Blocks.air);
                            changed = true;
                        }
                    }
                }
            }
            if (topSection.isEmpty()) {
                storageArrays[topSectionIndex] = null;
            }
        }

        for (int localZ = 0; localZ < 16; ++localZ) {
            for (int localX = 0; localX < 16; ++localX) {
                int worldX = (chunk.xPosition << 4) + localX;
                int worldZ = (chunk.zPosition << 4) + localZ;
                int ceilingStartY = getCeilingStartY(worldSeed, worldX, worldZ, topY);

                if (!WorldgenConfiguration.BedrockCeiling) {
                    continue;
                }

                for (int y = ceilingStartY; y <= topY; ++y) {
                    ExtendedBlockStorage storage = getOrCreateStorage(storageArrays, chunk, y >> 4);
                    int localY = y & 15;
                    if (storage.getBlockByExtId(localX, localY, localZ) != ceilingBlock) {
                        storage.func_150818_a(localX, localY, localZ, ceilingBlock);
                        changed = true;
                    }
                }
            }
        }

        if (changed) {
            chunk.generateSkylightMap();
        }
    }

    private static int getCeilingStartY(long worldSeed, int worldX, int worldZ, int topY) {
        if (!WorldgenConfiguration.BedrockCeiling) {
            return topY + 1;
        }
        if (WorldgenConfiguration.BedrockSmooth) {
            return topY;
        }
        return Math.max(1, topY - getColumnCeilingDepth(worldSeed, worldX, worldZ));
    }

    private static Block getCeilingBlock() {
        if (NetherliciousConfiguration.BrittleBedrock && WorldgenConfiguration.BrittleBedrockCeiling) {
            Block brittleBedrock = resolveBrittleBedrockBlock();
            if (brittleBedrock != null) {
                return brittleBedrock;
            }
        }
        return Blocks.bedrock;
    }

    private static Block resolveBrittleBedrockBlock() {
        if (triedResolveBrittleBedrock) {
            return resolvedBrittleBedrock;
        }

        triedResolveBrittleBedrock = true;
        try {
            ClassLoader loader = NetherliciousHeightHelper.class.getClassLoader();
            Class<?> modBlocks = Class.forName(NETHERLICIOUS_MOD_BLOCKS_CLASS, false, loader);
            Object value = modBlocks.getField(NETHERLICIOUS_BRITTLE_BEDROCK_FIELD).get(null);
            if (value instanceof Block) {
                resolvedBrittleBedrock = (Block) value;
            }
        } catch (Throwable ignored) {
            resolvedBrittleBedrock = null;
        }

        return resolvedBrittleBedrock;
    }

    private static long makeColumnSeed(long worldSeed, int worldX, int worldZ) {
        long seed = worldSeed;
        seed ^= (long) worldX * 341873128712L;
        seed ^= (long) worldZ * 132897987541L;
        seed ^= 0x9E3779B97F4A7C15L;
        return seed;
    }

    private static int getColumnCeilingDepth(long worldSeed, int worldX, int worldZ) {
        long mixed = makeColumnSeed(worldSeed, worldX, worldZ);
        mixed ^= mixed >>> 33;
        mixed *= 0xff51afd7ed558ccdl;
        mixed ^= mixed >>> 33;
        mixed *= 0xc4ceb9fe1a85ec53l;
        mixed ^= mixed >>> 33;
        return (int) Math.floorMod(mixed, 5L);
    }

    private static ExtendedBlockStorage getOrCreateStorage(ExtendedBlockStorage[] storageArrays, Chunk chunk, int sectionIndex) {
        ExtendedBlockStorage storage = storageArrays[sectionIndex];
        if (storage == null) {
            storage = new ExtendedBlockStorage(sectionIndex << 4, !chunk.worldObj.provider.hasNoSky);
            storageArrays[sectionIndex] = storage;
        }
        return storage;
    }

    private static int toBlockArrayIndex(int localX, int localZ, int y) {
        return (localZ * 16 + localX) * 256 + y;
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
