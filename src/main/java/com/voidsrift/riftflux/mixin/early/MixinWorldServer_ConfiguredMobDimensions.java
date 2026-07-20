package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.spawning.ConfiguredMobSpawns;
import com.voidsrift.riftflux.spawning.HostileSpawnRules;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer_ConfiguredMobDimensions {
    @Inject(method = "spawnRandomCreature", at = @At("RETURN"), cancellable = true)
    private void riftflux$filterConfiguredNaturalSpawn(
            EnumCreatureType creatureType,
            int x,
            int y,
            int z,
            CallbackInfoReturnable<BiomeGenBase.SpawnListEntry> cir
    ) {
        if (creatureType == EnumCreatureType.monster
                && !HostileSpawnRules.allowsNaturalHostileSpawning((WorldServer) (Object) this)) {
            cir.setReturnValue(null);
            return;
        }

        BiomeGenBase.SpawnListEntry entry = cir.getReturnValue();
        if (entry != null
                && !ConfiguredMobSpawns.allowsNaturalSpawn(
                        entry.entityClass,
                        (WorldServer) (Object) this,
                        x,
                        z
                )) {
            cir.setReturnValue(null);
        }
    }
}
