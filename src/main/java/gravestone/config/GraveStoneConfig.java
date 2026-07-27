package gravestone.config;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.client.registry.RenderingRegistry;
import gravestone.block.GraveStoneHelper;
import gravestone.structures.catacombs.CatacombsGenerationConfig;
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
   public static boolean generateCatacombs;
   public static boolean generateSingleGraves;
   public static boolean generateMemorials;
   public static int maxCatacombsHeight;
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
   public static String[] keepBaubleItemWhitelist;
   public static String[] keepBaubleItemBlacklist;
   public static boolean keepSatchelContentsOnDeath;
   public static boolean keepBackpackContentsOnDeath;
   public static boolean keepPouchContentsOnDeath;
   public static boolean keepTravellersGearOnDeath;
   public static String[] keepTravellersGearSlotWhitelist;
   public static String[] keepTravellersGearSlotBlacklist;
   public static String[] keepItemsOnDeathWhitelist;
   public static boolean keepHotbarOnDeath;
   public static boolean generateVillagerGraves;
   public static boolean generatePetGraves;
   public static boolean renderGravesFlowers;
   public static boolean convertGrassToDirtBelowGraves;
   public static boolean preventGraveDirtFromGrowingGrass;
   public static boolean allowFlowersOnAllGraves;
   public static boolean randomizeEnchantedGravestoneGlintColors;
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
   public static boolean enableSkeletonPetTaming;
   public static boolean enableZombiePetTaming;
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
   public static int pileOfBonesMinDrops;
   public static int pileOfBonesMaxDrops;
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
      generateCatacombs = config.get(CATEGORY_GRAVESTONE, "GenerateCatacombs", true, "Enable or disable catacombs generation.").getBoolean(true);
      maxCatacombsHeight = config.get(CATEGORY_GRAVESTONE, "MaximumCatacombsGenerationHeight", 75, "Maximum ground height at which catacombs are allowed to generate.").getInt();
      String[] catacombsDimensionWhitelist = config.get(CATEGORY_GRAVESTONE, "CatacombsDimensionWhitelist", new String[]{"0|0.033"}, "Dimensions where catacombs may generate, with a percent chance per newly generated chunk. Format: dimensionId|percent. Example: 0|0.033 is dimension 0 at 0.033% (about 1 chance roll per 3,030 chunks). Empty disables catacombs generation.").getStringList();
      String[] catacombsBiomeWhitelist = config.get(CATEGORY_GRAVESTONE, "CatacombsBiomeWhitelist", new String[0], "Biomes where catacombs may generate, with a percent chance per newly generated chunk. Format: biomeSelector|percent. Selectors may be biome IDs, exact names, name:<name>, or dictionary types such as type:FOREST. A matching biome chance overrides the dimension chance; first match wins. Empty allows every non-blacklisted biome using its dimension chance.").getStringList();
      String[] catacombsBiomeBlacklist = config.get(CATEGORY_GRAVESTONE, "CatacombsBiomeBlacklist", new String[0], "Biomes where catacombs must never generate. Uses biome IDs, exact names, name:<name>, or dictionary types such as type:WATER. Blacklist always takes precedence over both whitelists.").getStringList();
      CatacombsGenerationConfig.configure(catacombsDimensionWhitelist, catacombsBiomeWhitelist, catacombsBiomeBlacklist);
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
      int[] pileOfBonesDrops = parseDropRange(config.get(CATEGORY_GRAVESTONE, "PileOfBonesDrops", "0-3", "Number of bones dropped when a pile of bones is broken. Use a range such as 0-3, or one number for a fixed amount.").getString(), 0, 3);
      pileOfBonesMinDrops = pileOfBonesDrops[0];
      pileOfBonesMaxDrops = pileOfBonesDrops[1];
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
      keepBaublesOnDeath = config.get(CATEGORY_GRAVESTONE, "KeepBaublesOnDeath", true, "Keep allowed equipped Baubles items on the player after death instead of storing them in the grave or dropping them. This controls satchel, backpack, and pouch carrier items too; their stored contents are controlled separately by the three carrier-content settings. Blacklists take precedence over all keep rules.").getBoolean(true);
      keepBaubleSlotWhitelist = config.get(CATEGORY_GRAVESTONE, "KeepBaubleSlotWhitelist", new String[0], "Baubles Expanded slot types or numeric slot indexes whose items may stay with the player after death. Examples: ring, amulet, head, or slot:4. Empty means all slots unless blacklisted.").getStringList();
      keepBaubleSlotBlacklist = config.get(CATEGORY_GRAVESTONE, "KeepBaubleSlotBlacklist", new String[]{"chester_staff"}, "Baubles Expanded slot types or numeric slot indexes whose items must not be kept by KeepBaublesOnDeath. Blacklist takes precedence over the Baubles slot whitelist. Chester's staff slot is blacklisted by default.").getStringList();
      keepBaubleItemWhitelist = config.get(CATEGORY_GRAVESTONE, "KeepBaubleItemWhitelist", new String[0], "Registry names of equipped Baubles items that may stay with the player after death. Use modid:item or modid:item:metadata. Empty allows every item in an allowed Baubles slot unless blacklisted.").getStringList();
      keepBaubleItemBlacklist = config.get(CATEGORY_GRAVESTONE, "KeepBaubleItemBlacklist", new String[0], "Registry names of equipped Baubles items that must not stay with the player after death. Use modid:item or modid:item:metadata. Blacklist takes precedence over the Baubles item whitelist and KeepItemsOnDeathWhitelist. Empty blocks no items.").getStringList();
      keepSatchelContentsOnDeath = config.get(CATEGORY_GRAVESTONE, "KeepSatchelContentsOnDeath", false, "Keep items stored inside equipped RiftFlux satchels when the player dies. If false, contents leave the satchel and follow normal grave or drop handling; KeepBaublesOnDeath independently controls whether the satchel itself stays equipped. Works regardless of its configured Baubles slot type.").getBoolean(false);
      keepBackpackContentsOnDeath = config.get(CATEGORY_GRAVESTONE, "KeepBackpackContentsOnDeath", false, "Keep items stored inside equipped RiftFlux backpacks when the player dies. If false, contents leave the backpack and follow normal grave or drop handling; KeepBaublesOnDeath independently controls whether the backpack itself stays equipped. Works regardless of its configured Baubles slot type.").getBoolean(false);
      keepPouchContentsOnDeath = config.get(CATEGORY_GRAVESTONE, "KeepPouchContentsOnDeath", false, "Keep items stored inside equipped RiftFlux pouches when the player dies. If false, contents leave every equipped pouch and follow normal grave or drop handling; KeepBaublesOnDeath independently controls whether the pouches themselves stay equipped. Works regardless of their configured Baubles slot type.").getBoolean(false);
      keepTravellersGearOnDeath = config.get(CATEGORY_GRAVESTONE, "KeepTravellersGearOnDeath", true, "Keep items from allowed Traveller's Gear slots on the player after death instead of storing them in the grave or dropping them. When both slot lists are empty, every Traveller's Gear slot is allowed. The blacklist takes precedence over the slot whitelist.").getBoolean(true);
      keepTravellersGearSlotWhitelist = config.get(CATEGORY_GRAVESTONE, "KeepTravellersGearSlotWhitelist", new String[0], "Traveller's Gear slot types or numeric slot indexes whose items may stay with the player after death. Types: cloak, shoulder, vambrace, title. Numeric examples: 0 or slot:0. Empty means all slots unless blacklisted.").getStringList();
      keepTravellersGearSlotBlacklist = config.get(CATEGORY_GRAVESTONE, "KeepTravellersGearSlotBlacklist", new String[0], "Traveller's Gear slot types or numeric slot indexes whose items must not be kept by KeepTravellersGearOnDeath. Types: cloak, shoulder, vambrace, title. Blacklist takes precedence over the Traveller's Gear slot whitelist.").getStringList();
      keepItemsOnDeathWhitelist = config.get(CATEGORY_GRAVESTONE, "KeepItemsOnDeathWhitelist", new String[0], "Registry names of items that stay with the player after death, regardless of whether they are in the main inventory, hotbar, armour, a Baubles slot, or a Traveller's Gear slot. Use modid:item or modid:item:metadata. Empty disables this rule. Compatibility blacklists still take precedence.").getStringList();
      keepHotbarOnDeath = config.get(CATEGORY_GRAVESTONE, "KeepHotbarOnDeath", false, "Keep all nine hotbar slots on the player after death instead of storing them in the grave or dropping them. No copies are created.").getBoolean(false);
      generatePlayerGraves = config.get(CATEGORY_GRAVESTONE, "GeneratePlayerGraves", true, "Enable or disable grave generation when players die.").getBoolean(true);
      generateVillagerGraves = config.get(CATEGORY_GRAVESTONE, "GenerateVillagerGraves", false, "Enable or disable grave generation when villagers die.").getBoolean(false);
      generatePetGraves = config.get(CATEGORY_GRAVESTONE, "GeneratePetGraves", true, "Enable or disable grave generation when pets such as dogs, cats, and horses die.").getBoolean(true);
      generateGravesInLava = config.get(CATEGORY_GRAVESTONE, "GenerateGravesInLava", true, "Enable or disable grave generation when an entity dies in lava.").getBoolean(true);
      generateSwordGraves = config.get(CATEGORY_GRAVESTONE, "GenerateSwordGraves", true, "Allows one sword from the player's inventory to be used as their gravestone when they die.").getBoolean(true);
      renderGravesFlowers = config.get(CATEGORY_GRAVESTONE, "RenderGravesFlowers", true, "Enable grave flower rendering. Disable to improve rendering performance.").getBoolean(true);
      convertGrassToDirtBelowGraves = config.get(CATEGORY_GRAVESTONE, "ConvertGrassToDirtBelowGraves", true, "Convert grass and mycelium directly beneath a newly placed or generated gravestone into dirt. Disable to preserve the original ground block.").getBoolean(true);
      preventGraveDirtFromGrowingGrass = config.get(CATEGORY_GRAVESTONE, "PreventGraveDirtFromGrowingGrass", false, "Keep dirt beneath a gravestone from growing into grass or mycelium while the gravestone remains. Works independently from ConvertGrassToDirtBelowGraves, so graves placed on existing grass can leave it unchanged.").getBoolean(false);
      allowFlowersOnAllGraves = config.get(CATEGORY_GRAVESTONE, "AllowFlowersOnAllGraves", false, "Allow supported vanilla and modded flowers to be placed or generated on every gravestone type, including horizontal, statue, and sword graves.").getBoolean(false);
      randomizeEnchantedGravestoneGlintColors = config.get(CATEGORY_GRAVESTONE, "RandomizeEnchantedGravestoneGlintColors", true, "Give each newly generated enchanted gravestone one random glint colour instead of the default purple. Existing gravestones and gravestones with an explicitly selected Glint Rune colour are unchanged.").getBoolean(true);
      vanillaRendererForSwordsGraves = config.get(CATEGORY_GRAVESTONE, "VanillaRendererForSwordsGraves", true, "Controls sword gravestone rendering mode. The vanilla renderer uses considerably more resources.").getBoolean(true);
      chiselDurability = config.get(CATEGORY_GRAVESTONE, "ChiselDurability", 50, "Maximum durability of the built-in RiftFlux chisel. Crafting consumes 1 durability, editing gravestone text consumes 2, and editing memorial text consumes 5. Minimum: 1.", 1, Integer.MAX_VALUE).getInt();
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
      cursePotionEffectId = config.get(CATEGORY_GRAVESTONE, "CursePotionEffectId", 135, "Preferred potion effect ID for the Gravestone curse. If occupied, the next free potion ID is used.").getInt();
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

      hardAltarRecipe = config.get(CATEGORY_GRAVESTONE, "HardAltarRecipe", false, "Enable or disable the hard altar recipe.").getBoolean(false);
   }

   private static void entityConfig() {
      spawnZombieDogs = config.get(CATEGORY_GRAVESTONE, "SpawnZombieDogs", true, "Allow Zombie Dogs to spawn naturally, from gravestones, and from Gravestone spawners. Does not disable spawn eggs or remove existing entities. Requires restart.").getBoolean(true);
      spawnZombieCats = config.get(CATEGORY_GRAVESTONE, "SpawnZombieCats", true, "Allow Zombie Cats to spawn naturally, from gravestones, and from Gravestone spawners. Does not disable spawn eggs or remove existing entities. Requires restart.").getBoolean(true);
      spawnSkeletonDogs = config.get(CATEGORY_GRAVESTONE, "SpawnSkeletonDogs", true, "Allow Skeleton Dogs to spawn naturally, from gravestones, and from Gravestone spawners. Does not disable spawn eggs or remove existing entities. Requires restart.").getBoolean(true);
      spawnSkeletonCats = config.get(CATEGORY_GRAVESTONE, "SpawnSkeletonCats", true, "Allow Skeleton Cats to spawn naturally, from gravestones, and from Gravestone spawners. Does not disable spawn eggs or remove existing entities. Requires restart.").getBoolean(true);
      enableSkeletonPetTaming = config.get(CATEGORY_GRAVESTONE, "EnableSkeletonPetTaming", true, "Allow Skeleton Dogs to be tamed with bones and Skeleton Cats to be tamed with raw fish. Tamed skeleton pets follow their owner, stop targeting players, do not despawn, and no longer burn in sunlight.").getBoolean(true);
      enableZombiePetTaming = config.get(CATEGORY_GRAVESTONE, "EnableZombiePetTaming", true, "Allow Zombie Dogs to be tamed with bones and Zombie Cats to be tamed with raw fish. Tamed zombie pets use normal pet ownership, sitting, following, owner defence, persistence, sunlight immunity, and RiftFlux pet knockdown behavior.").getBoolean(true);
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

   private static int[] parseDropRange(String value, int defaultMin, int defaultMax) {
      if (value == null) {
         return new int[]{defaultMin, defaultMax};
      }

      String[] parts = value.trim().split("-", -1);
      if (parts.length < 1 || parts.length > 2) {
         return new int[]{defaultMin, defaultMax};
      }

      try {
         int min = Math.max(0, Math.min(64, Integer.parseInt(parts[0].trim())));
         int max = parts.length == 1 ? min : Math.max(0, Math.min(64, Integer.parseInt(parts[1].trim())));
         return new int[]{Math.min(min, max), Math.max(min, max)};
      } catch (NumberFormatException ignored) {
         return new int[]{defaultMin, defaultMax};
      }
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
