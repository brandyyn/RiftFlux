package gravestone.structures.catacombs;

import com.voidsrift.riftflux.util.ConfigResolver;
import gravestone.core.logger.GSLogger;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class CatacombsGenerationConfig {
   private static final double DISABLED_CHANCE = -1.0D;
   private static final Map<Integer, Double> dimensionChances = new LinkedHashMap<>();
   private static final List<BiomeChance> biomeChances = new LinkedList<>();
   private static String[] biomeBlacklist = new String[0];

   private CatacombsGenerationConfig() {
   }

   public static void configure(String[] dimensionEntries, String[] biomeEntries, String[] blacklistEntries) {
      dimensionChances.clear();
      biomeChances.clear();
      biomeBlacklist = blacklistEntries == null ? new String[0] : blacklistEntries.clone();

      if (dimensionEntries != null) {
         for (String entry : dimensionEntries) {
            parseDimensionEntry(entry);
         }
      }

      if (biomeEntries != null) {
         for (String entry : biomeEntries) {
            parseBiomeEntry(entry);
         }
      }
   }

   public static double getGenerationChance(World world, int x, int z) {
      if (world == null || world.provider == null) {
         return DISABLED_CHANCE;
      }

      Double dimensionChance = dimensionChances.get(world.provider.dimensionId);
      if (dimensionChance == null) {
         return DISABLED_CHANCE;
      }

      BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
      if (matchesAnyBiome(biome, biomeBlacklist)) {
         return DISABLED_CHANCE;
      }

      if (biomeChances.isEmpty()) {
         return dimensionChance;
      }

      for (BiomeChance biomeChance : biomeChances) {
         if (ConfigResolver.matchesBiomeEntry(biome, biomeChance.selector)) {
            return biomeChance.chance;
         }
      }

      return DISABLED_CHANCE;
   }

   private static void parseDimensionEntry(String entry) {
      String[] parts = splitEntry(entry);
      if (parts == null) {
         logInvalidEntry("dimension", entry, "dimensionId|percent");
         return;
      }

      try {
         Integer dimensionId = Integer.valueOf(parts[0]);
         Double chance = parseChance(parts[1]);
         if (chance == null || dimensionChances.containsKey(dimensionId)) {
            logInvalidEntry("dimension", entry, "dimensionId|percent with a unique dimension ID and percent from 0 to 100");
            return;
         }
         dimensionChances.put(dimensionId, chance);
      } catch (NumberFormatException ignored) {
         logInvalidEntry("dimension", entry, "dimensionId|percent");
      }
   }

   private static void parseBiomeEntry(String entry) {
      String[] parts = splitEntry(entry);
      if (parts == null) {
         logInvalidEntry("biome", entry, "biomeSelector|percent");
         return;
      }

      Double chance = parseChance(parts[1]);
      if (chance == null) {
         logInvalidEntry("biome", entry, "biomeSelector|percent with percent from 0 to 100");
         return;
      }
      biomeChances.add(new BiomeChance(parts[0], chance));
   }

   private static String[] splitEntry(String entry) {
      if (entry == null) {
         return null;
      }
      int separator = entry.lastIndexOf('|');
      if (separator <= 0 || separator >= entry.length() - 1) {
         return null;
      }
      String key = entry.substring(0, separator).trim();
      String value = entry.substring(separator + 1).trim();
      return key.isEmpty() || value.isEmpty() ? null : new String[]{key, value};
   }

   private static Double parseChance(String value) {
      try {
         double percent = Double.parseDouble(value);
         return Double.isNaN(percent) || Double.isInfinite(percent) || percent < 0.0D || percent > 100.0D
               ? null
               : percent / 100.0D;
      } catch (NumberFormatException ignored) {
         return null;
      }
   }

   private static boolean matchesAnyBiome(BiomeGenBase biome, String[] entries) {
      if (biome == null || entries == null) {
         return false;
      }
      for (String entry : entries) {
         if (ConfigResolver.matchesBiomeEntry(biome, entry)) {
            return true;
         }
      }
      return false;
   }

   private static void logInvalidEntry(String type, String entry, String expectedFormat) {
      GSLogger.logError("Ignoring invalid catacombs " + type + " whitelist entry '" + entry + "'. Expected " + expectedFormat + ".");
   }

   private static final class BiomeChance {
      private final String selector;
      private final double chance;

      private BiomeChance(String selector, double chance) {
         this.selector = selector;
         this.chance = chance;
      }
   }
}
