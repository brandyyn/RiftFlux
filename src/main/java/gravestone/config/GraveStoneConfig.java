package gravestone.config;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.client.registry.RenderingRegistry;
import gravestone.block.GraveStoneHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class GraveStoneConfig {
   public static final String CATEGORY_GRAVESTONE = "gravestone";
   private static Configuration config;
   private static GraveStoneConfig instance;
   private static String path;
   public static final String CATEGORY_COMPATIBILITY = CATEGORY_GRAVESTONE;
   public static final String CATEGORY_POTIONS = CATEGORY_GRAVESTONE;
   public static final String CATEGORY_STRUCTURES_CATACOMBS = CATEGORY_GRAVESTONE;
   public static final String CATEGORY_RECIPES = CATEGORY_GRAVESTONE;
   public static int graveRenderID = RenderingRegistry.getNextAvailableRenderId();
   public static int memorialRenderID = RenderingRegistry.getNextAvailableRenderId();
   public static int spawnerRenderID = RenderingRegistry.getNextAvailableRenderId();
   public static int skullCandleRenderID = RenderingRegistry.getNextAvailableRenderId();
   public static int candleRenderID = RenderingRegistry.getNextAvailableRenderId();
   public static int pileOfBonesRenderID = RenderingRegistry.getNextAvailableRenderId();
   public static int structuresDimensionId;
   public static boolean generateCatacombs;
   public static boolean generateSingleGraves;
   public static boolean generateMemorials;
   public static int maxCatacombsHeight;
   public static double catacombsGenerationChance;
   public static boolean generateCemeteries;
   public static boolean generateVillageMemorials;
   public static boolean generateUndertaker;
   public static boolean generatePlayerGraves;
   public static boolean enablePlayerDeathGraves;
   public static boolean enableXaeroMinimapGraveWaypoints;
   public static boolean keepArmorOnDeath;
   public static boolean keepBaublesOnDeath;
   public static String[] keepBaubleSlotWhitelist;
   public static String[] keepBaubleSlotBlacklist;
   public static boolean keepTravellersGearOnDeath;
   public static String[] keepTravellersGearItemWhitelist;
   public static String[] keepTravellersGearItemBlacklist;
   public static String[] keepItemsOnDeathWhitelist;
   public static boolean keepHotbarOnDeath;
   public static boolean generateVillagerGraves;
   public static boolean generatePetGraves;
   public static boolean renderGravesFlowers;
   public static boolean vanillaRendererForSwordsGraves;
   public static boolean generateGravesInLava;
   public static int graveItemsCount;
   public static int graveSpawnRate;
   public static boolean canPlaceGravesEveryWhere;
   public static boolean spawnMobAtGraveDestruction;
   public static boolean isFogEnabled;
   public static boolean enableNightStone;
   public static boolean enableThunderStone;
   public static boolean showNightStoneMessage;
   public static boolean enableCreeperStatuesRecipes;
   public static boolean enableBossSpawnerCraftingRecipe;
   public static boolean enableSpawnerCraftingRecipe;
   public static boolean craftableNightStone;
   public static boolean craftableThunderStone;
   public static boolean hardAltarRecipe;
   public static boolean replaceHauntedChest;
   public static ArrayList<String> graveNames;
   public static ArrayList<String> graveDogsNames;
   public static ArrayList<String> graveCatsNames;
   public static ArrayList<String> graveDeathMessages;
   public static ArrayList<String> memorialText;
   public static ArrayList<String> dogsMemorialText;
   public static ArrayList<String> catsMemorialText;
   public static boolean spawnZombieDogs;
   public static boolean spawnZombieCats;
   public static boolean spawnSkeletonDogs;
   public static boolean spawnSkeletonCats;
   public static boolean spawnSkullCrawlersAtMobsDeath;
   public static boolean spawnSkullCrawlersAtBoneBlockDestruction;
   public static boolean generateSwordGraves;
   public static int spawnChance;
   public static boolean removeEmptyGraves;
   public static boolean showGravesRemovingMessages;
   public static boolean onlyPlayersCanBreakGraves;
   public static boolean protectGravesFromExplosions;
   public static boolean onlyOwnerCanBreakGraves;
   public static boolean generateCatacombsGraveyard;
   public static boolean generateEyeboneInGraveyards;
   public static boolean generateEyeboneInCatacombsGrave;
   public static boolean generateEyeboneChestInLowestCatacombs;
   public static boolean generateEyeboneChestInRandomCatacombsLayer;
   public static int catacombsMinRoomsCountAt1Level;
   public static int catacombsMaxRoomsCountAt1Level;
   public static int catacombsMinRoomsCountAt2Level;
   public static int catacombsMaxRoomsCountAt2Level;
   public static int catacombsMinRoomsCountAt3Level;
   public static int catacombsMaxRoomsCountAt3Level;
   public static int catacombsMinRoomsCountAt4Level;
   public static int catacombsMaxRoomsCountAt4Level;
   public static boolean generatePilesOfBones;
   public static boolean generateGravesInMushroomBiomes;
   public static int undertakerId;
   public static int cursePotionEffectId;
   public static boolean spawnMoCreaturesMobs;
   public static boolean enableForestryBackpacks;
   public static boolean storeBattlegearItems;
   public static boolean storeTheCampingModItems;
   public static boolean storeBaublesItems;
   public static boolean storeTravellersGearItems;
   public static boolean storeMaricultureItems;
   public static boolean storeTinkerConstructItems;
   public static boolean storeRpgInventoryItems;
   public static boolean storeGalacticraftItems;
   public static boolean storeBackpacksItems;
   public static boolean enableArsMagicaSoulbound;
   public static boolean enableEnderIOSoulbound;
   public static boolean enableTwilightForestKeeping;
   public static boolean enableAntiqueAtlasDeathMarkers;
   public static int chiselDurability;
   public static String[] chiselItems;
   public static List<GraveStoneHelper.RestrictedArea> restrictGraveGenerationInArea;

   private GraveStoneConfig(String path) {
      config = ModConfig.config;
      if (config == null) {
         throw new IllegalStateException("RiftFlux config is not initialized before Gravestone integration");
      }
      GraveStoneConfig.path = path;
      this.getConfigs();
   }

   public static GraveStoneConfig getInstance(String path) {
      return instance == null ? new GraveStoneConfig(path) : instance;
   }

   public final void getConfigs() {
      config.load();
      config.setCategoryComment(CATEGORY_GRAVESTONE, "Integrated Gravestone module settings. Compatibility toggles can disable individual mod integrations if another mod changes its API or causes a conflict.");
      structures();
      gravesConfig();
      entityConfig();
      compatibilityConfigs();
      config.save();
      this.getGravesText();
   }

   private static void structures() {
      structuresDimensionId = config.get(CATEGORY_GRAVESTONE, "StructuresDimensionId", 0, "Allows choosing the dimension in which Gravestone structures can generate.").getInt();
      generateCatacombs = config.get(CATEGORY_GRAVESTONE, "GenerateCatacombs", true, "Enable or disable catacombs generation.").getBoolean(true);
      maxCatacombsHeight = config.get(CATEGORY_GRAVESTONE, "MaximumCatacombsGenerationHeight", 75, "Maximum ground height at which catacombs are allowed to generate.").getInt();
      catacombsGenerationChance = config.get(CATEGORY_GRAVESTONE, "CatacombsGenerationChance", 2.5E-4D, "Chance to generate catacombs.").getDouble();
      generateCatacombsGraveyard = config.get(CATEGORY_GRAVESTONE, "GenerateCatacombsGraveyard", true, "Enable or disable catacombs graveyard generation.").getBoolean(true);
      generateEyeboneInGraveyards = config.get(CATEGORY_GRAVESTONE, "GenerateEyeboneInGraveyards", true, "Add exactly one Chester Eyebone to a random courtyard grave outside each generated catacombs. Requires the Chester module.").getBoolean(true);
      generateEyeboneInCatacombsGrave = config.get(CATEGORY_GRAVESTONE, "GenerateEyeboneInCatacombsGrave", true, "Add exactly one Chester Eyebone to a random underground grave across all levels of each generated catacombs. Requires the Chester module.").getBoolean(true);
      generateEyeboneChestInLowestCatacombs = config.get(CATEGORY_GRAVESTONE, "GenerateEyeboneChestInLowestCatacombs", true, "Add one Eyebone to a randomly selected existing loot chest on the lowest catacombs level. Requires the Chester module.").getBoolean(true);
      generateEyeboneChestInRandomCatacombsLayer = config.get(CATEGORY_GRAVESTONE, "GenerateEyeboneChestInRandomCatacombsLayer", true, "Add one Eyebone to an existing loot chest on a randomly selected catacombs level. Requires the Chester module.").getBoolean(true);
      generateGravesInMushroomBiomes = config.get(CATEGORY_GRAVESTONE, "GenerateGravesInMushroomBiomes", false, "Enable or disable world-generated graves in Mushroom biomes.").getBoolean(false);
      generateMemorials = config.get(CATEGORY_GRAVESTONE, "GenerateMemorials", false, "Enable or disable memorial generation.").getBoolean(false);
      generateSingleGraves = config.get(CATEGORY_GRAVESTONE, "GenerateSingleGraves", false, "Enable or disable single-grave generation during world generation.").getBoolean(false);
      generateCemeteries = config.get(CATEGORY_GRAVESTONE, "GenerateCemeteries", false, "Enable or disable cemetery generation in villages.").getBoolean(false);
      generateVillageMemorials = config.get(CATEGORY_GRAVESTONE, "GenerateVillageMemorials", false, "Enable or disable memorial generation in villages.").getBoolean(false);
      generateUndertaker = config.get(CATEGORY_GRAVESTONE, "GenerateUndertaker", true, "Enable or disable undertaker house generation in villages.").getBoolean(true);
      undertakerId = config.get(CATEGORY_GRAVESTONE, "undertakerId", 385, "Villager profession ID used by the undertaker.").getInt();
      generatePilesOfBones = config.get(CATEGORY_GRAVESTONE, "GeneratePilesOfBones", false, "Enable or disable piles of bones in catacombs. Disable to improve performance.").getBoolean(false);
      catacombsMinRoomsCountAt1Level = config.get(CATEGORY_GRAVESTONE, "CatacombsMinRoomsCountAt1Level", 30, "Minimum room count on catacombs level 1.").getInt();
      catacombsMaxRoomsCountAt1Level = config.get(CATEGORY_GRAVESTONE, "CatacombsMaxRoomsCountAt1Level", 60, "Maximum room count on catacombs level 1.").getInt();
      catacombsMinRoomsCountAt2Level = config.get(CATEGORY_GRAVESTONE, "CatacombsMinRoomsCountAt2Level", 60, "Minimum room count on catacombs level 2.").getInt();
      catacombsMaxRoomsCountAt2Level = config.get(CATEGORY_GRAVESTONE, "CatacombsMaxRoomsCountAt2Level", 120, "Maximum room count on catacombs level 2.").getInt();
      catacombsMinRoomsCountAt3Level = config.get(CATEGORY_GRAVESTONE, "CatacombsMinRoomsCountAt3Level", 90, "Minimum room count on catacombs level 3.").getInt();
      catacombsMaxRoomsCountAt3Level = config.get(CATEGORY_GRAVESTONE, "CatacombsMaxRoomsCountAt3Level", 180, "Maximum room count on catacombs level 3.").getInt();
      catacombsMinRoomsCountAt4Level = config.get(CATEGORY_GRAVESTONE, "CatacombsMinRoomsCountAt4Level", 160, "Minimum room count on catacombs level 4.").getInt();
      catacombsMaxRoomsCountAt4Level = config.get(CATEGORY_GRAVESTONE, "CatacombsMaxRoomsCountAt4Level", 320, "Maximum room count on catacombs level 4.").getInt();
   }

   private static void gravesConfig() {
      canPlaceGravesEveryWhere = config.get(CATEGORY_GRAVESTONE, "CanPlaceGravesEveryWhere", true, "Allows gravestones to be placed on any type of surface.").getBoolean(true);
      enablePlayerDeathGraves = config.get(CATEGORY_GRAVESTONE, "EnablePlayerDeathGraves", true, "Enable this module's player-death graves and inventory capture. Disable when another grave mod handles player deaths; all other Gravestone content remains enabled.").getBoolean(true);
      enableXaeroMinimapGraveWaypoints = config.get(CATEGORY_GRAVESTONE, "EnableXaeroMinimapGraveWaypoints", true, "Make Xaero's Minimap death waypoint use the exact location where the player's gravestone was placed. Disable to retain Xaero's normal death-location waypoint behavior.").getBoolean(true);
      keepArmorOnDeath = config.get(CATEGORY_GRAVESTONE, "KeepArmorOnDeath", false, "Keep equipped vanilla armour on the player after death instead of storing it in the grave or dropping it. No copies are created.").getBoolean(false);
      keepBaublesOnDeath = config.get(CATEGORY_GRAVESTONE, "KeepBaublesOnDeath", true, "Keep items from allowed Baubles Expanded slots on the player after death instead of storing them in the grave or dropping them. When both slot lists are empty, every Baubles slot is allowed. The blacklist always takes precedence over the whitelist.").getBoolean(true);
      keepBaubleSlotWhitelist = config.get(CATEGORY_GRAVESTONE, "KeepBaubleSlotWhitelist", new String[0], "Baubles Expanded slot types or numeric slot indexes whose items may stay with the player after death. Examples: ring, amulet, head, or slot:4. Empty means all slots unless blacklisted.").getStringList();
      keepBaubleSlotBlacklist = config.get(CATEGORY_GRAVESTONE, "KeepBaubleSlotBlacklist", new String[]{"chester_staff"}, "Baubles Expanded slot types or numeric slot indexes whose items must not be kept by KeepBaublesOnDeath. Blacklist takes precedence over the Baubles slot whitelist. Chester's staff slot is blacklisted by default.").getStringList();
      keepTravellersGearOnDeath = config.get(CATEGORY_GRAVESTONE, "KeepTravellersGearOnDeath", true, "Keep equipped Traveller's Gear items on the player after death instead of storing them in the grave or dropping them. When both Traveller's Gear item lists are empty, every equipped item is kept. The blacklist always takes precedence over all keep-item whitelists.").getBoolean(true);
      keepTravellersGearItemWhitelist = config.get(CATEGORY_GRAVESTONE, "KeepTravellersGearItemWhitelist", new String[0], "Registry names of equipped Traveller's Gear items that may stay with the player after death. Use modid:item or modid:item:metadata. Empty allows every equipped Traveller's Gear item unless blacklisted.").getStringList();
      keepTravellersGearItemBlacklist = config.get(CATEGORY_GRAVESTONE, "KeepTravellersGearItemBlacklist", new String[0], "Registry names of equipped Traveller's Gear items that must not stay with the player after death. Use modid:item or modid:item:metadata. Blacklist takes precedence over the Traveller's Gear whitelist and KeepItemsOnDeathWhitelist. Empty blocks no items.").getStringList();
      keepItemsOnDeathWhitelist = config.get(CATEGORY_GRAVESTONE, "KeepItemsOnDeathWhitelist", new String[0], "Registry names of items that stay with the player after death, regardless of whether they are in the main inventory, hotbar, armour, a Baubles slot, or a Traveller's Gear slot. Use modid:item or modid:item:metadata. Empty disables this rule. Traveller's Gear blacklist entries still take precedence.").getStringList();
      keepHotbarOnDeath = config.get(CATEGORY_GRAVESTONE, "KeepHotbarOnDeath", false, "Keep all nine hotbar slots on the player after death instead of storing them in the grave or dropping them. No copies are created.").getBoolean(false);
      generatePlayerGraves = config.get(CATEGORY_GRAVESTONE, "GeneratePlayerGraves", true, "Enable or disable grave generation when players die.").getBoolean(true);
      generateVillagerGraves = config.get(CATEGORY_GRAVESTONE, "GenerateVillagerGraves", false, "Enable or disable grave generation when villagers die.").getBoolean(false);
      generatePetGraves = config.get(CATEGORY_GRAVESTONE, "GeneratePetGraves", true, "Enable or disable grave generation when pets such as dogs, cats, and horses die.").getBoolean(true);
      generateGravesInLava = config.get(CATEGORY_GRAVESTONE, "GenerateGravesInLava", true, "Enable or disable grave generation when an entity dies in lava.").getBoolean(true);
      generateSwordGraves = config.get(CATEGORY_GRAVESTONE, "GenerateSwordGraves", true, "Allows one sword from the player's inventory to be used as their gravestone when they die.").getBoolean(true);
      renderGravesFlowers = config.get(CATEGORY_GRAVESTONE, "RenderGravesFlowers", true, "Enable grave flower rendering. Disable to improve rendering performance.").getBoolean(true);
      vanillaRendererForSwordsGraves = config.get(CATEGORY_GRAVESTONE, "VanillaRendererForSwordsGraves", true, "Controls sword gravestone rendering mode. The vanilla renderer uses considerably more resources.").getBoolean(true);
      chiselDurability = config.get(CATEGORY_GRAVESTONE, "ChiselDurability", 50, "Number of gravestone crafting operations the built-in chisel can perform before breaking.", 1, Integer.MAX_VALUE).getInt();
      chiselItems = config.get(CATEGORY_GRAVESTONE, "ChiselItems", new String[]{"riftflux:Chisel"}, "Items accepted as chisels in all Gravestone crafting recipes. Use modid:item or modid:item:metadata. Remove riftflux:Chisel to disable the built-in chisel in recipes.").getStringList();
      Property graveItemsCountProperty = config.get(CATEGORY_GRAVESTONE, "SavedItemsCount", 40, "Amount of inventory slots stored in a grave when a player dies. Valid range: 0-40; 40 stores all standard inventory slots.");
      graveItemsCount = graveItemsCountProperty.getInt();
      if (graveItemsCount > 40 || graveItemsCount < 0) {
         graveItemsCount = 40;
      }

      Property graveSpawnRateProperty = config.get(CATEGORY_GRAVESTONE, "SpawnRate", 1000, "Amount of ticks between mob spawn attempts by graves. Must be greater than 600.");
      graveSpawnRate = graveSpawnRateProperty.getInt();
      if (graveSpawnRate < 600) {
         graveSpawnRate = 600;
      }

      spawnMobAtGraveDestruction = config.get(CATEGORY_GRAVESTONE, "SpawnMobAtGraveDestruction", false, "Enable or disable mobs spawning when a grave is destroyed.").getBoolean(false);
      spawnChance = config.get(CATEGORY_GRAVESTONE, "SpawnChance", 80, "Chance, in percent, for a mob to spawn by a grave.").getInt();
      isFogEnabled = config.get(CATEGORY_GRAVESTONE, "IsFogEnabled", true, "Enable or disable fog created by graves and cemeteries.").getBoolean(true);
      enableNightStone = config.get(CATEGORY_GRAVESTONE, "EnableNightStone", true, "Enable or disable the Night Stone's time-changing effect.").getBoolean(true);
      enableThunderStone = config.get(CATEGORY_GRAVESTONE, "EnableThunderStone", true, "Enable or disable the Thunder Stone's weather-changing effect.").getBoolean(true);
      showNightStoneMessage = config.get(CATEGORY_GRAVESTONE, "ShowNightStoneMessage", true, "Enable or disable messages when the Night Stone changes time.").getBoolean(true);
      cursePotionEffectId = config.get(CATEGORY_GRAVESTONE, "CursePotionEffectId", 135, "Potion effect ID used by the Gravestone curse.").getInt();
      enableCreeperStatuesRecipes = config.get(CATEGORY_GRAVESTONE, "EnableCreeperStatuesRecipes", true, "Enable or disable creeper statue crafting recipes.").getBoolean(true);
      enableBossSpawnerCraftingRecipe = config.get(CATEGORY_GRAVESTONE, "EnableBossSpawnerCraftingRecipe", false, "Enable or disable the Wither spawner crafting recipe.").getBoolean(false);
      enableSpawnerCraftingRecipe = config.get(CATEGORY_GRAVESTONE, "EnableMonsterSpawnerCraftingRecipe", false, "Enable or disable monster spawner crafting recipes.").getBoolean(false);
      replaceHauntedChest = config.get(CATEGORY_GRAVESTONE, "ReplaceHauntedChest", false, "Replace a haunted chest with a normal chest when used.").getBoolean(false);
      removeEmptyGraves = config.get(CATEGORY_GRAVESTONE, "RemoveEmptyGraves", true, "Automatically remove all empty graves after a random delay.").getBoolean(true);
      showGravesRemovingMessages = config.get(CATEGORY_GRAVESTONE, "ShowGravesRemovingMessages", true, "Enable or disable messages when empty graves are automatically removed.").getBoolean(true);
      onlyPlayersCanBreakGraves = config.get(CATEGORY_GRAVESTONE, "OnlyPlayersCanBreakGraves", true, "Prevent mobs, fake players, mining AI, and compatible mob-griefing mods from breaking gravestones.").getBoolean(true);
      protectGravesFromExplosions = config.get(CATEGORY_GRAVESTONE, "ProtectGravesFromExplosions", true, "Prevent creepers and all other explosions from destroying gravestones.").getBoolean(true);
      onlyOwnerCanBreakGraves = config.get(CATEGORY_GRAVESTONE, "OnlyOwnerCanBreakGraves", false, "Allow only the player whose death created a gravestone to break it. Existing graves without owner data remain breakable.").getBoolean(false);
      Property restrictGraveGenerationInAreaProperty = config.get(CATEGORY_GRAVESTONE, "RestrictGraveGenerationInArea", "", "Disables player-death grave generation within configured areas. Enter dimension ID, start X, start Y, start Z, end X, end Y, and end Z separated by commas. Dimension ID is optional and defaults to 0. Separate multiple areas with semicolons.");
      String ar = restrictGraveGenerationInAreaProperty.getString();
      String[] areas = ar.split(";");
      restrictGraveGenerationInArea = new ArrayList<>(areas.length);

      for(String area : areas) {
         GraveStoneHelper.RestrictedArea restrictedArea = GraveStoneHelper.RestrictedArea.getFromString(area);
         if (restrictedArea != null) {
            restrictGraveGenerationInArea.add(restrictedArea);
         }
      }

      craftableNightStone = config.get(CATEGORY_GRAVESTONE, "CraftableNightStone", true, "Enable or disable the Night Stone crafting recipe.").getBoolean(true);
      craftableThunderStone = config.get(CATEGORY_GRAVESTONE, "CraftableThunderStone", true, "Enable or disable the Thunder Stone crafting recipe.").getBoolean(true);
      hardAltarRecipe = config.get(CATEGORY_GRAVESTONE, "HardAltarRecipe", false, "Enable or disable the hard altar recipe.").getBoolean(false);
   }

   private static void entityConfig() {
      spawnZombieDogs = config.get(CATEGORY_GRAVESTONE, "SpawnZombieDogs", true, "Enable or disable Zombie Dogs spawning in the world.").getBoolean(true);
      spawnZombieCats = config.get(CATEGORY_GRAVESTONE, "SpawnZombieCats", true, "Enable or disable Zombie Cats spawning in the world.").getBoolean(true);
      spawnSkeletonDogs = config.get(CATEGORY_GRAVESTONE, "SpawnSkeletonDogs", true, "Enable or disable Skeleton Dogs spawning in the world.").getBoolean(true);
      spawnSkeletonCats = config.get(CATEGORY_GRAVESTONE, "SpawnSkeletonCats", true, "Enable or disable Skeleton Cats spawning in the world.").getBoolean(true);
      spawnSkullCrawlersAtMobsDeath = config.get(CATEGORY_GRAVESTONE, "SpawnSkullCrawlersAtMobsDeath", true, "Enable or disable Skull Crawlers spawning when mobs die.").getBoolean(true);
      spawnSkullCrawlersAtBoneBlockDestruction = config.get(CATEGORY_GRAVESTONE, "SpawnSkullCrawlersOnBoneBlockDestruction", true, "Enable or disable Skull Crawlers spawning when bone blocks are destroyed.").getBoolean(true);
   }

   private static void compatibilityConfigs() {
      spawnMoCreaturesMobs = config.get(CATEGORY_GRAVESTONE, "SpawnMoCreaturesMobs", true, "Enable or disable supported Mo' Creatures mobs spawning by graves when the mod is installed.").getBoolean(true);
      enableForestryBackpacks = config.get(CATEGORY_GRAVESTONE, "EnableForestryBackpacks", true, "Enable or disable Forestry backpack crafting recipes when Forestry is installed.").getBoolean(true);
      storeBattlegearItems = config.get(CATEGORY_GRAVESTONE, "StoreBattlegearItems", true, "Store Mine & Blade Battlegear 2 items in graves.").getBoolean(true);
      storeTheCampingModItems = config.get(CATEGORY_GRAVESTONE, "StoreTheCampingModItems", true, "Store The Camping Mod items in graves.").getBoolean(true);
      storeBaublesItems = config.get(CATEGORY_GRAVESTONE, "StoreBaublesItems", true, "Store Baubles mod items in graves.").getBoolean(true);
      storeTravellersGearItems = config.get(CATEGORY_GRAVESTONE, "StoreTravellersGearItems", true, "Store Traveller's Gear slot items, including vambraces and pauldrons, in graves.").getBoolean(true);
      storeMaricultureItems = config.get(CATEGORY_GRAVESTONE, "StoreMaricultureItems", true, "Store Mariculture mod items in graves.").getBoolean(true);
      storeTinkerConstructItems = config.get(CATEGORY_GRAVESTONE, "StoreTinkerConstructItems", true, "Store Tinkers' Construct mod items in graves.").getBoolean(true);
      storeRpgInventoryItems = config.get(CATEGORY_GRAVESTONE, "StoreRpgInventoryItems", true, "Store RPG Inventory mod items in graves.").getBoolean(true);
      storeGalacticraftItems = config.get(CATEGORY_GRAVESTONE, "StoreGalacticraftItems", true, "Store Galacticraft mod items in graves.").getBoolean(true);
      storeBackpacksItems = config.get(CATEGORY_GRAVESTONE, "StoreBackpacksItems", true, "Store Backpacks mod items in graves.").getBoolean(true);
      enableArsMagicaSoulbound = config.get(CATEGORY_GRAVESTONE, "EnableArsMagicaSoulbound", true, "Prevent items with Ars Magica Soulbound enchantments from being stored in graves.").getBoolean(true);
      enableEnderIOSoulbound = config.get(CATEGORY_GRAVESTONE, "EnableEnderIOSoulbound", true, "Prevent items with Ender IO Soulbound enchantments from being stored in graves.").getBoolean(true);
      enableTwilightForestKeeping = config.get(CATEGORY_GRAVESTONE, "EnableTwilightForestCharmsOfKeeping", true, "Prevent affected items from being stored in graves when Twilight Forest Charms of Keeping are used.").getBoolean(true);
      enableAntiqueAtlasDeathMarkers = config.get(CATEGORY_GRAVESTONE, "EnableAntiqueAtlasDeathMarkers", true, "Add an Antique Atlas death marker when a player grave is created.").getBoolean(true);
   }

   private void getGravesText() {
      graveNames = readStringsFromFile(path + "graveNames.txt", GravesDefaultText.NAMES);
      graveDogsNames = readStringsFromFile(path + "graveDogsNames.txt", GravesDefaultText.DOG_NAMES);
      graveCatsNames = readStringsFromFile(path + "graveCatsNames.txt", GravesDefaultText.CAT_NAMES);
      graveDeathMessages = readStringsFromFile(path + "graveDeathMessages.txt", GravesDefaultText.DEATH_TEXT);
      memorialText = readStringsFromFile(path + "memorialText.txt", GravesDefaultText.MEMORIAL_TEXT);
      dogsMemorialText = readStringsFromFile(path + "dogsMemorialText.txt", GravesDefaultText.DOGS_MEMORIAL_TEXT);
      catsMemorialText = readStringsFromFile(path + "catsMemorialText.txt", GravesDefaultText.CATS_MEMORIAL_TEXT);
   }

   private static ArrayList<String> readStringsFromFile(String fileName, String[] defaultValues) {
      ArrayList<String> list = new ArrayList<>();
      list.addAll(Arrays.asList(defaultValues));
      return list;
   }
}
