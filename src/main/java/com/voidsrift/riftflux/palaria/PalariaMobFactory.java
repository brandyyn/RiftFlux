package com.voidsrift.riftflux.palaria;

import com.voidsrift.riftflux.palaria.entity.EntityCowasaurus;
import com.voidsrift.riftflux.palaria.entity.EntityCreeptile;
import com.voidsrift.riftflux.palaria.entity.EntityEnderRaptorChicken;
import com.voidsrift.riftflux.palaria.entity.EntityEnderWalker;
import com.voidsrift.riftflux.palaria.entity.EntityMagmaRaptorChicken;
import com.voidsrift.riftflux.palaria.entity.EntityNimatin;
import com.voidsrift.riftflux.palaria.entity.EntityRaptorChicken;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public final class PalariaMobFactory {
    private PalariaMobFactory() {
    }

    public static EntityLiving createMobByKey(World world, String key) {
        if ("cowasaurus".equals(key)) return new EntityCowasaurus(world);
        if ("creeptile".equals(key)) return new EntityCreeptile(world);
        if ("raptor_chicken".equals(key)) return new EntityRaptorChicken(world);
        if ("ender_walker".equals(key)) return new EntityEnderWalker(world);
        if ("nimatin".equals(key)) return new EntityNimatin(world);
        if ("ender_raptor_chicken".equals(key)) return new EntityEnderRaptorChicken(world);
        if ("magma_raptor_chicken".equals(key)) return new EntityMagmaRaptorChicken(world);
        return null;
    }
}
