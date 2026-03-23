package com.voidsrift.riftflux.fence;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

public final class FenceRenderSupport {
    private static final String ETF_WOOD_FENCE = "ganymedes01.etfuturum.blocks.BlockWoodFence";
    private static final String ETF_MODERN_WOOD_FENCE = "ganymedes01.etfuturum.blocks.BlockModernWoodFence";
    private static final String ETF_BASE_LOG = "ganymedes01.etfuturum.blocks.BaseLog";
    private static final String ETF_NETHER_STEM = "ganymedes01.etfuturum.blocks.BlockNetherStem";
    private static final String ETF_BAMBOO_BLOCK = "ganymedes01.etfuturum.blocks.BlockBambooBlock";
    private static final String NETHERLICIOUS_CRIMSON_FENCE = "DelirusCrux.Netherlicious.Common.Blocks.WallsFence.CrimsonFence";
    private static final String NETHERLICIOUS_WARPED_FENCE = "DelirusCrux.Netherlicious.Common.Blocks.WallsFence.WarpedFence";
    private static final String NETHERLICIOUS_FOXFIRE_FENCE = "DelirusCrux.Netherlicious.Common.Blocks.WallsFence.FoxfireFence";
    private static final String NETHERLICIOUS_FUNGI_LOGS_1 = "DelirusCrux.Netherlicious.Common.Blocks.FungiLogs1";
    private static final String NETHERLICIOUS_FUNGI_LOGS_2 = "DelirusCrux.Netherlicious.Common.Blocks.FungiLogs2";
    private static final String FORESTRY_ARB_FENCE = "forestry.arboriculture.blocks.BlockArbFence";

    private static final Map<Block, FenceStyle> STYLE_CACHE = new IdentityHashMap<Block, FenceStyle>();
    private static final Map<String, Block> BLOCK_CACHE = new HashMap<String, Block>();

    private static boolean forestryReflectionResolved;
    private static Method forestryGetWoodTile;
    private static Method forestryGetWoodType;
    private static Method forestryGetBarkIcon;
    private static Method forestryGetPlankIcon;
    private static Object[] forestryWoodTypes;

    private FenceRenderSupport() {
    }

    public static boolean isSupportedFence(Block block) {
        return getStyle(block) != null;
    }

    public static FenceRenderTextures getWorldTextures(BlockFence fence, IBlockAccess blockAccess, int x, int y, int z) {
        FenceStyle style = getStyle(fence);
        if (style == null) {
            return null;
        }

        int metadata = blockAccess == null ? 0 : blockAccess.getBlockMetadata(x, y, z);
        IIcon railIcon = getWorldRailIcon(style, fence, blockAccess, x, y, z, metadata);
        IIcon postIcon = getWorldPostIcon(style, blockAccess, x, y, z, metadata, railIcon);
        if (railIcon == null || postIcon == null) {
            return null;
        }

        return new FenceRenderTextures(railIcon, postIcon);
    }

    public static FenceRenderTextures getItemTextures(BlockFence fence, int metadata) {
        FenceStyle style = getStyle(fence);
        if (style == null) {
            return null;
        }

        IIcon railIcon = getItemRailIcon(style, fence, metadata);
        IIcon postIcon = getItemPostIcon(style, metadata, railIcon);
        if (railIcon == null || postIcon == null) {
            return null;
        }

        return new FenceRenderTextures(railIcon, postIcon);
    }

    private static IIcon getItemRailIcon(FenceStyle style, BlockFence fence, int metadata) {
        if (style == FenceStyle.FORESTRY) {
            return getForestryPlankIcon(getForestryWoodType(metadata), fence.getIcon(2, metadata));
        }
        return fence.getIcon(2, metadata);
    }

    private static IIcon getWorldRailIcon(FenceStyle style, BlockFence fence, IBlockAccess blockAccess, int x, int y, int z, int metadata) {
        IIcon railIcon = blockAccess == null ? null : fence.getIcon(blockAccess, x, y, z, 2);
        if (railIcon == null) {
            railIcon = fence.getIcon(2, metadata);
        }
        if (railIcon == null && style == FenceStyle.FORESTRY) {
            railIcon = getForestryPlankIcon(blockAccess, x, y, z, null);
        }
        return railIcon;
    }

