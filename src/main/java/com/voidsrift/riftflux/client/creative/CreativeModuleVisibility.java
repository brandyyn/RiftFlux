package com.voidsrift.riftflux.client.creative;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Locale;

public final class CreativeModuleVisibility {
    private static final String RIFTFLUX_NAMESPACE = Constants.MODID.toLowerCase(Locale.ROOT);

    private static final String[] AXOLOTL_PATHS = {
            "axolotl_bucket", "axolotl_spawn_egg"
    };
    private static final String[] DUCKLING_NAMESPACES = {
            "ghibli", "duckling", "sootspritecraft"
    };
    private static final String[] DUCKLING_PATHS = {
            "raw_duck", "cooked_duck", "duck_egg", "duck_spawn_egg", "quackling_spawn_egg",
            "star_candy", "soot_jar", "soot_sprite_spawn_egg"
    };
    private static final String[] TERRA_PATHS = {
            "lens", "black_lens", "suspicious_looking_eye", "ice_rod", "demon_eye_spawn_egg",
            "whoopie_cushion", "magic_ice", "ice_rod_block", "terra_mushroom", "daybloom",
            "blinkroot", "waterleaf", "deathweed", "fireblossom", "jungle_spore", "moonglow",
            "demon_altar", "hellforge"
    };
    private static final String[] WAM_PATHS = {
            "cyclops_spawn_egg", "flower_man_spawn_egg", "ender_troll_spawn_egg", "jaxx_spawn_egg",
            "black_widow_spawn_egg"
    };
    private static final String[] OFF_LAWN_PATHS = {
            "lawn_block", "sunflower_bush", "bright_sunflower", "beanstalk", "sun_seed"
    };
    private static final String[] PUMPKIN_PASTURES_PATHS = {
            "pumpkin_j", "suspicious_pumpkin", "roast_pumpkin", "pumpkin_stew", "soul_candy",
            "pumpkin_potage", "corrupted_soul", "pumpkin_soul", "enderflame_sword",
            "enderflame_pickaxe", "enderflame_shax", "enderflame_staff", "pumpkin_zombie_spawn_egg",
            "pumpkin_skeleton_spawn_egg", "pumpkin_creeper_spawn_egg"
    };
    private static final String[] MORE_BOWS_PATHS = {
            "diamondbow", "enderbow", "flamebow", "frostbow", "goldbow", "ironbow", "multibow", "stonebow"
    };
    private static final String[] PALARIA_PATHS = {
            "raptor_claw", "creeptile_eye", "endermite_shard", "creeptile_spawn_egg",
            "raptor_chicken_spawn_egg", "cowasaurus_spawn_egg", "ender_walker_spawn_egg",
            "nimatin_spawn_egg", "ender_raptor_chicken_spawn_egg", "magma_raptor_chicken_spawn_egg"
    };
    private static final String[] ASGARD_PREFIXES = {
            "as_shield_", "as_giant_sword_"
    };
    private static final String[] FURNITURE_PREFIXES = {
            "furniture_"
    };
    private static final String[] LEVEL_UP_NAMESPACES = {
            "levelup"
    };
    private static final String[] LEVEL_UP_PATHS = {
            "xptalisman", "respecbook"
    };
    private static final String[] RIFT_EXPLORER_NAMESPACES = {
            "rift explorer", "worldexplorer"
    };
    private static final String[] RIFT_EXPLORER_PATHS = {
            "needle", "raregem", "pebble", "captured_ender_chest", "dart", "specialarrow",
            "longbow_handle", "longbow_string", "slingshot_skeleton_spawn_egg", "slingshot",
            "boomerang", "longbow", "blowpipe"
    };
    private static final String[] INVENTORY_PETS_NAMESPACES = {
            "inventorypets"
    };
    private static final String[] LEGEND_GEAR_NAMESPACES = {
            "legendgear", "legendgear2", "legendgearreturns"
    };
    private static final String[] LEGEND_GEAR_PATHS = {
            "emeraldshard", "stardust", "ingotstarglass", "ingotstarsteel", "blankspellbook",
            "duststarsteel", "fulgurite", "sunfirediamond", "emptyorb", "dimensionalcatalyst",
            "twinklestaff", "firestaff", "zapstaff", "icestaff", "tomescythewind", "tomerayfire",
            "tomeexit", "charmpendant", "magicboomerang", "nucleus", "azurite", "abstractiongel",
            "tuningfork", "record_dragondot", "fortunecookie", "phoenixfeather", "azurefeather",
            "spiritemblem", "spottingscope", "magicring", "milkchocolate", "reedpipes", "badbow",
            "starstoneblock", "infusedstarstoneblock", "starpieceblock", "infusedstarpieceblock",
            "thawingice", "starsand", "struckground", "staraltar", "starwellframe", "starwellcore",
            "ritualblock", "skylensblock", "azuriteore", "caltrops", "itemheadband", "itemwindboots",
            "magicmirror", "quiver", "heartpickup", "mysticseed", "earthmedallion", "windmedallion",
            "firemedallion", "endermedallion", "aeroamulet", "geoamulet", "pyroamulet", "titanband",
            "itembomb", "bombbag", "rockcandy", "hookshot", "itemkey", "itemmagicpowder", "slimesword",
            "mysticshrubblock", "blockbombflower", "blocksugarcube", "blockclayjar", "blockpedestal",
            "blockpedestaltechnical", "blockchained", "starrail", "blockskybeam", "starry_sand",
            "red_starry_sand", "lightning_struck_red_sand", "starglass_sword"
    };
    private static final String[] HEART_CRYSTAL_NAMESPACES = {
            "hearts"
    };
    private static final String[] HEART_CRYSTAL_PATHS = {
            "heart_shard", "heart_crystal", "heart_lantern", "star_lantern"
    };
    private static final String[] SOUL_HEART_NAMESPACES = {
            "healingaltar", "healaltar"
    };
    private static final String[] SOUL_HEART_PATHS = {
            "soulheart", "soul_heart"
    };

