package com.voidsrift.riftflux.duckling;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public final class DuckMobFactory {
    private DuckMobFactory() {
    }

    public static EntityLiving createMobByKey(World world, String key) {
        if ("duck".equals(key)) {
            EntityDuck duck = new EntityDuck(world);
            duck.setVariant(DuckVariant.randomNatural(world == null ? null : world.rand));
            return duck;
        }
        if ("quackling".equals(key)) {
            EntityQuackling quackling = new EntityQuackling(world);
            if (world != null) {
                quackling.setDripped(world.rand.nextBoolean());
            }
            return quackling;
        }
        if ("soot_sprite".equals(key)) {
            return new EntitySootSprite(world);
        }
        return null;
    }
}
