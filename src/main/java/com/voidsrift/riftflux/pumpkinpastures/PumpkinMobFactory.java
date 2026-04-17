package com.voidsrift.riftflux.pumpkinpastures;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public final class PumpkinMobFactory {
    private PumpkinMobFactory() {
    }

    public static EntityLiving createMobByKey(World world, String key) {
        if (world == null || key == null) {
            return null;
        }
        String normalized = key.trim().toLowerCase();
        if ("pumpkin_zombie".equals(normalized)) {
            return new EntityPumpkinZombie(world);
        }
        if ("pumpkin_skeleton".equals(normalized)) {
            return new EntityPumpkinSkeleton(world);
        }
        if ("pumpkin_creeper".equals(normalized)) {
            return new EntityPumpkinCreeper(world);
        }
        return null;
    }
}