    private static IIcon getWorldPostIcon(FenceStyle style, IBlockAccess blockAccess, int x, int y, int z, int metadata, IIcon fallback) {
        switch (style) {
            case VANILLA_OAK:
                return getBlockIcon(Blocks.log, 2, 0, fallback);
            case ETF_SPRUCE:
                return getBlockIcon(Blocks.log, 2, 1, fallback);
            case ETF_BIRCH:
                return getBlockIcon(Blocks.log, 2, 2, fallback);
            case ETF_JUNGLE:
                return getBlockIcon(Blocks.log, 2, 3, fallback);
            case ETF_ACACIA:
                return getBlockIcon(Blocks.log2, 2, 0, fallback);
            case ETF_DARK_OAK:
                return getBlockIcon(Blocks.log2, 2, 1, fallback);
            case ETF_MODERN:
                return getEtFuturumModernPostIcon(metadata, fallback);
            case NETHERLICIOUS_CRIMSON:
                return getBlockIcon(findFirstBlockByClassName(NETHERLICIOUS_FUNGI_LOGS_1), 2, 0, fallback);
            case NETHERLICIOUS_WARPED:
                return getBlockIcon(findFirstBlockByClassName(NETHERLICIOUS_FUNGI_LOGS_1), 2, 2, fallback);
            case NETHERLICIOUS_FOXFIRE:
                return getBlockIcon(findFirstBlockByClassName(NETHERLICIOUS_FUNGI_LOGS_2), 2, 0, fallback);
            case FORESTRY:
                return getForestryBarkIcon(blockAccess, x, y, z, fallback);
            default:
                return fallback;
        }
    }

    private static IIcon getItemPostIcon(FenceStyle style, int metadata, IIcon fallback) {
        switch (style) {
            case VANILLA_OAK:
                return getBlockIcon(Blocks.log, 2, 0, fallback);
            case ETF_SPRUCE:
                return getBlockIcon(Blocks.log, 2, 1, fallback);
            case ETF_BIRCH:
                return getBlockIcon(Blocks.log, 2, 2, fallback);
            case ETF_JUNGLE:
                return getBlockIcon(Blocks.log, 2, 3, fallback);
            case ETF_ACACIA:
                return getBlockIcon(Blocks.log2, 2, 0, fallback);
            case ETF_DARK_OAK:
                return getBlockIcon(Blocks.log2, 2, 1, fallback);
            case ETF_MODERN:
                return getEtFuturumModernPostIcon(metadata, fallback);
            case NETHERLICIOUS_CRIMSON:
                return getBlockIcon(findFirstBlockByClassName(NETHERLICIOUS_FUNGI_LOGS_1), 2, 0, fallback);
            case NETHERLICIOUS_WARPED:
                return getBlockIcon(findFirstBlockByClassName(NETHERLICIOUS_FUNGI_LOGS_1), 2, 2, fallback);
            case NETHERLICIOUS_FOXFIRE:
                return getBlockIcon(findFirstBlockByClassName(NETHERLICIOUS_FUNGI_LOGS_2), 2, 0, fallback);
            case FORESTRY:
                return getForestryBarkIcon(getForestryWoodType(metadata), fallback);
            default:
                return fallback;
        }
    }

    private static IIcon getEtFuturumModernPostIcon(int metadata, IIcon fallback) {
        switch (metadata) {
            case 0:
                return getBlockIcon(findBlockByClassAndName(ETF_NETHER_STEM, "crimson_stem"), 2, 0, fallback);
            case 1:
                return getBlockIcon(findBlockByClassAndName(ETF_NETHER_STEM, "warped_stem"), 2, 0, fallback);
            case 2:
                return getBlockIcon(findBlockByClassAndName(ETF_BASE_LOG, "mangrove_log"), 2, 0, fallback);
            case 3:
                return getBlockIcon(findBlockByClassAndName(ETF_BASE_LOG, "cherry_log"), 2, 0, fallback);
            case 4:
                return getBlockIcon(findBlockByClassAndName(ETF_BAMBOO_BLOCK, "bamboo_block"), 2, 0, fallback);
            default:
                return fallback;
        }
    }

