package de.sanandrew.mods.claysoldiers.util;

import net.minecraftforge.common.config.Configuration;

/**
 * Compatibility view used by native Clay Soldiers classes.
 *
 * Values live in RiftFlux's claysoldiers category; this class only preserves
 * original API expected by bundled Clay Soldiers code.
 */
public final class ModConfig {
    public static Configuration config;
    public static boolean useOldHurtSound = true;
    public static float soldierBaseHealth = 20.0F;
    public static float soldierBaseDamage = 1.0F;
    public static double statItemRange = 48.0D;
    public static int clayHutSpawnChance = 256;
    public static int clayHutZombieChance = 32;

    private ModConfig() {
    }

    public static void syncConfig() {
        config = com.voidsrift.riftflux.ModConfig.config;
        useOldHurtSound = com.voidsrift.riftflux.ModConfig.claySoldiersUseOldHurtSound;
        soldierBaseHealth = com.voidsrift.riftflux.ModConfig.claySoldiersBaseHealth;
        soldierBaseDamage = com.voidsrift.riftflux.ModConfig.claySoldiersBaseDamage;
        statItemRange = com.voidsrift.riftflux.ModConfig.claySoldiersStatItemRange;
        clayHutSpawnChance = com.voidsrift.riftflux.ModConfig.claySoldiersClayHutSpawnChance;
        clayHutZombieChance = com.voidsrift.riftflux.ModConfig.claySoldiersClayHutZombieChance;
    }
}
