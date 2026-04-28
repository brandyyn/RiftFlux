package com.voidsrift.riftflux.util;

import com.voidsrift.riftflux.ModConfig;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public final class TorchRenderRules {
    private TorchRenderRules() {
    }

    public static boolean isTorchRenderType(Block block) {
        return block != null && block.getRenderType() == Blocks.redstone_torch.getRenderType();
    }

    public static boolean isModernTorchRenderingEligible(Block block, int meta) {
        if (!isTorchRenderType(block)) {
            return false;
        }
        return torchFilterAllows(
                block,
                ModConfig.modernTorchRenderingWhitelist,
                ModConfig.modernTorchRenderingBlacklist
        );
    }

    public static boolean shouldUseBillboardTorchRendering(Block block, int meta) {
        boolean useModernTorchModel = ModConfig.modernTorchRendering && isModernTorchRenderingEligible(block, meta);
        return shouldUseBillboardTorchRendering(block, meta, useModernTorchModel);
    }

    public static boolean shouldUseBillboardTorchRendering(Block block, int meta, boolean useModernTorchModel) {
        if (!isTorchRenderType(block)) {
            return false;
        }
        if (matchesTorchFilter(block, ModConfig.billboardTorchRenderingWhitelist)) {
            return true;
        }
        if (matchesTorchFilter(block, ModConfig.billboardTorchRenderingBlacklist)) {
            return false;
        }
        return !useModernTorchModel;
    }

    public static boolean torchFilterAllows(Block block, String[] whitelist, String[] blacklist) {
        if (matchesTorchFilter(block, whitelist)) {
            return true;
        }
        return !matchesTorchFilter(block, blacklist);
    }

    public static boolean matchesTorchFilter(Block block, String[] entries) {
        if (block == null || entries == null || entries.length == 0) {
            return false;
        }

        String registryName = lower(Block.blockRegistry.getNameForObject(block));
        String modId = "";
        String blockPath = registryName;
        int separator = registryName.indexOf(':');
        if (separator >= 0) {
            modId = registryName.substring(0, separator);
            blockPath = registryName.substring(separator + 1);
        }

        String unlocalizedName = lower(block.getUnlocalizedName());
        String className = lower(block.getClass().getName());
        String simpleClassName = lower(block.getClass().getSimpleName());

        for (String rawEntry : entries) {
            if (rawEntry == null) {
                continue;
            }
            String entry = rawEntry.trim().toLowerCase(Locale.ROOT);
            if (entry.isEmpty()) {
                continue;
            }

            if (entry.endsWith(":*")) {
                if (entry.substring(0, entry.length() - 2).equals(modId)) {
                    return true;
                }
                continue;
            }

            if (entry.indexOf(':') >= 0) {
                if (entry.equals(registryName)) {
                    return true;
                }
                continue;
            }

            if (entry.equals(modId)
                    || entry.equals(blockPath)
                    || entry.equals(unlocalizedName)
                    || unlocalizedName.endsWith("." + entry)
                    || entry.equals(className)
                    || entry.equals(simpleClassName)) {
                return true;
            }
        }
        return false;
    }

    private static String lower(Object value) {
        return value == null ? "" : String.valueOf(value).toLowerCase(Locale.ROOT);
    }
}
