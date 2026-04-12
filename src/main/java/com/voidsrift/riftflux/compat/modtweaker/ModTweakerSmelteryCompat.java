package com.voidsrift.riftflux.compat.modtweaker;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ModTweakerSmelteryCompat {

    private static final Logger LOGGER = LogManager.getLogger("RiftFlux|ModTweakerSmeltery");
    private static final Set<String> LOGGED_INVALID_RENDERERS =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private ModTweakerSmelteryCompat() {}

    public static boolean needsRendererFallback(ItemStack renderer) {
        return !isValidRenderer(renderer);
    }

    public static Block resolveRendererBlock(ItemStack input, ItemStack renderer) {
        return Block.getBlockFromItem(getFallbackRenderer(input, renderer).getItem());
    }

    public static int resolveRendererMeta(ItemStack input, ItemStack renderer) {
        return getFallbackRenderer(input, renderer).getItemDamage();
    }

    private static ItemStack getFallbackRenderer(ItemStack input, ItemStack renderer) {
        if (isValidRenderer(renderer)) {
            return renderer;
        }

        ItemStack fallback = isValidRenderer(input) ? input : new ItemStack(Blocks.stone, 1, 0);
        logInvalidRenderer(input, renderer, fallback);
        return fallback;
    }

    private static boolean isValidRenderer(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        return Block.getBlockFromItem(item) != Blocks.air;
    }

    private static void logInvalidRenderer(ItemStack input, ItemStack renderer, ItemStack fallback) {
        String key = describe(input) + "|" + describe(renderer) + "|" + describe(fallback);
        if (!LOGGED_INVALID_RENDERERS.add(key)) {
            return;
        }

        LOGGER.warn(
                "Recovered invalid ModTweaker smeltery renderer while restoring melting recipe for {}. "
                        + "Original renderer: {}. Fallback renderer: {}. "
                        + "This usually means another mod registered a TConstruct melting recipe with a render block that has no ItemBlock.",
                describe(input),
                describe(renderer),
                describe(fallback));
    }

    private static String describe(ItemStack stack) {
        if (stack == null) {
            return "null";
        }

        Item item = stack.getItem();
        String itemName = item == null ? "null-item" : String.valueOf(Item.itemRegistry.getNameForObject(item));
        String meta = item == null ? "unknown" : String.valueOf(stack.getItemDamage());
        return itemName + " x" + stack.stackSize + " meta " + meta;
    }
}