    private CreativeModuleVisibility() {
    }

    public static boolean shouldShowStack(ItemStack stack) {
        return stack == null || shouldShowItem(stack.getItem());
    }

    public static boolean shouldShowItem(Item item) {
        if (item == null) {
            return false;
        }

        Block block = Block.getBlockFromItem(item);
        if (block != null && block != Blocks.air && !shouldShowBlock(block)) {
            return false;
        }

        return shouldShowRegistryName(Item.itemRegistry.getNameForObject(item));
    }

    public static boolean shouldShowBlock(Block block) {
        if (block == null || block == Blocks.air) {
            return true;
        }

        return shouldShowRegistryName(Block.blockRegistry.getNameForObject(block));
    }

    private static boolean shouldShowRegistryName(String registryName) {
        RegistryName name = parseName(registryName);
        if (name == null) {
            return true;
        }

        String namespace = name.namespace;
        String path = normalizePath(name.path);

        if (!ModConfig.enableAxolotlModule && matchesRiftFluxPath(namespace, path, AXOLOTL_PATHS)) {
            return false;
        }
        if (!ModConfig.enableDucklingModule && (matches(namespace, DUCKLING_NAMESPACES) || matchesRiftFluxPath(namespace, path, DUCKLING_PATHS))) {
            return false;
        }
        if (!ModConfig.enableTerraModule && matchesRiftFluxPath(namespace, path, TERRA_PATHS)) {
            return false;
        }
        if (!ModConfig.enableWitchesAndMoreModule && matchesRiftFluxPath(namespace, path, WAM_PATHS)) {
            return false;
        }
        if (!ModConfig.enableOffLawnModule && matchesRiftFluxPath(namespace, path, OFF_LAWN_PATHS)) {
            return false;
        }
        if (!ModConfig.enablePumpkinPasturesModule && matchesRiftFluxPath(namespace, path, PUMPKIN_PASTURES_PATHS)) {
            return false;
        }
        if (!ModConfig.enableMoreBowsModule && matchesRiftFluxPath(namespace, path, MORE_BOWS_PATHS)) {
            return false;
        }
        if (!ModConfig.enablePalariaModule && matchesRiftFluxPath(namespace, path, PALARIA_PATHS)) {
            return false;
        }
        if (!ModConfig.enableAsgardShieldModule && matchesRiftFluxPrefix(namespace, path, ASGARD_PREFIXES)) {
            return false;
        }
        if (!ModConfig.enableFurnitureModule && matchesRiftFluxPrefix(namespace, path, FURNITURE_PREFIXES)) {
            return false;
        }
        if (!ModConfig.enableLevelUpModule && (matches(namespace, LEVEL_UP_NAMESPACES) || matchesRiftFluxPath(namespace, path, LEVEL_UP_PATHS))) {
            return false;
        }
        if (!ModConfig.enableRiftExplorerModule && (matches(namespace, RIFT_EXPLORER_NAMESPACES) || matchesRiftFluxPath(namespace, path, RIFT_EXPLORER_PATHS))) {
            return false;
        }
        if (!ModConfig.enableInventoryPetsModule && (matches(namespace, INVENTORY_PETS_NAMESPACES) || isRiftFluxInventoryPet(namespace, path))) {
            return false;
        }
        if (!ModConfig.enableLegendGearModule && (matches(namespace, LEGEND_GEAR_NAMESPACES) || matchesRiftFluxPath(namespace, path, LEGEND_GEAR_PATHS))) {
            return false;
        }
        if (!ModConfig.enableHeartCrystalModule && (matches(namespace, HEART_CRYSTAL_NAMESPACES) || matchesRiftFluxPath(namespace, path, HEART_CRYSTAL_PATHS))) {
            return false;
        }
        if (!ModConfig.enableSoulHeartsModule && (matches(namespace, SOUL_HEART_NAMESPACES) || matchesRiftFluxPath(namespace, path, SOUL_HEART_PATHS))) {
            return false;
        }

        return true;
    }

