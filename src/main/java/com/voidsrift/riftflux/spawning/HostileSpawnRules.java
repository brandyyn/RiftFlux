package com.voidsrift.riftflux.spawning;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;

public final class HostileSpawnRules {
    private static final long TICKS_PER_DAY = 24000L;

    private HostileSpawnRules() {
    }

    public static boolean allowsNaturalHostileSpawning(World world) {
        if (world == null) {
            return true;
        }

        long worldDay = Math.floorDiv(world.getWorldTime(), TICKS_PER_DAY);
        if (worldDay < ModConfig.hostileMobSpawnGracePeriodDays) {
            return false;
        }

        if (world.provider == null) {
            return true;
        }

        int phase = world.provider.getMoonPhase(world.getWorldTime());
        boolean[] disabledPhases = ModConfig.hostileMobSpawnDisabledMoonPhaseFlags;
        return disabledPhases == null
                || phase < 0
                || phase >= disabledPhases.length
                || !disabledPhases[phase];
    }

    public static boolean isHostile(EntityLivingBase entity) {
        return entity instanceof IMob || entity instanceof EntityMob;
    }

    public static boolean isHostileClass(Class<?> entityClass) {
        return entityClass != null
                && (IMob.class.isAssignableFrom(entityClass)
                || EntityMob.class.isAssignableFrom(entityClass));
    }
}
