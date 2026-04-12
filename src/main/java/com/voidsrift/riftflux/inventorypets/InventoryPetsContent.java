package com.voidsrift.riftflux.inventorypets;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.util.LegacyRegistryAliasHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import cpw.mods.fml.client.registry.RenderingRegistry;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class InventoryPetsContent {
    private static final PetDefinition[] PETS = new PetDefinition[]{
            pet("cow", "petCow", "Cow", "cow_pet"),
            variant("cow_variant", "petCowVariant", "Cow", "cow_new"),
            pet("sheep", "petSheep", "Sheep", "sheep_pet"),
            variant("sheep_variant", "petSheepVariant", "Sheep", "sheep_new"),
            pet("pig", "petPig", "Pig", "pig_pet"),
            variant("pig_variant", "petPigVariant", "Pig", "pig_new"),
            pet("chicken", "petChicken", "Chicken", "chicken_pet"),
            variant("chicken_variant", "petChickenVariant", "Chicken", "chicken_new"),
            pet("squid", "petSquid", "Squid", "squid_pet"),
            variant("squid_variant", "petSquidVariant", "Squid", "squid_new"),
            pet("ocelot", "petOcelot", "Ocelot", "ocelot_pet"),
            variant("ocelot_variant", "petOcelotVariant", "Ocelot", "ocelot_new"),
            pet("mooshroom", "petMooshroom", "Mooshroom", "mooshroom_pet"),
            variant("mooshroom_variant", "petMooshroomVariant", "Mooshroom", "mooshroom_new"),
            pet("ghast", "petGhast", "Ghast", "ghast_pet"),
            variant("ghast_variant", "petGhastVariant", "Ghast", "ghast_new"),
            pet("spider", "petSpider", "Spider", "spider_pet"),
            variant("spider_variant", "petSpiderVariant", "Spider", "spider_new"),
            pet("iron_golem", "petIronGolem", "Iron Golem", "iron_golem_pet"),
            variant("iron_golem_variant", "petIronGolemVariant", "Iron Golem", "iron_golem_new"),
            pet("snow_golem", "petSnowGolem", "Snow Golem", "snow_golem_pet"),
            variant("snow_golem_variant", "petSnowGolemVariant", "Snow Golem", "snow_golem_new"),
            pet("enderman", "petEnderman", "Enderman", "enderman_pet"),
            variant("enderman_variant", "petEndermanVariant", "Enderman", "enderman_new"),
            pet("creeper", "petCreeper", "Creeper", "creeper_pet"),
            variant("creeper_variant", "petCreeperVariant", "Creeper", "creeper_new"),
            pet("magma_cube", "petMagmaCube", "Magma Cube", "magma_cube_pet"),
            variant("magma_cube_variant", "petMagmaCubeVariant", "Magma Cube", "magma_cube_new"),
            pet("wither", "petWither", "Wither", "wither_pet"),
            variant("wither_variant", "petWitherVariant", "Wither", "wither_new"),
            pet("blaze", "petBlaze", "Blaze", "blaze_pet"),
            variant("blaze_variant", "petBlazeVariant", "Blaze", "blaze_new"),
            pet("bed", "petBed", "Bed", "bed_pet"),
            variant("bed_variant", "petBedVariant", "Bed", "bed_new"),
            pet("chest", "petChest", "Chest", "chest_pet"),
            variant("chest_variant", "petChestVariant", "Chest", "chest_new"),
            pet("sated_chest", "pet_sated_chest", "Sated Chest", "sated_chest"),
            pet("double_chest", "petDoubleChest", "Double Chest", "double_chest_pet"),
            variant("double_chest_variant", "petDoubleChestVariant", "Double Chest", "double_chest_new"),
            pet("sated_double_chest", "pet_sated_double_chest", "Sated Double Chest", "sated_double_chest"),
            pet("ender_chest", "petEnderChest", "Ender Chest", "ender_chest_pet"),
            variant("ender_chest_variant", "petEnderChestVariant", "Ender Chest", "ender_chest_new"),
            pet("furnace", "petFurnace", "Furnace", "furnace_pet"),
            variant("furnace_variant", "petFurnaceVariant", "Furnace", "furnace_new"),
            pet("crafting_table", "petCraftingTable", "Crafting Table", "crafting_table_pet"),
            variant("crafting_table_variant", "petCraftingTableVariant", "Crafting Table", "crafting_table_new"),
            pet("enchanting_table", "petEnchantingTable", "Enchanting Table", "enchanting_table_pet"),
            variant("enchanting_table_variant", "petEnchantingTableVariant", "Enchanting Table", "enchanting_table_new"),
            pet("jukebox", "petJukebox", "Jukebox", "jukebox_pet"),
            variant("jukebox_variant", "petJukeboxVariant", "Jukebox", "jukebox_new"),
            pet("anvil", "petAnvil", "Anvil", "anvil_pet"),
            variant("anvil_variant", "petAnvilVariant", "Anvil", "anvil_new"),
            pet("brewing_stand", "petBrewingStand", "Brewing Stand", "brewing_stand_pet"),
            variant("brewing_stand_variant", "petBrewingStandVariant", "Brewing Stand", "brewing_stand_new"),
            pet("nether_portal", "petNetherPortal", "Nether Portal", "nether_portal_pet"),
            variant("nether_portal_variant", "petNetherPortalVariant", "Nether Portal", "nether_portal_new"),
            pet("end_portal", "pet_end_portal", "End Portal", "end_portal_pet"),
            pet("sponge", "petSponge", "Sponge", "sponge_pet"),
            variant("sponge_variant", "petSpongeVariant", "Sponge", "sponge_new"),
            pet("purplicious_cow", "petPurpliciousCow", "Purplicious Cow", "purplicious_cow_pet"),
            variant("purplicious_cow_variant", "petPurpliciousCowVariant", "Purplicious Cow", "purplicious_cow_new"),
            pet("mickerson", "petMickerson", "Mickerson", "mickerson_pet"),
            variant("mickerson_variant", "petMickersonVariant", "Mickerson", "mickerson_new"),
            pet("pingot", "petPingot", "Pingot", "pingot"),
            pet("dingot", "pet_dingot", "Dingot", "dingot"),
            pet("quantum_crystal_monster", "petQuantumCrystalMonster", "Quantum Crystal Monster", "qcm_pet"),
            banana("banana", "petBanana"),
            pet("loot", "petLoot", "Loot", "loot_pet"),
            variant("loot_variant", "petLootVariant", "Loot", "loot_new"),
            pet("illuminati", "petIlluminati", "Illuminati", "illuminati_pet2"),
            variant("illuminati_variant", "petIlluminatiVariant", "Illuminati", "illuminati_new"),
            pet("juggernaut", "petJuggernaut", "Juggernaut", "juggernaut_pet"),
            variant("juggernaut_variant", "petJuggernautVariant", "Juggernaut", "juggernaut_new"),
            pet("grave", "petGrave", "Grave", "grave_pet"),
            pet("quiver", "petQuiver", "Quiver", "quiver_pet"),
            variant("quiver_variant", "petQuiverVariant", "Quiver", "quiver_new"),
            pet("pacman", "petPacMan", "Pac-Man", "pacman_pet"),
            variant("pacman_variant", "petPacManVariant", "Pac-Man", "pac-man_new"),
            pet("cheetah", "petCheetah", "Cheetah", "cheetah_pet"),
            variant("cheetah_variant", "petCheetahVariant", "Cheetah", "cheetah_new"),
            pet("biome", "pet_biome", "Biome", "biome_new"),
            pet("house", "petHouse", "House", "house_pet"),
            variant("house_variant", "petHouseVariant", "House", "house_new"),
            pet("silverfish", "petSilverfish", "Silverfish", "silverfish_pet"),
            variant("silverfish_variant", "petSilverfishVariant", "Silverfish", "silverfish_new"),
            pet("wolf", "petWolf", "Wolf", "wolf_pet"),
            variant("wolf_variant", "petWolfVariant", "Wolf", "wolf_new"),
            pet("siamese", "pet_siamese", "Siamese", "siamese_new"),
            pet("apple", "petApple", "Apple", "apple_pet"),
            variant("apple_variant", "petAppleVariant", "Apple", "apple_new"),
            pet("sun", "pet_sun", "Sun", "sun_new"),
            pet("slime", "petSlime", "Slime", "slime_pet"),
            variant("slime_variant", "petSlimeVariant", "Slime", "slime_new"),
            pet("cloud", "petCloud", "Cloud", "cloud_pet"),
            variant("cloud_variant", "petCloudVariant", "Cloud", "cloud_new"),
            pet("pixie", "pet_pixie", "Pixie", "pixie_new"),
            pet("pufferfish", "petPufferfish", "Pufferfish", "pufferfish_pet"),
            variant("pufferfish_variant", "petPufferfishVariant", "Pufferfish", "pufferfish_new"),
            pet("black_hole", "petBlackHole", "Black Hole", "black_hole_pet"),
            variant("black_hole_variant", "petBlackHoleVariant", "Black Hole", "black_hole_new"),
            pet("lead", "pet_lead", "Lead", "lead_new"),
            pet("saddle", "pet_saddle", "Saddle", "saddle_new"),
            pet("flying_saddle", "pet_flying_saddle", "Flying Saddle", "flying_saddle_new"),
            pet("shield", "petShield", "Shield", "shield_pet"),
            variant("shield_variant", "petShieldVariant", "Shield", "shield_new"),
            pet("torch", "pet_torch", "Torch", "torch_new"),
            pet("heart", "petHeart", "Heart", "heart_pet"),
            variant("heart_variant", "petHeartVariant", "Heart", "heart_new"),
            pet("moon", "petMoon", "Moon", "moon_pet"),
            variant("moon_variant", "petMoonVariant", "Moon", "moon_new"),
            pet("dubstep", "petDubstep", "Dubstep", "dubstep_pet"),
            variant("dubstep_variant", "petDubstepVariant", "Dubstep", "dubstep_new"),
            pet("custom", "petCustom", "Custom", "custom_pet_1"),
            pet("dirt", "pet_dirt", "Dirt", "dirt"),
            pet("cobblestone", "pet_cobblestone", "Cobblestone", "cobblestone"),
            pet("christmas_tree", "christmasTreePet", "Christmas Tree", "christmas_tree_pet"),
            variant("christmas_tree_variant", "christmasTreePetVariant", "Christmas Tree", "christmas_tree_new"),
            pet("menorah", "menorahPet", "Menorah", "menorah_pet"),
            variant("menorah_variant", "menorahPetVariant", "Menorah", "menorah_new"),
            pet("mishumaa_saba", "mishumaaSabaPet", "Mishumaa Saba", "kwanzaa_pet"),
            variant("mishumaa_saba_variant", "mishumaaSabaPetVariant", "Mishumaa Saba", "mishumaa_saba_pet"),
            pet("politically_correct", "politicallyCorrectPet", "Politically Correct", "politically_correct_pet"),
            variant("politically_correct_variant", "politicallyCorrectPetVariant", "Politically Correct", "politically_correct_new"),
            pet("april_fool", "petAprilFool", "April Fool", "april_fool_pet"),
            variant("april_fool_variant", "petAprilFoolVariant", "April Fool", "april_fool_new")
    };

    private static final Map<String, PetDefinition> PETS_BY_KEY = new LinkedHashMap<String, PetDefinition>();

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;

    static {
        for (PetDefinition definition : PETS) {
            PETS_BY_KEY.put(definition.key, definition);
        }
    }

    private InventoryPetsContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableInventoryPetsModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (!isEnabled() || preInited) {
            return;
        }
        preInited = true;

        for (PetDefinition definition : PETS) {
            Item item = definition.banana ? new ItemBananaPet()
                    : new ItemInventoryPet(definition.registryName, definition.displayName, "riftflux:inventorypets/" + definition.textureName);
            definition.item = item;
            GameRegistry.registerItem(item, definition.registryName);
        }

        EntityRegistry.registerModEntity(EntityBananaBoomerang.class, "inventoryPetBananaBoomerang", 0, riftflux.instance, 64, 10, true);
    }

    public static void init(FMLInitializationEvent event) {
        if (!isEnabled() || initialized) {
            return;
        }
        initialized = true;

        Set<String> enabled = new LinkedHashSet<String>();
        if (ModConfig.inventoryPetsDungeonLootEntries != null) {
            for (String raw : ModConfig.inventoryPetsDungeonLootEntries) {
                if (raw == null) {
                    continue;
                }
                String key = normalizeKey(raw);
                if (!key.isEmpty()) {
                    enabled.add(key);
                }
            }
        }

        int weight = Math.max(0, ModConfig.inventoryPetsDungeonLootWeight);
        if (weight <= 0 || enabled.isEmpty()) {
            return;
        }

        for (PetDefinition definition : PETS) {
            if (definition.item == null) {
                continue;
            }
            if (!enabled.contains(definition.key) && !enabled.contains(normalizeKey(definition.registryName))) {
                continue;
            }
            ChestGenHooks.addItem(
                    ChestGenHooks.DUNGEON_CHEST,
                    new WeightedRandomChestContent(new ItemStack(definition.item), 1, 1, weight)
            );
        }

        EntityBananaBoomerang.BOOMERANG_DAMAGE = ModConfig.inventoryPetsBananaDamage;
    }

    public static void initClient() {
        if (!isEnabled() || clientInited) {
            return;
        }
        clientInited = true;
        RenderingRegistry.registerEntityRenderingHandler(EntityBananaBoomerang.class, new RenderBananaBoomerang());
    }

    public static String[] getDefaultLootPetKeys() {
        String[] values = new String[PETS.length];
        for (int i = 0; i < PETS.length; i++) {
            values[i] = fluxKey(PETS[i].key);
        }
        return values;
    }

    public static String getValidLootPetKeyComment() {
        return Arrays.toString(getDefaultLootPetKeys());
    }

    public static Item resolveLegacyItemAlias(String fullName) {
        if (fullName == null) {
            return null;
        }
        String lower = fullName.toLowerCase(Locale.ROOT);
        if (!lower.startsWith("inventorypets:")) {
            return null;
        }
        String path = lower.substring("inventorypets:".length());
        for (PetDefinition definition : PETS) {
            if (definition.registryName.toLowerCase(Locale.ROOT).equals(path)) {
                return definition.item;
            }
        }
        return null;
    }

    public static Item getItemByKey(String key) {
        if (key == null) {
            return null;
        }
        PetDefinition definition = PETS_BY_KEY.get(normalizeKey(key));
        return definition == null ? null : definition.item;
    }

    public static void registerLegacyItemAliases() {
        if (!preInited) {
            return;
        }

        for (PetDefinition definition : PETS) {
            if (definition.item == null) {
                continue;
            }
            String alias = "inventorypets:" + definition.registryName;
            LegacyRegistryAliasHelper.registerItemAliases(
                    definition.item,
                    alias,
                    alias.toLowerCase(Locale.ROOT)
            );
        }
    }

    private static String normalizeKey(String value) {
        String normalized = value.trim().toLowerCase(Locale.ROOT)
                .replace(' ', '_')
                .replace('-', '_');
        if (normalized.endsWith("_flux")) {
            return normalized.substring(0, normalized.length() - "_flux".length()) + "_variant";
        }
        return normalized;
    }

    private static PetDefinition pet(String key, String registryName, String displayName, String textureName) {
        return new PetDefinition(key, registryName, displayName, textureName, false);
    }

    private static PetDefinition variant(String key, String registryName, String displayName, String textureName) {
        return new PetDefinition(key, registryName, displayName + " Flux", textureName, false);
    }

    private static String fluxKey(String key) {
        return key.endsWith("_variant")
                ? key.substring(0, key.length() - "_variant".length()) + "_flux"
                : key;
    }

    private static PetDefinition banana(String key, String registryName) {
        return new PetDefinition(key, registryName, "Banana", "banana_pet2", true);
    }

    private static final class PetDefinition {
        private final String key;
        private final String registryName;
        private final String displayName;
        private final String textureName;
        private final boolean banana;
        private Item item;

        private PetDefinition(String key, String registryName, String displayName, String textureName, boolean banana) {
            this.key = key;
            this.registryName = registryName;
            this.displayName = displayName;
            this.textureName = textureName;
            this.banana = banana;
        }
    }
}
