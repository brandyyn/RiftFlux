package com.voidsrift.riftflux.util;

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public final class LegacyRegistryAliasHelper {
    private static final Logger LOGGER = LogManager.getLogger("RiftFluxLegacyAliases");

    private static Method addAliasMethod;
    private static boolean aliasMethodResolved;

    private LegacyRegistryAliasHelper() {
    }

    public static void registerItemAliases(Item item, String... aliases) {
        if (item == null || aliases == null || aliases.length == 0) {
            return;
        }
        registerAliases(GameData.getItemRegistry(), item, aliases);
    }

    public static void registerBlockAliases(Block block, String... aliases) {
        if (block == null || aliases == null || aliases.length == 0) {
            return;
        }
        registerAliases(GameData.getBlockRegistry(), block, aliases);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerAliases(FMLControlledNamespacedRegistry registry, Object thing, String... aliases) {
        String existingName = registry.getNameForObject(thing);
        if (existingName == null || existingName.isEmpty()) {
            return;
        }

        Method method = resolveAddAliasMethod(registry);
        for (String alias : aliases) {
            if (alias == null || alias.isEmpty() || existingName.equals(alias)) {
                continue;
            }

            Object current = registry.getRaw(alias);
            if (current == thing) {
                continue;
            }
            if (current != null) {
                LOGGER.warn("Skipping legacy alias '{}' because it already resolves to a different registry entry.", alias);
                continue;
            }

            try {
                if (method != null) {
                    method.invoke(registry, alias, existingName);
                } else {
                    registry.putObject(alias, thing);
                }
            } catch (ReflectiveOperationException e) {
                LOGGER.warn("Failed to register legacy alias '{}' -> '{}'", alias, existingName, e);
            }
        }
    }

    private static Method resolveAddAliasMethod(FMLControlledNamespacedRegistry<?> registry) {
        if (aliasMethodResolved) {
            return addAliasMethod;
        }

        aliasMethodResolved = true;
        try {
            addAliasMethod = registry.getClass().getDeclaredMethod("addAlias", String.class, String.class);
            addAliasMethod.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Unable to access Forge registry alias hook, falling back to putObject warnings.", e);
        }
        return addAliasMethod;
    }
}
