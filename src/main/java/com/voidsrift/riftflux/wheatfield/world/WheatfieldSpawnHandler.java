package com.voidsrift.riftflux.wheatfield.world;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfigResolver;
import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public final class WheatfieldSpawnHandler {
    @SubscribeEvent
    public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event == null
                || event.entityLiving == null
                || event.entityLiving.worldObj == null
                || event.entityLiving.worldObj.isRemote
                || !ModConfig.wheatfieldRestrictHostileSpawns
                || !(event.entityLiving instanceof IMob)
                || WheatfieldContent.wheatfieldBiome == null) {
            return;
        }

        int x = MathHelper.floor_double(event.x);
        int z = MathHelper.floor_double(event.z);
        BiomeGenBase biome = event.entityLiving.worldObj.getBiomeGenForCoords(x, z);
        if (biome != WheatfieldContent.wheatfieldBiome) {
            return;
        }

        if (!isAllowedHostile(event.entityLiving)) {
            event.setResult(Event.Result.DENY);
        }
    }

    private static boolean isAllowedHostile(net.minecraft.entity.EntityLivingBase entity) {
        String[] allowed = ModConfig.wheatfieldAllowedHostileMobIds;
        return ConfigResolver.matchesConfiguredEntity(entity, allowed);
    }
}