    private static FenceStyle getStyle(Block block) {
        if (block == null || !(block instanceof BlockFence)) {
            return null;
        }

        if (STYLE_CACHE.containsKey(block)) {
            return STYLE_CACHE.get(block);
        }

        FenceStyle resolved = resolveStyle(block);
        STYLE_CACHE.put(block, resolved);
        return resolved;
    }

    private static FenceStyle resolveStyle(Block block) {
        if (block == Blocks.fence) {
            return FenceStyle.VANILLA_OAK;
        }

        String className = block.getClass().getName();
        if (ETF_MODERN_WOOD_FENCE.equals(className)) {
            return FenceStyle.ETF_MODERN;
        }
        if (ETF_WOOD_FENCE.equals(className)) {
            String name = normalizeName(block.getUnlocalizedName());
            if ("spruce_fence".equals(name)) {
                return FenceStyle.ETF_SPRUCE;
            }
            if ("birch_fence".equals(name)) {
                return FenceStyle.ETF_BIRCH;
            }
            if ("jungle_fence".equals(name)) {
                return FenceStyle.ETF_JUNGLE;
            }
            if ("acacia_fence".equals(name)) {
                return FenceStyle.ETF_ACACIA;
            }
            if ("dark_oak_fence".equals(name)) {
                return FenceStyle.ETF_DARK_OAK;
            }
            return null;
        }
        if (NETHERLICIOUS_CRIMSON_FENCE.equals(className)) {
            return FenceStyle.NETHERLICIOUS_CRIMSON;
        }
        if (NETHERLICIOUS_WARPED_FENCE.equals(className)) {
            return FenceStyle.NETHERLICIOUS_WARPED;
        }
        if (NETHERLICIOUS_FOXFIRE_FENCE.equals(className)) {
            return FenceStyle.NETHERLICIOUS_FOXFIRE;
        }
        if (FORESTRY_ARB_FENCE.equals(className)) {
            return FenceStyle.FORESTRY;
        }

        return null;
    }

    private static Block findFirstBlockByClassName(String className) {
        String cacheKey = className + "#*";
        if (BLOCK_CACHE.containsKey(cacheKey)) {
            return BLOCK_CACHE.get(cacheKey);
        }

        for (Object obj : Block.blockRegistry) {
            if (!(obj instanceof Block)) {
                continue;
            }
            Block block = (Block) obj;
            if (className.equals(block.getClass().getName())) {
                BLOCK_CACHE.put(cacheKey, block);
                return block;
            }
        }

        BLOCK_CACHE.put(cacheKey, null);
        return null;
    }

    private static Block findBlockByClassAndName(String className, String normalizedName) {
        String cacheKey = className + "#" + normalizedName;
        if (BLOCK_CACHE.containsKey(cacheKey)) {
            return BLOCK_CACHE.get(cacheKey);
        }

        for (Object obj : Block.blockRegistry) {
            if (!(obj instanceof Block)) {
                continue;
            }
            Block block = (Block) obj;
            if (!className.equals(block.getClass().getName())) {
                continue;
            }
            if (!normalizedName.equals(normalizeName(block.getUnlocalizedName()))) {
                continue;
            }
            BLOCK_CACHE.put(cacheKey, block);
            return block;
        }

        BLOCK_CACHE.put(cacheKey, null);
        return null;
    }

    private static IIcon getBlockIcon(Block block, int side, int metadata, IIcon fallback) {
        if (block == null) {
            return fallback;
        }
        IIcon icon = block.getIcon(side, metadata);
        return icon != null ? icon : fallback;
    }