    private static boolean isRiftFluxInventoryPet(String namespace, String path) {
        return isRiftFluxNamespace(namespace) && (path.startsWith("pet") || path.endsWith("pet"));
    }

    private static boolean matchesRiftFluxPath(String namespace, String path, String[] paths) {
        return isRiftFluxNamespace(namespace) && matches(path, paths);
    }

    private static boolean matchesRiftFluxPrefix(String namespace, String path, String[] prefixes) {
        return isRiftFluxNamespace(namespace) && startsWithAny(path, prefixes);
    }

    private static boolean isRiftFluxNamespace(String namespace) {
        return namespace.length() == 0 || RIFTFLUX_NAMESPACE.equals(namespace);
    }

    private static boolean matches(String value, String[] candidates) {
        for (int i = 0; i < candidates.length; i++) {
            if (candidates[i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static boolean startsWithAny(String value, String[] prefixes) {
        for (int i = 0; i < prefixes.length; i++) {
            if (value.startsWith(prefixes[i])) {
                return true;
            }
        }
        return false;
    }

    private static RegistryName parseName(String registryName) {
        if (registryName == null) {
            return null;
        }

        String lower = registryName.toLowerCase(Locale.ROOT);
        int split = lower.indexOf(':');
        String namespace = split >= 0 ? lower.substring(0, split) : "";
        String path = split >= 0 ? lower.substring(split + 1) : lower;
        if (path.length() == 0) {
            return null;
        }

        return new RegistryName(namespace, path);
    }

    private static String normalizePath(String path) {
        return path.startsWith("item.") ? path.substring("item.".length()) : path;
    }

    private static final class RegistryName {
        private final String namespace;
        private final String path;

        private RegistryName(String namespace, String path) {
            this.namespace = namespace;
            this.path = path;
        }
    }
}
