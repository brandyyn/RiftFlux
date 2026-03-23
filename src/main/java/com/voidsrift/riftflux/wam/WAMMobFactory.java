package com.voidsrift.riftflux.wam;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfigResolver;
import com.voidsrift.riftflux.wam.entity.EntityBlackWidow;
import com.voidsrift.riftflux.wam.entity.EntityCyclops;
import com.voidsrift.riftflux.wam.entity.EntityEnderTroll;
import com.voidsrift.riftflux.wam.entity.EntityFlowerMan;
import com.voidsrift.riftflux.wam.entity.EntityJaxx;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public final class WAMMobFactory {
    private WAMMobFactory() {
    }

    public static EntityLiving createMobByKey(World world, String key) {
        if (world == null || key == null) {
            return null;
        }

        String normalized = ConfigResolver.normalizeToken(key);
        if (normalized.equals("cyclops") && ModConfig.enableWitchesAndMoreModule && ModConfig.enableCyclopsMob) {
            return new EntityCyclops(world);
        }
        if (normalized.equals("flowerman") && ModConfig.enableWitchesAndMoreModule && ModConfig.enableFlowerManMob) {
            return new EntityFlowerMan(world);
        }
        if ((normalized.equals("endertroll") || normalized.equals("troll"))
                && ModConfig.enableWitchesAndMoreModule
                && ModConfig.enableEnderTrollMob) {
            return new EntityEnderTroll(world);
        }
        if (normalized.equals("jaxx") && ModConfig.enableWitchesAndMoreModule && ModConfig.enableJaxxMob) {
            return new EntityJaxx(world);
        }
        if ((normalized.equals("blackwidow") || normalized.equals("widow") || normalized.equals("medianwidow"))
                && ModConfig.enableWitchesAndMoreModule
                && ModConfig.enableBlackWidowMob) {
            return new EntityBlackWidow(world);
        }

        return ConfigResolver.createRegisteredLivingByAlias(world, key);
    }
}