    private static String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        String lower = name.toLowerCase(Locale.ROOT);
        int dot = lower.lastIndexOf('.');
        if (dot >= 0 && dot < lower.length() - 1) {
            lower = lower.substring(dot + 1);
        }
        int colon = lower.lastIndexOf(':');
        if (colon >= 0 && colon < lower.length() - 1) {
            lower = lower.substring(colon + 1);
        }
        return lower;
    }

    private static IIcon getForestryBarkIcon(IBlockAccess blockAccess, int x, int y, int z, IIcon fallback) {
        return getForestryBarkIcon(getForestryWoodType(blockAccess, x, y, z), fallback);
    }

    private static IIcon getForestryPlankIcon(IBlockAccess blockAccess, int x, int y, int z, IIcon fallback) {
        return getForestryPlankIcon(getForestryWoodType(blockAccess, x, y, z), fallback);
    }

    private static IIcon getForestryBarkIcon(Object woodType, IIcon fallback) {
        if (woodType == null) {
            return fallback;
        }

        ensureForestryReflection();
        if (forestryGetBarkIcon == null) {
            return fallback;
        }

        try {
            Object icon = forestryGetBarkIcon.invoke(null, woodType);
            return icon instanceof IIcon ? (IIcon) icon : fallback;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static IIcon getForestryPlankIcon(Object woodType, IIcon fallback) {
        if (woodType == null) {
            return fallback;
        }

        ensureForestryReflection();
        if (forestryGetPlankIcon == null) {
            return fallback;
        }

        try {
            Object icon = forestryGetPlankIcon.invoke(null, woodType);
            return icon instanceof IIcon ? (IIcon) icon : fallback;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static Object getForestryWoodType(IBlockAccess blockAccess, int x, int y, int z) {
        ensureForestryReflection();
        if (forestryGetWoodTile == null || forestryGetWoodType == null) {
            return null;
        }

        try {
            Object tile = forestryGetWoodTile.invoke(null, blockAccess, Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z));
            if (tile == null) {
                return null;
            }
            return forestryGetWoodType.invoke(tile);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Object getForestryWoodType(int metadata) {
        ensureForestryReflection();
        if (forestryWoodTypes == null || forestryWoodTypes.length == 0) {
            return null;
        }

        int clamped = metadata;
        if (clamped < 0 || clamped >= forestryWoodTypes.length) {
            clamped = 0;
        }
        return forestryWoodTypes[clamped];
    }

    private static synchronized void ensureForestryReflection() {
        if (forestryReflectionResolved) {
            return;
        }
        forestryReflectionResolved = true;

        try {
            Class<?> tileWoodClass = Class.forName("forestry.arboriculture.tiles.TileWood");
            Class<?> enumWoodTypeClass = Class.forName("forestry.api.arboriculture.EnumWoodType");
            Class<?> iconProviderClass = Class.forName("forestry.arboriculture.render.IconProviderWood");
            forestryGetWoodTile = tileWoodClass.getMethod("getWoodTile", IBlockAccess.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            forestryGetWoodType = tileWoodClass.getMethod("getWoodType");
            forestryGetBarkIcon = iconProviderClass.getMethod("getBarkIcon", enumWoodTypeClass);
            forestryGetPlankIcon = iconProviderClass.getMethod("getPlankIcon", enumWoodTypeClass);
            forestryWoodTypes = (Object[]) enumWoodTypeClass.getField("VALUES").get(null);
        } catch (Throwable ignored) {
            forestryGetWoodTile = null;
            forestryGetWoodType = null;
            forestryGetBarkIcon = null;
            forestryGetPlankIcon = null;
            forestryWoodTypes = null;
        }
    }

    private enum FenceStyle {
        VANILLA_OAK,
        ETF_SPRUCE,
        ETF_BIRCH,
        ETF_JUNGLE,
        ETF_ACACIA,
        ETF_DARK_OAK,
        ETF_MODERN,
        NETHERLICIOUS_CRIMSON,
        NETHERLICIOUS_WARPED,
        NETHERLICIOUS_FOXFIRE,
        FORESTRY
    }

    public static final class FenceRenderTextures {
        public final IIcon railIcon;
        public final IIcon postIcon;

        public FenceRenderTextures(IIcon railIcon, IIcon postIcon) {
            this.railIcon = railIcon;
            this.postIcon = postIcon;
        }
    }
}
